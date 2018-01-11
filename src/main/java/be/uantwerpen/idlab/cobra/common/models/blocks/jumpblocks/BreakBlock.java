package be.uantwerpen.idlab.cobra.common.models.blocks.jumpblocks;

import be.uantwerpen.idlab.cobra.common.models.CodeSegment;
import be.uantwerpen.idlab.cobra.common.models.blocks.JumpBlock;

/**
 * Created by Thomas on 29/11/2016.
 */
public class BreakBlock extends JumpBlock
{
    protected BreakBlock()
    {
        super();
    }

    protected BreakBlock(Long id)
    {
        super(id);
    }

    public BreakBlock(CodeSegment codeSegment)
    {
        super(codeSegment);
    }
}
