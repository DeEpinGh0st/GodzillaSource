package org.mozilla.javascript;



















public class JavaScriptException
  extends RhinoException
{
  static final long serialVersionUID = -7666130513694669293L;
  private Object value;
  
  @Deprecated
  public JavaScriptException(Object value) {
    this(value, "", 0);
  }






  
  public JavaScriptException(Object value, String sourceName, int lineNumber) {
    recordErrorOrigin(sourceName, lineNumber, null, 0);
    this.value = value;

    
    if (value instanceof NativeError && Context.getContext().hasFeature(10)) {
      
      NativeError error = (NativeError)value;
      if (!error.has("fileName", error)) {
        error.put("fileName", error, sourceName);
      }
      if (!error.has("lineNumber", error)) {
        error.put("lineNumber", error, Integer.valueOf(lineNumber));
      }
      
      error.setStackProvider(this);
    } 
  }


  
  public String details() {
    if (this.value == null)
      return "null"; 
    if (this.value instanceof NativeError) {
      return this.value.toString();
    }
    try {
      return ScriptRuntime.toString(this.value);
    } catch (RuntimeException rte) {
      
      if (this.value instanceof Scriptable) {
        return ScriptRuntime.defaultObjectToString((Scriptable)this.value);
      }
      return this.value.toString();
    } 
  }





  
  public Object getValue() {
    return this.value;
  }




  
  @Deprecated
  public String getSourceName() {
    return sourceName();
  }




  
  @Deprecated
  public int getLineNumber() {
    return lineNumber();
  }
}
