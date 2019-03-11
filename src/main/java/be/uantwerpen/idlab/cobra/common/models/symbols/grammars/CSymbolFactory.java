package be.uantwerpen.idlab.cobra.common.models.symbols.grammars;

import be.uantwerpen.idlab.cobra.common.models.symbols.*;
import be.uantwerpen.idlab.cobra.common.tools.terminal.Terminal;
import javafx.util.Pair;

import java.util.LinkedList;

public class CSymbolFactory implements SymbolFactory
{
	private static final int NO_ARGUMENT_FUNCTION_SIZE = 7;

	private static final Sequence<TokenType> SINGLE_VARIABLE_DECLARATION = new Sequence<TokenType>().insert(TokenType.DECLARATION).insert(TokenType.TYPE);
	private static final Sequence<TokenType> SINGLE_VARIABLE_INITIALIZATION = new Sequence<TokenType>().insert(TokenType.DECLARATION).insert(TokenType.DIRECT_DECLARATOR);

	private static final Sequence<TokenType> ARRAY_VARIABLE_DECLARATION = new Sequence<TokenType>().insert(TokenType.DECLARATION).insert(TokenType.TYPE).insert(TokenType.DIRECT_DECLARATOR).insert(TokenType.DIRECT_DECLARATOR);

	private static final Sequence<TokenType> FUNCTION_DEFINITION = new Sequence<TokenType>().insert(TokenType.FUNCTION_DEFINITION).insert(TokenType.TYPE).insert(TokenType.DIRECT_DECLARATOR).insert(TokenType.DIRECT_DECLARATOR);
	private static final Sequence<TokenType> FUNCTION_DECLARATION = new Sequence<TokenType>().insert(TokenType.DECLARATION).insert(TokenType.TYPE).insert(TokenType.DIRECT_DECLARATOR).insert(TokenType.DIRECT_DECLARATOR);

	private static final Sequence<TokenType> FUNCTION_PARAMETER = new Sequence<TokenType>().insert(TokenType.PARAMETER_DECLARATION).insert(TokenType.TYPE).insert(TokenType.DIRECT_DECLARATOR);

	private boolean scopeExitQueued;

	private Sequence<TokenType> tokens;
	private LinkedList<String> stringTokens;

	private Scope currentScope;
	private SymbolTable symbolTable;

	/*
	 *  PRIVATE METHODS
	 */
	private void leaveScope()
	{
		if(this.currentScope.getParent() != null)
		{
			this.currentScope = this.currentScope.getParent();
		}

		this.scopeExitQueued = false;
	}

	private boolean isParameter(int startIndex)
	{
		return (this.tokens.match(FUNCTION_PARAMETER, startIndex) != Sequence.END);
	}

	private void addFunctionDefinition(int numParams)
	{
		String returnType = this.stringTokens.get(1);
		String name = this.stringTokens.get(3);

		LinkedList<ParameterSymbol> parameters = new LinkedList<>();

		for(int j = 0; j < numParams; j++)
		{
			int paramStartIndex = 7 + (j * 3);

			parameters.add(new ParameterSymbol(this.stringTokens.get(paramStartIndex + 1), this.stringTokens.get(paramStartIndex + 2)));
		}

		Symbol functionSymbol = new FunctionSymbol(returnType, name, parameters);

		this.symbolTable.insert(this.currentScope, functionSymbol);
		this.currentScope = new Scope(name, this.currentScope);

		for(ParameterSymbol parameter : parameters)
		{
			this.symbolTable.insert(this.currentScope, parameter);
		}

		this.stringTokens.clear();   // Clear the list
		this.tokens.clear();
	}

	/* Variable Declaration =
	 * 0. Declaration Token             int k = cnt
	 * 1. Type Token                    int
	 * 2. Direct Declarator Token       k
	 */
	private boolean checkSingleVariable()
	{
		if(this.tokens.size() == 4)
		{
			boolean variablePossible = (this.tokens.match(SINGLE_VARIABLE_DECLARATION) != -1) || (this.tokens.match(SINGLE_VARIABLE_INITIALIZATION) != -1);

			variablePossible &= !(this.tokens.get(3) == TokenType.DIRECT_DECLARATOR);

			if(variablePossible)
			{
				this.symbolTable.insert(this.currentScope, new VariableSymbol(this.stringTokens.get(1), this.stringTokens.get(2)));
				this.stringTokens.clear();
				this.tokens.clear();

				return true;
			}
		}

		return false;
	}

