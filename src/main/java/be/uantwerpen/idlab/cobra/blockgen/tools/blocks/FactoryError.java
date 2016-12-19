package be.uantwerpen.idlab.cobra.blockgen.tools.blocks;

/**
 * Created by Thomas on 29/11/2016.
 */
public class FactoryError
{
    private String message;
    private String stacktrace;

    protected FactoryError()
    {

    }

    public FactoryError(String message, String stacktrace)
    {
        this.message = message;
        this.stacktrace = stacktrace;
    }

    public String getMessage()
    {
        return this.message;
    }

    public String getStacktrace()
    {
        return this.stacktrace;
    }

    @Override
    public String toString()
    {
        return this.message;
    }
}
