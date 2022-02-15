package com.kitfox.svg.app.data;

import com.kitfox.svg.util.Base64InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.logging.Level;
import java.util.logging.Logger;







































public class Handler
  extends URLStreamHandler
{
  class Connection
    extends URLConnection
  {
    String mime;
    byte[] buf;
    
    public Connection(URL url) {
      super(url);
      
      String path = url.getPath();
      int idx = path.indexOf(';');
      this.mime = path.substring(0, idx);
      String content = path.substring(idx + 1);
      
      if (content.startsWith("base64,")) {
        
        content = content.substring(7);



        
        try {
          ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes());
          Base64InputStream b64is = new Base64InputStream(bis);
          
          ByteArrayOutputStream bout = new ByteArrayOutputStream();
          byte[] tmp = new byte[2056]; int size;
          for (size = b64is.read(tmp); size != -1; size = b64is.read(tmp))
          {
            bout.write(tmp, 0, size);
          }
          this.buf = bout.toByteArray();
        }
        catch (IOException e) {
          
          Logger.getLogger("svgSalamandeLogger").log(Level.WARNING, (String)null, e);
        } 
      } 
    }



    
    public void connect() throws IOException {}


    
    public String getHeaderField(String name) {
      if ("content-type".equals(name))
      {
        return this.mime;
      }
      
      return super.getHeaderField(name);
    }


    
    public InputStream getInputStream() throws IOException {
      return new ByteArrayInputStream(this.buf);
    }
  }







  
  protected URLConnection openConnection(URL u) throws IOException {
    return new Connection(u);
  }
}
