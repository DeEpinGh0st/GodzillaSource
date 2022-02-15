package javassist.util.proxy;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.security.ProtectionDomain;
import javassist.CannotCompileException;
import javassist.bytecode.ClassFile;



























public class FactoryHelper
{
  public static final int typeIndex(Class<?> type) {
    for (int i = 0; i < primitiveTypes.length; i++) {
      if (primitiveTypes[i] == type)
        return i; 
    } 
    throw new RuntimeException("bad type:" + type.getName());
  }



  
  public static final Class<?>[] primitiveTypes = new Class[] { boolean.class, byte.class, char.class, short.class, int.class, long.class, float.class, double.class, void.class };






  
  public static final String[] wrapperTypes = new String[] { "java.lang.Boolean", "java.lang.Byte", "java.lang.Character", "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.Void" };







  
  public static final String[] wrapperDesc = new String[] { "(Z)V", "(B)V", "(C)V", "(S)V", "(I)V", "(J)V", "(F)V", "(D)V" };









  
  public static final String[] unwarpMethods = new String[] { "booleanValue", "byteValue", "charValue", "shortValue", "intValue", "longValue", "floatValue", "doubleValue" };







  
  public static final String[] unwrapDesc = new String[] { "()Z", "()B", "()C", "()S", "()I", "()J", "()F", "()D" };






  
  public static final int[] dataSize = new int[] { 1, 1, 1, 1, 1, 2, 1, 2 };












  
  public static Class<?> toClass(ClassFile cf, ClassLoader loader) throws CannotCompileException {
    return toClass(cf, null, loader, null);
  }














  
  public static Class<?> toClass(ClassFile cf, ClassLoader loader, ProtectionDomain domain) throws CannotCompileException {
    return toClass(cf, null, loader, domain);
  }














  
  public static Class<?> toClass(ClassFile cf, Class<?> neighbor, ClassLoader loader, ProtectionDomain domain) throws CannotCompileException {
    try {
      byte[] b = toBytecode(cf);
      if (ProxyFactory.onlyPublicMethods) {
        return DefineClassHelper.toPublicClass(cf.getName(), b);
      }
      return DefineClassHelper.toClass(cf.getName(), neighbor, loader, domain, b);
    
    }
    catch (IOException e) {
      throw new CannotCompileException(e);
    } 
  }








  
  public static Class<?> toClass(ClassFile cf, MethodHandles.Lookup lookup) throws CannotCompileException {
    try {
      byte[] b = toBytecode(cf);
      return DefineClassHelper.toClass(lookup, b);
    }
    catch (IOException e) {
      throw new CannotCompileException(e);
    } 
  }
  
  private static byte[] toBytecode(ClassFile cf) throws IOException {
    ByteArrayOutputStream barray = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(barray);
    try {
      cf.write(out);
    } finally {
      
      out.close();
    } 
    
    return barray.toByteArray();
  }




  
  public static void writeFile(ClassFile cf, String directoryName) throws CannotCompileException {
    try {
      writeFile0(cf, directoryName);
    }
    catch (IOException e) {
      throw new CannotCompileException(e);
    } 
  }

  
  private static void writeFile0(ClassFile cf, String directoryName) throws CannotCompileException, IOException {
    String classname = cf.getName();
    
    String filename = directoryName + File.separatorChar + classname.replace('.', File.separatorChar) + ".class";
    int pos = filename.lastIndexOf(File.separatorChar);
    if (pos > 0) {
      String dir = filename.substring(0, pos);
      if (!dir.equals(".")) {
        (new File(dir)).mkdirs();
      }
    } 
    DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
    
    try {
      cf.write(out);
    }
    catch (IOException e) {
      throw e;
    } finally {
      
      out.close();
    } 
  }
}
