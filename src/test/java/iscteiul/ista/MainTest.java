package iscteiul.ista;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    @Test
    public void testCarregarPropriedadesCSV() {
        String fileName = "Madeira-Moodle-1.1.csv"; // Certifique-se de que o arquivo esteja no classpath
        List<PropriedadeRustica> propriedades = Main.carregarPropriedadesCSV(fileName);

        assertNotNull(propriedades);
        assertFalse(propriedades.isEmpty()); // Verifica se há propriedades carregadas
    }
}