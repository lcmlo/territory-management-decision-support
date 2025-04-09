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
    private static Map<PropriedadeRustica, Set<PropriedadeRustica>> grafoPropriedades;
    private STRtree index;
    private Map<PropriedadeRustica, Geometry> geometriaMap;

    /**
     * Construtor da classe {@code GrafoPropriedades}.
     * Inicializa as estruturas necessárias para representar o grafo,
     * incluindo o mapa de adjacências, o mapa de geometrias e o índice espacial.
     */
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
        List<Pair<PropriedadeRustica, PropriedadeRustica>> adjacencias = Collections.synchronizedList(new ArrayList<>());

        grafoPropriedades.keySet().parallelStream().forEach(propriedade -> {
            Geometry geom = geometriaMap.get(propriedade);
            List<?> candidatos = index.query(geom.getEnvelopeInternal());

            for (Object obj : candidatos) {
                PropriedadeRustica vizinho = (PropriedadeRustica) obj;

                if (!propriedade.equals(vizinho)) {
                    Geometry geomVizinho = geometriaMap.get(vizinho);

                    if (geom.touches(geomVizinho) || geom.intersects(geomVizinho)) {
                        // Ordena o par para evitar duplicados A-B e B-A
                        PropriedadeRustica menor = propriedade.getObjectId().compareTo(vizinho.getObjectId()) < 0 ? propriedade : vizinho;
                        PropriedadeRustica maior = menor == propriedade ? vizinho : propriedade;
                        adjacencias.add(new Pair<>(menor, maior));
                    }
                }
            }
        });

        // Atualiza o grafo sequencialmente
        for (Pair<PropriedadeRustica, PropriedadeRustica> par : adjacencias) {
            PropriedadeRustica a = par.first;
            PropriedadeRustica b = par.second;

            grafoPropriedades.get(a).add(b);
            grafoPropriedades.get(b).add(a);
        }
    }

    /**
     *
     * Se duas propriedades são adjacentes ou não
     * @param a uma propriedade
     * @param b uma propriedade
     * @return Se duas propriedades são adjacentes ou não
     */
    public static boolean adjacentes(PropriedadeRustica a, PropriedadeRustica b) {
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

    /**
     * Devolve o grafo de adjacência das propriedades rústicas.
     * Cada propriedade está associada a um conjunto de propriedades vizinhas.
     *
     * @return um mapa onde cada chave é uma propriedade rústica,
     *         e o valor é o conjunto de propriedades adjacentes.
     */
    public Map<PropriedadeRustica, Set<PropriedadeRustica>> getGrafo() {
        return grafoPropriedades;
    }


}
