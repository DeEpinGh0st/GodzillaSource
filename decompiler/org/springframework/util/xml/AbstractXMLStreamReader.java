package org.springframework.util.xml;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.springframework.lang.Nullable;

























abstract class AbstractXMLStreamReader
  implements XMLStreamReader
{
  public String getElementText() throws XMLStreamException {
    if (getEventType() != 1) {
      throw new XMLStreamException("Parser must be on START_ELEMENT to read next text", getLocation());
    }
    int eventType = next();
    StringBuilder builder = new StringBuilder();
    while (eventType != 2) {
      if (eventType == 4 || eventType == 12 || eventType == 6 || eventType == 9) {
        
        builder.append(getText());
      }
      else if (eventType != 3 && eventType != 5) {


        
        if (eventType == 8) {
          throw new XMLStreamException("Unexpected end of document when reading element text content", 
              getLocation());
        }
        if (eventType == 1) {
          throw new XMLStreamException("Element text content may not contain START_ELEMENT", getLocation());
        }
        
        throw new XMLStreamException("Unexpected event type " + eventType, getLocation());
      } 
      eventType = next();
    } 
    return builder.toString();
  }

  
  public String getAttributeLocalName(int index) {
    return getAttributeName(index).getLocalPart();
  }

  
  public String getAttributeNamespace(int index) {
    return getAttributeName(index).getNamespaceURI();
  }

  
  public String getAttributePrefix(int index) {
    return getAttributeName(index).getPrefix();
  }

  
  public String getNamespaceURI() {
    int eventType = getEventType();
    if (eventType == 1 || eventType == 2) {
      return getName().getNamespaceURI();
    }
    
    throw new IllegalStateException("Parser must be on START_ELEMENT or END_ELEMENT state");
  }


  
  public String getNamespaceURI(String prefix) {
    return getNamespaceContext().getNamespaceURI(prefix);
  }

  
  public boolean hasText() {
    int eventType = getEventType();
    return (eventType == 6 || eventType == 4 || eventType == 5 || eventType == 12 || eventType == 9);
  }



  
  public String getPrefix() {
    int eventType = getEventType();
    if (eventType == 1 || eventType == 2) {
      return getName().getPrefix();
    }
    
    throw new IllegalStateException("Parser must be on START_ELEMENT or END_ELEMENT state");
  }


  
  public boolean hasName() {
    int eventType = getEventType();
    return (eventType == 1 || eventType == 2);
  }

  
  public boolean isWhiteSpace() {
    return (getEventType() == 6);
  }

  
  public boolean isStartElement() {
    return (getEventType() == 1);
  }

  
  public boolean isEndElement() {
    return (getEventType() == 2);
  }

  
  public boolean isCharacters() {
    return (getEventType() == 4);
  }

  
  public int nextTag() throws XMLStreamException {
    int eventType = next();
    while ((eventType == 4 && isWhiteSpace()) || (eventType == 12 && 
      isWhiteSpace()) || eventType == 6 || eventType == 3 || eventType == 5)
    {
      eventType = next();
    }
    if (eventType != 1 && eventType != 2) {
      throw new XMLStreamException("expected start or end tag", getLocation());
    }
    return eventType;
  }

  
  public void require(int expectedType, String namespaceURI, String localName) throws XMLStreamException {
    int eventType = getEventType();
    if (eventType != expectedType) {
      throw new XMLStreamException("Expected [" + expectedType + "] but read [" + eventType + "]");
    }
  }

  
  @Nullable
  public String getAttributeValue(@Nullable String namespaceURI, String localName) {
    for (int i = 0; i < getAttributeCount(); i++) {
      QName name = getAttributeName(i);
      if (name.getLocalPart().equals(localName) && (namespaceURI == null || name
        .getNamespaceURI().equals(namespaceURI))) {
        return getAttributeValue(i);
      }
    } 
    return null;
  }

  
  public boolean hasNext() {
    return (getEventType() != 8);
  }

  
  public String getLocalName() {
    return getName().getLocalPart();
  }

  
  public char[] getTextCharacters() {
    return getText().toCharArray();
  }

  
  public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) {
    char[] source = getTextCharacters();
    length = Math.min(length, source.length);
    System.arraycopy(source, sourceStart, target, targetStart, length);
    return length;
  }

  
  public int getTextLength() {
    return getText().length();
  }
}
