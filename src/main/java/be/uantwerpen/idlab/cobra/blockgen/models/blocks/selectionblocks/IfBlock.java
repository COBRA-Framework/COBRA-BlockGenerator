package be.uantwerpen.idlab.cobra.blockgen.models.blocks.selectionblocks;

import be.uantwerpen.idlab.cobra.blockgen.models.CodeSegment;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.SelectionBlock;

/**
 * Created by Thomas on 29/11/2016.
 */
public class IfBlock extends SelectionBlock
{
    protected IfBlock()
    {
        super();
    }

    public IfBlock(String selectionStatement, CodeSegment codeSegment)
    {
        super(codeSegment, selectionStatement);
    }
}
