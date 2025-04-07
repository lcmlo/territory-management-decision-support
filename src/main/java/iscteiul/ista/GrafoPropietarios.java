package iscteiul.ista;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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


    // 🔥 添加这个方法
    public Map<Integer, Set<Integer>> getGrafo() {
        return this.grafo;
    }

    public void exibirGrafo() {
        for (Map.Entry<Integer, Set<Integer>> entry : grafo.entrySet()) {
            System.out.println("Proprietário: " + entry.getKey() + " -> Vizinhos: " + entry.getValue());
        }
    }
}