	private boolean checkArrayVariable()
	{
		if(this.tokens.size() == 5)
		{
			boolean arrayPossible = (this.tokens.match(ARRAY_VARIABLE_DECLARATION) != -1);

			arrayPossible &= !(this.tokens.get(4) == TokenType.PARAMETER_LIST);

			if(arrayPossible)
			{
				String type = this.stringTokens.get(1);
				String name = this.stringTokens.get(3);
				String fullDecl = this.stringTokens.get(2);
				String sizeStr = fullDecl.substring(name.length() + 1, fullDecl.length() - 1);
				int size;

				try
				{
					size = Integer.parseInt(sizeStr);
				}
				catch(NumberFormatException e)
				{
					//Array size not defined (prototype declaration)
					size = -1;
				}

				this.symbolTable.insert(this.currentScope, new ArraySymbol(type, name, size));
				this.stringTokens.clear();
				this.tokens.clear();

				return true;
			}
		}

		return false;
	}

	/* Function Declaration =
	 * 0. Declaration Token             void bitonic_compare(int i, int j, int dir)             void bitonic_init (void)
	 * 1. Type Token                    void                                                    void
	 * 2. Direct Declarator Token       bitonic_compare(int i, int j, int dir)                  bitonic_init(void)
	 * 3. Direct Declarator Token       bitconic_compare                                        bitonic_init
	 * 4. ParameterSymbol List Token          int i, int j, int dir                             void
	 * 5. ParameterSymbol List Token          int i, int j
	 * 6. ParameterSymbol List Token          int i
	 * 7. ParameterSymbol Declaration Token   int i                                             void
	 * 8. Type Token                    int                                                     void
	 * 9. Direct Declarator Token       i
	 * 10. ParameterSymbol Declaration Token  int j
	 * 11. Type Token                   int
	 * 12. Direct Declarator Token      j
	 * 13. ParameterSymbol Declaration Token  int dir
	 * 14. Type Token                   int
	 * 15. Direct Declarator Token      dir
	 */
	private boolean checkFunctionDeclaration(Pair<TokenType, String> pair)
	{
		if(this.tokens.size() >= NO_ARGUMENT_FUNCTION_SIZE)
		{
			boolean functionPossible = this.tokens.match(FUNCTION_DECLARATION) != -1;

			// 4 - 15
			int numParams = 0;
			int i = 4;

			while((i < this.tokens.size()) && (this.tokens.get(i) == TokenType.PARAMETER_LIST))    // Count function parameters, functions with a single 'void' parameter are counted as having a single parameter right now
			{
				numParams++;
				i++;
			}

			if((this.tokens.size() == (NO_ARGUMENT_FUNCTION_SIZE + 1)) && (numParams == 1))
			{
				if(this.tokens.get(NO_ARGUMENT_FUNCTION_SIZE) != TokenType.DIRECT_DECLARATOR)  // Function with a single void parameter = Function with no arguments
				{
					numParams = 0;

					if(functionPossible)   // There currently is a function worth of tokenPairs in the list
					{
						this.stringTokens.clear();
						this.tokens.clear();

						if((pair.getKey() != null) && (pair.getValue() != null))
						{
							this.stringTokens.add(pair.getValue());
							this.tokens.insert(pair.getKey());
						}

						return true;
					}
				}
			}

			if(this.tokens.size() == (4 + numParams + (3 * numParams)))
			{
				for(int j = 0; j < numParams; j++)
				{
					int listIndex = 4 + numParams + (j * 3);

					functionPossible &= this.isParameter(listIndex);
				}

				if(functionPossible)   // There currently is a function worth of tokenPairs in the list
				{
					this.stringTokens.clear();
					this.tokens.clear();
					return true;
				}
			}
		}

		return false;
	}

