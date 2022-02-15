package javassist.bytecode.analysis;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;



















public class MultiArrayType
  extends Type
{
  private MultiType component;
  private int dims;
  
  public MultiArrayType(MultiType component, int dims) {
    super(null);
    this.component = component;
    this.dims = dims;
  }

  
  public CtClass getCtClass() {
    CtClass clazz = this.component.getCtClass();
    if (clazz == null) {
      return null;
    }
    ClassPool pool = clazz.getClassPool();
    if (pool == null) {
      pool = ClassPool.getDefault();
    }
    String name = arrayName(clazz.getName(), this.dims);
    
    try {
      return pool.get(name);
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    } 
  }

  
  boolean popChanged() {
    return this.component.popChanged();
  }

  
  public int getDimensions() {
    return this.dims;
  }

  
  public Type getComponent() {
    return (this.dims == 1) ? this.component : new MultiArrayType(this.component, this.dims - 1);
  }

  
  public int getSize() {
    return 1;
  }

  
  public boolean isArray() {
    return true;
  }

  
  public boolean isAssignableFrom(Type type) {
    throw new UnsupportedOperationException("Not implemented");
  }

  
  public boolean isReference() {
    return true;
  }
  
  public boolean isAssignableTo(Type type) {
    if (eq(type.getCtClass(), Type.OBJECT.getCtClass())) {
      return true;
    }
    if (eq(type.getCtClass(), Type.CLONEABLE.getCtClass())) {
      return true;
    }
    if (eq(type.getCtClass(), Type.SERIALIZABLE.getCtClass())) {
      return true;
    }
    if (!type.isArray()) {
      return false;
    }
    Type typeRoot = getRootComponent(type);
    int typeDims = type.getDimensions();
    
    if (typeDims > this.dims) {
      return false;
    }
    if (typeDims < this.dims) {
      if (eq(typeRoot.getCtClass(), Type.OBJECT.getCtClass())) {
        return true;
      }
      if (eq(typeRoot.getCtClass(), Type.CLONEABLE.getCtClass())) {
        return true;
      }
      if (eq(typeRoot.getCtClass(), Type.SERIALIZABLE.getCtClass())) {
        return true;
      }
      return false;
    } 
    
    return this.component.isAssignableTo(typeRoot);
  }


  
  public int hashCode() {
    return this.component.hashCode() + this.dims;
  }

  
  public boolean equals(Object o) {
    if (!(o instanceof MultiArrayType))
      return false; 
    MultiArrayType multi = (MultiArrayType)o;
    
    return (this.component.equals(multi.component) && this.dims == multi.dims);
  }


  
  public String toString() {
    return arrayName(this.component.toString(), this.dims);
  }
}
