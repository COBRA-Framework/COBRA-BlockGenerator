package be.uantwerpen.idlab.cobra.common.models.blocks;

import be.uantwerpen.idlab.cobra.common.models.CodeSegment;
import be.uantwerpen.idlab.cobra.common.models.xml.XMLBlock;
import be.uantwerpen.idlab.cobra.common.models.xml.XMLObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Thomas on 18/03/2016.
 */
public class BasicBlock extends XMLBlock implements Block
{
    protected CodeSegment codeSegment;
    protected Block parentBlock;
    protected Vector<Block> childBlocks;

    protected BasicBlock()
    {
        super();

        this.codeSegment = null;
        this.parentBlock = null;
        this.childBlocks = new Vector<Block>();
    }

    public BasicBlock(CodeSegment codeSegment)
    {
        super();

        this.codeSegment = codeSegment;
        this.parentBlock = null;
        this.childBlocks = new Vector<Block>();
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

    public Vector<Block> getDescendantBlocks()
    {
        Vector<Block> descendants = new Vector<Block>();

        for(Block child : this.getChildBlocks())
        {
            descendants.add(child);
            descendants.addAll(child.getDescendantBlocks());
        }

        return descendants;
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

    public int getNumOfDescendants()
    {
        int descendants = getNumOfChildren();

        for(Block child : this.getChildBlocks())
        {
            descendants = descendants + child.getNumOfDescendants();
        }

        return descendants;
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

    public List<XMLObject> getXMLElements()
    {
        List<XMLObject> elements = new ArrayList<XMLObject>();

        //Element: code string
        elements.add(this.codeSegment);

        return elements;
    }
}
