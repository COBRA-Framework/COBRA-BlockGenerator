package be.uantwerpen.idlab.cobra.common.models.blocks.iterationblocks;

import be.uantwerpen.idlab.cobra.common.models.CodeSegment;
import be.uantwerpen.idlab.cobra.common.models.blocks.Block;
import be.uantwerpen.idlab.cobra.common.models.blocks.IterationBlock;

/**
 * Created by Thomas on 29/11/2016.
 */
public class WhileBlock extends IterationBlock
{
    protected WhileBlock()
    {
        super();
    }

    protected WhileBlock(long id)
    {
        super(id);
    }

    public WhileBlock(String iterationCondition, CodeSegment codeSegment)
    {
        super(codeSegment, iterationCondition);
    }

    @Override
    public String getCodeString()
    {
        String codeString = new String();

        codeString = this.codeSegment.toString();

        codeString = codeString.concat("\n{\n");

        for(Block block : this.getChildBlocks())
        {
            for(String subString : block.getCodeString().split("\n"))
            {
                codeString = codeString.concat("\t" + subString + "\n");
            }
        }

        codeString = codeString.concat("}");

        return codeString;
    }

    @Override
    public String toString()
    {
        return "[Iteration block] " + this.codeSegment.toString();
    }
}
