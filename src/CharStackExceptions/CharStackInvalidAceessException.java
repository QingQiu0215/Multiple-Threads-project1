package CharStackExceptions;

public class CharStackInvalidAceessException extends Exception
{
	public CharStackInvalidAceessException()
    {
            super("Invalid piPosition specified.");
    }
    public CharStackInvalidAceessException (int piPosition)
    {
            super ("Invalid piPosition specified: " + piPosition);
     }
}
