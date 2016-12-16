package be.uantwerpen.idlab.cobra.blockgen.tools.blocks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 29/11/2016.
 */
public class FactoryErrorListener
{
    private List<FactoryError> errors;

    public FactoryErrorListener()
    {
        errors = new ArrayList<FactoryError>();
    }

    public void report(String message, String stacktrace)
    {
        errors.add(new FactoryError(message, stacktrace));
    }

    public void report(FactoryError error)
    {
        errors.add(error);
    }

    public List<FactoryError> getFactoryErrors()
    {
        return this.errors;
    }

    public int getNumberOfFactoryErrors()
    {
        return this.errors.size();
    }

    @Override
    public String toString()
    {
        String factoryMessages = "";

        if(!this.errors.isEmpty())
        {
            for(FactoryError factoryError : errors)
            {
                factoryMessages = factoryMessages.concat(factoryError.getMessage() + "\n");
            }

            factoryMessages = factoryMessages.substring(0, factoryMessages.length() - 1);
        }

        return factoryMessages;
    }
}
