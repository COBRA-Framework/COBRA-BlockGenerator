package be.uantwerpen.idlab.cobra.blockgen.models.blocks;

import be.uantwerpen.idlab.cobra.blockgen.models.CodeSegment;
import be.uantwerpen.idlab.cobra.blockgen.models.xml.XMLElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 22/03/2016.
 */
public class MethodBlock extends BasicBlock implements Block
{
    private String methodName;
    private String parameters;

    protected MethodBlock()
    {
        super();
    }

    public MethodBlock(String methodName, CodeSegment codeSegment)
    {
        super(codeSegment);

        this.methodName = methodName;
    }

    public String getMethodName()
    {
        return this.methodName;
    }

    public void setMethodName(String name)
    {
        this.methodName = name;
    }

    @Override
    public String toString()
    {
        return "[Method block] " + this.codeSegment.toString();
    }

    @Override
    public List<XMLElement> getXMLElements()
    {
        List<XMLElement> elements = new ArrayList<XMLElement>();

        //Element: method name
        elements.add(new XMLElement("name", this.codeSegment));

        return elements;
    }
}
