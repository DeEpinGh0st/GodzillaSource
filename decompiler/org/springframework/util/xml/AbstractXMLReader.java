package org.springframework.util.xml;

import org.springframework.lang.Nullable;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;



































abstract class AbstractXMLReader
  implements XMLReader
{
  @Nullable
  private DTDHandler dtdHandler;
  @Nullable
  private ContentHandler contentHandler;
  @Nullable
  private EntityResolver entityResolver;
  @Nullable
  private ErrorHandler errorHandler;
  @Nullable
  private LexicalHandler lexicalHandler;
  
  public void setContentHandler(@Nullable ContentHandler contentHandler) {
    this.contentHandler = contentHandler;
  }

  
  @Nullable
  public ContentHandler getContentHandler() {
    return this.contentHandler;
  }

  
  public void setDTDHandler(@Nullable DTDHandler dtdHandler) {
    this.dtdHandler = dtdHandler;
  }

  
  @Nullable
  public DTDHandler getDTDHandler() {
    return this.dtdHandler;
  }

  
  public void setEntityResolver(@Nullable EntityResolver entityResolver) {
    this.entityResolver = entityResolver;
  }

  
  @Nullable
  public EntityResolver getEntityResolver() {
    return this.entityResolver;
  }

  
  public void setErrorHandler(@Nullable ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }

  
  @Nullable
  public ErrorHandler getErrorHandler() {
    return this.errorHandler;
  }
  
  @Nullable
  protected LexicalHandler getLexicalHandler() {
    return this.lexicalHandler;
  }







  
  public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (name.startsWith("http://xml.org/sax/features/")) {
      return false;
    }
    
    throw new SAXNotRecognizedException(name);
  }







  
  public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
    if (name.startsWith("http://xml.org/sax/features/")) {
      if (value) {
        throw new SAXNotSupportedException(name);
      }
    } else {
      
      throw new SAXNotRecognizedException(name);
    } 
  }





  
  @Nullable
  public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
    if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
      return this.lexicalHandler;
    }
    
    throw new SAXNotRecognizedException(name);
  }






  
  public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
    if ("http://xml.org/sax/properties/lexical-handler".equals(name)) {
      this.lexicalHandler = (LexicalHandler)value;
    } else {
      
      throw new SAXNotRecognizedException(name);
    } 
  }
}
