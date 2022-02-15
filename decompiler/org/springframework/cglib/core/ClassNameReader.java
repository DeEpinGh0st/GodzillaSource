package org.springframework.cglib.core;

import java.util.ArrayList;
import java.util.List;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;



















public class ClassNameReader
{
  private static final EarlyExitException EARLY_EXIT = new EarlyExitException();


  
  public static String getClassName(ClassReader r) {
    return getClassInfo(r)[0];
  }
  private static class EarlyExitException extends RuntimeException {
    private EarlyExitException() {} }
  public static String[] getClassInfo(ClassReader r) {
    final List array = new ArrayList();
    try {
      r.accept(new ClassVisitor(Constants.ASM_API, null)
          {


            
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
            {
              array.add(name.replace('/', '.'));
              if (superName != null) {
                array.add(superName.replace('/', '.'));
              }
              for (int i = 0; i < interfaces.length; i++) {
                array.add(interfaces[i].replace('/', '.'));
              }
              
              throw ClassNameReader.EARLY_EXIT;
            }
          }6);
    } catch (EarlyExitException earlyExitException) {}
    
    return (String[])array.toArray((Object[])new String[0]);
  }
}
