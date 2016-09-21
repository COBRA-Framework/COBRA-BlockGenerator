package be.uantwerpen.cobra.blockgen.tools.antlr.grammars.cpp;

import be.uantwerpen.cobra.blockgen.tools.antlr.interfaces.AntlrLexer;
import org.antlr.v4.grammar.cpp.CPP14Lexer;
import org.antlr.v4.runtime.CharStream;

/**
 * Created by Thomas on 20/03/2016.
 */
public class AntlrCPPLexer extends CPP14Lexer implements AntlrLexer
{
    public AntlrCPPLexer(CharStream input)
    {
        super(input);
    }
}
