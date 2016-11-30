package be.uantwerpen.idlab.cobra.blockgen.models.blocks.caseblocks;

import be.uantwerpen.idlab.cobra.blockgen.models.CodeSegment;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.CaseBlock;

/**
 * Created by Thomas on 29/11/2016.
 */
public class ValueCaseBlock extends CaseBlock
{
    private String labelValue;

    protected ValueCaseBlock()
    {
        super();
    }

    public ValueCaseBlock(String value, CodeSegment codeSegment)
    {
        super(codeSegment);

        this.labelValue = value;
    }

    public String getLabelValue()
    {
        return this.labelValue;
    }

    public void setLabelValue(String value)
    {
        this.labelValue = value;
    }
}
