package org.fife.rsta.ac.xml;

public interface ValidationConfig {
  void configureParser(XmlParser paramXmlParser);
  
  void configureHandler(XmlParser.Handler paramHandler);
}
