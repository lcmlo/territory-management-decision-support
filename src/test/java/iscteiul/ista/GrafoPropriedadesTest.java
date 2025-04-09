package iscteiul.ista;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GrafoPropriedadesTest {

    private GrafoPropriedades grafo;
    private PropriedadeRustica p1, p2, p3;

    @BeforeEach
    public void setUp() {
        grafo = new GrafoPropriedades();

        // Três propriedades (duas adjacentes e uma isolada)
        p1 = new PropriedadeRustica();
        p1.setObjectId("1");
        p1.setGeometry("POLYGON ((0 0, 0 1, 1 1, 1 0, 0 0))");

        p2 = new PropriedadeRustica();
        p2.setObjectId("2");
        p2.setGeometry("POLYGON ((1 0, 1 1, 2 1, 2 0, 1 0))");

        p3 = new PropriedadeRustica();
        p3.setObjectId("3");
        p3.setGeometry("POLYGON ((3 3, 3 4, 4 4, 4 3, 3 3))"); // totalmente isolada

        // Adiciona as propriedades ao grafo
        grafo.adicionarPropriedade(p1);
        grafo.adicionarPropriedade(p2);
        grafo.adicionarPropriedade(p3);

        // Constrói as adjacências
        grafo.construirAdjacencias();
    }

    @Test
    public void testAdjacenciaEntreP1eP2() {
        assertTrue(GrafoPropriedades.adjacentes(p1, p2), "P1 e P2 deviam ser adjacentes");
        assertTrue(GrafoPropriedades.adjacentes(p2, p1), "Adjacência devia ser bidirecional");
    }

    @Test
    public void testNaoAdjacenciaComP3() {
        assertFalse(GrafoPropriedades.adjacentes(p1, p3), "P1 e P3 não deviam ser adjacentes");
        assertFalse(GrafoPropriedades.adjacentes(p2, p3), "P2 e P3 não deviam ser adjacentes");
    }
}
