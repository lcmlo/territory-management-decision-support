package iscteiul.ista;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        String fileName = "111.csv";
        //TODO aviso este ficheiro a representacao do grafo e excessiva
       // String fileName = "Madeira-Moodle-1.1.csv";
        List<PropriedadeRustica> propriedades = carregarPropriedadesCSV(fileName);

        GrafoPropriedades grafoVizinhanca = new GrafoPropriedades();

        // Adicionar as propriedades ao grafo
        for (PropriedadeRustica p : propriedades) {
            grafoVizinhanca.adicionarPropriedade(p);
        }

        // Adicionar adjacências entre as propriedades
        grafoVizinhanca.construirAdjacencias();
        grafoVizinhanca.mostrarGrafo();
        // Converte o grafo de propriedades para um grafo de IDs (usando getObjectId())
        Graph<Integer, DefaultEdge> grafoIdsPropriedades = new SimpleGraph<>(DefaultEdge.class);
        Map<PropriedadeRustica, Set<PropriedadeRustica>> mapaPropriedades = grafoVizinhanca.getGrafo();

        // Adiciona os nós (IDs das propriedades)
        for (PropriedadeRustica p : mapaPropriedades.keySet()) {
            grafoIdsPropriedades.addVertex(Integer.parseInt(p.getObjectId()));
        }

        // Adiciona as arestas (ligações entre IDs)
        for (PropriedadeRustica origem : mapaPropriedades.keySet()) {
            int idOrigem = Integer.parseInt(origem.getObjectId());
            for (PropriedadeRustica destino : mapaPropriedades.get(origem)) {
                int idDestino = Integer.parseInt(destino.getObjectId());
                if (!grafoIdsPropriedades.containsEdge(idOrigem, idDestino) && idOrigem != idDestino) {
                    grafoIdsPropriedades.addEdge(idOrigem, idDestino);
                }
            }
        }

        // Chamada ao visualizador
        VisualizadorGrafo.mostrarGrafo(grafoIdsPropriedades, "Grafo de Propriedades Vizinhas");


        // Construir grafo de proprietários
        GrafoPropietarios grafoProprietarios = new GrafoPropietarios();
        grafoProprietarios.construirGrafoProprietarios(grafoVizinhanca, propriedades);
        grafoProprietarios.exibirGrafo();

        // Visualizar graficamente o grafo de proprietários com JGraphX
        Graph<Integer, DefaultEdge> grafoJGraphT = new SimpleGraph<>(DefaultEdge.class);
        for (Integer owner : grafoProprietarios.getGrafo().keySet()) {
            grafoJGraphT.addVertex(owner);
        }
        for (Integer owner : grafoProprietarios.getGrafo().keySet()) {
            for (Integer vizinho : grafoProprietarios.getGrafo().get(owner)) {
                if (!grafoJGraphT.containsEdge(owner, vizinho) && owner != vizinho) {
                    grafoJGraphT.addEdge(owner, vizinho);
                }
            }
        }
        VisualizadorGrafo.mostrarGrafo(grafoJGraphT, "Grafo de Proprietários Vizinhos");
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
