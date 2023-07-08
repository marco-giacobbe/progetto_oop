package segreto.exception;

/**
Eccezione sollevata se i dati di autenticazione sono errati
*/
public class BadSigninDataException extends AuthException {

        public BadSigninDataException() {
                super("Credenziali non corrette");
        }

        public BadSigninDataException(String msg) {
                super(msg);
        }

}
