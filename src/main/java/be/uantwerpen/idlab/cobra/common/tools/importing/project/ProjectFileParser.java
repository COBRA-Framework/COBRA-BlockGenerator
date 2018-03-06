package be.uantwerpen.idlab.cobra.common.tools.importing.project;

import be.uantwerpen.idlab.cobra.common.models.ProjectConfig;

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