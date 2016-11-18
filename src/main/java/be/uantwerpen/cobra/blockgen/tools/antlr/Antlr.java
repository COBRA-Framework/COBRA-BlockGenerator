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
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
        ParserErrorListener errorListener;
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
        errorListener = new ParserErrorListener();
        parser.addErrorListener(errorListener);

        //Get listener
        listener = getListener(grammar, blockParser);

        //Walk tree and attach listener
        ParseTree tree = parser.getRootNode();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);

        if(errorListener.getNumberOfSyntaxErrors() > 0)
        {
            throw new Exception("Errors are detected in the syntax of the input file. Parsing will be terminated!\n" + errorListener.toString());
        }

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

    private class ParserErrorListener extends BaseErrorListener
    {
        private final List<String> syntaxErrors;

        public ParserErrorListener()
        {
            this.syntaxErrors = new ArrayList<String>();
        }

        public List<String> getSyntaxErrors()
        {
            return this.syntaxErrors;
        }

        public int getNumberOfSyntaxErrors()
        {
            return this.syntaxErrors.size();
        }

        @Override
        public String toString()
        {
            String syntaxMessages = "";

            if(!this.syntaxErrors.isEmpty())
            {
                for(String syntaxError : this.syntaxErrors)
                {
                    syntaxMessages = syntaxMessages.concat(syntaxError + "\n");
                }

                syntaxMessages = syntaxMessages.substring(0, syntaxMessages.length() - 1);
            }

            return syntaxMessages;
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
        {
            this.syntaxErrors.add("Syntax error (line " + line + ":" + charPositionInLine + ") -> " + msg);
        }
    }
}
