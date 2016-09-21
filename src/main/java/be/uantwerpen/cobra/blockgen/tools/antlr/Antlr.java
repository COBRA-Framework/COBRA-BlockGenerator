package be.uantwerpen.cobra.blockgen.tools.antlr;

import be.uantwerpen.cobra.blockgen.models.CodeFile;
import be.uantwerpen.cobra.blockgen.models.blocks.Block;
import be.uantwerpen.cobra.blockgen.tools.BlockFactory;
import be.uantwerpen.cobra.blockgen.tools.antlr.grammars.c.AntlrCLexer;
import be.uantwerpen.cobra.blockgen.tools.antlr.grammars.c.AntlrCListener;
import be.uantwerpen.cobra.blockgen.tools.antlr.grammars.c.AntlrCParser;
import be.uantwerpen.cobra.blockgen.tools.antlr.grammars.cpp.AntlrCPPLexer;
import be.uantwerpen.cobra.blockgen.tools.antlr.grammars.cpp.AntlrCPPListener;
import be.uantwerpen.cobra.blockgen.tools.antlr.grammars.cpp.AntlrCPPParser;
import be.uantwerpen.cobra.blockgen.tools.antlr.interfaces.AntlrLexer;
import be.uantwerpen.cobra.blockgen.tools.antlr.interfaces.AntlrListener;
import be.uantwerpen.cobra.blockgen.tools.antlr.interfaces.AntlrParser;
import be.uantwerpen.cobra.blockgen.tools.interfaces.CodeParser;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by Thomas on 20/03/2016.
 */
public class Antlr implements CodeParser
{
    public Vector<Block> parseCodeFile(String fileName, Grammar grammar) throws Exception
    {
        AntlrLexer lexer;
        AntlrParser parser;
        AntlrListener listener;
        CharStream fileStream;
        BlockFactory blockParser;
        CodeFile codeFile;

        Vector<Block> blocks = new Vector<Block>();

        //Get file stream of code file
        try
        {
            fileStream = new ANTLRFileStream(fileName);
        }
        catch(IOException ex)
        {
            throw new IOException("Could not open file stream: \"" + fileName + "\" for parsing!\n\n" + ex.getMessage());
        }

        //Create code file
        codeFile = new CodeFile(fileStream.getText(Interval.of(0, fileStream.size())), grammar.name(), fileStream.getSourceName());

        //Create block parser
        blockParser = new BlockFactory(codeFile);

        //Get lexer
        lexer = getLexer(grammar, fileStream);

        //Get parser
        parser = getParser(grammar, lexer);

        //Get listener
        listener = getListener(grammar, blockParser);

        //Walk tree and attach listener
        ParseTree tree = parser.getRootNode();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);

        return blockParser.getGeneratedBlocks();
    }

    protected AntlrLexer getLexer(Grammar grammar, CharStream stream) throws Exception
    {
        AntlrLexer lexer = null;

        switch(grammar)
        {
            case C:
            {
                lexer = new AntlrCLexer(stream);
                break;
            }
            case CPP:
            {
                lexer = new AntlrCPPLexer(stream);
                break;
            }
            default:
            {
                throw new Exception("Could not initialize lexer for grammar (" + grammar + "). Grammar unknown!");
            }
        }

        return lexer;
    }

    protected AntlrParser getParser(Grammar grammar, AntlrLexer lexer) throws Exception
    {
        AntlrParser parser = null;

        //Get token list
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        switch(grammar)
        {
            case C:
            {
                parser = new AntlrCParser(tokens);
                break;
            }
            case CPP:
            {
                parser = new AntlrCPPParser(tokens);
                break;
            }
            default:
            {
                throw new Exception("Could not initialize parser for grammar (" + grammar + "). Grammar unknown!");
            }
        }

        return parser;
    }

    protected AntlrListener getListener(Grammar grammar, BlockFactory blockParser) throws Exception
    {
        AntlrListener listener = null;

        switch(grammar)
        {
            case C:
            {
                listener = new AntlrCListener(blockParser);
                break;
            }
            case CPP:
            {
                listener = new AntlrCPPListener(blockParser);
                break;
            }
            default:
            {
                throw new Exception("Could not initialize listener for grammar (" + grammar + "). Grammar unknown!");
            }
        }

        return listener;
    }
}
