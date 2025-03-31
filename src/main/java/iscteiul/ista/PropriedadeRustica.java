package iscteiul.ista;

/**
 * Representa uma propriedade rústica com informações como ID, área, coordenadas e localização.
 */
public class PropriedadeRustica {
    private String objectId;
    private String parId;
    private double parNum;
    private double shapeLength;
    private double shapeArea;
    private String geometry;
    private int owner;
    private String freguesia;
    private String municipio;
    private String ilha;

    /**
     * Define o ID único da propriedade.
     *
     * @param objectId ID da propriedade.
     */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    /**
     * Define o ID de PAR.
     *
     * @param parId ID de PAR.
     */
    public void setParId(String parId) {
        this.parId = parId;
    }

    /**
     * Define o número de PAR.
     *
     * @param parNum Número de PAR.
     */
    public void setParNum(double parNum) {
        this.parNum = parNum;
    }

    /**
     * Define o comprimento da propriedade.
     *
     * @param shapeLength Comprimento da propriedade.
     */
    public void setShapeLength(double shapeLength) {
        this.shapeLength = shapeLength;
    }

    /**
     * Define a área da propriedade.
     *
     * @param shapeArea Área da propriedade.
     */
    public void setShapeArea(double shapeArea) {
        this.shapeArea = shapeArea;
    }

    /**
     * Define a geometria (coordenadas) da propriedade.
     *
     * @param geometry Geometria (MULTIPOLYGON).
     */
    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    /**
     * Define o proprietário da propriedade (ID do proprietário).
     *
     * @param owner ID do proprietário.
     */
    public void setOwner(int owner) {
        this.owner = owner;
    }

    /**
     * Define a freguesia (bairro) onde a propriedade está localizada.
     *
     * @param freguesia Nome da freguesia.
     */
    public void setFreguesia(String freguesia) {
        this.freguesia = freguesia;
    }

    /**
     * Define o município onde a propriedade está localizada.
     *
     * @param municipio Nome do município.
     */
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    /**
     * Define a ilha onde a propriedade está localizada.
     *
     * @param ilha Nome da ilha.
     */
    public void setIlha(String ilha) {
        this.ilha = ilha;
    }

    /**
     * Retorna o ID único da propriedade.
     *
     * @return ID da propriedade.
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * Retorna o ID de PAR.
     *
     * @return ID de PAR.
     */
    public String getParId() {
        return parId;
    }

    /**
     * Retorna o número de PAR.
     *
     * @return Número de PAR.
     */
    public double getParNum() {
        return parNum;
    }

    /**
     * Retorna o comprimento da propriedade.
     *
     * @return Comprimento da propriedade.
     */
    public double getShapeLength() {
        return shapeLength;
    }

    /**
     * Retorna a área da propriedade.
     *
     * @return Área da propriedade.
     */
    public double getShapeArea() {
        return shapeArea;
    }

    /**
     * Retorna a geometria da propriedade.
     *
     * @return Geometria da propriedade (MULTIPOLYGON).
     */
    public String getGeometry() {
        return geometry;
    }

    /**
     * Retorna o ID do proprietário da propriedade.
     *
     * @return ID do proprietário.
     */
    public int getOwner() {
        return owner;
    }

    /**
     * Retorna o nome da freguesia onde a propriedade está localizada.
     *
     * @return Nome da freguesia.
     */
    public String getFreguesia() {
        return freguesia;
    }

    /**
     * Retorna o nome do município onde a propriedade está localizada.
     *
     * @return Nome do município.
     */
    public String getMunicipio() {
        return municipio;
    }

    /**
     * Retorna o nome da ilha onde a propriedade está localizada.
     *
     * @return Nome da ilha.
     */
    public String getIlha() {
        return ilha;
    }

    /**
     * Retorna uma representação em formato String dos dados da propriedade rústica.
     *
     * @return Representação da propriedade.
     */
    @Override
    public String toString() {
        return "PropriedadeRustica{" +
                "objectId='" + objectId + '\'' +
                ", parId='" + parId + '\'' +
                ", parNum=" + parNum +
                ", shapeLength=" + shapeLength +
                ", shapeArea=" + shapeArea +
                ", geometry='" + geometry + '\'' +
                ", owner=" + owner +
                ", freguesia='" + freguesia + '\'' +
                ", municipio='" + municipio + '\'' +
                ", ilha='" + ilha + '\'' +
                '}';
    }
}
