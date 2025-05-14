package iscteiul.ista;

public class ResultadoMunicipioDistancia {
    private Municipio municipio;
    private double distancia;

    public ResultadoMunicipioDistancia(Municipio municipio, double distancia) {
        this.municipio = municipio;
        this.distancia = distancia;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public double getDistancia() {
        return distancia;
    }
}
