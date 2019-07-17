package be.uantwerpen.idlab.cobra.common.tools.importing.symbol.versions;

import be.uantwerpen.idlab.cobra.common.models.symbols.*;
import be.uantwerpen.idlab.cobra.common.models.ProjectConfig;
import be.uantwerpen.idlab.cobra.common.tools.importing.symbol.SymbolFileParser;
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
    private final static String VERSION = "1.5";

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

                if(table.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element tableElement = (Element)table;
                    SymbolTable symbolTable = parseTable(tableElement, config);

                    symbolTables.add(symbolTable);
                }
            }
        }
        catch(Exception e)
        {
            throw new Exception("Could not parse the symbol file: " + symbolFile.getName() + " (" + e.getClass().getName() + ": " + e.getMessage() + ")", e);
        }

        return symbolTables;
    }

    private SymbolTable parseTable(Element tableElement, ProjectConfig config) throws Exception
    {
        SymbolTable symbolTable = null;

        //Get first node
        Node scopeNode = tableElement.getFirstChild();

        //Get global scope
        while(scopeNode != null && symbolTable == null)
        {
            if(scopeNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element scopeElement = (Element)scopeNode;

                Scope globalScope = parseScope(scopeElement, config);
                symbolTable = new SymbolTable(globalScope);
            }

            scopeNode = scopeNode.getNextSibling();
        }

        if(symbolTable == null)
        {
            throw new Exception("Table does not contain a global context!");
        }

        return symbolTable;
    }

    private Scope parseScope(Element scopeElement, ProjectConfig config) throws Exception
    {
        return parseScope(null, scopeElement, config);
    }

    private Scope parseScope(Scope parentScope, Element scopeElement, ProjectConfig config) throws Exception
    {
        Scope scope = null;
        String scopeType = scopeElement.getTagName().toLowerCase();

        switch(scopeType)
        {
            case "globalscope":
                scope = parseGlobalScope(scopeElement, config);
                break;
            case "functionscope":
                scope = parseFunctionScope(parentScope, scopeElement, config);
                break;
            case "statementscope":
                scope = parseStatementScope(parentScope, scopeElement, config);
                break;
            case "blockscope":
                scope = parseBlockScope(parentScope, scopeElement, config);
                break;
            case "parameters":
                //Skip function scope specific tag
                return parentScope;
            default:
                //Check for symbol tag
                Symbol symbol = parseSymbol(scopeElement, config);
                parentScope.insertSymbol(symbol);
                return parentScope;
        }

        parseChildScopes(scope, scopeElement, config);

        return scope;
    }

    private void parseChildScopes(Scope parentScope, Element scopeElement, ProjectConfig config) throws Exception
    {
        //Get first node
        Node childScopeNode = scopeElement.getFirstChild();

        //Get child scopes
        while(childScopeNode != null)
        {
            if(childScopeNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element childScopeElement = (Element)childScopeNode;

                parseScope(parentScope, childScopeElement, config);
            }

            childScopeNode = childScopeNode.getNextSibling();
        }
    }

    private GlobalScope parseGlobalScope(Element scopeElement, ProjectConfig config) throws Exception
    {
        GlobalScope scope = new GlobalScope();

        return scope;
    }

    private FunctionScope parseFunctionScope(Scope parentScope, Element symbolElement, ProjectConfig config) throws Exception
    {
        FunctionScope scope;
        String name;
        String returnType;
        Long blockId;

        try
        {
            name = symbolElement.getAttribute("name");
        }
        catch(Exception e)
        {
            throw new Exception("Could not find or parse name attribute from function scope! " + e.getMessage(), e);
        }

        try
        {
            returnType = symbolElement.getAttribute("return_type");
        }
        catch(Exception e)
        {
            throw new Exception("Could not find or parse return type attribute from function scope! " + e.getMessage(), e);
        }

        try
        {
            blockId = Long.parseLong(symbolElement.getAttribute("block_id"));
        }
        catch(Exception e)
        {
            throw new Exception("Could not find or parse block id attribute from function scope! " + e.getMessage(), e);
        }

        List<VariableSymbol> parameters = new ArrayList<VariableSymbol>();
        NodeList propertiesList = symbolElement.getChildNodes();

        for(int i = 0; i < propertiesList.getLength(); i++)
        {
            Node propertyNode = propertiesList.item(i);

            if(propertyNode.getNodeType() == Node.ELEMENT_NODE && propertyNode.getNodeName().toLowerCase().equals("parameters"))
            {
                NodeList parametersList = propertyNode.getChildNodes();

                for(int j = 0; j < parametersList.getLength(); j++)
                {
                    Node parameterNode = parametersList.item(j);

                    if(parameterNode.getNodeType() == Node.ELEMENT_NODE)
                    {
                        Element parameterElement = (Element)parameterNode;

                        Symbol parsedSymbol = parseSymbol(parameterElement, config);
                        VariableSymbol parameterSymbol;

                        if(parsedSymbol instanceof VariableSymbol)
                        {
                            parameterSymbol = (VariableSymbol)parsedSymbol;
                        }
                        else
                        {
                            throw new RuntimeException("Incompatible symbol '" + parsedSymbol.getClass().getSimpleName() + "' detected in parameters tag!");
                        }

                        parameters.add(parameterSymbol);
                    }
                }
            }
        }

        scope = new FunctionScope(blockId, name, returnType, parentScope);
        scope.addParameters(parameters);

        return scope;
    }

    private StatementScope parseStatementScope(Scope parentScope, Element scopeElement, ProjectConfig config) throws Exception
    {
        StatementScope scope;
        String name;
        Long blockId;

        try
        {
            name = scopeElement.getAttribute("name");
        }
        catch(Exception e)
        {
            throw new Exception("Could not find or parse name attribute from statement scope! " + e.getMessage(), e);
        }

        try
        {
            blockId = Long.parseLong(scopeElement.getAttribute("block_id"));
        }
        catch(Exception e)
        {
            throw new Exception("Could not find or parse block id attribute from statement scope! " + e.getMessage(), e);
        }

        scope = new StatementScope(blockId, name, parentScope);

        return scope;
    }

    private BlockScope parseBlockScope(Scope parentScope, Element scopeElement, ProjectConfig config) throws Exception
    {
        Integer startIndex;
        Integer endIndex;

        try
        {
            startIndex = Integer.parseInt(scopeElement.getAttribute("start_index"));
        }
        catch(Exception e)
        {
            throw new Exception("Could not find or parse start index attribute from block scope! " + e.getMessage(), e);
        }

        try
        {
            endIndex = Integer.parseInt(scopeElement.getAttribute("end_index"));
        }
        catch(Exception e)
        {
            throw new Exception("Could not find or parse end index attribute from block scope! " + e.getMessage(), e);
        }

        BlockScope scope = new BlockScope(startIndex, endIndex, parentScope);

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
            default:
                throw new Exception("Unknown symbol. Could not find symbol with type: " + symbolType);
        }

        return symbol;
    }

    private VariableSymbol parseVariableSymbol(Element symbolElement, ProjectConfig config) throws Exception
    {
        VariableSymbol symbol;
        String name;
        String type;

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
        ArraySymbol symbol;
        String name;
        String type;
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

        if(symbolElement.hasAttribute("size"))
        {
            try
            {
                size = Integer.parseInt(symbolElement.getAttribute("size"));
            }
            catch(Exception e)
            {
                throw new Exception("Could not find or parse size attribute from array symbol! " + e.getMessage(), e);
            }
        }

        symbol = new ArraySymbol(type, name, size);

        return symbol;
    }
}
