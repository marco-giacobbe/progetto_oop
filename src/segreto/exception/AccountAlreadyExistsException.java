package segreto.exception;

/**
Eccezione sollevata se il client crea un account già esistente
*/
public class AccountAlreadyExistsException extends AuthException {

        public AccountAlreadyExistsException() {
                super("Account esistente");
        }

        public AccountAlreadyExistsException(String msg) {
                super(msg);
        }

}
