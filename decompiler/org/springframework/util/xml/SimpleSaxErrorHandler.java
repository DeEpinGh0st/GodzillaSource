package org.springframework.util.xml;

import org.apache.commons.logging.Log;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;





























public class SimpleSaxErrorHandler
  implements ErrorHandler
{
  private final Log logger;
  
  public SimpleSaxErrorHandler(Log logger) {
    this.logger = logger;
  }


  
  public void warning(SAXParseException ex) throws SAXException {
    this.logger.warn("Ignored XML validation warning", ex);
  }

  
  public void error(SAXParseException ex) throws SAXException {
    throw ex;
  }

  
  public void fatalError(SAXParseException ex) throws SAXException {
    throw ex;
  }
}
