package be.uantwerpen.idlab.cobra.common.models.blocks;

import be.uantwerpen.idlab.cobra.common.models.CodeSegment;
import be.uantwerpen.idlab.cobra.common.models.xml.XMLObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 12/04/2016.
 */
public abstract class CaseBlock extends BasicBlock implements Block
{
    protected CaseBlock()
    {
        super();
    }

    protected CaseBlock(Long id)
    {
        super(id);
    }

    public CaseBlock(CodeSegment codeSegment)
    {
        super(codeSegment);
    }

    public CaseBlock(boolean ifSelection)
    {
        super();
    }

    @Override
    public String getCodeString()
    {
        String codeString = new String();

        codeString = super.getCodeString();

        return codeString;
    }

    @Override
    public String toString()
    {
        return "[Case block] " + this.codeSegment;
    }

    @Override
    public List<XMLObject> getXMLElements()
    {
        List<XMLObject> elements = new ArrayList<XMLObject>();

        elements.addAll(super.getXMLElements());

        return elements;
    }
}
