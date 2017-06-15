package be.uantwerpen.idlab.cobra.uppaalexporter;

import be.uantwerpen.idlab.cobra.uppaalexporter.Main;
import be.uantwerpen.idlab.cobra.blockgen.blockreduction.interfaces.ReductionRule;
import be.uantwerpen.idlab.cobra.blockgen.blockreduction.rules.AbstractionReductionRule;
import be.uantwerpen.idlab.cobra.blockgen.blockreduction.rules.BasicBlockReductionRule;
import be.uantwerpen.idlab.cobra.blockgen.models.Grammar;
import be.uantwerpen.idlab.cobra.blockgen.models.SourceFile;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.Block;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.MethodBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.SourceBlock;
import be.uantwerpen.idlab.cobra.blockgen.services.BlockGenerationService;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.SelectionBlock;
import be.uantwerpen.idlab.cobra.blockgen.tools.antlr.Antlr;
import be.uantwerpen.idlab.cobra.uppaalexporter.tools.export.ExportTool;
import be.uantwerpen.idlab.cobra.uppaalexporter.tools.export.blockmodel.BlockModelExport;
import be.uantwerpen.idlab.cobra.uppaalexporter.tools.export.uppaal.TimedAutomaton;
import be.uantwerpen.idlab.cobra.uppaalexporter.tools.export.uppaal.UppaalModelExport;
import be.uantwerpen.idlab.cobra.blockgen.tools.interfaces.CodeParser;
import be.uantwerpen.idlab.cobra.blockgen.tools.terminal.Terminal;

import be.uantwerpen.idlab.cobra.blockgen.models.ProjectConfig;

import be.uantwerpen.idlab.cobra.blockgen.services.TerminalService;
import be.uantwerpen.idlab.cobra.blockgen.tools.exporting.ProjectExport;

import be.uantwerpen.idlab.cobra.blockgen.tools.importing.ProjectFileManager;
import be.uantwerpen.idlab.cobra.blockgen.tools.interfaces.GraphDisplay;
import be.uantwerpen.idlab.cobra.blockgen.tools.jgraphx.JGraphX;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;

/**
 * Created by Thomas on 18/03/2016.
 */
public class Main {
	private static TerminalService terminalService = new TerminalService();
	private static ProjectFileManager projectFileManager = new ProjectFileManager();
	private static boolean terminalMode = false;
	private static String file = null;
	private static String outputFolder = null;
	private static ProjectConfig projectConfig = null;
	private static int abstractionlevel = 0;

	public static void main(String[] args) {
		// List<SourceBlock> sourceBlocks = new ArrayList<SourceBlock>();
		Vector<Block> blocks = new Vector<Block>();

		checkArguments(args);

		CodeParser codeParser = new Antlr();

		if (!terminalMode) {
			try {
				projectConfig = projectFileManager.parseProjectConfig(file);

				Terminal.printTerminalInfo("Performing block generation with the following configuration:");
				Terminal.printTerminal(projectConfig.toString());

				// Generate block for each source file

				for (SourceFile sourceFile : projectConfig.getSourceFiles()) {
					Vector<Block> blocks_temp = codeParser.parseCodeFile(sourceFile, projectConfig.getGrammar());
					blocks.addAll(blocks_temp);
				}

				Terminal.printTerminalInfo("Generating UPPAAL model...");

				UPPAALExport(blocks);

				Terminal.printTerminalInfo("UPPAAL model exported to: " + outputFolder + "uppaal.xml");

			} catch (Exception e) {
				Terminal.printTerminalError("Failed to generate UPPAAL model!");
				Terminal.printTerminalError(e.getMessage());
				Terminal.printTerminalError("UPPAAL model generation will be terminated!");
			}
		}
	}

