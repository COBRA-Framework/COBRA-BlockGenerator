package be.uantwerpen.idlab.cobra.common.models.blocks.selectionblocks;

import be.uantwerpen.idlab.cobra.common.models.CodeSegment;
import be.uantwerpen.idlab.cobra.common.models.blocks.SelectionBlock;

/**
 * Created by Thomas on 29/11/2016.
 */
public class IfBlock extends SelectionBlock
{
    protected IfBlock()
    {
        super();
    }

    protected IfBlock(long id)
    {
        super(id);
    }

    public IfBlock(String selectionStatement, CodeSegment codeSegment)
    {
        super(codeSegment, selectionStatement);
    }
}
