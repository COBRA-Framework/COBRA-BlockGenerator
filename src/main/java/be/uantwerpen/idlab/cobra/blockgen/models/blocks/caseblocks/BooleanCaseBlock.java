package be.uantwerpen.idlab.cobra.blockgen.models.blocks.caseblocks;

import be.uantwerpen.idlab.cobra.blockgen.models.blocks.CaseBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.xml.XMLElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 29/11/2016.
 */
public class BooleanCaseBlock extends CaseBlock
{
    private boolean booleanValue;

    protected BooleanCaseBlock()
    {
        super();
    }

    public BooleanCaseBlock(boolean value)
    {
        super();

        this.booleanValue = value;
    }

    public boolean getBooleanValue()
    {
        return this.booleanValue;
    }

    public void setBooleanValue(boolean value)
    {
        this.booleanValue = value;
    }

    @Override
    public List<XMLElement> getXMLElements()
    {
        List<XMLElement> elements = new ArrayList<XMLElement>();

        //Element: boolean value
        elements.add(new XMLElement("value", this.booleanValue));

        return elements;
    }
}
