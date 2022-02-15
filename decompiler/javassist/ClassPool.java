package javassist;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import javassist.bytecode.ClassFile;
import javassist.bytecode.Descriptor;
import javassist.util.proxy.DefineClassHelper;
import javassist.util.proxy.DefinePackageHelper;





































































































public class ClassPool
{
  public boolean childFirstLookup = false;
  public static boolean doPruning = false;
  private int compressCount;
  private static final int COMPRESS_THRESHOLD = 100;
  public static boolean releaseUnmodifiedClassFile = true;
  public static boolean cacheOpenedJarFile = true;
  protected ClassPoolTail source;
  protected ClassPool parent;
  protected Hashtable classes;
  private Hashtable cflow = null;

  
  private static final int INIT_HASH_SIZE = 191;

  
  private ArrayList importedPackages;

  
  public ClassPool() {
    this((ClassPool)null);
  }









  
  public ClassPool(boolean useDefaultPath) {
    this((ClassPool)null);
    if (useDefaultPath) {
      appendSystemPath();
    }
  }






  
  public ClassPool(ClassPool parent) {
    this.classes = new Hashtable<>(191);
    this.source = new ClassPoolTail();
    this.parent = parent;
    if (parent == null) {
      CtClass[] pt = CtClass.primitiveTypes;
      for (int i = 0; i < pt.length; i++) {
        this.classes.put(pt[i].getName(), pt[i]);
      }
    } 
    this.cflow = null;
    this.compressCount = 0;
    clearImportedPackages();
  }

























  
  public static synchronized ClassPool getDefault() {
    if (defaultPool == null) {
      defaultPool = new ClassPool(null);
      defaultPool.appendSystemPath();
    } 
    
    return defaultPool;
  }
  
