package org.bouncycastle.i18n;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;

public class MissingEntryException extends RuntimeException {
  protected final String resource;
  
  protected final String key;
  
  protected final ClassLoader loader;
  
  protected final Locale locale;
  
  private String debugMsg;
  
  public MissingEntryException(String paramString1, String paramString2, String paramString3, Locale paramLocale, ClassLoader paramClassLoader) {
    super(paramString1);
    this.resource = paramString2;
    this.key = paramString3;
    this.locale = paramLocale;
    this.loader = paramClassLoader;
  }
  
  public MissingEntryException(String paramString1, Throwable paramThrowable, String paramString2, String paramString3, Locale paramLocale, ClassLoader paramClassLoader) {
    super(paramString1, paramThrowable);
    this.resource = paramString2;
    this.key = paramString3;
    this.locale = paramLocale;
    this.loader = paramClassLoader;
  }
  
  public String getKey() {
    return this.key;
  }
  
  public String getResource() {
    return this.resource;
  }
  
  public ClassLoader getClassLoader() {
    return this.loader;
  }
  
  public Locale getLocale() {
    return this.locale;
  }
  
  public String getDebugMsg() {
    if (this.debugMsg == null) {
      this.debugMsg = "Can not find entry " + this.key + " in resource file " + this.resource + " for the locale " + this.locale + ".";
      if (this.loader instanceof URLClassLoader) {
        URL[] arrayOfURL = ((URLClassLoader)this.loader).getURLs();
        this.debugMsg += " The following entries in the classpath were searched: ";
        for (byte b = 0; b != arrayOfURL.length; b++)
          this.debugMsg += arrayOfURL[b] + " "; 
      } 
    } 
    return this.debugMsg;
  }
}
