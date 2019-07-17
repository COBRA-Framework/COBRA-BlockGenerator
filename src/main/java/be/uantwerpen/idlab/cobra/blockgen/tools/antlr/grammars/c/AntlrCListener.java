package be.uantwerpen.idlab.cobra.blockgen.tools.antlr.grammars.c;

import be.uantwerpen.idlab.cobra.blockgen.tools.blocks.BlockFactory;
import be.uantwerpen.idlab.cobra.blockgen.tools.antlr.interfaces.AntlrListener;
import be.uantwerpen.idlab.cobra.blockgen.tools.symbols.SymbolFactory;
import be.uantwerpen.idlab.cobra.common.models.BlockReference;
import be.uantwerpen.idlab.cobra.common.models.blocks.Block;
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
        String methodName = ctx.declarator().directDeclarator().getText().split("\\(")[0];
        String returnType;

        if(ctx.declarationSpecifiers() != null)
        {
            returnType = ctx.declarationSpecifiers().getText();

            if(ctx.declarator().pointer() != null)
            {
                returnType += ctx.declarator().pointer().getText();
            }
        }
        else
        {
            returnType = "void";
        }

        Block block = blockFactory.createMethodBlock(methodName, startIndex, endIndex, getBlockReference(ctx));

        symbolFactory.enterFunctionScope(block.getId(), methodName, returnType);
    }

    @Override
    public void exitFunctionDefinition(CParser.FunctionDefinitionContext ctx)
    {
        blockFactory.exitCurrentBlock();
        symbolFactory.exitFunctionScope();
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

        Block block = blockFactory.createStatementBlock(startIndex, endIndex, getBlockReference(ctx));
    }

    @Override
    public void enterDeclarationStatement(CParser.DeclarationStatementContext ctx)
    {
        int startIndex = ctx.getStart().getStartIndex();
        int endIndex = ctx.getStop().getStopIndex() + 1;

        Block block = blockFactory.createStatementBlock(startIndex, endIndex, getBlockReference(ctx));
    }

    @Override
    public void enterInitializer(CParser.InitializerContext ctx)
    {
        if(ctx.initializerList() != null)
        {
            symbolFactory.enterBlockScope(ctx.start.getStartIndex(), ctx.stop.getStopIndex());
        }
    }

    @Override
    public void exitInitializer(CParser.InitializerContext ctx)
    {
        if(ctx.initializerList() != null)
        {
            symbolFactory.exitBlockScope(ctx.start.getStartIndex(), ctx.stop.getStopIndex());
        }
    }

    @Override
    public void enterCompoundStatement(CParser.CompoundStatementContext ctx)
    {
        symbolFactory.enterBlockScope(ctx.start.getStartIndex(), ctx.stop.getStopIndex());
    }

    @Override
    public void exitCompoundStatement(CParser.CompoundStatementContext ctx)
    {
        symbolFactory.exitBlockScope(ctx.start.getStartIndex(), ctx.stop.getStopIndex());
    }

    @Override
    public void enterDeclaration(CParser.DeclarationContext ctx)
    {
        String type = "";

        if(ctx.declarationSpecifiers() != null)
        {
            if(ctx.initDeclaratorList() != null)
            {
                //Check for function prototypes
                if(ctx.initDeclaratorList().getText().contains("(") && !ctx.initDeclaratorList().getText().contains("="))
                {
                    //Skip function prototypes
                    return;
                }

                for(CParser.DeclarationSpecifierContext declarationSpecifier : ctx.declarationSpecifiers().declarationSpecifier())
                {
                    type += declarationSpecifier.getText() + " ";
                }

                parseDeclaratorList(ctx.initDeclaratorList(), type.trim());
            }
            else
            {
                //Fix single variable declaration without initialisation detection
                for(int i = 0; i < ctx.declarationSpecifiers().declarationSpecifier().size() - 1; i++)
                {
                    type += ctx.declarationSpecifiers().declarationSpecifier(i).getText() + " ";
                }

                int nameIndex = ctx.declarationSpecifiers().declarationSpecifier().size() - 1;
                String name = ctx.declarationSpecifiers().declarationSpecifier(nameIndex).getText();

                symbolFactory.addVariableSymbol(type.trim(), name);
            }
        }
    }

    private void parseDeclaratorList(CParser.InitDeclaratorListContext ctx, String type)
    {
        if(ctx.initDeclaratorList() != null)
        {
            parseDeclaratorList(ctx.initDeclaratorList(), type);
        }

        CParser.DeclaratorContext variableDeclarator = ctx.initDeclarator().declarator();

        String variableName = variableDeclarator.directDeclarator().getText().split("\\[", 2)[0];
        String variableType = type;

        if(variableDeclarator.pointer() != null)
        {
            variableType += variableDeclarator.pointer().getText();
        }

        if(variableDeclarator.getText().contains("["))
        {
            int size = -1;

            String sizeString = variableDeclarator.getText().split("\\[", 2)[1];
            sizeString = sizeString.split("\\]", 2)[0];

            try
            {
                size = Integer.parseInt(sizeString);
            }
            catch(NumberFormatException e)
            {
                //Array size not defined, leaving -1
            }

            symbolFactory.addArraySymbol(variableType, variableName, size);
        }
        else
        {
            symbolFactory.addVariableSymbol(variableType, variableName);
        }
    }

    @Override
    public void enterParameterDeclaration(CParser.ParameterDeclarationContext ctx)
    {
        String type = "";
        String identifier;

        if(ctx.declarationSpecifiers() != null)
        {
            for(CParser.DeclarationSpecifierContext declarationSpecifier : ctx.declarationSpecifiers().declarationSpecifier())
            {
                type += declarationSpecifier.getText() + " ";
            }

            type = type.trim();
            identifier = ctx.declarator().directDeclarator().getText().split("\\[", 2)[0];

            if(ctx.declarator().pointer() != null)
            {
                type += ctx.declarator().pointer().getText();
            }

            if(ctx.declarator().getText().contains("["))
            {
                int size = -1;

                String sizeString = ctx.declarator().getText().split("\\[", 2)[1];
                sizeString = sizeString.split("\\]", 2)[0];

                try
                {
                    size = Integer.parseInt(sizeString);
                }
                catch(NumberFormatException e)
                {
                    //Array size not defined, leaving -1
                }

                symbolFactory.addParameterArraySymbol(type, identifier, size);
            }
            else
            {
                symbolFactory.addParameterVariableSymbol(type, identifier);
            }
        }
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

        Block block = blockFactory.createIterationBlock(startIndex, endIndex, isDoWhileIteration, getBlockReference(ctx));
        symbolFactory.enterStatementScope(block.getId(), "Iteration statement");
    }

    @Override
    public void exitIterationStatement(CParser.IterationStatementContext ctx)
    {
        blockFactory.exitCurrentBlock();
        symbolFactory.exitScope();
    }

    @Override
    public void enterSelectionStatement(CParser.SelectionStatementContext ctx)
    {
        int startIndex = ctx.getStart().getStartIndex();
        int endIndex = getStatementEndIndex(ctx.getTokens(CParser.LeftParen), ctx.getTokens(CParser.RightParen));

        Block block = blockFactory.createSelectionBlock(startIndex, endIndex, getBlockReference(ctx));
        symbolFactory.enterStatementScope(block.getId(), "Selection statement");

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
        symbolFactory.exitScope();
    }

    @Override
    public void enterTrueStatement(CParser.TrueStatementContext ctx)
    {
        Block block = blockFactory.addBooleanCaseStatement(true, getBlockReference(ctx));
        symbolFactory.enterStatementScope(block.getId(), "True statement");
    }

    @Override
    public void exitTrueStatement(CParser.TrueStatementContext ctx)
    {
        blockFactory.exitCurrentBlock();
        symbolFactory.exitScope();
    }

    @Override
    public void enterFalseStatement(CParser.FalseStatementContext ctx)
    {
        Block block = blockFactory.addBooleanCaseStatement(false, getBlockReference(ctx));
        symbolFactory.enterStatementScope(block.getId(), "False statement");
    }

    @Override
    public void exitFalseStatement(CParser.FalseStatementContext ctx)
    {
        blockFactory.exitCurrentBlock();
        symbolFactory.exitScope();
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
                {
                    nextLeftParen = itLeftParen.next();
                }
                else
                {
                    nextLeftParen = null;
                }
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
