package be.uantwerpen.idlab.cobra.blockgen.models.xml;

import be.uantwerpen.idlab.cobra.blockgen.models.blocks.Block;

import java.util.List;

/**
 * Created by Thomas on 1/12/2016.
 */
public abstract class XMLBlock implements XMLObject, Block
{
    private final String eol;

    public XMLBlock()
    {
        eol = System.getProperty("line.separator");
    }

    public String getXMLObject()
    {
        String xml = new String();

        xml = "<block type=\"" + this.getClass().getSimpleName() + "\">";

        //Add block specific field elements
        for(XMLElement element : this.getXMLElements())
        {
            xml = xml.concat(eol + "\t" + element.getXMLString());
        }

        if(!this.getChildBlocks().isEmpty())
        {
            xml = xml.concat(eol + "\t<children>");

            //Add child blocks elements
            for(Block childBlock : this.getChildBlocks())
            {
                String blockXML = new String();

                //Add tabs to xml elements
                for(String subString : ((XMLBlock)childBlock).getXMLObject().split(eol))
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

    public abstract List<XMLElement> getXMLElements();
}
