package org.fife.rsta.ac.xml;

import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.EntityResolver;


















public class DtdValidationConfig
  implements ValidationConfig
{
  private EntityResolver entityResolver;
  
  public DtdValidationConfig(EntityResolver entityResolver) {
    this.entityResolver = entityResolver;
  }


  
  public void configureParser(XmlParser parser) {
    SAXParserFactory spf = parser.getSaxParserFactory();
    spf.setValidating(true);
    spf.setSchema(null);
  }


  
  public void configureHandler(XmlParser.Handler handler) {
    handler.setEntityResolver(this.entityResolver);
  }
}