	/* Function Definition =
	 * 0. Function Definition Token     void bitonic_compare(int i, int j, int dir)             void bitonic_init (void)
	 * 1. Type Token                    void                                                    void
	 * 2. Direct Declarator Token       bitonic_compare(int i, int j, int dir)                  bitonic_init(void)
	 * 3. Direct Declarator Token       bitconic_compare                                        bitonic_init
	 * 4. ParameterSymbol List Token          int i, int j, int dir                             void
	 * 5. ParameterSymbol List Token          int i, int j
	 * 6. ParameterSymbol List Token          int i
	 * 7. ParameterSymbol Declaration Token   int i                                             void
	 * 8. Type Token                    int                                                     void
	 * 9. Direct Declarator Token       i
	 * 10. ParameterSymbol Declaration Token  int j
	 * 11. Type Token                   int
	 * 12. Direct Declarator Token      j
	 * 13. ParameterSymbol Declaration Token  int dir
	 * 14. Type Token                   int
	 * 15. Direct Declarator Token      dir
	 */
	private boolean checkFunctionDefinition(Pair<TokenType, String> pair)
	{
		if(this.tokens.size() >= NO_ARGUMENT_FUNCTION_SIZE)
		{
			boolean functionPossible = this.tokens.match(FUNCTION_DEFINITION) != -1;

			int numParams = 0;
			int i = 4;

			while((i < this.tokens.size() && (this.tokens.get(i) == TokenType.PARAMETER_LIST)))    // Count function parameters, functions with a single 'void' parameter are counted as having a single parameter right now
			{
				numParams++;
				i++;
			}

			if((this.tokens.size() == (NO_ARGUMENT_FUNCTION_SIZE + 1)) && (numParams == 1))
			{
				if(this.tokens.get(NO_ARGUMENT_FUNCTION_SIZE) != TokenType.DIRECT_DECLARATOR)  // Function with a single void parameter = Function with no arguments
				{
					numParams = 0;

					if(functionPossible)   // There currently is a function worth of tokenPairs in the list
					{
						this.addFunctionDefinition(numParams);

						if((pair.getKey() != null) && (pair.getValue() != null))
						{
							this.stringTokens.add(pair.getValue());
							this.tokens.insert(pair.getKey());
						}

						return true;
					}
				}
			}
			else if((this.tokens.size() == (NO_ARGUMENT_FUNCTION_SIZE)) && (numParams == 1) && (pair.getKey() == null) && (pair.getValue() == null))
			{
				//todo: Check for end of compilation unit
				// This method was called from the flush method, we don't need to check if the next parameter is a direct declarator, because there won't be one

				if(functionPossible)   // There currently is a function worth of tokenPairs in the list
				{
					this.addFunctionDefinition(0);

					return true;
				}
			}

			if(this.tokens.size() == (4 + numParams + (3 * numParams)))
			{
				for(int j = 0; j < numParams; j++)
				{
					int listIndex = 4 + numParams + (j * 3);

					functionPossible &= this.isParameter(listIndex);
				}

				if(functionPossible)   // There currently is a function worth of tokenPairs in the list
				{
					this.addFunctionDefinition(numParams);

					return true;
				}
			}
		}

		return false;
	}

	/*
	 *      PUBLIC METHODS
	 */
	public CSymbolFactory()
	{
		this.tokens = new Sequence<TokenType>();
		this.stringTokens = new LinkedList<String>();
		this.currentScope = new Scope("global", null);
		this.symbolTable = new SymbolTable();
	}

	@Override
	public SymbolTable flush()
	{
		//todo: Note to people optimizing this software:
		//      This method can return after each of these checks, based on the result (true = return early)
		//      I didn't implement the early returning behavior in order to be able to print a proper message showing that there are still tokens left in the stream
		this.checkFunctionDeclaration(new Pair<TokenType, String>(null, null));
		this.checkFunctionDefinition(new Pair<TokenType, String>(null, null));
		this.checkArrayVariable();
		this.checkSingleVariable();

		if(!this.symbolTable.contains(this.currentScope))
		{
			this.symbolTable.insertScope(this.currentScope);
		}

		if(this.tokens.size() > 0)
		{
			Terminal.printTerminalWarning("There were still " + this.tokens.size() + " tokens left in the stream when flush() was called.");
		}

		return this.symbolTable;
	}

	@Override
	public SymbolTable getGeneratedSymbolTable()
	{
		return this.symbolTable;
	}

	@Override
	public void pushToken(TokenType type, String value)
	{
		this.tokens.insert(type);
		this.stringTokens.add(value);

		if(this.checkFunctionDeclaration(new Pair<TokenType, String>(type, value)))
		{
			if(this.scopeExitQueued)
			{
				this.leaveScope();
			}

			return;
		}


		if(this.checkFunctionDefinition(new Pair<TokenType, String>(type, value)))
		{
			if(this.scopeExitQueued)
			{
				this.leaveScope();
			}

			return;
		}

		if(this.checkArrayVariable())
		{
			// Re-add the final token because we waited 1 token 'too long' to be sure this isn't a function
			this.stringTokens.add(value);
			this.tokens.insert(type);

			if(this.scopeExitQueued)
			{
				this.leaveScope();
			}

			return;
		}

		if(this.checkSingleVariable())
		{
			// Re-add the final token because we waited 1 token 'too long' to be sure this isn't a function
			this.stringTokens.add(value);
			this.tokens.insert(type);

			if(this.scopeExitQueued)
			{
				this.leaveScope();
			}
		}
	}

	/**
	 * Used to indicate we are leaving a scope
	 * This creates a 'token' delay, to prevent problems with certain sequences needing a look-ahead
	 */
	@Override
	public void exitScope()
	{
		this.scopeExitQueued = true;
	}
}
