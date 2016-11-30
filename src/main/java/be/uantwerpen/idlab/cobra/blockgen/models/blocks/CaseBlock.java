package be.uantwerpen.idlab.cobra.blockgen.models.blocks;

import be.uantwerpen.idlab.cobra.blockgen.models.CodeSegment;

/**
 * Created by Thomas on 12/04/2016.
 */
public abstract class CaseBlock extends BasicBlock implements Block
{
    private boolean ifSelection;

    protected CaseBlock()
    {
        super();

        this.ifSelection = false;
    }

    public CaseBlock(CodeSegment codeSegment)
    {
        super(codeSegment);

        this.ifSelection = false;
    }

    public CaseBlock(boolean ifSelection)
    {
        super();

        this.ifSelection = ifSelection;
    }

    public void setIfSelectionValue(boolean ifSelection)
    {
        this.ifSelection = ifSelection;
    }

    public boolean getIfSelectionValue()
    {
        return this.ifSelection;
    }

    @Override
    public String getCodeString()
    {
        String codeString = new String();

        if(this.codeSegment != null)
        {
            //Case statement
            codeString = super.getCodeString();
        }
        else
        {
            //If statement
            if(this.getNumOfChildren() > 0)
            {
                //False statement (else)
                if(!this.ifSelection)
                {
                    codeString = codeString.concat("}\nelse\n{\n");
                }

                for(Block block : this.getChildBlocks())
                {
                    for(String subString : block.getCodeString().split("\n"))
                    {
                        codeString = codeString.concat("\t" + subString + "\n");
                    }
                }
            }
        }

        return codeString;
    }

    @Override
    public String toString()
    {
        if(this.codeSegment != null)
        {
            return "[Case block] " + this.codeSegment;
        }
        else
        {
            return "[" + this.ifSelection + "]";
        }
    }
}
