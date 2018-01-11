package be.uantwerpen.idlab.cobra.common.tools.importing.model.versions;

import be.uantwerpen.idlab.cobra.common.models.CodeFile;
import be.uantwerpen.idlab.cobra.common.models.CodeSegment;
import be.uantwerpen.idlab.cobra.common.models.ProjectConfig;
import be.uantwerpen.idlab.cobra.common.models.SourceFile;
import be.uantwerpen.idlab.cobra.common.models.blocks.Block;
import be.uantwerpen.idlab.cobra.common.tools.importing.model.ModelFileParser;
import be.uantwerpen.idlab.cobra.common.tools.terminal.Terminal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Thomas on 7/12/2016.
 */
public class ModelFileParserV1 implements ModelFileParser
{
    private final static String VERSION = "1.1";
    private final static String BLOCK_NAMESPACE_ROOT = "be.uantwerpen.idlab.cobra.common.models.blocks";

    public ModelFileParserV1()
    {

    }

    public String getVersionCompatibility()
    {
        return VERSION;
    }

    public Boolean isCompatible(File modelFile) throws Exception
    {
        boolean compatible = false;
        BufferedReader buffReader = null;

        try
        {
            FileReader reader = new FileReader(modelFile);
            buffReader = new BufferedReader(reader);
        }
        catch(FileNotFoundException e)
        {
            throw new Exception("Could not read file: " + modelFile.getName(), e);
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
            throw new Exception("Could not determine version compatibility for the file: " + modelFile.getName(), e);
        }

        return compatible;
    }

    public List<Block> parseModelFile(File modelFile, ProjectConfig config) throws Exception
    {
        List<Block> blocks = new ArrayList<Block>();

        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(modelFile);
            doc.getDocumentElement().normalize();

            //Get models
            NodeList modelsList = doc.getElementsByTagName("model");

            for(int i = 0; i < modelsList.getLength(); i++)
            {
                Node model = modelsList.item(i);

                if(model.getNodeType() == Node.ELEMENT_NODE)
                {
                    //Get first node
                    Node blockNode = model.getFirstChild();

                    if(blockNode != null)
                    {
                        do
                        {
                            if(blockNode.getNodeType() == Node.ELEMENT_NODE)
                            {
                                Element blockElement = (Element)blockNode;

                                blocks.add(parseBlock(blockElement, config));
                            }

                            blockNode = blockNode.getNextSibling();
                        } while(blockNode != null);
                    }
                    else
                    {
                        Terminal.printTerminalWarning("Model contains no blocks. Model will be skipped!");
                    }
                }
            }
        }
        catch(Exception e)
        {
            throw new Exception("Could not parse the model file: " + modelFile.getName() + " (" + e.getClass().getName() + ": " + e.getMessage() + ")", e);
        }

