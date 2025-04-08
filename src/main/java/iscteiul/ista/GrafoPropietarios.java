package iscteiul.ista;

import java.util.*;

public class GrafoPropietarios {

    private Map<Integer, Set<Integer>> grafo; // 用于存储每个 Owner 与其他相邻的 Owners

    public GrafoPropietarios() {
        this.grafo = new HashMap<>();
    }

    public void adicionarProprietario(int owner) {
        grafo.putIfAbsent(owner, new HashSet<>());
    }

    public void adicionarVizinhanca(int owner1, int owner2) {
        if (owner1 != owner2) { // 避免自连接
            grafo.get(owner1).add(owner2);
            grafo.get(owner2).add(owner1);
        }
    }

    public Set<Integer> getVizinhos(int owner) {
        return grafo.getOrDefault(owner, new HashSet<>());
    }


    public Map<Integer, Set<Integer>> getGrafo() {
        return this.grafo;
    }

    public void exibirGrafo() {
        for (Map.Entry<Integer, Set<Integer>> entry : grafo.entrySet()) {
            System.out.println("Proprietário: " + entry.getKey() + " -> Vizinhos: " + entry.getValue());
        }
    }
    public static GrafoPropietarios construirGrafoProprietarios(List<PropriedadeRustica> propriedades) {
        GrafoPropietarios grafoProprietarios = new GrafoPropietarios();

        // 初始化所有业主
        for (PropriedadeRustica propriedade : propriedades) {
            grafoProprietarios.adicionarProprietario(propriedade.getOwner());
        }

        // 对所有成对的地块进行比较
        for (int i = 0; i < propriedades.size(); i++) {
            PropriedadeRustica p1 = propriedades.get(i);

            for (int j = i + 1; j < propriedades.size(); j++) {
                PropriedadeRustica p2 = propriedades.get(j);

                if (p1.getOwner() != p2.getOwner() && GrafoPropriedades.adjacentes(p1, p2)) {
                    grafoProprietarios.adicionarVizinhanca(p1.getOwner(), p2.getOwner());
                }
            }
        }

        return grafoProprietarios;
    }

}