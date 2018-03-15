package be.uantwerpen.idlab.cobra.common.models;

public class BlockReference
{
    private String id;

    public BlockReference()
    {
        this.id = "-1";
    }

    public BlockReference(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return this.id;
    }

    @Override
    public String toString()
    {
        return this.id;
    }
}
