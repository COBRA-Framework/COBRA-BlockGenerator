package be.uantwerpen.idlab.cobra.common.tools.importing.model;

import be.uantwerpen.idlab.cobra.common.models.ProjectConfig;
import be.uantwerpen.idlab.cobra.common.models.blocks.Block;
import be.uantwerpen.idlab.cobra.common.tools.importing.model.versions.ModelFileParserV1;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Thomas on 7/12/2016.
 */
public class ModelFileManager
{
    List<ModelFileParser> modelParsers;

    public ModelFileManager()
    {
        modelParsers = new ArrayList<ModelFileParser>();

        //Add model parser versions
        modelParsers.add(new ModelFileParserV1());
    }

    public List<Block> parseModelFile(String fileLocation, ProjectConfig projectConfig) throws Exception
    {
        ModelFileParser parser = null;

        File modelFile = new File(fileLocation);

        if(!modelFile.exists() || !modelFile.isFile())
        {
            throw new Exception("The provided path is not a model file!");
        }

        boolean found = false;
        Iterator<ModelFileParser> it = modelParsers.iterator();
        while(it.hasNext() && !found)
        {
            parser = it.next();

            try
            {
                if(parser.isCompatible(modelFile))
                {
                    found = true;
                }
            }
            catch(Exception e)
            {
                //Parser is not compatible or model file is corrupted
                continue;
            }
        }

        if(!found)
        {
            throw new Exception("The provided model file is not compatible with this application version or the file is corrupted!");
        }

        return parser.parseModelFile(modelFile, projectConfig);
    }
}
