package be.uantwerpen.idlab.cobra.blockgen.models.blocks.caseblocks;

import be.uantwerpen.idlab.cobra.blockgen.models.blocks.Block;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.CaseBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.xml.XMLElement;
import be.uantwerpen.idlab.cobra.blockgen.models.xml.XMLObject;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<XMLObject> getXMLElements()
    {
        List<XMLObject> elements = new ArrayList<XMLObject>();

        //Element: boolean value
        elements.add(new XMLElement("value", this.booleanValue));

        return elements;
    }

    @Override
    public String getCodeString()
    {
        String codeString = new String();

        //If statement
        if(this.getNumOfChildren() > 0)
        {
            //False statement (else)
            if(!this.booleanValue)
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

        return codeString;
    }

    @Override
    public String toString()
    {
        return "[" + this.booleanValue + "]";
    }
}
