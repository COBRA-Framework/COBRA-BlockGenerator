package be.uantwerpen.cobra.blockgen;

import be.uantwerpen.cobra.blockgen.blockreduction.interfaces.ReductionRule;
import be.uantwerpen.cobra.blockgen.blockreduction.rules.AbstractionReductionRule;
import be.uantwerpen.cobra.blockgen.blockreduction.rules.BasicBlockReductionRule;
import be.uantwerpen.cobra.blockgen.models.blocks.Block;
import be.uantwerpen.cobra.blockgen.models.blocks.MethodBlock;
import be.uantwerpen.cobra.blockgen.models.blocks.ProgramBlock;
import be.uantwerpen.cobra.blockgen.models.blocks.SelectionBlock;
import be.uantwerpen.cobra.blockgen.tools.antlr.Antlr;
import be.uantwerpen.cobra.blockgen.tools.export.ExportTool;
import be.uantwerpen.cobra.blockgen.tools.export.blockmodel.BlockModelExport;
import be.uantwerpen.cobra.blockgen.tools.export.uppaal.TimedAutomaton;
import be.uantwerpen.cobra.blockgen.tools.export.uppaal.UppaalModelExport;
import be.uantwerpen.cobra.blockgen.tools.interfaces.CodeParser;
import be.uantwerpen.cobra.blockgen.tools.jgraph.GraphTool;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

/**
 * Created by Thomas on 18/03/2016.
 */
public class Main extends Application
{
    public static void main(String[] args)
    {
        int abstractionDepth = 0;
        //Setup JavaFX
        //launch(args);

        printBanner();

        if(args.length < 3)
        {
            System.out.println("Missing arguments!");
            System.out.println("arg 1: Import file location\narg 2: Export file location\narg 3: Depth-level of abstraction");
            System.exit(-1);
        }

        try
        {
            abstractionDepth = Integer.parseInt(args[2]);
        }
        catch(Exception e)
        {
            System.err.println("Arg 3: Depth-level of abstraction is not a valid number!");

            System.exit(-1);
        }

        generateOUTPUT(args[0], args[1], abstractionDepth);
/*
        try
        {
            //System.in.read();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }*/

        System.exit(0);
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        primaryStage.setTitle("COBRA Framework - Block Generator");

        StackPane layout = new StackPane();

        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
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

    private static ProgramBlock generateOUTPUT(String file, String exportLocation, int abstractionDepth)
    {
        CodeParser codeParser = new Antlr();
        GraphTool graphTool = new GraphTool();

        Vector<Block> blocks = null;

        File exportFolder = new File(exportLocation);
        exportFolder.mkdirs();

        try
        {
            blocks = codeParser.parseCodeFile(file, CodeParser.Grammar.C);
        }
        catch(Exception e)
        {
            System.err.println(e.getMessage());

            e.printStackTrace();
        }

        System.out.println("\n*****Block view*****");
        for(Block block : blocks)
        {
            System.out.println(block.toStringRecursive() + "\n");
        }

        System.out.println("\n*****Code view*****");
        for(Block block : blocks)
        {
            System.out.println(block.getCodeString() + "\n");
        }
/*
        System.out.println("\n*****Leaf blocks*****");
        for(Block block : blocks)
        {
            MethodBlock methodBlock = (MethodBlock) block;

            System.out.println("Method: " + methodBlock.getMethodName());

            for(Block leafblock : block.getLeafs())
            {
                System.out.println(leafblock.toString());
            }
        }
*/
/*
        for(Block block : blocks)
        {
            //graphTool.createGraphFromBlock(block);
        }

        graphTool.createGraphFromBlock(blocks.get(2));

        ReductionRule rule = new BasicBlockReductionRule();

        Block ruleBlock = rule.applyRule(blocks.get(2).getChildBlock(2));

        graphTool.createGraphFromBlock(blocks.get(2));

        for(Block block : blocks)
        {
            //graphTool.createGraphFromBlock(block);
        }*/

        //Apply basic block reduction
        for(Block methodBlock : blocks)
        {
            applyReductionRuleRecursive(methodBlock);
        }

        ProgramBlock programBlock = new ProgramBlock("PROGRAM");

        for(Block methodBlock : blocks)
        {
            programBlock.addChildBlock(methodBlock);
        }

        ExportTool exportTool = new UppaalModelExport();

        String[] exportArgs = {Integer.toString(abstractionDepth)};

        Collection<TimedAutomaton> automata = new ArrayList<TimedAutomaton>();

        for(Block methodBlock : programBlock.getChildBlocks())
        {
            try
            {
                automata.add(((UppaalModelExport)exportTool).createTimedAutomaton(methodBlock, abstractionDepth));
            }
            catch(Exception e)
            {
                System.err.println("Could not create automaton from method block!\n");
                System.err.println(e.getMessage());
            }
        }

        ((UppaalModelExport)exportTool).exportToXML(automata, exportLocation + "uppaal.xml");

        BlockModelExport benchmarkExport = new BlockModelExport();

        //Generate UPPAAL C source files for benchmarking
        File uppaalCSourceFolder = new File(exportLocation + "UPPAAL_source/");
        uppaalCSourceFolder.mkdirs();

        for(TimedAutomaton automaton : automata)
        {
            try
            {
                benchmarkExport.generateSourceOfTimedAutomaton(exportLocation + "UPPAAL_source/", automaton, CodeParser.Grammar.C);
            }
            catch(Exception e)
            {
                System.err.println("Could not create source file for automaton!");
                System.err.println(e.getMessage());
            }
        }

        //Generate HPA C source files for benchmarking
        File hpaCSourceFolder = new File(exportLocation + "HPA_source/");
        hpaCSourceFolder.mkdirs();

        try
        {
            for(Block methodBlock : blocks)
            {
                applyAbstractionRuleRecursive(methodBlock, abstractionDepth);
                applyReductionRuleRecursive(methodBlock);

                //graphTool.createGraphFromBlock(methodBlock);

                for(Block leafBlock : methodBlock.getLeafs())
                {
                    benchmarkExport.generateSourceOfBlock(exportLocation + "HPA_source/" + ((MethodBlock)methodBlock).getMethodName() + "_", leafBlock, CodeParser.Grammar.C);
                }
            }
        }
        catch(Exception e)
        {
            System.err.println("Could not generate benchmark source files!");
            System.err.println(e.getMessage());
        }

        return programBlock;
    }

    private static void printBanner()
    {
        System.out.println(
                " _____  _____ ______ ______   ___   \n" +
                "/  __ \\|  _  || ___ \\| ___ \\ / _ \\ \n" +
                "| /  \\/| | | || |_/ /| |_/ // /_\\ \\\n" +
                "| |    | | | || ___ \\|    / |  _  |\n" +
                "| \\__/\\\\ \\_/ /| |_/ /| |\\ \\ | | | |\n" +
                " \\____/ \\___/ \\____/ \\_| \\_|\\_| |_/ \n");

        System.out.println("COBRA-framework - BlockGenerator");
        System.out.println("================================\n");
        System.out.println("Developed by: Thomas Huybrechts - 2016 MOSAIC\n");
    }
}
