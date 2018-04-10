package be.uantwerpen.idlab.cobra.blockgen.blockreduction.rules;

import be.uantwerpen.idlab.cobra.blockgen.blockreduction.interfaces.ReductionRule;
import be.uantwerpen.idlab.cobra.common.models.BlockReference;
import be.uantwerpen.idlab.cobra.common.models.CodeFile;
import be.uantwerpen.idlab.cobra.common.models.CodeSegment;
import be.uantwerpen.idlab.cobra.common.models.blocks.BasicBlock;
import be.uantwerpen.idlab.cobra.common.models.blocks.Block;

import java.util.Iterator;
import java.util.Vector;

/**
 * Created by Thomas on 21/04/2016.
 */
public abstract class BasicReductionRule implements ReductionRule
{
    protected Block concatBlocks(Vector<Block> blocks)
    {
        BasicBlock replacementBlock;
        CodeSegment replacementCodeSegment = null;
        CodeFile codeFile;
        int startIndex, endIndex;

        if(blocks.isEmpty())
        {
            //No blocks to concat
            return null;
        }

        CodeSegment firstChildCodeSegment = null;
        CodeSegment lastChildCodeSegment = null;
        Iterator<Block> it = blocks.iterator();

        while(it.hasNext() && firstChildCodeSegment == null)
        {
            Block childBlock = it.next();

            firstChildCodeSegment = childBlock.getCodeSegment();
        }

        if(firstChildCodeSegment != null)
        {
            codeFile = firstChildCodeSegment.getCodeFile();

            while(it.hasNext())
            {
                Block childBlock = it.next();

                if(childBlock.getCodeSegment() != null)
                {
                    lastChildCodeSegment = childBlock.getCodeSegment();
                }
            }

            if(lastChildCodeSegment == null)
            {
                lastChildCodeSegment = firstChildCodeSegment;
            }

            startIndex = firstChildCodeSegment.getStartIndex();
            endIndex = lastChildCodeSegment.getEndIndex();

            replacementCodeSegment = new CodeSegment(codeFile, startIndex, endIndex);
        }

        //Parse new block reference index
        it = blocks.iterator();
        Block block = it.next();

        String refString = block.getRef().toString();

        while(it.hasNext())
        {
            block = it.next();

            refString += "," + block.getRef().toString();
        }

        BlockReference ref = new BlockReference(refString);

        replacementBlock = new BasicBlock(replacementCodeSegment);

        replacementBlock.setRef(ref);

        return replacementBlock;
    }
}
