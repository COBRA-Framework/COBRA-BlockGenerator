package be.uantwerpen.idlab.cobra.blockgen.models.blocks.caseblocks;

import be.uantwerpen.idlab.cobra.blockgen.models.blocks.CaseBlock;

/**
 * Created by Thomas on 29/11/2016.
 */
public class BooleanCaseBlock extends CaseBlock
{
    private boolean booleanValue;

    protected BooleanCaseBlock()
    {
        super();
    }

    public BooleanCaseBlock(boolean value)
    {
        super();

        this.booleanValue = value;
    }

    public boolean getBooleanValue()
    {
        return this.booleanValue;
    }

    public void setBooleanValue(boolean value)
    {
        this.booleanValue = value;
    }
}
