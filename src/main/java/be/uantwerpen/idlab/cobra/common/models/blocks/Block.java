package be.uantwerpen.idlab.cobra.common.models.blocks;

import be.uantwerpen.idlab.cobra.common.models.CodeSegment;

import java.util.Vector;

/**
 * Created by Thomas on 18/03/2016.
 */
public interface Block
{
    String getCodeString();

    Block getParentBlock();
    void setParentBlock(Block parent);

    Block getChildBlock(int index);
    void addChildBlock(int index, Block child);
    void addChildBlock(Block child);
    void removeChildBlock(int index);
    Vector<Block> getChildBlocks();
    Vector<Block> getDescendantBlocks();
    Vector<Block> getLeafs();
    int getNumOfChildren();
    int getNumOfDescendants();
    String toStringRecursive();
    CodeSegment getCodeSegment();
    int getStartRowNumber();
    int getEndRowNumber();
}
