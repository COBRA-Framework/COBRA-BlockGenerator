package be.uantwerpen.idlab.cobra.blockgen.tools.interfaces;

import be.uantwerpen.idlab.cobra.blockgen.tools.blocks.SymbolTable;
import be.uantwerpen.idlab.cobra.common.models.Grammar;
import be.uantwerpen.idlab.cobra.common.models.SourceFile;
import be.uantwerpen.idlab.cobra.common.models.blocks.Block;

import java.util.List;
import java.util.Vector;

/**
 * Created by Thomas on 20/03/2016.
 */
public interface CodeParser
{
    Vector<Block> parseCodeFile(SourceFile file, Grammar grammar) throws Exception;
    List<Block> getBlockModel() throws Exception;
    SymbolTable getSymbolTable() throws Exception;
}
