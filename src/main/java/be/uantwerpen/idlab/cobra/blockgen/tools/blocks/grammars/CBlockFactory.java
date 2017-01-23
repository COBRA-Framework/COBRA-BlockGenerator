package be.uantwerpen.idlab.cobra.blockgen.tools.blocks.grammars;

import be.uantwerpen.idlab.cobra.blockgen.models.CodeFile;
import be.uantwerpen.idlab.cobra.blockgen.models.CodeSegment;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.*;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.caseblocks.BooleanCaseBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.caseblocks.DefaultCaseBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.caseblocks.ValueCaseBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.iterationblocks.DoWhileBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.iterationblocks.ForBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.iterationblocks.WhileBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.jumpblocks.BreakBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.jumpblocks.ContinueBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.jumpblocks.GotoBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.jumpblocks.ReturnBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.selectionblocks.IfBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.selectionblocks.SwitchBlock;
import be.uantwerpen.idlab.cobra.blockgen.tools.blocks.BlockFactory;

/**
 * Created by Thomas on 29/11/2016.
 */
public class CBlockFactory extends BlockFactory
{
    protected CBlockFactory()
    {
        super();
    }

    public CBlockFactory(CodeFile codeFile)
    {
        super(codeFile);
    }

    public void createMethodBlock(String name, int start, int end)
    {
        Block methodBlock = new MethodBlock(name, false, generateCodeSegment(start, end));

        currentBlockPointer = methodBlock;

        blocks.add(methodBlock);
    }

    public void createStatementBlock(int start, int end)
    {
        Block statementBlock = new BasicBlock(generateCodeSegment(start, end));

        if(currentBlockPointer != null)
        {
            currentBlockPointer.addChildBlock(statementBlock);
        }
        else
        {
            //System.err.println("Statement declared outside method. Statement: char#." + start + "-" + end +" will be ignored!");
        }
    }

    public void createIterationBlock(int start, int end, boolean runAtLeastOnce)
    {
        Block iterationBlock = null;
        CodeSegment codeSegment = generateCodeSegment(start, end);

        String codeStream = codeSegment.toString();
        String iterationType = codeStream.trim().toLowerCase();
        String iterationCondition = new String();

        if(iterationType.startsWith("while") && runAtLeastOnce)
        {
            //Do...while-iteration
            try
            {
                iterationCondition = codeStream.substring(codeStream.indexOf('(') + 1, codeStream.lastIndexOf(')')).trim();
            }
            catch(IndexOutOfBoundsException e)
            {
                iterationCondition = "";
            }

            iterationBlock = new DoWhileBlock(iterationCondition, codeSegment);
        }
        else if(iterationType.startsWith("while"))
        {
            //While-iteration
            try
            {
                iterationCondition = codeStream.substring(codeStream.indexOf('(') + 1, codeStream.lastIndexOf(')')).trim();
            }
            catch(IndexOutOfBoundsException e)
            {
                iterationCondition = "";
            }

            iterationBlock = new WhileBlock(iterationCondition, codeSegment);
        }
        else if(iterationType.startsWith("for"))
        {
            //For-iteration
            String initStatement = new String();
            String postStatement = new String();

            try
            {
                initStatement = codeStream.substring(codeStream.indexOf('(') + 1, codeStream.indexOf(';')).trim();
            }
            catch(IndexOutOfBoundsException e)
            {
                initStatement = "";
            }

            try
            {
                iterationCondition = codeStream.substring(codeStream.indexOf(';') + 1, codeStream.lastIndexOf(';')).trim();
            }
            catch(IndexOutOfBoundsException e)
            {
                iterationCondition = "";
            }

            try
            {
                postStatement = codeStream.substring(codeStream.lastIndexOf(';') + 1, codeStream.lastIndexOf(')')).trim();
            }
            catch(IndexOutOfBoundsException e)
            {
                postStatement = "";
            }

            iterationBlock = new ForBlock(initStatement, iterationCondition, postStatement, codeSegment);
        }
        else
        {
            reportFactoryError("Iteration type can not be determined for the statement: (r." + codeSegment.getRowNumber(codeSegment.getStartIndex()) + "-" + codeSegment.getColumnNumber(codeSegment.getStartIndex()) + ") " + codeStream, new Throwable().getStackTrace().toString());
            return;
        }

        currentBlockPointer.addChildBlock(iterationBlock);

        currentBlockPointer = iterationBlock;
    }

