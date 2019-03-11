package be.uantwerpen.idlab.cobra.common.tools.importing.symbol;

import be.uantwerpen.idlab.cobra.common.models.symbols.SymbolTable;
import be.uantwerpen.idlab.cobra.common.models.ProjectConfig;

import java.io.File;
import java.util.List;

public interface SymbolFileParser
{
    String getVersionCompatibility();
    Boolean isCompatible(File symbolFile) throws Exception;
    List<SymbolTable> parseSymbolFile(File symbolFile, ProjectConfig config) throws Exception;
}
