package be.uantwerpen.idlab.cobra.blockgen.tools.terminal;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Thomas on 13/11/2016.
 */
public abstract class Terminal
{
    private TerminalReader terminalReader;
    private static ColoredPrinter terminalPrinter = new ColoredPrinter.Builder(1, false).build();

    public Terminal()
    {
        terminalReader = new TerminalReader();

        terminalReader.getObserver().addObserver(new Observer()
        {
            @Override
            public void update(Observable source, Object object)
            {
                if(object != null)
                {
                    String command = ((String)object).trim();

                    if(!command.equals(""))
                    {
                        executeCommand((String) object);
                    }
                }

                activateTerminal();
            }
        });
    }

    public static void printTerminal(String message)
    {
        terminalPrinter.clear();
        terminalPrinter.print(message + "\n");
    }

    public static void printTerminalInfo(String message)
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        terminalPrinter.clear();
        terminalPrinter.print("[INFO - " + timeFormat.format(calendar.getTime()) + "]", Ansi.Attribute.BOLD, Ansi.FColor.GREEN, Ansi.BColor.NONE);
        terminalPrinter.print(" " + message + "\n", Ansi.Attribute.CLEAR, Ansi.FColor.WHITE, Ansi.BColor.NONE);
    }

    public static void printTerminalWarning(String message)
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        terminalPrinter.clear();
        terminalPrinter.print("[WARNING - " + timeFormat.format(calendar.getTime()) + "]", Ansi.Attribute.BOLD, Ansi.FColor.YELLOW, Ansi.BColor.NONE);
        terminalPrinter.print(" " + message + "\n", Ansi.Attribute.CLEAR, Ansi.FColor.WHITE, Ansi.BColor.NONE);
    }

    public static void printTerminalError(String message)
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        terminalPrinter.clear();
        terminalPrinter.print("[ERROR - " + timeFormat.format(calendar.getTime()) + "]", Ansi.Attribute.BOLD, Ansi.FColor.RED, Ansi.BColor.NONE);
        terminalPrinter.print(" " + message + "\n", Ansi.Attribute.CLEAR, Ansi.FColor.WHITE, Ansi.BColor.NONE);
    }

    public void activateTerminal()
    {
        new Thread(terminalReader).start();
    }

    abstract public void executeCommand(String commandString);

    private class TerminalReader implements Runnable
    {
        private TerminalObserver observer;

        public TerminalReader()
        {
            this.observer = new TerminalObserver();
        }

        public TerminalObserver getObserver()
        {
            return this.observer;
        }

        @Override
        public void run()
        {
            BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

            System.out.print("# ");

            try
            {
                String command = input.readLine();
                this.observer.setChanged();
                this.observer.notifyObservers(command);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private class TerminalObserver extends Observable
    {
        public void clearChanged()
        {
            super.clearChanged();
        }

        public void setChanged()
        {
            super.setChanged();
        }
    }
}
