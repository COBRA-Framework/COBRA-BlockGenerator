package be.uantwerpen.idlab.cobra.blockgen.tools.interfaces;

import be.uantwerpen.idlab.cobra.blockgen.models.Grammar;
import be.uantwerpen.idlab.cobra.blockgen.models.SourceFile;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.Block;

import java.util.Vector;

/**
 * Created by Thomas on 20/03/2016.
 */
public interface CodeParser
{
    
    Vector<Block> parseCodeFile(SourceFile fileName, Grammar grammar) throws Exception;
}
