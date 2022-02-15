package org.springframework.cglib.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.ClassWriter;


















public class DebuggingClassWriter
  extends ClassVisitor
{
  public static final String DEBUG_LOCATION_PROPERTY = "cglib.debugLocation";
  private static String debugLocation = System.getProperty("cglib.debugLocation"); private static Constructor traceCtor; static {
    if (debugLocation != null) {
      System.err.println("CGLIB debugging enabled, writing to '" + debugLocation + "'");
      try {
        Class<?> clazz = Class.forName("org.springframework.asm.util.TraceClassVisitor");
        traceCtor = clazz.getConstructor(new Class[] { ClassVisitor.class, PrintWriter.class });
      } catch (Throwable throwable) {}
    } 
  }
  private String className; private String superName;
  
  public DebuggingClassWriter(int flags) {
    super(Constants.ASM_API, (ClassVisitor)new ClassWriter(flags));
  }





  
  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    this.className = name.replace('/', '.');
    this.superName = superName.replace('/', '.');
    super.visit(version, access, name, signature, superName, interfaces);
  }
  
  public String getClassName() {
    return this.className;
  }
  
  public String getSuperName() {
    return this.superName;
  }

  
  public byte[] toByteArray() {
    return AccessController.<byte[]>doPrivileged(new PrivilegedAction<byte>()
        {
          
          public Object run()
          {
            byte[] b = ((ClassWriter)DebuggingClassWriter.this.cv).toByteArray();
            if (DebuggingClassWriter.debugLocation != null) {
              String dirs = DebuggingClassWriter.this.className.replace('.', File.separatorChar);
              try {
                (new File(DebuggingClassWriter.debugLocation + File.separatorChar + dirs)).getParentFile().mkdirs();
                
                File file = new File(new File(DebuggingClassWriter.debugLocation), dirs + ".class");
                OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
                try {
                  out.write(b);
                } finally {
                  out.close();
                } 
                
                if (DebuggingClassWriter.traceCtor != null) {
                  file = new File(new File(DebuggingClassWriter.debugLocation), dirs + ".asm");
                  out = new BufferedOutputStream(new FileOutputStream(file));
                  try {
                    ClassReader cr = new ClassReader(b);
                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
                    ClassVisitor tcv = DebuggingClassWriter.traceCtor.newInstance(new Object[] { null, pw });
                    cr.accept(tcv, 0);
                    pw.flush();
                  } finally {
                    out.close();
                  } 
                } 
              } catch (Exception e) {
                throw new CodeGenerationException(e);
              } 
            } 
            return b;
          }
        });
  }
}
