package org.springframework.util.xml;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;
import org.springframework.lang.Nullable;





























class XMLEventStreamReader
  extends AbstractXMLStreamReader
{
  private XMLEvent event;
  private final XMLEventReader eventReader;
  
  public XMLEventStreamReader(XMLEventReader eventReader) throws XMLStreamException {
    this.eventReader = eventReader;
    this.event = eventReader.nextEvent();
  }


  
  public QName getName() {
    if (this.event.isStartElement()) {
      return this.event.asStartElement().getName();
    }
    if (this.event.isEndElement()) {
      return this.event.asEndElement().getName();
    }
    
    throw new IllegalStateException();
  }


  
  public Location getLocation() {
    return this.event.getLocation();
  }

  
  public int getEventType() {
    return this.event.getEventType();
  }

  
  @Nullable
  public String getVersion() {
    if (this.event.isStartDocument()) {
      return ((StartDocument)this.event).getVersion();
    }
    
    return null;
  }


  
  public Object getProperty(String name) throws IllegalArgumentException {
    return this.eventReader.getProperty(name);
  }

  
  public boolean isStandalone() {
    if (this.event.isStartDocument()) {
      return ((StartDocument)this.event).isStandalone();
    }
    
    throw new IllegalStateException();
  }


  
  public boolean standaloneSet() {
    if (this.event.isStartDocument()) {
      return ((StartDocument)this.event).standaloneSet();
    }
    
    throw new IllegalStateException();
  }


  
  @Nullable
  public String getEncoding() {
    return null;
  }

  
  @Nullable
  public String getCharacterEncodingScheme() {
    return null;
  }

  
  public String getPITarget() {
    if (this.event.isProcessingInstruction()) {
      return ((ProcessingInstruction)this.event).getTarget();
    }
    
    throw new IllegalStateException();
  }


  
  public String getPIData() {
    if (this.event.isProcessingInstruction()) {
      return ((ProcessingInstruction)this.event).getData();
    }
    
    throw new IllegalStateException();
  }


  
  public int getTextStart() {
    return 0;
  }

  
  public String getText() {
    if (this.event.isCharacters()) {
      return this.event.asCharacters().getData();
    }
    if (this.event.getEventType() == 5) {
      return ((Comment)this.event).getText();
    }
    
    throw new IllegalStateException();
  }



  
  public int getAttributeCount() {
    if (!this.event.isStartElement()) {
      throw new IllegalStateException();
    }
    Iterator attributes = this.event.asStartElement().getAttributes();
    return countIterator(attributes);
  }

  
  public boolean isAttributeSpecified(int index) {
    return getAttribute(index).isSpecified();
  }

  
  public QName getAttributeName(int index) {
    return getAttribute(index).getName();
  }

  
  public String getAttributeType(int index) {
    return getAttribute(index).getDTDType();
  }

  
  public String getAttributeValue(int index) {
    return getAttribute(index).getValue();
  }

  
  private Attribute getAttribute(int index) {
    if (!this.event.isStartElement()) {
      throw new IllegalStateException();
    }
    int count = 0;
    Iterator<Attribute> attributes = this.event.asStartElement().getAttributes();
    while (attributes.hasNext()) {
      Attribute attribute = attributes.next();
      if (count == index) {
        return attribute;
      }
      
      count++;
    } 
    
    throw new IllegalArgumentException();
  }

  
  public NamespaceContext getNamespaceContext() {
    if (this.event.isStartElement()) {
      return this.event.asStartElement().getNamespaceContext();
    }
    
    throw new IllegalStateException();
  }



  
  public int getNamespaceCount() {
    Iterator namespaces;
    if (this.event.isStartElement()) {
      namespaces = this.event.asStartElement().getNamespaces();
    }
    else if (this.event.isEndElement()) {
      namespaces = this.event.asEndElement().getNamespaces();
    } else {
      
      throw new IllegalStateException();
    } 
    return countIterator(namespaces);
  }

  
  public String getNamespacePrefix(int index) {
    return getNamespace(index).getPrefix();
  }

  
  public String getNamespaceURI(int index) {
    return getNamespace(index).getNamespaceURI();
  }

  
  private Namespace getNamespace(int index) {
    Iterator<Namespace> namespaces;
    if (this.event.isStartElement()) {
      namespaces = this.event.asStartElement().getNamespaces();
    }
    else if (this.event.isEndElement()) {
      namespaces = this.event.asEndElement().getNamespaces();
    } else {
      
      throw new IllegalStateException();
    } 
    int count = 0;
    while (namespaces.hasNext()) {
      Namespace namespace = namespaces.next();
      if (count == index) {
        return namespace;
      }
      
      count++;
    } 
    
    throw new IllegalArgumentException();
  }

  
  public int next() throws XMLStreamException {
    this.event = this.eventReader.nextEvent();
    return this.event.getEventType();
  }

  
  public void close() throws XMLStreamException {
    this.eventReader.close();
  }


  
  private static int countIterator(Iterator iterator) {
    int count = 0;
    while (iterator.hasNext()) {
      iterator.next();
      count++;
    } 
    return count;
  }
}
