package javassist;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.ConstantAttribute;
import javassist.bytecode.Descriptor;
import javassist.bytecode.EnclosingMethodAttribute;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.InnerClassesAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationImpl;
import javassist.compiler.AccessorMaker;
import javassist.compiler.CompileError;
import javassist.compiler.Javac;
import javassist.expr.ExprEditor;





















class CtClassType
  extends CtClass
{
  ClassPool classPool;
  boolean wasChanged;
  private boolean wasFrozen;
  boolean wasPruned;
  boolean gcConstPool;
  ClassFile classfile;
  byte[] rawClassfile;
  private Reference<CtMember.Cache> memberCache;
  private AccessorMaker accessors;
  private FieldInitLink fieldInitializers;
  private Map<CtMethod, String> hiddenMethods;
  private int uniqueNumberSeed;
  private boolean doPruning = ClassPool.doPruning;
  private int getCount;
  private static final int GET_THRESHOLD = 2;
  
  CtClassType(String name, ClassPool cp) {
    super(name);
    this.classPool = cp;
    this.wasChanged = this.wasFrozen = this.wasPruned = this.gcConstPool = false;
    this.classfile = null;
    this.rawClassfile = null;
    this.memberCache = null;
    this.accessors = null;
    this.fieldInitializers = null;
    this.hiddenMethods = null;
    this.uniqueNumberSeed = 0;
    this.getCount = 0;
  }
  
  CtClassType(InputStream ins, ClassPool cp) throws IOException {
    this((String)null, cp);
    this.classfile = new ClassFile(new DataInputStream(ins));
    this.qualifiedName = this.classfile.getName();
  }
  
  CtClassType(ClassFile cf, ClassPool cp) {
    this((String)null, cp);
    this.classfile = cf;
    this.qualifiedName = this.classfile.getName();
  }

  
  protected void extendToString(StringBuffer buffer) {
    if (this.wasChanged) {
      buffer.append("changed ");
    }
    if (this.wasFrozen) {
      buffer.append("frozen ");
    }
    if (this.wasPruned) {
      buffer.append("pruned ");
    }
    buffer.append(Modifier.toString(getModifiers()));
    buffer.append(" class ");
    buffer.append(getName());
    
    try {
      CtClass ext = getSuperclass();
      if (ext != null) {
        String name = ext.getName();
        if (!name.equals("java.lang.Object")) {
          buffer.append(" extends " + ext.getName());
        }
      } 
    } catch (NotFoundException e) {
      buffer.append(" extends ??");
    } 
    
    try {
      CtClass[] intf = getInterfaces();
      if (intf.length > 0) {
        buffer.append(" implements ");
      }
      for (int i = 0; i < intf.length; i++) {
        buffer.append(intf[i].getName());
        buffer.append(", ");
      }
    
    } catch (NotFoundException e) {
      buffer.append(" extends ??");
    } 
    
    CtMember.Cache memCache = getMembers();
    exToString(buffer, " fields=", memCache
        .fieldHead(), memCache.lastField());
    exToString(buffer, " constructors=", memCache
        .consHead(), memCache.lastCons());
    exToString(buffer, " methods=", memCache
        .methodHead(), memCache.lastMethod());
  }

  
  private void exToString(StringBuffer buffer, String msg, CtMember head, CtMember tail) {
    buffer.append(msg);
    while (head != tail) {
      head = head.next();
      buffer.append(head);
      buffer.append(", ");
    } 
  }

  
  public AccessorMaker getAccessorMaker() {
    if (this.accessors == null) {
      this.accessors = new AccessorMaker(this);
    }
    return this.accessors;
  }

  
  public ClassFile getClassFile2() {
    return getClassFile3(true);
  }
  
  public ClassFile getClassFile3(boolean doCompress) {
    byte[] rcfile;
    ClassFile cfile = this.classfile;
    if (cfile != null) {
      return cfile;
    }
    if (doCompress) {
      this.classPool.compress();
    }
    
    synchronized (this) {
      
      cfile = this.classfile;
      if (cfile != null) {
        return cfile;
      }
      rcfile = this.rawClassfile;
    } 
    
    if (rcfile != null) {
      ClassFile cf;
      try {
        cf = new ClassFile(new DataInputStream(new ByteArrayInputStream(rcfile)));
      }
      catch (IOException e) {
        throw new RuntimeException(e.toString(), e);
      } 
      this.getCount = 2;
      synchronized (this) {
        this.rawClassfile = null;
        return setClassFile(cf);
      } 
    } 
    
    InputStream inputStream = null;
    try {
      inputStream = this.classPool.openClassfile(getName());
      if (inputStream == null) {
        throw new NotFoundException(getName());
      }
      inputStream = new BufferedInputStream(inputStream);
      ClassFile cf = new ClassFile(new DataInputStream(inputStream));
      if (!cf.getName().equals(this.qualifiedName)) {
        throw new RuntimeException("cannot find " + this.qualifiedName + ": " + cf
            .getName() + " found in " + this.qualifiedName
            .replace('.', '/') + ".class");
      }
      return setClassFile(cf);
    }
    catch (NotFoundException e) {
      throw new RuntimeException(e.toString(), e);
    }
    catch (IOException e) {
      throw new RuntimeException(e.toString(), e);
    } finally {
      
      if (inputStream != null) {
        try {
          inputStream.close();
        }
        catch (IOException iOException) {}
      }
    } 
  }




  
  final void incGetCounter() {
    this.getCount++;
  }





  
  void compress() {
    if (this.getCount < 2)
      if (!isModified() && ClassPool.releaseUnmodifiedClassFile) {
        removeClassFile();
      } else if (isFrozen() && !this.wasPruned) {
        saveClassFile();
      }  
    this.getCount = 0;
  }






  
  private synchronized void saveClassFile() {
    if (this.classfile == null || hasMemberCache() != null) {
      return;
    }
    ByteArrayOutputStream barray = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(barray);
    try {
      this.classfile.write(out);
      barray.close();
      this.rawClassfile = barray.toByteArray();
      this.classfile = null;
    }
    catch (IOException iOException) {}
  }
  
  private synchronized void removeClassFile() {
    if (this.classfile != null && !isModified() && hasMemberCache() == null) {
      this.classfile = null;
    }
  }


  
  private synchronized ClassFile setClassFile(ClassFile cf) {
    if (this.classfile == null) {
      this.classfile = cf;
    }
    return this.classfile;
  }
  
  public ClassPool getClassPool() {
    return this.classPool;
  } void setClassPool(ClassPool cp) {
    this.classPool = cp;
  }
  
  public URL getURL() throws NotFoundException {
    URL url = this.classPool.find(getName());
    if (url == null)
      throw new NotFoundException(getName()); 
    return url;
  }
  
  public boolean isModified() {
    return this.wasChanged;
  }
  public boolean isFrozen() {
    return this.wasFrozen;
  }
  public void freeze() {
    this.wasFrozen = true;
  }
  
  void checkModify() throws RuntimeException {
    if (isFrozen()) {
      String msg = getName() + " class is frozen";
      if (this.wasPruned) {
        msg = msg + " and pruned";
      }
      throw new RuntimeException(msg);
    } 
    
    this.wasChanged = true;
  }

  
  public void defrost() {
    checkPruned("defrost");
    this.wasFrozen = false;
  }


  
  public boolean subtypeOf(CtClass clazz) throws NotFoundException {
    String cname = clazz.getName();
    if (this == clazz || getName().equals(cname)) {
      return true;
    }
    ClassFile file = getClassFile2();
    String supername = file.getSuperclass();
    if (supername != null && supername.equals(cname)) {
      return true;
    }
    String[] ifs = file.getInterfaces();
    int num = ifs.length; int i;
    for (i = 0; i < num; i++) {
      if (ifs[i].equals(cname))
        return true; 
    } 
    if (supername != null && this.classPool.get(supername).subtypeOf(clazz)) {
      return true;
    }
    for (i = 0; i < num; i++) {
      if (this.classPool.get(ifs[i]).subtypeOf(clazz))
        return true; 
    } 
    return false;
  }

  
  public void setName(String name) throws RuntimeException {
    String oldname = getName();
    if (name.equals(oldname)) {
      return;
    }
    
    this.classPool.checkNotFrozen(name);
    ClassFile cf = getClassFile2();
    super.setName(name);
    cf.setName(name);
    nameReplaced();
    this.classPool.classNameChanged(oldname, this);
  }


  
  public String getGenericSignature() {
    SignatureAttribute sa = (SignatureAttribute)getClassFile2().getAttribute("Signature");
    return (sa == null) ? null : sa.getSignature();
  }

  
  public void setGenericSignature(String sig) {
    ClassFile cf = getClassFile();
    SignatureAttribute sa = new SignatureAttribute(cf.getConstPool(), sig);
    cf.addAttribute((AttributeInfo)sa);
  }



  
  public void replaceClassName(ClassMap classnames) throws RuntimeException {
    String oldClassName = getName();
    
    String newClassName = classnames.get(Descriptor.toJvmName(oldClassName));
    if (newClassName != null) {
      newClassName = Descriptor.toJavaName(newClassName);
      
      this.classPool.checkNotFrozen(newClassName);
    } 
    
    super.replaceClassName(classnames);
    ClassFile cf = getClassFile2();
    cf.renameClass(classnames);
    nameReplaced();
    
    if (newClassName != null) {
      super.setName(newClassName);
      this.classPool.classNameChanged(oldClassName, this);
    } 
  }



  
  public void replaceClassName(String oldname, String newname) throws RuntimeException {
    String thisname = getName();
    if (thisname.equals(oldname)) {
      setName(newname);
    } else {
      super.replaceClassName(oldname, newname);
      getClassFile2().renameClass(oldname, newname);
      nameReplaced();
    } 
  }

  
  public boolean isInterface() {
    return Modifier.isInterface(getModifiers());
  }

  
  public boolean isAnnotation() {
    return Modifier.isAnnotation(getModifiers());
  }

  
  public boolean isEnum() {
    return Modifier.isEnum(getModifiers());
  }

  
  public int getModifiers() {
    ClassFile cf = getClassFile2();
    int acc = cf.getAccessFlags();
    acc = AccessFlag.clear(acc, 32);
    int inner = cf.getInnerAccessFlags();
    if (inner != -1) {
      if ((inner & 0x8) != 0)
        acc |= 0x8; 
      if ((inner & 0x1) != 0) {
        acc |= 0x1;
      } else {
        acc &= 0xFFFFFFFE;
        if ((inner & 0x4) != 0) {
          acc |= 0x4;
        } else if ((inner & 0x2) != 0) {
          acc |= 0x2;
        } 
      } 
    }  return AccessFlag.toModifier(acc);
  }

  
  public CtClass[] getNestedClasses() throws NotFoundException {
    ClassFile cf = getClassFile2();
    
    InnerClassesAttribute ica = (InnerClassesAttribute)cf.getAttribute("InnerClasses");
    if (ica == null) {
      return new CtClass[0];
    }
    String thisName = cf.getName() + "$";
    int n = ica.tableLength();
    List<CtClass> list = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      String name = ica.innerClass(i);
      if (name != null && 
        name.startsWith(thisName))
      {
        if (name.lastIndexOf('$') < thisName.length()) {
          list.add(this.classPool.get(name));
        }
      }
    } 
    return list.<CtClass>toArray(new CtClass[list.size()]);
  }

  
  public void setModifiers(int mod) {
    checkModify();
    updateInnerEntry(mod, getName(), this, true);
    ClassFile cf = getClassFile2();
    cf.setAccessFlags(AccessFlag.of(mod & 0xFFFFFFF7));
  }
  
  private static void updateInnerEntry(int newMod, String name, CtClass clazz, boolean outer) {
    ClassFile cf = clazz.getClassFile2();
    
    InnerClassesAttribute ica = (InnerClassesAttribute)cf.getAttribute("InnerClasses");
    if (ica != null) {


      
      int mod = newMod & 0xFFFFFFF7;
      int i = ica.find(name);
      if (i >= 0) {
        int isStatic = ica.accessFlags(i) & 0x8;
        if (isStatic != 0 || !Modifier.isStatic(newMod)) {
          clazz.checkModify();
          ica.setAccessFlags(i, AccessFlag.of(mod) | isStatic);
          String outName = ica.outerClass(i);
          if (outName != null && outer) {
            try {
              CtClass parent = clazz.getClassPool().get(outName);
              updateInnerEntry(mod, name, parent, false);
            }
            catch (NotFoundException e) {
              throw new RuntimeException("cannot find the declaring class: " + outName);
            } 
          }
          
          return;
        } 
      } 
    } 
    
    if (Modifier.isStatic(newMod)) {
      throw new RuntimeException("cannot change " + Descriptor.toJavaName(name) + " into a static class");
    }
  }

  
  public boolean hasAnnotation(String annotationName) {
    ClassFile cf = getClassFile2();
    
    AnnotationsAttribute ainfo = (AnnotationsAttribute)cf.getAttribute("RuntimeInvisibleAnnotations");
    
    AnnotationsAttribute ainfo2 = (AnnotationsAttribute)cf.getAttribute("RuntimeVisibleAnnotations");
    return hasAnnotationType(annotationName, getClassPool(), ainfo, ainfo2);
  }






  
  @Deprecated
  static boolean hasAnnotationType(Class<?> clz, ClassPool cp, AnnotationsAttribute a1, AnnotationsAttribute a2) {
    return hasAnnotationType(clz.getName(), cp, a1, a2);
  }



  
  static boolean hasAnnotationType(String annotationTypeName, ClassPool cp, AnnotationsAttribute a1, AnnotationsAttribute a2) {
    Annotation[] anno1;
    Annotation[] anno2;
    if (a1 == null) {
      anno1 = null;
    } else {
      anno1 = a1.getAnnotations();
    } 
    if (a2 == null) {
      anno2 = null;
    } else {
      anno2 = a2.getAnnotations();
    } 
    if (anno1 != null)
      for (int i = 0; i < anno1.length; i++) {
        if (anno1[i].getTypeName().equals(annotationTypeName))
          return true; 
      }  
    if (anno2 != null)
      for (int i = 0; i < anno2.length; i++) {
        if (anno2[i].getTypeName().equals(annotationTypeName))
          return true; 
      }  
    return false;
  }

  
  public Object getAnnotation(Class<?> clz) throws ClassNotFoundException {
    ClassFile cf = getClassFile2();
    
    AnnotationsAttribute ainfo = (AnnotationsAttribute)cf.getAttribute("RuntimeInvisibleAnnotations");
    
    AnnotationsAttribute ainfo2 = (AnnotationsAttribute)cf.getAttribute("RuntimeVisibleAnnotations");
    return getAnnotationType(clz, getClassPool(), ainfo, ainfo2);
  }




  
  static Object getAnnotationType(Class<?> clz, ClassPool cp, AnnotationsAttribute a1, AnnotationsAttribute a2) throws ClassNotFoundException {
    Annotation[] anno1, anno2;
    if (a1 == null) {
      anno1 = null;
    } else {
      anno1 = a1.getAnnotations();
    } 
    if (a2 == null) {
      anno2 = null;
    } else {
      anno2 = a2.getAnnotations();
    } 
    String typeName = clz.getName();
    if (anno1 != null)
      for (int i = 0; i < anno1.length; i++) {
        if (anno1[i].getTypeName().equals(typeName))
          return toAnnoType(anno1[i], cp); 
      }  
    if (anno2 != null)
      for (int i = 0; i < anno2.length; i++) {
        if (anno2[i].getTypeName().equals(typeName))
          return toAnnoType(anno2[i], cp); 
      }  
    return null;
  }

  
  public Object[] getAnnotations() throws ClassNotFoundException {
    return getAnnotations(false);
  }

  
  public Object[] getAvailableAnnotations() {
    try {
      return getAnnotations(true);
    }
    catch (ClassNotFoundException e) {
      throw new RuntimeException("Unexpected exception ", e);
    } 
  }


  
  private Object[] getAnnotations(boolean ignoreNotFound) throws ClassNotFoundException {
    ClassFile cf = getClassFile2();
    
    AnnotationsAttribute ainfo = (AnnotationsAttribute)cf.getAttribute("RuntimeInvisibleAnnotations");
    
    AnnotationsAttribute ainfo2 = (AnnotationsAttribute)cf.getAttribute("RuntimeVisibleAnnotations");
    return toAnnotationType(ignoreNotFound, getClassPool(), ainfo, ainfo2);
  }




  
  static Object[] toAnnotationType(boolean ignoreNotFound, ClassPool cp, AnnotationsAttribute a1, AnnotationsAttribute a2) throws ClassNotFoundException {
    Annotation[] anno1, anno2;
    int size1, size2;
    if (a1 == null) {
      anno1 = null;
      size1 = 0;
    } else {
      
      anno1 = a1.getAnnotations();
      size1 = anno1.length;
    } 
    
    if (a2 == null) {
      anno2 = null;
      size2 = 0;
    } else {
      
      anno2 = a2.getAnnotations();
      size2 = anno2.length;
    } 
    
    if (!ignoreNotFound) {
      Object[] result = new Object[size1 + size2];
      for (int m = 0; m < size1; m++) {
        result[m] = toAnnoType(anno1[m], cp);
      }
      for (int k = 0; k < size2; k++) {
        result[k + size1] = toAnnoType(anno2[k], cp);
      }
      return result;
    } 
    List<Object> annotations = new ArrayList();
    for (int i = 0; i < size1; i++) {
      try {
        annotations.add(toAnnoType(anno1[i], cp));
      }
      catch (ClassNotFoundException classNotFoundException) {}
    }  for (int j = 0; j < size2; j++) {
      try {
        annotations.add(toAnnoType(anno2[j], cp));
      }
      catch (ClassNotFoundException classNotFoundException) {}
    } 
    return annotations.toArray();
  }





  
  static Object[][] toAnnotationType(boolean ignoreNotFound, ClassPool cp, ParameterAnnotationsAttribute a1, ParameterAnnotationsAttribute a2, MethodInfo minfo) throws ClassNotFoundException {
    int numParameters = 0;
    if (a1 != null) {
      numParameters = a1.numParameters();
    } else if (a2 != null) {
      numParameters = a2.numParameters();
    } else {
      numParameters = Descriptor.numOfParameters(minfo.getDescriptor());
    } 
    Object[][] result = new Object[numParameters][];
    for (int i = 0; i < numParameters; i++) {
      Annotation[] anno1; Annotation[] anno2;
      int size1;
      int size2;
      if (a1 == null) {
        anno1 = null;
        size1 = 0;
      } else {
        
        anno1 = a1.getAnnotations()[i];
        size1 = anno1.length;
      } 
      
      if (a2 == null) {
        anno2 = null;
        size2 = 0;
      } else {
        
        anno2 = a2.getAnnotations()[i];
        size2 = anno2.length;
      } 
      
      if (!ignoreNotFound) {
        result[i] = new Object[size1 + size2]; int j;
        for (j = 0; j < size1; j++) {
          result[i][j] = toAnnoType(anno1[j], cp);
        }
        for (j = 0; j < size2; j++) {
          result[i][j + size1] = toAnnoType(anno2[j], cp);
        }
      } else {
        List<Object> annotations = new ArrayList(); int j;
        for (j = 0; j < size1; j++) {
          try {
            annotations.add(toAnnoType(anno1[j], cp));
          }
          catch (ClassNotFoundException classNotFoundException) {}
        } 
        for (j = 0; j < size2; j++) {
          try {
            annotations.add(toAnnoType(anno2[j], cp));
          }
          catch (ClassNotFoundException classNotFoundException) {}
        } 
        
        result[i] = annotations.toArray();
      } 
    } 
    
    return result;
  }


  
  private static Object toAnnoType(Annotation anno, ClassPool cp) throws ClassNotFoundException {
    try {
      ClassLoader cl = cp.getClassLoader();
      return anno.toAnnotationType(cl, cp);
    }
    catch (ClassNotFoundException e) {
      ClassLoader cl2 = cp.getClass().getClassLoader();
      try {
        return anno.toAnnotationType(cl2, cp);
      }
      catch (ClassNotFoundException e2) {
        try {
          Class<?> clazz = cp.get(anno.getTypeName()).toClass();
          return AnnotationImpl.make(clazz
              .getClassLoader(), clazz, cp, anno);
        
        }
        catch (Throwable e3) {
          throw new ClassNotFoundException(anno.getTypeName());
        } 
      } 
    } 
  }

  
  public boolean subclassOf(CtClass superclass) {
    if (superclass == null) {
      return false;
    }
    String superName = superclass.getName();
    CtClass curr = this;
    try {
      while (curr != null) {
        if (curr.getName().equals(superName)) {
          return true;
        }
        curr = curr.getSuperclass();
      }
    
    } catch (Exception exception) {}
    return false;
  }

  
  public CtClass getSuperclass() throws NotFoundException {
    String supername = getClassFile2().getSuperclass();
    if (supername == null)
      return null; 
    return this.classPool.get(supername);
  }

  
  public void setSuperclass(CtClass clazz) throws CannotCompileException {
    checkModify();
    if (isInterface()) {
      addInterface(clazz);
    } else {
      getClassFile2().setSuperclass(clazz.getName());
    } 
  }
  
  public CtClass[] getInterfaces() throws NotFoundException {
    String[] ifs = getClassFile2().getInterfaces();
    int num = ifs.length;
    CtClass[] ifc = new CtClass[num];
    for (int i = 0; i < num; i++) {
      ifc[i] = this.classPool.get(ifs[i]);
    }
    return ifc;
  }
  
  public void setInterfaces(CtClass[] list) {
    String[] ifs;
    checkModify();
    
    if (list == null) {
      ifs = new String[0];
    } else {
      int num = list.length;
      ifs = new String[num];
      for (int i = 0; i < num; i++) {
        ifs[i] = list[i].getName();
      }
    } 
    getClassFile2().setInterfaces(ifs);
  }

  
  public void addInterface(CtClass anInterface) {
    checkModify();
    if (anInterface != null) {
      getClassFile2().addInterface(anInterface.getName());
    }
  }
  
  public CtClass getDeclaringClass() throws NotFoundException {
    ClassFile cf = getClassFile2();
    InnerClassesAttribute ica = (InnerClassesAttribute)cf.getAttribute("InnerClasses");
    
    if (ica == null) {
      return null;
    }
    String name = getName();
    int n = ica.tableLength();
    for (int i = 0; i < n; i++) {
      if (name.equals(ica.innerClass(i))) {
        String outName = ica.outerClass(i);
        if (outName != null) {
          return this.classPool.get(outName);
        }

        
        EnclosingMethodAttribute ema = (EnclosingMethodAttribute)cf.getAttribute("EnclosingMethod");
        
        if (ema != null) {
          return this.classPool.get(ema.className());
        }
      } 
    } 
    return null;
  }


  
  public CtBehavior getEnclosingBehavior() throws NotFoundException {
    ClassFile cf = getClassFile2();
    
    EnclosingMethodAttribute ema = (EnclosingMethodAttribute)cf.getAttribute("EnclosingMethod");
    
    if (ema == null)
      return null; 
    CtClass enc = this.classPool.get(ema.className());
    String name = ema.methodName();
    if ("<init>".equals(name))
      return enc.getConstructor(ema.methodDescriptor()); 
    if ("<clinit>".equals(name)) {
      return enc.getClassInitializer();
    }
    return enc.getMethod(name, ema.methodDescriptor());
  }


  
  public CtClass makeNestedClass(String name, boolean isStatic) {
    if (!isStatic) {
      throw new RuntimeException("sorry, only nested static class is supported");
    }
    
    checkModify();
    CtClass c = this.classPool.makeNestedClass(getName() + "$" + name);
    ClassFile cf = getClassFile2();
    ClassFile cf2 = c.getClassFile2();
    InnerClassesAttribute ica = (InnerClassesAttribute)cf.getAttribute("InnerClasses");
    
    if (ica == null) {
      ica = new InnerClassesAttribute(cf.getConstPool());
      cf.addAttribute((AttributeInfo)ica);
    } 
    
    ica.append(c.getName(), getName(), name, cf2
        .getAccessFlags() & 0xFFFFFFDF | 0x8);
    cf2.addAttribute(ica.copy(cf2.getConstPool(), null));
    return c;
  }


  
  private void nameReplaced() {
    CtMember.Cache cache = hasMemberCache();
    if (cache != null) {
      CtMember mth = cache.methodHead();
      CtMember tail = cache.lastMethod();
      while (mth != tail) {
        mth = mth.next();
        mth.nameReplaced();
      } 
    } 
  }



  
  protected CtMember.Cache hasMemberCache() {
    if (this.memberCache != null)
      return this.memberCache.get(); 
    return null;
  }
  
  protected synchronized CtMember.Cache getMembers() {
    CtMember.Cache cache = null;
    if (this.memberCache == null || (
      cache = this.memberCache.get()) == null) {
      cache = new CtMember.Cache(this);
      makeFieldCache(cache);
      makeBehaviorCache(cache);
      this.memberCache = new WeakReference<>(cache);
    } 
    
    return cache;
  }
  
  private void makeFieldCache(CtMember.Cache cache) {
    List<FieldInfo> fields = getClassFile3(false).getFields();
    for (FieldInfo finfo : fields)
      cache.addField(new CtField(finfo, this)); 
  }
  
  private void makeBehaviorCache(CtMember.Cache cache) {
    List<MethodInfo> methods = getClassFile3(false).getMethods();
    for (MethodInfo minfo : methods) {
      if (minfo.isMethod()) {
        cache.addMethod(new CtMethod(minfo, this)); continue;
      } 
      cache.addConstructor(new CtConstructor(minfo, this));
    } 
  }
  
  public CtField[] getFields() {
    List<CtMember> alist = new ArrayList<>();
    getFields(alist, this);
    return alist.<CtField>toArray(new CtField[alist.size()]);
  }
  
  private static void getFields(List<CtMember> alist, CtClass cc) {
    if (cc == null) {
      return;
    }
    try {
      getFields(alist, cc.getSuperclass());
    }
    catch (NotFoundException notFoundException) {}
    
    try {
      CtClass[] ifs = cc.getInterfaces();
      for (CtClass ctc : ifs) {
        getFields(alist, ctc);
      }
    } catch (NotFoundException notFoundException) {}
    
    CtMember.Cache memCache = ((CtClassType)cc).getMembers();
    CtMember field = memCache.fieldHead();
    CtMember tail = memCache.lastField();
    while (field != tail) {
      field = field.next();
      if (!Modifier.isPrivate(field.getModifiers())) {
        alist.add(field);
      }
    } 
  }
  
  public CtField getField(String name, String desc) throws NotFoundException {
    CtField f = getField2(name, desc);
    return checkGetField(f, name, desc);
  }


  
  private CtField checkGetField(CtField f, String name, String desc) throws NotFoundException {
    if (f == null) {
      String msg = "field: " + name;
      if (desc != null) {
        msg = msg + " type " + desc;
      }
      throw new NotFoundException(msg + " in " + getName());
    } 
    return f;
  }

  
  CtField getField2(String name, String desc) {
    CtField df = getDeclaredField2(name, desc);
    if (df != null) {
      return df;
    }
    try {
      CtClass[] ifs = getInterfaces();
      for (CtClass ctc : ifs) {
        CtField f = ctc.getField2(name, desc);
        if (f != null) {
          return f;
        }
      } 
      CtClass s = getSuperclass();
      if (s != null) {
        return s.getField2(name, desc);
      }
    } catch (NotFoundException notFoundException) {}
    return null;
  }

  
  public CtField[] getDeclaredFields() {
    CtMember.Cache memCache = getMembers();
    CtMember field = memCache.fieldHead();
    CtMember tail = memCache.lastField();
    int num = CtMember.Cache.count(field, tail);
    CtField[] cfs = new CtField[num];
    int i = 0;
    while (field != tail) {
      field = field.next();
      cfs[i++] = (CtField)field;
    } 
    
    return cfs;
  }

  
  public CtField getDeclaredField(String name) throws NotFoundException {
    return getDeclaredField(name, (String)null);
  }

  
  public CtField getDeclaredField(String name, String desc) throws NotFoundException {
    CtField f = getDeclaredField2(name, desc);
    return checkGetField(f, name, desc);
  }
  
  private CtField getDeclaredField2(String name, String desc) {
    CtMember.Cache memCache = getMembers();
    CtMember field = memCache.fieldHead();
    CtMember tail = memCache.lastField();
    while (field != tail) {
      field = field.next();
      if (field.getName().equals(name) && (desc == null || desc
        .equals(field.getSignature()))) {
        return (CtField)field;
      }
    } 
    return null;
  }

  
  public CtBehavior[] getDeclaredBehaviors() {
    CtMember.Cache memCache = getMembers();
    CtMember cons = memCache.consHead();
    CtMember consTail = memCache.lastCons();
    int cnum = CtMember.Cache.count(cons, consTail);
    CtMember mth = memCache.methodHead();
    CtMember mthTail = memCache.lastMethod();
    int mnum = CtMember.Cache.count(mth, mthTail);
    
    CtBehavior[] cb = new CtBehavior[cnum + mnum];
    int i = 0;
    while (cons != consTail) {
      cons = cons.next();
      cb[i++] = (CtBehavior)cons;
    } 
    
    while (mth != mthTail) {
      mth = mth.next();
      cb[i++] = (CtBehavior)mth;
    } 
    
    return cb;
  }

  
  public CtConstructor[] getConstructors() {
    CtMember.Cache memCache = getMembers();
    CtMember cons = memCache.consHead();
    CtMember consTail = memCache.lastCons();
    
    int n = 0;
    CtMember mem = cons;
    while (mem != consTail) {
      mem = mem.next();
      if (isPubCons((CtConstructor)mem)) {
        n++;
      }
    } 
    CtConstructor[] result = new CtConstructor[n];
    int i = 0;
    mem = cons;
    while (mem != consTail) {
      mem = mem.next();
      CtConstructor cc = (CtConstructor)mem;
      if (isPubCons(cc)) {
        result[i++] = cc;
      }
    } 
    return result;
  }
  
  private static boolean isPubCons(CtConstructor cons) {
    return (!Modifier.isPrivate(cons.getModifiers()) && cons
      .isConstructor());
  }



  
  public CtConstructor getConstructor(String desc) throws NotFoundException {
    CtMember.Cache memCache = getMembers();
    CtMember cons = memCache.consHead();
    CtMember consTail = memCache.lastCons();
    
    while (cons != consTail) {
      cons = cons.next();
      CtConstructor cc = (CtConstructor)cons;
      if (cc.getMethodInfo2().getDescriptor().equals(desc) && cc
        .isConstructor()) {
        return cc;
      }
    } 
    return super.getConstructor(desc);
  }

  
  public CtConstructor[] getDeclaredConstructors() {
    CtMember.Cache memCache = getMembers();
    CtMember cons = memCache.consHead();
    CtMember consTail = memCache.lastCons();
    
    int n = 0;
    CtMember mem = cons;
    while (mem != consTail) {
      mem = mem.next();
      CtConstructor cc = (CtConstructor)mem;
      if (cc.isConstructor()) {
        n++;
      }
    } 
    CtConstructor[] result = new CtConstructor[n];
    int i = 0;
    mem = cons;
    while (mem != consTail) {
      mem = mem.next();
      CtConstructor cc = (CtConstructor)mem;
      if (cc.isConstructor()) {
        result[i++] = cc;
      }
    } 
    return result;
  }

  
  public CtConstructor getClassInitializer() {
    CtMember.Cache memCache = getMembers();
    CtMember cons = memCache.consHead();
    CtMember consTail = memCache.lastCons();
    
    while (cons != consTail) {
      cons = cons.next();
      CtConstructor cc = (CtConstructor)cons;
      if (cc.isClassInitializer()) {
        return cc;
      }
    } 
    return null;
  }

  
  public CtMethod[] getMethods() {
    Map<String, CtMember> h = new HashMap<>();
    getMethods0(h, this);
    return (CtMethod[])h.values().toArray((Object[])new CtMethod[h.size()]);
  }
  
  private static void getMethods0(Map<String, CtMember> h, CtClass cc) {
    try {
      CtClass[] ifs = cc.getInterfaces();
      for (CtClass ctc : ifs) {
        getMethods0(h, ctc);
      }
    } catch (NotFoundException notFoundException) {}
    
    try {
      CtClass s = cc.getSuperclass();
      if (s != null) {
        getMethods0(h, s);
      }
    } catch (NotFoundException notFoundException) {}
    
    if (cc instanceof CtClassType) {
      CtMember.Cache memCache = ((CtClassType)cc).getMembers();
      CtMember mth = memCache.methodHead();
      CtMember mthTail = memCache.lastMethod();
      
      while (mth != mthTail) {
        mth = mth.next();
        if (!Modifier.isPrivate(mth.getModifiers())) {
          h.put(((CtMethod)mth).getStringRep(), mth);
        }
      } 
    } 
  }


  
  public CtMethod getMethod(String name, String desc) throws NotFoundException {
    CtMethod m = getMethod0(this, name, desc);
    if (m != null)
      return m; 
    throw new NotFoundException(name + "(..) is not found in " + 
        getName());
  }

  
  private static CtMethod getMethod0(CtClass cc, String name, String desc) {
    if (cc instanceof CtClassType) {
      CtMember.Cache memCache = ((CtClassType)cc).getMembers();
      CtMember mth = memCache.methodHead();
      CtMember mthTail = memCache.lastMethod();
      
      while (mth != mthTail) {
        mth = mth.next();
        if (mth.getName().equals(name) && ((CtMethod)mth)
          .getMethodInfo2().getDescriptor().equals(desc)) {
          return (CtMethod)mth;
        }
      } 
    } 
    try {
      CtClass s = cc.getSuperclass();
      if (s != null) {
        CtMethod m = getMethod0(s, name, desc);
        if (m != null) {
          return m;
        }
      } 
    } catch (NotFoundException notFoundException) {}
    
    try {
      CtClass[] ifs = cc.getInterfaces();
      for (CtClass ctc : ifs) {
        CtMethod m = getMethod0(ctc, name, desc);
        if (m != null) {
          return m;
        }
      } 
    } catch (NotFoundException notFoundException) {}
    return null;
  }

  
  public CtMethod[] getDeclaredMethods() {
    CtMember.Cache memCache = getMembers();
    CtMember mth = memCache.methodHead();
    CtMember mthTail = memCache.lastMethod();
    List<CtMember> methods = new ArrayList<>();
    while (mth != mthTail) {
      mth = mth.next();
      methods.add(mth);
    } 
    
    return methods.<CtMethod>toArray(new CtMethod[methods.size()]);
  }

  
  public CtMethod[] getDeclaredMethods(String name) throws NotFoundException {
    CtMember.Cache memCache = getMembers();
    CtMember mth = memCache.methodHead();
    CtMember mthTail = memCache.lastMethod();
    List<CtMember> methods = new ArrayList<>();
    while (mth != mthTail) {
      mth = mth.next();
      if (mth.getName().equals(name)) {
        methods.add(mth);
      }
    } 
    return methods.<CtMethod>toArray(new CtMethod[methods.size()]);
  }

  
  public CtMethod getDeclaredMethod(String name) throws NotFoundException {
    CtMember.Cache memCache = getMembers();
    CtMember mth = memCache.methodHead();
    CtMember mthTail = memCache.lastMethod();
    while (mth != mthTail) {
      mth = mth.next();
      if (mth.getName().equals(name)) {
        return (CtMethod)mth;
      }
    } 
    throw new NotFoundException(name + "(..) is not found in " + 
        getName());
  }



  
  public CtMethod getDeclaredMethod(String name, CtClass[] params) throws NotFoundException {
    String desc = Descriptor.ofParameters(params);
    CtMember.Cache memCache = getMembers();
    CtMember mth = memCache.methodHead();
    CtMember mthTail = memCache.lastMethod();
    
    while (mth != mthTail) {
      mth = mth.next();
      if (mth.getName().equals(name) && ((CtMethod)mth)
        .getMethodInfo2().getDescriptor().startsWith(desc)) {
        return (CtMethod)mth;
      }
    } 
    throw new NotFoundException(name + "(..) is not found in " + 
        getName());
  }



  
  public void addField(CtField f, String init) throws CannotCompileException {
    addField(f, CtField.Initializer.byExpr(init));
  }



  
  public void addField(CtField f, CtField.Initializer init) throws CannotCompileException {
    checkModify();
    if (f.getDeclaringClass() != this) {
      throw new CannotCompileException("cannot add");
    }
    if (init == null) {
      init = f.getInit();
    }
    if (init != null) {
      init.check(f.getSignature());
      int mod = f.getModifiers();
      if (Modifier.isStatic(mod) && Modifier.isFinal(mod)) {
        try {
          ConstPool cp = getClassFile2().getConstPool();
          int index = init.getConstantValue(cp, f.getType());
          if (index != 0) {
            f.getFieldInfo2().addAttribute((AttributeInfo)new ConstantAttribute(cp, index));
            init = null;
          }
        
        } catch (NotFoundException notFoundException) {}
      }
    } 
    getMembers().addField(f);
    getClassFile2().addField(f.getFieldInfo2());
    
    if (init != null) {
      FieldInitLink fil = new FieldInitLink(f, init);
      FieldInitLink link = this.fieldInitializers;
      if (link == null) {
        this.fieldInitializers = fil;
      } else {
        while (link.next != null) {
          link = link.next;
        }
        link.next = fil;
      } 
    } 
  }

  
  public void removeField(CtField f) throws NotFoundException {
    checkModify();
    FieldInfo fi = f.getFieldInfo2();
    ClassFile cf = getClassFile2();
    if (cf.getFields().remove(fi)) {
      getMembers().remove(f);
      this.gcConstPool = true;
    } else {
      
      throw new NotFoundException(f.toString());
    } 
  }


  
  public CtConstructor makeClassInitializer() throws CannotCompileException {
    CtConstructor clinit = getClassInitializer();
    if (clinit != null) {
      return clinit;
    }
    checkModify();
    ClassFile cf = getClassFile2();
    Bytecode code = new Bytecode(cf.getConstPool(), 0, 0);
    modifyClassConstructor(cf, code, 0, 0);
    return getClassInitializer();
  }



  
  public void addConstructor(CtConstructor c) throws CannotCompileException {
    checkModify();
    if (c.getDeclaringClass() != this) {
      throw new CannotCompileException("cannot add");
    }
    getMembers().addConstructor(c);
    getClassFile2().addMethod(c.getMethodInfo2());
  }

  
  public void removeConstructor(CtConstructor m) throws NotFoundException {
    checkModify();
    MethodInfo mi = m.getMethodInfo2();
    ClassFile cf = getClassFile2();
    if (cf.getMethods().remove(mi)) {
      getMembers().remove(m);
      this.gcConstPool = true;
    } else {
      
      throw new NotFoundException(m.toString());
    } 
  }
  
  public void addMethod(CtMethod m) throws CannotCompileException {
    checkModify();
    if (m.getDeclaringClass() != this) {
      throw new CannotCompileException("bad declaring class");
    }
    int mod = m.getModifiers();
    if ((getModifiers() & 0x200) != 0) {
      if (Modifier.isProtected(mod) || Modifier.isPrivate(mod)) {
        throw new CannotCompileException("an interface method must be public: " + m
            .toString());
      }
      m.setModifiers(mod | 0x1);
    } 
    
    getMembers().addMethod(m);
    getClassFile2().addMethod(m.getMethodInfo2());
    if ((mod & 0x400) != 0) {
      setModifiers(getModifiers() | 0x400);
    }
  }

  
  public void removeMethod(CtMethod m) throws NotFoundException {
    checkModify();
    MethodInfo mi = m.getMethodInfo2();
    ClassFile cf = getClassFile2();
    if (cf.getMethods().remove(mi)) {
      getMembers().remove(m);
      this.gcConstPool = true;
    } else {
      
      throw new NotFoundException(m.toString());
    } 
  }

  
  public byte[] getAttribute(String name) {
    AttributeInfo ai = getClassFile2().getAttribute(name);
    if (ai == null)
      return null; 
    return ai.get();
  }


  
  public void setAttribute(String name, byte[] data) {
    checkModify();
    ClassFile cf = getClassFile2();
    cf.addAttribute(new AttributeInfo(cf.getConstPool(), name, data));
  }



  
  public void instrument(CodeConverter converter) throws CannotCompileException {
    checkModify();
    ClassFile cf = getClassFile2();
    ConstPool cp = cf.getConstPool();
    List<MethodInfo> methods = cf.getMethods();
    for (MethodInfo minfo : (MethodInfo[])methods.<MethodInfo>toArray(new MethodInfo[methods.size()])) {
      converter.doit(this, minfo, cp);
    }
  }


  
  public void instrument(ExprEditor editor) throws CannotCompileException {
    checkModify();
    ClassFile cf = getClassFile2();
    List<MethodInfo> methods = cf.getMethods();
    for (MethodInfo minfo : (MethodInfo[])methods.<MethodInfo>toArray(new MethodInfo[methods.size()])) {
      editor.doit(this, minfo);
    }
  }





  
  public void prune() {
    if (this.wasPruned) {
      return;
    }
    this.wasPruned = this.wasFrozen = true;
    getClassFile2().prune();
  }
  
  public void rebuildClassFile() {
    this.gcConstPool = true;
  }


  
  public void toBytecode(DataOutputStream out) throws CannotCompileException, IOException {
    try {
      if (isModified()) {
        checkPruned("toBytecode");
        ClassFile cf = getClassFile2();
        if (this.gcConstPool) {
          cf.compact();
          this.gcConstPool = false;
        } 
        
        modifyClassConstructor(cf);
        modifyConstructors(cf);
        if (debugDump != null) {
          dumpClassFile(cf);
        }
        cf.write(out);
        out.flush();
        this.fieldInitializers = null;
        if (this.doPruning) {
          
          cf.prune();
          this.wasPruned = true;
        } 
      } else {
        
        this.classPool.writeClassfile(getName(), out);
      } 


      
      this.getCount = 0;
      this.wasFrozen = true;
    }
    catch (NotFoundException e) {
      throw new CannotCompileException(e);
    }
    catch (IOException e) {
      throw new CannotCompileException(e);
    } 
  }

  
  private void dumpClassFile(ClassFile cf) throws IOException {
    DataOutputStream dump = makeFileOutput(debugDump);
    try {
      cf.write(dump);
    } finally {
      
      dump.close();
    } 
  }



  
  private void checkPruned(String method) {
    if (this.wasPruned) {
      throw new RuntimeException(method + "(): " + getName() + " was pruned.");
    }
  }


  
  public boolean stopPruning(boolean stop) {
    boolean prev = !this.doPruning;
    this.doPruning = !stop;
    return prev;
  }


  
  private void modifyClassConstructor(ClassFile cf) throws CannotCompileException, NotFoundException {
    if (this.fieldInitializers == null) {
      return;
    }
    Bytecode code = new Bytecode(cf.getConstPool(), 0, 0);
    Javac jv = new Javac(code, this);
    int stacksize = 0;
    boolean doInit = false;
    for (FieldInitLink fi = this.fieldInitializers; fi != null; fi = fi.next) {
      CtField f = fi.field;
      if (Modifier.isStatic(f.getModifiers())) {
        doInit = true;
        int s = fi.init.compileIfStatic(f.getType(), f.getName(), code, jv);
        
        if (stacksize < s) {
          stacksize = s;
        }
      } 
    } 
    if (doInit) {
      modifyClassConstructor(cf, code, stacksize, 0);
    }
  }


  
  private void modifyClassConstructor(ClassFile cf, Bytecode code, int stacksize, int localsize) throws CannotCompileException {
    MethodInfo m = cf.getStaticInitializer();
    if (m == null) {
      code.add(177);
      code.setMaxStack(stacksize);
      code.setMaxLocals(localsize);
      m = new MethodInfo(cf.getConstPool(), "<clinit>", "()V");
      m.setAccessFlags(8);
      m.setCodeAttribute(code.toCodeAttribute());
      cf.addMethod(m);
      CtMember.Cache cache = hasMemberCache();
      if (cache != null) {
        cache.addConstructor(new CtConstructor(m, this));
      }
    } else {
      CodeAttribute codeAttr = m.getCodeAttribute();
      if (codeAttr == null) {
        throw new CannotCompileException("empty <clinit>");
      }
      try {
        CodeIterator it = codeAttr.iterator();
        int pos = it.insertEx(code.get());
        it.insert(code.getExceptionTable(), pos);
        int maxstack = codeAttr.getMaxStack();
        if (maxstack < stacksize) {
          codeAttr.setMaxStack(stacksize);
        }
        int maxlocals = codeAttr.getMaxLocals();
        if (maxlocals < localsize) {
          codeAttr.setMaxLocals(localsize);
        }
      } catch (BadBytecode e) {
        throw new CannotCompileException(e);
      } 
    } 
    
    try {
      m.rebuildStackMapIf6(this.classPool, cf);
    }
    catch (BadBytecode e) {
      throw new CannotCompileException(e);
    } 
  }


  
  private void modifyConstructors(ClassFile cf) throws CannotCompileException, NotFoundException {
    if (this.fieldInitializers == null) {
      return;
    }
    ConstPool cp = cf.getConstPool();
    List<MethodInfo> methods = cf.getMethods();
    for (MethodInfo minfo : methods) {
      if (minfo.isConstructor()) {
        CodeAttribute codeAttr = minfo.getCodeAttribute();
        if (codeAttr != null) {
          
          try {
            Bytecode init = new Bytecode(cp, 0, codeAttr.getMaxLocals());
            
            CtClass[] params = Descriptor.getParameterTypes(minfo
                .getDescriptor(), this.classPool);
            
            int stacksize = makeFieldInitializer(init, params);
            insertAuxInitializer(codeAttr, init, stacksize);
            minfo.rebuildStackMapIf6(this.classPool, cf);
          }
          catch (BadBytecode e) {
            throw new CannotCompileException(e);
          } 
        }
      } 
    } 
  }



  
  private static void insertAuxInitializer(CodeAttribute codeAttr, Bytecode initializer, int stacksize) throws BadBytecode {
    CodeIterator it = codeAttr.iterator();
    int index = it.skipSuperConstructor();
    if (index < 0) {
      index = it.skipThisConstructor();
      if (index >= 0) {
        return;
      }
    } 

    
    int pos = it.insertEx(initializer.get());
    it.insert(initializer.getExceptionTable(), pos);
    int maxstack = codeAttr.getMaxStack();
    if (maxstack < stacksize) {
      codeAttr.setMaxStack(stacksize);
    }
  }

  
  private int makeFieldInitializer(Bytecode code, CtClass[] parameters) throws CannotCompileException, NotFoundException {
    int stacksize = 0;
    Javac jv = new Javac(code, this);
    try {
      jv.recordParams(parameters, false);
    }
    catch (CompileError e) {
      throw new CannotCompileException(e);
    } 
    
    for (FieldInitLink fi = this.fieldInitializers; fi != null; fi = fi.next) {
      CtField f = fi.field;
      if (!Modifier.isStatic(f.getModifiers())) {
        int s = fi.init.compile(f.getType(), f.getName(), code, parameters, jv);
        
        if (stacksize < s) {
          stacksize = s;
        }
      } 
    } 
    return stacksize;
  }


  
  Map<CtMethod, String> getHiddenMethods() {
    if (this.hiddenMethods == null) {
      this.hiddenMethods = new Hashtable<>();
    }
    return this.hiddenMethods;
  }
  int getUniqueNumber() {
    return this.uniqueNumberSeed++;
  }
  
  public String makeUniqueName(String prefix) {
    Map<Object, CtClassType> table = new HashMap<>();
    makeMemberList(table);
    Set<Object> keys = table.keySet();
    String[] methods = new String[keys.size()];
    keys.toArray(methods);
    
    if (notFindInArray(prefix, methods)) {
      return prefix;
    }
    int i = 100;
    
    while (true) {
      if (i > 999) {
        throw new RuntimeException("too many unique name");
      }
      String name = prefix + i++;
      if (notFindInArray(name, methods))
        return name; 
    } 
  }
  private static boolean notFindInArray(String prefix, String[] values) {
    int len = values.length;
    for (int i = 0; i < len; i++) {
      if (values[i].startsWith(prefix))
        return false; 
    } 
    return true;
  }
  
  private void makeMemberList(Map<Object, CtClassType> table) {
    int mod = getModifiers();
    if (Modifier.isAbstract(mod) || Modifier.isInterface(mod)) {
      try {
        CtClass[] ifs = getInterfaces();
        for (CtClass ic : ifs) {
          if (ic != null && ic instanceof CtClassType)
            ((CtClassType)ic).makeMemberList(table); 
        } 
      } catch (NotFoundException notFoundException) {}
    }
    try {
      CtClass s = getSuperclass();
      if (s != null && s instanceof CtClassType) {
        ((CtClassType)s).makeMemberList(table);
      }
    } catch (NotFoundException notFoundException) {}
    
    List<MethodInfo> methods = getClassFile2().getMethods();
    for (MethodInfo minfo : methods) {
      table.put(minfo.getName(), this);
    }
    List<FieldInfo> fields = getClassFile2().getFields();
    for (FieldInfo finfo : fields)
      table.put(finfo.getName(), this); 
  }
}
