package be.uantwerpen.idlab.cobra.blockgen.blockreduction.rules;

import be.uantwerpen.idlab.cobra.blockgen.blockreduction.interfaces.ReductionRule;
import be.uantwerpen.idlab.cobra.common.models.blocks.Block;

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

        model.removeChildBlocks();
        model.addChildBlock(reducedChildBlock);

        return model;
    }
}
