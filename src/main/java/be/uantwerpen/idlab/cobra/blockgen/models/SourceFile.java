package be.uantwerpen.idlab.cobra.blockgen.models;

/**
 * Created by Thomas on 6/12/2016.
 */
public class SourceFile
{
    protected long id;
    protected String sourceLocation;

    protected SourceFile()
    {
        this.id = -1L;
    }

    public SourceFile(long id, String sourceLocation)
    {
        this.id = id;
        this.sourceLocation = sourceLocation;
    }

    public long getId()
    {
        return this.id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getSourceLocation()
    {
        return this.sourceLocation;
    }

    public void setSourceLocation(String sourceLocation)
    {
        this.sourceLocation = sourceLocation;
    }

    @Override
    public String toString()
    {
        return this.sourceLocation;
    }
}
