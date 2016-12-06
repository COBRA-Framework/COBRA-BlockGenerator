package be.uantwerpen.idlab.cobra.blockgen.models.blocks.iterationblocks;

import be.uantwerpen.idlab.cobra.blockgen.models.CodeSegment;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.Block;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.IterationBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.xml.XMLElement;
import be.uantwerpen.idlab.cobra.blockgen.models.xml.XMLObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 29/11/2016.
 */
public class ForBlock extends IterationBlock
{
    private String initStatement;
    private String postStatement;

    protected ForBlock()
    {
        super();
    }

    public ForBlock(String initStatement, String iterationCondition, String postStatement, CodeSegment codeSegment)
    {
        super(codeSegment, iterationCondition);

        this.initStatement = initStatement;
        this.postStatement = postStatement;
    }

    public String getInitStatement()
    {
        return this.initStatement;
    }

    public String getPostStatement()
    {
        return this.postStatement;
    }

    public void setInitStatement(String initStatement)
    {
        this.initStatement = initStatement;
    }

    public void setPostStatement(String postStatement)
    {
        this.postStatement = postStatement;
    }

    @Override
    public String getCodeString()
    {
        String codeString = new String();

        codeString = this.codeSegment.toString();

        codeString = codeString.concat("\n{\n");

        for(Block block : this.getChildBlocks())
        {
            for(String subString : block.getCodeString().split("\n"))
            {
                codeString = codeString.concat("\t" + subString + "\n");
            }
        }

        codeString = codeString.concat("}");

        return codeString;
    }

    @Override
    public String toString()
    {
        return "[Iteration block] " + this.codeSegment.toString();
    }

    public List<XMLObject> getXMLElements()
    {
        List<XMLObject> elements = super.getXMLElements();

        //Element: init statement
        elements.add(new XMLElement("init", this.initStatement));

        //Element: post statement
        elements.add(new XMLElement("post", this.postStatement));

        return elements;
    }
}
