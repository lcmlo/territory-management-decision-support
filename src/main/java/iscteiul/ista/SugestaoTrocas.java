package iscteiul.ista;

import org.locationtech.jts.geom.Coordinate;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class SugestaoTrocas {

    public static List<Pair<PropriedadeRustica, PropriedadeRustica>> sugerirTrocas(
            List<PropriedadeRustica> propriedades,
            GrafoPropietarios grafoProprietarios,
            GrafoPropriedades grafoPropriedades) {
        List<Pair<PropriedadeRustica, PropriedadeRustica>> trocasSugeridas = new ArrayList<>();
        Map<PropriedadeRustica,List<PropriedadeRustica>> proximidade= mapaPropriedadesProximas(propriedades);
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
                            double areaDiff = Math.abs(a.getShapeArea() - b.getShapeArea());
                            double areaMedia = (a.getShapeArea() + b.getShapeArea()) / 2.0;
                            double percentualDiferenca = areaDiff / areaMedia;

                            if (percentualDiferenca < 0.2 && proximidade.get(a).contains(b)) {
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
     * Retorna um mapa onde cada propriedade está associada às propriedades de donos diferentes
     * que estão a menos de 5 km de distância.
     *
     * @param propriedades Lista de propriedades
     * @return Mapa com as propriedades e suas vizinhas próximas (< 5 km)
     */
    public static Map<PropriedadeRustica, List<PropriedadeRustica>> mapaPropriedadesProximas(List<PropriedadeRustica> propriedades) {
        Map<PropriedadeRustica, List<PropriedadeRustica>> proximidades = new HashMap<>();

        for (int i = 0; i < propriedades.size(); i++) {
            PropriedadeRustica a = propriedades.get(i);

            for (int j = i + 1; j < propriedades.size(); j++) {
                PropriedadeRustica b = propriedades.get(j);

                if (a.getOwner() != b.getOwner()) {
                    double dist = a.getGeometryObj().distance(b.getGeometryObj());

                    if (dist < 5000) { // 5 km
                        proximidades.computeIfAbsent(a, k -> new ArrayList<>()).add(b);
                        proximidades.computeIfAbsent(b, k -> new ArrayList<>()).add(a); // garantir simetria

                        System.out.printf(" Distância: %.2f m entre %s e %s%n",
                                dist, a.getObjectId(), b.getObjectId());
                    }
                }
            }
        }

        return proximidades;
    }

    public List<Municipio> tresMaioresMunicipios(String fileName) {
        // String caminhoCsv = "C:\\Users\\Utilizador\\IdeaProjects\\ES-2024-25-2Sem-GrupoK\\src\\main\\resources\\InfoMadeira.csv"; // Caminho para o ficheiro CSV

        List<Municipio> municipios = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String linha = br.readLine(); // Ler cabeçalho
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(";");
                String nome = partes[0].trim();
                int populacao = Integer.parseInt(partes[1].trim());
                Coordinate coord = new Coordinate(Double.parseDouble(partes[2].trim()), Double.parseDouble(partes[3].trim()));
                municipios.add(new Municipio(nome, populacao,coord));
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

    public ResultadoMunicipioDistancia principalMunicipioMaisPerto(List<Municipio> municipios, PropriedadeRustica propriedade) {
        double menorDistancia = Double.MAX_VALUE;
        Municipio municipioMaisPerto = null;

        for (Municipio m : municipios) {
            double distancia = calcularDistancia(propriedade, m);
            if (distancia < menorDistancia) {
                menorDistancia = distancia;
                municipioMaisPerto = m;
            }
        }

        return new ResultadoMunicipioDistancia(municipioMaisPerto, menorDistancia);
    }

    public static double calcularDistancia(PropriedadeRustica propriedade, Municipio municipio) {
        if (propriedade.getGeometryObj() == null) {
            System.err.println("Geometria da propriedade não está definida.");
            return -1;
        }

        Coordinate centroide = propriedade.getGeometryObj().getCentroid().getCoordinate();
        Coordinate cidade = municipio.getCoordenadas();
        return distancia(centroide, cidade);
    }

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
                + Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