	private static void checkArguments(String[] args) {

		if (args.length == 0) {
			Terminal.printTerminal("Use option '--help' to get a list of all known commands.");

			System.exit(0);
		} else {
			int i = 0;

			while (i < args.length) {
				String arg = args[i].toLowerCase().trim();

				if (arg.startsWith("-")) {
					if (arg.equals("--help") || arg.equals("-?")) {
						String jarName = new java.io.File(
								Main.class.getProtectionDomain().getCodeSource().getLocation().getFile()).getName();

						Terminal.printTerminal(
								"Usage: java -jar " + jarName + " [options] export_location config_file");
						Terminal.printTerminal("--help\t\tDisplay this message");
						Terminal.printTerminal("--version\t\tDisplay application version information");
						Terminal.printTerminal(
								"--output\t\tSet the output folder location (default: config file folder)");
						Terminal.printTerminal(
								"--level\t\tSet the abstraction level");

						System.exit(0);
					} else if (arg.equals("--version") || arg.equals("-v")) {
						printBanner();

						System.exit(0);
					} else if (arg.equals("--terminal") || arg.equals("-t")) {
						terminalMode = true;

						printBanner();

						terminalService.systemReady();

						return;

					} else if (arg.equals("--output") || arg.equals("-o")) {
						// Get output folder
						if (!args[i + 1].startsWith("-")) {
							outputFolder = new File(args[i + 1]).getPath() + File.separator;

							// Skip output folder arg in next iteration
							i++;
						} else {
							Terminal.printTerminalError("arg: " + args[i + 1] + " is an invalid output location!");

							System.exit(-1);
						}
					} else if (arg.equals("--level") || arg.equals("-l")) {
						// Get output folder
						if (args[i + 1].matches("\\d+")) {
							abstractionlevel = Integer.parseInt(args[i + 1]);

							// Skip output folder arg in next iteration
							i++;
						} else {
							Terminal.printTerminalError("arg: " + args[i + 1] + " is an invalid abstraction level!");

							System.exit(-1);
						}
					} else if (arg.length() >= 2 && i < arg.length() - 2) {
						Terminal.printTerminalWarning("arg: " + arg + " is an invalid option!");
					}
				}

				i++;

			}

			int numOfArgs = args.length;
			boolean missingArguments = false;

			if (numOfArgs >= 1) {
				// Get config file location
				if (!args[numOfArgs - 1].startsWith("-")) {
					file = args[numOfArgs - 1];

					File inputFile = new File(file);
					if (!inputFile.exists()) {
						Terminal.printTerminalError("Config file: '" + file + "' does not exist!");

						missingArguments = true;
					}

					// Get output folder if not defined
					if (outputFolder == null) {
						outputFolder = inputFile.getParent() + File.separator;
					}
				} else {
					missingArguments = true;
				}
			} else {
				missingArguments = true;
			}

			if (missingArguments) {
				Terminal.printTerminalError("Missing arguments!");
				Terminal.printTerminalInfo("Use option '--help' to get the correct command syntax.");

				System.exit(-1);
			}
		}

	}

	private static void UPPAALExport(Vector<Block> blocks) {

		SourceBlock programBlock = new SourceBlock("PROGRAM");

		for (Block methodBlock : blocks) {
			programBlock.addChildBlock(methodBlock);
		}

		ExportTool exportTool = new UppaalModelExport();

		int abstractionDepth = abstractionlevel;

		Collection<TimedAutomaton> automata = new ArrayList<TimedAutomaton>();

		for (Block methodBlock : programBlock.getChildBlocks()) {
			try {
				automata.add(((UppaalModelExport) exportTool).createTimedAutomaton(methodBlock, abstractionDepth, outputFolder));
			} catch (Exception e) {
				System.err.println("Could not create automaton from method block!\n");
				System.err.println(e.getMessage());
			}
		}
		String exportLocation = outputFolder;

		((UppaalModelExport) exportTool).exportToXML(automata, exportLocation + "uppaal.xml");

		BlockModelExport benchmarkExport = new BlockModelExport();

		// Generate UPPAAL C source files for benchmarking
		File uppaalCSourceFolder = new File(exportLocation + "UPPAAL_source/");
		uppaalCSourceFolder.mkdirs();

		for (TimedAutomaton automaton : automata) {
			try {
				benchmarkExport.generateSourceOfTimedAutomaton(exportLocation + "UPPAAL_source/", automaton, Grammar.C);
			} catch (Exception e) {
				System.err.println("Could not create source file for automaton!");
				System.err.println(e.getMessage());
			}
		}

	}
	
	private static void printBanner() {
		Terminal.printTerminal(" _____  _____ ______ ______   ___   \n" + "/  __ \\|  _  || ___ \\| ___ \\ / _ \\ \n"
				+ "| /  \\/| | | || |_/ /| |_/ // /_\\ \\\n" + "| |    | | | || ___ \\|    / |  _  |\n"
				+ "| \\__/\\\\ \\_/ /| |_/ /| |\\ \\ | | | |\n" + " \\____/ \\___/ \\____/ \\_| \\_|\\_| |_/ ");

		Terminal.printTerminal("==================================");
		Terminal.printTerminal(":: COBRA framework - 0.1(alpha) ::");
		Terminal.printTerminal("\nCOBRA - Block Generator [Version "
				+ Main.class.getPackage().getImplementationVersion()
				+ "]\nCopyright (c) 2016-2017 Thomas Huybrechts, IDLab,\nUniversity of Antwerp, Belgium. All rights reserved.");
		Terminal.printTerminal("This program comes with ABSOLUTELY NO WARRANTY.");
	}
}