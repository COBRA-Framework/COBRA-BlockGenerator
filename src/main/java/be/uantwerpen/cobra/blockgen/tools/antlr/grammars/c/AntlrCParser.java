package be.uantwerpen.cobra.blockgen.tools.antlr.grammars.c;

import be.uantwerpen.cobra.blockgen.tools.antlr.interfaces.AntlrParser;
import org.antlr.v4.grammar.c.CParser;
import org.antlr.v4.runtime.*;

/**
 * Created by Thomas on 20/03/2016.
 */
public class AntlrCParser extends CParser implements AntlrParser
{
    public AntlrCParser(TokenStream input)
    {
        super(input);
        super.removeErrorListener(ConsoleErrorListener.INSTANCE);
    }

    public final ParserRuleContext getRootNode() throws RecognitionException
    {
        return super.compilationUnit();
    }

    public void addErrorListener(BaseErrorListener errorListener)
    {
        super.addErrorListener(errorListener);
    }
}
