package be.uantwerpen.cobra.blockgen.models;

/**
 * Created by Thomas on 22/03/2016.
 */
public class CodeFile
{
    private String codeStream;
    private String grammar;
    private String sourceLocation;

    protected CodeFile()
    {

    }

    public CodeFile(String codeStream, String grammar, String sourceLocation)
    {
        this.codeStream = codeStream;
        this.grammar = grammar;
        this.sourceLocation = sourceLocation;
    }

    public String getCodeStream()
    {
        return this.codeStream;
    }

    public void setCodeStream(String codeStream)
    {
        this.codeStream = codeStream;
    }

    public String getGrammar()
    {
        return this.grammar;
    }

    public void setGrammar(String grammar)
    {
        this.grammar = grammar;
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
        return this.codeStream;
    }
}
