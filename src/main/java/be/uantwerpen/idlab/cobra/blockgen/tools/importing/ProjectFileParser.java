package be.uantwerpen.idlab.cobra.blockgen.tools.importing;

import be.uantwerpen.idlab.cobra.blockgen.models.ProjectConfig;

import java.io.File;

/**
 * Created by Thomas on 6/12/2016.
 */
public interface ProjectFileParser
{
    String getVersionCompatibility();
    Boolean isCompatible(File projectFile) throws Exception;
    ProjectConfig parseProjectFile(File projectFile) throws Exception;
}
