package be.uantwerpen.idlab.cobra.blockgen.blockreduction.rules;

import be.uantwerpen.idlab.cobra.blockgen.blockreduction.interfaces.ReductionRule;
import be.uantwerpen.idlab.cobra.blockgen.models.CodeFile;
import be.uantwerpen.idlab.cobra.blockgen.models.CodeSegment;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.BasicBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.Block;

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
