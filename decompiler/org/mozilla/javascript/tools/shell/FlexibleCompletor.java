package org.mozilla.javascript.tools.shell;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
























































































































































































































































































































































































































class FlexibleCompletor
  implements InvocationHandler
{
  private Method completeMethod;
  private Scriptable global;
  
  FlexibleCompletor(Class<?> completorClass, Scriptable global) throws NoSuchMethodException {
    this.global = global;
    this.completeMethod = completorClass.getMethod("complete", new Class[] { String.class, int.class, List.class });
  }


  
  public Object invoke(Object proxy, Method method, Object[] args) {
    if (method.equals(this.completeMethod)) {
      int result = complete((String)args[0], ((Integer)args[1]).intValue(), (List<String>)args[2]);
      
      return Integer.valueOf(result);
    } 
    throw new NoSuchMethodError(method.toString());
  }






  
  public int complete(String buffer, int cursor, List<String> candidates) {
    int m = cursor - 1;
    while (m >= 0) {
      char c = buffer.charAt(m);
      if (!Character.isJavaIdentifierPart(c) && c != '.')
        break; 
      m--;
    } 
    String namesAndDots = buffer.substring(m + 1, cursor);
    String[] names = namesAndDots.split("\\.", -1);
    Scriptable obj = this.global;
    for (int i = 0; i < names.length - 1; i++) {
      Object val = obj.get(names[i], this.global);
      if (val instanceof Scriptable) {
        obj = (Scriptable)val;
      } else {
        return buffer.length();
      } 
    } 
    Object[] ids = (obj instanceof ScriptableObject) ? ((ScriptableObject)obj).getAllIds() : obj.getIds();

    
    String lastPart = names[names.length - 1];
    for (int j = 0; j < ids.length; j++) {
      if (ids[j] instanceof String) {
        
        String id = (String)ids[j];
        if (id.startsWith(lastPart)) {
          if (obj.get(id, obj) instanceof org.mozilla.javascript.Function)
            id = id + "("; 
          candidates.add(id);
        } 
      } 
    }  return buffer.length() - lastPart.length();
  }
}
