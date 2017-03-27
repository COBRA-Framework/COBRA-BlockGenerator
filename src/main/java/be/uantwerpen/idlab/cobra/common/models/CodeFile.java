package be.uantwerpen.idlab.cobra.common.models;

/**
 * Created by Thomas on 22/03/2016.
 */
public class CodeFile extends SourceFile
{
    private String codeStream;
    private String grammar;

    protected CodeFile()
    {
        super();
    }

    public CodeFile(String codeStream, String grammar, String sourceLocation)
    {
        super();

        this.codeStream = codeStream;
        this.grammar = grammar;
    }

    public CodeFile(String codeStream, String grammar, String sourceLocation, long id)
    {
        super(id, sourceLocation);

        this.codeStream = codeStream;
        this.grammar = grammar;
    }

    public CodeFile(String codeStream, String grammar, SourceFile sourceFile)
    {
        super(sourceFile.getId(), sourceFile.getSourceLocation());

        this.codeStream = codeStream;
        this.grammar = grammar;
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

    @Override
    public String toString()
    {
        return this.codeStream;
    }
}
