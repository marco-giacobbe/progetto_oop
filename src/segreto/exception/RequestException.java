package segreto.exception;

/**
Eccezione generica in fare di richiesta al server
*/

public class RequestException extends CSException {

        public RequestException() {
                super("richiesta non valida");
        }

        public RequestException(String msg) {
                super(msg);
        }

}
