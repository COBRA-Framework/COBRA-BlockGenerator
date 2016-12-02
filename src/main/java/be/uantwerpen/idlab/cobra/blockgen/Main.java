package be.uantwerpen.idlab.cobra.blockgen;

import be.uantwerpen.idlab.cobra.blockgen.blockreduction.interfaces.ReductionRule;
import be.uantwerpen.idlab.cobra.blockgen.blockreduction.rules.AbstractionReductionRule;
import be.uantwerpen.idlab.cobra.blockgen.blockreduction.rules.BasicBlockReductionRule;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.Block;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.MethodBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.ProgramBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.SelectionBlock;
import be.uantwerpen.idlab.cobra.blockgen.services.TerminalService;
import be.uantwerpen.idlab.cobra.blockgen.tools.antlr.Antlr;
import be.uantwerpen.idlab.cobra.blockgen.tools.export.ProjectExport;
import be.uantwerpen.idlab.cobra.blockgen.tools.export.blockmodel.BlockModelExport;
import be.uantwerpen.idlab.cobra.blockgen.tools.interfaces.CodeParser;
import be.uantwerpen.idlab.cobra.blockgen.tools.interfaces.GraphDisplay;
import be.uantwerpen.idlab.cobra.blockgen.tools.jgraphx.JGraphX;
import be.uantwerpen.idlab.cobra.blockgen.tools.terminal.Terminal;

import javax.swing.*;
import java.io.File;
import java.util.Vector;

/**
 * Created by Thomas on 18/03/2016.
 */
public class Main
{
    private static TerminalService terminalService = new TerminalService();
    private static boolean terminalMode = false;
    private static boolean showGraph = false;
    private static String file = new String();
    private static String outputFolder = new String();
    private static int abstractionDepth = -1;

    public static void main(String[] args)
    {
        checkArguments(args);

        if(!terminalMode)
        {
            try
            {
                runBlockGenerator(file, outputFolder, abstractionDepth);
            }
            catch(Exception e)
            {
                Terminal.printTerminalError("Failed to generate block model!");
                Terminal.printTerminalError(e.getMessage());
                Terminal.printTerminalError("Block generation will be terminated!");
                //e.printStackTrace();
            }
        }
    }

