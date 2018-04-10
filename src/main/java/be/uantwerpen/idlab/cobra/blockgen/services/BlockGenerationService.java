package be.uantwerpen.idlab.cobra.blockgen.services;

import be.uantwerpen.idlab.cobra.blockgen.blockreduction.interfaces.ReductionRule;
import be.uantwerpen.idlab.cobra.blockgen.blockreduction.rules.AbstractionReductionRule;
import be.uantwerpen.idlab.cobra.blockgen.blockreduction.rules.BasicBlockReductionRule;
import be.uantwerpen.idlab.cobra.common.models.Grammar;
import be.uantwerpen.idlab.cobra.common.models.SourceFile;
import be.uantwerpen.idlab.cobra.common.models.blocks.Block;
import be.uantwerpen.idlab.cobra.common.models.blocks.SourceBlock;
import be.uantwerpen.idlab.cobra.common.models.blocks.SelectionBlock;
import be.uantwerpen.idlab.cobra.blockgen.tools.antlr.Antlr;
import be.uantwerpen.idlab.cobra.blockgen.tools.interfaces.CodeParser;
import be.uantwerpen.idlab.cobra.common.tools.terminal.Terminal;

import java.util.Vector;

/**
 * Created by Thomas on 6/12/2016.
 */
public class BlockGenerationService
{
    public static SourceBlock parseProgramFile(SourceFile file, int abstractionDepth, Grammar grammar) throws Exception
    {
        CodeParser codeParser = new Antlr();

        Vector<Block> blocks = null;

        //Parse file and generate block model
        blocks = codeParser.parseCodeFile(file, grammar);

        //Apply abstraction depth reduction
        if(abstractionDepth >= 0)
        {
            for(Block methodBlock : blocks)
            {
                applyAbstractionRuleRecursive(methodBlock, abstractionDepth);
            }
        }

        //Apply basic block reduction
        for(Block methodBlock : blocks)
        {
            applyReductionRuleRecursive(methodBlock);
        }

        Terminal.printTerminal("*****Block view*****");
        for(Block block : blocks)
        {
            Terminal.printTerminal(block.toStringRecursive() + "\n");
        }

        Terminal.printTerminal("*****Code view*****");
        for(Block block : blocks)
        {
            Terminal.printTerminal(block.getCodeString() + "\n");
        }

        SourceBlock programBlock = new SourceBlock(file.toString());

        for(Block methodBlock : blocks)
        {
            programBlock.addChildBlock(methodBlock);
        }

        return programBlock;
    }

    private static void applyReductionRuleRecursive(Block block)
    {
        ReductionRule blockReduction = new BasicBlockReductionRule();

        blockReduction.applyRule(block);

        for(Block childBlock : block.getChildBlocks())
        {
            applyReductionRuleRecursive(childBlock);
        }
    }

    private static void applyAbstractionRuleRecursive(Block block, int abstractionDepth)
    {
        ReductionRule abstractionRule = new AbstractionReductionRule();

        if(abstractionDepth <= 0 && !SelectionBlock.class.isAssignableFrom(block.getClass()))
        {
            abstractionRule.applyRule(block);
        }
        else
        {
            for(Block childBlock : block.getChildBlocks())
            {
                if(SelectionBlock.class.isAssignableFrom(childBlock.getClass()))
                {
                    //Case statements are the same abstraction level
                    applyAbstractionRuleRecursive(childBlock, abstractionDepth);
                }
                else
                {
                    applyAbstractionRuleRecursive(childBlock, abstractionDepth - 1);
                }
            }
        }
    }
}
