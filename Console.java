import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 *
 * @author Paola Campos da Silva 
 * 
 * @author Paulo Pagliosa (base)
 */
public class Console
{
  private final static InputStreamReader keyboard;
  private final static BufferedReader in;
  private final static PrintStream out;

  static
  {
    keyboard = new InputStreamReader(System.in);
    in = new BufferedReader(keyboard);
    out = System.out;
  }

  // ---------------------------------------------------------------
  // Funções Estilização do Menu - Adicionadas
  // ---------------------------------------------------------------
  
  public static void printHeader(String title) {
    out.println("\n==================================================");
    int padding = (50 - title.length()) / 2;
    for (int i = 0; i < padding; i++) out.print(" ");
    out.println(title);
    out.println("==================================================");
  }

  public static void printSubHeader(String title) {
    out.println("\n--------------------------------------------------");
    out.println("  " + title);
    out.println("--------------------------------------------------");
  }

  public static void info(String message) {
    out.println("  [Info] " + message);
  }

  // ---------------------------------------------------------------
  // Métodos Originais Modificados
  // ---------------------------------------------------------------

  private static void prompt(String message)
  {
    // Adiciona uma formatação mais limpa antes de ler o dado
    out.print("   > " + message + ": ");
  }

  public static void error(String message)
  {
    // Destaca o erro visualmente
    out.println("\n   [!] Error: " + message + ". Try again.");
  }

  private static void inputError()
  {
    error("input");
  }

  final public static void println(String s)
  {
    out.println(s);
  }

  final public static void println(int i)
  {
    out.println(i);
  }

  final public static void println(Object object)
  {
    out.println(object);
  }

  final public static void printf(String fmt, Object... args)
  {
    out.printf(fmt, args);
  }

  public static String readString(String message)
  {
    for (;;)
    {
      prompt(message);
      try
      {
        return in.readLine();
      }
      catch (IOException e)
      {
        inputError();
      }
    }
  }

  public static int readChar(String message)
  {
    for (;;)
    {
      prompt(message);
      try
      {
        return keyboard.read();
      }
      catch (IOException e)
      {
        inputError();
      }
    }
  }

  public static int readInt(String message)
  {
    for (;;)
      try
      {
        return Integer.parseInt(readString(message));
      }
      catch (NumberFormatException e)
      {
        error("integer expected");
      }
  }

  public static float readFloat(String message)
  {
    for (;;)
      try
      {
        return Float.parseFloat(readString(message));
      }
      catch (NumberFormatException e)
      {
        error("float expected");
      }
  }

  public static char readOption(String message, String options)
  {
    for (;;)
    {
      prompt(message);
      try
      {
        String input = in.readLine();
        int index = input.equals("") ? 0 : options.indexOf(input.charAt(0));

        if (index != -1)
          return options.charAt(index);
        error("invalid option");
      }
      catch (IOException e)
      {
        inputError();
      }
    }
  }

  public static Date readDate(String message)
  {
    for (DateFormat df = DateFormat.getDateInstance();;)
      try
      {
        return df.parse(readString(message));
      }
      catch (ParseException e)
      {
        error("invalid date format");
      }
  }

} // Console
