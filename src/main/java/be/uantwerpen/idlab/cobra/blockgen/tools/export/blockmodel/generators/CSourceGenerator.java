package be.uantwerpen.idlab.cobra.blockgen.tools.export.blockmodel.generators;

import be.uantwerpen.idlab.cobra.blockgen.models.blocks.Block;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.MethodBlock;
import be.uantwerpen.idlab.cobra.blockgen.models.blocks.ProgramBlock;
import be.uantwerpen.idlab.cobra.blockgen.tools.export.blockmodel.SourceGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Thomas on 27/09/2016.
 */
public class CSourceGenerator extends BasicSourceGenerator implements SourceGenerator
{
    public int writeSourceFile(String exportFile, Block codeBlock) throws Exception
    {
        File file;
        FileWriter writer;
        BufferedWriter buffWriter;

        if(codeBlock.getClass() == ProgramBlock.class)
        {
            throw new Exception("Program block export is not supported!");
        }

        //Get file
        file = openFile(exportFile);

        try
        {
            String eol = System.getProperty("line.separator");
            writer = new FileWriter(file.getAbsoluteFile());
            buffWriter = new BufferedWriter(writer);

            //Write file introduction
            String fileHeader = "/* " + eol + eol + getSourceHeader() + eol + eol + "*/" + eol + eol;
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
}
