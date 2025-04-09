package iscteiul.ista;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;

import java.util.*;

public class GrafoPropietarios {

    private final Map<Integer, Set<Integer>> grafo;

    public GrafoPropietarios() {
        this.grafo = new HashMap<>();
    }

    public void adicionarProprietario(int owner) {
        grafo.putIfAbsent(owner, new HashSet<>());
    }

    public void adicionarVizinhanca(int owner1, int owner2) {
        if (owner1 != owner2) {
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

        for (PropriedadeRustica propriedade : propriedades) {
            grafoProprietarios.adicionarProprietario(propriedade.getOwner());
        }

        for (int i = 0; i < propriedades.size(); i++) {
            PropriedadeRustica p1 = propriedades.get(i);

            for (int j = i + 1; j < propriedades.size(); j++) {
                PropriedadeRustica p2 = propriedades.get(j);

                //if (p1.getOwner() != p2.getOwner() && GrafoPropriedades.adjacentes(p1, p2)) {
                //    grafoProprietarios.adicionarVizinhanca(p1.getOwner(), p2.getOwner());
                //}
                if (p1.getOwner() != p2.getOwner() && saoAdjacentes(p1, p2)) {
                    grafoProprietarios.adicionarVizinhanca(p1.getOwner(), p2.getOwner());

                }
            }
        }


        return grafoProprietarios;
    }
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