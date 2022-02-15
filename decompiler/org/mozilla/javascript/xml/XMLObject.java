package org.mozilla.javascript.xml;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.NativeWith;
import org.mozilla.javascript.Ref;
import org.mozilla.javascript.Scriptable;










public abstract class XMLObject
  extends IdScriptableObject
{
  static final long serialVersionUID = 8455156490438576500L;
  
  public XMLObject() {}
  
  public XMLObject(Scriptable scope, Scriptable prototype) {
    super(scope, prototype);
  }




  
  public abstract boolean has(Context paramContext, Object paramObject);




  
  public abstract Object get(Context paramContext, Object paramObject);




  
  public abstract void put(Context paramContext, Object paramObject1, Object paramObject2);




  
  public abstract boolean delete(Context paramContext, Object paramObject);




  
  public abstract Object getFunctionProperty(Context paramContext, String paramString);




  
  public abstract Object getFunctionProperty(Context paramContext, int paramInt);




  
  public abstract Scriptable getExtraMethodSource(Context paramContext);




  
  public abstract Ref memberRef(Context paramContext, Object paramObject, int paramInt);




  
  public abstract Ref memberRef(Context paramContext, Object paramObject1, Object paramObject2, int paramInt);




  
  public abstract NativeWith enterWith(Scriptable paramScriptable);



  
  public abstract NativeWith enterDotQuery(Scriptable paramScriptable);



  
  public Object addValues(Context cx, boolean thisIsLeft, Object value) {
    return Scriptable.NOT_FOUND;
  }







  
  public String getTypeOf() {
    return avoidObjectDetection() ? "undefined" : "xml";
  }
}
