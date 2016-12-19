package be.uantwerpen.idlab.cobra.blockgen.tools.exporting.blockmodel;

import be.uantwerpen.idlab.cobra.blockgen.models.Grammar;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.Block;
import be.uantwerpen.idlab.cobra.blockgen.tools.exporting.blockmodel.generators.CSourceGenerator;

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
}
