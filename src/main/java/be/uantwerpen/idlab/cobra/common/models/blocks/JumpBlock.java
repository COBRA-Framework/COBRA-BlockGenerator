package be.uantwerpen.idlab.cobra.common.models.blocks;

import be.uantwerpen.idlab.cobra.common.models.CodeSegment;
import be.uantwerpen.idlab.cobra.common.models.xml.XMLObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 02/05/2016.
 */
public abstract class JumpBlock extends BasicBlock implements Block
{
    protected JumpBlock()
    {
        super();
    }

    protected JumpBlock(Long id)
    {
        super(id);
    }

    public JumpBlock(CodeSegment codeSegment)
    {
        super(codeSegment);
    }

    @Override
    public String toString()
    {
        return "[Jump block] " + this.codeSegment;
    }

    @Override
    public List<XMLObject> getXMLElements()
    {
        List<XMLObject> elements = new ArrayList<XMLObject>();

        elements.addAll(super.getXMLElements());

        return elements;
    }
}
