package be.uantwerpen.idlab.cobra.blockgen.tools.exporting;

import be.uantwerpen.idlab.cobra.blockgen.models.blocks.Block;
import be.uantwerpen.idlab.cobra.blockgen.models.xml.XMLBlock;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created by Thomas on 30/11/2016.
 */
public class ProjectExport implements ExportTool
{
    private static final int MAIN_VERSION = 1;
    private static final int MINOR_VERSION = 0;
    private static final String eol = System.getProperty("line.separator");

    public int exportToXML(List<Block> models, String exportFile, String[] args) throws Exception
    {
        File file;

        //Open file
        try
        {
            file = new File(exportFile);

            if(!file.exists())
            {
                file.createNewFile();
            }
        }
        catch(IOException e)
        {
            //Could not create file
            throw new IOException("Could not open file: " + exportFile + "\nMessage: " + e.getMessage(), e);
        }

        return writeProjectFile(models, file);
    }

    private int writeProjectFile(List<Block> models, File file) throws Exception
    {
        FileWriter writer;
        BufferedWriter buffWriter;

        try
        {
            writer = new FileWriter(file.getAbsoluteFile());
            buffWriter = new BufferedWriter(writer);

            //Write project file introduction
            String xmlProlog = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + eol;
            buffWriter.write(xmlProlog);

            String xmlIntroductionComments = "<!-- This project file is generated by the COBRA-BlockGenerator Tool (version " + this.getClass().getPackage().getImplementationVersion() + ") -->" + eol;
            xmlIntroductionComments = xmlIntroductionComments.concat("<!-- Copyright (c) 2016-2017 Thomas Huybrechts, IDLab, University of Antwerp, Belgium. All rights reserved. -->" + eol);
            buffWriter.write(xmlIntroductionComments);

            //Write project file metadata
            String projectMetaData = "<blockmodel>" + eol + "\t<version>" + MAIN_VERSION + "." + MINOR_VERSION + "</version>" + eol + "\t<models>" + eol;
            buffWriter.write(projectMetaData);

            //Write project file body
            for(Block model : models)
            {
                buffWriter.write("\t\t<model>" + eol);

                String modelData = getModelXML(model);
                buffWriter.write(modelData);

                buffWriter.write("\t\t</model>" + eol);
            }

            buffWriter.write("\t</models>" + eol);

            buffWriter.write("</blockmodel>" + eol);

            buffWriter.close();
        }
        catch(IOException e)
        {
            //Could not write to file
            throw new IOException("Could not write to file: " + file.getName() + "\nMessage: " + e.getMessage(), e);
        }

        return 0;
    }

    private String getModelXML(Block model)
    {
        String modelXML = new String();

        //Add tabs to xml elements
        for(String subString : ((XMLBlock)model).getXMLString().split(eol))
        {
            modelXML = modelXML.concat("\t\t\t" + subString + eol);
        }

        return modelXML;
    }
}
