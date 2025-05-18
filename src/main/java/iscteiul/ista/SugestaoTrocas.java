package iscteiul.ista;



import org.locationtech.jts.geom.Coordinate;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Classe que sugere trocas de propriedades rústicas entre proprietários com base em critérios de
 * continuidade, área, compacidade e proximidade geográfica.
 */
public class SugestaoTrocas {

    /**
     * Sugere trocas entre propriedades rústicas pertencentes a proprietários vizinhos no grafo de proprietários.
     * A troca é sugerida se melhorar a média da área agrupada e preservar critérios de continuidade e área semelhante.
     *
     * @param propriedades Lista de propriedades rústicas a considerar.
     * @param grafoProprietarios Grafo representando relações entre proprietários.
     * @param grafoPropriedades Grafo representando relações entre propriedades (vizinhança).
     * @return Lista de pares de propriedades sugeridas para troca.
     */
    public static List<Pair<PropriedadeRustica, PropriedadeRustica>> sugerirTrocas(
            List<PropriedadeRustica> propriedades,
            GrafoPropietarios grafoProprietarios,
            GrafoPropriedades grafoPropriedades,List<Municipio> tresMunicipios) {
        List<Pair<PropriedadeRustica, PropriedadeRustica>> trocasSugeridas = new ArrayList<>();

        StringBuilder sb = new StringBuilder();

        Map<Integer, List<PropriedadeRustica>> mapaPorDono = propriedades.stream()
                .collect(Collectors.groupingBy(PropriedadeRustica::getOwner));

        for (Integer donoA : mapaPorDono.keySet()) {
            for (Integer vizinho : grafoProprietarios.getVizinhos(donoA)) {
                if (donoA >= vizinho) continue;

                List<PropriedadeRustica> terrasA = mapaPorDono.get(donoA);
                List<PropriedadeRustica> terrasB = mapaPorDono.get(vizinho);

                for (PropriedadeRustica a : terrasA) {
                    for (PropriedadeRustica b : terrasB) {
                        double valorA = PropriedadeRustica.avaliarPropriedade(a, tresMunicipios);
                        double valorB = PropriedadeRustica.avaliarPropriedade(b, tresMunicipios);
                        System.out.println(valorA + "," + valorB);
                        double diffRelativa = Math.abs(valorA - valorB) / ((valorA + valorB) / 2.0);
                        boolean valoresSemelhantes = diffRelativa <= 0.05;

                        if (valoresSemelhantes) {
                            List<PropriedadeRustica> novasTerrasA = new ArrayList<>(terrasA);
                            List<PropriedadeRustica> novasTerrasB = new ArrayList<>(terrasB);
                            novasTerrasA.remove(a);
                            novasTerrasB.remove(b);

                            b.setOwner(donoA);
                            a.setOwner(vizinho);
                            novasTerrasA.add(b);
                            novasTerrasB.add(a);

                            double mediaAntesA = calcularMediaComFusaoIndividual(terrasA, grafoPropriedades);
                            double mediaAntesB = calcularMediaComFusaoIndividual(terrasB, grafoPropriedades);
                            double mediaDepoisA = calcularMediaComFusaoIndividual(novasTerrasA, grafoPropriedades);
                            double mediaDepoisB = calcularMediaComFusaoIndividual(novasTerrasB, grafoPropriedades);

                            a.setOwner(donoA);
                            b.setOwner(vizinho);

                            if (mediaDepoisA > mediaAntesA && mediaDepoisB > mediaAntesB) {
                                boolean conectaA = conectaGrupoVizinho(b, terrasA, grafoPropriedades);
                                boolean conectaB = conectaGrupoVizinho(a, terrasB, grafoPropriedades);
                                boolean melhoraContinuidade = conectaA || conectaB;

                                double areaDiff = Math.abs(a.getShapeArea() - b.getShapeArea());
                                double areaMedia = (a.getShapeArea() + b.getShapeArea()) / 2.0;
                                double percentualDiferenca = areaDiff / areaMedia;

                                if (percentualDiferenca < 0.2 && melhoraContinuidade) {
                                    trocasSugeridas.add(new Pair<>(a, b));

                                    sb.append(String.format(
                                            "\n🔁 Trocar propriedade %s (dono %d, área %.2f m²) ↔ propriedade %s (dono %d, área %.2f m²)\n",
                                            a.getObjectId(), donoA, a.getShapeArea(),
                                            b.getObjectId(), vizinho, b.getShapeArea()
                                    ));
                                    sb.append(String.format("   📈 Média dono %d: antes = %.2f m², depois = %.2f m²\n", donoA, mediaAntesA, mediaDepoisA));
                                    sb.append(String.format("   📈 Média dono %d: antes = %.2f m², depois = %.2f m²\n", vizinho, mediaAntesB, mediaDepoisB));
                                }
                            }
                        }
                    }
                }
            }
        }

        if (sb.isEmpty()) {
            sb.append("⚠️ Nenhuma troca sugerida.");
        }

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setCaretPosition(0);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(800, 600));

        JFrame frame = new JFrame("Sugestões de Troca");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(scrollPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        return trocasSugeridas;
    }

