package be.uantwerpen.idlab.cobra.blockgen.models;

import be.uantwerpen.idlab.cobra.blockgen.models.xml.XMLElement;
import be.uantwerpen.idlab.cobra.blockgen.tools.interfaces.CodeParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 2/12/2016.
 */
public class ProjectConfig extends XMLElement
{
    private String name;
    private String version;
    private List<SourceFile> sourceFiles;
    private CodeParser.Grammar grammar;

    protected ProjectConfig()
    {
        this.sourceFiles = new ArrayList<SourceFile>();
    }

    public ProjectConfig(String name, String version, CodeParser.Grammar grammar)
    {
        this();

        this.name = name;
        this.version = version;
        this.grammar = grammar;
    }

    public ProjectConfig(String name, String version, CodeParser.Grammar grammar, SourceFile sourceFile)
    {
        this(name, version, grammar);

        this.sourceFiles.add(sourceFile);
    }

    public ProjectConfig(String name, String version, CodeParser.Grammar grammar, List<SourceFile> sourceFiles)
    {
        this(name, version, grammar);

        this.sourceFiles = sourceFiles;
    }

    public String getName()
    {
        return this.name;
    }

    public String getVersion()
    {
        return this.version;
    }

    public CodeParser.Grammar getGrammar()
    {
        return this.grammar;
    }

    public List<SourceFile> getSourceFiles()
    {
        return this.sourceFiles;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public void setGrammar(CodeParser.Grammar grammar)
    {
        this.grammar = grammar;
    }

    public void setSourceFiles(List<SourceFile> sourceFiles)
    {
        this.sourceFiles = sourceFiles;
    }

    public void addSourceFile(SourceFile sourceFile)
    {
        this.sourceFiles.add(sourceFile);
    }

    @Override
    public String toString()
    {
        String string = "Project: " + this.getName() + "\nVersion: " + this.getVersion() + "\nGrammar: " + this.getGrammar().name() + "\nSources:\n";

        for(SourceFile sourceFile : this.getSourceFiles())
        {
            string = string.concat("\t" + sourceFile + "\n");
        }

        return string;
    }
}
