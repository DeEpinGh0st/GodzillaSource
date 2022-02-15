package org.fife.rsta.ac.js.engine;

import java.util.HashMap;




public class JavaScriptEngineFactory
{
  public static final String DEFAULT = "EMCA";
  private HashMap<String, JavaScriptEngine> supportedEngines = new HashMap<>();

  
  private static JavaScriptEngineFactory Instance = new JavaScriptEngineFactory();
  
  static {
    Instance().addEngine("EMCA", new EMCAJavaScriptEngine());
    Instance().addEngine("JSR223", new JSR223JavaScriptEngine());
    Instance().addEngine("RHINO", new RhinoJavaScriptEngine());
  }





  
  public static JavaScriptEngineFactory Instance() {
    return Instance;
  }

  
  public JavaScriptEngine getEngineFromCache(String name) {
    if (name == null) {
      name = "EMCA";
    }
    return this.supportedEngines.get(name);
  }

  
  public void addEngine(String name, JavaScriptEngine engine) {
    this.supportedEngines.put(name, engine);
  }

  
  public void removeEngine(String name) {
    this.supportedEngines.remove(name);
  }
}
