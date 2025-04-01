package iscteiul.ista;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    @Test
    public void testCarregarPropriedadesCSV() {
        String fileName = "Madeira-Moodle-1.1.csv";
        List<PropriedadeRustica> propriedades = Main.carregarPropriedadesCSV(fileName);

        assertNotNull(propriedades);
        assertFalse(propriedades.isEmpty());
    }

    @Test
    public void testCriarGrafoPropietarios() {
        String fileName = "Madeira-Moodle-1.1.csv";
        List<PropriedadeRustica> propriedades = Main.carregarPropriedadesCSV(fileName);

        GrafoPropietarios grafo = new GrafoPropietarios();
        for (PropriedadeRustica p : propriedades) {
            grafo.adicionarProprietario(p.getOwner());
        }

        assertTrue(grafo.getVizinhos(1).size() >= 0);
    }
}