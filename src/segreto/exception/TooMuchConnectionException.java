package segreto.exception;

/**
Eccezione sollevata se il server sta gestendo il massimo numero di connessioni possibile
*/

public class TooMuchConnectionException extends CSException {

	public TooMuchConnectionException() {
		super("Troppe connessioni, riprovare più tardi");
	}

	public TooMuchConnectionException(String msg) {
		super(msg);
	}

}
