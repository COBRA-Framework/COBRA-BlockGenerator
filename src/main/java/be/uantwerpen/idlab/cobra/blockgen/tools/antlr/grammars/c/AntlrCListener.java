package be.uantwerpen.idlab.cobra.blockgen.tools.antlr.grammars.c;

import be.uantwerpen.idlab.cobra.blockgen.tools.blocks.BlockFactory;
import be.uantwerpen.idlab.cobra.blockgen.tools.antlr.interfaces.AntlrListener;
import be.uantwerpen.idlab.cobra.blockgen.tools.blocks.CSymbolFactory;
import be.uantwerpen.idlab.cobra.blockgen.tools.blocks.SymbolFactory;
import be.uantwerpen.idlab.cobra.common.models.BlockReference;
import org.antlr.v4.grammar.c.CBaseListener;
import org.antlr.v4.grammar.c.CParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Thomas on 20/03/2016.
 */
public class AntlrCListener extends CBaseListener implements AntlrListener
{
    private BlockFactory blockFactory;
    private SymbolFactory symbolFactory;
    private boolean firstCase = false;

    protected AntlrCListener()
    {
        super();
    }

    public AntlrCListener(BlockFactory blockFactory, SymbolFactory symbolFactory)
    {
        super();

        this.blockFactory = blockFactory;
        this.symbolFactory = symbolFactory;
    }

    @Override
    public void enterFunctionDefinition(CParser.FunctionDefinitionContext ctx)
    {
        int startIndex = ctx.getStart().getStartIndex();
        int endIndex = ctx.declarator().getStop().getStopIndex() + 1;
        String methodName = ctx.declarator().getText().split("\\(")[0];

        blockFactory.createMethodBlock(methodName, startIndex, endIndex, getBlockReference(ctx));
        this.symbolFactory.pushToken(SymbolFactory.TokenType.FUNCTION_DEFINITION, ctx.getText());
    }

    @Override
    public void exitFunctionDefinition(CParser.FunctionDefinitionContext ctx)
    {
        blockFactory.exitCurrentBlock();
        this.symbolFactory.exitScope();
    }

    @Override
    public void enterCompilationUnit(CParser.CompilationUnitContext ctx)
    {
        //New compilation unit
    }

    @Override
    public void exitCompilationUnit(CParser.CompilationUnitContext ctx)
    {
        //Exit compilation unit
    }

    @Override
    public void enterExpressionStatement(CParser.ExpressionStatementContext ctx)
    {
        int startIndex = ctx.getStart().getStartIndex();
        int endIndex = ctx.getStop().getStopIndex() + 1;

        blockFactory.createStatementBlock(startIndex, endIndex, getBlockReference(ctx));
    }

    @Override
    public void enterDeclarationStatement(CParser.DeclarationStatementContext ctx)
    {
        int startIndex = ctx.getStart().getStartIndex();
        int endIndex = ctx.getStop().getStopIndex() + 1;

        blockFactory.createStatementBlock(startIndex, endIndex, getBlockReference(ctx));


        this.symbolFactory.pushToken(SymbolFactory.TokenType.DECLARATION, ctx.getText());
    }

    @Override
    public void enterTypeSpecifier(CParser.TypeSpecifierContext ctx)
    {
       this.symbolFactory.pushToken(SymbolFactory.TokenType.TYPE, ctx.getText());
    }

    @Override public void enterParameterList(CParser.ParameterListContext ctx)
    {
        this.symbolFactory.pushToken(SymbolFactory.TokenType.PARAMETER_LIST, ctx.getText());
    }

    @Override public void enterParameterDeclaration(CParser.ParameterDeclarationContext ctx)
    {
        this.symbolFactory.pushToken(SymbolFactory.TokenType.PARAMETER_DECLARATION, ctx.getText());
    }

    @Override public void enterDirectDeclarator(CParser.DirectDeclaratorContext ctx)
    {
        this.symbolFactory.pushToken(SymbolFactory.TokenType.DIRECT_DECLARATOR, ctx.getText());
    }

