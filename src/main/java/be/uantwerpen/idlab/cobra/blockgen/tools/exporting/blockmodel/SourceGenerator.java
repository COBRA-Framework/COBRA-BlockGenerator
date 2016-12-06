package be.uantwerpen.idlab.cobra.blockgen.tools.exporting.blockmodel;

import be.uantwerpen.idlab.cobra.blockgen.models.blocks.Block;

/**
 * Created by Thomas on 27/09/2016.
 */
public interface SourceGenerator
{
    int writeSourceFile(String exportFile, Block codeBlock) throws Exception;
}
