package be.uantwerpen.idlab.cobra.blockgen.tools.loopboundanalysis.analysers;

import be.uantwerpen.idlab.cobra.common.models.blocks.Block;
import be.uantwerpen.idlab.cobra.common.models.blocks.IterationBlock;
import be.uantwerpen.idlab.cobra.common.tools.terminal.Terminal;

public class PragmaParser extends AnnotationParser
{
    @Override
    public Block parseLoopbounds(Block block) throws Exception
    {
        if(!IterationBlock.class.isAssignableFrom(block.getClass()))
        {
            Terminal.printTerminalWarning("Pragma loop bound detection is only supported for iteration-type blocks.");
            Terminal.printTerminalWarning("Block (id: " + block.getId() + ") of type: " + block.getClass().getSimpleName() + " will be skipped!");

            return block;
        }

        IterationBlock iterationBlock = (IterationBlock)block;
        int prefixEndIndex = iterationBlock.getCodeSegment().getStartIndex();
        int prefixStartIndex = 0;

        //Get previous block
        Block previousBlock = getPreviousBlockWithCodeSegment(iterationBlock);

        if(previousBlock == null)
        {
            //First block type in file...
            prefixStartIndex = 0;
        }
        else
        {
            prefixStartIndex = previousBlock.getCodeSegment().getEndIndex() + 1;
        }

        String codePrefix = "";

        try
        {
            codePrefix = iterationBlock.getCodeSegment().getCodeFile().getCodeStream().substring(prefixStartIndex, prefixEndIndex);

            //Parse loopbound PRAGMA
            if(codePrefix.toLowerCase().contains("_pragma"))
            {
                String pragmaString = codePrefix.toLowerCase().split("_pragma")[codePrefix.toLowerCase().split("_pragma").length - 1].trim();

                //Get content
                pragmaString = pragmaString.split("\"", 2)[1];
                pragmaString = pragmaString.split("\"",2)[0];

                if(pragmaString.contains("loopbound") && pragmaString.contains("max"))
                {
                    String maxLoopbound = pragmaString.split("max", 2)[1];

                    try
                    {
                        int loopbound = Integer.parseInt(maxLoopbound.trim());

                        iterationBlock.setLoopbound(loopbound);
                    }
                    catch(Exception e)
                    {
                        Terminal.printTerminalWarning("Could not parse loop bound pragma: '" + pragmaString + "' for block with id: " + block.getId() + "!");
                    }
                }
            }
        }
        catch(Exception e)
        {
            Terminal.printTerminalWarning("Could not detect or parse pragma! Skipping block with id: " + iterationBlock.getId() + " ...");
        }

        return block;
    }

    private Block getPreviousBlockWithCodeSegment(Block block)
    {
        Block previousBlock;

        if(block.getParentBlock() == null)
        {
            //First block in tree...
            return null;
        }
        else
        {
            int childIndex = block.getParentBlock().getChildBlocks().indexOf(block);

            if(childIndex == 0)
            {
                //First block of children --> take parent
                previousBlock = block.getParentBlock();
            }
            else
            {
                //Get last leaf of previous sibling
                Block previousSibling = block.getParentBlock().getChildBlock(childIndex - 1);

                previousBlock = previousSibling.getLeafs().lastElement();
            }
        }

        //Check if the previous block has a code segment
        if(previousBlock.getCodeSegment() == null)
        {
            //Recursive search to previous block with a code segment
            return getPreviousBlockWithCodeSegment(previousBlock);
        }
        else
        {
            return previousBlock;
        }
    }
}
