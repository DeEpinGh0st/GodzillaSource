package javassist;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Comparator;
import javassist.bytecode.ClassFile;
import javassist.bytecode.Descriptor;
































public class SerialVersionUID
{
  public static void setSerialVersionUID(CtClass clazz) throws CannotCompileException, NotFoundException {
    try {
      clazz.getDeclaredField("serialVersionUID");
      
      return;
    } catch (NotFoundException notFoundException) {

      
      if (!isSerializable(clazz)) {
        return;
      }
      
      CtField field = new CtField(CtClass.longType, "serialVersionUID", clazz);
      
      field.setModifiers(26);
      
      clazz.addField(field, calculateDefault(clazz) + "L");
      return;
    } 
  }



  
  private static boolean isSerializable(CtClass clazz) throws NotFoundException {
    ClassPool pool = clazz.getClassPool();
    return clazz.subtypeOf(pool.get("java.io.Serializable"));
  }








  
  public static long calculateDefault(CtClass clazz) throws CannotCompileException {
    try {
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      DataOutputStream out = new DataOutputStream(bout);
      ClassFile classFile = clazz.getClassFile();

      
      String javaName = javaName(clazz);
      out.writeUTF(javaName);
      
      CtMethod[] methods = clazz.getDeclaredMethods();

      
      int classMods = clazz.getModifiers();
      if ((classMods & 0x200) != 0)
        if (methods.length > 0) {
          classMods |= 0x400;
        } else {
          classMods &= 0xFFFFFBFF;
        }  
      out.writeInt(classMods);

      
      String[] interfaces = classFile.getInterfaces(); int i;
      for (i = 0; i < interfaces.length; i++) {
        interfaces[i] = javaName(interfaces[i]);
      }
      Arrays.sort((Object[])interfaces);
      for (i = 0; i < interfaces.length; i++) {
        out.writeUTF(interfaces[i]);
      }
      
      CtField[] fields = clazz.getDeclaredFields();
      Arrays.sort(fields, new Comparator<CtField>()
          {
            public int compare(CtField field1, CtField field2) {
              return field1.getName().compareTo(field2.getName());
            }
          });
      
      for (int j = 0; j < fields.length; j++) {
        CtField field = fields[j];
        int mods = field.getModifiers();
        if ((mods & 0x2) == 0 || (mods & 0x88) == 0) {
          
          out.writeUTF(field.getName());
          out.writeInt(mods);
          out.writeUTF(field.getFieldInfo2().getDescriptor());
        } 
      } 

      
      if (classFile.getStaticInitializer() != null) {
        out.writeUTF("<clinit>");
        out.writeInt(8);
        out.writeUTF("()V");
      } 

      
      CtConstructor[] constructors = clazz.getDeclaredConstructors();
      Arrays.sort(constructors, new Comparator<CtConstructor>()
          {
            public int compare(CtConstructor c1, CtConstructor c2) {
              return c1.getMethodInfo2().getDescriptor().compareTo(c2
                  .getMethodInfo2().getDescriptor());
            }
          });
      int k;
      for (k = 0; k < constructors.length; k++) {
        CtConstructor constructor = constructors[k];
        int mods = constructor.getModifiers();
        if ((mods & 0x2) == 0) {
          out.writeUTF("<init>");
          out.writeInt(mods);
          out.writeUTF(constructor.getMethodInfo2()
              .getDescriptor().replace('/', '.'));
        } 
      } 

      
      Arrays.sort(methods, new Comparator<CtMethod>()
          {
            public int compare(CtMethod m1, CtMethod m2) {
              int value = m1.getName().compareTo(m2.getName());
              if (value == 0)
              {
                value = m1.getMethodInfo2().getDescriptor().compareTo(m2.getMethodInfo2().getDescriptor());
              }
              return value;
            }
          });
      
      for (k = 0; k < methods.length; k++) {
        CtMethod method = methods[k];
        int mods = method.getModifiers() & 0xD3F;



        
        if ((mods & 0x2) == 0) {
          out.writeUTF(method.getName());
          out.writeInt(mods);
          out.writeUTF(method.getMethodInfo2()
              .getDescriptor().replace('/', '.'));
        } 
      } 

      
      out.flush();
      MessageDigest digest = MessageDigest.getInstance("SHA");
      byte[] digested = digest.digest(bout.toByteArray());
      long hash = 0L;
      for (int m = Math.min(digested.length, 8) - 1; m >= 0; m--) {
        hash = hash << 8L | (digested[m] & 0xFF);
      }
      return hash;
    }
    catch (IOException e) {
      throw new CannotCompileException(e);
    }
    catch (NoSuchAlgorithmException e) {
      throw new CannotCompileException(e);
    } 
  }
  
  private static String javaName(CtClass clazz) {
    return Descriptor.toJavaName(Descriptor.toJvmName(clazz));
  }
  
  private static String javaName(String name) {
    return Descriptor.toJavaName(Descriptor.toJvmName(name));
  }
}
