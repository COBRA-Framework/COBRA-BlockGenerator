package be.uantwerpen.idlab.cobra.common.models.blocks.selectionblocks;

import be.uantwerpen.idlab.cobra.common.models.CodeSegment;
import be.uantwerpen.idlab.cobra.common.models.blocks.SelectionBlock;

/**
 * Created by Thomas on 29/11/2016.
 */
public class SwitchBlock extends SelectionBlock
{
    protected SwitchBlock()
    {
        super();
    }

    protected SwitchBlock(Long id)
    {
        super(id);
    }

    public SwitchBlock(String selectionStatement, CodeSegment codeSegment)
    {
        super(codeSegment, selectionStatement);
    }
}
