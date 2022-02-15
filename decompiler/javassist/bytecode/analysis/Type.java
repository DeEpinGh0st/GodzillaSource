package javassist.bytecode.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;


























public class Type
{
  private final CtClass clazz;
  private final boolean special;
  private static final Map<CtClass, Type> prims = new IdentityHashMap<>();
  
  public static final Type DOUBLE = new Type(CtClass.doubleType);
  
  public static final Type BOOLEAN = new Type(CtClass.booleanType);
  
  public static final Type LONG = new Type(CtClass.longType);
  
  public static final Type CHAR = new Type(CtClass.charType);
  
  public static final Type BYTE = new Type(CtClass.byteType);
  
  public static final Type SHORT = new Type(CtClass.shortType);
  
  public static final Type INTEGER = new Type(CtClass.intType);
  
  public static final Type FLOAT = new Type(CtClass.floatType);
  
  public static final Type VOID = new Type(CtClass.voidType);









  
  public static final Type UNINIT = new Type(null);




  
  public static final Type RETURN_ADDRESS = new Type(null, true);

  
  public static final Type TOP = new Type(null, true);







  
  public static final Type BOGUS = new Type(null, true);

  
  public static final Type OBJECT = lookupType("java.lang.Object");
  
  public static final Type SERIALIZABLE = lookupType("java.io.Serializable");
  
  public static final Type CLONEABLE = lookupType("java.lang.Cloneable");
  
  public static final Type THROWABLE = lookupType("java.lang.Throwable");
  
  static {
    prims.put(CtClass.doubleType, DOUBLE);
    prims.put(CtClass.longType, LONG);
    prims.put(CtClass.charType, CHAR);
    prims.put(CtClass.shortType, SHORT);
    prims.put(CtClass.intType, INTEGER);
    prims.put(CtClass.floatType, FLOAT);
    prims.put(CtClass.byteType, BYTE);
    prims.put(CtClass.booleanType, BOOLEAN);
    prims.put(CtClass.voidType, VOID);
  }









  
  public static Type get(CtClass clazz) {
    Type type = prims.get(clazz);
    return (type != null) ? type : new Type(clazz);
  }
  
  private static Type lookupType(String name) {
    try {
      return new Type(ClassPool.getDefault().get(name));
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    } 
  }
  
  Type(CtClass clazz) {
    this(clazz, false);
  }
  
