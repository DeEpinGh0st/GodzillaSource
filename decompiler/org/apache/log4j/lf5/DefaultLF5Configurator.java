package org.apache.log4j.lf5;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.LoggerRepository;


































































public class DefaultLF5Configurator
  implements Configurator
{
  public static void configure() throws IOException {
    String resource = "/org/apache/log4j/lf5/config/defaultconfig.properties";
    
    URL configFileResource = DefaultLF5Configurator.class.getResource(resource);

    
    if (configFileResource != null) {
      PropertyConfigurator.configure(configFileResource);
    } else {
      throw new IOException("Error: Unable to open the resource" + resource);
    } 
  }








  
  public void doConfigure(InputStream inputStream, LoggerRepository repository) {
    throw new IllegalStateException("This class should NOT be instantiated!");
  }




  
  public void doConfigure(URL configURL, LoggerRepository repository) {
    throw new IllegalStateException("This class should NOT be instantiated!");
  }
}