  private static ClassPool defaultPool = null;







  
  protected CtClass getCached(String classname) {
    return (CtClass)this.classes.get(classname);
  }







  
  protected void cacheCtClass(String classname, CtClass c, boolean dynamic) {
    this.classes.put(classname, c);
  }







  
  protected CtClass removeCached(String classname) {
    return (CtClass)this.classes.remove(classname);
  }



  
  public String toString() {
    return this.source.toString();
  }




  
  void compress() {
    if (this.compressCount++ > 100) {
      this.compressCount = 0;
      Enumeration<CtClass> e = this.classes.elements();
      while (e.hasMoreElements()) {
        ((CtClass)e.nextElement()).compress();
      }
    } 
  }
















  
  public void importPackage(String packageName) {
    this.importedPackages.add(packageName);
  }







  
  public void clearImportedPackages() {
    this.importedPackages = new ArrayList();
    this.importedPackages.add("java.lang");
  }






  
  public Iterator<String> getImportedPackages() {
    return this.importedPackages.iterator();
  }












  
  public void recordInvalidClassName(String name) {}












  
  void recordCflow(String name, String cname, String fname) {
    if (this.cflow == null) {
      this.cflow = new Hashtable<>();
    }
    this.cflow.put(name, new Object[] { cname, fname });
  }





  
  public Object[] lookupCflow(String name) {
    if (this.cflow == null) {
      this.cflow = new Hashtable<>();
    }
    return (Object[])this.cflow.get(name);
  }




















  
  public CtClass getAndRename(String orgName, String newName) throws NotFoundException {
    CtClass clazz = get0(orgName, false);
    if (clazz == null) {
      throw new NotFoundException(orgName);
    }
    if (clazz instanceof CtClassType) {
      ((CtClassType)clazz).setClassPool(this);
    }
    clazz.setName(newName);
    
    return clazz;
  }





  
  synchronized void classNameChanged(String oldname, CtClass clazz) {
    CtClass c = getCached(oldname);
    if (c == clazz) {
      removeCached(oldname);
    }
    String newName = clazz.getName();
    checkNotFrozen(newName);
    cacheCtClass(newName, clazz, false);
  }
















  
  public CtClass get(String classname) throws NotFoundException {
    CtClass clazz;
    if (classname == null) {
      clazz = null;
    } else {
      clazz = get0(classname, true);
    } 
    if (clazz == null) {
      throw new NotFoundException(classname);
    }
    clazz.incGetCounter();
    return clazz;
  }















  
  public CtClass getOrNull(String classname) {
    CtClass clazz = null;
    if (classname == null) {
      clazz = null;
    } else {

      
      try {

        
        clazz = get0(classname, true);
      }
      catch (NotFoundException notFoundException) {}
    } 
    if (clazz != null) {
      clazz.incGetCounter();
    }
    return clazz;
  }




















  
  public CtClass getCtClass(String classname) throws NotFoundException {
    if (classname.charAt(0) == '[') {
      return Descriptor.toCtClass(classname, this);
    }
    return get(classname);
  }






  
  protected synchronized CtClass get0(String classname, boolean useCache) throws NotFoundException {
    CtClass clazz = null;
    if (useCache) {
      clazz = getCached(classname);
      if (clazz != null) {
        return clazz;
      }
    } 
    if (!this.childFirstLookup && this.parent != null) {
      clazz = this.parent.get0(classname, useCache);
      if (clazz != null) {
        return clazz;
      }
    } 
    clazz = createCtClass(classname, useCache);
    if (clazz != null) {
      
      if (useCache) {
        cacheCtClass(clazz.getName(), clazz, false);
      }
      return clazz;
    } 
    
    if (this.childFirstLookup && this.parent != null) {
      clazz = this.parent.get0(classname, useCache);
    }
    return clazz;
  }








  
  protected CtClass createCtClass(String classname, boolean useCache) {
    if (classname.charAt(0) == '[') {
      classname = Descriptor.toClassName(classname);
    }
    if (classname.endsWith("[]")) {
      String base = classname.substring(0, classname.indexOf('['));
      if ((!useCache || getCached(base) == null) && find(base) == null) {
        return null;
      }
      return new CtArray(classname, this);
    } 
    
    if (find(classname) == null) {
      return null;
    }
    return new CtClassType(classname, this);
  }









  
  public URL find(String classname) {
    return this.source.find(classname);
  }








  
  void checkNotFrozen(String classname) throws RuntimeException {
    CtClass clazz = getCached(classname);
    if (clazz == null) {
      if (!this.childFirstLookup && this.parent != null) {
        try {
          clazz = this.parent.get0(classname, true);
        }
        catch (NotFoundException notFoundException) {}
        if (clazz != null) {
          throw new RuntimeException(classname + " is in a parent ClassPool.  Use the parent.");
        }
      }
    
    }
    else if (clazz.isFrozen()) {
      throw new RuntimeException(classname + ": frozen class (cannot edit)");
    } 
  }






  
  CtClass checkNotExists(String classname) {
    CtClass clazz = getCached(classname);
    if (clazz == null && 
      !this.childFirstLookup && this.parent != null) {
      try {
        clazz = this.parent.get0(classname, true);
      }
      catch (NotFoundException notFoundException) {}
    }
    
    return clazz;
  }


  
  InputStream openClassfile(String classname) throws NotFoundException {
    return this.source.openClassfile(classname);
  }


  
  void writeClassfile(String classname, OutputStream out) throws NotFoundException, IOException, CannotCompileException {
    this.source.writeClassfile(classname, out);
  }











  
  public CtClass[] get(String[] classnames) throws NotFoundException {
    if (classnames == null) {
      return new CtClass[0];
    }
    int num = classnames.length;
    CtClass[] result = new CtClass[num];
    for (int i = 0; i < num; i++) {
      result[i] = get(classnames[i]);
    }
    return result;
  }









  
  public CtMethod getMethod(String classname, String methodname) throws NotFoundException {
    CtClass c = get(classname);
    return c.getDeclaredMethod(methodname);
  }

















  
  public CtClass makeClass(InputStream classfile) throws IOException, RuntimeException {
    return makeClass(classfile, true);
  }
















  
  public CtClass makeClass(InputStream classfile, boolean ifNotFrozen) throws IOException, RuntimeException {
    compress();
    classfile = new BufferedInputStream(classfile);
    CtClass clazz = new CtClassType(classfile, this);
    clazz.checkModify();
    String classname = clazz.getName();
    if (ifNotFrozen) {
      checkNotFrozen(classname);
    }
    cacheCtClass(classname, clazz, true);
    return clazz;
  }
















  
  public CtClass makeClass(ClassFile classfile) throws RuntimeException {
    return makeClass(classfile, true);
  }
















  
  public CtClass makeClass(ClassFile classfile, boolean ifNotFrozen) throws RuntimeException {
    compress();
    CtClass clazz = new CtClassType(classfile, this);
    clazz.checkModify();
    String classname = clazz.getName();
    if (ifNotFrozen) {
      checkNotFrozen(classname);
    }
    cacheCtClass(classname, clazz, true);
    return clazz;
  }

















  
  public CtClass makeClassIfNew(InputStream classfile) throws IOException, RuntimeException {
    compress();
    classfile = new BufferedInputStream(classfile);
    CtClass clazz = new CtClassType(classfile, this);
    clazz.checkModify();
    String classname = clazz.getName();
    CtClass found = checkNotExists(classname);
    if (found != null) {
      return found;
    }
    cacheCtClass(classname, clazz, true);
    return clazz;
  }

















  
  public CtClass makeClass(String classname) throws RuntimeException {
    return makeClass(classname, (CtClass)null);
  }



















  
  public synchronized CtClass makeClass(String classname, CtClass superclass) throws RuntimeException {
    checkNotFrozen(classname);
    CtClass clazz = new CtNewClass(classname, this, false, superclass);
    cacheCtClass(classname, clazz, true);
    return clazz;
  }







  
  synchronized CtClass makeNestedClass(String classname) {
    checkNotFrozen(classname);
    CtClass clazz = new CtNewClass(classname, this, false, null);
    cacheCtClass(classname, clazz, true);
    return clazz;
  }








  
  public CtClass makeInterface(String name) throws RuntimeException {
    return makeInterface(name, null);
  }











  
  public synchronized CtClass makeInterface(String name, CtClass superclass) throws RuntimeException {
    checkNotFrozen(name);
    CtClass clazz = new CtNewClass(name, this, true, superclass);
    cacheCtClass(name, clazz, true);
    return clazz;
  }










  
  public CtClass makeAnnotation(String name) throws RuntimeException {
    try {
      CtClass cc = makeInterface(name, get("java.lang.annotation.Annotation"));
      cc.setModifiers(cc.getModifiers() | 0x2000);
      return cc;
    }
    catch (NotFoundException e) {
      
      throw new RuntimeException(e.getMessage(), e);
    } 
  }










  
  public ClassPath appendSystemPath() {
    return this.source.appendSystemPath();
  }









  
  public ClassPath insertClassPath(ClassPath cp) {
    return this.source.insertClassPath(cp);
  }









  
  public ClassPath appendClassPath(ClassPath cp) {
    return this.source.appendClassPath(cp);
  }














  
  public ClassPath insertClassPath(String pathname) throws NotFoundException {
    return this.source.insertClassPath(pathname);
  }














  
  public ClassPath appendClassPath(String pathname) throws NotFoundException {
    return this.source.appendClassPath(pathname);
  }





  
  public void removeClassPath(ClassPath cp) {
    this.source.removeClassPath(cp);
  }












  
  public void appendPathList(String pathlist) throws NotFoundException {
    char sep = File.pathSeparatorChar;
    int i = 0;
    while (true) {
      int j = pathlist.indexOf(sep, i);
      if (j < 0) {
        appendClassPath(pathlist.substring(i));
        
        break;
      } 
      appendClassPath(pathlist.substring(i, j));
      i = j + 1;
    } 
  }


































  
  public Class toClass(CtClass clazz) throws CannotCompileException {
    return toClass(clazz, getClassLoader());
  }










  
  public ClassLoader getClassLoader() {
    return getContextClassLoader();
  }




  
  static ClassLoader getContextClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }
















  
  public Class toClass(CtClass ct, ClassLoader loader) throws CannotCompileException {
    return toClass(ct, null, loader, null);
  }


































  
  public Class toClass(CtClass ct, ClassLoader loader, ProtectionDomain domain) throws CannotCompileException {
    return toClass(ct, null, loader, domain);
  }

















  
  public Class<?> toClass(CtClass ct, Class<?> neighbor) throws CannotCompileException {
    try {
      return DefineClassHelper.toClass(neighbor, ct
          .toBytecode());
    }
    catch (IOException e) {
      throw new CannotCompileException(e);
    } 
  }
















  
  public Class<?> toClass(CtClass ct, MethodHandles.Lookup lookup) throws CannotCompileException {
    try {
      return DefineClassHelper.toClass(lookup, ct
          .toBytecode());
    }
    catch (IOException e) {
      throw new CannotCompileException(e);
    } 
  }














































  
  public Class toClass(CtClass ct, Class<?> neighbor, ClassLoader loader, ProtectionDomain domain) throws CannotCompileException {
    try {
      return DefineClassHelper.toClass(ct.getName(), neighbor, loader, domain, ct
          .toBytecode());
    }
    catch (IOException e) {
      throw new CannotCompileException(e);
    } 
  }






















  
  public void makePackage(ClassLoader loader, String name) throws CannotCompileException {
    DefinePackageHelper.definePackage(name, loader);
  }
}
