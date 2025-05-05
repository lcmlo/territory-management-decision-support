package iscteiul.ista;

import iscteiul.ista.PropriedadeRustica;

import java.util.*;
import java.util.stream.Collectors;

public class CalculadoraArea {

    /**
     * Calcula a área média das propriedades com base numa área geográfica fornecida.
     *
     * @param propriedades Lista de propriedades carregadas.
     */
    public static void calcularAreaMediaPorZona(List<PropriedadeRustica> propriedades) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Escolha o tipo de área geográfica (freguesia, municipio ou ilha):");
        String tipo = scanner.nextLine().trim().toLowerCase();

        System.out.println("Indique o nome da " + tipo + ":");
        String nome = scanner.nextLine().trim();

        List<PropriedadeRustica> filtradas = propriedades.stream().filter(p -> {
            return switch (tipo) {
                case "freguesia" -> p.getFreguesia().equalsIgnoreCase(nome);
                case "municipio" -> p.getMunicipio().equalsIgnoreCase(nome);
                case "ilha" -> p.getIlha().equalsIgnoreCase(nome);
                default -> false;
            };
        }).collect(Collectors.toList());

        if (filtradas.isEmpty()) {
            System.out.println("Nenhuma propriedade encontrada para o critério indicado.");
            return;
        }

        double areaTotal = filtradas.stream().mapToDouble(PropriedadeRustica::getShapeArea).sum();
        double media = areaTotal / filtradas.size();

        System.out.printf("Área média das %d propriedades em %s (%s): %.2f m²%n",
                filtradas.size(), nome, tipo, media);
    }

    /**
     * Calcula a área média assumindo fusão de propriedades adjacentes do mesmo proprietário.
     *
     * @param propriedades Lista de propriedades carregadas.
     * @param grafo Grafo de adjacências das propriedades.
     */
    public static void calcularAreaMediaComFusao(List<PropriedadeRustica> propriedades, GrafoPropriedades grafo) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Escolha o tipo de área geográfica (freguesia, municipio ou ilha):");
        String tipoZona = scanner.nextLine().trim().toLowerCase();

        System.out.println("Indique o nome da " + tipoZona + ":");
        String nomeZona = scanner.nextLine().trim();

        // 1. Filtrar
        List<PropriedadeRustica> filtradas = propriedades.stream()
                .filter(p -> tipoZona.equalsIgnoreCase("freguesia") && p.getFreguesia().equalsIgnoreCase(nomeZona)
                        || tipoZona.equalsIgnoreCase("municipio") && p.getMunicipio().equalsIgnoreCase(nomeZona)
                        || tipoZona.equalsIgnoreCase("ilha") && p.getIlha().equalsIgnoreCase(nomeZona))
                .toList();

        Set<PropriedadeRustica> visitadas = new HashSet<>();
        List<Double> areasAgrupadas = new ArrayList<>();

        for (PropriedadeRustica p : filtradas) {
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
                                && filtradas.contains(vizinha)) {
                            fila.add(vizinha);
                            visitadas.add(vizinha);
                        }
                    }
                }
                areasAgrupadas.add(areaGrupo);
            }
        }

        double media = areasAgrupadas.stream().mapToDouble(Double::doubleValue).average().orElse(0);

        System.out.printf("Área média das %d propriedades agrupadas por proprietário em %s (%s): %.2f m²%n",
                areasAgrupadas.size(), nomeZona, tipoZona, media);
    }

    /**
     * Calcula a área média assumindo fusão de propriedades adjacentes do mesmo proprietário,
     * para uma zona geográfica passada por parâmetro.
     *
     * @param propriedades Lista de propriedades carregadas.
     * @param grafo Grafo de adjacências das propriedades.
     * @param tipoZona Tipo de zona geográfica (freguesia, municipio, ilha).
     * @param nomeZona Nome da zona geográfica.
     * @return Média das áreas após fusão de propriedades adjacentes do mesmo dono.
     */
    public static double calcularAreaMediaComFusao(List<PropriedadeRustica> propriedades, GrafoPropriedades grafo,
                                                   String tipoZona, String nomeZona) {

        List<PropriedadeRustica> filtradas = propriedades.stream()
                .filter(p -> tipoZona.equalsIgnoreCase("freguesia") && p.getFreguesia().equalsIgnoreCase(nomeZona)
                        || tipoZona.equalsIgnoreCase("municipio") && p.getMunicipio().equalsIgnoreCase(nomeZona)
                        || tipoZona.equalsIgnoreCase("ilha") && p.getIlha().equalsIgnoreCase(nomeZona))
                .toList();

        Set<PropriedadeRustica> visitadas = new HashSet<>();
        List<Double> areasAgrupadas = new ArrayList<>();

        for (PropriedadeRustica p : filtradas) {
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
                                && filtradas.contains(vizinha)) {
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


}
