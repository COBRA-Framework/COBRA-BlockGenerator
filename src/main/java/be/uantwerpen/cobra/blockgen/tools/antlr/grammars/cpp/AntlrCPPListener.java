package be.uantwerpen.cobra.blockgen.tools.antlr.grammars.cpp;

import be.uantwerpen.cobra.blockgen.tools.BlockFactory;
import be.uantwerpen.cobra.blockgen.tools.antlr.interfaces.AntlrListener;
import org.antlr.v4.grammar.cpp.CPP14BaseListener;

/**
 * Created by Thomas on 20/03/2016.
 */
public class AntlrCPPListener extends CPP14BaseListener implements AntlrListener
{
    private BlockFactory blockParser;

    protected AntlrCPPListener()
    {
        super();
    }

    public AntlrCPPListener(BlockFactory blockParser)
    {
        super();

        this.blockParser = blockParser;
    }
}
