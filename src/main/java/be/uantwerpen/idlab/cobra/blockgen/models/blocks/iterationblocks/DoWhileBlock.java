package be.uantwerpen.idlab.cobra.blockgen.models.blocks.iterationblocks;

import be.uantwerpen.idlab.cobra.blockgen.models.CodeSegment;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.Block;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.IterationBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.xml.XMLElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 29/11/2016.
 */
public class DoWhileBlock extends IterationBlock
{
    protected DoWhileBlock()
    {
        super();
    }

    public DoWhileBlock(String iterationCondition, CodeSegment codeSegment)
    {
        super(codeSegment, iterationCondition);
    }

    @Override
    public String getCodeString()
    {
        String codeString = new String();

        codeString = "do";

        codeString = codeString.concat("\n{\n");

        for(Block block : this.getChildBlocks())
        {
            for(String subString : block.getCodeString().split("\n"))
            {
                codeString = codeString.concat("\t" + subString + "\n");
            }
        }

        codeString = codeString.concat("}");

        codeString = codeString.concat(" " + this.codeSegment.toString());

        return codeString;
    }

    @Override
    public String toString()
    {
        return "[Iteration block] do ... " + this.codeSegment.toString();
    }
}
