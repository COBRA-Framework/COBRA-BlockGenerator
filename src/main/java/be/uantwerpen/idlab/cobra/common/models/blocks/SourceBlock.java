package be.uantwerpen.idlab.cobra.common.models.blocks;

import be.uantwerpen.idlab.cobra.common.models.xml.XMLElement;
import be.uantwerpen.idlab.cobra.common.models.xml.XMLObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 18/03/2016.
 */
public class SourceBlock extends BasicBlock implements Block
{
    private String sourceName;

    protected SourceBlock()
    {
        super();
    }

    protected SourceBlock(Long id)
    {
        super(id);
    }

    public SourceBlock(String sourceName)
    {
        super();

        this.sourceName = sourceName;
    }

    public String getName()
    {
        return this.sourceName;
    }

    @Override
    public List<XMLObject> getXMLElements()
    {
        List<XMLObject> elements = new ArrayList<XMLObject>();

        //Element: program name
        elements.add(new XMLElement("name", this.sourceName));

        return elements;
    }

    @Override
    public String toString()
    {
        return "[Source Block] " + this.sourceName;
    }
}
