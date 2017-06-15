package be.uantwerpen.idlab.cobra.uppaalexporter.tools.export.blockmodel;

import be.uantwerpen.idlab.cobra.blockgen.models.blocks.Block;
import be.uantwerpen.idlab.cobra.blockgen.tools.export.uppaal.models.Node;

/**
 * Created by Thomas on 27/09/2016.
 */
public interface SourceGenerator
{
    int writeSourceFile(String exportFile, Block codeBlock) throws Exception;
    int writeSourceFile(String exportFile, Node node) throws Exception;
}
