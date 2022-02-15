package org.springframework.util.xml;

import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.NotationDeclaration;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.Locator2;
import org.xml.sax.helpers.AttributesImpl;



































class StaxEventXMLReader
  extends AbstractStaxXMLReader
{
  private static final String DEFAULT_XML_VERSION = "1.0";
  private final XMLEventReader reader;
  private String xmlVersion = "1.0";




  
  @Nullable
  private String encoding;




  
  StaxEventXMLReader(XMLEventReader reader) {
    try {
      XMLEvent event = reader.peek();
      if (event != null && !event.isStartDocument() && !event.isStartElement()) {
        throw new IllegalStateException("XMLEventReader not at start of document or element");
      }
    }
    catch (XMLStreamException ex) {
      throw new IllegalStateException("Could not read first element: " + ex.getMessage());
    } 
    this.reader = reader;
  }


  
  protected void parseInternal() throws SAXException, XMLStreamException {
    boolean documentStarted = false;
    boolean documentEnded = false;
    int elementDepth = 0;
    while (this.reader.hasNext() && elementDepth >= 0) {
      XMLEvent event = this.reader.nextEvent();
      if (!event.isStartDocument() && !event.isEndDocument() && !documentStarted) {
        handleStartDocument(event);
        documentStarted = true;
      } 
      switch (event.getEventType()) {
        case 7:
          handleStartDocument(event);
          documentStarted = true;
        
        case 1:
          elementDepth++;
          handleStartElement(event.asStartElement());
        
        case 2:
          elementDepth--;
          if (elementDepth >= 0) {
            handleEndElement(event.asEndElement());
          }
        
        case 3:
          handleProcessingInstruction((ProcessingInstruction)event);
        
        case 4:
        case 6:
        case 12:
          handleCharacters(event.asCharacters());
        
        case 8:
          handleEndDocument();
          documentEnded = true;
        
        case 14:
          handleNotationDeclaration((NotationDeclaration)event);
        
        case 15:
          handleEntityDeclaration((EntityDeclaration)event);
        
        case 5:
          handleComment((Comment)event);
        
        case 11:
          handleDtd((DTD)event);
        
        case 9:
          handleEntityReference((EntityReference)event);
      } 
    
    } 
    if (documentStarted && !documentEnded) {
      handleEndDocument();
    }
  }

  
  private void handleStartDocument(XMLEvent event) throws SAXException {
    if (event.isStartDocument()) {
      StartDocument startDocument = (StartDocument)event;
      String xmlVersion = startDocument.getVersion();
      if (StringUtils.hasLength(xmlVersion)) {
        this.xmlVersion = xmlVersion;
      }
      if (startDocument.encodingSet()) {
        this.encoding = startDocument.getCharacterEncodingScheme();
      }
    } 
    
    ContentHandler contentHandler = getContentHandler();
    if (contentHandler != null) {
      final Location location = event.getLocation();
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
              return StaxEventXMLReader.this.xmlVersion;
            }
            
            @Nullable
            public String getEncoding() {
              return StaxEventXMLReader.this.encoding;
            }
          });
      contentHandler.startDocument();
    } 
  }
  
  private void handleStartElement(StartElement startElement) throws SAXException {
    if (getContentHandler() != null) {
      QName qName = startElement.getName();
      if (hasNamespacesFeature()) {
        for (Iterator<Namespace> iterator = startElement.getNamespaces(); iterator.hasNext(); ) {
          Namespace namespace = iterator.next();
          startPrefixMapping(namespace.getPrefix(), namespace.getNamespaceURI());
        } 
        for (Iterator<Attribute> i = startElement.getAttributes(); i.hasNext(); ) {
          Attribute attribute = i.next();
          QName attributeName = attribute.getName();
          startPrefixMapping(attributeName.getPrefix(), attributeName.getNamespaceURI());
        } 
        
        getContentHandler().startElement(qName.getNamespaceURI(), qName.getLocalPart(), toQualifiedName(qName), 
            getAttributes(startElement));
      } else {
        
        getContentHandler().startElement("", "", toQualifiedName(qName), getAttributes(startElement));
      } 
    } 
  }
  
  private void handleCharacters(Characters characters) throws SAXException {
    char[] data = characters.getData().toCharArray();
    if (getContentHandler() != null && characters.isIgnorableWhiteSpace()) {
      getContentHandler().ignorableWhitespace(data, 0, data.length);
      return;
    } 
    if (characters.isCData() && getLexicalHandler() != null) {
      getLexicalHandler().startCDATA();
    }
    if (getContentHandler() != null) {
      getContentHandler().characters(data, 0, data.length);
    }
    if (characters.isCData() && getLexicalHandler() != null) {
      getLexicalHandler().endCDATA();
    }
  }
  
  private void handleEndElement(EndElement endElement) throws SAXException {
    if (getContentHandler() != null) {
      QName qName = endElement.getName();
      if (hasNamespacesFeature()) {
        getContentHandler().endElement(qName.getNamespaceURI(), qName.getLocalPart(), toQualifiedName(qName));
        for (Iterator<Namespace> i = endElement.getNamespaces(); i.hasNext(); ) {
          Namespace namespace = i.next();
          endPrefixMapping(namespace.getPrefix());
        } 
      } else {
        
        getContentHandler().endElement("", "", toQualifiedName(qName));
      } 
    } 
  }

  
  private void handleEndDocument() throws SAXException {
    if (getContentHandler() != null) {
      getContentHandler().endDocument();
    }
  }
  
  private void handleNotationDeclaration(NotationDeclaration declaration) throws SAXException {
    if (getDTDHandler() != null) {
      getDTDHandler().notationDecl(declaration.getName(), declaration.getPublicId(), declaration.getSystemId());
    }
  }
  
  private void handleEntityDeclaration(EntityDeclaration entityDeclaration) throws SAXException {
    if (getDTDHandler() != null) {
      getDTDHandler().unparsedEntityDecl(entityDeclaration.getName(), entityDeclaration.getPublicId(), entityDeclaration
          .getSystemId(), entityDeclaration.getNotationName());
    }
  }
  
  private void handleProcessingInstruction(ProcessingInstruction pi) throws SAXException {
    if (getContentHandler() != null) {
      getContentHandler().processingInstruction(pi.getTarget(), pi.getData());
    }
  }
  
  private void handleComment(Comment comment) throws SAXException {
    if (getLexicalHandler() != null) {
      char[] ch = comment.getText().toCharArray();
      getLexicalHandler().comment(ch, 0, ch.length);
    } 
  }
  
  private void handleDtd(DTD dtd) throws SAXException {
    if (getLexicalHandler() != null) {
      Location location = dtd.getLocation();
      getLexicalHandler().startDTD(null, location.getPublicId(), location.getSystemId());
    } 
    if (getLexicalHandler() != null) {
      getLexicalHandler().endDTD();
    }
  }

  
  private void handleEntityReference(EntityReference reference) throws SAXException {
    if (getLexicalHandler() != null) {
      getLexicalHandler().startEntity(reference.getName());
    }
    if (getLexicalHandler() != null) {
      getLexicalHandler().endEntity(reference.getName());
    }
  }

  
  private Attributes getAttributes(StartElement event) {
    AttributesImpl attributes = new AttributesImpl();
    for (Iterator<Attribute> i = event.getAttributes(); i.hasNext(); ) {
      Attribute attribute = i.next();
      QName qName = attribute.getName();
      String namespace = qName.getNamespaceURI();
      if (namespace == null || !hasNamespacesFeature()) {
        namespace = "";
      }
      String type = attribute.getDTDType();
      if (type == null) {
        type = "CDATA";
      }
      attributes.addAttribute(namespace, qName.getLocalPart(), toQualifiedName(qName), type, attribute.getValue());
    } 
    if (hasNamespacePrefixesFeature()) {
      for (Iterator<Namespace> iterator = event.getNamespaces(); iterator.hasNext(); ) {
        String qName; Namespace namespace = iterator.next();
        String prefix = namespace.getPrefix();
        String namespaceUri = namespace.getNamespaceURI();
        
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
