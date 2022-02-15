package javassist;

import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.URL;









































public class LoaderClassPath
  implements ClassPath
{
  private Reference<ClassLoader> clref;
  
  public LoaderClassPath(ClassLoader cl) {
    this.clref = new WeakReference<>(cl);
  }

  
  public String toString() {
    return (this.clref.get() == null) ? "<null>" : ((ClassLoader)this.clref.get()).toString();
  }






  
  public InputStream openClassfile(String classname) throws NotFoundException {
    String cname = classname.replace('.', '/') + ".class";
    ClassLoader cl = this.clref.get();
    if (cl == null)
      return null; 
    InputStream is = cl.getResourceAsStream(cname);
    return is;
  }








  
  public URL find(String classname) {
    String cname = classname.replace('.', '/') + ".class";
    ClassLoader cl = this.clref.get();
    if (cl == null)
      return null; 
    URL url = cl.getResource(cname);
    return url;
  }
}
