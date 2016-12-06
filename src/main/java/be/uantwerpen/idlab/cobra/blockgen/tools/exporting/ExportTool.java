package be.uantwerpen.idlab.cobra.blockgen.tools.exporting;

import be.uantwerpen.idlab.cobra.blockgen.models.blocks.Block;

import java.util.List;

/**
 * Created by Thomas on 25/04/2016.
 */
public interface ExportTool
{
    int exportToXML(List<Block> model, String exportFile, String[] args) throws Exception;
}
