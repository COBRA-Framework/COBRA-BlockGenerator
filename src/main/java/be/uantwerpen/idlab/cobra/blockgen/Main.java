package be.uantwerpen.idlab.cobra.blockgen;

import be.uantwerpen.idlab.cobra.blockgen.tools.loopboundanalysis.LoopboundAnalyser;
import be.uantwerpen.idlab.cobra.common.models.Grammar;
import be.uantwerpen.idlab.cobra.common.models.ProjectConfig;
import be.uantwerpen.idlab.cobra.common.models.SourceFile;
import be.uantwerpen.idlab.cobra.common.models.blocks.Block;
import be.uantwerpen.idlab.cobra.common.models.blocks.SourceBlock;
import be.uantwerpen.idlab.cobra.blockgen.services.BlockGenerationService;
import be.uantwerpen.idlab.cobra.blockgen.services.TerminalService;
import be.uantwerpen.idlab.cobra.blockgen.tools.exporting.ProjectExport;
import be.uantwerpen.idlab.cobra.common.tools.importing.project.ProjectFileManager;
import be.uantwerpen.idlab.cobra.blockgen.tools.interfaces.GraphDisplay;
import be.uantwerpen.idlab.cobra.blockgen.tools.jgraphx.JGraphX;
import be.uantwerpen.idlab.cobra.common.tools.terminal.Terminal;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Thomas on 18/03/2016.
 */
public class Main
{
    private static TerminalService terminalService = new TerminalService();
    private static ProjectFileManager projectFileManager = new ProjectFileManager();
    private static boolean terminalMode = false;
    private static boolean showGraph = false;
    private static String file = null;
    private static String outputFolder = null;
    private static ProjectConfig projectConfig = null;

    public static void main(String[] args)
    {
        List<SourceBlock> sourceBlocks = new ArrayList<SourceBlock>();

        checkArguments(args);

        if(!terminalMode)
        {
            try
            {
                projectConfig = projectFileManager.parseProjectConfig(file);

                Terminal.printTerminalInfo("Performing block generation with the following configuration:");
                Terminal.printTerminal(projectConfig.toString() + "\n");

                //Generate models for each source file
                for(SourceFile sourceFile : projectConfig.getSourceFiles())
                {
                    sourceBlocks.add((runBlockGenerator(sourceFile, projectConfig.getAbstractionDepth(), projectConfig.getGrammar())));
                }

                Terminal.printTerminalInfo("Analyse iteration loop bounds...");

                //Analyse iteration loop bounds
                for(Block block : sourceBlocks)
                {
                    LoopboundAnalyser.analyseBlock(block, projectConfig.getGrammar());
                }

                Terminal.printTerminalInfo("Exporting block model to project file...");

                ProjectExport projectExport = new ProjectExport();

                projectExport.exportToXML((List<Block>)(Object)sourceBlocks, outputFolder + "model.xml", null);

                Terminal.printTerminalInfo("Block model exported to: " + outputFolder + "model.xml");

                if(showGraph)
                {
                    //Create graphical view of the block models
                    GraphDisplay graphDisplay = new JGraphX();
                    Vector<JFrame> displays = new Vector<JFrame>();

                    for(Block sourceBlock : sourceBlocks)
                    {
                        displays.add(graphDisplay.DisplayGraphFromBlock(sourceBlock));
                    }

                    Terminal.printTerminalInfo("Block Viewer is ready. Close the Block Viewer window or use 'ctrl+c' in the console to terminate the application.");
                }
            }
            catch(Exception e)
            {
                Terminal.printTerminalError("Failed to generate block model!");
                Terminal.printTerminalError(e.getMessage());
                Terminal.printTerminalError("Block generation will be terminated!");
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
                        String jarName = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getName();

                        Terminal.printTerminal("Usage: java -jar " + jarName + " [options] config_file");
                        Terminal.printTerminal("--help\t\t\tDisplay this message");
                        Terminal.printTerminal("--version\t\tDisplay application version information");
                        Terminal.printTerminal("--output\t\tSet the output folder location (default: config file folder)");
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
                    else if(arg.equals("--output") || arg.equals("-o"))
                    {
                        //Get output folder
                        if(!args[i + 1].startsWith("-"))
                        {
                            outputFolder = new File(args[i + 1]).getPath() + File.separator;

                            //Skip output folder arg in next iteration
                            i++;
                        }
                        else
                        {
                            Terminal.printTerminalError("arg: " + args[i + 1] + " is an invalid output location!");

                            System.exit(-1);
                        }
                    }
                    else if(arg.equals("--show-graph") || arg.equals("-g"))
                    {
                        showGraph = true;
                    }
                    else if(arg.length() >= 2 && i < arg.length() - 2)
                    {
                        Terminal.printTerminalWarning("arg: " + arg + " is an invalid option!");
                    }
                }

                i++;
            }

            int numOfArgs = args.length;
            boolean missingArguments = false;

            if(numOfArgs >= 1)
            {
                //Get config file location
                if(!args[numOfArgs - 1].startsWith("-"))
                {
                    file = args[numOfArgs - 1];

                    File inputFile = new File(file);
                    if(!inputFile.exists())
                    {
                        Terminal.printTerminalError("Config file: '" + file + "' does not exist!");

                        missingArguments = true;
                    }

                    //Get output folder if not defined
                    if(outputFolder == null)
                    {
                        outputFolder = inputFile.getParent() + File.separator;
                    }
                    else
                    {
                        //Create output folder if not existing
                        try
                        {
                            File outputFolderDirectory = new File(outputFolder);

                            if(!outputFolderDirectory.exists())
                            {
                                if(!new File(outputFolder).mkdirs())
                                {
                                    throw new Exception("Invalid output folder location '" + outputFolder + "'");
                                }
                            }
                        }
                        catch(Exception e)
                        {
                            Terminal.printTerminalError("Error while checking for output folder: " + e.getMessage());
                            Terminal.printTerminalError("Application will terminate.");

                            System.exit(-2);
                        }
                    }
                }
                else
                {
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

    private static SourceBlock runBlockGenerator(SourceFile file, int abstractionDepth, Grammar grammar) throws Exception
    {
        SourceBlock sourceBlock;

        Terminal.printTerminalInfo("Generating block model from file: " + file);

        sourceBlock = BlockGenerationService.parseProgramFile(file, abstractionDepth, grammar);

        Terminal.printTerminalInfo("Block generation complete!");

        return sourceBlock;
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
