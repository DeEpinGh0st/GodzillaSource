package org.mozilla.javascript;










































public class WrapFactory
{
  public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
    if (obj == null || obj == Undefined.instance || obj instanceof Scriptable)
    {
      
      return obj;
    }
    if (staticType != null && staticType.isPrimitive()) {
      if (staticType == void.class)
        return Undefined.instance; 
      if (staticType == char.class)
        return Integer.valueOf(((Character)obj).charValue()); 
      return obj;
    } 
    if (!isJavaPrimitiveWrap()) {
      if (obj instanceof String || obj instanceof Number || obj instanceof Boolean)
      {
        
        return obj; } 
      if (obj instanceof Character) {
        return String.valueOf(((Character)obj).charValue());
      }
    } 
    Class<?> cls = obj.getClass();
    if (cls.isArray()) {
      return NativeJavaArray.wrap(scope, obj);
    }
    return wrapAsJavaObject(cx, scope, obj, staticType);
  }








  
  public Scriptable wrapNewObject(Context cx, Scriptable scope, Object obj) {
    if (obj instanceof Scriptable) {
      return (Scriptable)obj;
    }
    Class<?> cls = obj.getClass();
    if (cls.isArray()) {
      return NativeJavaArray.wrap(scope, obj);
    }
    return wrapAsJavaObject(cx, scope, obj, null);
  }




















  
  public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
    return new NativeJavaObject(scope, javaObject, staticType);
  }















  
  public Scriptable wrapJavaClass(Context cx, Scriptable scope, Class<?> javaClass) {
    return new NativeJavaClass(scope, javaClass);
  }












  
  public final boolean isJavaPrimitiveWrap() {
    return this.javaPrimitiveWrap;
  }




  
  public final void setJavaPrimitiveWrap(boolean value) {
    Context cx = Context.getCurrentContext();
    if (cx != null && cx.isSealed()) {
      Context.onSealedMutation();
    }
    this.javaPrimitiveWrap = value;
  }
  
  private boolean javaPrimitiveWrap = true;
}
