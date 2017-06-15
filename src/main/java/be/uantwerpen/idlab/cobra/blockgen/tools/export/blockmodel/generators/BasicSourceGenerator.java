package be.uantwerpen.idlab.cobra.blockgen.tools.export.blockmodel.generators;

import java.io.File;
import java.io.IOException;

import be.uantwerpen.idlab.cobra.uppaalexporter.tools.export.blockmodel.SourceGenerator;

/**
 * Created by Thomas on 27/09/2016.
 */
public abstract class BasicSourceGenerator implements SourceGenerator
{
    protected File openFile(String fileLocation) throws Exception
    {
        File file;

        //Open file
        try
        {
            file = new File(fileLocation);

            if(!file.exists())
            {
                file.createNewFile();
            }
        }
        catch(IOException e)
        {
            //Could not create file
            throw new IOException("Could not open file: " + fileLocation + ".\n" + e.getMessage());
        }

        return file;
    }

    protected String getSourceHeader()
    {
        String eol = System.getProperty("line.separator");
        String header;

        header = "\tCreated with COBRA-Framework Export Tool v0.1" + eol + "\tDeveloped by: Thomas Huybrechts - MOSAIC 2016";

        return header;
    }
}
