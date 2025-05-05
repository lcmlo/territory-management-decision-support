
package iscteiul.ista;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraAreaTest {

    private List<PropriedadeRustica> propriedades;

    @BeforeEach
    void setUp() {
        propriedades = new ArrayList<>();

        PropriedadeRustica p1 = new PropriedadeRustica();
        p1.setFreguesia("Freguesia A");
        p1.setMunicipio("Municipio X");
        p1.setIlha("Ilha Z");
        p1.setShapeArea(100);

        PropriedadeRustica p2 = new PropriedadeRustica();
        p2.setFreguesia("Freguesia A");
        p2.setMunicipio("Municipio X");
        p2.setIlha("Ilha Z");
        p2.setShapeArea(300);

        PropriedadeRustica p3 = new PropriedadeRustica();
        p3.setFreguesia("Freguesia B");
        p3.setMunicipio("Municipio Y");
        p3.setIlha("Ilha Z");
        p3.setShapeArea(500);

        propriedades.add(p1);
        propriedades.add(p2);
        propriedades.add(p3);
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
}
