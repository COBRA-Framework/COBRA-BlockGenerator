package be.uantwerpen.idlab.cobra.blockgen.tools.symbols;

import be.uantwerpen.idlab.cobra.common.models.symbols.SymbolTable;

/**
 *  This interface represents a language-agnostic Factory class, that creates a symbol table from a stream of ANTLR rule-matches
 */
public abstract class SymbolFactory
{
	private static long symbolId;
	private static long scopeId;

	protected SymbolFactory()
	{
		this.symbolId = 0;
		this.scopeId = 0;
	}

	protected static long getNextSymbolId()
	{
		long id = symbolId;

		//Increment symbol id
		symbolId++;

		return id;
	}

	protected static long getNextScopeId()
	{
		long id = scopeId;

		//Increment scope id
		scopeId++;

		return id;
	}

	public abstract void enterFunctionScope(Long blockId, String name, String returnType);

	public abstract void enterStatementScope(Long blockId, String name);

	public abstract void enterBlockScope(int startIndex, int endIndex);

	public abstract void addVariableSymbol(String type, String identifier);

	public abstract void addArraySymbol(String type, String identifier, int size);

	public abstract void addParameterVariableSymbol(String type, String identifier);

	public abstract void addParameterArraySymbol(String type, String identifier, int size);

	public abstract void exitFunctionScope();

	public abstract void exitBlockScope(int startIndex, int endIndex);

	public abstract void exitScope();

	public abstract SymbolTable build();
}
