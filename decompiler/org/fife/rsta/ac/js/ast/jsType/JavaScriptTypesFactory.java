package org.fife.rsta.ac.js.ast.jsType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.Util;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.FieldInfo;
import org.fife.rsta.ac.java.classreader.MemberInfo;
import org.fife.rsta.ac.java.classreader.MethodInfo;
import org.fife.rsta.ac.java.classreader.Util;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.type.TypeDeclaration;
import org.fife.rsta.ac.js.ast.type.TypeDeclarationFactory;
import org.fife.rsta.ac.js.ast.type.ecma.TypeDeclarations;
import org.fife.rsta.ac.js.completion.JSBeanCompletion;
import org.fife.rsta.ac.js.completion.JSClassCompletion;
import org.fife.rsta.ac.js.completion.JSCompletion;
import org.fife.rsta.ac.js.completion.JSConstructorCompletion;
import org.fife.rsta.ac.js.completion.JSFieldCompletion;
import org.fife.rsta.ac.js.completion.JSFunctionCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;










public abstract class JavaScriptTypesFactory
{
  protected HashMap<TypeDeclaration, JavaScriptType> cachedTypes = new HashMap<>();
  
  private boolean useBeanproperties;
  
  protected TypeDeclarationFactory typesFactory;
  private static final List<String> UNSUPPORTED_COMPLETIONS;
  private static String SPECIAL_METHOD = "<clinit>";


  
  static {
    UNSUPPORTED_COMPLETIONS = new ArrayList<>();
    UNSUPPORTED_COMPLETIONS.add("java.lang.Object");
  }

  
  public JavaScriptTypesFactory(TypeDeclarationFactory typesFactory) {
    this.typesFactory = typesFactory;
  }
  
  private static class DefaultJavaScriptTypeFactory
    extends JavaScriptTypesFactory {
    public DefaultJavaScriptTypeFactory(TypeDeclarationFactory typesFactory) {
      super(typesFactory);
    }
  }

  
  public static JavaScriptTypesFactory getDefaultJavaScriptTypesFactory(TypeDeclarationFactory typesFactory) {
    return new DefaultJavaScriptTypeFactory(typesFactory);
  }

  
  public void setUseBeanProperties(boolean useBeanproperties) {
    this.useBeanproperties = useBeanproperties;
  }

  
  public boolean isUseBeanProperties() {
    return this.useBeanproperties;
  }













  
  public JavaScriptType getCachedType(TypeDeclaration type, JarManager manager, DefaultCompletionProvider provider, String text) {
    if (manager == null || type == null) {
      return null;
    }
    
    if (this.cachedTypes.containsKey(type)) {
      return this.cachedTypes.get(type);
    }
    
    ClassFile cf = getClassFile(manager, type);
    
    JavaScriptType cachedType = makeJavaScriptType(type);
    
    this.cachedTypes.put(type, cachedType);
    readClassFile(cachedType, cf, provider, manager, type);
    return cachedType;
  }


  
  public ClassFile getClassFile(JarManager manager, TypeDeclaration type) {
    return (manager != null) ? manager.getClassEntry(type.getQualifiedName()) : null;
  }













  
  private void readClassFile(JavaScriptType cachedType, ClassFile cf, DefaultCompletionProvider provider, JarManager manager, TypeDeclaration type) {
    if (cf != null) {
      readMethodsAndFieldsFromTypeDeclaration(cachedType, provider, manager, cf);
    }
  }





  
  private boolean isBeanProperty(MethodInfo method) {
    return (method.getParameterCount() == 0 && (method
      .getName().startsWith("get") || method.getName()
      .startsWith("is")));
  }















  
  private void readMethodsAndFieldsFromTypeDeclaration(JavaScriptType cachedType, DefaultCompletionProvider provider, JarManager jarManager, ClassFile cf) {
    boolean staticOnly = cachedType.getType().isStaticsOnly();
    boolean supportsBeanProperties = cachedType.getType().supportsBeanProperties();
    boolean isJSType = this.typesFactory.isJavaScriptType(cachedType.getType());

    
    if (isJSType) {
      cachedType.setClassTypeCompletion((JSCompletion)new JSClassCompletion((CompletionProvider)provider, cf, false));
    }

    
    int methodCount = cf.getMethodCount();
    for (int i = 0; i < methodCount; i++) {
      MethodInfo info = cf.getMethodInfo(i);
      if (!info.isConstructor() && !SPECIAL_METHOD.equals(info.getName())) {
        if (isAccessible(info.getAccessFlags(), staticOnly, isJSType) && ((staticOnly && info.isStatic()) || !staticOnly)) {
          JSFunctionCompletion completion = new JSFunctionCompletion((CompletionProvider)provider, info, true);
          cachedType.addCompletion((JSCompletion)completion);
        } 

        
        if (!staticOnly && this.useBeanproperties && supportsBeanProperties && isBeanProperty(info)) {
          JSBeanCompletion beanCompletion = new JSBeanCompletion((CompletionProvider)provider, info, jarManager);
          
          cachedType.addCompletion((JSCompletion)beanCompletion);
        } 
      } 

      
      if (isJSType && info.isConstructor() && !SPECIAL_METHOD.equals(info.getName()) && 
        this.typesFactory.canJavaScriptBeInstantiated(cachedType.getType().getQualifiedName())) {
        
        JSConstructorCompletion completion = new JSConstructorCompletion((CompletionProvider)provider, info);
        cachedType.addConstructor((JSCompletion)completion);
      } 
    } 


    
    int fieldCount = cf.getFieldCount();
    for (int j = 0; j < fieldCount; j++) {
      FieldInfo info = cf.getFieldInfo(j);
      if (isAccessible((MemberInfo)info, staticOnly, isJSType)) {
        JSFieldCompletion completion = new JSFieldCompletion((CompletionProvider)provider, info);
        cachedType.addCompletion((JSCompletion)completion);
      } 
    } 

    
    String superClassName = cf.getSuperClassName(true);
    ClassFile superClass = getClassFileFor(cf, superClassName, jarManager);
    if (superClass != null && !ignoreClass(superClassName)) {
      TypeDeclaration type = createNewTypeDeclaration(superClass, staticOnly, false);
      JavaScriptType extendedType = makeJavaScriptType(type);
      cachedType.addExtension(extendedType);
      readClassFile(extendedType, superClass, provider, jarManager, type);
    } 


    
    for (int k = 0; k < cf.getImplementedInterfaceCount(); k++) {
      String inter = cf.getImplementedInterfaceName(k, true);
      ClassFile intf = getClassFileFor(cf, inter, jarManager);
      if (intf != null && !ignoreClass(inter)) {
        TypeDeclaration type = createNewTypeDeclaration(intf, staticOnly, false);
        
        JavaScriptType extendedType = new JavaScriptType(type);
        cachedType.addExtension(extendedType);
        readClassFile(extendedType, intf, provider, jarManager, type);
      } 
    } 
  }

  
  public static boolean ignoreClass(String className) {
    return UNSUPPORTED_COMPLETIONS.contains(className);
  }


  
  private boolean isAccessible(MemberInfo info, boolean staticOnly, boolean isJJType) {
    int access = info.getAccessFlags();
    boolean accessible = isAccessible(access, staticOnly, isJJType);
    
    return ((!staticOnly && accessible) || (staticOnly && info.isStatic() && accessible));
  }















  
  private boolean isAccessible(int access, boolean staticsOnly, boolean isJSType) {
    boolean accessible = false;
    if ((staticsOnly && Util.isPublic(access)) || (!staticsOnly && 
      Util.isPublic(access)) || (isJSType && Util.isProtected(access))) {
      accessible = true;
    }
    return accessible;
  }


  
  public TypeDeclaration createNewTypeDeclaration(ClassFile cf, boolean staticOnly) {
    return createNewTypeDeclaration(cf, staticOnly, true);
  }
  
