package be.uantwerpen.idlab.cobra.blockgen.models.xml;

/**
 * Created by Thomas on 1/12/2016.
 */
public class XMLElement
{
    private final String eol;
    private String name;
    private Object value;

    protected XMLElement()
    {
        eol = System.getProperty("line.separator");
    }

    public XMLElement(String name, Object value)
    {
        this();

        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return this.name;
    }

    public Object getValue()
    {
        return this.value;
    }

    public String getXMLString()
    {
        String xml = new String();

        xml = "<" + this.getName() + ">";

        xml = xml.concat(formatXMLString(this.getValue().toString()));

        xml = xml.concat("</" + this.getName() + ">");

        return xml;
    }

    private String formatXMLString(String value)
    {
        String reformattedString = value;

        //Remove '&' chars
        reformattedString = reformattedString.replaceAll("&", "&amp;");

        //Remove '<' chars
        reformattedString = reformattedString.replaceAll("<", "&lt;");

        //Remove '>' chars
        reformattedString = reformattedString.replaceAll(">", "&gt;");

        //Remove '"' chars
        reformattedString = reformattedString.replaceAll("\"", "&quot;");

        //Remove ' chars
        reformattedString = reformattedString.replaceAll("'", "&apos;");

        //Remove 'new line' chars
        reformattedString = reformattedString.replaceAll("\n", eol);

        return reformattedString;
    }
}
