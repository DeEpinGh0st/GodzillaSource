package org.springframework.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

public interface PropertiesPersister {
  void load(Properties paramProperties, InputStream paramInputStream) throws IOException;
  
  void load(Properties paramProperties, Reader paramReader) throws IOException;
  
  void store(Properties paramProperties, OutputStream paramOutputStream, String paramString) throws IOException;
  
  void store(Properties paramProperties, Writer paramWriter, String paramString) throws IOException;
  
  void loadFromXml(Properties paramProperties, InputStream paramInputStream) throws IOException;
  
  void storeToXml(Properties paramProperties, OutputStream paramOutputStream, String paramString) throws IOException;
  
  void storeToXml(Properties paramProperties, OutputStream paramOutputStream, String paramString1, String paramString2) throws IOException;
}
