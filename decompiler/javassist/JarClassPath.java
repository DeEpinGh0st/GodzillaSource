package javassist;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


















































































































final class JarClassPath
  implements ClassPath
{
  Set<String> jarfileEntries;
  String jarfileURL;
  
  JarClassPath(String pathname) throws NotFoundException {
    JarFile jarfile = null;
    
    try { jarfile = new JarFile(pathname);
      this.jarfileEntries = new HashSet<>();
      for (JarEntry je : Collections.<JarEntry>list(jarfile.entries())) {
        if (je.getName().endsWith(".class"))
          this.jarfileEntries.add(je.getName()); 
      }  this
        .jarfileURL = (new File(pathname)).getCanonicalFile().toURI().toURL().toString();
      return; }
    catch (IOException iOException) {  }
    finally
    { if (null != jarfile)
        try {
          jarfile.close();
        } catch (IOException iOException) {}  }
    
    throw new NotFoundException(pathname);
  }



  
  public InputStream openClassfile(String classname) throws NotFoundException {
    URL jarURL = find(classname);
    if (null != jarURL) {
      try {
        if (ClassPool.cacheOpenedJarFile) {
          return jarURL.openConnection().getInputStream();
        }
        URLConnection con = jarURL.openConnection();
        con.setUseCaches(false);
        return con.getInputStream();
      
      }
      catch (IOException e) {
        throw new NotFoundException("broken jar file?: " + classname);
      } 
    }
    return null;
  }

  
  public URL find(String classname) {
    String jarname = classname.replace('.', '/') + ".class";
    if (this.jarfileEntries.contains(jarname))
      try {
        return new URL(String.format("jar:%s!/%s", new Object[] { this.jarfileURL, jarname }));
      }
      catch (MalformedURLException malformedURLException) {} 
    return null;
  }

  
  public String toString() {
    return (this.jarfileURL == null) ? "<null>" : this.jarfileURL.toString();
  }
}
