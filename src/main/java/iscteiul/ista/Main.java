package iscteiul.ista;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;

import java.io.BufferedReader;
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
     * Construtor padrão (sem argumentos).
     */
    public Main() {
        // não instanciado
    }

    /**
     * Método principal que carrega e exibe as propriedades rústicas de um arquivo CSV.
     *
     * @param args Argumentos de linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        String fileName = "111.csv";
        //Aviso no "Madeira-Moodle-1.1.csv" a representacao do grafo e gigante
        //String fileName = "Madeira-Moodle-1.1.csv";
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

        CalculadoraArea.calcularAreaMediaPorZona(propriedades);


        List<Municipio> tresMunicipios = SugestaoTrocas.tresMaioresMunicipios("InfoMadeira.csv");
        List<Pair<PropriedadeRustica, PropriedadeRustica>> trocas = SugestaoTrocas.sugerirTrocas(propriedades, grafoProprietarios, grafoVizinhanca,tresMunicipios);
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
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream)))
              {

            if (inputStream == null) {
                System.out.println("Arquivo não encontrado: " + fileName);
                return propriedades; // Retorna lista vazia se o arquivo não for encontrado
            }

            String linha=br.readLine(); // Pula o cabeçalho
            while ((linha = br.readLine()) != null) { // Lê todas as linhas
                String[] partes = linha.split(";");
                PropriedadeRustica propriedade = new PropriedadeRustica();
                propriedade.setObjectId(partes[0]);
                propriedade.setParId(partes[1]);

                try {
                    propriedade.setParNum(Double.parseDouble(partes[2].replace(',', '.')));
                } catch (NumberFormatException e) {
                    System.out.println("Erro de formatação em PAR_NUM: " + partes[2]);
                    continue; // Continua com a próxima linha em vez de retornar
                }

                try {
                    propriedade.setShapeLength(Double.parseDouble(partes[3].replace(',', '.')));
                    propriedade.setShapeArea(Double.parseDouble(partes[4].replace(',', '.')));
                } catch (NumberFormatException e) {
                    System.out.println("Erro de formatação nos valores de Shape: " + partes[3] + ", " + partes[4]);
                    continue; // Continua com a próxima linha em vez de retornar
                }


                String geometry = partes[5];
                propriedade.setGeometry(geometry);
                try {
                    WKTReader wktReader = new WKTReader();
                    Geometry geometryWKT = wktReader.read(geometry);
                    propriedade.setGeometryObj(geometryWKT); // este método precisa de existir na tua classe
                } catch (Exception e) {
                    System.out.println("Erro ao ler geometria WKT: " + geometry);
                    continue; // ignora esta linha
                }

                propriedade.setOwner(Integer.parseInt(partes[6]));
                propriedade.setFreguesia(partes[7]);
                propriedade.setMunicipio(partes[8]);
                propriedade.setIlha(partes[9]);

                propriedades.add(propriedade); // Adiciona a propriedade à lista
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return propriedades;
    }

}
