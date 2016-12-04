package be.uantwerpen.cobra.blockgen.tools.export.uppaal.models;

import java.util.Vector;

/**
 * Created by Thomas on 25/04/2016.
 */
public class Node
{
    private int id;
    private String name;
    private String invariant;
    private String comments;
    private boolean initial;
    private boolean urgent;
    private boolean committed;
    private int locX;
    private int locY;
    private Vector<Link> links;

    public Node()
    {
        this.id = 0;
        this.name = null;
        this.invariant = null;
        this.comments = null;
        this.initial = false;
        this.urgent = false;
        this.committed = false;
        this.locX = 0;
        this.locY = 0;
        this.links = new Vector<Link>();
    }

    public Node(int id, String name, String invariant, String comments)
    {
        this.id = id;
        this.name = name;
        this.invariant = invariant;
        this.comments = comments;
        this.initial = false;
        this.urgent = false;
        this.committed = false;
        this.locX = 0;
        this.locY = 0;
        this.links = new Vector<Link>();
    }

    public int getId()
    {
        return this.id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getInvariant()
    {
        return invariant;
    }

    public void setInvariant(String invariant)
    {
        this.invariant = invariant;
    }

    public String getComments()
    {
        return comments;
    }

    public void setComments(String comments)
    {
        this.comments = comments;
    }

    public boolean isInitial()
    {
        return initial;
    }

    public void setInitial(boolean initial)
    {
        this.initial = initial;
    }

    public boolean isUrgent()
    {
        return urgent;
    }

    public void setUrgent(boolean urgent)
    {
        this.urgent = urgent;
    }

    public boolean isCommitted()
    {
        return committed;
    }

    public void setCommitted(boolean committed)
    {
        this.committed = committed;
    }

    public boolean getCommitted()
    {
        return this.committed;
    }
    
    public int getLocX()
    {
        return locX;
    }

    public void setLocX(int locX)
    {
        this.locX = locX;
    }

    public int getLocY()
    {
        return locY;
    }

    public void setLocY(int locY)
    {
        this.locY = locY;
    }

    public void setLocation(int locX, int locY)
    {
        this.locX = locX;
        this.locY = locY;
    }

    public void addLink(Link link)
    {
        link.setSourceNode(this);

        this.links.add(link);
    }

    public Vector<Link> getLinks()
    {
        return this.links;
    }

    public Vector<Node> getLeafNodes()
    {
        Vector<Node> leafs = new Vector<Node>();

        for(Link link : this.links)
        {
            Node node = link.getTargetNode();

            if(node != null && node != this)
            {
                if(node.getLinks().isEmpty())
                {
                    leafs.add(node);
                }
                else
                {
                    leafs.addAll(node.getLeafNodes());
                }
            }
        }

        return leafs;
    }
}
