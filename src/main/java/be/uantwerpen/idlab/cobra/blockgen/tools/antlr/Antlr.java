package be.uantwerpen.idlab.cobra.blockgen.tools.antlr;

import be.uantwerpen.idlab.cobra.blockgen.tools.blocks.CSymbolFactory;
import be.uantwerpen.idlab.cobra.blockgen.tools.blocks.SymbolFactory;
import be.uantwerpen.idlab.cobra.common.models.CodeFile;
import be.uantwerpen.idlab.cobra.common.models.Grammar;
import be.uantwerpen.idlab.cobra.common.models.SourceFile;
import be.uantwerpen.idlab.cobra.common.models.blocks.Block;
import be.uantwerpen.idlab.cobra.blockgen.tools.blocks.BlockFactory;
import be.uantwerpen.idlab.cobra.blockgen.tools.antlr.grammars.c.AntlrCLexer;
import be.uantwerpen.idlab.cobra.blockgen.tools.antlr.grammars.c.AntlrCListener;
import be.uantwerpen.idlab.cobra.blockgen.tools.antlr.grammars.c.AntlrCParser;
import be.uantwerpen.idlab.cobra.blockgen.tools.antlr.grammars.cpp.AntlrCPPLexer;
import be.uantwerpen.idlab.cobra.blockgen.tools.antlr.grammars.cpp.AntlrCPPListener;
import be.uantwerpen.idlab.cobra.blockgen.tools.antlr.grammars.cpp.AntlrCPPParser;
import be.uantwerpen.idlab.cobra.blockgen.tools.antlr.interfaces.AntlrLexer;
import be.uantwerpen.idlab.cobra.blockgen.tools.antlr.interfaces.AntlrListener;
import be.uantwerpen.idlab.cobra.blockgen.tools.antlr.interfaces.AntlrParser;
import be.uantwerpen.idlab.cobra.blockgen.tools.blocks.FactoryErrorListener;
import be.uantwerpen.idlab.cobra.blockgen.tools.blocks.grammars.CBlockFactory;
import be.uantwerpen.idlab.cobra.blockgen.tools.interfaces.CodeParser;
import be.uantwerpen.idlab.cobra.common.tools.terminal.Terminal;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Thomas on 20/03/2016.
 */
public class Antlr implements CodeParser
{
    public Vector<Block> parseCodeFile(SourceFile file, Grammar grammar) throws Exception
    {
        AntlrLexer lexer;
        AntlrParser parser;
        AntlrListener listener;
        ParserErrorListener parserErrorListener;
        FactoryErrorListener factoryErrorListener;
        CharStream fileStream;
        BlockFactory blockParser;
        SymbolFactory symbolFactory;
        CodeFile codeFile;

        Vector<Block> blocks = new Vector<Block>();

        //Get file stream of code file
        try
        {
            fileStream = new ANTLRFileStream(file.getSourceLocation());
        }
        catch(IOException e)
        {
            throw new IOException("Could not open file stream: \"" + file + "\" for parsing!\n\n" + e.getMessage(), e);
        }

        //Create code file
        codeFile = new CodeFile(fileStream.getText(Interval.of(0, fileStream.size())), grammar.name(), fileStream.getSourceName(), file.getId());

        //Create block parser
        blockParser = getBlockFactory(grammar, codeFile);
        factoryErrorListener = new FactoryErrorListener();
        blockParser.addErrorListener(factoryErrorListener);

        //Create Symbol Factory
	    symbolFactory = getSymbolFactory(grammar);

        //Get lexer
        lexer = getLexer(grammar, fileStream);

        //Get parser
        parser = getParser(grammar, lexer);
        parserErrorListener = new ParserErrorListener();
        parser.addErrorListener(parserErrorListener);

        //Get listener
        listener = getListener(grammar, blockParser, symbolFactory);

        //Walk tree and attach listener
        ParseTree tree = parser.getRootNode();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, tree);

        if(parserErrorListener.getNumberOfSyntaxErrors() > 0)
        {
            throw new Exception("Errors are detected in the syntax of the input file. Parsing will be terminated!\n" + parserErrorListener.toString());
        }

        if(factoryErrorListener.getNumberOfFactoryErrors() > 0)
        {
            throw new Exception("Errors are detected in the block builder. Block generation will be terminated!\n" + factoryErrorListener.toString());
        }

	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    DocumentBuilder db = dbf.newDocumentBuilder();
	    Document doc = db.newDocument();

        doc.appendChild(symbolFactory.flush().toXMLNode(doc, null));

	    TransformerFactory tf = TransformerFactory.newInstance();
	    Transformer transformer = tf.newTransformer();

	    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

	    DOMSource source = new DOMSource(doc);

	    StreamResult stream = new StreamResult(new File("SymbolTable.xml"));
	    transformer.transform(source, stream);

        return blockParser.getGeneratedBlocks();
    }

	protected SymbolFactory getSymbolFactory(Grammar grammar) throws Exception
	{
		SymbolFactory factory = null;

		switch(grammar)
		{
			case C:
			{
				factory = new CSymbolFactory();
				break;
			}
			default:
			{
				throw new Exception("Could not initialize symbol factory for grammar (" + grammar + "). Grammar unknown!");
			}
		}

		return factory;
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

    protected AntlrListener getListener(Grammar grammar, BlockFactory blockParser, SymbolFactory symbolFactory) throws Exception
    {
        AntlrListener listener = null;

        switch(grammar)
        {
            case C:
            {
                listener = new AntlrCListener(blockParser, symbolFactory);
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

    protected BlockFactory getBlockFactory(Grammar grammar, CodeFile codeFile) throws Exception
    {
        BlockFactory factory = null;

        switch(grammar)
        {
            case C:
            {
                factory = new CBlockFactory(codeFile);
                break;
            }
            default:
            {
                throw new Exception("Could not initialize block factory for grammar (" + grammar + "). Grammar unknown!");
            }
        }

        return factory;
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

                syntaxMessages = syntaxMessages.trim();
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
