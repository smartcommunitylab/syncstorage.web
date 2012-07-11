package eu.trentorise.smartcampus.presentation.common.exception;

public class NotFoundException extends Exception {

	private static final long serialVersionUID = 2986975700056245523L;

	public NotFoundException(String s) {
		super(s);
	}

	public NotFoundException(Throwable cause) {
		super(cause);
	}

}