  private Type(CtClass clazz, boolean special) {
    this.clazz = clazz;
    this.special = special;
  }

  
  boolean popChanged() {
    return false;
  }






  
  public int getSize() {
    return (this.clazz == CtClass.doubleType || this.clazz == CtClass.longType || this == TOP) ? 2 : 1;
  }





  
  public CtClass getCtClass() {
    return this.clazz;
  }





  
  public boolean isReference() {
    return (!this.special && (this.clazz == null || !this.clazz.isPrimitive()));
  }






  
  public boolean isSpecial() {
    return this.special;
  }





  
  public boolean isArray() {
    return (this.clazz != null && this.clazz.isArray());
  }






  
  public int getDimensions() {
    if (!isArray()) return 0;
    
    String name = this.clazz.getName();
    int pos = name.length() - 1;
    int count = 0;
    while (name.charAt(pos) == ']') {
      pos -= 2;
      count++;
    } 
    
    return count;
  }





  
  public Type getComponent() {
    CtClass component;
    if (this.clazz == null || !this.clazz.isArray()) {
      return null;
    }
    
    try {
      component = this.clazz.getComponentType();
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    } 
    
    Type type = prims.get(component);
    return (type != null) ? type : new Type(component);
  }








  
  public boolean isAssignableFrom(Type type) {
    if (this == type) {
      return true;
    }
    if ((type == UNINIT && isReference()) || (this == UNINIT && type.isReference())) {
      return true;
    }
    if (type instanceof MultiType) {
      return ((MultiType)type).isAssignableTo(this);
    }
    if (type instanceof MultiArrayType) {
      return ((MultiArrayType)type).isAssignableTo(this);
    }

    
    if (this.clazz == null || this.clazz.isPrimitive()) {
      return false;
    }
    try {
      return type.clazz.subtypeOf(this.clazz);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }











  
  public Type merge(Type type) {
    if (type == this)
      return this; 
    if (type == null)
      return this; 
    if (type == UNINIT)
      return this; 
    if (this == UNINIT) {
      return type;
    }
    
    if (!type.isReference() || !isReference()) {
      return BOGUS;
    }
    
    if (type instanceof MultiType) {
      return type.merge(this);
    }
    if (type.isArray() && isArray()) {
      return mergeArray(type);
    }
    try {
      return mergeClasses(type);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    } 
  }
  
  Type getRootComponent(Type type) {
    while (type.isArray()) {
      type = type.getComponent();
    }
    return type;
  }
  private Type createArray(Type rootComponent, int dims) {
    Type type;
    if (rootComponent instanceof MultiType) {
      return new MultiArrayType((MultiType)rootComponent, dims);
    }
    String name = arrayName(rootComponent.clazz.getName(), dims);

    
    try {
      type = get(getClassPool(rootComponent).get(name));
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    } 
    
    return type;
  }


  
  String arrayName(String component, int dims) {
    int i = component.length();
    int size = i + dims * 2;
    char[] string = new char[size];
    component.getChars(0, i, string, 0);
    while (i < size) {
      string[i++] = '[';
      string[i++] = ']';
    } 
    component = new String(string);
    return component;
  }
  
  private ClassPool getClassPool(Type rootComponent) {
    ClassPool pool = rootComponent.clazz.getClassPool();
    return (pool != null) ? pool : ClassPool.getDefault();
  } private Type mergeArray(Type type) {
    Type targetRoot;
    int targetDims;
    Type typeRoot = getRootComponent(type);
    Type thisRoot = getRootComponent(this);
    int typeDims = type.getDimensions();
    int thisDims = getDimensions();

    
    if (typeDims == thisDims) {
      Type mergedComponent = thisRoot.merge(typeRoot);


      
      if (mergedComponent == BOGUS) {
        return OBJECT;
      }
      return createArray(mergedComponent, thisDims);
    } 



    
    if (typeDims < thisDims) {
      targetRoot = typeRoot;
      targetDims = typeDims;
    } else {
      targetRoot = thisRoot;
      targetDims = thisDims;
    } 

    
    if (eq(CLONEABLE.clazz, targetRoot.clazz) || eq(SERIALIZABLE.clazz, targetRoot.clazz)) {
      return createArray(targetRoot, targetDims);
    }
    return createArray(OBJECT, targetDims);
  }
  
  private static CtClass findCommonSuperClass(CtClass one, CtClass two) throws NotFoundException {
    CtClass deep = one;
    CtClass shallow = two;
    CtClass backupShallow = shallow;
    CtClass backupDeep = deep;


    
    while (true) {
      if (eq(deep, shallow) && deep.getSuperclass() != null) {
        return deep;
      }
      CtClass deepSuper = deep.getSuperclass();
      CtClass shallowSuper = shallow.getSuperclass();
      
      if (shallowSuper == null) {
        
        shallow = backupShallow;
        
        break;
      } 
      if (deepSuper == null) {
        
        deep = backupDeep;
        backupDeep = backupShallow;
        backupShallow = deep;
        
        deep = shallow;
        shallow = backupShallow;
        
        break;
      } 
      deep = deepSuper;
      shallow = shallowSuper;
    } 

    
    while (true) {
      deep = deep.getSuperclass();
      if (deep == null) {
        break;
      }
      backupDeep = backupDeep.getSuperclass();
    } 
    
    deep = backupDeep;


    
    while (!eq(deep, shallow)) {
      deep = deep.getSuperclass();
      shallow = shallow.getSuperclass();
    } 
    
    return deep;
  }
  
  private Type mergeClasses(Type type) throws NotFoundException {
    CtClass superClass = findCommonSuperClass(this.clazz, type.clazz);

    
    if (superClass.getSuperclass() == null) {
      Map<String, CtClass> interfaces = findCommonInterfaces(type);
      if (interfaces.size() == 1)
        return new Type(interfaces.values().iterator().next()); 
      if (interfaces.size() > 1) {
        return new MultiType(interfaces);
      }
      
      return new Type(superClass);
    } 

    
    Map<String, CtClass> commonDeclared = findExclusiveDeclaredInterfaces(type, superClass);
    if (commonDeclared.size() > 0) {
      return new MultiType(commonDeclared, new Type(superClass));
    }
    
    return new Type(superClass);
  }
  
  private Map<String, CtClass> findCommonInterfaces(Type type) {
    Map<String, CtClass> typeMap = getAllInterfaces(type.clazz, null);
    Map<String, CtClass> thisMap = getAllInterfaces(this.clazz, null);
    
    return findCommonInterfaces(typeMap, thisMap);
  }
  
  private Map<String, CtClass> findExclusiveDeclaredInterfaces(Type type, CtClass exclude) {
    Map<String, CtClass> typeMap = getDeclaredInterfaces(type.clazz, null);
    Map<String, CtClass> thisMap = getDeclaredInterfaces(this.clazz, null);
    Map<String, CtClass> excludeMap = getAllInterfaces(exclude, null);
    
    for (String intf : excludeMap.keySet()) {
      typeMap.remove(intf);
      thisMap.remove(intf);
    } 
    
    return findCommonInterfaces(typeMap, thisMap);
  }

  
  Map<String, CtClass> findCommonInterfaces(Map<String, CtClass> typeMap, Map<String, CtClass> alterMap) {
    if (alterMap == null) {
      alterMap = new HashMap<>();
    }
    if (typeMap == null || typeMap.isEmpty()) {
      alterMap.clear();
    }
    Iterator<String> it = alterMap.keySet().iterator();
    while (it.hasNext()) {
      String name = it.next();
      if (!typeMap.containsKey(name)) {
        it.remove();
      }
    } 


    
    Collection<CtClass> interfaces = new ArrayList<>();
    for (CtClass intf : alterMap.values()) {
      try {
        interfaces.addAll(Arrays.asList(intf.getInterfaces()));
      } catch (NotFoundException e) {
        throw new RuntimeException(e);
      } 
    } 
    for (CtClass c : interfaces) {
      alterMap.remove(c.getName());
    }
    return alterMap;
  }
  
  Map<String, CtClass> getAllInterfaces(CtClass clazz, Map<String, CtClass> map) {
    if (map == null) {
      map = new HashMap<>();
    }
    if (clazz.isInterface())
      map.put(clazz.getName(), clazz); 
    do {
      try {
        CtClass[] interfaces = clazz.getInterfaces();
        for (CtClass intf : interfaces) {
          map.put(intf.getName(), intf);
          getAllInterfaces(intf, map);
        } 
        
        clazz = clazz.getSuperclass();
      } catch (NotFoundException e) {
        throw new RuntimeException(e);
      } 
    } while (clazz != null);
    
    return map;
  }
  Map<String, CtClass> getDeclaredInterfaces(CtClass clazz, Map<String, CtClass> map) {
    CtClass[] interfaces;
    if (map == null) {
      map = new HashMap<>();
    }
    if (clazz.isInterface()) {
      map.put(clazz.getName(), clazz);
    }
    
    try {
      interfaces = clazz.getInterfaces();
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    } 
    
    for (CtClass intf : interfaces) {
      map.put(intf.getName(), intf);
      getDeclaredInterfaces(intf, map);
    } 
    
    return map;
  }

  
  public int hashCode() {
    return getClass().hashCode() + this.clazz.hashCode();
  }

  
  public boolean equals(Object o) {
    if (!(o instanceof Type)) {
      return false;
    }
    return (o.getClass() == getClass() && eq(this.clazz, ((Type)o).clazz));
  }
  
  static boolean eq(CtClass one, CtClass two) {
    return (one == two || (one != null && two != null && one.getName().equals(two.getName())));
  }

  
  public String toString() {
    if (this == BOGUS)
      return "BOGUS"; 
    if (this == UNINIT)
      return "UNINIT"; 
    if (this == RETURN_ADDRESS)
      return "RETURN ADDRESS"; 
    if (this == TOP) {
      return "TOP";
    }
    return (this.clazz == null) ? "null" : this.clazz.getName();
  }
}
