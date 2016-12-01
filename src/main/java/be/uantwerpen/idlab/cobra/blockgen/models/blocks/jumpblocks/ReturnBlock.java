package be.uantwerpen.idlab.cobra.blockgen.models.blocks.jumpblocks;

import be.uantwerpen.idlab.cobra.blockgen.models.CodeSegment;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.JumpBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.xml.XMLElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 29/11/2016.
 */
public class ReturnBlock extends JumpBlock
{
    private String returnStatement;

    protected ReturnBlock()
    {
        super();
    }

    public ReturnBlock(String returnStatement, CodeSegment codeSegment)
    {
        super(codeSegment);

        this.returnStatement = returnStatement;
    }

    public String getReturnStatement()
    {
        return this.returnStatement;
    }

    public void setReturnStatement(String returnStatement)
    {
        this.returnStatement = returnStatement;
    }

    @Override
    public List<XMLElement> getXMLElements()
    {
        List<XMLElement> elements = new ArrayList<XMLElement>();

        //Element: return value
        elements.add(new XMLElement("statement", this.returnStatement));

        return elements;
    }
}
