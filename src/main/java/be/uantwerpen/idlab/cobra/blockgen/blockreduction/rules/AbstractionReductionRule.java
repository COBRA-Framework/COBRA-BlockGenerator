package be.uantwerpen.idlab.cobra.blockgen.blockreduction.rules;

import be.uantwerpen.idlab.cobra.blockgen.blockreduction.interfaces.ReductionRule;
import be.uantwerpen.idlab.cobra.common.models.CodeFile;
import be.uantwerpen.idlab.cobra.common.models.CodeSegment;
import be.uantwerpen.idlab.cobra.common.models.blocks.BasicBlock;
import be.uantwerpen.idlab.cobra.common.models.blocks.Block;

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

        abstractedBlock = new BasicBlock(replacementCodeSegment);

        return abstractedBlock;
    }
}
