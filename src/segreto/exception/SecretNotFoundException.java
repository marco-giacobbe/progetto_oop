package segreto.exception;

/**
Eccezione sollevata se il segreto cercato non esiste
*/
public class SecretNotFoundException extends RequestException {

        public SecretNotFoundException() {
                super("Segreto non trovato");
        }

        public SecretNotFoundException(String msg) {
                super(msg);
        }

}

