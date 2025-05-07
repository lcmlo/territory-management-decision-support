package iscteiul.ista;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class SugestaoTrocasTest {

    @Test
    public void testCalcularMediaComFusaoIndividualSimples() {
        String geometry = "POLYGON((0 0, 1 0, 1 1, 0 1, 0 0))";

        PropriedadeRustica p1 = criarPropriedade("1", 1, 100.0, geometry);
        PropriedadeRustica p2 = criarPropriedade("2", 1, 200.0, geometry);

        GrafoPropriedades grafo = new GrafoPropriedades();
        grafo.getGrafo().put(p1, new HashSet<>());
        grafo.getGrafo().put(p2, new HashSet<>());
        grafo.getGrafo().get(p1).add(p2);
        grafo.getGrafo().get(p2).add(p1);

        List<PropriedadeRustica> lista = Arrays.asList(p1, p2);

        double media = SugestaoTrocas.calcularMediaComFusaoIndividual(lista, grafo);
        assertEquals(300.0, media, 0.001);
    }

    @Test
    public void testSemTrocaPossivel() {
        String geometry = "POLYGON((0 0, 1 0, 1 1, 0 1, 0 0))";

        PropriedadeRustica p1 = criarPropriedade("1", 1, 100.0, geometry);
        PropriedadeRustica p2 = criarPropriedade("2", 2, 1000.0, geometry);

        List<PropriedadeRustica> propriedades = List.of(p1, p2);

        GrafoPropriedades grafoPropriedades = new GrafoPropriedades();
        grafoPropriedades.getGrafo().put(p1, new HashSet<>());
        grafoPropriedades.getGrafo().put(p2, new HashSet<>());

        GrafoPropietarios grafoPropietarios = new GrafoPropietarios();
        grafoPropietarios.getGrafo().put(1, new HashSet<>()); // no neighbors

        List<Pair<PropriedadeRustica, PropriedadeRustica>> trocas =
                SugestaoTrocas.sugerirTrocas(propriedades, grafoPropietarios, grafoPropriedades);

        assertTrue(trocas.isEmpty());
    }

    @Test
    public void testTrocaValidaGerada() {
        String geometry = "POLYGON((0 0, 1 0, 1 1, 0 1, 0 0))";

        PropriedadeRustica a1 = criarPropriedade("1", 1, 500.0, geometry);
        PropriedadeRustica a2 = criarPropriedade("2", 1, 600.0, geometry);
        PropriedadeRustica b1 = criarPropriedade("3", 2, 550.0, geometry);

        List<PropriedadeRustica> propriedades = List.of(a1, a2, b1);

        GrafoPropriedades grafoPropriedades = new GrafoPropriedades();
        for (PropriedadeRustica p : propriedades) {
            grafoPropriedades.getGrafo().put(p, new HashSet<>());
        }
        grafoPropriedades.getGrafo().get(a1).add(b1);
        grafoPropriedades.getGrafo().get(b1).add(a1);

        GrafoPropietarios grafoPropietarios = new GrafoPropietarios();
        grafoPropietarios.getGrafo().put(1, new HashSet<>(List.of(2)));
        grafoPropietarios.getGrafo().put(2, new HashSet<>(List.of(1)));

        List<Pair<PropriedadeRustica, PropriedadeRustica>> trocas =
                SugestaoTrocas.sugerirTrocas(propriedades, grafoPropietarios, grafoPropriedades);

        assertFalse(trocas.isEmpty());
    }

    private PropriedadeRustica criarPropriedade(String id, int owner, double area, String geometry) {
        PropriedadeRustica p = new PropriedadeRustica();
        p.setObjectId(id);
        p.setParId("P" + id);
        p.setParNum(1.0);
        p.setShapeLength(10.0);
        p.setShapeArea(area);
        p.setGeometry(geometry);
        p.setOwner(owner);
        p.setFreguesia("F1");
        p.setMunicipio("M1");
        p.setIlha("I1");
        return p;
    }
}