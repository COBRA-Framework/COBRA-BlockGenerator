package be.uantwerpen.cobra.blockgen.tools.antlr.grammars.cpp;

import be.uantwerpen.cobra.blockgen.tools.antlr.interfaces.AntlrParser;
import org.antlr.v4.grammar.cpp.CPP14Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.TokenStream;

/**
 * Created by Thomas on 20/03/2016.
 */
public class AntlrCPPParser extends CPP14Parser implements AntlrParser
{
    public AntlrCPPParser(TokenStream input)
    {
        super(input);
    }

    public final ParserRuleContext getRootNode() throws RecognitionException
    {
        return super.translationunit();
    }
}
