package org.fife.ui.rsyntaxtextarea;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


























public abstract class AbstractTokenMakerFactory
  extends TokenMakerFactory
{
  private Map<String, Object> tokenMakerMap;
  
  protected AbstractTokenMakerFactory() {
    this.tokenMakerMap = new HashMap<>();
    initTokenMakerMap();
  }









  
  protected TokenMaker getTokenMakerImpl(String key) {
    TokenMakerCreator tmc = (TokenMakerCreator)this.tokenMakerMap.get(key);
    if (tmc != null) {
      try {
        return tmc.create();
      } catch (RuntimeException re) {
        throw re;
      } catch (Exception e) {
        e.printStackTrace();
      } 
    }
    return null;
  }






  
  protected abstract void initTokenMakerMap();






  
  public Set<String> keySet() {
    return this.tokenMakerMap.keySet();
  }









  
  public void putMapping(String key, String className) {
    putMapping(key, className, null);
  }










  
  public void putMapping(String key, String className, ClassLoader cl) {
    this.tokenMakerMap.put(key, new TokenMakerCreator(className, cl));
  }


  
  private static class TokenMakerCreator
  {
    private String className;
    
    private ClassLoader cl;

    
    public TokenMakerCreator(String className, ClassLoader cl) {
      this.className = className;
      this.cl = (cl != null) ? cl : getClass().getClassLoader();
    }
    
    public TokenMaker create() throws Exception {
      return (TokenMaker)Class.forName(this.className, true, this.cl).newInstance();
    }
  }
}
