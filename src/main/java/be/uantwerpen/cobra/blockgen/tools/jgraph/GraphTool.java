package be.uantwerpen.cobra.blockgen.tools.jgraph;

import be.uantwerpen.cobra.blockgen.models.blocks.Block;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thomas on 10/04/2016.
 */
public class GraphTool
{
    private static final Color DEFAULT_BG_COLOR = Color.white;
    private static final Dimension DEFAULT_SIZE = new Dimension(800, 600);

    private JGraphModelAdapter<Block, DefaultEdge> graphModelAdapter;

    public int createGraphFromBlock(Block block)
    {
        int level = 0;
        JFrame jFrame = createFrame();

        Graph<Block, DefaultEdge> graph = new ListenableDirectedGraph<Block, DefaultEdge>(NoLabelEdge.class);

        graphModelAdapter = new JGraphModelAdapter<Block, DefaultEdge>(graph);

        JGraph jGraph = new JGraph(graphModelAdapter);

        adjustDisplaySettings(jGraph);
        jFrame.getContentPane().add(jGraph);
        jFrame.resize(DEFAULT_SIZE);

        Block blockPointer = block;


        graph.addVertex(block);
        positionVertexAt(block, 350, 50);
        addVertices(block, graph, 0);

        return 0;
    }

    private void addVertices(Block block, Graph graph, int level)
    {
        int i = 0;

        level++;

        for(Block childBlock : block.getChildBlocks())
        {
            graph.addVertex(childBlock);
            graph.addEdge(block, childBlock);

            positionVertexAt(childBlock, 50 + i * 150, 100 * level);

            addVertices(childBlock, graph, level);

            i++;
        }

        return;
    }

    private JFrame createFrame()
    {
        JFrame frame = new JFrame();

        frame.setTitle("HPAFramework graph");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        return frame;
    }

    private void adjustDisplaySettings(JGraph graph)
    {
        graph.setPreferredSize(DEFAULT_SIZE);
        graph.setBackground(DEFAULT_BG_COLOR);
    }

    private void positionVertexAt(Object vertex, int x, int y)
    {
        DefaultGraphCell cell = graphModelAdapter.getVertexCell(vertex);
        Map attributes = cell.getAttributes();
        Rectangle2D bounds = GraphConstants.getBounds(attributes);

        GraphConstants.setBounds(attributes, new Rectangle2D.Double(x, y, bounds.getWidth(), bounds.getHeight()));

        Map cellAttributes = new HashMap();
        cellAttributes.put(cell, attributes);
        graphModelAdapter.edit(cellAttributes, null, null, null);
    }
}