        return blocks;
    }

    private Block parseBlock(Element blockElement, ProjectConfig config) throws Exception
    {
        Block block = null;
        Object newInstance = null;
        long id = 0;
        String type = BLOCK_NAMESPACE_ROOT.concat("." + blockElement.getAttribute("type"));

        try
        {
            id = Long.parseLong(blockElement.getAttribute("id"));
        }
        catch(Exception e)
        {
            throw new Exception("Could not find or parse id attribute from block! " + e.getMessage(), e);
        }

        try
        {
            Class<?> classType = Class.forName(type);
            Constructor<?> constructor = classType.getConstructor(Long.class);
            newInstance = constructor.newInstance(id);
        }
        catch(ClassNotFoundException e)
        {
            throw new Exception("Class not found. Could not find block with type: " + type, e);
        }

        if(!Block.class.isAssignableFrom(newInstance.getClass()))
        {
            throw new Exception("Invalid class. Could not find block with type: " + type);
        }
        else
        {
            block = (Block)newInstance;
        }

        //Set parameters with setter functions
        NodeList blockParameters = blockElement.getChildNodes();

        for(int i = 0; i < blockParameters.getLength(); i++)
        {
            Node parameter = blockParameters.item(i);

            if(parameter.getNodeType() == Node.ELEMENT_NODE)
            {
                Element parameterElement = (Element)parameter;

                String parameterName = parameterElement.getTagName();

                if(parameterName.equals("children"))
                {
                    //Add children blocks to current block
                    NodeList childBlocks = parameterElement.getChildNodes();

                    for(int j = 0; j < childBlocks.getLength(); j++)
                    {
                        Node childBlock = childBlocks.item(j);

                        if(childBlock.getNodeType() == Node.ELEMENT_NODE)
                        {
                            Element childElement = (Element)childBlock;

                            block.addChildBlock(parseBlock(childElement, config));
                        }
                    }
                }
                else if(parameterName.equals("code"))
                {
                    //Add code segment to current block
                    block.setCodeSegment(parseCodeSegment(parameterElement, config));
                }
                else
                {
                    //Get the setter method of block to set the value of the block
                    Method[] methods = block.getClass().getMethods();
                    Method method = null;
                    boolean found = false;
                    int j = 0;

                    while(j < methods.length && !found)
                    {
                        method = methods[j];
                        String methodName = method.getName().split("\\(", 2)[0].toLowerCase();

                        if(methodName.equals("set" + parameterName.replaceAll("_", "").toLowerCase().trim()) && method.getParameterTypes().length == 1)
                        {
                            found = true;
                        }

                        j++;
                    }

                    if(!found)
                    {
                        throw new Exception("Invalid parameter found: '" + parameterName + "'. Can not found corresponding field in block type: '" + block.getClass().getName() + "'!");
                    }
                    else
                    {
                        //Set parameter
                        try
                        {
                            Class parameterType = method.getParameterTypes()[0];

                            if(String.class.isAssignableFrom(parameterType))
                            {
                                method.invoke(block, parameterElement.getTextContent());
                            }
                            else if(Number.class.isAssignableFrom(parameterType))
                            {
                                Double value = Double.parseDouble(parameterElement.getTextContent());

                                method.invoke(block, value);
                            }
                            else if(boolean.class.isAssignableFrom(parameterType))
                            {
                                boolean value = Boolean.parseBoolean(parameterElement.getTextContent());

                                method.invoke(block, value);
                            }
                            else
                            {
                                Terminal.printTerminalWarning("Could not set the value of field: '" + parameterName + "'. Undefined value conversion!");
                            }
                        }
                        catch(Exception e)
                        {
                            throw new Exception("Could not assign value to field: '" + parameterName + "'.", e);
                        }
                    }
                }
            }
        }

        return block;
    }

    private CodeSegment parseCodeSegment(Element codeElement, ProjectConfig config) throws Exception
    {
        String idString = codeElement.getAttribute("id");
        SourceFile sourceFile = null;
        long id = -1L;

        try
        {
            id = Long.parseLong(idString);
        }
        catch(NumberFormatException e)
        {
            throw new NumberFormatException("Could not parse id of code segment!");
        }

        boolean found = false;
        Iterator<SourceFile> it = config.getSourceFiles().iterator();
        while(it.hasNext() && !found)
        {
            sourceFile = it.next();

            if(sourceFile.getId() == id)
            {
                found = true;
            }
        }

        if(!found)
        {
            throw new Exception("Could not find a source file with the corresponding id: " + id);
        }

        //Generate code file
        CodeFile codeFile = new CodeFile(getCodeFromSource(sourceFile.getSourceLocation()), config.getGrammar().name(), sourceFile);

        //Parse code segment
        int startIndex = -1;
        int endIndex = -1;

        try
        {
            startIndex = Integer.parseInt(codeElement.getElementsByTagName("start").item(0).getTextContent());
            endIndex = Integer.parseInt(codeElement.getElementsByTagName("end").item(0).getTextContent());
        }
        catch(NumberFormatException e)
        {
            throw new NumberFormatException("Could not parse start and end of code segment!");
        }

        CodeSegment codeSegment = new CodeSegment(codeFile, startIndex, endIndex);

        return codeSegment;
    }

    private String getCodeFromSource(String fileLocation) throws Exception
    {
        String code = new String();
        BufferedReader buffReader = null;

        try
        {
            File codeFile = new File(fileLocation);
            FileReader reader = new FileReader(codeFile);
            buffReader = new BufferedReader(reader);
        }
        catch(FileNotFoundException e)
        {
            throw new Exception("Could not open file: " + fileLocation, e);
        }

        try
        {
            while(buffReader.ready())
            {
                char character = (char)buffReader.read();

                code = code + character;
            }

            buffReader.close();
        }
        catch(IOException e)
        {
            throw new Exception("Could not read file: " + fileLocation, e);
        }

        return code;
    }
}
