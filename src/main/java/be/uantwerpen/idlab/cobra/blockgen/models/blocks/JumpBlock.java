package be.uantwerpen.idlab.cobra.blockgen.models.blocks;

import be.uantwerpen.idlab.cobra.blockgen.models.CodeSegment;

/**
 * Created by Thomas on 02/05/2016.
 */
public abstract class JumpBlock extends BasicBlock implements Block
{
    protected JumpBlock()
    {
        super();
    }

    public JumpBlock(CodeSegment codeSegment)
    {
        super(codeSegment);
    }

    @Override
    public String toString()
    {
        return "[Jump block] " + this.codeSegment;
    }
}