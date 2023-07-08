package segreto.terminal.config;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import java.io.IOException;

public class XMLManager {

	private XMLReader xmlReader;
	private AbstractHandler handler;
	private String filename;

	public XMLManager(String filename, AbstractHandler handler) {
		this.filename = filename;
		try {
			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			xmlReader = saxParser.getXMLReader();
			this.handler = handler;
			xmlReader.setContentHandler(handler);
		} catch (ParserConfigurationException|SAXException e) {
			System.out.println(e.getMessage());
		}
	}

	public String[] parse() {
		try {
			xmlReader.parse(filename);
		} catch (IOException|SAXException e) {
			System.out.println(e.getMessage());
		}
		return handler.getData();
	}

}
