package segreto.exception;

/**
Eccezione sollevata quando scade il timer di keepalive
*/
public class TimeoutException extends CSException {

        public TimeoutException() {
                super("Timeout scaduto");
        }

        public TimeoutException(String msg) {
                super(msg);
        }

}

