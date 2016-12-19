package be.uantwerpen.idlab.cobra.blockgen.models.blocks.selectionblocks;

import be.uantwerpen.idlab.cobra.blockgen.models.CodeSegment;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.SelectionBlock;

/**
 * Created by Thomas on 29/11/2016.
 */
public class SwitchBlock extends SelectionBlock
{
    protected SwitchBlock()
    {
        super();
    }

    public SwitchBlock(String selectionStatement, CodeSegment codeSegment)
    {
        super(codeSegment, selectionStatement);
    }
}
