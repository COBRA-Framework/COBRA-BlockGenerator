package be.uantwerpen.idlab.cobra.blockgen.tools.loopboundanalysis;

import be.uantwerpen.idlab.cobra.blockgen.tools.loopboundanalysis.analysers.AnnotationParser;
import be.uantwerpen.idlab.cobra.blockgen.tools.loopboundanalysis.analysers.PragmaParser;
import be.uantwerpen.idlab.cobra.common.models.Grammar;
import be.uantwerpen.idlab.cobra.common.models.blocks.AbstractionBlock;
import be.uantwerpen.idlab.cobra.common.models.blocks.Block;
import be.uantwerpen.idlab.cobra.common.models.blocks.IterationBlock;

import java.util.Vector;

public class LoopboundAnalyser
{
    public static Block analyseBlock(Block block, Grammar grammar) throws Exception
    {
        Vector<Block> analysedBlocks = new Vector<Block>();
        analysedBlocks.add(block);
        analysedBlocks.addAll(block.getDescendantBlocks());

        for(Block analysedBlock : analysedBlocks)
        {
            if(IterationBlock.class.isAssignableFrom(analysedBlock.getClass()))
            {
                //First try to parse annotations
                detectAnnotations(analysedBlock, grammar);

                //Add other analysis techniques...
            }
            else if(AbstractionBlock.class.isAssignableFrom(analysedBlock.getClass()))
            {
                //Get abstracted blocks
                for(Block abstractedBlock : ((AbstractionBlock)analysedBlock).getAbstractedBlocks())
                {
                    analyseBlock(abstractedBlock, grammar);
                }
            }
        }

        return block;
    }

    public static Block detectAnnotations(Block block, Grammar grammar) throws Exception
    {
        AnnotationParser parser;

        switch(grammar)
        {
            case C:
                parser = new PragmaParser();
                break;
            default:
                throw new Exception("Could not initialize loop bound annotation parser for grammar (" + grammar + "). Grammar unknown!");
        }

        return parser.parseLoopbounds(block);
    }
}
