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
        CodeSegment replacementCodeSegment;
        CodeFile codeFile;
        int startIndex, endIndex;

        if(blocks.isEmpty())
        {
            //No blocks to concat
            return null;
        }

        codeFile = blocks.firstElement().getCodeSegment().getCodeFile();

        startIndex = blocks.firstElement().getCodeSegment().getStartIndex();
        endIndex = blocks.lastElement().getCodeSegment().getEndIndex();

        replacementCodeSegment = new CodeSegment(codeFile, startIndex, endIndex);

        //Parse new block reference index
        Iterator<Block> it = blocks.iterator();
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
