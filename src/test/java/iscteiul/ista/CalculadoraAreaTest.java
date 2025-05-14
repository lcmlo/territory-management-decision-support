package iscteiul.ista;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraAreaTest {

    private List<PropriedadeRustica> propriedades;
    private GrafoPropriedades grafo;

    @BeforeEach
    void setUp() {
        propriedades = new ArrayList<>();
        grafo = new GrafoPropriedades();

        PropriedadeRustica p1 = new PropriedadeRustica();
        p1.setFreguesia("Freguesia A");
        p1.setMunicipio("Municipio X");
        p1.setIlha("Ilha Z");
        p1.setShapeArea(100);
        p1.setOwner(1);
        p1.setObjectId("1");
        p1.setGeometry("MULTIPOLYGON (((0 0, 0 1, 1 1, 1 0, 0 0)))");

        PropriedadeRustica p2 = new PropriedadeRustica();
        p2.setFreguesia("Freguesia A");
        p2.setMunicipio("Municipio X");
        p2.setIlha("Ilha Z");
        p2.setShapeArea(200);
        p2.setOwner(1);
        p2.setObjectId("2");
        p2.setGeometry("MULTIPOLYGON (((1 0, 1 1, 2 1, 2 0, 1 0)))");

        PropriedadeRustica p3 = new PropriedadeRustica();
        p3.setFreguesia("Freguesia B");
        p3.setMunicipio("Municipio Y");
        p3.setIlha("Ilha Z");
        p3.setShapeArea(500);
        p3.setOwner(2);
        p3.setObjectId("3");
        p3.setGeometry("MULTIPOLYGON (((10 10, 10 11, 11 11, 11 10, 10 10)))");

        propriedades.add(p1);
        propriedades.add(p2);
        propriedades.add(p3);

        grafo.adicionarPropriedade(p1);
        grafo.adicionarPropriedade(p2);
        grafo.adicionarPropriedade(p3);
        grafo.construirAdjacencias();
    }

    @Test
    void testCalcularAreaMediaPorFreguesia() {
        String input = "freguesia\nFreguesia A\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        assertDoesNotThrow(() -> CalculadoraArea.calcularAreaMediaPorZona(propriedades));
    }

    @Test
    void testCalcularAreaMediaPorMunicipio() {
        String input = "municipio\nMunicipio Y\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        assertDoesNotThrow(() -> CalculadoraArea.calcularAreaMediaPorZona(propriedades));
    }

    @Test
    void testCalcularAreaMediaPorIlhaSemResultados() {
        String input = "ilha\nIlha Inexistente\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        assertDoesNotThrow(() -> CalculadoraArea.calcularAreaMediaPorZona(propriedades));
    }

    @Test
    void testCalcularAreaMediaComFusaoPorIlha() {
        double media = CalculadoraArea.calcularAreaMediaComFusao(propriedades, grafo, "ilha", "Ilha Z");
        assertEquals(400.0, media, 0.01); // p1 e p2 fundem-se (100+200), p3 isolado (500) -> (300, 500) → média 400
    }

    @Test
    void testCalcularAreaMediaComFusaoPorMunicipio() {
        double media = CalculadoraArea.calcularAreaMediaComFusao(propriedades, grafo, "municipio", "Municipio X");
        assertEquals(300.0, media, 0.01); // apenas p1 e p2, mesmo owner, são fundidos
    }

    @Test
    void testCalcularAreaMediaComFusaoSemResultados() {
        double media = CalculadoraArea.calcularAreaMediaComFusao(propriedades, grafo, "freguesia", "Freguesia Inexistente");
        assertEquals(0.0, media, 0.01);
    }
}
