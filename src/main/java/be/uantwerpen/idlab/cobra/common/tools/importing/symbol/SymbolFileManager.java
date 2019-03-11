package be.uantwerpen.idlab.cobra.common.tools.importing.symbol;

import be.uantwerpen.idlab.cobra.blockgen.tools.symbols.SymbolTable;
import be.uantwerpen.idlab.cobra.common.models.ProjectConfig;
import be.uantwerpen.idlab.cobra.common.tools.importing.symbol.versions.SymbolFileParserV1;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SymbolFileManager
{
    List<SymbolFileParser> symbolParsers;

    public SymbolFileManager()
    {
        symbolParsers = new ArrayList<SymbolFileParser>();

        //Add symbol parser versions
        symbolParsers.add(new SymbolFileParserV1());
    }

    public List<SymbolTable> parseSymbolTable(String fileLocation, ProjectConfig projectConfig) throws Exception
    {
        SymbolFileParser parser = null;

        File symbolFile = new File(fileLocation);

        if(!symbolFile.exists() || !symbolFile.isFile())
        {
            throw new Exception("The provided path is not a symbol configuration file! Input:" + fileLocation);
        }

        boolean found = false;
        Iterator<SymbolFileParser> it = symbolParsers.iterator();
        while(it.hasNext() && !found)
        {
            parser = it.next();

            try
            {
                if(parser.isCompatible(symbolFile))
                {
                    found = true;
                }
            }
            catch(Exception e)
            {
                //Parser is not compatible or symbol file is corrupted
                continue;
            }
        }

        if(!found)
        {
            throw new Exception("The provided symbol file is not compatible with this application version or the file is corrupted!");
        }

        return parser.parseSymbolFile(symbolFile, projectConfig);
    }
}
