package be.uantwerpen.idlab.cobra.blockgen.models.blocks;

import be.uantwerpen.idlab.cobra.blockgen.models.CodeSegment;
import be.uantwerpen.idlab.cobra.blockgen.models.xml.XMLElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 22/03/2016.
 */
public abstract class IterationBlock extends BasicBlock implements Block
{
    protected String iterationCondition;

    protected IterationBlock()
    {
        super();
    }

    public IterationBlock(CodeSegment codeSegment, String iterationCondition)
    {
        super(codeSegment);

        this.iterationCondition = iterationCondition;
    }

    public String getIterationCondition()
    {
        return this.iterationCondition;
    }

    public void setIterationCondition(String iterationCondition)
    {
        this.iterationCondition = iterationCondition;
    }

    @Override
    public List<XMLElement> getXMLElements()
    {
        List<XMLElement> elements = new ArrayList<XMLElement>();

        //Element: condition string
        elements.add(new XMLElement("condition", this.iterationCondition));

        return elements;
    }
}
