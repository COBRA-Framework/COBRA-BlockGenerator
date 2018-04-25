package be.uantwerpen.idlab.cobra.common.models.blocks;

import be.uantwerpen.idlab.cobra.common.models.CodeSegment;

import java.util.Vector;

public class AbstractionBlock extends BasicBlock implements Block
{
    protected Vector<Block> abstractedBlocks;

    protected AbstractionBlock()
    {
        super();
        this.abstractedBlocks = new Vector<Block>();
    }

    protected AbstractionBlock(long id)
    {
        super(id);
        this.abstractedBlocks = new Vector<Block>();
    }

    public AbstractionBlock(BasicBlock block)
    {
        super(block.getId(), block.getCodeSegment());

        this.blockRef = block.getRef();
        this.parentBlock = block.getParentBlock();
        this.childBlocks = block.getChildBlocks();
        this.abstractedBlocks = new Vector<Block>();
    }

    public AbstractionBlock(Vector<Block> abstractedBlocks, CodeSegment codeSegment)
    {
        super(codeSegment);

        this.abstractedBlocks = abstractedBlocks;
    }

    public Vector<Block> getAbstractedBlocks()
    {
        return this.abstractedBlocks;
    }

    public void setAbstractedBlocks(Vector<Block> abstractedBlocks)
    {
        this.abstractedBlocks = abstractedBlocks;
    }

    public void addAbstractedBlock(Block block)
    {
        this.abstractedBlocks.add(block);
    }

    @Override
    public String toString()
    {
        return "[Abstraction block] " + this.codeSegment;
    }
}
