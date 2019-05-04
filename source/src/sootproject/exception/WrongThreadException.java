package sootproject.exception;

public class WrongThreadException extends Exception {
	private static WrongThreadException instance = new WrongThreadException();
	
	public final static WrongThreadException getWrongThreadException() {
		return instance;
	}
}
