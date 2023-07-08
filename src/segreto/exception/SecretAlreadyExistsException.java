package segreto.exception;

/**
Eccezione sollevata se il segreto da caricare esiste già
*/

public class SecretAlreadyExistsException extends RequestException {

        public SecretAlreadyExistsException() {
                super("Segreto già esistente");
        }

        public SecretAlreadyExistsException(String msg) {
                super(msg);
        }

}
