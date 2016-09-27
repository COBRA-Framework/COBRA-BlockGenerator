package be.uantwerpen.cobra.blockgen.blockreduction.rules;

import be.uantwerpen.cobra.blockgen.blockreduction.interfaces.ReductionRule;
import be.uantwerpen.cobra.blockgen.models.CodeFile;
import be.uantwerpen.cobra.blockgen.models.CodeSegment;
import be.uantwerpen.cobra.blockgen.models.blocks.BasicBlock;
import be.uantwerpen.cobra.blockgen.models.blocks.Block;

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

        replacementBlock = new BasicBlock(replacementCodeSegment);

        return replacementBlock;
    }
}
