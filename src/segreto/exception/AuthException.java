package segreto.exception;

/**
Eccezione generica in fase di autenticazione
*/
public class AuthException extends CSException {

        public AuthException() {
                super("errore durante l'autenticazione");
        }

        public AuthException(String msg) {
                super(msg);
        }

}
