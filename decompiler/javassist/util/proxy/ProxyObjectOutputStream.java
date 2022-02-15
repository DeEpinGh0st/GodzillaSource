package javassist.util.proxy;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

































public class ProxyObjectOutputStream
  extends ObjectOutputStream
{
  public ProxyObjectOutputStream(OutputStream out) throws IOException {
    super(out);
  }

  
  protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException {
    Class<?> cl = desc.forClass();
    if (ProxyFactory.isProxyClass(cl)) {
      writeBoolean(true);
      Class<?> superClass = cl.getSuperclass();
      Class<?>[] interfaces = cl.getInterfaces();
      byte[] signature = ProxyFactory.getFilterSignature(cl);
      String name = superClass.getName();
      writeObject(name);
      
      writeInt(interfaces.length - 1);
      for (int i = 0; i < interfaces.length; i++) {
        Class<?> interfaze = interfaces[i];
        if (interfaze != ProxyObject.class && interfaze != Proxy.class) {
          name = interfaces[i].getName();
          writeObject(name);
        } 
      } 
      writeInt(signature.length);
      write(signature);
    } else {
      writeBoolean(false);
      super.writeClassDescriptor(desc);
    } 
  }
}
