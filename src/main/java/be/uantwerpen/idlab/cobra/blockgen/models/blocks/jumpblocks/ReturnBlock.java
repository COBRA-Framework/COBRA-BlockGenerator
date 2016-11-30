package be.uantwerpen.idlab.cobra.blockgen.models.blocks.jumpblocks;

import be.uantwerpen.idlab.cobra.blockgen.models.CodeSegment;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.JumpBlock;

/**
 * Created by Thomas on 29/11/2016.
 */
public class ReturnBlock extends JumpBlock
{
    private String returnStatement;

    protected ReturnBlock()
    {
        super();
    }

    public ReturnBlock(String returnStatement, CodeSegment codeSegment)
    {
        super(codeSegment);

        this.returnStatement = returnStatement;
    }

    public String getReturnStatement()
    {
        return this.returnStatement;
    }

    public void setReturnStatement(String returnStatement)
    {
        this.returnStatement = returnStatement;
    }
}
