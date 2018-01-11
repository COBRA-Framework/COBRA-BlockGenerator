package be.uantwerpen.idlab.cobra.blockgen.tools.blocks;

import be.uantwerpen.idlab.cobra.common.models.CodeFile;
import be.uantwerpen.idlab.cobra.common.models.CodeSegment;
import be.uantwerpen.idlab.cobra.common.models.blocks.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Thomas on 20/03/2016.
 */
public abstract class BlockFactory
{
    protected Vector<Block> blocks;
    protected CodeFile codeFile;
    protected Block currentBlockPointer;
    protected List<FactoryErrorListener> errorListeners;
    private static long blockId;

    protected BlockFactory()
    {
        this.blockId = 0;
        this.blocks = new Vector<Block>();
        this.errorListeners = new ArrayList<FactoryErrorListener>();
    }

    public BlockFactory(CodeFile codeFile)
    {
        this();
        this.codeFile = codeFile;
    }

    public static long getNextBlockId()
    {
        long id = blockId;

        //Increment block id
        blockId++;

        return id;
    }

    public void setCodeFile(CodeFile codeFile)
    {
        this.codeFile = codeFile;
    }

    public abstract void createStatementBlock(int start, int end);

    public abstract void createIterationBlock(int start, int end, boolean runAtLeastOnce);

    public abstract void createSelectionBlock(int start, int end);

    public abstract void createJumpBlock(int start, int end);

    public abstract void addBooleanCaseStatement(boolean value);

    public abstract void addCaseStatement(int start, int end);

    public abstract void createMethodBlock(String name, int start, int end);

    public void exitCurrentBlock()
    {
        try
        {
            currentBlockPointer = currentBlockPointer.getParentBlock();
        }
        catch(NullPointerException e)
        {
            reportFactoryError("Can not exit current block. Current block is null!", e.getMessage());
        }
    }

    public Vector<Block> getGeneratedBlocks()
    {
        return this.blocks;
    }

    protected CodeSegment generateCodeSegment(int startIndex, int endIndex)
    {
        return new CodeSegment(this.codeFile, startIndex, endIndex);
    }

    protected void reportFactoryError(String message, String stacktrace)
    {
        for(FactoryErrorListener errorListener : errorListeners)
        {
            errorListener.report(message, stacktrace);
        }
    }

    public void addErrorListener(FactoryErrorListener errorListener)
    {
        errorListeners.add(errorListener);
    }

    public void removeErrorListener(FactoryErrorListener errorListener)
    {
        errorListeners.remove(errorListener);
    }

    public List<FactoryErrorListener> getErrorListeners()
    {
        return this.errorListeners;
    }
}
