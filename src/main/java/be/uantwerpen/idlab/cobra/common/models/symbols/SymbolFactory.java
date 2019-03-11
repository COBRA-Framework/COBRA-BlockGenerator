package be.uantwerpen.idlab.cobra.common.models.symbols;

/**
 *  This interface represents a language-agnostic Factory class, that creates a symbol table from a stream of ANTLR rule-matches
 */
public interface SymbolFactory
{
	/**
	 *  These are all the types of tokens necessary to build a complete symbol table
	 */
	enum TokenType
	{
		DECLARATION,
		DIRECT_DECLARATOR,
		PARAMETER_DECLARATION,
		PARAMETER_LIST,
		TYPE,
		FUNCTION_DEFINITION
	}

	/**
	 * This method should be called after the entire file has been parsed, it will flush the last tokens from the queue
	 * @return
	 */
	SymbolTable flush();

	SymbolTable getGeneratedSymbolTable();

	/**
	 * This method should be called any time one of the tokens in TokenType is encountered.
	 * @see TokenType
	 * @param type      The type of token to be added to the sequence
	 * @param value     The string value of the token
	 */
	void pushToken(TokenType type, String value);

	void exitScope();
}
