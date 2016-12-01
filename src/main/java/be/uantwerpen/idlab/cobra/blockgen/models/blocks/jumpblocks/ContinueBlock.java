package be.uantwerpen.idlab.cobra.blockgen.models.blocks.jumpblocks;

import be.uantwerpen.idlab.cobra.blockgen.models.CodeSegment;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.JumpBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.xml.XMLElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 29/11/2016.
 */
public class ContinueBlock extends JumpBlock
{
    protected ContinueBlock()
    {
        super();
    }

    public ContinueBlock(CodeSegment codeSegment)
    {
        super(codeSegment);
    }

    @Override
    public List<XMLElement> getXMLElements()
    {
        List<XMLElement> elements = new ArrayList<XMLElement>();

        return elements;
    }
}
