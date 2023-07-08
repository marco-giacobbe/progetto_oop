package segreto.terminal.config;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

/**
Definisce un generico Handler per il parsing SAX
da utilizzare quando l'XML è strutturato in modo che
tutti i figli della root tag sono anche foglie
*/
public abstract class AbstractHandler extends DefaultHandler {

	/*
	Il parsing si basa sul fatto che il numero dei parametri
	da leggere è costante, di conseguenza è possibile saperlo a priori.
	È possibile leggere quindi tutti i parametri tramite un contatore
	e sapendo il nome del tag radice in modo da scartarlo
	*/
	private int counter = -1;
	private String[] data;
	private boolean read = false;
	private String firstTagName;

	// chiamato dal costruttore dell'handler concreto
	public AbstractHandler(String firstTagName, int childNumber) {
		this.firstTagName = firstTagName;
		data = new String[childNumber];
	}

	@Override
        public void startElement(String namespaceURI, String localName,
                                String rawName, Attributes atts) {
                if (!rawName.equals(firstTagName)) {
                        counter++;
                        read=true;
                }
        }

        @Override
        public void characters(char ch[], int start, int length) {
                if (read) {
                        data[counter] = new String(ch, start, length);
                        read = false;
                }
        }

	public String[] getData() {
		return data;
	}

}
