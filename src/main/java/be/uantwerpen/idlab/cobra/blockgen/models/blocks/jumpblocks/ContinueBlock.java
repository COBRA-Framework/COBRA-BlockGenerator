package be.uantwerpen.idlab.cobra.blockgen.models.blocks.jumpblocks;

import be.uantwerpen.idlab.cobra.blockgen.models.CodeSegment;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.JumpBlock;

/**
 * Created by Thomas on 29/11/2016.
 */
public class ContinueBlock extends JumpBlock
{
    protected ContinueBlock()
    {
        super();
    }

    public ContinueBlock(CodeSegment codeSegment)
    {
        super(codeSegment);
    }
}
