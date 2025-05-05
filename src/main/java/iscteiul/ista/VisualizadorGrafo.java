package iscteiul.ista;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe que permite visualizar um grafo (JGraphT) de forma gráfica com JGraphX.
 */
public class VisualizadorGrafo {

    /**
     * Cria uma janela Swing para mostrar visualmente um grafo.
     *
     * @param grafo Grafo de JGraphT com nós Integer e arestas DefaultEdge
     * @param titulo Título da janela
     */
    public static void mostrarGrafo(Graph<Integer, DefaultEdge> grafo, String titulo) {
        mxGraph mxgraph = new mxGraph();
        Object parent = mxgraph.getDefaultParent();

        mxgraph.getModel().beginUpdate();
        try {
            Map<Integer, Object> verticesMap = new HashMap<>();

            // Criar nós
            for (Integer vertex : grafo.vertexSet()) {
                Object v = mxgraph.insertVertex(parent, null, vertex, 0, 0, 40, 40);
                verticesMap.put(vertex, v);
            }

            // Criar arestas
            for (DefaultEdge edge : grafo.edgeSet()) {
                Integer source = grafo.getEdgeSource(edge);
                Integer target = grafo.getEdgeTarget(edge);
                mxgraph.insertEdge(parent, null, "", verticesMap.get(source), verticesMap.get(target));
            }
        } finally {
            mxgraph.getModel().endUpdate();
        }

        // Layout circular
        mxIGraphLayout layout = new mxCircleLayout(mxgraph);
        layout.execute(mxgraph.getDefaultParent());

        // Mostrar janela
        JFrame frame = new JFrame(titulo);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);
        frame.getContentPane().add(new mxGraphComponent(mxgraph));
        frame.setVisible(true);
    }
}
