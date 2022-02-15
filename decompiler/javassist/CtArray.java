package javassist;
















final class CtArray
  extends CtClass
{
  protected ClassPool pool;
  private CtClass[] interfaces;
  
  public ClassPool getClassPool() {
    return this.pool;
  }
  
  CtArray(String name, ClassPool cp) {
    super(name);














    
    this.interfaces = null;
    this.pool = cp;
  }
  
  public int getModifiers() {
    int mod = 16;
    try {
      mod |= getComponentType().getModifiers() & 0x7;
    
    }
    catch (NotFoundException notFoundException) {}
    return mod;
  }


  
  public CtClass[] getInterfaces() throws NotFoundException {
    if (this.interfaces == null) {
      Class<?>[] intfs = Object[].class.getInterfaces();

      
      this.interfaces = new CtClass[intfs.length];
      for (int i = 0; i < intfs.length; i++) {
        this.interfaces[i] = this.pool.get(intfs[i].getName());
      }
    } 
    return this.interfaces;
  }
  public boolean isArray() {
    return true;
  }
  public boolean subtypeOf(CtClass clazz) throws NotFoundException {
    if (super.subtypeOf(clazz)) {
      return true;
    }
    String cname = clazz.getName();
    if (cname.equals("java.lang.Object")) {
      return true;
    }
    CtClass[] intfs = getInterfaces();
    for (int i = 0; i < intfs.length; i++) {
      if (intfs[i].subtypeOf(clazz))
        return true; 
    } 
    return (clazz.isArray() && 
      getComponentType().subtypeOf(clazz.getComponentType()));
  }


  
  public CtClass getComponentType() throws NotFoundException {
    String name = getName();
    return this.pool.get(name.substring(0, name.length() - 2));
  }


  
  public CtClass getSuperclass() throws NotFoundException {
    return this.pool.get("java.lang.Object");
  }


  
  public CtMethod[] getMethods() {
    try {
      return getSuperclass().getMethods();
    }
    catch (NotFoundException e) {
      return super.getMethods();
    } 
  }



  
  public CtMethod getMethod(String name, String desc) throws NotFoundException {
    return getSuperclass().getMethod(name, desc);
  }


  
  public CtConstructor[] getConstructors() {
    try {
      return getSuperclass().getConstructors();
    }
    catch (NotFoundException e) {
      return super.getConstructors();
    } 
  }
}
