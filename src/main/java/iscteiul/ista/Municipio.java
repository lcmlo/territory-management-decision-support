package iscteiul.ista;

import org.locationtech.jts.geom.Coordinate;


/**
 * Representa um município com nome, população e localização geográfica.
 */
public class Municipio {
    /**
     * Nome do município.
     */
    private final String nome;

    /**
     * População do município.
     */
    private final int populacao;

    /**
     * Coordenadas geográficas do município.
     */
    private Coordinate coordenadas;


    /**
     * Construtor para criar um município com nome, população e coordenadas.
     *
     * @param nome Nome do município
     * @param populacao Número de habitantes do município
     * @param coordenadas Coordenadas geográficas do município
     */
    public Municipio(String nome, int populacao, Coordinate coordenadas) {
        this.nome = nome;
        this.populacao = populacao;
        this.coordenadas = coordenadas;
    }

    /**
     * Obtém o nome do município.
     *
     * @return nome do município
     */
    public String getNome() {
        return nome;
    }

    /**
     * Obtém a população do município.
     *
     * @return população do município
     */
    public int getPopulacao() {
        return populacao;
    }

    /**
     * Obtém as coordenadas geográficas do município.
     *
     * @return coordenadas do município
     */
    public Coordinate getCoordenadas() {
        return coordenadas;
    }

}