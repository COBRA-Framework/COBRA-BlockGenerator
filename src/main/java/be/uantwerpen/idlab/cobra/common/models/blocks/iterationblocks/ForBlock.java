package be.uantwerpen.idlab.cobra.common.models.blocks.iterationblocks;

import be.uantwerpen.idlab.cobra.common.models.CodeSegment;
import be.uantwerpen.idlab.cobra.common.models.blocks.Block;
import be.uantwerpen.idlab.cobra.common.models.blocks.IterationBlock;
import be.uantwerpen.idlab.cobra.common.models.xml.XMLElement;
import be.uantwerpen.idlab.cobra.common.models.xml.XMLObject;

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

    protected ForBlock(long id)
    {
        super(id);
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
        List<XMLObject> elements = new ArrayList<XMLObject>();

        //Element: init statement
        elements.add(new XMLElement("init_statement", this.initStatement));

        //Element: post statement
        elements.add(new XMLElement("post_statement", this.postStatement));

        elements.addAll(super.getXMLElements());

        return elements;
    }
}
