package iscteiul.ista;

import iscteiul.ista.PropriedadeRustica;

import java.util.List;
import java.util.Scanner;
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
}
