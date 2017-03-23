package be.uantwerpen.idlab.cobra.common.models.blocks.jumpblocks;

import be.uantwerpen.idlab.cobra.common.models.CodeSegment;
import be.uantwerpen.idlab.cobra.common.models.blocks.JumpBlock;
import be.uantwerpen.idlab.cobra.common.models.xml.XMLElement;
import be.uantwerpen.idlab.cobra.common.models.xml.XMLObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 23/01/2017.
 */
//TODO: Not implemented yet!
public class CallBlock extends JumpBlock
{
    private String calledSection;

    protected CallBlock()
    {
        super();
    }

    public CallBlock(String calledSection, CodeSegment codeSegment)
    {
        super(codeSegment);

        this.calledSection = calledSection;
    }

    public String getCalledSection()
    {
        return this.calledSection;
    }

    public void setCalledSection(String calledSection)
    {
        this.calledSection = calledSection;
    }

    @Override
    public List<XMLObject> getXMLElements()
    {
        List<XMLObject> elements = new ArrayList<XMLObject>();

        //Element: called section value
        elements.add(new XMLElement("called_section", this.calledSection));
        elements.addAll(super.getXMLElements());

        return elements;
    }
}
