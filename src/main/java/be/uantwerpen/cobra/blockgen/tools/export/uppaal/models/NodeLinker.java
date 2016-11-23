package be.uantwerpen.cobra.blockgen.tools.export.uppaal.models;

/**
 * Created by Thomas on 18/11/2016.
 */
public class NodeLinker extends Node
{
    private int linkDistance;

    public NodeLinker()
    {
        super();
        this.linkDistance = 1;
    }

    public NodeLinker(int id, String name, String invariant, String comments)
    {
        super(id, name, invariant, comments);
        this.linkDistance = 1;
    }

    public NodeLinker(int id, String name, String invariant, String comments, int linkDistance)
    {
        super(id, name, invariant, comments);
        this.linkDistance = linkDistance;
    }

    public boolean isLocked()
    {
        return this.linkDistance > 0;
    }

    public void setLinkDistance(int linkDistance)
    {
        this.linkDistance = linkDistance;
    }

    public int getLinkDistance()
    {
        return this.linkDistance;
    }

    public boolean tryLinking()
    {
        this.linkDistance = this.linkDistance - 1;

        return !isLocked();
    }
}
