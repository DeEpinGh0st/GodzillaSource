package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Set;

















































































































































































public class NativeJavaPackage
  extends ScriptableObject
{
  static final long serialVersionUID = 7445054382212031523L;
  private String packageName;
  private transient ClassLoader classLoader;
  private Set<String> negativeCache;
  
  NativeJavaPackage(boolean internalUsage, String packageName, ClassLoader classLoader) {
    this.negativeCache = null;
    this.packageName = packageName;
    this.classLoader = classLoader;
  }
  
  @Deprecated
  public NativeJavaPackage(String packageName, ClassLoader classLoader) {
    this(false, packageName, classLoader);
  }
  
  @Deprecated
  public NativeJavaPackage(String packageName) {
    this(false, packageName, Context.getCurrentContext().getApplicationClassLoader());
  }
  
  public String getClassName() {
    return "JavaPackage";
  }
  
  public boolean has(String id, Scriptable start) {
    return true;
  }
  
  public boolean has(int index, Scriptable start) {
    return false;
  }
  
  public void put(String id, Scriptable start, Object value) {}
  
  public void put(int index, Scriptable start, Object value) {
    throw Context.reportRuntimeError0("msg.pkg.int");
  }
  
  public Object get(String id, Scriptable start) {
    return getPkgProperty(id, start, true);
  }
  
  public Object get(int index, Scriptable start) {
    return NOT_FOUND;
  }
  
  NativeJavaPackage forcePackage(String name, Scriptable scope) {
    Object cached = super.get(name, this);
    if (cached != null && cached instanceof NativeJavaPackage)
      return (NativeJavaPackage)cached; 
    String newPackage = (this.packageName.length() == 0) ? name : (this.packageName + "." + name);
    NativeJavaPackage pkg = new NativeJavaPackage(true, newPackage, this.classLoader);
    ScriptRuntime.setObjectProtoAndParent(pkg, scope);
    super.put(name, this, pkg);
    return pkg;
  }
  
  synchronized Object getPkgProperty(String name, Scriptable start, boolean createPkg) {
    Object cached = super.get(name, start);
    if (cached != NOT_FOUND)
      return cached; 
    if (this.negativeCache != null && this.negativeCache.contains(name))
      return null; 
    String className = (this.packageName.length() == 0) ? name : (this.packageName + '.' + name);
    Context cx = Context.getContext();
    ClassShutter shutter = cx.getClassShutter();
    Scriptable newValue = null;
    if (shutter == null || shutter.visibleToScripts(className)) {
      Class<?> cl = null;
      if (this.classLoader != null) {
        cl = Kit.classOrNull(this.classLoader, className);
      } else {
        cl = Kit.classOrNull(className);
      } 
      if (cl != null) {
        WrapFactory wrapFactory = cx.getWrapFactory();
        newValue = wrapFactory.wrapJavaClass(cx, getTopLevelScope(this), cl);
        newValue.setPrototype(getPrototype());
      } 
    } 
    if (newValue == null)
      if (createPkg) {
        NativeJavaPackage pkg = new NativeJavaPackage(true, className, this.classLoader);
        ScriptRuntime.setObjectProtoAndParent(pkg, getParentScope());
        newValue = pkg;
      } else {
        if (this.negativeCache == null)
          this.negativeCache = new HashSet<String>(); 
        this.negativeCache.add(name);
      }  
    if (newValue != null)
      super.put(name, start, newValue); 
    return newValue;
  }
  
  public Object getDefaultValue(Class<?> ignored) {
    return toString();
  }
  
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    this.classLoader = Context.getCurrentContext().getApplicationClassLoader();
  }
  
  public String toString() {
    return "[JavaPackage " + this.packageName + "]";
  }
  
  public boolean equals(Object obj) {
    if (obj instanceof NativeJavaPackage) {
      NativeJavaPackage njp = (NativeJavaPackage)obj;
      return (this.packageName.equals(njp.packageName) && this.classLoader == njp.classLoader);
    } 
    return false;
  }
  
  public int hashCode() {
    return this.packageName.hashCode() ^ ((this.classLoader == null) ? 0 : this.classLoader.hashCode());
  }
}
