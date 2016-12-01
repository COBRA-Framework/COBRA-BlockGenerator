package be.uantwerpen.idlab.cobra.blockgen.models.blocks;

import be.uantwerpen.idlab.cobra.blockgen.models.xml.XMLElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 18/03/2016.
 */
public class ProgramBlock extends BasicBlock implements Block
{
    private String programName;

    protected ProgramBlock()
    {
        super();
    }

    public ProgramBlock(String programName)
    {
        super();

        this.programName = programName;
    }

    public String getProgramName()
    {
        return this.programName;
    }

    @Override
    public List<XMLElement> getXMLElements()
    {
        List<XMLElement> elements = new ArrayList<XMLElement>();

        //Element: program name
        elements.add(new XMLElement("name", this.programName));

        return elements;
    }
}
