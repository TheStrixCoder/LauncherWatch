package com.bid.launcherwatch.online;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLContentHandler extends DefaultHandler {
    private String lastUpdateTime = "";
    private List<OnlineClockSkinXMLNode> mOnlineClockSkin = null;
    private OnlineClockSkinXMLNode mOnlineClockSkinNode;
    private String tagName = null;

    public List<OnlineClockSkinXMLNode> getOnlineThemes() {
        return this.mOnlineClockSkin;
    }

    public void startDocument() throws SAXException {
        this.mOnlineClockSkin = new ArrayList();
    }

    public void endDocument() throws SAXException {
        super.endDocument();
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (localName.equals("clockskin")) {
            this.mOnlineClockSkinNode = new OnlineClockSkinXMLNode();
        }
        this.tagName = localName;
        if (localName.equals("title")) {
            this.mOnlineClockSkinNode.setName(atts.getValue(0));
        } else if (localName.equals("skinid")) {
            this.mOnlineClockSkinNode.setSkinId(atts.getValue(0));
        } else if (localName.equals("customer")) {
            this.mOnlineClockSkinNode.setCustomer(atts.getValue(0));
        } else if (localName.equals("type")) {
            this.mOnlineClockSkinNode.setClockTpye(atts.getValue(0));
        } else if (localName.equals("file")) {
            this.mOnlineClockSkinNode.setFilePath(atts.getValue(0));
            this.mOnlineClockSkinNode.setPreview(atts.getValue(0).split("\\.")[0] + ".png");
        } else if (localName.equals("last")) {
            this.lastUpdateTime = atts.getValue(0);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("clockskin")) {
            if (this.mOnlineClockSkinNode.getClockTpye().equals("R-400") && (this.mOnlineClockSkinNode.getCustomer().equals("wiite") || this.mOnlineClockSkinNode.getCustomer().equals("KY"))) {
                this.mOnlineClockSkin.add(this.mOnlineClockSkinNode);
            }
            this.mOnlineClockSkinNode = null;
        }
        Log.d("XMLP", "endElement  localName = " + localName);
        this.tagName = null;
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
    }
}
