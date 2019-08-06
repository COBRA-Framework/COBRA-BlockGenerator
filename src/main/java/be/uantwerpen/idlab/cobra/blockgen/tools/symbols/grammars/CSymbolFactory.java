package be.uantwerpen.idlab.cobra.blockgen.tools.symbols.grammars;

import be.uantwerpen.idlab.cobra.blockgen.tools.symbols.SymbolFactory;
import be.uantwerpen.idlab.cobra.common.models.symbols.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CSymbolFactory extends SymbolFactory
{
	private Scope currentScope;
	private List<VariableSymbol> functionScopeParameters;
	private boolean enteredNewScope;
	private Vector<Integer> enteredBlockScopes;

    public CSymbolFactory()
    {
        this.currentScope = new GlobalScope();
        this.enteredNewScope = false;
        this.enteredBlockScopes = new Vector<Integer>();
    }

	public void enterFunctionScope(Long blockId, String name, String returnType)
    {
        this.functionScopeParameters = new ArrayList<>();

        this.currentScope = new FunctionScope(getNextScopeId(), blockId, name, returnType, this.currentScope);
        this.enteredNewScope = true;
    }

    public void enterStatementScope(Long blockId, String name)
    {
        this.currentScope = new StatementScope(getNextScopeId(), blockId, name, this.currentScope);
        this.enteredNewScope = true;
    }

    public void enterBlockScope(int startIndex, int endIndex)
    {
        if(this.enteredNewScope)
        {
            //Skip block scope entry (already taken into account for by previous scope)
            this.enteredNewScope = false;
        }
        else
        {
            this.currentScope = new BlockScope(getNextScopeId(), startIndex, endIndex, this.currentScope);
            this.enteredBlockScopes.add(startIndex);
        }
    }

    public void exitFunctionScope()
    {
        if(!(this.currentScope instanceof FunctionScope))
        {
            throw new RuntimeException("Could not exit FUNCTION scope while inner scopes are not exited!");
        }

        ((FunctionScope)this.currentScope).addParameters(this.functionScopeParameters);
        this.exitScope();
    }

    public void exitBlockScope(int startIndex, int endIndex)
    {
        if(this.enteredBlockScopes.contains(startIndex))
        {
            this.exitScope();
            this.enteredBlockScopes.remove(((Integer)startIndex));
        }
    }

    public void exitScope()
    {
        if(this.currentScope.getParent() == null)
        {
            throw new RuntimeException("Could not exit GLOBAL scope!");
        }

        this.currentScope = this.currentScope.getParent();
    }

    public SymbolTable build()
    {
        if(this.currentScope.getParent() != null)
        {
            throw new RuntimeException("Failed to build Symbol Table. Scope: '" + this.currentScope.getClass().getSimpleName() + "' did not finish properly!\n" + this.currentScope.toString());
        }

        SymbolTable symbolTable = new SymbolTable(this.currentScope);

        return symbolTable;
    }

    public void addArraySymbol(String type, String identifier, int size)
    {
        ArraySymbol array = new ArraySymbol(getNextSymbolId(), type, identifier, size);

        this.currentScope.insertSymbol(array);
    }

    public void addVariableSymbol(String type, String identifier)
    {
        VariableSymbol variable = new VariableSymbol(getNextSymbolId(), type, identifier);

        this.currentScope.insertSymbol(variable);
    }

    public void addParameterArraySymbol(String type, String identifier, int size)
    {
        ArraySymbol array = new ArraySymbol(getNextSymbolId(), type, identifier, size);

        addParameterSymbol(array);
    }

    public void addParameterVariableSymbol(String type, String identifier)
    {
        VariableSymbol variable = new VariableSymbol(getNextSymbolId(), type, identifier);

        addParameterSymbol(variable);
    }

    private void addParameterSymbol(VariableSymbol variable)
    {
        if(this.currentScope.getParent() == null)
        {
            //Skip function prototype declarations
            return;
        }

        this.functionScopeParameters.add(variable);
    }
}
