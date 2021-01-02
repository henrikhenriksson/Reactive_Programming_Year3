package dt176g.hehe0601.paint;

/**
 * Exception class for handling exceptions where no end point was found.
 *
 * @author Henrik Henriksson (hehe0601)
 * @version 1.0
 * @since 2019-11-19
 */
public final class NoEndPointException extends Exception {

	/**
	 * Required version ID for all serializable classes.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default Constructor.
	 */
	public NoEndPointException() {
		super("A no End point Exception has occured.");
	}

	/**
	 * Constructor initialized with message string.
	 * 
	 * @param message
	 */
	public NoEndPointException(String message) {
		super(message);
	}

	/**
	 * Overridden toString method.
	 * 
	 * @return string exception message.
	 * @Override
	 */
	public String toString() {
		return "NoEndPointException [" + super.getMessage() + "]";
	}

}
