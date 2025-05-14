package iscteiul.ista;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class Municipio {
    private final String nome;
    private final int populacao;
    private Coordinate coordenadas;
    private Point center;

    public Municipio(String nome, int populacao, Coordinate coordenadas) {
        this.nome = nome;
        this.populacao = populacao;
        this.coordenadas = coordenadas;
        this.center=CoordenadastoPoint(coordenadas);
    }

    public String getNome() {
        return nome;
    }

    public int getPopulacao() {
        return populacao;
    }

    public Coordinate getCoordenadas() {
        return coordenadas;
    }

    public Point CoordenadastoPoint(Coordinate coordenadas) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point pontoMunicipio=geometryFactory.createPoint(coordenadas);
        return pontoMunicipio;
    }
}