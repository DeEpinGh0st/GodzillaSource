package javassist.bytecode.stackmap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;






















public abstract class TypeData
{
  public static TypeData[] make(int size) {
    TypeData[] array = new TypeData[size];
    for (int i = 0; i < size; i++) {
      array[i] = TypeTag.TOP;
    }
    return array;
  }










  
  private static void setType(TypeData td, String className, ClassPool cp) throws BadBytecode {
    td.setType(className, cp);
  }
  public abstract int getTypeTag();
  public abstract int getTypeData(ConstPool paramConstPool);
  
  public TypeData join() {
    return new TypeVar(this);
  }


  
  public abstract BasicType isBasicType();


  
  public abstract boolean is2WordType();

  
  public boolean isNullType() {
    return false;
  } public boolean isUninit() {
    return false;
  }


  
  public abstract boolean eq(TypeData paramTypeData);


  
  public abstract String getName();


  
  public abstract void setType(String paramString, ClassPool paramClassPool) throws BadBytecode;


  
  public abstract TypeData getArrayType(int paramInt) throws NotFoundException;

  
  public int dfs(List<TypeData> order, int index, ClassPool cp) throws NotFoundException {
    return index;
  }






  
  protected TypeVar toTypeVar(int dim) {
    return null;
  }

  
  public void constructorCalled(int offset) {}
  
  public String toString() {
    return super.toString() + "(" + toString2(new HashSet<>()) + ")";
  }

  
  abstract String toString2(Set<TypeData> paramSet);
  
  protected static class BasicType
    extends TypeData
  {
    private String name;
    private int typeTag;
    private char decodedName;
    
    public BasicType(String type, int tag, char decoded) {
      this.name = type;
      this.typeTag = tag;
      this.decodedName = decoded;
    }
    
    public int getTypeTag() {
      return this.typeTag;
    } public int getTypeData(ConstPool cp) {
      return 0;
    }
    
    public TypeData join() {
      if (this == TypeTag.TOP)
        return this; 
      return super.join();
    }
    
    public BasicType isBasicType() {
      return this;
    }
    
    public boolean is2WordType() {
      return (this.typeTag == 4 || this.typeTag == 3);
    }

    
    public boolean eq(TypeData d) {
      return (this == d);
    }
    
    public String getName() {
      return this.name;
    }
    public char getDecodedName() {
      return this.decodedName;
    }
    
    public void setType(String s, ClassPool cp) throws BadBytecode {
      throw new BadBytecode("conflict: " + this.name + " and " + s);
    }




    
    public TypeData getArrayType(int dim) throws NotFoundException {
      if (this == TypeTag.TOP)
        return this; 
      if (dim < 0)
        throw new NotFoundException("no element type: " + this.name); 
      if (dim == 0) {
        return this;
      }
      char[] name = new char[dim + 1];
      for (int i = 0; i < dim; i++) {
        name[i] = '[';
      }
      name[dim] = this.decodedName;
      return new TypeData.ClassName(new String(name));
    }

    
    String toString2(Set<TypeData> set) {
      return this.name;
    }
  }
  
  public static abstract class AbsTypeVar extends TypeData {
    public abstract void merge(TypeData param1TypeData);
    
    public int getTypeTag() {
      return 7;
    }
    
