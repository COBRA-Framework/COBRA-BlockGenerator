package be.uantwerpen.cobra.blockgen.tools.export.uppaal.models;

/**
 * Created by Thomas on 18/11/2016.
 */
public class NodeLinker
{
    private Node node;
    private int linkDistance;

    private NodeLinker()
    {
        this(null, 1);
    }

    public NodeLinker(Node node)
    {
        this(node, 1);
    }

    public NodeLinker(Node node, int linkDistance)
    {
        this.node = node;
        this.linkDistance = linkDistance;
    }

    public Node getNode()
    {
        return this.node;
    }

    public boolean isLocked()
    {
        return this.linkDistance > 0;
    }

    public int getLinkDistance()
    {
        return this.linkDistance;
    }

    public boolean tryLinking()
    {
        this.linkDistance = this.linkDistance - 1;

        return isLocked();
    }
}
