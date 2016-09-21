package be.uantwerpen.cobra.blockgen.blockreduction.rules;

import be.uantwerpen.cobra.blockgen.blockreduction.interfaces.ReductionRule;
import be.uantwerpen.cobra.blockgen.models.blocks.Block;

import java.util.Vector;

/**
 * Created by Thomas on 21/04/2016.
 */
public abstract class BasicReductionRule implements ReductionRule
{
    protected Block concatBlocks(Vector<Block> blocks)
    {
        return null;
    }
}
