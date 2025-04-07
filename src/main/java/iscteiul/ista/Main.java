package iscteiul.ista;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;


/**
 * Classe principal para carregar propriedades rústicas de um arquivo CSV e exibi-las.
 * Utiliza a biblioteca OpenCSV para ler o arquivo CSV e extrair os dados.
 */
public class Main {

    /**
     * Método principal que carrega e exibe as propriedades rústicas de um arquivo CSV.
     *
     * @param args Argumentos de linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        String fileName = "Madeira-Moodle-1.1.csv";
        String fileName1 = "111.csv";//csv com menos dados

        List<PropriedadeRustica> propriedades = carregarPropriedadesCSV(fileName);
        GrafoPropietarios grafo = new GrafoPropietarios();

        // Exibe as propriedades carregadas
        for (PropriedadeRustica propriedade : propriedades) {
            System.out.println(propriedade);
            int owner = propriedade.getOwner();
            grafo.adicionarProprietario(owner);
        }

        int numThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < propriedades.size(); i++) {
            final int index = i;
            executor.submit(() -> {
                PropriedadeRustica p1 = propriedades.get(index);

                for (int j = index + 1; j < propriedades.size(); j++) {
                    PropriedadeRustica p2 = propriedades.get(j);

                    if (saoAdjacentes(p1, p2)) {
                        synchronized (grafo) { // 确保 grafo 的线程安全性
                            grafo.adicionarVizinhanca(p1.getOwner(), p2.getOwner());
                        }
                    }
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        grafo.exibirGrafo();
    }

    public static boolean saoAdjacentes(PropriedadeRustica p1, PropriedadeRustica p2) {
        // 检查 Freguesia 和 Municipio 是否相同 (更严格)
        if (!(p1.getFreguesia().equals(p2.getFreguesia()) && p1.getMunicipio().equals(p2.getMunicipio()))) {
            return false; // 如果不同，直接返回 false
        }

        try {
            // 使用 WKTReader 来读取 WKT (Well-Known Text) 格式的 Geometry
            WKTReader reader = new WKTReader();
            Geometry geometry1 = reader.read(p1.getGeometry());
            Geometry geometry2 = reader.read(p2.getGeometry());

            // 检查两个几何体是否相交
            return geometry1.intersects(geometry2);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * Carrega as propriedades rústicas a partir de um arquivo CSV.
     *
     * @param fileName Nome do arquivo CSV.
     * @return Lista de propriedades rústicas carregadas a partir do arquivo CSV.
     */
    public static List<PropriedadeRustica> carregarPropriedadesCSV(String fileName) {
        List<PropriedadeRustica> propriedades = new ArrayList<>();

        // Usando ClassLoader para aceder ao arquivo na pasta resources
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(fileName);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             CSVReader reader = new CSVReader(inputStreamReader)) {

            if (inputStream == null) {
                System.out.println("Arquivo não encontrado: " + fileName);
                return propriedades; // Retorna lista vazia se o arquivo não for encontrado
            }

            String[] linha;
            reader.skip(1); // Pula o cabeçalho
            while ((linha = reader.readNext()) != null) { // Lê todas as linhas
                PropriedadeRustica propriedade = new PropriedadeRustica();
                propriedade.setObjectId(linha[0]);
                propriedade.setParId(linha[1]);

                try {
                    propriedade.setParNum(Double.parseDouble(linha[2].replace(',', '.')));
                } catch (NumberFormatException e) {
                    System.out.println("Erro de formatação em PAR_NUM: " + linha[2]);
                    continue; // Continua com a próxima linha em vez de retornar
                }

                try {
                    propriedade.setShapeLength(Double.parseDouble(linha[3].replace(',', '.')));
                    propriedade.setShapeArea(Double.parseDouble(linha[4].replace(',', '.')));
                } catch (NumberFormatException e) {
                    System.out.println("Erro de formatação nos valores de Shape: " + linha[3] + ", " + linha[4]);
                    continue; // Continua com a próxima linha em vez de retornar
                }

                String geometry = linha[5];

                propriedade.setGeometry(geometry);
                propriedade.setOwner(Integer.parseInt(linha[6]));
                propriedade.setFreguesia(linha[7]);
                propriedade.setMunicipio(linha[8]);
                propriedade.setIlha(linha[9]);

                propriedades.add(propriedade); // Adiciona a propriedade à lista
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return propriedades;
    }
}
