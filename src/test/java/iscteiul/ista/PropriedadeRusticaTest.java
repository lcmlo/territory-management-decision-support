package iscteiul.ista;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PropriedadeRusticaTest {
    @Test
    public void testSetGetObjectId() {
        PropriedadeRustica propriedade = new PropriedadeRustica();
        propriedade.setObjectId("12345");
        assertEquals("12345", propriedade.getObjectId());
    }

    @Test
    public void testSetGetParId() {
        PropriedadeRustica propriedade = new PropriedadeRustica();
        propriedade.setParId("23456");
        assertEquals("23456", propriedade.getParId());
    }

    @Test
    public void testSetGetParNum() {
        PropriedadeRustica propriedade = new PropriedadeRustica();
        propriedade.setParNum(123.45);
        assertEquals(123.45, propriedade.getParNum());
    }

    @Test
    public void testSetGetShapeLength() {
        PropriedadeRustica propriedade = new PropriedadeRustica();
        propriedade.setShapeLength(567.89);
        assertEquals(567.89, propriedade.getShapeLength());
    }

    @Test
    public void testSetGetShapeArea() {
        PropriedadeRustica propriedade = new PropriedadeRustica();
        propriedade.setShapeArea(987.65);
        assertEquals(987.65, propriedade.getShapeArea());
    }

    @Test
    public void testSetGetGeometry() {
        PropriedadeRustica propriedade = new PropriedadeRustica();
        propriedade.setGeometry("MULTIPOLYGON ...");
        assertEquals("MULTIPOLYGON ...", propriedade.getGeometry());
    }

    @Test
    public void testSetGetOwner() {
        PropriedadeRustica propriedade = new PropriedadeRustica();
        propriedade.setOwner(1);
        assertEquals(1, propriedade.getOwner());
    }

    @Test
    public void testSetGetFreguesia() {
        PropriedadeRustica propriedade = new PropriedadeRustica();
        propriedade.setFreguesia("Arco da Calheta");
        assertEquals("Arco da Calheta", propriedade.getFreguesia());
    }

    @Test
    public void testSetGetMunicipio() {
        PropriedadeRustica propriedade = new PropriedadeRustica();
        propriedade.setMunicipio("Calheta");
        assertEquals("Calheta", propriedade.getMunicipio());
    }

    @Test
    public void testSetGetIlha() {
        PropriedadeRustica propriedade = new PropriedadeRustica();
        propriedade.setIlha("Madeira");
        assertEquals("Madeira", propriedade.getIlha());
    }

    GeometryFactory gf = new GeometryFactory();

    @Test
    void getIndiceCompacidade() {
        // Criar um quadrado 1x1 (compacidade deve ser próxima de ~0.785)
        Coordinate[] coords = new Coordinate[] {
                new Coordinate(0,0),
                new Coordinate(1,0),
                new Coordinate(1,1),
                new Coordinate(0,1),
                new Coordinate(0,0)
        };
        LinearRing shell = gf.createLinearRing(coords);
        Polygon polygon = gf.createPolygon(shell, null);

        PropriedadeRustica p = new PropriedadeRustica();
        p.setGeometryObj(polygon);

        double compacidade = p.getIndiceCompacidade();

        // Círculo tem compacidade 1, quadrado deve ser menor (cerca de 0.785)
        assertTrue(compacidade > 0.7 && compacidade < 0.8, "Compacidade de quadrado 1x1");

        // Testar geometria nula retorna 0
        p.setGeometryObj(null);
        assertEquals(0, p.getIndiceCompacidade());
    }

    @Test
    void avaliarPropriedade() {
        // Criar um município num ponto fixo
        Municipio m = new Municipio("A", 1000, new Coordinate(0,0)); // assumindo latitude e longitude ou coords planas
        List<Municipio> municipios = List.of(m);

        // Criar propriedade em redor do município
        Coordinate[] coords = new Coordinate[] {
                new Coordinate(0,0),
                new Coordinate(1,0),
                new Coordinate(1,1),
                new Coordinate(0,1),
                new Coordinate(0,0)
        };
        Polygon polygon = gf.createPolygon(gf.createLinearRing(coords), null);

        PropriedadeRustica p = new PropriedadeRustica();
        p.setGeometryObj(polygon);

        // Avaliar propriedade
        double score = PropriedadeRustica.avaliarPropriedade(p, municipios);

        // score deve ser > 0 e <= 1 (já que compacidade e scoreDistancia entram com pesos)
        assertTrue(score > 0 && score <= 1, "Score deve estar entre 0 e 1");

        // Testar com geometria nula ou lista vazia retorna 0
        assertEquals(0, PropriedadeRustica.avaliarPropriedade(p, List.of()));
        p.setGeometryObj(null);
        assertEquals(0, PropriedadeRustica.avaliarPropriedade(p, municipios));
    }
}