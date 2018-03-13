package be.uantwerpen.idlab.cobra.common.models.blocks.jumpblocks;

import be.uantwerpen.idlab.cobra.common.models.CodeSegment;
import be.uantwerpen.idlab.cobra.common.models.blocks.JumpBlock;

/**
 * Created by Thomas on 29/11/2016.
 */
public class ContinueBlock extends JumpBlock
{
    protected ContinueBlock()
    {
        super();
    }

    protected ContinueBlock(long id)
    {
        super(id);
    }

    public ContinueBlock(CodeSegment codeSegment)
    {
        super(codeSegment);
    }
}