  public TypeDeclaration createNewTypeDeclaration(ClassFile cf, boolean staticOnly, boolean addToCache) {
    String className = cf.getClassName(false);
    String packageName = cf.getPackageName();
    
    if (staticOnly && !addToCache)
    {
      return new TypeDeclaration(packageName, className, cf
          .getClassName(true), staticOnly);
    }
    
    String qualified = cf.getClassName(true);
    
    TypeDeclaration td = this.typesFactory.getTypeDeclaration(qualified);
    if (td == null) {
      
      td = new TypeDeclaration(packageName, className, cf.getClassName(true), staticOnly);
      
      if (addToCache) {
        this.typesFactory.addType(qualified, td);
      }
    } 
    return td;
  }







  
  private ClassFile getClassFileFor(ClassFile cf, String className, JarManager jarManager) {
    if (className == null) {
      return null;
    }
    
    ClassFile superClass = null;

    
    if (!Util.isFullyQualified(className)) {

      
      String pkg = cf.getPackageName();
      if (pkg != null) {
        String temp = pkg + "." + className;
        superClass = jarManager.getClassEntry(temp);
      }
    
    } else {
      
      superClass = jarManager.getClassEntry(className);
    } 
    
    return superClass;
  }











  
  public void populateCompletionsForType(JavaScriptType cachedType, Set<Completion> completions) {
    if (cachedType != null) {
      Map<String, JSCompletion> completionsForType = cachedType.getMethodFieldCompletions();
      for (JSCompletion completion : completionsForType.values()) {
        completions.add(completion);
      }

      
      List<JavaScriptType> extendedClasses = cachedType.getExtendedClasses();
      for (JavaScriptType extendedType : extendedClasses) {
        populateCompletionsForType(extendedType, completions);
      }
    } 
  }

  
  public void removeCachedType(TypeDeclaration typeDef) {
    this.cachedTypes.remove(typeDef);
  }

  
  public void clearCache() {
    this.cachedTypes.clear();
  }
  
  public JavaScriptType makeJavaScriptType(TypeDeclaration type) {
    return new JavaScriptType(type);
  }





  
  public List<JavaScriptType> getECMAObjectTypes(SourceCompletionProvider provider) {
    List<JavaScriptType> constructors = new ArrayList<>();
    
    Set<TypeDeclarations.JavaScriptObject> types = this.typesFactory.getECMAScriptObjects();
    JarManager manager = provider.getJarManager();
    for (TypeDeclarations.JavaScriptObject object : types) {
      TypeDeclaration type = this.typesFactory.getTypeDeclaration(object.getName());
      JavaScriptType js = getCachedType(type, manager, (DefaultCompletionProvider)provider, null);
      if (js != null) {
        constructors.add(js);
      }
    } 
    return constructors;
  }
}
