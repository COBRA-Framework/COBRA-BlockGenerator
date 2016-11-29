package be.uantwerpen.cobra.blockgen.tools.export.uppaal;

import be.uantwerpen.cobra.blockgen.models.blocks.Block;
import be.uantwerpen.cobra.blockgen.models.blocks.ProgramBlock;
import be.uantwerpen.cobra.blockgen.tools.export.ExportTool;
import be.uantwerpen.cobra.blockgen.tools.export.uppaal.models.Link;
import be.uantwerpen.cobra.blockgen.tools.export.uppaal.models.Node;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Thomas on 25/04/2016.
 */
public class UppaalModelExport implements ExportTool
{
    @Override
    public int exportToXML(Block model, String exportFile, String[] args) throws IllegalArgumentException
    {
        int abstractionDepth;

        if(args.length > 1)
        {
            throw new IllegalArgumentException("Uppaal export tools requires only one argument. Args[0] = abstractionDepth");
        }
        else if(args.length == 0)
        {
            abstractionDepth = -1;
        }
        else
        {
            try
            {
                abstractionDepth = Integer.parseInt(args[0]);
            }
            catch(Exception e)
            {
                throw new IllegalArgumentException("AbstractionDepth (Arg[0]) is not a valid number!\n" + e.toString());
            }
        }

        return exportToXML(model, exportFile, abstractionDepth);
    }

    public int exportToXML(Block model, String exportFile, int abstractionDepth)
    {
        Collection<TimedAutomaton> uppaalModel = new ArrayList<TimedAutomaton>();

        if(model.getClass() == ProgramBlock.class)
        {
            for(Block methodBlock : model.getChildBlocks())
            {
                try
                {
                    uppaalModel.add(createTimedAutomaton(methodBlock, abstractionDepth));
                }
                catch(Exception e)
                {
                    System.err.println("Failed to export model to XML!\n" + e.getMessage());
                    e.printStackTrace();

                    return -1;
                }
            }
        }
        else
        {
            try
            {
                uppaalModel.add(createTimedAutomaton(model, abstractionDepth));
            }
            catch(Exception e)
            {
                System.err.println("Failed to export model to XML!\n" + e.getMessage());
                e.printStackTrace();

                return -1;
            }
        }

        try
        {
            writeXMLFile(exportFile, uppaalModel);
        }
        catch(Exception e)
        {
            System.err.println("Error occurred while exporting to XML!\n" + e.getMessage());
            e.printStackTrace();

            return -2;
        }

        return 0;
    }

    public int exportToXML(Collection<TimedAutomaton> automata, String exportFile)
    {
        try
        {
            writeXMLFile(exportFile, automata);
        }
        catch(Exception e)
        {
            System.err.println("Error occurred while exporting to XML!\n" + e.getMessage());
            e.printStackTrace();

            return -2;
        }

        return 0;
    }

    public TimedAutomaton createTimedAutomaton(Block model, int abstractionDepth) throws Exception
    {
        TimedAutomaton automaton = new TimedAutomaton();

        automaton.createAutomaton(model, abstractionDepth);

        return automaton;
    }

    private int writeXMLFile(String exportFile, Collection<TimedAutomaton> systems) throws Exception
    {
        File file;
        FileWriter writer;
        BufferedWriter buffWriter;

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
            throw new IOException("Could not open file: " + exportFile + ".\n" + e.getMessage());
        }

