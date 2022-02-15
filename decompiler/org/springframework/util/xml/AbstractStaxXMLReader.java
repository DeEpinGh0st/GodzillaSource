package org.springframework.util.xml;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;





































abstract class AbstractStaxXMLReader
  extends AbstractXMLReader
{
  private static final String NAMESPACES_FEATURE_NAME = "http://xml.org/sax/features/namespaces";
  private static final String NAMESPACE_PREFIXES_FEATURE_NAME = "http://xml.org/sax/features/namespace-prefixes";
  private static final String IS_STANDALONE_FEATURE_NAME = "http://xml.org/sax/features/is-standalone";
  private boolean namespacesFeature = true;
  private boolean namespacePrefixesFeature = false;
  @Nullable
  private Boolean isStandalone;
  private final Map<String, String> namespaces = new LinkedHashMap<>();


  
  public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
    switch (name) {
      case "http://xml.org/sax/features/namespaces":
        return this.namespacesFeature;
      case "http://xml.org/sax/features/namespace-prefixes":
        return this.namespacePrefixesFeature;
      case "http://xml.org/sax/features/is-standalone":
        if (this.isStandalone != null) {
          return this.isStandalone.booleanValue();
        }
        
        throw new SAXNotSupportedException("startDocument() callback not completed yet");
    } 
    
    return super.getFeature(name);
  }


  
  public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
    if ("http://xml.org/sax/features/namespaces".equals(name)) {
      this.namespacesFeature = value;
    }
    else if ("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
      this.namespacePrefixesFeature = value;
    } else {
      
      super.setFeature(name, value);
    } 
  }
  
  protected void setStandalone(boolean standalone) {
    this.isStandalone = Boolean.valueOf(standalone);
  }



  
  protected boolean hasNamespacesFeature() {
    return this.namespacesFeature;
  }



  
  protected boolean hasNamespacePrefixesFeature() {
    return this.namespacePrefixesFeature;
  }







  
  protected String toQualifiedName(QName qName) {
    String prefix = qName.getPrefix();
    if (!StringUtils.hasLength(prefix)) {
      return qName.getLocalPart();
    }
    
    return prefix + ":" + qName.getLocalPart();
  }









  
  public final void parse(InputSource ignored) throws SAXException {
    parse();
  }







  
  public final void parse(String ignored) throws SAXException {
    parse();
  }
  
  private void parse() throws SAXException {
    try {
      parseInternal();
    }
    catch (XMLStreamException ex) {
      Locator locator = null;
      if (ex.getLocation() != null) {
        locator = new StaxLocator(ex.getLocation());
      }
      SAXParseException saxException = new SAXParseException(ex.getMessage(), locator, ex);
      if (getErrorHandler() != null) {
        getErrorHandler().fatalError(saxException);
      } else {
        
        throw saxException;
      } 
    } 
  }




  
  protected abstract void parseInternal() throws SAXException, XMLStreamException;




  
  protected void startPrefixMapping(@Nullable String prefix, String namespace) throws SAXException {
    if (getContentHandler() != null && StringUtils.hasLength(namespace)) {
      if (prefix == null) {
        prefix = "";
      }
      if (!namespace.equals(this.namespaces.get(prefix))) {
        getContentHandler().startPrefixMapping(prefix, namespace);
        this.namespaces.put(prefix, namespace);
      } 
    } 
  }




  
  protected void endPrefixMapping(String prefix) throws SAXException {
    if (getContentHandler() != null && this.namespaces.containsKey(prefix)) {
      getContentHandler().endPrefixMapping(prefix);
      this.namespaces.remove(prefix);
    } 
  }



  
  private static class StaxLocator
    implements Locator
  {
    private final Location location;


    
    public StaxLocator(Location location) {
      this.location = location;
    }

    
    public String getPublicId() {
      return this.location.getPublicId();
    }

    
    public String getSystemId() {
      return this.location.getSystemId();
    }

    
    public int getLineNumber() {
      return this.location.getLineNumber();
    }

    
    public int getColumnNumber() {
      return this.location.getColumnNumber();
    }
  }
}
