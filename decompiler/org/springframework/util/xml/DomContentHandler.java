package org.springframework.util.xml;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;

























class DomContentHandler
  implements ContentHandler
{
  private final Document document;
  private final List<Element> elements = new ArrayList<>();


  
  private final Node node;



  
  DomContentHandler(Node node) {
    this.node = node;
    if (node instanceof Document) {
      this.document = (Document)node;
    } else {
      
      this.document = node.getOwnerDocument();
    } 
  }

  
  private Node getParent() {
    if (!this.elements.isEmpty()) {
      return this.elements.get(this.elements.size() - 1);
    }
    
    return this.node;
  }


  
  public void startElement(String uri, String localName, String qName, Attributes attributes) {
    Node parent = getParent();
    Element element = this.document.createElementNS(uri, qName);
    for (int i = 0; i < attributes.getLength(); i++) {
      String attrUri = attributes.getURI(i);
      String attrQname = attributes.getQName(i);
      String value = attributes.getValue(i);
      if (!attrQname.startsWith("xmlns")) {
        element.setAttributeNS(attrUri, attrQname, value);
      }
    } 
    element = (Element)parent.appendChild(element);
    this.elements.add(element);
  }

  
  public void endElement(String uri, String localName, String qName) {
    this.elements.remove(this.elements.size() - 1);
  }

  
  public void characters(char[] ch, int start, int length) {
    String data = new String(ch, start, length);
    Node parent = getParent();
    Node lastChild = parent.getLastChild();
    if (lastChild != null && lastChild.getNodeType() == 3) {
      ((Text)lastChild).appendData(data);
    } else {
      
      Text text = this.document.createTextNode(data);
      parent.appendChild(text);
    } 
  }

  
  public void processingInstruction(String target, String data) {
    Node parent = getParent();
    ProcessingInstruction pi = this.document.createProcessingInstruction(target, data);
    parent.appendChild(pi);
  }
  
  public void setDocumentLocator(Locator locator) {}
  
  public void startDocument() {}
  
  public void endDocument() {}
  
  public void startPrefixMapping(String prefix, String uri) {}
  
  public void endPrefixMapping(String prefix) {}
  
  public void ignorableWhitespace(char[] ch, int start, int length) {}
  
  public void skippedEntity(String name) {}
}
