package be.uantwerpen.idlab.cobra.blockgen.models.blocks;

import be.uantwerpen.idlab.cobra.blockgen.models.CodeSegment;

/**
 * Created by Thomas on 12/04/2016.
 */
public abstract class CaseBlock extends BasicBlock implements Block
{
    protected CaseBlock()
    {
        super();
    }

    public CaseBlock(CodeSegment codeSegment)
    {
        super(codeSegment);
    }

    public CaseBlock(boolean ifSelection)
    {
        super();
    }

    @Override
    public String getCodeString()
    {
        String codeString = new String();

        codeString = super.getCodeString();

        return codeString;
    }

    @Override
    public String toString()
    {
        return "[Case block] " + this.codeSegment;
    }
}
