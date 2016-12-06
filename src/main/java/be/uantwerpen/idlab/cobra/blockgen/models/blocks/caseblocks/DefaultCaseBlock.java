package be.uantwerpen.idlab.cobra.blockgen.models.blocks.caseblocks;

import be.uantwerpen.idlab.cobra.blockgen.models.CodeSegment;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.CaseBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.xml.XMLObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 1/12/2016.
 */
public class DefaultCaseBlock extends CaseBlock
{
    protected DefaultCaseBlock()
    {
        super();
    }

    public DefaultCaseBlock(CodeSegment codeSegment)
    {
        super(codeSegment);
    }

    @Override
    public List<XMLObject> getXMLElements()
    {
        List<XMLObject> elements = new ArrayList<XMLObject>();

        return elements;
    }
}
