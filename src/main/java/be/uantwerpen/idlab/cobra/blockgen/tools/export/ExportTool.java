package be.uantwerpen.idlab.cobra.blockgen.tools.export;

import be.uantwerpen.idlab.cobra.blockgen.models.blocks.Block;

/**
 * Created by Thomas on 25/04/2016.
 */
public interface ExportTool
{
    int exportToXML(Block model, String exportFile, String[] args) throws Exception;
}
