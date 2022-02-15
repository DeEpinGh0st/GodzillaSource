package javassist;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;
import javassist.bytecode.ClassFile;















































































































































public class Loader
  extends ClassLoader
{
  private HashMap<String, ClassLoader> notDefinedHere;
  private Vector<String> notDefinedPackages;
  private ClassPool source;
  private Translator translator;
  private ProtectionDomain domain;
  
  public static class Simple
    extends ClassLoader
  {
    public Simple() {}
    
    public Simple(ClassLoader parent) {
      super(parent);
    }




    
    public Class<?> invokeDefineClass(CtClass cc) throws IOException, CannotCompileException {
      byte[] code = cc.toBytecode();
      return defineClass(cc.getName(), code, 0, code.length);
    }
  }










  
  public boolean doDelegation = true;










  
  public Loader() {
    this((ClassPool)null);
  }





  
  public Loader(ClassPool cp) {
    init(cp);
  }







  
  public Loader(ClassLoader parent, ClassPool cp) {
    super(parent);
    init(cp);
  }
  
  private void init(ClassPool cp) {
    this.notDefinedHere = new HashMap<>();
    this.notDefinedPackages = new Vector<>();
    this.source = cp;
    this.translator = null;
    this.domain = null;
    delegateLoadingOf("javassist.Loader");
  }








  
  public void delegateLoadingOf(String classname) {
    if (classname.endsWith(".")) {
      this.notDefinedPackages.addElement(classname);
    } else {
      this.notDefinedHere.put(classname, this);
    } 
  }





  
  public void setDomain(ProtectionDomain d) {
    this.domain = d;
  }



  
  public void setClassPool(ClassPool cp) {
    this.source = cp;
  }










  
  public void addTranslator(ClassPool cp, Translator t) throws NotFoundException, CannotCompileException {
    this.source = cp;
    this.translator = t;
    t.start(cp);
  }













  
  public static void main(String[] args) throws Throwable {
    Loader cl = new Loader();
    cl.run(args);
  }









  
  public void run(String[] args) throws Throwable {
    if (args.length >= 1) {
      run(args[0], Arrays.<String>copyOfRange(args, 1, args.length));
    }
  }





  
  public void run(String classname, String[] args) throws Throwable {
    Class<?> c = loadClass(classname);
    try {
      c.getDeclaredMethod("main", new Class[] { String[].class }).invoke(null, new Object[] { args });

    
    }
    catch (InvocationTargetException e) {
      throw e.getTargetException();
    } 
  }





  
  protected Class<?> loadClass(String name, boolean resolve) throws ClassFormatError, ClassNotFoundException {
    name = name.intern();
    synchronized (name) {
      Class<?> c = findLoadedClass(name);
      if (c == null) {
        c = loadClassByDelegation(name);
      }
      if (c == null) {
        c = findClass(name);
      }
      if (c == null) {
        c = delegateToParent(name);
      }
      if (resolve) {
        resolveClass(c);
      }
      return c;
    } 
  }













  
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    byte[] classfile;
    try {
      if (this.source != null) {
        if (this.translator != null) {
          this.translator.onLoad(this.source, name);
        }
        try {
          classfile = this.source.get(name).toBytecode();
        }
        catch (NotFoundException e) {
          return null;
        } 
      } else {
        
        String jarname = "/" + name.replace('.', '/') + ".class";
        InputStream in = getClass().getResourceAsStream(jarname);
        if (in == null) {
          return null;
        }
        classfile = ClassPoolTail.readStream(in);
      }
    
    } catch (Exception e) {
      throw new ClassNotFoundException("caught an exception while obtaining a class file for " + name, e);
    } 


    
    int i = name.lastIndexOf('.');
    if (i != -1) {
      String pname = name.substring(0, i);
      if (isDefinedPackage(pname)) {
        try {
          definePackage(pname, (String)null, (String)null, (String)null, (String)null, (String)null, (String)null, (URL)null);
        
        }
        catch (IllegalArgumentException illegalArgumentException) {}
      }
    } 


    
    if (this.domain == null)
      return defineClass(name, classfile, 0, classfile.length); 
    return defineClass(name, classfile, 0, classfile.length, this.domain);
  }
  
  private boolean isDefinedPackage(String name) {
    if (ClassFile.MAJOR_VERSION >= 53) {
      return (getDefinedPackage(name) == null);
    }
    return (getPackage(name) == null);
  }












  
  protected Class<?> loadClassByDelegation(String name) throws ClassNotFoundException {
    Class<?> c = null;
    if (this.doDelegation && (
      name.startsWith("java.") || name
      .startsWith("javax.") || name
      .startsWith("sun.") || name
      .startsWith("com.sun.") || name
      .startsWith("org.w3c.") || name
      .startsWith("org.xml.") || 
      notDelegated(name))) {
      c = delegateToParent(name);
    }
    return c;
  }
  
  private boolean notDelegated(String name) {
    if (this.notDefinedHere.containsKey(name)) {
      return true;
    }
    for (String pack : this.notDefinedPackages) {
      if (name.startsWith(pack))
        return true; 
    } 
    return false;
  }


  
  protected Class<?> delegateToParent(String classname) throws ClassNotFoundException {
    ClassLoader cl = getParent();
    if (cl != null)
      return cl.loadClass(classname); 
    return findSystemClass(classname);
  }
}
