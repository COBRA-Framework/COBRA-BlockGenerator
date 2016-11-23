package be.uantwerpen.cobra.blockgen.tools;

import be.uantwerpen.cobra.blockgen.models.CodeFile;
import be.uantwerpen.cobra.blockgen.models.CodeSegment;
import be.uantwerpen.cobra.blockgen.models.blocks.*;

import java.util.Vector;

/**
 * Created by Thomas on 20/03/2016.
 */
public class BlockFactory
{
    private Vector<Block> blocks;
    private CodeFile codeFile;
    private Block currentBlockPointer;

    protected BlockFactory()
    {
        this.blocks = new Vector<Block>();
    }

    public BlockFactory(CodeFile codeFile)
    {
        this.blocks = new Vector<Block>();
        this.codeFile = codeFile;
    }

    public void setCodeFile(CodeFile codeFile)
    {
        this.codeFile = codeFile;
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

    public void createIterationBlock(int start, int end, boolean doWhileStatement)
    {
        Block iterationBlock = new IterationBlock(generateCodeSegment(start, end), doWhileStatement);

        currentBlockPointer.addChildBlock(iterationBlock);

        currentBlockPointer = iterationBlock;
    }

    public void createSelectionBlock(int start, int end)
    {
        Block iterationBlock = new SelectionBlock(generateCodeSegment(start, end));

        currentBlockPointer.addChildBlock(iterationBlock);

        currentBlockPointer = iterationBlock;
    }

    public void createJumpBlock(int start, int end)
    {
        Block jumpBlock = new JumpBlock(generateCodeSegment(start, end));

        currentBlockPointer.addChildBlock(jumpBlock);
    }

    public void addCaseStatement(boolean ifStatement)
    {
        Block caseBlock = new CaseBlock(ifStatement);

        currentBlockPointer.addChildBlock(caseBlock);

        currentBlockPointer = caseBlock;
    }

    public void addCaseStatement(int start, int end)
    {
        Block caseBlock = new CaseBlock(generateCodeSegment(start, end));

        currentBlockPointer.addChildBlock(caseBlock);

        currentBlockPointer = caseBlock;
    }

    public void exitCurrentBlock()
    {
        currentBlockPointer = currentBlockPointer.getParentBlock();
    }

    public void createMethodBlock(String name, int start, int end)
    {
        Block methodBlock = new MethodBlock(name, generateCodeSegment(start, end));

        currentBlockPointer = methodBlock;

        blocks.add(methodBlock);
    }

    public Vector<Block> getGeneratedBlocks()
    {
        return this.blocks;
    }

    private CodeSegment generateCodeSegment(int startIndex, int endIndex)
    {
        return new CodeSegment(this.codeFile, startIndex, endIndex);
    }
}
