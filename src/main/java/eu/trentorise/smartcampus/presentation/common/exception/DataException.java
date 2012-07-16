package eu.trentorise.smartcampus.presentation.common.exception;

public class DataException extends Exception {

	private static final long serialVersionUID = 8192286081992662655L;

	public DataException() {
		super();
	}

	public DataException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataException(String message) {
		super(message);
	}

	public DataException(Throwable cause) {
		super(cause);
	}

}
