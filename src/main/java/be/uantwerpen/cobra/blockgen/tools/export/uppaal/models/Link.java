package be.uantwerpen.cobra.blockgen.tools.export.uppaal.models;

/**
 * Created by Thomas on 25/04/2016.
 */
public class Link
{
    private Node sourceNode;
    private Node targetNode;
    private String select;
    private String guard;
    private String sync;
    private String update;

    public Link()
    {
        this.sourceNode = null;
        this.targetNode = null;
        this.select = null;
        this.guard = null;
        this.sync = null;
        this.update = null;
    }

    public Link(Node sourceNode, Node targetNode)
    {
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.select = null;
        this.guard = null;
        this.sync = null;
        this.update = null;
    }

    public Node getSourceNode()
    {
        return sourceNode;
    }

    public void setSourceNode(Node sourceNode)
    {
        this.sourceNode = sourceNode;
    }

    public Node getTargetNode()
    {
        return targetNode;
    }

    public void setTargetNode(Node targetNode)
    {
        this.targetNode = targetNode;
    }

    public void setLinkNodes(Node sourceNode, Node targetNode)
    {
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
    }

    public String getSelect()
    {
        return select;
    }

    public void setSelect(String select)
    {
        this.select = select;
    }

    public String getGuard()
    {
        return guard;
    }

    public void setGuard(String guard)
    {
        this.guard = guard;
    }

    public String getSync()
    {
        return sync;
    }

    public void setSync(String sync)
    {
        this.sync = sync;
    }

    public String getUpdate()
    {
        return this.update;
    }

    public void setUpdate(String update)
    {
        this.update = update;
    }
}
