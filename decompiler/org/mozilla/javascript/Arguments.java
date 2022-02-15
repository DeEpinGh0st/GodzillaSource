package org.mozilla.javascript;
































































































































































































































































































































































final class Arguments
  extends IdScriptableObject
{
  static final long serialVersionUID = 4275508002492040609L;
  private static final String FTAG = "Arguments";
  private static final int Id_callee = 1;
  private static final int Id_length = 2;
  private static final int Id_caller = 3;
  private static final int MAX_INSTANCE_ID = 3;
  private Object callerObj;
  private Object calleeObj;
  private Object lengthObj;
  private int callerAttr;
  private int calleeAttr;
  private int lengthAttr;
  private NativeCall activation;
  private Object[] args;
  
  public Arguments(NativeCall activation) {
    this.callerAttr = 2;
    this.calleeAttr = 2;
    this.lengthAttr = 2;
    this.activation = activation;
    Scriptable parent = activation.getParentScope();
    setParentScope(parent);
    setPrototype(ScriptableObject.getObjectPrototype(parent));
    this.args = activation.originalArgs;
    this.lengthObj = Integer.valueOf(this.args.length);
    NativeFunction f = activation.function;
    this.calleeObj = f;
    int version = f.getLanguageVersion();
    if (version <= 130 && version != 0) {
      this.callerObj = null;
    } else {
      this.callerObj = NOT_FOUND;
    } 
  }
  
  public String getClassName() {
    return "Arguments";
  }
  
  private Object arg(int index) {
    if (index < 0 || this.args.length <= index)
      return NOT_FOUND; 
    return this.args[index];
  }
  
  private void putIntoActivation(int index, Object value) {
    String argName = this.activation.function.getParamOrVarName(index);
    this.activation.put(argName, this.activation, value);
  }
  
  private Object getFromActivation(int index) {
    String argName = this.activation.function.getParamOrVarName(index);
    return this.activation.get(argName, this.activation);
  }
  
  private void replaceArg(int index, Object value) {
    if (sharedWithActivation(index))
      putIntoActivation(index, value); 
    synchronized (this) {
      if (this.args == this.activation.originalArgs)
        this.args = (Object[])this.args.clone(); 
      this.args[index] = value;
    } 
  }
  
  private void removeArg(int index) {
    synchronized (this) {
      if (this.args[index] != NOT_FOUND) {
        if (this.args == this.activation.originalArgs)
          this.args = (Object[])this.args.clone(); 
        this.args[index] = NOT_FOUND;
      } 
    } 
  }
  
  public boolean has(int index, Scriptable start) {
    if (arg(index) != NOT_FOUND)
      return true; 
    return super.has(index, start);
  }
  
  public Object get(int index, Scriptable start) {
    Object value = arg(index);
    if (value == NOT_FOUND)
      return super.get(index, start); 
    if (sharedWithActivation(index))
      return getFromActivation(index); 
    return value;
  }
  
  private boolean sharedWithActivation(int index) {
    NativeFunction f = this.activation.function;
    int definedCount = f.getParamCount();
    if (index < definedCount) {
      if (index < definedCount - 1) {
        String argName = f.getParamOrVarName(index);
        for (int i = index + 1; i < definedCount; i++) {
          if (argName.equals(f.getParamOrVarName(i)))
            return false; 
        } 
      } 
      return true;
    } 
    return false;
  }
  
  public void put(int index, Scriptable start, Object value) {
    if (arg(index) == NOT_FOUND) {
      super.put(index, start, value);
    } else {
      replaceArg(index, value);
    } 
  }
  
  public void delete(int index) {
    if (0 <= index && index < this.args.length)
      removeArg(index); 
    super.delete(index);
  }
  
  protected int getMaxInstanceId() {
    return 3;
  }
  
  protected int findInstanceIdInfo(String s) {
    int attr, id = 0;
    String X = null;
    int s_length = s.length();
    if (s_length == 6) {
      int c = s.charAt(5);
      if (c == 101) {
        X = "callee";
        id = 1;
      } else if (c == 104) {
        X = "length";
        id = 2;
      } else if (c == 114) {
        X = "caller";
        id = 3;
      } 
    } 
    if (X != null && X != s && !X.equals(s))
      id = 0; 
    if (id == 0)
      return super.findInstanceIdInfo(s); 
    switch (id) {
      case 1:
        attr = this.calleeAttr;
        return instanceIdInfo(attr, id);
      case 3:
        attr = this.callerAttr;
        return instanceIdInfo(attr, id);
      case 2:
        attr = this.lengthAttr;
        return instanceIdInfo(attr, id);
    } 
    throw new IllegalStateException();
  }
  
  protected String getInstanceIdName(int id) {
    switch (id) {
      case 1:
        return "callee";
      case 2:
        return "length";
      case 3:
        return "caller";
    } 
    return null;
  }
  
  protected Object getInstanceIdValue(int id) {
    Object value;
    switch (id) {
      case 1:
        return this.calleeObj;
      case 2:
        return this.lengthObj;
      case 3:
        value = this.callerObj;
        if (value == UniqueTag.NULL_VALUE) {
          value = null;
        } else if (value == null) {
          NativeCall caller = this.activation.parentActivationCall;
          if (caller != null)
            value = caller.get("arguments", caller); 
        } 
        return value;
    } 
    return super.getInstanceIdValue(id);
  }
  
  protected void setInstanceIdValue(int id, Object value) {
    switch (id) {
      case 1:
        this.calleeObj = value;
        return;
      case 2:
        this.lengthObj = value;
        return;
      case 3:
        this.callerObj = (value != null) ? value : UniqueTag.NULL_VALUE;
        return;
    } 
    super.setInstanceIdValue(id, value);
  }
  
  protected void setInstanceIdAttributes(int id, int attr) {
    switch (id) {
      case 1:
        this.calleeAttr = attr;
        return;
      case 2:
        this.lengthAttr = attr;
        return;
      case 3:
        this.callerAttr = attr;
        return;
    } 
    super.setInstanceIdAttributes(id, attr);
  }
  
  Object[] getIds(boolean getAll) {
    Object[] ids = super.getIds(getAll);
    if (this.args.length != 0) {
      boolean[] present = new boolean[this.args.length];
      int extraCount = this.args.length;
      int i;
      for (i = 0; i != ids.length; i++) {
        Object id = ids[i];
        if (id instanceof Integer) {
          int index = ((Integer)id).intValue();
          if (0 <= index && index < this.args.length && !present[index]) {
            present[index] = true;
            extraCount--;
          } 
        } 
      } 
      if (!getAll)
        for (i = 0; i < present.length; i++) {
          if (!present[i] && super.has(i, this)) {
            present[i] = true;
            extraCount--;
          } 
        }  
      if (extraCount != 0) {
        Object[] tmp = new Object[extraCount + ids.length];
        System.arraycopy(ids, 0, tmp, extraCount, ids.length);
        ids = tmp;
        int offset = 0;
        for (int j = 0; j != this.args.length; j++) {
          if (present == null || !present[j]) {
            ids[offset] = Integer.valueOf(j);
            offset++;
          } 
        } 
        if (offset != extraCount)
          Kit.codeBug(); 
      } 
    } 
    return ids;
  }
  
  protected ScriptableObject getOwnPropertyDescriptor(Context cx, Object id) {
    double d = ScriptRuntime.toNumber(id);
    int index = (int)d;
    if (d != index)
      return super.getOwnPropertyDescriptor(cx, id); 
    Object value = arg(index);
    if (value == NOT_FOUND)
      return super.getOwnPropertyDescriptor(cx, id); 
    if (sharedWithActivation(index))
      value = getFromActivation(index); 
    if (super.has(index, this)) {
      ScriptableObject desc = super.getOwnPropertyDescriptor(cx, id);
      desc.put("value", desc, value);
      return desc;
    } 
    Scriptable scope = getParentScope();
    if (scope == null)
      scope = this; 
    return buildDataDescriptor(scope, value, 0);
  }
  
  protected void defineOwnProperty(Context cx, Object id, ScriptableObject desc, boolean checkValid) {
    super.defineOwnProperty(cx, id, desc, checkValid);
    double d = ScriptRuntime.toNumber(id);
    int index = (int)d;
    if (d != index)
      return; 
    Object value = arg(index);
    if (value == NOT_FOUND)
      return; 
    if (isAccessorDescriptor(desc)) {
      removeArg(index);
      return;
    } 
    Object newValue = getProperty(desc, "value");
    if (newValue == NOT_FOUND)
      return; 
    replaceArg(index, newValue);
    if (isFalse(getProperty(desc, "writable")))
      removeArg(index); 
  }
}
