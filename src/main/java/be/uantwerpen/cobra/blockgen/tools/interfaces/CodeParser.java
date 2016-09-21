package be.uantwerpen.cobra.blockgen.tools.interfaces;

import be.uantwerpen.cobra.blockgen.models.blocks.Block;

import java.util.Vector;

/**
 * Created by Thomas on 20/03/2016.
 */
public interface CodeParser
{
    enum Grammar
    {
        C,
        CPP
    }

    Vector<Block> parseCodeFile(String fileName, Grammar grammar) throws Exception;
}
