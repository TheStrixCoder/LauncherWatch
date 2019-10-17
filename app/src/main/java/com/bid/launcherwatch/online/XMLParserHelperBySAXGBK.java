package com.bid.launcherwatch.online;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class XMLParserHelperBySAXGBK {
    static XMLContentHandler handler;

    public static List<OnlineClockSkinXMLNode> parseXML(InputStream input) {
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            handler = new XMLContentHandler();
            XMLReader reader = parser.getXMLReader();
            reader.setContentHandler(handler);
            reader.parse(new InputSource(new InputStreamReader(input, "GBK")));
            input.close();
            return handler.getOnlineThemes();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        return null;
    }
}
