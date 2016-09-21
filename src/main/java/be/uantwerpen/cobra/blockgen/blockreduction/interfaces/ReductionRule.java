package be.uantwerpen.cobra.blockgen.blockreduction.interfaces;

import be.uantwerpen.cobra.blockgen.models.blocks.Block;

/**
 * Created by Thomas on 21/04/2016.
 */
public interface ReductionRule
{
    Block applyRule(Block model);
}
