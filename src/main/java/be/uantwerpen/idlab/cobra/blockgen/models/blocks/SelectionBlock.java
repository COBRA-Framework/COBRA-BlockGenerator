package be.uantwerpen.idlab.cobra.blockgen.models.blocks;

import be.uantwerpen.idlab.cobra.blockgen.models.CodeSegment;
import be.uantwerpen.idlab.cobra.blockgen.models.xml.XMLElement;
import be.uantwerpen.idlab.cobra.blockgen.models.xml.XMLObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Thomas on 22/03/2016.
 */
public abstract class SelectionBlock extends BasicBlock implements Block
{
    protected String selectionStatement;

    protected SelectionBlock()
    {
        super();
    }

    public SelectionBlock(CodeSegment codeSegment, String selectionStatement)
    {
        super(codeSegment);

        this.selectionStatement = selectionStatement;
    }

    public String getSelectionStatement()
    {
        return selectionStatement;
    }

    public void setSelectionStatement(String selectionStatement)
    {
        this.selectionStatement = selectionStatement;
    }

    @Override
    public Vector<Block> getLeafs()
    {
        Vector<Block> leafs = new Vector<Block>();

        if(this.getNumOfChildren() > 0)
        {
            for(Block caseBlocks : this.getChildBlocks())
            {
                for(Block child : caseBlocks.getChildBlocks())
                {
                    leafs.addAll(child.getLeafs());
                }
            }
        }
        else
        {
            leafs.add(this);
        }

        return leafs;
    }

    @Override
    public Vector<Block> getDescendantBlocks()
    {
        Vector<Block> descendants = new Vector<Block>();

        for(Block child : this.getChildBlocks())
        {
            //Don't add case blocks as descendant blocks
            descendants.addAll(child.getDescendantBlocks());
        }

        return descendants;
    }

    @Override
    public int getNumOfDescendants()
    {
        int descendants = 0;

        for(Block child : this.getChildBlocks())
        {
            descendants = descendants + child.getNumOfDescendants();
        }

        return descendants;
    }

    @Override
    public String getCodeString()
    {
        String codeString = new String();

        if(this.codeSegment != null)
        {
            codeString = this.codeSegment.toString();
        }

        if(this.getNumOfChildren() > 0)
        {
            codeString = codeString.concat("\n{\n");

            for(Block block : this.getChildBlocks())
            {
                for(String subString : block.getCodeString().split("\n"))
                {
                    if(block.getCodeSegment() != null)
                    {
                        codeString = codeString.concat("\t" + subString + "\n");
                    }
                    else
                    {
                        codeString = codeString.concat(subString + "\n");
                    }
                }
            }

            codeString = codeString.concat("}");

        }

        return codeString;
    }

    @Override
    public String toString()
    {
        return "[Selection block] " + this.codeSegment.toString();
    }

    @Override
    public List<XMLObject> getXMLElements()
    {
        List<XMLObject> elements = new ArrayList<XMLObject>();

        //Element: selection statement
        elements.add(new XMLElement("selection_statement", this.selectionStatement));
        elements.addAll(super.getXMLElements());

        return elements;
    }
}
