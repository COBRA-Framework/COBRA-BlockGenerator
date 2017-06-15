package be.uantwerpen.idlab.cobra.uppaalexporter.tools.export.blockmodel;

import be.uantwerpen.idlab.cobra.blockgen.models.blocks.Block;
import be.uantwerpen.idlab.cobra.blockgen.tools.export.blockmodel.generators.CSourceGenerator;
import be.uantwerpen.idlab.cobra.blockgen.tools.export.uppaal.models.Node;
import be.uantwerpen.idlab.cobra.blockgen.tools.interfaces.CodeParser;
import be.uantwerpen.idlab.cobra.uppaalexporter.tools.export.uppaal.TimedAutomaton;
import be.uantwerpen.idlab.cobra.blockgen.models.Grammar;

/**
 * Created by Thomas on 09/05/2016.
 */
public class BlockModelExport
{
    public int generateSourceOfBlock(String fileLocation, Block codeBlock, Grammar grammar) throws Exception
    {
        SourceGenerator generator;
        int startRowNumber = codeBlock.getStartRowNumber();
        int endRowNumber = codeBlock.getEndRowNumber();
        String exportFile = fileLocation + "block_";

        //Generate file name
        if(startRowNumber != endRowNumber)
        {
            exportFile = exportFile.concat("r" + startRowNumber + "-" + endRowNumber);
        }
        else
        {
            exportFile = exportFile.concat("r" + startRowNumber);
        }

        switch(grammar)
        {
            case C:
                exportFile = exportFile.concat(".c");
                generator = new CSourceGenerator();
                break;
            default:
                throw new Exception("Exporting to:" + grammar.name() + " is not supported!");
        }

        return generator.writeSourceFile(exportFile, codeBlock);
    }

    public int generateSourceOfTimedAutomaton(String fileLocation, TimedAutomaton automaton, Grammar grammar) throws Exception
    {
        for(Node node : automaton.getNodes())
        {	
        	if(node.getName().matches(".*r([0-9]+)$"))
            generateSourceOfNode(fileLocation + automaton.getName() + "_", node, grammar);
        }

        return 0;
    }

    public int generateSourceOfNode(String fileLocation, Node node, Grammar grammar) throws Exception
    {
        String exportFile = fileLocation + "node_" + node.getName();
        SourceGenerator generator;

        switch(grammar)
        {
            case C:
                exportFile = exportFile.concat(".c");
                generator = new CSourceGenerator();
                break;
            default:
                throw new Exception("Exporting to:" + grammar.name() + " is not supported!");
        }

        return generator.writeSourceFile(exportFile, node);
    }
}
