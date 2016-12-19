package be.uantwerpen.idlab.cobra.blockgen.services;

import be.uantwerpen.idlab.cobra.blockgen.tools.terminal.Terminal;

/**
 * Created by Thomas on 17/11/2016.
 */
public class TerminalService
{
    private Terminal terminal;

    public TerminalService()
    {
        terminal = new Terminal()
        {
            @Override
            public void executeCommand(String commandString)
            {
                parseCommand(commandString);
            }
        };
    }

    public void systemReady()
    {
        terminal.printTerminal("\nTerminal mode active...");
        terminal.printTerminal("Type 'help' to display the possible commands.");

        terminal.activateTerminal();
    }

    private void parseCommand(String commandString)
    {

    }

    private void exitSystem()
    {
        System.exit(0);
    }

    private void printHelp(String command)
    {
        switch(command.hashCode())
        {
            default:
                terminal.printTerminal("Available commands:");
                terminal.printTerminal("-------------------");
                terminal.printTerminal("'exit' : shutdown the server.");
                terminal.printTerminal("'help' / '?' : show all available commands.\n");
                break;
        }
    }
}
