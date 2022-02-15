package javassist.tools.reflect;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CodeConverter;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.MethodInfo;




























































public class Reflection
  implements Translator
{
  static final String classobjectField = "_classobject";
  static final String classobjectAccessor = "_getClass";
  static final String metaobjectField = "_metaobject";
  static final String metaobjectGetter = "_getMetaobject";
  static final String metaobjectSetter = "_setMetaobject";
  static final String readPrefix = "_r_";
  static final String writePrefix = "_w_";
  static final String metaobjectClassName = "javassist.tools.reflect.Metaobject";
  static final String classMetaobjectClassName = "javassist.tools.reflect.ClassMetaobject";
  protected CtMethod trapMethod;
  protected CtMethod trapStaticMethod;
  protected CtMethod trapRead;
  protected CtMethod trapWrite;
  protected CtClass[] readParam;
  protected ClassPool classPool;
  protected CodeConverter converter;
  
  private boolean isExcluded(String name) {
    return (name.startsWith("_m_") || name
      .equals("_getClass") || name
      .equals("_setMetaobject") || name
      .equals("_getMetaobject") || name
      .startsWith("_r_") || name
      .startsWith("_w_"));
  }



  
  public Reflection() {
    this.classPool = null;
    this.converter = new CodeConverter();
  }




  
  public void start(ClassPool pool) throws NotFoundException {
    this.classPool = pool;
    String msg = "javassist.tools.reflect.Sample is not found or broken.";
    
    try {
      CtClass c = this.classPool.get("javassist.tools.reflect.Sample");
      rebuildClassFile(c.getClassFile());
      this.trapMethod = c.getDeclaredMethod("trap");
      this.trapStaticMethod = c.getDeclaredMethod("trapStatic");
      this.trapRead = c.getDeclaredMethod("trapRead");
      this.trapWrite = c.getDeclaredMethod("trapWrite");
      this
        .readParam = new CtClass[] { this.classPool.get("java.lang.Object") };
    }
    catch (NotFoundException e) {
      throw new RuntimeException("javassist.tools.reflect.Sample is not found or broken.");
    } catch (BadBytecode e) {
      throw new RuntimeException("javassist.tools.reflect.Sample is not found or broken.");
    } 
  }







  
  public void onLoad(ClassPool pool, String classname) throws CannotCompileException, NotFoundException {
    CtClass clazz = pool.get(classname);
    clazz.instrument(this.converter);
  }
















  
  public boolean makeReflective(String classname, String metaobject, String metaclass) throws CannotCompileException, NotFoundException {
    return makeReflective(this.classPool.get(classname), this.classPool
        .get(metaobject), this.classPool
        .get(metaclass));
  }




















  
  public boolean makeReflective(Class<?> clazz, Class<?> metaobject, Class<?> metaclass) throws CannotCompileException, NotFoundException {
    return makeReflective(clazz.getName(), metaobject.getName(), metaclass
        .getName());
  }






















  
  public boolean makeReflective(CtClass clazz, CtClass metaobject, CtClass metaclass) throws CannotCompileException, CannotReflectException, NotFoundException {
    if (clazz.isInterface()) {
      throw new CannotReflectException("Cannot reflect an interface: " + clazz
          .getName());
    }
    if (clazz.subclassOf(this.classPool.get("javassist.tools.reflect.ClassMetaobject"))) {
      throw new CannotReflectException("Cannot reflect a subclass of ClassMetaobject: " + clazz
          
          .getName());
    }
    if (clazz.subclassOf(this.classPool.get("javassist.tools.reflect.Metaobject"))) {
      throw new CannotReflectException("Cannot reflect a subclass of Metaobject: " + clazz
          
          .getName());
    }
    registerReflectiveClass(clazz);
    return modifyClassfile(clazz, metaobject, metaclass);
  }




  
  private void registerReflectiveClass(CtClass clazz) {
    CtField[] fs = clazz.getDeclaredFields();
    for (int i = 0; i < fs.length; i++) {
      CtField f = fs[i];
      int mod = f.getModifiers();
      if ((mod & 0x1) != 0 && (mod & 0x10) == 0) {
        String name = f.getName();
        this.converter.replaceFieldRead(f, clazz, "_r_" + name);
        this.converter.replaceFieldWrite(f, clazz, "_w_" + name);
      } 
    } 
  }



  
  private boolean modifyClassfile(CtClass clazz, CtClass metaobject, CtClass metaclass) throws CannotCompileException, NotFoundException {
    if (clazz.getAttribute("Reflective") != null)
      return false; 
    clazz.setAttribute("Reflective", new byte[0]);
    
    CtClass mlevel = this.classPool.get("javassist.tools.reflect.Metalevel");
    boolean addMeta = !clazz.subtypeOf(mlevel);
    if (addMeta) {
      clazz.addInterface(mlevel);
    }
    processMethods(clazz, addMeta);
    processFields(clazz);

    
    if (addMeta) {
      CtField ctField = new CtField(this.classPool.get("javassist.tools.reflect.Metaobject"), "_metaobject", clazz);
      
      ctField.setModifiers(4);
      clazz.addField(ctField, CtField.Initializer.byNewWithParams(metaobject));
      
      clazz.addMethod(CtNewMethod.getter("_getMetaobject", ctField));
      clazz.addMethod(CtNewMethod.setter("_setMetaobject", ctField));
    } 
    
    CtField f = new CtField(this.classPool.get("javassist.tools.reflect.ClassMetaobject"), "_classobject", clazz);
    
    f.setModifiers(10);
    clazz.addField(f, CtField.Initializer.byNew(metaclass, new String[] { clazz
            .getName() }));
    
    clazz.addMethod(CtNewMethod.getter("_getClass", f));
    return true;
  }


  
  private void processMethods(CtClass clazz, boolean dontSearch) throws CannotCompileException, NotFoundException {
    CtMethod[] ms = clazz.getMethods();
    for (int i = 0; i < ms.length; i++) {
      CtMethod m = ms[i];
      int mod = m.getModifiers();
      if (Modifier.isPublic(mod) && !Modifier.isAbstract(mod)) {
        processMethods0(mod, clazz, m, i, dontSearch);
      }
    } 
  }


  
  private void processMethods0(int mod, CtClass clazz, CtMethod m, int identifier, boolean dontSearch) throws CannotCompileException, NotFoundException {
    CtMethod body, m2;
    String name = m.getName();
    
    if (isExcluded(name)) {
      return;
    }
    
    if (m.getDeclaringClass() == clazz) {
      if (Modifier.isNative(mod)) {
        return;
      }
      m2 = m;
      if (Modifier.isFinal(mod)) {
        mod &= 0xFFFFFFEF;
        m2.setModifiers(mod);
      } 
    } else {
      
      if (Modifier.isFinal(mod)) {
        return;
      }
      mod &= 0xFFFFFEFF;
      m2 = CtNewMethod.delegator(findOriginal(m, dontSearch), clazz);
      m2.setModifiers(mod);
      clazz.addMethod(m2);
    } 
    
    m2.setName("_m_" + identifier + "_" + name);

    
    if (Modifier.isStatic(mod)) {
      body = this.trapStaticMethod;
    } else {
      body = this.trapMethod;
    } 
    
    CtMethod wmethod = CtNewMethod.wrapped(m.getReturnType(), name, m
        .getParameterTypes(), m.getExceptionTypes(), body, 
        CtMethod.ConstParameter.integer(identifier), clazz);
    
    wmethod.setModifiers(mod);
    clazz.addMethod(wmethod);
  }


  
  private CtMethod findOriginal(CtMethod m, boolean dontSearch) throws NotFoundException {
    if (dontSearch) {
      return m;
    }
    String name = m.getName();
    CtMethod[] ms = m.getDeclaringClass().getDeclaredMethods();
    for (int i = 0; i < ms.length; i++) {
      String orgName = ms[i].getName();
      if (orgName.endsWith(name) && orgName
        .startsWith("_m_") && ms[i]
        .getSignature().equals(m.getSignature())) {
        return ms[i];
      }
    } 
    return m;
  }


  
  private void processFields(CtClass clazz) throws CannotCompileException, NotFoundException {
    CtField[] fs = clazz.getDeclaredFields();
    for (int i = 0; i < fs.length; i++) {
      CtField f = fs[i];
      int mod = f.getModifiers();
      if ((mod & 0x1) != 0 && (mod & 0x10) == 0) {
        mod |= 0x8;
        String name = f.getName();
        CtClass ftype = f.getType();
        
        CtMethod wmethod = CtNewMethod.wrapped(ftype, "_r_" + name, this.readParam, null, this.trapRead, 
            
            CtMethod.ConstParameter.string(name), clazz);
        
        wmethod.setModifiers(mod);
        clazz.addMethod(wmethod);
        CtClass[] writeParam = new CtClass[2];
        writeParam[0] = this.classPool.get("java.lang.Object");
        writeParam[1] = ftype;
        wmethod = CtNewMethod.wrapped(CtClass.voidType, "_w_" + name, writeParam, null, this.trapWrite, 

            
            CtMethod.ConstParameter.string(name), clazz);
        wmethod.setModifiers(mod);
        clazz.addMethod(wmethod);
      } 
    } 
  }
  
  public void rebuildClassFile(ClassFile cf) throws BadBytecode {
    if (ClassFile.MAJOR_VERSION < 50) {
      return;
    }
    for (MethodInfo mi : cf.getMethods())
      mi.rebuildStackMap(this.classPool); 
  }
}
