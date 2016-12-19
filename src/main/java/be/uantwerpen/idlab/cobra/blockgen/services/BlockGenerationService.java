package be.uantwerpen.idlab.cobra.blockgen.services;

import be.uantwerpen.idlab.cobra.blockgen.blockreduction.interfaces.ReductionRule;
import be.uantwerpen.idlab.cobra.blockgen.blockreduction.rules.AbstractionReductionRule;
import be.uantwerpen.idlab.cobra.blockgen.blockreduction.rules.BasicBlockReductionRule;
import be.uantwerpen.idlab.cobra.blockgen.models.Grammar;
import be.uantwerpen.idlab.cobra.blockgen.models.SourceFile;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.Block;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.SourceBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.SelectionBlock;
import be.uantwerpen.idlab.cobra.blockgen.tools.antlr.Antlr;
import be.uantwerpen.idlab.cobra.blockgen.tools.interfaces.CodeParser;
import be.uantwerpen.idlab.cobra.blockgen.tools.terminal.Terminal;

import java.io.File;
import java.util.Vector;

/**
 * Created by Thomas on 6/12/2016.
 */
public class BlockGenerationService
{
    public static SourceBlock parseProgramFile(SourceFile file, String exportLocation, Grammar grammar) throws Exception
    {
        CodeParser codeParser = new Antlr();

        Vector<Block> blocks = null;

        File exportFolder = new File(exportLocation);
        exportFolder.mkdirs();

        //Parse file and generate block model
        blocks = codeParser.parseCodeFile(file, grammar);

        //Apply basic block reduction
        for(Block methodBlock : blocks)
        {
            applyReductionRuleRecursive(methodBlock);
        }

        Terminal.printTerminal("*****Block view*****");
        for(Block block : blocks)
        {
            System.out.println(block.toStringRecursive() + "\n");
        }

        Terminal.printTerminal("*****Code view*****");
        for(Block block : blocks)
        {
            System.out.println(block.getCodeString() + "\n");
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

    private static void applyAbstractionRuleRecursive(Block block, int abstractionLevel)
    {
        ReductionRule abstractionRule = new AbstractionReductionRule();

        if(abstractionLevel <= 0)
        {
            abstractionRule.applyRule(block);
        }
        else
        {
            for(Block childBlock : block.getChildBlocks())
            {
                if(childBlock.getClass() == SelectionBlock.class)
                {
                    //Case statements are the same abstraction level
                    applyAbstractionRuleRecursive(childBlock, abstractionLevel);
                }
                else
                {
                    applyAbstractionRuleRecursive(childBlock, abstractionLevel - 1);
                }
            }
        }
    }
}
