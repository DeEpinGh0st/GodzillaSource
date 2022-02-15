package org.fife.ui.rsyntaxtextarea;

import java.security.AccessControlException;
import java.util.Set;
import org.fife.ui.rsyntaxtextarea.modes.PlainTextTokenMaker;


































public abstract class TokenMakerFactory
{
  public static final String PROPERTY_DEFAULT_TOKEN_MAKER_FACTORY = "TokenMakerFactory";
  private static TokenMakerFactory DEFAULT_INSTANCE;
  
  public static synchronized TokenMakerFactory getDefaultInstance() {
    if (DEFAULT_INSTANCE == null) {
      String clazz;
      try {
        clazz = System.getProperty("TokenMakerFactory");
      } catch (AccessControlException ace) {
        clazz = null;
      } 
      if (clazz == null) {
        clazz = "org.fife.ui.rsyntaxtextarea.DefaultTokenMakerFactory";
      }
      
      try {
        DEFAULT_INSTANCE = Class.forName(clazz).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
      } catch (RuntimeException re) {
        throw re;
      } catch (Exception e) {
        e.printStackTrace();
        throw new InternalError("Cannot find TokenMakerFactory: " + clazz);
      } 
    } 
    
    return DEFAULT_INSTANCE;
  }







  
  public final TokenMaker getTokenMaker(String key) {
    PlainTextTokenMaker plainTextTokenMaker;
    TokenMaker tm = getTokenMakerImpl(key);
    if (tm == null) {
      plainTextTokenMaker = new PlainTextTokenMaker();
    }
    return (TokenMaker)plainTextTokenMaker;
  }









  
  protected abstract TokenMaker getTokenMakerImpl(String paramString);









  
  public abstract Set<String> keySet();








  
  public static synchronized void setDefaultInstance(TokenMakerFactory tmf) {
    if (tmf == null) {
      throw new IllegalArgumentException("tmf cannot be null");
    }
    DEFAULT_INSTANCE = tmf;
  }
}
