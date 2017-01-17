package be.uantwerpen.idlab.cobra.blockgen.tools.importing.versions;

import be.uantwerpen.idlab.cobra.blockgen.models.Grammar;
import be.uantwerpen.idlab.cobra.blockgen.models.ProjectConfig;
import be.uantwerpen.idlab.cobra.blockgen.models.SourceFile;
import be.uantwerpen.idlab.cobra.blockgen.tools.importing.ProjectFileParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Thomas on 6/12/2016.
 */
public class ProjectFileParserV1 implements ProjectFileParser
{
    private final static String VERSION = "1.0";

    public ProjectFileParserV1()
    {

    }

    public String getVersionCompatibility()
    {
        return VERSION;
    }

    //TODO: improve parsing with XML DOM Parser
    public Boolean isCompatible(File projectFile) throws Exception
    {
        boolean compatible = false;
        BufferedReader buffReader = null;

        try
        {
            FileReader reader = new FileReader(projectFile);
            buffReader = new BufferedReader(reader);
        }
        catch(FileNotFoundException e)
        {
            throw new Exception("Could not read file: " + projectFile.getName(), e);
        }

        try
        {
            String line = buffReader.readLine();
            boolean found = false;

            while(!found && line != null)
            {
                if(line.trim().matches("<version>\\d+.\\d+</version>"))
                {
                    found = true;
                    String versionString = line.split("<version>", 2)[1];
                    versionString = versionString.split("</version>", 2)[0];

                    if(versionString.trim().equals(VERSION))
                    {
                        compatible = true;
                    }
                }

                line = buffReader.readLine();
            }

            buffReader.close();
        }
        catch(Exception e)
        {
            throw new Exception("Could not determine version compatibility for the file: " + projectFile.getName(), e);
        }

        return compatible;
    }

    //TODO: improve parsing with XML DOM Parser
    public ProjectConfig parseProjectFile(File projectFile) throws Exception
    {
        ProjectConfig config;
        String name = null;
        String version = null;
        List<SourceFile> sourceFiles = new ArrayList<SourceFile>();
        Grammar grammar = Grammar.UNKNOWN;

        BufferedReader buffReader = null;

        try
        {
            FileReader reader = new FileReader(projectFile);
            buffReader = new BufferedReader(reader);
        }
        catch(FileNotFoundException e)
        {
            throw new Exception("Could not read file: " + projectFile.getName(), e);
        }

        try
        {
            String line = buffReader.readLine();

            while(line != null)
            {
                //Skip comments until project tag
                if(line.trim().startsWith("<project>"))
                {
                    line = buffReader.readLine();
                    break;
                }

                line = buffReader.readLine();
            }

            while(line != null)
            {
                if(line.trim().startsWith("</project>"))
                {
                    //End of project tag detected
                    break;
                }
                else if(line.trim().startsWith("<version>"))
                {
                    //Version tag detected
                    try
                    {
                        version = line.split("<version>", 2)[1];
                        version = version.split("</version>", 2)[0];
                    }
                    catch(Exception e)
                    {
                        throw new Exception("Could not parse version field!", e);
                    }
                }
                else if(line.trim().startsWith("<name>"))
                {
                    //Name tag detected
                    try
                    {
                        name = line.split("<name>", 2)[1];
                        name = name.split("</name>", 2)[0];
                    }
                    catch(Exception e)
                    {
                        throw new Exception("Could not parse name field!", e);
                    }
                }
                else if(line.trim().startsWith("<grammar>"))
                {
                    //Grammar tag detected
                    String grammarString;

                    try
                    {
                        grammarString = line.split("<grammar>", 2)[1];
                        grammarString = grammarString.split("</grammar>", 2)[0];
                    }
                    catch(Exception e)
                    {
                        throw new Exception("Could not parse grammar field!", e);
                    }

                    try
                    {
                        grammar = Grammar.valueOf(grammarString);
                    }
                    catch(IllegalArgumentException e)
                    {
                        throw new Exception("Grammar: " + grammarString + " is not supported!", e);
                    }
                }
                else if(line.trim().startsWith("<sources>"))
                {
                    String projectFileLocation = projectFile.getParent();

                    if(projectFileLocation == null)
                    {
                        projectFileLocation = ".";
                    }

                    sourceFiles = parseSourceFiles(buffReader, projectFileLocation + System.getProperty("file.separator"));
                }

                line = buffReader.readLine();
            }

            buffReader.close();
        }
        catch(Exception e)
        {
            throw new Exception("Could not parse the project file: " + projectFile.getName() + " (" + e.getClass().getName() + ": " + e.getMessage() + ")", e);
        }

        if(name == null || version == null || grammar == Grammar.UNKNOWN)
        {
            throw new Exception("Configuration file is corrupted! Missing or invalid configuration fields detected...");
        }

        config = new ProjectConfig(name, version, grammar, sourceFiles);

        return config;
    }

    private String mapRelativePathToWorkingDirectory(File sourceFile, String configLocation) throws Exception
    {
        String remappedPath;

        if(!sourceFile.isAbsolute())
        {
            try
            {
                String splitRegex = Pattern.quote(System.getProperty("file.separator"));
                remappedPath = sourceFile.getPath().split(splitRegex, 2)[1];
                remappedPath = configLocation.concat(remappedPath);
            }
            catch(Exception e)
            {
                throw new Exception("Could not remap the source file location to the current working directory!", e);
            }
        }
        else
        {
            remappedPath = sourceFile.getPath();
        }

        return remappedPath;
    }

    //TODO: improve parsing with XML DOM Parser
    private List<SourceFile> parseSourceFiles(BufferedReader reader, String projectFileLocation) throws Exception
    {
        List<SourceFile> sourceFiles = new ArrayList<SourceFile>();
        String line = reader.readLine();

        while(line != null)
        {
            if(line.trim().startsWith("</sources>"))
            {
                //End of sources tag detected
                break;
            }
            else if(line.trim().startsWith("<source"))
            {
                //Source tag detected
                String sourceLocation = "";
                long sourceId = -1L;

                //Extract source id attribute
                try
                {
                    String sourceIdString = line.split("id=\"", 2)[1];
                    sourceIdString = sourceIdString.split("\"", 2)[0];

                    sourceId = Long.parseLong(sourceIdString.trim());
                }
                catch(Exception e)
                {
                    throw new Exception("Could not parse id attribute!", e);
                }

                line = reader.readLine();

                while(line != null)
                {
                    if(line.trim().startsWith("</source>"))
                    {
                        //End of source tag detected
                        sourceFiles.add(new SourceFile(sourceId, mapRelativePathToWorkingDirectory(new File(sourceLocation), projectFileLocation)));

                        break;
                    }
                    else if(line.trim().startsWith("<location>"))
                    {
                        //Location tag detected
                        try
                        {
                            sourceLocation = line.split("<location>", 2)[1];
                            sourceLocation = sourceLocation.split("</location>", 2)[0];
                        }
                        catch(Exception e)
                        {
                            throw new Exception("Could not parse location field!");
                        }
                    }

                    line = reader.readLine();
                }
            }

            line = reader.readLine();
        }

        return sourceFiles;
    }
}
