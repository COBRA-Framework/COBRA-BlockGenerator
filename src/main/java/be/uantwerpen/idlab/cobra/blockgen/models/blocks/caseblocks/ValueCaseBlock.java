package be.uantwerpen.idlab.cobra.blockgen.models.blocks.caseblocks;

import be.uantwerpen.idlab.cobra.blockgen.models.CodeSegment;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.CaseBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.xml.XMLElement;
import be.uantwerpen.idlab.cobra.blockgen.models.xml.XMLObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 29/11/2016.
 */
public class ValueCaseBlock extends CaseBlock
{
    private String labelValue;

    protected ValueCaseBlock()
    {
        super();
    }

    public ValueCaseBlock(String value, CodeSegment codeSegment)
    {
        super(codeSegment);

        this.labelValue = value;
    }

    public String getLabelValue()
    {
        return this.labelValue;
    }

    public void setLabelValue(String value)
    {
        this.labelValue = value;
    }

    @Override
    public List<XMLObject> getXMLElements()
    {
        List<XMLObject> elements = new ArrayList<XMLObject>();

        //Element: label value
        elements.add(new XMLElement("label", this.labelValue));

        return elements;
    }
}
