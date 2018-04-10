package be.uantwerpen.idlab.cobra.common.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 2/12/2016.
 */
public class ProjectConfig
{
    private String name;
    private String version;
    private List<SourceFile> sourceFiles;
    private Grammar grammar;
    private int abstractionDepth;

    protected ProjectConfig()
    {
        this.sourceFiles = new ArrayList<SourceFile>();
    }

    public ProjectConfig(String name, String version, Grammar grammar)
    {
        this(name, version, grammar, -1);
    }

    public ProjectConfig(String name, String version, Grammar grammar, int abstractionDepth)
    {
        this();

        this.name = name;
        this.version = version;
        this.grammar = grammar;
        this.abstractionDepth = abstractionDepth;
    }

    public ProjectConfig(String name, String version, Grammar grammar, SourceFile sourceFile)
    {
        this(name, version, grammar);

        this.sourceFiles.add(sourceFile);
    }

    public ProjectConfig(String name, String version, Grammar grammar, int abstractionDepth, SourceFile sourceFile)
    {
        this(name, version, grammar, abstractionDepth);

        this.sourceFiles.add(sourceFile);
    }

    public ProjectConfig(String name, String version, Grammar grammar, List<SourceFile> sourceFiles)
    {
        this(name, version, grammar);

        this.sourceFiles = sourceFiles;
    }

    public ProjectConfig(String name, String version, Grammar grammar, int abstractionDepth, List<SourceFile> sourceFiles)
    {
        this(name, version, grammar, abstractionDepth);

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

    public Grammar getGrammar()
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

    public void setGrammar(Grammar grammar)
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

    public int getAbstractionDepth()
    {
        return this.abstractionDepth;
    }

    public void setAbstractionDepth(int abstractionDepth)
    {
        this.abstractionDepth = abstractionDepth;
    }

    @Override
    public String toString()
    {
        String string = "Project: " + this.getName() + "\nVersion: " + this.getVersion() + "\nGrammar: " + this.getGrammar().name() + (this.abstractionDepth >= 0 ? ("\nAbstraction depth: " + this.abstractionDepth) : "") + "\nSources:\n";

        for(SourceFile sourceFile : this.getSourceFiles())
        {
            string = string.concat("\t" + sourceFile + "\n");
        }

        return string.trim();
    }
}
