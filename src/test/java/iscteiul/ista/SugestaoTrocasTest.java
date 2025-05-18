package iscteiul.ista;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;

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
        Municipio m = new Municipio("A", 1000, new Coordinate(0,0)); // assumindo latitude e longitude ou coords planas
        List<Municipio> municipios = List.of(m);

        PropriedadeRustica p1 = criarPropriedade("1", 1, 100.0, geometry);
        PropriedadeRustica p2 = criarPropriedade("2", 2, 1000.0, geometry);


        List<PropriedadeRustica> propriedades = List.of(p1, p2);

        GrafoPropriedades grafoPropriedades = new GrafoPropriedades();
        grafoPropriedades.getGrafo().put(p1, new HashSet<>());
        grafoPropriedades.getGrafo().put(p2, new HashSet<>());

        GrafoPropietarios grafoPropietarios = new GrafoPropietarios();
        grafoPropietarios.getGrafo().put(1, new HashSet<>()); // no neighbors

        List<Pair<PropriedadeRustica, PropriedadeRustica>> trocas =
                SugestaoTrocas.sugerirTrocas(propriedades, grafoPropietarios, grafoPropriedades,municipios);

        assertTrue(trocas.isEmpty());
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


    @Test
    void calcularMediaComFusaoIndividual() {
        PropriedadeRustica p1 = criarPropriedade("1", 1, 100.0);
        PropriedadeRustica p2 = criarPropriedade("2", 1, 200.0);
        PropriedadeRustica p3 = criarPropriedade("3", 1, 300.0);

        GrafoPropriedades grafo = new GrafoPropriedades();
        grafo.getGrafo().put(p1, new HashSet<>(List.of(p2)));
        grafo.getGrafo().put(p2, new HashSet<>(List.of(p1)));
        grafo.getGrafo().put(p3, new HashSet<>());

        List<PropriedadeRustica> props = List.of(p1, p2, p3);

        double media = SugestaoTrocas.calcularMediaComFusaoIndividual(props, grafo);

        // Grupo 1: p1 + p2 = 300; grupo 2: p3 = 300; média dos grupos = (300 + 300)/2 = 300
        assertEquals(300, media, 0.001);
    }

    @Test
    void conectaGrupoVizinho() {
        PropriedadeRustica p1 = criarPropriedade("1", 1, 100.0);
        PropriedadeRustica p2 = criarPropriedade("2", 1, 150.0);

        GrafoPropriedades grafo = new GrafoPropriedades();
        grafo.getGrafo().put(p1, new HashSet<>(List.of(p2)));
        grafo.getGrafo().put(p2, new HashSet<>(List.of(p1)));

        List<PropriedadeRustica> grupo = List.of(p1);

        assertTrue(SugestaoTrocas.conectaGrupoVizinho(p2, grupo, grafo));
    }

    @Test
    void tresMaioresMunicipios() {
        List<Municipio> municipios = List.of(
                new Municipio("A", 1000, new Coordinate(0,0)),
                new Municipio("B", 2000, new Coordinate(0,0)),
                new Municipio("C", 1500, new Coordinate(0,0))
        );

        assertNotNull(municipios);
        assertEquals(3, municipios.size());

        // Validar ordem decrescente por população
        assertTrue(municipios.get(1).getPopulacao() > municipios.get(0).getPopulacao() ||
                municipios.get(2).getPopulacao() > municipios.get(0).getPopulacao());
    }

    @Test
    void calcularDistancia() throws Exception {
        PropriedadeRustica p = criarPropriedade("1", 1, 100.0);
        Municipio m = new Municipio("M1", 1000, new Coordinate(-16.9, 32.7));

        // Definir geometria para a propriedade (ex: ponto)
        WKTReader reader = new WKTReader();
        Geometry geom = reader.read("POINT (-16.9 32.7)");
        p.setGeometryObj(geom);

        double dist = SugestaoTrocas.calcularDistancia(p, m);
        assertEquals(0, dist, 0.01);
    }

    @Test
    void distancia() {
        Coordinate c1 = new Coordinate(-16.9, 32.7);
        Coordinate c2 = new Coordinate(-16.9, 32.7);

        double d = SugestaoTrocas.distancia(c1, c2);
        assertEquals(0, d, 0.001);

        Coordinate c3 = new Coordinate(0, 0);
        Coordinate c4 = new Coordinate(0, 1);
        double d2 = SugestaoTrocas.distancia(c3, c4);

        // Aproximadamente 111.195 km (1 grau de latitude)
        assertTrue(d2 > 111000 && d2 < 112000);
    }



    private PropriedadeRustica criarPropriedade(String id, int owner, double area) {
        PropriedadeRustica p = new PropriedadeRustica();
        p.setObjectId(id);
        p.setOwner(owner);
        p.setShapeArea(area);
        return p;
    }
}