package iscteiul.ista;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.index.strtree.STRtree;
import org.locationtech.jts.io.WKTReader;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Classe em que se representa o Grafo de adjacência de propriedades
 */
public class GrafoPropriedades {
    private Map<PropriedadeRustica, Set<PropriedadeRustica>> grafoPropriedades;
    private STRtree index;
    private Map<PropriedadeRustica, Geometry> geometriaMap;

    public GrafoPropriedades() {
        grafoPropriedades = new ConcurrentHashMap<>();
        geometriaMap = new ConcurrentHashMap<>();
        index = new STRtree();
    }

    /**
     * Adiciona uma propriedade como nó do grafo
     *
     * @param propriedade Propriedade que está no ficheiro csv
     */
    public void adicionarPropriedade(PropriedadeRustica propriedade) {
        try {
            WKTReader wktReader = new WKTReader();
            Geometry geom = wktReader.read(propriedade.getGeometry());
            geometriaMap.put(propriedade, geom);
            index.insert(geom.getEnvelopeInternal(), propriedade);
            grafoPropriedades.putIfAbsent(propriedade, Collections.synchronizedSet(new HashSet<>()));
        } catch (Exception e) {
            System.err.println("Erro ao ler geometria: " + e.getMessage());
        }
    }

    /**
     * Constrói as listas de ajdacências de todas as propriedades definidas no ficheiro csv
     */
    public void construirAdjacencias() {
        grafoPropriedades.keySet().parallelStream().forEach(propriedade -> {
            Geometry geom = geometriaMap.get(propriedade);
            List<?> candidatos = index.query(geom.getEnvelopeInternal());

            for (Object obj : candidatos) {
                PropriedadeRustica vizinho = (PropriedadeRustica) obj;

                if (!propriedade.equals(vizinho)) {
                    Geometry geomVizinho = geometriaMap.get(vizinho);

                    if (geom.touches(geomVizinho)) {
                        grafoPropriedades.get(propriedade).add(vizinho);
                        grafoPropriedades.get(vizinho).add(propriedade);
                    }
                }
            }
        });
    }

    /**
     *
     *
     * @param a uma propriedade
     * @param b uma propriedade
     * @return Se duas propriedades são adjacentes ou não
     */
    public boolean adjacentes(PropriedadeRustica a, PropriedadeRustica b) {
        return grafoPropriedades.get(a).contains(b);
    }

    /**
     * Mostra o grafo criado na consola
     */
    public void mostrarGrafo() {
        System.out.println("Grafo de Adjacência das Propriedades Rústicas:");
        for (PropriedadeRustica propriedade : grafoPropriedades.keySet()) {
            System.out.print("Propriedade " + propriedade.getObjectId() + " -> [");

            Set<PropriedadeRustica> adjacentes = grafoPropriedades.get(propriedade);
            String adjIds = adjacentes.stream()
                    .map(PropriedadeRustica::getObjectId)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

            System.out.println(adjIds + "]");
        }
    }



}
