package be.uantwerpen.idlab.cobra.blockgen.models.blocks.jumpblocks;

import be.uantwerpen.idlab.cobra.blockgen.models.CodeSegment;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.JumpBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.xml.XMLElement;
import be.uantwerpen.idlab.cobra.blockgen.models.xml.XMLObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 29/11/2016.
 */
public class GotoBlock extends JumpBlock
{
    private String jumpLabel;

    protected GotoBlock()
    {
        super();
    }

    public GotoBlock(String jumpLabel, CodeSegment codeSegment)
    {
        super(codeSegment);

        this.jumpLabel = jumpLabel;
    }

    public String getJumpLabel()
    {
        return this.jumpLabel;
    }

    public void setJumpLabel(String jumpLabel)
    {
        this.jumpLabel = jumpLabel;
    }

    @Override
    public List<XMLObject> getXMLElements()
    {
        List<XMLObject> elements = new ArrayList<XMLObject>();

        //Element: label value
        elements.add(new XMLElement("label", this.jumpLabel));

        return elements;
    }
}
