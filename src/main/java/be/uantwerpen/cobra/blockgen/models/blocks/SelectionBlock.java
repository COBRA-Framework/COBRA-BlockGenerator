package be.uantwerpen.cobra.blockgen.models.blocks;

import be.uantwerpen.cobra.blockgen.models.CodeSegment;

import java.util.Vector;

/**
 * Created by Thomas on 22/03/2016.
 */
public class SelectionBlock extends BasicBlock implements Block
{
    protected SelectionBlock()
    {
        super();
    }

    public SelectionBlock(CodeSegment codeSegment)
    {
        super(codeSegment);
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

    /*
    @Override
    public String toStringRecursive()
    {
        String string = this.toString();

        //True statement
        List<String> subStrings = Arrays.asList(this.getChildBlocks().get(1).toString().split("\n"));
        Iterator<String> it = subStrings.iterator();
        int i = 0;

        string = string.concat("\n" + "[TRUE]");

        while(it.hasNext())
        {
            String subString = it.next();

            if(i == 0)
            {
                //Skip basic group box of true statement
            }
            else
            {
                string = string.concat("\n" + subString);
            }

            i++;
        }

        //False statement
        subStrings = Arrays.asList(this.getChildBlocks().get(0).toString().split("\n"));
        it = subStrings.iterator();
        i = 0;

        if(this.getChildBlocks().get(0).getNumOfChildren() > 0)
        {
            string = string.concat("\n" + "[FALSE]");

            while(it.hasNext())
            {
                String subString = it.next();

                if(i == 0)
                {
                    //Skip basic group box of false statement
                }
                else
                {
                    string = string.concat("\n" + subString);
                }

                i++;
            }
        }

        return string;
    }*/
}
