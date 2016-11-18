package be.uantwerpen.cobra.blockgen.tools.antlr.grammars.c;

import be.uantwerpen.cobra.blockgen.tools.antlr.interfaces.AntlrLexer;
import org.antlr.v4.grammar.c.CLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.ConsoleErrorListener;

/**
 * Created by Thomas on 20/03/2016.
 */
public class AntlrCLexer extends CLexer implements AntlrLexer
{
    public AntlrCLexer(CharStream input)
    {
        super(input);
        super.removeErrorListener(ConsoleErrorListener.INSTANCE);
    }
}
