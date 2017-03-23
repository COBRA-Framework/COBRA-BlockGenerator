package be.uantwerpen.idlab.cobra.blockgen.tools.interfaces;

import be.uantwerpen.idlab.cobra.common.models.blocks.Block;

import javax.swing.*;

/**
 * Created by Thomas on 1/12/2016.
 */
public interface GraphDisplay
{
    JFrame DisplayGraphFromBlock(Block block) throws Exception;
}
