package be.uantwerpen.idlab.cobra.common.models.blocks.jumpblocks;

import be.uantwerpen.idlab.cobra.common.models.CodeSegment;
import be.uantwerpen.idlab.cobra.common.models.blocks.JumpBlock;
import be.uantwerpen.idlab.cobra.common.models.xml.XMLElement;
import be.uantwerpen.idlab.cobra.common.models.xml.XMLObject;

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
    public List<XMLObject> getXMLElements()
    {
        List<XMLObject> elements = new ArrayList<XMLObject>();

        //Element: return value
        elements.add(new XMLElement("return_statement", this.returnStatement));
        elements.addAll(super.getXMLElements());

        return elements;
    }
}
