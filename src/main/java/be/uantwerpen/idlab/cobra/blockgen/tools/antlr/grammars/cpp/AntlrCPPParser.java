package be.uantwerpen.idlab.cobra.blockgen.tools.antlr.grammars.cpp;

import be.uantwerpen.idlab.cobra.blockgen.tools.antlr.interfaces.AntlrParser;
import org.antlr.v4.grammar.cpp.CPP14Parser;
import org.antlr.v4.runtime.*;

/**
 * Created by Thomas on 20/03/2016.
 */
public class AntlrCPPParser extends CPP14Parser implements AntlrParser
{
    public AntlrCPPParser(TokenStream input)
    {
        super(input);
        super.removeErrorListener(ConsoleErrorListener.INSTANCE);
    }

    public final ParserRuleContext getRootNode() throws RecognitionException
    {
        return super.translationunit();
    }

    public void addErrorListener(BaseErrorListener errorListener)
    {
        super.addErrorListener(errorListener);
    }
}
