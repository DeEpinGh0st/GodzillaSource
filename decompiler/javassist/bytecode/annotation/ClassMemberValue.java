package javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;
import javassist.bytecode.SignatureAttribute;




























public class ClassMemberValue
  extends MemberValue
{
  int valueIndex;
  
  public ClassMemberValue(int index, ConstPool cp) {
    super('c', cp);
    this.valueIndex = index;
  }





  
  public ClassMemberValue(String className, ConstPool cp) {
    super('c', cp);
    setValue(className);
  }




  
  public ClassMemberValue(ConstPool cp) {
    super('c', cp);
    setValue("java.lang.Class");
  }


  
  Object getValue(ClassLoader cl, ClassPool cp, Method m) throws ClassNotFoundException {
    String classname = getValue();
    if (classname.equals("void"))
      return void.class; 
    if (classname.equals("int"))
      return int.class; 
    if (classname.equals("byte"))
      return byte.class; 
    if (classname.equals("long"))
      return long.class; 
    if (classname.equals("double"))
      return double.class; 
    if (classname.equals("float"))
      return float.class; 
    if (classname.equals("char"))
      return char.class; 
    if (classname.equals("short"))
      return short.class; 
    if (classname.equals("boolean")) {
      return boolean.class;
    }
    return loadClass(cl, classname);
  }

  
  Class<?> getType(ClassLoader cl) throws ClassNotFoundException {
    return loadClass(cl, "java.lang.Class");
  }





  
  public String getValue() {
    String v = this.cp.getUtf8Info(this.valueIndex);
    try {
      return SignatureAttribute.toTypeSignature(v).jvmTypeName();
    } catch (BadBytecode e) {
      throw new RuntimeException(e);
    } 
  }





  
  public void setValue(String newClassName) {
    String setTo = Descriptor.of(newClassName);
    this.valueIndex = this.cp.addUtf8Info(setTo);
  }




  
  public String toString() {
    return getValue().replace('$', '.') + ".class";
  }




  
  public void write(AnnotationsWriter writer) throws IOException {
    writer.classInfoIndex(this.cp.getUtf8Info(this.valueIndex));
  }




  
  public void accept(MemberValueVisitor visitor) {
    visitor.visitClassMemberValue(this);
  }
}
