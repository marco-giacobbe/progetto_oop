package segreto.exception;

/**
Eccezione dipendente esclusivamente dal server
*/
public class ServerSideException extends CSException {

        public ServerSideException() {
                super("errore lato server");
        }

        public ServerSideException(String msg) {
                super(msg);
        }

}