    public int getTypeData(ConstPool cp) {
      return cp.addClassInfo(getName());
    }

    
    public boolean eq(TypeData d) {
      if (d.isUninit()) {
        return d.eq(this);
      }
      return getName().equals(d.getName());
    }
  }




  
  public static class TypeVar
    extends AbsTypeVar
  {
    protected List<TypeData> lowers;



    
    protected List<TypeData> usedBy;



    
    protected List<String> uppers;



    
    protected String fixedType;



    
    private boolean is2WordType;



    
    private int visited;



    
    private int smallest;



    
    private boolean inList;



    
    private int dimension;




    
    public String getName() {
      if (this.fixedType == null) {
        return ((TypeData)this.lowers.get(0)).getName();
      }
      return this.fixedType;
    }




    
    public TypeData.BasicType isBasicType() {
      if (this.fixedType == null) {
        return ((TypeData)this.lowers.get(0)).isBasicType();
      }
      return null;
    }




    
    public boolean is2WordType() {
      if (this.fixedType == null) {
        return this.is2WordType;
      }
      return false;
    }




    
    public TypeVar(TypeData t) {
      this.visited = 0;
      this.smallest = 0;
      this.inList = false;
      this.dimension = 0; this.uppers = null; this.lowers = new ArrayList<>(2); this.usedBy = new ArrayList<>(2); merge(t); this.fixedType = null;
      this.is2WordType = t.is2WordType();
    } public boolean isNullType() { if (this.fixedType == null)
        return ((TypeData)this.lowers.get(0)).isNullType(); 
      return false; } protected TypeVar toTypeVar(int dim) { this.dimension = dim;
      return this; } public boolean isUninit() { if (this.fixedType == null)
        return ((TypeData)this.lowers.get(0)).isUninit(); 
      return false; }
    public void merge(TypeData t) {
      this.lowers.add(t);
      if (t instanceof TypeVar)
        ((TypeVar)t).usedBy.add(this); 
    }
    public TypeData getArrayType(int dim) throws NotFoundException { if (dim == 0)
        return this; 
      TypeData.BasicType bt = isBasicType();
      if (bt == null) {
        if (isNullType()) {
          return new TypeData.NullType();
        }
        return (new TypeData.ClassName(getName())).getArrayType(dim);
      }  return bt.getArrayType(dim); }
    
    public int getTypeTag() {
      if (this.fixedType == null)
        return ((TypeData)this.lowers.get(0)).getTypeTag(); 
      return super.getTypeTag();
    } public int dfs(List<TypeData> preOrder, int index, ClassPool cp) throws NotFoundException { if (this.visited > 0) {
        return index;
      }
      this.visited = this.smallest = ++index;
      preOrder.add(this);
      this.inList = true;
      int n = this.lowers.size();
      for (int i = 0; i < n; i++) {
        TypeVar child = ((TypeData)this.lowers.get(i)).toTypeVar(this.dimension);
        if (child != null)
          if (child.visited == 0) {
            index = child.dfs(preOrder, index, cp);
            if (child.smallest < this.smallest) {
              this.smallest = child.smallest;
            }
          } else if (child.inList && 
            child.visited < this.smallest) {
            this.smallest = child.visited;
          }  
      } 
      if (this.visited == this.smallest) {
        List<TypeData> scc = new ArrayList<>();
        
        while (true) {
          TypeVar cv = (TypeVar)preOrder.remove(preOrder.size() - 1);
          cv.inList = false;
          scc.add(cv);
          if (cv == this) {
            fixTypes(scc, cp); break;
          } 
        } 
      }  return index; }
    public int getTypeData(ConstPool cp) { if (this.fixedType == null)
        return ((TypeData)this.lowers.get(0)).getTypeData(cp);  return super.getTypeData(cp); }
    public void setType(String typeName, ClassPool cp) throws BadBytecode { if (this.uppers == null)
        this.uppers = new ArrayList<>();  this.uppers.add(typeName); } private void fixTypes(List<TypeData> scc, ClassPool cp) throws NotFoundException { Set<String> lowersSet = new HashSet<>();
      boolean isBasicType = false;
      TypeData kind = null;
      int size = scc.size();
      for (int i = 0; i < size; i++) {
        TypeVar tvar = (TypeVar)scc.get(i);
        List<TypeData> tds = tvar.lowers;
        int size2 = tds.size();
        for (int j = 0; j < size2; j++) {
          TypeData td = tds.get(j);
          TypeData d = td.getArrayType(tvar.dimension);
          TypeData.BasicType bt = d.isBasicType();
          if (kind == null) {
            if (bt == null) {
              isBasicType = false;
              kind = d;



              
              if (d.isUninit()) {
                break;
              }
            } else {
              isBasicType = true;
              kind = bt;
            }
          
          }
          else if ((bt == null && isBasicType) || (bt != null && kind != bt)) {
            isBasicType = true;
            kind = TypeTag.TOP;
            
            break;
          } 
          
          if (bt == null && !d.isNullType()) {
            lowersSet.add(d.getName());
          }
        } 
      } 
      if (isBasicType) {
        this.is2WordType = kind.is2WordType();
        fixTypes1(scc, kind);
      } else {
        
        String typeName = fixTypes2(scc, lowersSet, cp);
        fixTypes1(scc, new TypeData.ClassName(typeName));
      }  }

    
    private void fixTypes1(List<TypeData> scc, TypeData kind) throws NotFoundException {
      int size = scc.size();
      for (int i = 0; i < size; i++) {
        TypeVar cv = (TypeVar)scc.get(i);
        TypeData kind2 = kind.getArrayType(-cv.dimension);
        if (kind2.isBasicType() == null) {
          cv.fixedType = kind2.getName();
        } else {
          cv.lowers.clear();
          cv.lowers.add(kind2);
          cv.is2WordType = kind2.is2WordType();
        } 
      } 
    }
    
    private String fixTypes2(List<TypeData> scc, Set<String> lowersSet, ClassPool cp) throws NotFoundException {
      Iterator<String> it = lowersSet.iterator();
      if (lowersSet.size() == 0)
        return null; 
      if (lowersSet.size() == 1) {
        return it.next();
      }
      CtClass cc = cp.get(it.next());
      while (it.hasNext()) {
        cc = commonSuperClassEx(cc, cp.get(it.next()));
      }
      if (cc.getSuperclass() == null || isObjectArray(cc)) {
        cc = fixByUppers(scc, cp, new HashSet<>(), cc);
      }
      if (cc.isArray()) {
        return Descriptor.toJvmName(cc);
      }
      return cc.getName();
    }

    
    private static boolean isObjectArray(CtClass cc) throws NotFoundException {
      return (cc.isArray() && cc.getComponentType().getSuperclass() == null);
    }


    
    private CtClass fixByUppers(List<TypeData> users, ClassPool cp, Set<TypeData> visited, CtClass type) throws NotFoundException {
      if (users == null) {
        return type;
      }
      int size = users.size();
      for (int i = 0; i < size; i++) {
        TypeVar t = (TypeVar)users.get(i);
        if (!visited.add(t)) {
          return type;
        }
        if (t.uppers != null) {
          int s = t.uppers.size();
          for (int k = 0; k < s; k++) {
            CtClass cc = cp.get(t.uppers.get(k));
            if (cc.subtypeOf(type)) {
              type = cc;
            }
          } 
        } 
        type = fixByUppers(t.usedBy, cp, visited, type);
      } 
      
      return type;
    }

    
    String toString2(Set<TypeData> hash) {
      hash.add(this);
      if (this.lowers.size() > 0) {
        TypeData e = this.lowers.get(0);
        if (e != null && !hash.contains(e)) {
          return e.toString2(hash);
        }
      } 
      return "?";
    }
  }




  
  public static CtClass commonSuperClassEx(CtClass one, CtClass two) throws NotFoundException {
    if (one == two)
      return one; 
    if (one.isArray() && two.isArray()) {
      CtClass ele1 = one.getComponentType();
      CtClass ele2 = two.getComponentType();
      CtClass element = commonSuperClassEx(ele1, ele2);
      if (element == ele1)
        return one; 
      if (element == ele2) {
        return two;
      }
      return one.getClassPool().get((element == null) ? "java.lang.Object" : (
          element.getName() + "[]"));
    } 
    if (one.isPrimitive() || two.isPrimitive())
      return null; 
    if (one.isArray() || two.isArray()) {
      return one.getClassPool().get("java.lang.Object");
    }
    return commonSuperClass(one, two);
  }




  
  public static CtClass commonSuperClass(CtClass one, CtClass two) throws NotFoundException {
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
  
  static boolean eq(CtClass one, CtClass two) {
    return (one == two || (one != null && two != null && one.getName().equals(two.getName())));
  }
  
  public static void aastore(TypeData array, TypeData value, ClassPool cp) throws BadBytecode {
    if (array instanceof AbsTypeVar && 
      !value.isNullType()) {
      ((AbsTypeVar)array).merge(ArrayType.make(value));
    }
    if (value instanceof AbsTypeVar)
      if (array instanceof AbsTypeVar) {
        ArrayElement.make(array);
      } else if (array instanceof ClassName) {
        if (!array.isNullType()) {
          String type = ArrayElement.typeName(array.getName());
          value.setType(type, cp);
        } 
      } else {
        
        throw new BadBytecode("bad AASTORE: " + array);
      }  
  }
  
  public static class ArrayType
    extends AbsTypeVar
  {
    private TypeData.AbsTypeVar element;
    
    private ArrayType(TypeData.AbsTypeVar elementType) {
      this.element = elementType;
    }
    
    static TypeData make(TypeData element) throws BadBytecode {
      if (element instanceof TypeData.ArrayElement)
        return ((TypeData.ArrayElement)element).arrayType(); 
      if (element instanceof TypeData.AbsTypeVar)
        return new ArrayType((TypeData.AbsTypeVar)element); 
      if (element instanceof TypeData.ClassName && 
        !element.isNullType()) {
        return new TypeData.ClassName(typeName(element.getName()));
      }
      throw new BadBytecode("bad AASTORE: " + element);
    }

    
    public void merge(TypeData t) {
      try {
        if (!t.isNullType()) {
          this.element.merge(TypeData.ArrayElement.make(t));
        }
      } catch (BadBytecode e) {
        
        throw new RuntimeException("fatal: " + e);
      } 
    }

    
    public String getName() {
      return typeName(this.element.getName());
    }
    public TypeData.AbsTypeVar elementType() {
      return this.element;
    }
    public TypeData.BasicType isBasicType() {
      return null;
    } public boolean is2WordType() {
      return false;
    }


    
    public static String typeName(String elementType) {
      if (elementType.charAt(0) == '[')
        return "[" + elementType; 
      return "[L" + elementType.replace('.', '/') + ";";
    }

    
    public void setType(String s, ClassPool cp) throws BadBytecode {
      this.element.setType(TypeData.ArrayElement.typeName(s), cp);
    }
    
    protected TypeData.TypeVar toTypeVar(int dim) {
      return this.element.toTypeVar(dim + 1);
    }
    
    public TypeData getArrayType(int dim) throws NotFoundException {
      return this.element.getArrayType(dim + 1);
    }

    
    public int dfs(List<TypeData> order, int index, ClassPool cp) throws NotFoundException {
      return this.element.dfs(order, index, cp);
    }

    
    String toString2(Set<TypeData> set) {
      return "[" + this.element.toString2(set);
    }
  }

  
  public static class ArrayElement
    extends AbsTypeVar
  {
    private TypeData.AbsTypeVar array;
    
    private ArrayElement(TypeData.AbsTypeVar a) {
      this.array = a;
    }
    
    public static TypeData make(TypeData array) throws BadBytecode {
      if (array instanceof TypeData.ArrayType)
        return ((TypeData.ArrayType)array).elementType(); 
      if (array instanceof TypeData.AbsTypeVar)
        return new ArrayElement((TypeData.AbsTypeVar)array); 
      if (array instanceof TypeData.ClassName && 
        !array.isNullType()) {
        return new TypeData.ClassName(typeName(array.getName()));
      }
      throw new BadBytecode("bad AASTORE: " + array);
    }

    
    public void merge(TypeData t) {
      try {
        if (!t.isNullType()) {
          this.array.merge(TypeData.ArrayType.make(t));
        }
      } catch (BadBytecode e) {
        
        throw new RuntimeException("fatal: " + e);
      } 
    }

    
    public String getName() {
      return typeName(this.array.getName());
    }
    public TypeData.AbsTypeVar arrayType() {
      return this.array;
    }



    
    public TypeData.BasicType isBasicType() {
      return null;
    }
    public boolean is2WordType() {
      return false;
    }
    private static String typeName(String arrayType) {
      if (arrayType.length() > 1 && arrayType.charAt(0) == '[') {
        char c = arrayType.charAt(1);
        if (c == 'L')
          return arrayType.substring(2, arrayType.length() - 1).replace('/', '.'); 
        if (c == '[') {
          return arrayType.substring(1);
        }
      } 
      return "java.lang.Object";
    }

    
    public void setType(String s, ClassPool cp) throws BadBytecode {
      this.array.setType(TypeData.ArrayType.typeName(s), cp);
    }
    
    protected TypeData.TypeVar toTypeVar(int dim) {
      return this.array.toTypeVar(dim - 1);
    }
    
    public TypeData getArrayType(int dim) throws NotFoundException {
      return this.array.getArrayType(dim - 1);
    }

    
    public int dfs(List<TypeData> order, int index, ClassPool cp) throws NotFoundException {
      return this.array.dfs(order, index, cp);
    }

    
    String toString2(Set<TypeData> set) {
      return "*" + this.array.toString2(set);
    } }
  
  public static class UninitTypeVar extends AbsTypeVar {
    protected TypeData type;
    
    public UninitTypeVar(TypeData.UninitData t) {
      this.type = t;
    } public int getTypeTag() {
      return this.type.getTypeTag();
    } public int getTypeData(ConstPool cp) {
      return this.type.getTypeData(cp);
    } public TypeData.BasicType isBasicType() {
      return this.type.isBasicType();
    } public boolean is2WordType() {
      return this.type.is2WordType();
    } public boolean isUninit() {
      return this.type.isUninit();
    } public boolean eq(TypeData d) {
      return this.type.eq(d);
    } public String getName() {
      return this.type.getName();
    }
    protected TypeData.TypeVar toTypeVar(int dim) {
      return null;
    } public TypeData join() {
      return this.type.join();
    }
    
    public void setType(String s, ClassPool cp) throws BadBytecode {
      this.type.setType(s, cp);
    }

    
    public void merge(TypeData t) {
      if (!t.eq(this.type)) {
        this.type = TypeTag.TOP;
      }
    }
    
    public void constructorCalled(int offset) {
      this.type.constructorCalled(offset);
    }
    
    public int offset() {
      if (this.type instanceof TypeData.UninitData)
        return ((TypeData.UninitData)this.type).offset; 
      throw new RuntimeException("not available");
    }

    
    public TypeData getArrayType(int dim) throws NotFoundException {
      return this.type.getArrayType(dim);
    }
    
    String toString2(Set<TypeData> set) {
      return "";
    }
  }
  
  public static class ClassName
    extends TypeData
  {
    private String name;
    
    public ClassName(String n) {
      this.name = n;
    }

    
    public String getName() {
      return this.name;
    }
    
    public TypeData.BasicType isBasicType() {
      return null;
    }
    public boolean is2WordType() {
      return false;
    }
    public int getTypeTag() {
      return 7;
    }
    
    public int getTypeData(ConstPool cp) {
      return cp.addClassInfo(getName());
    }

    
    public boolean eq(TypeData d) {
      if (d.isUninit()) {
        return d.eq(this);
      }
      return this.name.equals(d.getName());
    }

    
    public void setType(String typeName, ClassPool cp) throws BadBytecode {}

    
    public TypeData getArrayType(int dim) throws NotFoundException {
      if (dim == 0)
        return this; 
      if (dim > 0) {
        char[] dimType = new char[dim];
        for (int j = 0; j < dim; j++) {
          dimType[j] = '[';
        }
        String elementType = getName();
        if (elementType.charAt(0) != '[') {
          elementType = "L" + elementType.replace('.', '/') + ";";
        }
        return new ClassName(new String(dimType) + elementType);
      } 
      
      for (int i = 0; i < -dim; i++) {
        if (this.name.charAt(i) != '[')
          throw new NotFoundException("no " + dim + " dimensional array type: " + getName()); 
      } 
      char type = this.name.charAt(-dim);
      if (type == '[')
        return new ClassName(this.name.substring(-dim)); 
      if (type == 'L')
        return new ClassName(this.name.substring(-dim + 1, this.name.length() - 1).replace('/', '.')); 
      if (type == TypeTag.DOUBLE.decodedName)
        return TypeTag.DOUBLE; 
      if (type == TypeTag.FLOAT.decodedName)
        return TypeTag.FLOAT; 
      if (type == TypeTag.LONG.decodedName) {
        return TypeTag.LONG;
      }
      return TypeTag.INTEGER;
    }


    
    String toString2(Set<TypeData> set) {
      return this.name;
    }
  }



  
  public static class NullType
    extends ClassName
  {
    public NullType() {
      super("null-type");
    }

    
    public int getTypeTag() {
      return 5;
    }
    
    public boolean isNullType() {
      return true;
    } public int getTypeData(ConstPool cp) {
      return 0;
    }
    public TypeData getArrayType(int dim) {
      return this;
    }
  }
  
  public static class UninitData
    extends ClassName
  {
    int offset;
    boolean initialized;
    
    UninitData(int offset, String className) {
      super(className);
      this.offset = offset;
      this.initialized = false;
    }
    public UninitData copy() {
      return new UninitData(this.offset, getName());
    }
    
    public int getTypeTag() {
      return 8;
    }

    
    public int getTypeData(ConstPool cp) {
      return this.offset;
    }

    
    public TypeData join() {
      if (this.initialized)
        return new TypeData.TypeVar(new TypeData.ClassName(getName())); 
      return new TypeData.UninitTypeVar(copy());
    }
    
    public boolean isUninit() {
      return true;
    }
    
    public boolean eq(TypeData d) {
      if (d instanceof UninitData) {
        UninitData ud = (UninitData)d;
        return (this.offset == ud.offset && getName().equals(ud.getName()));
      } 
      return false;
    }
    public int offset() {
      return this.offset;
    }
    
    public void constructorCalled(int offset) {
      if (offset == this.offset)
        this.initialized = true; 
    }
    
    String toString2(Set<TypeData> set) {
      return getName() + "," + this.offset;
    } }
  
  public static class UninitThis extends UninitData {
    UninitThis(String className) {
      super(-1, className);
    }
    
    public TypeData.UninitData copy() {
      return new UninitThis(getName());
    }
    
    public int getTypeTag() {
      return 6;
    }

    
    public int getTypeData(ConstPool cp) {
      return 0;
    }
    
    String toString2(Set<TypeData> set) {
      return "uninit:this";
    }
  }
}
