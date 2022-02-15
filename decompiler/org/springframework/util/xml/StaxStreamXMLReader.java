package org.springframework.util.xml;

import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.AttributesImpl;

































class StaxStreamXMLReader
  extends AbstractStaxXMLReader
{
  private static final String DEFAULT_XML_VERSION = "1.0";
  private final XMLStreamReader reader;
  private String xmlVersion = "1.0";




  
  @Nullable
  private String encoding;




  
  StaxStreamXMLReader(XMLStreamReader reader) {
    int event = reader.getEventType();
    if (event != 7 && event != 1) {
      throw new IllegalStateException("XMLEventReader not at start of document or element");
    }
    this.reader = reader;
  }


  
  protected void parseInternal() throws SAXException, XMLStreamException {
    boolean documentStarted = false;
    boolean documentEnded = false;
    int elementDepth = 0;
    int eventType = this.reader.getEventType();
    while (true) {
      if (eventType != 7 && eventType != 8 && !documentStarted) {
        
        handleStartDocument();
        documentStarted = true;
      } 
      switch (eventType) {
        case 1:
          elementDepth++;
          handleStartElement();
          break;
        case 2:
          elementDepth--;
          if (elementDepth >= 0) {
            handleEndElement();
          }
          break;
        case 3:
          handleProcessingInstruction();
          break;
        case 4:
        case 6:
        case 12:
          handleCharacters();
          break;
        case 7:
          handleStartDocument();
          documentStarted = true;
          break;
        case 8:
          handleEndDocument();
          documentEnded = true;
          break;
        case 5:
          handleComment();
          break;
        case 11:
          handleDtd();
          break;
        case 9:
          handleEntityReference();
          break;
      } 
      if (this.reader.hasNext() && elementDepth >= 0) {
        eventType = this.reader.next();
        
        continue;
      } 
      break;
    } 
    if (!documentEnded) {
      handleEndDocument();
    }
  }
  
  private void handleStartDocument() throws SAXException {
    if (7 == this.reader.getEventType()) {
      String xmlVersion = this.reader.getVersion();
      if (StringUtils.hasLength(xmlVersion)) {
        this.xmlVersion = xmlVersion;
      }
      this.encoding = this.reader.getCharacterEncodingScheme();
    } 
    
    ContentHandler contentHandler = getContentHandler();
    if (contentHandler != null) {
      final Location location = this.reader.getLocation();
      contentHandler.setDocumentLocator(new Locator2()
          {
            public int getColumnNumber() {
              return (location != null) ? location.getColumnNumber() : -1;
            }
            
            public int getLineNumber() {
              return (location != null) ? location.getLineNumber() : -1;
            }
            
            @Nullable
            public String getPublicId() {
              return (location != null) ? location.getPublicId() : null;
            }
            
            @Nullable
            public String getSystemId() {
              return (location != null) ? location.getSystemId() : null;
            }
            
            public String getXMLVersion() {
              return StaxStreamXMLReader.this.xmlVersion;
            }
            
            @Nullable
            public String getEncoding() {
              return StaxStreamXMLReader.this.encoding;
            }
          });
      contentHandler.startDocument();
      if (this.reader.standaloneSet()) {
        setStandalone(this.reader.isStandalone());
      }
    } 
  }
  
  private void handleStartElement() throws SAXException {
    if (getContentHandler() != null) {
      QName qName = this.reader.getName();
      if (hasNamespacesFeature()) {
        int i; for (i = 0; i < this.reader.getNamespaceCount(); i++) {
          startPrefixMapping(this.reader.getNamespacePrefix(i), this.reader.getNamespaceURI(i));
        }
        for (i = 0; i < this.reader.getAttributeCount(); i++) {
          String prefix = this.reader.getAttributePrefix(i);
          String namespace = this.reader.getAttributeNamespace(i);
          if (StringUtils.hasLength(namespace)) {
            startPrefixMapping(prefix, namespace);
          }
        } 
        getContentHandler().startElement(qName.getNamespaceURI(), qName.getLocalPart(), 
            toQualifiedName(qName), getAttributes());
      } else {
        
        getContentHandler().startElement("", "", toQualifiedName(qName), getAttributes());
      } 
    } 
  }
  
  private void handleEndElement() throws SAXException {
    if (getContentHandler() != null) {
      QName qName = this.reader.getName();
      if (hasNamespacesFeature()) {
        getContentHandler().endElement(qName.getNamespaceURI(), qName.getLocalPart(), toQualifiedName(qName));
        for (int i = 0; i < this.reader.getNamespaceCount(); i++) {
          String prefix = this.reader.getNamespacePrefix(i);
          if (prefix == null) {
            prefix = "";
          }
          endPrefixMapping(prefix);
        } 
      } else {
        
        getContentHandler().endElement("", "", toQualifiedName(qName));
      } 
    } 
  }
  
  private void handleCharacters() throws SAXException {
    if (12 == this.reader.getEventType() && getLexicalHandler() != null) {
      getLexicalHandler().startCDATA();
    }
    if (getContentHandler() != null) {
      getContentHandler().characters(this.reader.getTextCharacters(), this.reader
          .getTextStart(), this.reader.getTextLength());
    }
    if (12 == this.reader.getEventType() && getLexicalHandler() != null) {
      getLexicalHandler().endCDATA();
    }
  }
  
  private void handleComment() throws SAXException {
    if (getLexicalHandler() != null) {
      getLexicalHandler().comment(this.reader.getTextCharacters(), this.reader
          .getTextStart(), this.reader.getTextLength());
    }
  }
  
  private void handleDtd() throws SAXException {
    if (getLexicalHandler() != null) {
      Location location = this.reader.getLocation();
      getLexicalHandler().startDTD(null, location.getPublicId(), location.getSystemId());
    } 
    if (getLexicalHandler() != null) {
      getLexicalHandler().endDTD();
    }
  }
  
  private void handleEntityReference() throws SAXException {
    if (getLexicalHandler() != null) {
      getLexicalHandler().startEntity(this.reader.getLocalName());
    }
    if (getLexicalHandler() != null) {
      getLexicalHandler().endEntity(this.reader.getLocalName());
    }
  }
  
  private void handleEndDocument() throws SAXException {
    if (getContentHandler() != null) {
      getContentHandler().endDocument();
    }
  }
  
  private void handleProcessingInstruction() throws SAXException {
    if (getContentHandler() != null) {
      getContentHandler().processingInstruction(this.reader.getPITarget(), this.reader.getPIData());
    }
  }
  
  private Attributes getAttributes() {
    AttributesImpl attributes = new AttributesImpl(); int i;
    for (i = 0; i < this.reader.getAttributeCount(); i++) {
      String namespace = this.reader.getAttributeNamespace(i);
      if (namespace == null || !hasNamespacesFeature()) {
        namespace = "";
      }
      String type = this.reader.getAttributeType(i);
      if (type == null) {
        type = "CDATA";
      }
      attributes.addAttribute(namespace, this.reader.getAttributeLocalName(i), 
          toQualifiedName(this.reader.getAttributeName(i)), type, this.reader.getAttributeValue(i));
    } 
    if (hasNamespacePrefixesFeature()) {
      for (i = 0; i < this.reader.getNamespaceCount(); i++) {
        String qName, prefix = this.reader.getNamespacePrefix(i);
        String namespaceUri = this.reader.getNamespaceURI(i);
        
        if (StringUtils.hasLength(prefix)) {
          qName = "xmlns:" + prefix;
        } else {
          
          qName = "xmlns";
        } 
        attributes.addAttribute("", "", qName, "CDATA", namespaceUri);
      } 
    }
    
    return attributes;
  }
}
