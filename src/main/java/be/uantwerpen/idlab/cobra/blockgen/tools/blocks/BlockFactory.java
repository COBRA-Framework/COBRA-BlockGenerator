package be.uantwerpen.idlab.cobra.blockgen.tools.blocks;

import be.uantwerpen.idlab.cobra.common.models.BlockReference;
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

    protected BlockFactory(CodeFile codeFile)
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

    public abstract Block createStatementBlock(int start, int end, BlockReference ref);

    public abstract Block createIterationBlock(int start, int end, boolean runAtLeastOnce, BlockReference ref);

    public abstract Block createSelectionBlock(int start, int end, BlockReference ref);

    public abstract Block createJumpBlock(int start, int end, BlockReference ref);

    public abstract Block addBooleanCaseStatement(boolean value, BlockReference ref);

    public abstract Block addCaseStatement(int start, int end, BlockReference ref);

    public abstract Block createMethodBlock(String name, int start, int end, BlockReference ref);

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
