package be.uantwerpen.idlab.cobra.blockgen.tools.symbols;

import be.uantwerpen.idlab.cobra.common.models.symbols.SymbolTable;

/**
 *  This interface represents a language-agnostic Factory class, that creates a symbol table from a stream of ANTLR rule-matches
 */
public interface SymbolFactory
{
	void enterFunctionScope(Long blockId, String name, String returnType);

	void enterStatementScope(Long blockId, String name);

	void enterBlockScope(int startIndex, int endIndex);

    void addVariableSymbol(String type, String identifier);

    void addArraySymbol(String type, String identifier, int size);

	void addParameterVariableSymbol(String type, String identifier);

	void addParameterArraySymbol(String type, String identifier, int size);

	void exitFunctionScope();

	void exitBlockScope(int startIndex, int endIndex);

	void exitScope();

	SymbolTable build();
}
