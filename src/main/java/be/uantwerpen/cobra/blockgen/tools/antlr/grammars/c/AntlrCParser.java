package be.uantwerpen.cobra.blockgen.tools.antlr.grammars.c;

import be.uantwerpen.cobra.blockgen.tools.antlr.interfaces.AntlrParser;
import org.antlr.v4.grammar.c.CParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.TokenStream;

/**
 * Created by Thomas on 20/03/2016.
 */
public class AntlrCParser extends CParser implements AntlrParser
{
    public AntlrCParser(TokenStream input)
    {
        super(input);
    }

    public final ParserRuleContext getRootNode() throws RecognitionException
    {
        return super.compilationUnit();
    }
}
