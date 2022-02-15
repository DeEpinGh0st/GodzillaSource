package org.springframework.util.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;





























class ListBasedXMLEventReader
  extends AbstractXMLEventReader
{
  private final List<XMLEvent> events;
  @Nullable
  private XMLEvent currentEvent;
  private int cursor = 0;

  
  public ListBasedXMLEventReader(List<XMLEvent> events) {
    Assert.notNull(events, "XMLEvent List must not be null");
    this.events = new ArrayList<>(events);
  }


  
  public boolean hasNext() {
    return (this.cursor < this.events.size());
  }

  
  public XMLEvent nextEvent() {
    if (hasNext()) {
      this.currentEvent = this.events.get(this.cursor);
      this.cursor++;
      return this.currentEvent;
    } 
    
    throw new NoSuchElementException();
  }


  
  @Nullable
  public XMLEvent peek() {
    if (hasNext()) {
      return this.events.get(this.cursor);
    }
    
    return null;
  }


  
  public String getElementText() throws XMLStreamException {
    checkIfClosed();
    if (this.currentEvent == null || !this.currentEvent.isStartElement()) {
      throw new XMLStreamException("Not at START_ELEMENT: " + this.currentEvent);
    }
    
    StringBuilder builder = new StringBuilder();
    while (true) {
      XMLEvent event = nextEvent();
      if (event.isEndElement()) {
        break;
      }
      if (!event.isCharacters()) {
        throw new XMLStreamException("Unexpected non-text event: " + event);
      }
      Characters characters = event.asCharacters();
      if (!characters.isIgnorableWhiteSpace()) {
        builder.append(event.asCharacters().getData());
      }
    } 
    return builder.toString();
  }
  
  @Nullable
  public XMLEvent nextTag() throws XMLStreamException {
    XMLEvent event;
    checkIfClosed();
    
    while (true) {
      event = nextEvent();
      switch (event.getEventType()) {
        case 1:
        case 2:
          return event;
        case 8:
          return null;
        case 3:
        case 5:
        case 6:
          continue;
        case 4:
        case 12:
          if (!event.asCharacters().isWhiteSpace())
            throw new XMLStreamException("Non-ignorable whitespace CDATA or CHARACTERS event: " + event); 
          continue;
      } 
      break;
    } 
    throw new XMLStreamException("Expected START_ELEMENT or END_ELEMENT: " + event);
  }



  
  public void close() {
    super.close();
    this.events.clear();
  }
}
