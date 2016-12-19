package be.uantwerpen.idlab.cobra.blockgen.tools.antlr.interfaces;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;

/**
 * Created by Thomas on 20/03/2016.
 */
public interface AntlrParser
{
    ParserRuleContext getRootNode() throws RecognitionException;
    void addErrorListener(BaseErrorListener errorListener);
}
