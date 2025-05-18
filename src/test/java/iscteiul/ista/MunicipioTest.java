package iscteiul.ista;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

import static org.junit.jupiter.api.Assertions.*;

class MunicipioTest {

    @Test
    void getNome() {
        Municipio m = new Municipio("Funchal", 100000, new Coordinate(32.65, -16.90));
        assertEquals("Funchal", m.getNome());
    }

    @Test
    void getPopulacao() {
        Municipio m = new Municipio("Funchal", 100000, new Coordinate(32.65, -16.90));
        assertEquals(100000, m.getPopulacao());
    }

    @Test
    void getCoordenadas() {
        Coordinate coord = new Coordinate(32.65, -16.90);
        Municipio m = new Municipio("Funchal", 100000, coord);
        assertEquals(coord, m.getCoordenadas());
    }
}