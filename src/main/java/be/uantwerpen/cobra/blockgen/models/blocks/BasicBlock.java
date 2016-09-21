package be.uantwerpen.cobra.blockgen.models.blocks;

import be.uantwerpen.cobra.blockgen.models.CodeSegment;

import java.util.Vector;

/**
 * Created by Thomas on 18/03/2016.
 */
public class BasicBlock implements Block
{
    protected CodeSegment codeSegment;
    protected Block parentBlock;
    protected Vector<Block> childBlocks;
    protected double wcet;

    protected BasicBlock()
    {
        this.codeSegment = null;
        this.parentBlock = null;
        this.childBlocks = new Vector<Block>();
        this.wcet = 0;
    }

    public BasicBlock(CodeSegment codeSegment)
    {
        this.codeSegment = codeSegment;
        this.parentBlock = null;
        this.childBlocks = new Vector<Block>();
        this.wcet = 0;
    }

    public CodeSegment getCodeSegment()
    {
        return this.codeSegment;
    }

    public void setCodeSegment(CodeSegment codeSegment)
    {
        this.codeSegment = codeSegment;
    }

    public String getCodeString()
    {
        String codeString = new String();

        if(this.codeSegment != null)
        {
            codeString = this.codeSegment.toString();
        }

        if(this.getNumOfChildren() > 0)
        {
            codeString = codeString.concat("\n{\n");

            for(Block block : this.getChildBlocks())
            {
                for(String subString : block.getCodeString().split("\n"))
                {
                    codeString = codeString.concat("\t" + subString + "\n");
                }
            }

            codeString = codeString.concat("}");

        }

        return codeString;
    }

    public Block getParentBlock()
    {
        return this.parentBlock;
    }

    public void setParentBlock(Block parent)
    {
        this.parentBlock = parent;
    }

    public Block getChildBlock(int index)
    {
        return this.childBlocks.get(index);
    }

    public void addChildBlock(int index, Block child)
    {
        child.setParentBlock(this);

        this.childBlocks.add(index, child);
    }

    public void addChildBlock(Block child)
    {
        child.setParentBlock(this);

        this.childBlocks.add(child);
    }

    public void removeChildBlock(int index)
    {
        Block removedChild = this.childBlocks.remove(index);

        removedChild.setParentBlock(null);
    }

    public Vector<Block> getChildBlocks()
    {
        return this.childBlocks;
    }

    public Vector<Block> getLeafs()
    {
        Vector<Block> leafs = new Vector<Block>();

        if(this.getNumOfChildren() > 0)
        {
            for(Block child : this.getChildBlocks())
            {
                leafs.addAll(child.getLeafs());
            }
        }
        else
        {
            leafs.add(this);
        }

        return leafs;
    }

    public int getStartRowNumber()
    {
        return this.codeSegment.getRowNumber(this.codeSegment.getStartIndex());
    }

    public int getEndRowNumber()
    {
        return this.codeSegment.getRowNumber(this.codeSegment.getEndIndex());
    }

    public int getNumOfChildren()
    {
        return this.childBlocks.size();
    }

    @Override
    public String toString()
    {
        return "[Basic block] " + this.codeSegment;
    }

    public String toStringRecursive()
    {
        String string = this.toString();

        for(Block block : this.getChildBlocks())
        {
            for(String subString : block.toStringRecursive().split("\n"))
            {
                string = string.concat("\n\t" + subString);
            }
        }

        return string;
    }

    public void setWCET(double wcet)
    {
        this.wcet = wcet;
    }

    public double getWCET()
    {
        return this.wcet;
    }
}
