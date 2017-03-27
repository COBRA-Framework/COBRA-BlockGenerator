package be.uantwerpen.idlab.cobra.common.tools.importing.model;

import be.uantwerpen.idlab.cobra.common.models.ProjectConfig;
import be.uantwerpen.idlab.cobra.common.models.blocks.Block;

import java.io.File;
import java.util.List;

/**
 * Created by Thomas on 7/12/2016.
 */
public interface ModelFileParser
{
    String getVersionCompatibility();
    Boolean isCompatible(File modelFile) throws Exception;
    List<Block> parseModelFile(File modelFile, ProjectConfig config) throws Exception;
}
