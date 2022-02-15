package javassist;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
















































public class ByteArrayClassPath
  implements ClassPath
{
  protected String classname;
  protected byte[] classfile;
  
  public ByteArrayClassPath(String name, byte[] classfile) {
    this.classname = name;
    this.classfile = classfile;
  }

  
  public String toString() {
    return "byte[]:" + this.classname;
  }




  
  public InputStream openClassfile(String classname) {
    if (this.classname.equals(classname))
      return new ByteArrayInputStream(this.classfile); 
    return null;
  }




  
  public URL find(String classname) {
    if (this.classname.equals(classname)) {
      String cname = classname.replace('.', '/') + ".class";
      try {
        return new URL(null, "file:/ByteArrayClassPath/" + cname, new BytecodeURLStreamHandler());
      }
      catch (MalformedURLException malformedURLException) {}
    } 
    
    return null;
  }
  
  private class BytecodeURLStreamHandler extends URLStreamHandler {
    protected URLConnection openConnection(URL u) {
      return new ByteArrayClassPath.BytecodeURLConnection(u);
    }
    
    private BytecodeURLStreamHandler() {} }
  
  private class BytecodeURLConnection extends URLConnection { protected BytecodeURLConnection(URL url) {
      super(url);
    }

    
    public void connect() throws IOException {}
    
    public InputStream getInputStream() throws IOException {
      return new ByteArrayInputStream(ByteArrayClassPath.this.classfile);
    }
    
    public int getContentLength() {
      return ByteArrayClassPath.this.classfile.length;
    } }

}
