package org.apache.log4j.lf5.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


















































public class Resource
{
  protected String _name;
  
  public Resource() {}
  
  public Resource(String name) {
    this._name = name;
  }

















  
  public void setName(String name) {
    this._name = name;
  }






  
  public String getName() {
    return this._name;
  }







  
  public InputStream getInputStream() {
    InputStream in = ResourceUtils.getResourceAsStream(this, this);
    
    return in;
  }







  
  public InputStreamReader getInputStreamReader() {
    InputStream in = ResourceUtils.getResourceAsStream(this, this);
    
    if (in == null) {
      return null;
    }
    
    InputStreamReader reader = new InputStreamReader(in);
    
    return reader;
  }





  
  public URL getURL() {
    return ResourceUtils.getResourceAsURL(this, this);
  }
}