    @Override
    public void enterIterationStatement(CParser.IterationStatementContext ctx)
    {
        int startIndex;
        int endIndex;
        boolean isDoWhileIteration;

        if(ctx.getText().trim().split("\n")[0].startsWith("do"))
        {
            startIndex = ctx.getTokens(CParser.While).get(ctx.getTokens(CParser.While).size() - 1).getSymbol().getStartIndex();
            endIndex = ctx.getTokens(CParser.Semi).get(ctx.getTokens(CParser.Semi).size() - 1).getSymbol().getStopIndex() + 1;

            isDoWhileIteration = true;
        }
        else
        {
            startIndex = ctx.getStart().getStartIndex();
            endIndex = getStatementEndIndex(ctx.getTokens(CParser.LeftParen), ctx.getTokens(CParser.RightParen));

            isDoWhileIteration = false;
        }

        blockFactory.createIterationBlock(startIndex, endIndex, isDoWhileIteration, getBlockReference(ctx));
    }

    @Override
    public void exitIterationStatement(CParser.IterationStatementContext ctx)
    {
        blockFactory.exitCurrentBlock();
    }

    @Override
    public void enterSelectionStatement(CParser.SelectionStatementContext ctx)
    {
        int startIndex = ctx.getStart().getStartIndex();
        int endIndex = getStatementEndIndex(ctx.getTokens(CParser.LeftParen), ctx.getTokens(CParser.RightParen));

        blockFactory.createSelectionBlock(startIndex, endIndex, getBlockReference(ctx));

        if(ctx.getText().trim().split("\n")[0].startsWith("switch"))
        {
            firstCase = true;
        }
    }

    @Override
    public void exitSelectionStatement(CParser.SelectionStatementContext ctx)
    {
        if(ctx.getText().trim().split("\n")[0].startsWith("switch"))
        {
            //Exit last case block
            blockFactory.exitCurrentBlock();
        }

        blockFactory.exitCurrentBlock();
    }

    @Override
    public void enterTrueStatement(CParser.TrueStatementContext ctx)
    {
        blockFactory.addBooleanCaseStatement(true, getBlockReference(ctx));
    }

    @Override
    public void exitTrueStatement(CParser.TrueStatementContext ctx)
    {
        blockFactory.exitCurrentBlock();
    }

    @Override
    public void enterFalseStatement(CParser.FalseStatementContext ctx)
    {
        blockFactory.addBooleanCaseStatement(false, getBlockReference(ctx));
    }

    @Override
    public void exitFalseStatement(CParser.FalseStatementContext ctx)
    {
        blockFactory.exitCurrentBlock();
    }

    @Override
    public void enterLabeledStatement(CParser.LabeledStatementContext ctx)
    {
        int startIndex = ctx.getStart().getStartIndex();
        int endIndex = ctx.getTokens(CParser.Colon).get(0).getSymbol().getStopIndex() + 1;

        if(ctx.getText().trim().split("\n")[0].startsWith("case") || ctx.getText().trim().split("\n")[0].startsWith("default"))
        {
            if(!firstCase)
            {
                //Exit previous case statement
                blockFactory.exitCurrentBlock();
            }
            else
            {
                firstCase = false;
            }

            blockFactory.addCaseStatement(startIndex, endIndex, getBlockReference(ctx));
        }
    }

    @Override
    public void enterJumpStatement(CParser.JumpStatementContext ctx)
    {
        int startIndex = ctx.getStart().getStartIndex();
        int endIndex = ctx.getStop().getStopIndex() + 1;

        blockFactory.createJumpBlock(startIndex, endIndex, getBlockReference(ctx));
    }

    private int getStatementEndIndex(List<TerminalNode> leftParenTokens, List<TerminalNode> rightParenTokens)
    {
        int openParenTokens = 0;
        Iterator<TerminalNode> itLeftParen = leftParenTokens.iterator();
        Iterator<TerminalNode> itRightParen = rightParenTokens.iterator();
        TerminalNode nextLeftParen = itLeftParen.next();
        TerminalNode nextRightParen = itRightParen.next();

        do
        {
            if(nextLeftParen == null)
            {
                openParenTokens--;
            }
            else if(nextLeftParen.getSymbol().getStartIndex() < nextRightParen.getSymbol().getStartIndex())
            {
                openParenTokens++;

                if(itLeftParen.hasNext())
                    nextLeftParen = itLeftParen.next();
                else
                    nextLeftParen = null;
            }
            else
            {
                openParenTokens--;

                if(openParenTokens > 0)
                {
                    nextRightParen = itRightParen.next();
                }
            }
        } while(openParenTokens > 0);

        return nextRightParen.getSymbol().getStopIndex() + 1;
    }

    private BlockReference getBlockReference(ParserRuleContext ctx)
    {
        String refString = ctx.getRuleContext().getSourceInterval().a + "-" + ctx.getRuleContext().getSourceInterval().b;
        BlockReference ref = new BlockReference(refString);

        return ref;
    }
}
