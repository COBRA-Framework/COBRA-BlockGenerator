package be.uantwerpen.idlab.cobra.blockgen.tools.jgraphx;

import be.uantwerpen.idlab.cobra.common.models.blocks.Block;
import be.uantwerpen.idlab.cobra.blockgen.tools.interfaces.GraphDisplay;
import be.uantwerpen.idlab.cobra.common.tools.terminal.Terminal;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Created by Thomas on 1/12/2016.
 */
public class JGraphX implements GraphDisplay
{
    private final static int MIN_WIDTH = 400;
    private final static int MIN_HEIGHT = 320;

    public JFrame DisplayGraphFromBlock(Block block) throws Exception
    {
        JFrame frame;
        mxGraphComponent graphComponent;

        try
        {
            graphComponent = new mxGraphComponent(generateGraphFromBlock(block));
            graphComponent.setToolTips(true);

            frame = createFrame(graphComponent, block.toString());
        }
        catch(Exception e)
        {
            throw new Exception("Could not display graph in window! " + e.getMessage(), e);
        }

        return frame;
    }

    private mxGraph generateGraphFromBlock(Block block) throws Exception
    {
        mxGraph graph = new mxGraph()
        {
            @Override
            public String getToolTipForCell(Object cell)
            {
                String result = "";

                Object object = (((mxCell)cell).getValue());

                if(object != null)
                {
                    if(Block.class.isAssignableFrom(object.getClass()))
                    {
                        if(((Block)object).getCodeSegment() != null)
                        {
                            result = ((Block)object).getCodeSegment().toString();
                        }
                        else
                        {
                            result = object.toString();
                        }
                    }
                }

                return result;
            }

            @Override
            public String getLabel(Object cell)
            {
                String result = "";

                if(cell != null)
                {
                    mxCellState state = this.view.getState(cell);
                    Map<String, Object> style = state != null ? state.getStyle() : this.getCellStyle(cell);
                    if(this.labelsVisible && !mxUtils.isTrue(style, mxConstants.STYLE_NOLABEL, false))
                    {
                        Object object = (((mxCell)cell).getValue());

                        try
                        {
                            if(Block.class.isAssignableFrom(object.getClass()))
                            {
                                result = "[" + ((Block)object).getClass().getSimpleName() + "]";
                            }
                            else
                            {
                                result = this.convertValueToString(cell);
                            }
                        }
                        catch(Exception e)
                        {
                            result = this.convertValueToString(cell);
                        }
                    }
                }

                return result;
            }
        };

        Object contentPanel = graph.getDefaultParent();

        graph.getModel().beginUpdate();

        try
        {
            //Add root block
            Object rootVertex = graph.insertVertex(contentPanel, null, block, 20,20,95, 25);

            addChildVertices(block, graph, rootVertex);

            //Set automatic layout
            mxCompactTreeLayout layout = new mxCompactTreeLayout(graph, false, false);
            layout.execute(contentPanel, rootVertex);
        }
        catch(Exception e)
        {
            throw new Exception("Could not generate JGraphX object! " + e.getMessage(), e);
        }
        finally
        {
            graph.getModel().endUpdate();
        }

        return graph;
    }

    private void addChildVertices(Block block, mxGraph graph, Object parentVertex)
    {
        Object contentPanel = graph.getDefaultParent();

        for(Block childBlock : block.getChildBlocks())
        {
            Object childVertex = graph.insertVertex(contentPanel, null, childBlock, 20,20,95, 25);
            graph.insertEdge(contentPanel, null, null, parentVertex, childVertex);

            addChildVertices(childBlock, graph, childVertex);
        }
    }

    private JFrame createFrame(mxGraphComponent graph, String name)
    {
        JFrame frame = new JFrame();

        frame.setTitle("Block Viewer (" + name + ") | COBRA - Block Generator [v." + this.getClass().getPackage().getImplementationVersion() + "]  -  Created by Thomas Huybrechts, IDLab, UAntwerp (c) 2016-2019");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try
        {
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("images/BlockGenIcon.png")));
        }
        catch(Exception e)
        {
            Terminal.printTerminalWarning("Missing resources: \"images/BlockGenIcon.png\". Installation is corrupted!");
        }

        graph.setEnabled(false);
        frame.getContentPane().add(graph);

        //Set frame size
        frame.pack();   //Make a close fit with the graph

        Dimension frameSize = frame.getSize();
        int newWidth = frameSize.width + 20;
        int newHeight = frameSize.height + 20;

        frame.setSize(new Dimension(newWidth, newHeight));
        frame.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));

        frame.setVisible(true);

        return frame;
    }
}
