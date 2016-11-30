package be.uantwerpen.idlab.cobra.blockgen.models.blocks;

import be.uantwerpen.idlab.cobra.blockgen.models.CodeSegment;

/**
 * Created by Thomas on 22/03/2016.
 */
public class MethodBlock extends BasicBlock implements Block
{
    private String methodName;

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
}
