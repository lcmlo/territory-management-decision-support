package iscteiul.ista;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


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
        System.out.println("Grafo de Proprietários Vizinhos:");
        for (Map.Entry<Integer, Set<Integer>> entry : grafo.entrySet()) {
            System.out.println("Proprietário: " + entry.getKey() + " -> Vizinhos: " + entry.getValue());
        }
    }

    /**
     * Constrói o grafo de proprietários com base numa lista de propriedades,
     * adicionando arestas entre proprietários de propriedades adjacentes.
     *
     * @param propriedades Lista de propriedades rústicas.
     * @param grafoPropriedades Grafo das Propriedades
     *
     */
    public void construirGrafoProprietarios(GrafoPropriedades grafoPropriedades, List<PropriedadeRustica> propriedades) {

        for (PropriedadeRustica propriedade : propriedades) {
            this.adicionarProprietario(propriedade.getOwner());
        }

        Map<PropriedadeRustica, Set<PropriedadeRustica>> mapaAdj = grafoPropriedades.getGrafo();

        for (PropriedadeRustica propriedade : mapaAdj.keySet()) {
            int donoA = propriedade.getOwner();

            for (PropriedadeRustica vizinha : mapaAdj.get(propriedade)) {
                int donoB = vizinha.getOwner();

                // Adiciona adjacência entre donos se forem diferentes
                if (donoA!=donoB) {
                    this.adicionarVizinhanca(donoA, donoB);
                }
            }
        }
    }

}