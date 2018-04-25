package be.uantwerpen.idlab.cobra.blockgen.blockreduction.rules;

import be.uantwerpen.idlab.cobra.blockgen.blockreduction.interfaces.ReductionRule;
import be.uantwerpen.idlab.cobra.common.models.blocks.AbstractionBlock;
import be.uantwerpen.idlab.cobra.common.models.blocks.BasicBlock;
import be.uantwerpen.idlab.cobra.common.models.blocks.Block;

import java.util.Vector;

/**
 * Created by Thomas on 09/05/2016.
 */
public class AbstractionReductionRule extends BasicReductionRule implements ReductionRule
{
    @Override
    public Block applyRule(Block model) throws Exception
    {
        if(model.getNumOfChildren() == 0)
        {
            //Nothing to abstract
            return model;
        }

        Block reducedChildBlock = concatBlocks(model.getDescendantBlocks());

        if(reducedChildBlock.getCodeSegment() == null)
        {
            //Pure abstract rule block detected, ignore abstraction
            return model;
        }

        AbstractionBlock abstractedBlock = new AbstractionBlock((BasicBlock)reducedChildBlock);

        //Make copy of vector
        Vector<Block> abstractedBlocks = new Vector<Block>();
        for(Block abstractedBlockIt : model.getChildBlocks())
        {
            abstractedBlocks.add(abstractedBlockIt);
        }

        abstractedBlock.setAbstractedBlocks(abstractedBlocks);

        model.removeChildBlocks();
        model.addChildBlock(abstractedBlock);

        return model;
    }
}
