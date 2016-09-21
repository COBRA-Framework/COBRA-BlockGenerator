package be.uantwerpen.cobra.blockgen.models.blocks;

import be.uantwerpen.cobra.blockgen.models.CodeSegment;

/**
 * Created by Thomas on 22/03/2016.
 */
public class IterationBlock extends BasicBlock implements Block
{
    private boolean doWhileStatement;

    protected IterationBlock()
    {
        super();

        this.doWhileStatement = false;
    }

    public IterationBlock(CodeSegment codeSegment, boolean doWhileStatement)
    {
        super(codeSegment);

        this.doWhileStatement = doWhileStatement;
    }

    @Override
    public String getCodeString()
    {
        String codeString = new String();

        if(!this.doWhileStatement)
        {
            codeString = this.codeSegment.toString();
        }
        else
        {
            codeString = "do";
        }

        codeString = codeString.concat("\n{\n");

        for(Block block : this.getChildBlocks())
        {
            for(String subString : block.getCodeString().split("\n"))
            {
                codeString = codeString.concat("\t" + subString + "\n");
            }
        }

        codeString = codeString.concat("}");

        if(this.doWhileStatement)
        {
            codeString = codeString.concat(" " + this.codeSegment.toString());
        }

        return codeString;
    }

    @Override
    public String toString()
    {
        if(doWhileStatement)
        {
            return "[Iteration block] do ... " + this.codeSegment.toString();
        }
        else
        {
            return "[Iteration block] " + this.codeSegment.toString();
        }
    }

    public boolean isDoWhileStatement()
    {
        return this.doWhileStatement;
    }
}
