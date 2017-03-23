package be.uantwerpen.idlab.cobra.blockgen.blockreduction.interfaces;

import be.uantwerpen.idlab.cobra.common.models.blocks.Block;

/**
 * Created by Thomas on 21/04/2016.
 */
public interface ReductionRule
{
    Block applyRule(Block model);
}
