package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
























public abstract class MemberValue
{
  ConstPool cp;
  char tag;
  
  MemberValue(char tag, ConstPool cp) {
    this.cp = cp;
    this.tag = tag;
  }



  
  abstract Object getValue(ClassLoader paramClassLoader, ClassPool paramClassPool, Method paramMethod) throws ClassNotFoundException;


  
  abstract Class<?> getType(ClassLoader paramClassLoader) throws ClassNotFoundException;


  
  static Class<?> loadClass(ClassLoader cl, String classname) throws ClassNotFoundException, NoSuchClassError {
    try {
      return Class.forName(convertFromArray(classname), true, cl);
    }
    catch (LinkageError e) {
      throw new NoSuchClassError(classname, e);
    } 
  }

  
  private static String convertFromArray(String classname) {
    int index = classname.indexOf("[]");
    if (index != -1) {
      String rawType = classname.substring(0, index);
      StringBuffer sb = new StringBuffer(Descriptor.of(rawType));
      while (index != -1) {
        sb.insert(0, "[");
        index = classname.indexOf("[]", index + 1);
      } 
      return sb.toString().replace('/', '.');
    } 
    return classname;
  }
  
  public abstract void accept(MemberValueVisitor paramMemberValueVisitor);
  
  public abstract void write(AnnotationsWriter paramAnnotationsWriter) throws IOException;
}
