package iscteiul.ista;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GrafoPropietariosTest {

    private GrafoPropietarios grafo;
    private List<PropriedadeRustica> propriedades;

    @BeforeEach
    public void setUp() {
        grafo = new GrafoPropietarios();

        // 你在 Main 类中应该已经实现了 carregarPropriedadesCSV 方法
        propriedades = Main.carregarPropriedadesCSV("Madeira-Moodle-1.1.csv");

        // 添加所有者节点
        for (PropriedadeRustica propriedade : propriedades) {
            grafo.adicionarProprietario(propriedade.getOwner());
        }

        // 添加邻接关系
        for (int i = 0; i < propriedades.size(); i++) {
            for (int j = i + 1; j < propriedades.size(); j++) {
                PropriedadeRustica p1 = propriedades.get(i);
                PropriedadeRustica p2 = propriedades.get(j);

                if (Main.saoAdjacentes(p1, p2)) {
                    grafo.adicionarVizinhanca(p1.getOwner(), p2.getOwner());
                }
            }
        }
    }

    @Test
    public void testGrafoNaoVazio() {
        assertFalse(grafo.getGrafo().isEmpty(), "O grafo não deveria estar vazio.");
    }

    @Test
    public void testAdicionarProprietario() {
        grafo.adicionarProprietario(999);
        assertTrue(grafo.getGrafo().containsKey(999), "O novo proprietário não foi adicionado.");
    }

    @Test
    public void testAdicionarVizinhanca() {
        grafo.adicionarProprietario(1);
        grafo.adicionarProprietario(2);
        grafo.adicionarVizinhanca(1, 2);
        System.out.println("visinhoList (1): " + grafo.getVizinhos(1));
        System.out.println("visinhoList (2): " + grafo.getVizinhos(2));
        assertTrue(grafo.getVizinhos(1).contains(2), "Os proprietários deveriam estar conectados.");
        assertTrue(grafo.getVizinhos(2).contains(1), "Os proprietários deveriam estar conectados.");
    }

    @Test
    public void testVizinhancaCorreta() {
        for (PropriedadeRustica propriedade : propriedades) {
            int owner = propriedade.getOwner();
            Set<Integer> vizinhos = grafo.getVizinhos(owner);

            assertNotNull(vizinhos, "O proprietário deveria ter vizinhos listados.");
        }
    }
}