    private static void checkArguments(String[] args)
    {
        if(args.length == 0)
        {
            Terminal.printTerminal("Use option '--help' to get a list of all known commands.");

            System.exit(0);
        }
        else
        {
            int i = 0;

            while(i < args.length)
            {
                String arg = args[i].toLowerCase().trim();

                if(arg.startsWith("-"))
                {
                    if(arg.equals("--help") || arg.equals("-?"))
                    {
                        String jarName = new java.io.File(Main.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getName();

                        Terminal.printTerminal("Usage: java -jar " + jarName + " [options] file output-folder abstraction-depth");
                        Terminal.printTerminal("--help\t\t\tDisplay this message");
                        Terminal.printTerminal("--version\t\tDisplay application version information");
                        Terminal.printTerminal("--show-graph\tDisplay a graphical view of the block model");

                        System.exit(0);
                    }
                    else if(arg.equals("--version") || arg.equals("-v"))
                    {
                        printBanner();

                        System.exit(0);
                    }
                    else if(arg.equals("--terminal") || arg.equals("-t"))
                    {
                        terminalMode = true;

                        printBanner();

                        terminalService.systemReady();

                        return;
                    }
                    else if(arg.equals("--show-graph") || arg.equals("-g"))
                    {
                        showGraph = true;
                    }
                    else if(arg.length() >= 3 && i < arg.length() - 3)
                    {
                        Terminal.printTerminalWarning("arg: " + arg + " is an invalid option!");
                    }
                }

                i++;
            }

            int numOfArgs = args.length;
            boolean missingArguments = false;

            if(numOfArgs >= 3)
            {
                //Get input file location
                if(!args[numOfArgs - 3].startsWith("-"))
                {
                    file = args[numOfArgs - 3];

                    File inputFile = new File(file);
                    if(!inputFile.exists())
                    {
                        Terminal.printTerminalError("Input file: '" + file + "' does not exist!");

                        missingArguments = true;
                    }
                }
                else
                {
                    missingArguments = true;
                }

                //Get output folder
                if(!args[numOfArgs - 2].startsWith("-"))
                {
                    outputFolder = args[numOfArgs - 2];
                }
                else
                {
                    missingArguments = true;
                }

                //Parse abstraction depth (last argument)
                try
                {
                    abstractionDepth = Integer.parseInt(args[numOfArgs - 1]);
                }
                catch(Exception e)
                {
                    Terminal.printTerminalError("Arg 3: Abstraction-depth is not a valid number!");

                    missingArguments = true;
                }
            }
            else
            {
                missingArguments = true;
            }

            if(missingArguments)
            {
                Terminal.printTerminalError("Missing arguments!");
                Terminal.printTerminalInfo("Use option '--help' to get the correct command syntax.");

                System.exit(-1);
            }
        }
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

    private static ProgramBlock runBlockGenerator(String file, String exportLocation, int abstractionDepth) throws Exception
    {
        CodeParser codeParser = new Antlr();

        Vector<Block> blocks = null;

        File exportFolder = new File(exportLocation);
        exportFolder.mkdirs();

        Terminal.printTerminalInfo("Generating block model from file: " + file);

        //Parse file and generate block model
        blocks = codeParser.parseCodeFile(file, CodeParser.Grammar.C);

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

        ProgramBlock programBlock = new ProgramBlock("PROGRAM");

        for(Block methodBlock : blocks)
        {
            programBlock.addChildBlock(methodBlock);
        }

        Terminal.printTerminalInfo("Block generation complete!");

        BlockModelExport benchmarkExport = new BlockModelExport();

        //Generate HPA source files for benchmarking
        File hpaSourceFolder = new File(exportLocation + "HPA_source/");
        hpaSourceFolder.mkdirs();

        Terminal.printTerminalInfo("Exporting block model source files...");

        for(Block methodBlock : blocks)
        {
            //applyAbstractionRuleRecursive(methodBlock, abstractionDepth);
            //applyReductionRuleRecursive(methodBlock);

            for(Block leafBlock : methodBlock.getLeafs())
            {
                benchmarkExport.generateSourceOfBlock(exportLocation + "HPA_source/" + ((MethodBlock)methodBlock).getMethodName() + "_", leafBlock, CodeParser.Grammar.C);
            }
        }

        Terminal.printTerminalInfo("Generated files will be available in the folder: " + outputFolder);

        Terminal.printTerminalInfo("Exporting block model to project file...");

        ProjectExport projectExport = new ProjectExport();

        projectExport.exportToXML(programBlock, exportLocation + "model.xml", null);

        Terminal.printTerminalInfo("Block model exported to: " + exportLocation + "model.xml");

        if(showGraph)
        {
            //Create graphical view of the block models
            GraphDisplay graphDisplay = new JGraphX();
            Vector<JFrame> displays = new Vector<JFrame>();

            for(Block block : blocks)
            {
                displays.add(graphDisplay.DisplayGraphFromBlock(block));
            }

            Terminal.printTerminalInfo("Block Viewer is ready. Close the Block Viewer window or use 'ctrl+c' in the console to terminate the application.");
        }

        return programBlock;
    }

    private static void printBanner()
    {
        Terminal.printTerminal(
                " _____  _____ ______ ______   ___   \n" +
                "/  __ \\|  _  || ___ \\| ___ \\ / _ \\ \n" +
                "| /  \\/| | | || |_/ /| |_/ // /_\\ \\\n" +
                "| |    | | | || ___ \\|    / |  _  |\n" +
                "| \\__/\\\\ \\_/ /| |_/ /| |\\ \\ | | | |\n" +
                " \\____/ \\___/ \\____/ \\_| \\_|\\_| |_/ ");

        Terminal.printTerminal("==================================");
        Terminal.printTerminal(":: COBRA framework - 0.1(alpha) ::");
        Terminal.printTerminal("\nCOBRA - Block Generator [Version " + Main.class.getPackage().getImplementationVersion() + "]\nCopyright (c) 2016-2017 Thomas Huybrechts, IDLab,\nUniversity of Antwerp, Belgium. All rights reserved.");
        Terminal.printTerminal("This program comes with ABSOLUTELY NO WARRANTY.");
    }
}
