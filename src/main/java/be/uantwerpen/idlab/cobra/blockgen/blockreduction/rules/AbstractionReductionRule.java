package be.uantwerpen.idlab.cobra.blockgen.blockreduction.rules;

import be.uantwerpen.idlab.cobra.blockgen.blockreduction.interfaces.ReductionRule;
import be.uantwerpen.idlab.cobra.common.models.BlockReference;
import be.uantwerpen.idlab.cobra.common.models.CodeFile;
import be.uantwerpen.idlab.cobra.common.models.CodeSegment;
import be.uantwerpen.idlab.cobra.common.models.blocks.BasicBlock;
import be.uantwerpen.idlab.cobra.common.models.blocks.Block;

import java.util.Iterator;

/**
 * Created by Thomas on 09/05/2016.
 */
public class AbstractionReductionRule extends BasicReductionRule implements ReductionRule
{
    @Override
    public Block applyRule(Block model)
    {
        Block abstractedBlock;
        CodeSegment replacementCodeSegment;
        CodeFile codeFile;
        int startIndex, endIndex;

        if(model.getNumOfChildren() == 0)
        {
            //Nothing to abstract
            return model;
        }

        codeFile = model.getChildBlocks().firstElement().getCodeSegment().getCodeFile();

        startIndex = model.getChildBlocks().firstElement().getCodeSegment().getStartIndex();
        endIndex = model.getLeafs().lastElement().getCodeSegment().getEndIndex();

        replacementCodeSegment = new CodeSegment(codeFile, startIndex, endIndex);

        //Parse new block reference index
        Iterator<Block> it = model.getDescendantBlocks().iterator();
        Block block = it.next();

        String refString = block.getRef().toString();

        while(it.hasNext())
        {
            block = it.next();

            refString += "," + block.getRef().toString();
        }

        BlockReference ref = new BlockReference(refString);

        abstractedBlock = new BasicBlock(replacementCodeSegment);

        abstractedBlock.setRef(ref);

        return abstractedBlock;
    }
}
