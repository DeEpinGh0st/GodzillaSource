package javassist;


















public abstract class CtMember
{
  CtMember next;
  protected CtClass declaringClass;
  
  static class Cache
    extends CtMember
  {
    private CtMember methodTail;
    private CtMember consTail;
    private CtMember fieldTail;
    
    protected void extendToString(StringBuffer buffer) {}
    
    public boolean hasAnnotation(String clz) {
      return false;
    }
    public Object getAnnotation(Class<?> clz) throws ClassNotFoundException {
      return null;
    }
    public Object[] getAnnotations() throws ClassNotFoundException {
      return null;
    } public byte[] getAttribute(String name) {
      return null;
    } public Object[] getAvailableAnnotations() {
      return null;
    } public int getModifiers() {
      return 0;
    } public String getName() {
      return null;
    } public String getSignature() {
      return null;
    }
    public void setAttribute(String name, byte[] data) {}
    public void setModifiers(int mod) {}
    
    public String getGenericSignature() {
      return null;
    }


    
    public void setGenericSignature(String sig) {}

    
    Cache(CtClassType decl) {
      super(decl);
      this.methodTail = this;
      this.consTail = this;
      this.fieldTail = this;
      this.fieldTail.next = this;
    }
    
    CtMember methodHead() { return this; }
    CtMember lastMethod() { return this.methodTail; }
    CtMember consHead() { return this.methodTail; }
    CtMember lastCons() { return this.consTail; }
    CtMember fieldHead() { return this.consTail; } CtMember lastField() {
      return this.fieldTail;
    }
    void addMethod(CtMember method) {
      method.next = this.methodTail.next;
      this.methodTail.next = method;
      if (this.methodTail == this.consTail) {
        this.consTail = method;
        if (this.methodTail == this.fieldTail) {
          this.fieldTail = method;
        }
      } 
      this.methodTail = method;
    }


    
    void addConstructor(CtMember cons) {
      cons.next = this.consTail.next;
      this.consTail.next = cons;
      if (this.consTail == this.fieldTail) {
        this.fieldTail = cons;
      }
      this.consTail = cons;
    }
    
    void addField(CtMember field) {
      field.next = this;
      this.fieldTail.next = field;
      this.fieldTail = field;
    }
    
    static int count(CtMember head, CtMember tail) {
      int n = 0;
      while (head != tail) {
        n++;
        head = head.next;
      } 
      
      return n;
    }
    
    void remove(CtMember mem) {
      CtMember m = this;
      CtMember node;
      while ((node = m.next) != this) {
        if (node == mem) {
          m.next = node.next;
          if (node == this.methodTail) {
            this.methodTail = m;
          }
          if (node == this.consTail) {
            this.consTail = m;
          }
          if (node == this.fieldTail) {
            this.fieldTail = m;
          }
          break;
        } 
        m = m.next;
      } 
    }
  }
  
  protected CtMember(CtClass clazz) {
    this.declaringClass = clazz;
    this.next = null;
  }
  final CtMember next() {
    return this.next;
  }



  
  void nameReplaced() {}



  
  public String toString() {
    StringBuffer buffer = new StringBuffer(getClass().getName());
    buffer.append("@");
    buffer.append(Integer.toHexString(hashCode()));
    buffer.append("[");
    buffer.append(Modifier.toString(getModifiers()));
    extendToString(buffer);
    buffer.append("]");
    return buffer.toString();
  }





  
  protected abstract void extendToString(StringBuffer paramStringBuffer);




  
  public CtClass getDeclaringClass() {
    return this.declaringClass;
  }

  
  public boolean visibleFrom(CtClass clazz) {
    boolean visible;
    int mod = getModifiers();
    if (Modifier.isPublic(mod))
      return true; 
    if (Modifier.isPrivate(mod)) {
      return (clazz == this.declaringClass);
    }
    String declName = this.declaringClass.getPackageName();
    String fromName = clazz.getPackageName();
    
    if (declName == null) {
      visible = (fromName == null);
    } else {
      visible = declName.equals(fromName);
    } 
    if (!visible && Modifier.isProtected(mod)) {
      return clazz.subclassOf(this.declaringClass);
    }
    return visible;
  }







  
  public abstract int getModifiers();







  
  public abstract void setModifiers(int paramInt);






  
  public boolean hasAnnotation(Class<?> clz) {
    return hasAnnotation(clz.getName());
  }
  
  public abstract boolean hasAnnotation(String paramString);
  
  public abstract Object getAnnotation(Class<?> paramClass) throws ClassNotFoundException;
  
  public abstract Object[] getAnnotations() throws ClassNotFoundException;
  
  public abstract Object[] getAvailableAnnotations();
  
  public abstract String getName();
  
  public abstract String getSignature();
  
  public abstract String getGenericSignature();
  
  public abstract void setGenericSignature(String paramString);
  
  public abstract byte[] getAttribute(String paramString);
  
  public abstract void setAttribute(String paramString, byte[] paramArrayOfbyte);
}
