package be.uantwerpen.cobra.blockgen.blockreduction.rules;

import be.uantwerpen.cobra.blockgen.blockreduction.interfaces.ReductionRule;
import be.uantwerpen.cobra.blockgen.models.blocks.BasicBlock;
import be.uantwerpen.cobra.blockgen.models.blocks.Block;

import java.util.Iterator;
import java.util.Vector;

/**
 * Created by Thomas on 21/04/2016.
 */
public class BasicBlockReductionRule extends BasicReductionRule implements ReductionRule
{
    //To do: Code segment layout fix!

    @Override
    public Block applyRule(Block model)
    {
        Vector<Block> newChildBlocks = new Vector<Block>();
        Vector<Block> replaceBlocks = new Vector<Block>();

        Iterator<Block> itBlock = model.getChildBlocks().iterator();
        while(itBlock.hasNext())
        {
            Block block = itBlock.next();

            if(block.getClass() == BasicBlock.class && block.getNumOfChildren() == 0)
            {
                //Add basic block to temporary vector
                replaceBlocks.add(block);

                //Remove basic block from child blocks
                itBlock.remove();
            }
            else
            {
                //Replace basic blocks if present
                if(!replaceBlocks.isEmpty())
                {
                    //Add replacement block to child blocks
                    newChildBlocks.add(concatBlocks(replaceBlocks));

                    //Clear temporary vector
                    replaceBlocks.clear();
                }

                //Add not replaced block to child blocks
                newChildBlocks.add(block);
            }
        }

        //Replace basic blocks if present
        if(!replaceBlocks.isEmpty())
        {
            //Add replacement block to child blocks
            newChildBlocks.add(concatBlocks(replaceBlocks));

            //Clear temporary vector
            replaceBlocks.clear();
        }

        //Replace old child blocks with new
        model.getChildBlocks().clear();

        for(Block block : newChildBlocks)
        {
            model.addChildBlock(block);
        }

        return model;
    }
}
