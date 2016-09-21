package be.uantwerpen.cobra.blockgen.tools.export.blockbenchmarker;

import be.uantwerpen.cobra.blockgen.models.blocks.Block;
import be.uantwerpen.cobra.blockgen.models.blocks.MethodBlock;
import be.uantwerpen.cobra.blockgen.models.blocks.ProgramBlock;
import be.uantwerpen.cobra.blockgen.tools.export.uppaal.TimedAutomaton;
import be.uantwerpen.cobra.blockgen.tools.export.uppaal.models.Node;
import be.uantwerpen.cobra.blockgen.tools.interfaces.CodeParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Thomas on 09/05/2016.
 */
public class BlockBenchmarkExport
{
    public int generateSourceOfBlock(String fileLocation, Block codeBlock, CodeParser.Grammar grammar) throws Exception
    {
        int startRowNumber = codeBlock.getStartRowNumber();
        int endRowNumber = codeBlock.getEndRowNumber();
        String exportFile = fileLocation + "block_";

        //Generate file name
        if(startRowNumber != endRowNumber)
        {
            exportFile = exportFile.concat("r" + startRowNumber + "-" + endRowNumber);
        }
        else
        {
            exportFile = exportFile.concat("r" + startRowNumber);
        }

        switch(grammar)
        {
            case C:
                exportFile = exportFile.concat(".c");
                return writeCSourceFile(exportFile, codeBlock);
            default:
                throw new Exception("Exporting to:" + grammar.name() + " is not supported!");
        }
    }

    public int generateSourceOfTimedAutomaton(String fileLocation, TimedAutomaton automaton, CodeParser.Grammar grammar) throws Exception
    {
        for(Node node : automaton.getNodes())
        {
            generateSourceOfNode(fileLocation + automaton.getName() + "_", node, grammar);
        }

        return 0;
    }

    public int generateSourceOfNode(String fileLocation, Node node, CodeParser.Grammar grammar) throws Exception
    {
        String exportFile = fileLocation + "node_" + node.getName();

        switch(grammar)
        {
            case C:
                exportFile = exportFile.concat(".c");
                return writeCSourceFile(exportFile, node);
            default:
                throw new Exception("Exporting to:" + grammar.name() + " is not supported!");
        }
    }

    private int writeCSourceFile(String exportFile, Block codeBlock) throws Exception
    {
        File file;
        FileWriter writer;
        BufferedWriter buffWriter;

        if(codeBlock.getClass() == ProgramBlock.class)
        {
            throw new Exception("Program block export is not supported!");
        }

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

            //Write file introduction
            String fileHeader = "/* " + eol + eol + "\tCreated with COBRA-Framework Export Tool v0.1" + eol + "\tDeveloped by: Thomas Huybrechts - MOSAIC 2016" + eol + eol + "*/" + eol + eol;
            buffWriter.write(fileHeader);

            //Write include section
            String includeHeader = "/*" + eol + "\tInclude section" + eol + "*/" + eol + eol;
            buffWriter.write(includeHeader);

            includeHeader = "// Include header files here..." + eol + eol;
            buffWriter.write(includeHeader);

            //Forward declaration of function section
            String forwardDeclaration = "/*" + eol + "\tForward declaration of functions" + eol + "*/" + eol + eol;
            buffWriter.write(forwardDeclaration);

            if(codeBlock.getClass() == MethodBlock.class)
            {
                forwardDeclaration = codeBlock.getCodeSegment().toString() + ";" + eol;
            }
            else
            {
                forwardDeclaration = "void benchmarkBlock(void);" + eol;
            }

            forwardDeclaration = forwardDeclaration.concat("int main(void);" + eol + eol);
            buffWriter.write(forwardDeclaration);

            //Create benchmark function header
            String benchmarkFunctionHeader;

            if(codeBlock.getClass() == MethodBlock.class)
            {
                benchmarkFunctionHeader = "void " + ((MethodBlock)codeBlock).getMethodName() + "(void)" + eol + "{" + eol;
            }
            else
            {
                benchmarkFunctionHeader = "void benchmarkBlock(void)" + eol + "{" + eol;
            }
            buffWriter.write(benchmarkFunctionHeader);

            //Create function body
            if(codeBlock.getClass() == MethodBlock.class)
            {
                for(Block childBlock : codeBlock.getChildBlocks())
                {
                    buffWriter.write(childBlock.getCodeString() + eol);
                }
            }
            else
            {
                buffWriter.write(codeBlock.getCodeString() + eol);
            }

            //Create benchmark function footer
            String benchmarkFunctionFooter = "}" + eol + eol;
            buffWriter.write(benchmarkFunctionFooter);

            //Create main function
            String mainFunction = "int main(void)" + eol + "{" + eol + "\t";

            if(codeBlock.getClass() == MethodBlock.class)
            {
                mainFunction = mainFunction.concat(((MethodBlock)codeBlock).getMethodName() + "();" + eol + "}");
            }
            else
            {
                mainFunction = mainFunction.concat("benchmarkBlock();" + eol + "}" + eol);
            }
            buffWriter.write(mainFunction);

            buffWriter.close();
        }
        catch(IOException e)
        {
            //Could not write to file
            throw new IOException("Could not write to file: " + exportFile + ".\n" + e.getMessage());
        }

        return 0;
    }

    private int writeCSourceFile(String exportFile, Node node) throws Exception
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

            //Write file introduction
            String fileHeader = "/* " + eol + eol + "\tCreated with COBRA-Framework Export Tool v0.1" + eol + "\tDeveloped by: Thomas Huybrechts - MOSAIC 2016" + eol + eol + "*/" + eol + eol;
            buffWriter.write(fileHeader);

            //Write include section
            String includeHeader = "/*" + eol + "\tInclude section" + eol + "*/" + eol + eol;
            buffWriter.write(includeHeader);

            includeHeader = "// Include header files here..." + eol + eol;
            buffWriter.write(includeHeader);

            //Forward declaration of function section
            String forwardDeclaration = "/*" + eol + "\tForward declaration of functions" + eol + "*/" + eol + eol;
            buffWriter.write(forwardDeclaration);

            forwardDeclaration = "void benchmarkNode(void);" + eol;

            forwardDeclaration = forwardDeclaration.concat("int main(void);" + eol + eol);
            buffWriter.write(forwardDeclaration);

            //Create benchmark function header
            String benchmarkFunctionHeader;

            benchmarkFunctionHeader = "void benchmarkNode(void)" + eol + "{" + eol;

            buffWriter.write(benchmarkFunctionHeader);

            //Create function body
            buffWriter.write(node.getComments() + eol);

            //Create benchmark function footer
            String benchmarkFunctionFooter = "}" + eol + eol;
            buffWriter.write(benchmarkFunctionFooter);

            //Create main function
            String mainFunction = "int main(void)" + eol + "{" + eol + "\t";

            mainFunction = mainFunction.concat("benchmarkNode();" + eol + "}" + eol);

            buffWriter.write(mainFunction);

            buffWriter.close();
        }
        catch(IOException e)
        {
            //Could not write to file
            throw new IOException("Could not write to file: " + exportFile + ".\n" + e.getMessage());
        }

        return 0;
    }
}
