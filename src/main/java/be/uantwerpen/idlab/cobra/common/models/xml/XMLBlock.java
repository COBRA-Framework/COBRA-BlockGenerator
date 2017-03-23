package be.uantwerpen.idlab.cobra.common.models.xml;

import be.uantwerpen.idlab.cobra.common.models.blocks.Block;

import java.util.List;

/**
 * Created by Thomas on 1/12/2016.
 */
public abstract class XMLBlock implements XMLObject, Block
{
    private final String eol;
    private final static String BLOCK_NAMESPACE_ROOT = "be.uantwerpen.idlab.cobra.common.models.blocks";

    public XMLBlock()
    {
        eol = System.getProperty("line.separator");
    }

    public String getXMLString()
    {
        String xml = new String();
        String typeName = this.getClass().getCanonicalName().split(BLOCK_NAMESPACE_ROOT)[1].substring(1);

        xml = "<block type=\"" + typeName + "\">";

        //Add block specific field elements
        for(XMLObject element : this.getXMLElements())
        {
            for(String line : element.getXMLString().split(eol))
            {
                xml = xml.concat(eol + "\t" + line);
            }
        }

        if(!this.getChildBlocks().isEmpty())
        {
            xml = xml.concat(eol + "\t<children>");

            //Add child blocks elements
            for(Block childBlock : this.getChildBlocks())
            {
                String blockXML = new String();

                //Add tabs to xml elements
                for(String subString : ((XMLBlock)childBlock).getXMLString().split(eol))
                {
                    blockXML = blockXML.concat(eol + "\t\t" + subString);
                }

                xml = xml.concat(blockXML);
            }

            xml = xml.concat(eol + "\t</children>" + eol);
        }
        else if(!this.getXMLElements().isEmpty())
        {
            xml = xml.concat(eol);
        }

        xml = xml.concat("</block>");

        return xml;
    }

    public abstract List<XMLObject> getXMLElements();
}
