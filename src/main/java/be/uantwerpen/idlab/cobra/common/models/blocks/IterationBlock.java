package be.uantwerpen.idlab.cobra.common.models.blocks;

import be.uantwerpen.idlab.cobra.common.models.CodeSegment;
import be.uantwerpen.idlab.cobra.common.models.xml.XMLElement;
import be.uantwerpen.idlab.cobra.common.models.xml.XMLObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 22/03/2016.
 */
public abstract class IterationBlock extends BasicBlock implements Block
{
    protected String iterationCondition;
    protected long loopbound;

    protected IterationBlock()
    {
        super();
    }

    protected IterationBlock(long id)
    {
        super(id);
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

    public long getLoopbound()
    {
        return this.loopbound;
    }

    public void setLoopbound(long loopbound)
    {
        this.loopbound = loopbound;
    }

    @Override
    public List<XMLObject> getXMLElements()
    {
        List<XMLObject> elements = new ArrayList<XMLObject>();

        //Element: condition string
        elements.add(new XMLElement("iteration_condition", this.iterationCondition));

        //Element: max. loopbound
        elements.add(new XMLElement("loopbound", this.loopbound));

        elements.addAll(super.getXMLElements());

        return elements;
    }
}
