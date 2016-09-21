package be.uantwerpen.cobra.blockgen.models.blocks;

import be.uantwerpen.cobra.blockgen.models.CodeSegment;

/**
 * Created by Thomas on 02/05/2016.
 */
public class JumpBlock extends BasicBlock implements Block
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