    /**
     * Calcula a média das áreas agrupadas das propriedades de um proprietário,
     * considerando grupos conectados pelo grafo de propriedades.
     *
     * @param propriedades Propriedades do mesmo proprietário.
     * @param grafo Grafo que define a vizinhança entre propriedades.
     * @return Média das áreas dos grupos conectados das propriedades.
     */
    public static double calcularMediaComFusaoIndividual(List<PropriedadeRustica> propriedades, GrafoPropriedades grafo) {
        Set<PropriedadeRustica> visitadas = new HashSet<>();
        List<Double> areasAgrupadas = new ArrayList<>();

        for (PropriedadeRustica p : propriedades) {
            if (!visitadas.contains(p)) {
                double areaGrupo = 0;
                Queue<PropriedadeRustica> fila = new LinkedList<>();
                fila.add(p);
                visitadas.add(p);

                while (!fila.isEmpty()) {
                    PropriedadeRustica atual = fila.poll();
                    areaGrupo += atual.getShapeArea();

                    for (PropriedadeRustica vizinha : grafo.getGrafo().getOrDefault(atual, Set.of())) {
                        if (!visitadas.contains(vizinha)
                                && vizinha.getOwner() == atual.getOwner()
                                && propriedades.contains(vizinha)) {
                            fila.add(vizinha);
                            visitadas.add(vizinha);
                        }
                    }
                }
                areasAgrupadas.add(areaGrupo);
            }
        }

        return areasAgrupadas.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    /**
     * Verifica se uma nova propriedade conecta-se a algum grupo de propriedades já existentes
     * do mesmo proprietário no grafo.
     *
     * @param novaProp Nova propriedade a ser conectada.
     * @param outrasDoMesmoDono Lista de outras propriedades do mesmo proprietário.
     * @param grafo Grafo que representa vizinhança entre propriedades.
     * @return true se conecta a pelo menos uma propriedade do grupo, false caso contrário.
     */
    public static boolean conectaGrupoVizinho(PropriedadeRustica novaProp,
                                              List<PropriedadeRustica> outrasDoMesmoDono,
                                              GrafoPropriedades grafo) {
        for (PropriedadeRustica outra : outrasDoMesmoDono) {
            if (grafo.getGrafo().getOrDefault(novaProp, Set.of()).contains(outra)) {
                return true; // conecta-se a alguma já existente
            }
        }
        return false;
    }

    /**
     * Lê informações de municípios a partir de um ficheiro CSV e retorna os três municípios com maior população.
     * O ficheiro deve estar no formato: nome;população;longitude;latitude
     *
     * @param fileName Caminho para o ficheiro CSV.
     * @return Lista com os três municípios mais populosos.
     */
    public static List<Municipio> tresMaioresMunicipios(String fileName) {

        List<Municipio> municipios = new ArrayList<>();

        try (InputStream is = SugestaoTrocas.class.getClassLoader().getResourceAsStream(fileName);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            if (is == null) {
                System.err.println("Ficheiro " + fileName + " não encontrado no classpath.");
                return Collections.emptyList();
            }

            String linha = br.readLine(); // ler cabeçalho
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(";");
                String nome = partes[0].trim();
                int populacao = Integer.parseInt(partes[1].trim());
                Coordinate coord = new Coordinate(Double.parseDouble(partes[2].trim()), Double.parseDouble(partes[3].trim()));
                municipios.add(new Municipio(nome, populacao, coord));
            }
        } catch (IOException e) {
            System.err.println("Erro a ler o ficheiro: " + e.getMessage());
        }


        // Ordenar por população decrescente e obter os 3 primeiros
        List<Municipio> top3 = municipios.stream()
                .sorted(Comparator.comparingInt(Municipio::getPopulacao).reversed())
                .limit(3)
                .collect(Collectors.toList());

        System.out.println("Top 3 municípios com maior população:");
        for (Municipio m : top3) {
            System.out.printf("- %s (%d habitantes)%n", m.getNome(), m.getPopulacao());
        }
        return municipios;
    }


    /**
     * Calcula a distância, em metros, entre o centroide da propriedade rústica e o centro do município.
     *
     * @param propriedade Propriedade rústica cuja distância será calculada.
     * @param municipio Município de referência.
     * @return Distância em metros; retorna -1 se a geometria da propriedade não estiver definida.
     */
    public static double calcularDistancia(PropriedadeRustica propriedade, Municipio municipio) {
        if (propriedade.getGeometryObj() == null) {
            System.err.println("Geometria da propriedade não está definida.");
            return -1;
        }

        Coordinate centroide = propriedade.getGeometryObj().getCentroid().getCoordinate();
        Coordinate cidade = municipio.getCoordenadas();
        return distancia(centroide, cidade);
    }


    /**
     * Calcula a distância haversine entre duas coordenadas geográficas.
     *
     * @param c1 Primeira coordenada (longitude, latitude).
     * @param c2 Segunda coordenada (longitude, latitude).
     * @return Distância em metros entre as duas coordenadas.
     */
    public static double distancia(Coordinate c1, Coordinate c2) {
        double R = 6371000; // raio da Terra em metros
        double lat1 = Math.toRadians(c1.y);
        double lon1 = Math.toRadians(c1.x);
        double lat2 = Math.toRadians(c2.y);
        double lon2 = Math.toRadians(c2.x);

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dlon / 2) * Math.sin(dlon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }


}
