package exception;

public class EspressoTagnameNotFoundException extends RuntimeException{
	private static EspressoTagnameNotFoundException instance = new EspressoTagnameNotFoundException();
	
	public final static EspressoTagnameNotFoundException getException() {
		return instance;
	}

}
