package be.uantwerpen.idlab.cobra.blockgen.tools.importing;

import be.uantwerpen.idlab.cobra.common.models.ProjectConfig;
import be.uantwerpen.idlab.cobra.blockgen.tools.importing.versions.ProjectFileParserV1;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Thomas on 6/12/2016.
 */
public class ProjectFileManager
{
    private List<ProjectFileParser> projectParsers;

    public ProjectFileManager()
    {
        projectParsers = new ArrayList<ProjectFileParser>();

        //Add project parser versions
        projectParsers.add(new ProjectFileParserV1());
    }

    public ProjectConfig parseProjectConfig(String fileLocation) throws Exception
    {
        ProjectConfig projectConfig = null;
        ProjectFileParser parser = null;

        File projectFile = new File(fileLocation);

        if(!projectFile.exists() || !projectFile.isFile())
        {
            throw new Exception("The provided path is not a project configuration file! Input:" + fileLocation);
        }

        boolean found = false;
        Iterator<ProjectFileParser> it = projectParsers.iterator();
        while(it.hasNext() && !found)
        {
            parser = it.next();

            try
            {
                if(parser.isCompatible(projectFile))
                {
                    found = true;
                }
            }
            catch(Exception e)
            {
                //Parser is not compatible or project file is corrupted
                continue;
            }
        }

        if(!found)
        {
            throw new Exception("The provided project file is not compatible with this application version or the file is corrupted!");
        }

        return parser.parseProjectFile(projectFile);
    }
}