        try
        {
            String eol = System.getProperty("line.separator");
            writer = new FileWriter(file.getAbsoluteFile());
            buffWriter = new BufferedWriter(writer);
            
            //Declare global clock
            String gclock = "clock t;";
            
            //Declare chan
            String ChanSet = " ";
            for(TimedAutomaton system : systems)
            {
            	ChanSet = ChanSet + "_" + system.getName() + ", " + "_" + system.getName() + "_out, ";
            }
            
            ChanSet = "chan " + ChanSet.substring(0,ChanSet.length() - 2) + ";";
            
            //Write file introduction
            String fileHeader = "<?xml version='1.0' encoding='utf-8'?><!DOCTYPE nta SYSTEM 'http://www.it.uu.se/research/group/darts/uppaal/flat-1_1.dtd'>" + eol + "<nta>" + eol + "<declaration>// Created with COBRA-Framework Export Tool v0.1" + eol + "// Developed by: Thomas Huybrechts - MOSAIC 2016" + eol + "// Place global variables here." + eol + gclock + eol + ChanSet + eol + "</declaration>" + eol;
            buffWriter.write(fileHeader);
            
            for(TimedAutomaton system : systems)
            {
                //Create model declaration
                String modelDeclaration = "<template>\n<name x=\"5\" y=\"5\">" + system.getName() + "</name>" + eol;
                buffWriter.write(modelDeclaration);

                //Write parameter list
                String parameterList = "<parameter>" + "</parameter>" + eol;
                buffWriter.write(parameterList);

                //Write local variables
                
                //Declare global clock
                String lclock = "clock x;";
                
                //Declare chan
                String nodeWCET = " ";
                
                for(Node node : system.getNodes())
                {	
                	if(node.getName().matches("r"+ ".*"))
                	{
                		nodeWCET = nodeWCET + node.getName() + "_WCET=20, ";
                	}
                	
                }
                
                nodeWCET = "int" + nodeWCET.substring(0,nodeWCET.length() - 2) + ";";
                
                String localVariables = "<declaration>" + "// Place local variables here." + eol + lclock + eol + nodeWCET +"</declaration>" + eol;
                buffWriter.write(localVariables);

                //Write nodes
                for(Node node : system.getNodes())
                {
                    String nodeString = "<location id=\"id" + node.getId() + "\" x=\"" + node.getLocX() + "\" y=\"" + node.getLocY() + "\"><name x=\"" + (node.getLocX() + 16) + "\" y=\"" + (node.getLocY() - 16) + "\">" + node.getName() + "</name>" + "<label kind=\"invariant\"" + " x=\"" + (node.getLocX() + 20) + "\" y=\"" + node.getLocY() + "\">" + (node.getName().matches("r"+ ".*") ? node.getInvariant() : "") + "</label>" + "<label kind=\"comments\">" + formatXMLString(node.getComments()) + "</label>" + (node.isCommitted() ? "<committed/>" : "" ) + "</location>" + eol;
                    buffWriter.write(nodeString);
                }
  
                //Write end node
                String exitnodeString = "<location id=\"id" + system.getEndNode().getId() + "\" x=\"" + system.getEndNode().getLocX() + "\" y=\"" + system.getEndNode().getLocY() + "\"><name x=\"" + (system.getEndNode().getLocX() + 16) + "\" y=\"" + (system.getEndNode().getLocY() - 16) + "\">" + system.getEndNode().getName() + "</name><label kind=\"comments\">" + formatXMLString(system.getEndNode().getComments()) + "</label>" + (system.getEndNode().isCommitted() ? "<committed/>" : "" ) + "</location>" + eol;
                buffWriter.write(exitnodeString);

                //Set initial node
                int i = 0;
                boolean initialFound = false;

                while(!initialFound && i < system.getNodes().size())
                {
                    if(system.getNodes().get(i).isInitial())
                    {
                        initialFound = true;
                    }
                    else
                    {
                        i++;
                    }
                }

                String initialNode = "<init ref=\"id" + system.getNodes().get(i).getId() + "\"/>" + eol;
                buffWriter.write(initialNode);

                //Write links
                for(Link link : system.getLinks())
                {
                    if(link.getTargetNode() != null)
                    {
                        int linkLocX = link.getSourceNode().getLocX() + 8;
                        int linkLocY = link.getSourceNode().getLocY();

                        String linkString = "<transition><source ref=\"id" + link.getSourceNode().getId() + "\"/><target ref=\"id" + link.getTargetNode().getId() + "\"/>";
                        buffWriter.write(linkString);

                        if(link.getSourceNode().equals(link.getTargetNode()))
                        {
                            linkLocY = linkLocY - 80;
                        }
                        else if(Math.abs(link.getSourceNode().getId() - link.getTargetNode().getId()) > 1)
                        {
                            if(link.getTargetNode().getId() > link.getSourceNode().getId())
                            {
                                //Escape link on right side
                                linkLocX = link.getSourceNode().getLocX() + 110;
                            }
                            else
                            {
                                //Escape link on left side
                                linkLocX = link.getSourceNode().getLocX() - 100;
                                linkLocY = linkLocY - 80;
                            }
                        }

                        //Set labels
                        if(link.getSelect() != null)
                        {
                            linkLocY = linkLocY + 16;

                            String selectLabel = "<label kind=\"select\" x=\"" + linkLocX + "\" y=\"" + linkLocY + "\">" + link.getSelect() + "</label>";
                            buffWriter.write(selectLabel);
                        }

                        if(link.getGuard() != null)
                        {
                            linkLocY = linkLocY + 16;

                            String guardLabel = "<label kind=\"guard\" x=\"" + linkLocX + "\" y=\"" + linkLocY + "\">" + link.getGuard() + "</label>";
                            buffWriter.write(guardLabel);
                        }

                        if(link.getSync() != null)
                        {
                            linkLocY = linkLocY + 16;

                            String syncLabel = "<label kind=\"synchronisation\" x=\"" + linkLocX + "\" y=\"" + linkLocY + "\">" + link.getSync() + "</label>";
                            buffWriter.write(syncLabel);
                        }

                        if(link.getUpdate() != null)
                        {
                            linkLocY = linkLocY + 16;

                            String updateLabel = "<label kind=\"assignment\" x=\"" + linkLocX + "\" y=\"" + linkLocY + "\">" + link.getUpdate() + "</label>";
                            buffWriter.write(updateLabel);
                        }

                        //Add nails for links
                        if(link.getSourceNode().equals(link.getTargetNode()))
                        {
                            String nailString = "<nail x=\"" + link.getSourceNode().getLocX() + "\" y=\"" + (link.getSourceNode().getLocY() - 32) + "\"/>" + "<nail x=\"" + (link.getSourceNode().getLocX() - 32) + "\" y=\"" + (link.getSourceNode().getLocY() - 32) + "\"/>" + "<nail x=\"" + (link.getSourceNode().getLocX() - 32) + "\" y=\"" + link.getSourceNode().getLocY() + "\"/>";
                            buffWriter.write(nailString);

                            linkLocY = linkLocY - 80;
                        }
                        else if(Math.abs(link.getSourceNode().getId() - link.getTargetNode().getId()) > 1)
                        {
                            int escapeLength = 105;
                            int diffLength = link.getTargetNode().getId() - link.getSourceNode().getId();
                            String nailString = new String();

                            if(link.getTargetNode().getId() > link.getSourceNode().getId())
                            {
                                //Escape link on right side
                                nailString = "<nail x=\"" + (link.getSourceNode().getLocX() + escapeLength) + "\" y=\"" + link.getSourceNode().getLocY() + "\"/>" + "<nail x=\"" + (link.getTargetNode().getLocX() + escapeLength) + "\" y=\"" + link.getTargetNode().getLocY() + "\"/>";
                            }
                            else
                            {
                                //Escape link on left side
                                nailString = "<nail x=\"" + (link.getSourceNode().getLocX() - escapeLength) + "\" y=\"" + link.getSourceNode().getLocY() + "\"/>" + "<nail x=\"" + (link.getTargetNode().getLocX() - escapeLength) + "\" y=\"" + link.getTargetNode().getLocY() + "\"/>";
                            }

                            buffWriter.write(nailString);
                        }

                        String linkFooter = "</transition>\"" + eol;
                        buffWriter.write(linkFooter);
                    }
                }

                //Write model declaration end
                String modelDeclarationEnd = "</template>" + eol;
                buffWriter.write(modelDeclarationEnd);
            }

            //Write system declaration
            
            //Template instantiations
            String templateinstantiation = "";
            //Processes 
            String processesdeclaration = "";
      
            int i = 0;
            
            for(TimedAutomaton system : systems)
            {
            	
            	templateinstantiation = templateinstantiation + "p_" + system.getName() + " = " +  system.getName() +"();" + eol;

            	processesdeclaration = processesdeclaration + "p_" + system.getName() + ", ";

            }
            
            processesdeclaration = "system " + processesdeclaration.substring(0,processesdeclaration.length() - 2) + ";";
           
            //Write system declaration end
            
            String systemDeclaration = "<system>// Place template instantiations here." + eol + eol + templateinstantiation + eol + "// List one or more processes to be composed into a system." + eol + processesdeclaration + eol + "</system>";
            
            buffWriter.write(systemDeclaration);

            //Write file closing
            String fileFooter = "</nta>";
            buffWriter.write(fileFooter);

            buffWriter.close();
        }
        catch(IOException e)
        {
            //Could not write to file
            throw new IOException("Could not write to file: " + exportFile + ".\n" + e.getMessage());
        }

        return 0;
    }

    private String formatXMLString(String string)
    {
        String formattedString = new String(string);

        //Escape XML syntax characters
        formattedString = formattedString.replaceAll("&", "&amp;");     // &
        formattedString = formattedString.replaceAll("\"", "&quot;");   // "
        formattedString = formattedString.replaceAll("'", "&apos;");    // '
        formattedString = formattedString.replaceAll("<", "&lt;");      // <
        formattedString = formattedString.replaceAll(">", "&gt;");      // >

        return formattedString;
    }
}
