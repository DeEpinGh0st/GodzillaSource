package javassist.tools.web;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;














































public class Viewer
  extends ClassLoader
{
  private String server;
  private int port;
  
  public static void main(String[] args) throws Throwable {
    if (args.length >= 3) {
      Viewer cl = new Viewer(args[0], Integer.parseInt(args[1]));
      String[] args2 = new String[args.length - 3];
      System.arraycopy(args, 3, args2, 0, args.length - 3);
      cl.run(args[2], args2);
    } else {
      
      System.err.println("Usage: java javassist.tools.web.Viewer <host> <port> class [args ...]");
    } 
  }






  
  public Viewer(String host, int p) {
    this.server = host;
    this.port = p;
  }


  
  public String getServer() {
    return this.server;
  }

  
  public int getPort() {
    return this.port;
  }







  
  public void run(String classname, String[] args) throws Throwable {
    Class<?> c = loadClass(classname);
    try {
      c.getDeclaredMethod("main", new Class[] { String[].class
          }).invoke(null, new Object[] { args });
    }
    catch (InvocationTargetException e) {
      throw e.getTargetException();
    } 
  }






  
  protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    Class<?> c = findLoadedClass(name);
    if (c == null) {
      c = findClass(name);
    }
    if (c == null) {
      throw new ClassNotFoundException(name);
    }
    if (resolve) {
      resolveClass(c);
    }
    return c;
  }











  
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    Class<?> c = null;
    if (name.startsWith("java.") || name.startsWith("javax.") || name
      .equals("javassist.tools.web.Viewer")) {
      c = findSystemClass(name);
    }
    if (c == null) {
      try {
        byte[] b = fetchClass(name);
        if (b != null) {
          c = defineClass(name, b, 0, b.length);
        }
      } catch (Exception exception) {}
    }
    
    return c;
  }






  
  protected byte[] fetchClass(String classname) throws Exception {
    byte[] b;
    URL url = new URL("http", this.server, this.port, "/" + classname.replace('.', '/') + ".class");
    URLConnection con = url.openConnection();
    con.connect();
    int size = con.getContentLength();
    InputStream s = con.getInputStream();
    if (size <= 0) {
      b = readStream(s);
    } else {
      b = new byte[size];
      int len = 0;
      do {
        int n = s.read(b, len, size - len);
        if (n < 0) {
          s.close();
          throw new IOException("the stream was closed: " + classname);
        } 
        
        len += n;
      } while (len < size);
    } 
    
    s.close();
    return b;
  }
  
  private byte[] readStream(InputStream fin) throws IOException {
    byte[] buf = new byte[4096];
    int size = 0;
    int len = 0;
    do {
      size += len;
      if (buf.length - size <= 0) {
        byte[] newbuf = new byte[buf.length * 2];
        System.arraycopy(buf, 0, newbuf, 0, size);
        buf = newbuf;
      } 
      
      len = fin.read(buf, size, buf.length - size);
    } while (len >= 0);
    
    byte[] result = new byte[size];
    System.arraycopy(buf, 0, result, 0, size);
    return result;
  }
}
