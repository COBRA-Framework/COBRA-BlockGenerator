package be.uantwerpen.idlab.cobra.common.models.blocks.caseblocks;

import be.uantwerpen.idlab.cobra.common.models.CodeSegment;
import be.uantwerpen.idlab.cobra.common.models.blocks.CaseBlock;

/**
 * Created by Thomas on 1/12/2016.
 */
public class DefaultCaseBlock extends CaseBlock
{
    protected DefaultCaseBlock()
    {
        super();
    }

    protected DefaultCaseBlock(Long id)
    {
        super(id);
    }

    public DefaultCaseBlock(CodeSegment codeSegment)
    {
        super(codeSegment);
    }
}
