package be.uantwerpen.idlab.cobra.blockgen.models;

import be.uantwerpen.idlab.cobra.blockgen.models.xml.XMLObject;

/**
 * Created by Thomas on 22/03/2016.
 */
public class CodeSegment implements XMLObject
{
    private CodeFile codeFile;
    private int startIndex;
    private int endIndex;

    protected CodeSegment()
    {

    }

    public CodeSegment(CodeSegment codeSegment)
    {
        this(codeSegment.getCodeFile(), codeSegment.getStartIndex(), codeSegment.getEndIndex());
    }

    public CodeSegment(CodeFile codeFile, int startIndex, int endIndex)
    {
        this.codeFile = codeFile;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public CodeFile getCodeFile()
    {
        return this.codeFile;
    }

    public void setCodeFile(CodeFile codeFile)
    {
        this.codeFile = codeFile;
    }

    public int getStartIndex()
    {
        return this.startIndex;
    }

    public void setStartIndex(int startIndex)
    {
        this.startIndex = startIndex;
    }

    public int getEndIndex()
    {
        return this.endIndex;
    }

    public void setEndIndex(int endIndex)
    {
        this.endIndex = endIndex;
    }

    public int getRowNumber(int index)
    {
        int row = 1;
        String code = this.codeFile.getCodeStream();

        for(int i = 0; i <= index; i++)
        {
            if(code.charAt(i) == '\n' && i != index)
            {
                //New row
                row++;
            }
        }

        return row;
    }

    public int getColumnNumber(int index)
    {
        int column = 1;
        String code = this.codeFile.getCodeStream();

        for(int i = 0; i <= index; i++)
        {
            if(code.charAt(i) == '\n' && i != index)
            {
                //Reset column number
                column = 1;
            }
            else
            {
                column++;
            }
        }

        return column;
    }

    public String getXMLString()
    {
        final String eol = System.getProperty("line.separator");
        String xml = new String();

        xml = "<code id=\"" + this.getCodeFile().getId() + "\">";

        xml = xml.concat(eol + "\t<start>" + this.startIndex + "</start>");

        xml = xml.concat(eol + "\t<end>" + this.endIndex + "</end>");

        xml = xml.concat(eol + "</code>");

        return xml;
    }

    @Override
    public String toString()
    {
        return codeFile.getCodeStream().substring(this.startIndex, this.endIndex);
    }
}
