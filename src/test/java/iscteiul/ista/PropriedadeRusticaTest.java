package iscteiul.ista;

import org.junit.jupiter.api.Test;

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
}