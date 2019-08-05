package be.uantwerpen.idlab.cobra.blockgen.tools.exporting;

import be.uantwerpen.idlab.cobra.common.models.symbols.SymbolTable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class SymbolExport
{
    private static final int MAIN_VERSION = 1;
    private static final int MINOR_VERSION = 6;

    public int exportToXML(List<SymbolTable> tables, String exportFile) throws Exception
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

        return writeSymbolFile(tables, file);
    }

    private int writeSymbolFile(List<SymbolTable> tables, File file) throws Exception
    {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbFactory.newDocumentBuilder();
        Document doc = db.newDocument();

        //Create root element
        Element rootElement = doc.createElement("symboltable");
        doc.appendChild(rootElement);

        //Add version element
        Element versionElement = doc.createElement("version");
        versionElement.setTextContent(MAIN_VERSION + "." + MINOR_VERSION);
        rootElement.appendChild(versionElement);

        for(SymbolTable table : tables)
        {
            rootElement.appendChild(table.toXMLNode(doc, null));
        }

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();

        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(doc);

        StreamResult stream = new StreamResult(file);
        transformer.transform(source, stream);

        return 0;
    }
}
