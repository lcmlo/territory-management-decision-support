package iscteiul.ista;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;

import java.util.*;

/**
 * Classe que representa um grafo de proprietários, onde os nós são identificadores de proprietários
 * e as arestas representam vizinhança (propriedades adjacentes) entre propriedades de diferentes donos.
 */
public class GrafoPropietarios {

    /** Mapa que representa o grafo: cada proprietário está associado a um conjunto de proprietários vizinhos */
    private final Map<Integer, Set<Integer>> grafo;

    /**
     * Construtor da classe, inicializa o grafo vazio.
     */
    public GrafoPropietarios() {
        this.grafo = new HashMap<>();
    }

    /**
     * Adiciona um proprietário ao grafo, se ainda não estiver presente.
     *
     * @param owner ID do proprietário.
     */
    public void adicionarProprietario(int owner) {
        grafo.putIfAbsent(owner, new HashSet<>());
    }

    /**
     * Adiciona uma relação de vizinhança (aresta) entre dois proprietários distintos.
     *
     * @param owner1 ID do primeiro proprietário.
     * @param owner2 ID do segundo proprietário.
     */
    public void adicionarVizinhanca(int owner1, int owner2) {
        if (owner1 != owner2) {
            grafo.get(owner1).add(owner2);
            grafo.get(owner2).add(owner1);
        }
    }

    /**
     * Obtém o conjunto de IDs de proprietários vizinhos de um determinado proprietário.
     *
     * @param owner ID do proprietário.
     * @return Conjunto de IDs dos vizinhos, ou conjunto vazio se não tiver vizinhos.
     */
    public Set<Integer> getVizinhos(int owner) {
        return grafo.getOrDefault(owner, new HashSet<>());
    }

    /**
     * Obtém o mapa completo que representa o grafo dos proprietários.
     *
     * @return Mapa do grafo com vizinhanças.
     */
    public Map<Integer, Set<Integer>> getGrafo() {
        return this.grafo;
    }

    /**
     * Exibe na consola o grafo dos proprietários, mostrando cada proprietário e os seus vizinhos.
     */
    public void exibirGrafo() {
        for (Map.Entry<Integer, Set<Integer>> entry : grafo.entrySet()) {
            System.out.println("Proprietário: " + entry.getKey() + " -> Vizinhos: " + entry.getValue());
        }
    }

    /**
     * Constrói o grafo de proprietários com base numa lista de propriedades,
     * adicionando arestas entre proprietários de propriedades adjacentes.
     *
     * @param propriedades Lista de propriedades rústicas.
     * @return Grafo de proprietários.
     */
    public static GrafoPropietarios construirGrafoProprietarios(List<PropriedadeRustica> propriedades) {
        GrafoPropietarios grafoProprietarios = new GrafoPropietarios();

        for (PropriedadeRustica propriedade : propriedades) {
            grafoProprietarios.adicionarProprietario(propriedade.getOwner());
        }

        for (int i = 0; i < propriedades.size(); i++) {
            PropriedadeRustica p1 = propriedades.get(i);

            for (int j = i + 1; j < propriedades.size(); j++) {
                PropriedadeRustica p2 = propriedades.get(j);

                //if (p1.getOwner() != p2.getOwner() && GrafoPropriedades.adjacentes(p1, p2)) {
                //    grafoProprietarios.adicionarVizinhanca(p1.getOwner(), p2.getOwner()); //TODO
                //}
                if (p1.getOwner() != p2.getOwner() && saoAdjacentes(p1, p2)) {
                    grafoProprietarios.adicionarVizinhanca(p1.getOwner(), p2.getOwner());
                }
            }
        }

        return grafoProprietarios;
    }

    /**
     * Verifica se duas propriedades são adjacentes, ou seja, se partilham fronteira ou se intersectam,
     * e pertencem ao mesmo município e freguesia.
     *
     * @param p1 Primeira propriedade.
     * @param p2 Segunda propriedade.
     * @return true se forem adjacentes, false caso contrário.
     */
    private static boolean saoAdjacentes(PropriedadeRustica p1, PropriedadeRustica p2) {
        if (!(p1.getFreguesia().equals(p2.getFreguesia()) &&
                p1.getMunicipio().equals(p2.getMunicipio()))) {
            return false;
        }

        try {
            WKTReader reader = new WKTReader();
            Geometry geometry1 = reader.read(p1.getGeometry());
            Geometry geometry2 = reader.read(p2.getGeometry());

            return geometry1.intersects(geometry2);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
