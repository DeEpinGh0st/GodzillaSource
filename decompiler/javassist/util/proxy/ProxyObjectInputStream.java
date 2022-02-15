package javassist.util.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

































public class ProxyObjectInputStream
  extends ObjectInputStream
{
  private ClassLoader loader;
  
  public ProxyObjectInputStream(InputStream in) throws IOException {
    super(in);
    this.loader = Thread.currentThread().getContextClassLoader();
    if (this.loader == null) {
      this.loader = ClassLoader.getSystemClassLoader();
    }
  }





  
  public void setClassLoader(ClassLoader loader) {
    if (loader != null) {
      this.loader = loader;
    } else {
      loader = ClassLoader.getSystemClassLoader();
    } 
  }

  
  protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
    boolean isProxy = readBoolean();
    if (isProxy) {
      String name = (String)readObject();
      Class<?> superClass = this.loader.loadClass(name);
      int length = readInt();
      Class<?>[] interfaces = new Class[length];
      for (int i = 0; i < length; i++) {
        name = (String)readObject();
        interfaces[i] = this.loader.loadClass(name);
      } 
      length = readInt();
      byte[] signature = new byte[length];
      read(signature);
      ProxyFactory factory = new ProxyFactory();

      
      factory.setUseCache(true);
      factory.setUseWriteReplace(false);
      factory.setSuperclass(superClass);
      factory.setInterfaces(interfaces);
      Class<?> proxyClass = factory.createClass(signature);
      return ObjectStreamClass.lookup(proxyClass);
    } 
    return super.readClassDescriptor();
  }
}
