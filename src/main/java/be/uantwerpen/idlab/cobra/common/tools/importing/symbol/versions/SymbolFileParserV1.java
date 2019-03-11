package be.uantwerpen.idlab.cobra.common.tools.importing.symbol.versions;

import be.uantwerpen.idlab.cobra.blockgen.tools.symbols.*;
import be.uantwerpen.idlab.cobra.common.models.ProjectConfig;
import be.uantwerpen.idlab.cobra.common.tools.importing.symbol.SymbolFileParser;
import be.uantwerpen.idlab.cobra.common.tools.terminal.Terminal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class SymbolFileParserV1 implements SymbolFileParser
{
    private final static String VERSION = "1.3";

    public SymbolFileParserV1()
    {

    }

    @Override
    public String getVersionCompatibility()
    {
        return VERSION;
    }

    @Override
    public Boolean isCompatible(File symbolFile) throws Exception
    {
        boolean compatible = false;
        BufferedReader buffReader = null;

        try
        {
            FileReader reader = new FileReader(symbolFile);
            buffReader = new BufferedReader(reader);
        }
        catch(FileNotFoundException e)
        {
            throw new Exception("Could not read file: " + symbolFile.getName(), e);
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
            throw new Exception("Could not determine version compatibility for the file: " + symbolFile.getName(), e);
        }

        return compatible;
    }

    @Override
    public List<SymbolTable> parseSymbolFile(File symbolFile, ProjectConfig config) throws Exception
    {
        List<SymbolTable> symbolTables = new ArrayList<SymbolTable>();

        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(symbolFile);
            doc.getDocumentElement().normalize();

            //Get models
            NodeList tablesList = doc.getElementsByTagName("table");

            for(int i = 0; i < tablesList.getLength(); i++)
            {
                Node table = tablesList.item(i);
                SymbolTable symbolTable = new SymbolTable();

                if(table.getNodeType() == Node.ELEMENT_NODE)
                {
                    //Get first node
                    Node scopeNode = table.getFirstChild();

                    if(scopeNode != null)
                    {
                        do
                        {
                            if(scopeNode.getNodeType() == Node.ELEMENT_NODE)
                            {
                                Element scopeElement = (Element)scopeNode;

                                symbolTable.insertScope(parseScope(scopeElement, config));
                            }

                            scopeNode = scopeNode.getNextSibling();
                        } while(scopeNode != null);
                    }
                    else
                    {
                        Terminal.printTerminalWarning("Table contains no scopes. Table will be skipped!");
                    }
                }

                symbolTables.add(symbolTable);
            }
        }
        catch(Exception e)
        {
            throw new Exception("Could not parse the symbol file: " + symbolFile.getName() + " (" + e.getClass().getName() + ": " + e.getMessage() + ")", e);
        }

        return symbolTables;
    }

    private Scope parseScope(Element scopeElement, ProjectConfig config) throws Exception
    {
        return parseScope(null, scopeElement, config);
    }

    private Scope parseScope(Scope parentScope, Element scopeElement, ProjectConfig config) throws Exception
    {
        Scope scope = null;
        String name = null;

        try
        {
            name = scopeElement.getAttribute("name");
        }
        catch(Exception e)
        {
            throw new Exception("Could not find or parse name attribute from table! " + e.getMessage(), e);
        }

        List<Symbol> symbols = new ArrayList<Symbol>();
        NodeList symbolList = scopeElement.getChildNodes();

        for(int i = 0; i < symbolList.getLength(); i++)
        {
            Node symbolNode = symbolList.item(i);

            if(symbolNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element symbolElement = (Element)symbolNode;

                symbols.add(parseSymbol(symbolElement, config));
            }
        }

        scope = new Scope(name, null, symbols);

        return scope;
    }

    private Symbol parseSymbol(Element symbolElement, ProjectConfig config) throws Exception
    {
        Symbol symbol = null;
        String symbolType = symbolElement.getTagName().toLowerCase();

        switch(symbolType)
        {
            case "variable":
                symbol = parseVariableSymbol(symbolElement, config);
                break;
            case "array":
                symbol = parseArraySymbol(symbolElement, config);
                break;
            case "function":
                symbol = parseFunctionSymbol(symbolElement, config);
                break;
            case "parameter":
                symbol = parseParameterSymbol(symbolElement, config);
                break;
            default:
                throw new Exception("Unknown symbol. Could not find symbol with type: " + symbolType);
        }

        return symbol;
    }

    private VariableSymbol parseVariableSymbol(Element symbolElement, ProjectConfig config) throws Exception
    {
        VariableSymbol symbol = null;
        String name = null;
        String type = null;

        try
        {
            name = symbolElement.getAttribute("name");
        }
        catch(Exception e)
        {
            throw new Exception("Could not find or parse name attribute from variable symbol! " + e.getMessage(), e);
        }

        try
        {
            type = symbolElement.getAttribute("type");
        }
        catch(Exception e)
        {
            throw new Exception("Could not find or parse type attribute from variable symbol! " + e.getMessage(), e);
        }

        symbol = new VariableSymbol(type, name);

        return symbol;
    }

    private ArraySymbol parseArraySymbol(Element symbolElement, ProjectConfig config) throws Exception
    {
        ArraySymbol symbol = null;
        String name = null;
        String type = null;
        int size = -1;

        try
        {
            name = symbolElement.getAttribute("name");
        }
        catch(Exception e)
        {
            throw new Exception("Could not find or parse name attribute from array symbol! " + e.getMessage(), e);
        }

        try
        {
            type = symbolElement.getAttribute("type");
        }
        catch(Exception e)
        {
            throw new Exception("Could not find or parse type attribute from array symbol! " + e.getMessage(), e);
        }

        try
        {
            size = Integer.parseInt(symbolElement.getAttribute("size"));
        }
        catch(Exception e)
        {
            throw new Exception("Could not find or parse size attribute from array symbol! " + e.getMessage(), e);
        }

        symbol = new ArraySymbol(type, name, size);

        return symbol;
    }

    private FunctionSymbol parseFunctionSymbol(Element symbolElement, ProjectConfig config) throws Exception
    {
        FunctionSymbol symbol = null;
        String name = null;
        String returnType = null;

        try
        {
            name = symbolElement.getAttribute("name");
        }
        catch(Exception e)
        {
            throw new Exception("Could not find or parse name attribute from function symbol! " + e.getMessage(), e);
        }

        try
        {
            returnType = symbolElement.getAttribute("return_type");
        }
        catch(Exception e)
        {
            throw new Exception("Could not find or parse return type attribute from function symbol! " + e.getMessage(), e);
        }

        List<ParameterSymbol> parameters = new ArrayList<ParameterSymbol>();
        NodeList parameterList = symbolElement.getChildNodes();

        for(int i = 0; i < parameterList.getLength(); i++)
        {
            Node parameterNode = parameterList.item(i);

            if(parameterNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element parameterElement = (Element)parameterNode;

                parameters.add(parseParameterSymbol(parameterElement, config));
            }
        }

        symbol = new FunctionSymbol(returnType, name, parameters);

        return symbol;
    }

    private ParameterSymbol parseParameterSymbol(Element symbolElement, ProjectConfig config) throws Exception
    {
        ParameterSymbol symbol = null;
        String name = null;
        String type = null;

        try
        {
            name = symbolElement.getAttribute("name");
        }
        catch(Exception e)
        {
            throw new Exception("Could not find or parse name attribute from parameter symbol! " + e.getMessage(), e);
        }

        try
        {
            type = symbolElement.getAttribute("type");
        }
        catch(Exception e)
        {
            throw new Exception("Could not find or parse type attribute from parameter symbol! " + e.getMessage(), e);
        }

        symbol = new ParameterSymbol(type, name);

        return symbol;
    }
}