    public void createSelectionBlock(int start, int end)
    {
        Block selectionBlock = null;
        CodeSegment codeSegment = generateCodeSegment(start, end);

        String codeStream = codeSegment.toString();
        String selectionType = codeStream.trim().toLowerCase();
        String selectionCondition = new String();

        if(selectionType.startsWith("if"))
        {
            //If...else-selection
            try
            {
                selectionCondition = codeStream.substring(codeStream.indexOf('(') + 1, codeStream.lastIndexOf(')')).trim();
            }
            catch(IndexOutOfBoundsException e)
            {
                selectionCondition = "";
            }

            selectionBlock = new IfBlock(selectionCondition, codeSegment);
        }
        else if(selectionType.startsWith("switch"))
        {
            //Switch-selection
            try
            {
                selectionCondition = codeStream.substring(codeStream.indexOf('(') + 1, codeStream.lastIndexOf(')')).trim();
            }
            catch(IndexOutOfBoundsException e)
            {
                selectionCondition = "";
            }

            selectionBlock = new SwitchBlock(selectionCondition, codeSegment);
        }
        else
        {
            reportFactoryError("Selection type can not be determined for the statement: (r." + codeSegment.getRowNumber(codeSegment.getStartIndex()) + "-" + codeSegment.getColumnNumber(codeSegment.getStartIndex()) + ") " + codeStream, new Throwable().getStackTrace().toString());
            return;
        }

        currentBlockPointer.addChildBlock(selectionBlock);

        currentBlockPointer = selectionBlock;
    }

    public void createJumpBlock(int start, int end)
    {
        Block jumpBlock = null;
        CodeSegment codeSegment = generateCodeSegment(start, end);

        String codeStream = codeSegment.toString();
        String jumpType = codeStream.trim().toLowerCase();

        if(jumpType.startsWith("break"))
        {
            jumpBlock = new BreakBlock(codeSegment);
        }
        else if(jumpType.startsWith("continue"))
        {
            jumpBlock = new ContinueBlock(codeSegment);
        }
        else if(jumpType.startsWith("goto"))
        {
            String jumpLabel = new String();

            try
            {
                jumpLabel = codeStream.substring(codeStream.indexOf("goto") + 4, codeStream.lastIndexOf(';')).trim();
            }
            catch(IndexOutOfBoundsException e)
            {
                jumpLabel = "";
            }

            jumpBlock = new GotoBlock(jumpLabel, codeSegment);
        }
        else if(jumpType.startsWith("return"))
        {
            String returnStatement = new String();

            try
            {
                returnStatement = codeStream.substring(codeStream.indexOf("return") + 6, codeStream.lastIndexOf(';')).trim();
            }
            catch(IndexOutOfBoundsException e)
            {
                returnStatement = "";
            }

            jumpBlock = new ReturnBlock(returnStatement, codeSegment);
        }
        else
        {
            reportFactoryError("Jump type can not be determined for the statement: (r." + codeSegment.getRowNumber(codeSegment.getStartIndex()) + "-" + codeSegment.getColumnNumber(codeSegment.getStartIndex()) + ") " + codeStream, new Throwable().getStackTrace().toString());
            return;
        }

        currentBlockPointer.addChildBlock(jumpBlock);
    }

    public void addBooleanCaseStatement(boolean value)
    {
        Block caseBlock = new BooleanCaseBlock(value);

        currentBlockPointer.addChildBlock(caseBlock);

        currentBlockPointer = caseBlock;
    }

    public void addCaseStatement(int start, int end)
    {
        Block caseBlock = null;
        CodeSegment codeSegment = generateCodeSegment(start, end);

        String codeStream = codeSegment.toString();
        String caseType = codeStream.trim().toLowerCase();

        if(caseType.startsWith("default"))
        {
            caseBlock = new DefaultCaseBlock(codeSegment);
        }
        else if(caseType.startsWith("case"))
        {
            String labelStatement = new String();

            try
            {
                labelStatement = codeStream.substring(codeStream.indexOf("case") + 4, codeStream.lastIndexOf(':')).trim();
            }
            catch(IndexOutOfBoundsException e)
            {
                labelStatement = "";
            }

            caseBlock = new ValueCaseBlock(labelStatement, codeSegment);
        }
        else
        {
            reportFactoryError("Case type can not be determined for the statement: (r." + codeSegment.getRowNumber(codeSegment.getStartIndex()) + "-" + codeSegment.getColumnNumber(codeSegment.getStartIndex()) + ") " + codeStream, new Throwable().getStackTrace().toString());
            return;
        }

        currentBlockPointer.addChildBlock(caseBlock);

        currentBlockPointer = caseBlock;
    }
}
