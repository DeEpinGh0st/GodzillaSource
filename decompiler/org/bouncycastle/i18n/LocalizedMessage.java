package org.bouncycastle.i18n;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TimeZone;
import org.bouncycastle.i18n.filter.Filter;
import org.bouncycastle.i18n.filter.TrustedInput;
import org.bouncycastle.i18n.filter.UntrustedInput;

public class LocalizedMessage {
  protected final String id;
  
  protected final String resource;
  
  public static final String DEFAULT_ENCODING = "ISO-8859-1";
  
  protected String encoding = "ISO-8859-1";
  
  protected FilteredArguments arguments;
  
  protected FilteredArguments extraArgs = null;
  
  protected Filter filter = null;
  
  protected ClassLoader loader = null;
  
  public LocalizedMessage(String paramString1, String paramString2) throws NullPointerException {
    if (paramString1 == null || paramString2 == null)
      throw new NullPointerException(); 
    this.id = paramString2;
    this.resource = paramString1;
    this.arguments = new FilteredArguments();
  }
  
  public LocalizedMessage(String paramString1, String paramString2, String paramString3) throws NullPointerException, UnsupportedEncodingException {
    if (paramString1 == null || paramString2 == null)
      throw new NullPointerException(); 
    this.id = paramString2;
    this.resource = paramString1;
    this.arguments = new FilteredArguments();
    if (!Charset.isSupported(paramString3))
      throw new UnsupportedEncodingException("The encoding \"" + paramString3 + "\" is not supported."); 
    this.encoding = paramString3;
  }
  
  public LocalizedMessage(String paramString1, String paramString2, Object[] paramArrayOfObject) throws NullPointerException {
    if (paramString1 == null || paramString2 == null || paramArrayOfObject == null)
      throw new NullPointerException(); 
    this.id = paramString2;
    this.resource = paramString1;
    this.arguments = new FilteredArguments(paramArrayOfObject);
  }
  
  public LocalizedMessage(String paramString1, String paramString2, String paramString3, Object[] paramArrayOfObject) throws NullPointerException, UnsupportedEncodingException {
    if (paramString1 == null || paramString2 == null || paramArrayOfObject == null)
      throw new NullPointerException(); 
    this.id = paramString2;
    this.resource = paramString1;
    this.arguments = new FilteredArguments(paramArrayOfObject);
    if (!Charset.isSupported(paramString3))
      throw new UnsupportedEncodingException("The encoding \"" + paramString3 + "\" is not supported."); 
    this.encoding = paramString3;
  }
  
  public String getEntry(String paramString, Locale paramLocale, TimeZone paramTimeZone) throws MissingEntryException {
    String str = this.id;
    if (paramString != null)
      str = str + "." + paramString; 
    try {
      ResourceBundle resourceBundle;
      if (this.loader == null) {
        resourceBundle = ResourceBundle.getBundle(this.resource, paramLocale);
      } else {
        resourceBundle = ResourceBundle.getBundle(this.resource, paramLocale, this.loader);
      } 
      null = resourceBundle.getString(str);
      if (!this.encoding.equals("ISO-8859-1"))
        null = new String(null.getBytes("ISO-8859-1"), this.encoding); 
      if (!this.arguments.isEmpty())
        null = formatWithTimeZone(null, this.arguments.getFilteredArgs(paramLocale), paramLocale, paramTimeZone); 
      return addExtraArgs(null, paramLocale);
    } catch (MissingResourceException missingResourceException) {
      throw new MissingEntryException("Can't find entry " + str + " in resource file " + this.resource + ".", this.resource, str, paramLocale, (this.loader != null) ? this.loader : getClassLoader());
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new RuntimeException(unsupportedEncodingException);
    } 
  }
  
  protected String formatWithTimeZone(String paramString, Object[] paramArrayOfObject, Locale paramLocale, TimeZone paramTimeZone) {
    MessageFormat messageFormat = new MessageFormat(" ");
    messageFormat.setLocale(paramLocale);
    messageFormat.applyPattern(paramString);
    if (!paramTimeZone.equals(TimeZone.getDefault())) {
      Format[] arrayOfFormat = messageFormat.getFormats();
      for (byte b = 0; b < arrayOfFormat.length; b++) {
        if (arrayOfFormat[b] instanceof DateFormat) {
          DateFormat dateFormat = (DateFormat)arrayOfFormat[b];
          dateFormat.setTimeZone(paramTimeZone);
          messageFormat.setFormat(b, dateFormat);
        } 
      } 
    } 
    return messageFormat.format(paramArrayOfObject);
  }
  
  protected String addExtraArgs(String paramString, Locale paramLocale) {
    if (this.extraArgs != null) {
      StringBuffer stringBuffer = new StringBuffer(paramString);
      Object[] arrayOfObject = this.extraArgs.getFilteredArgs(paramLocale);
      for (byte b = 0; b < arrayOfObject.length; b++)
        stringBuffer.append(arrayOfObject[b]); 
      paramString = stringBuffer.toString();
    } 
    return paramString;
  }
  
  public void setFilter(Filter paramFilter) {
    this.arguments.setFilter(paramFilter);
    if (this.extraArgs != null)
      this.extraArgs.setFilter(paramFilter); 
    this.filter = paramFilter;
  }
  
  public Filter getFilter() {
    return this.filter;
  }
  
  public void setClassLoader(ClassLoader paramClassLoader) {
    this.loader = paramClassLoader;
  }
  
  public ClassLoader getClassLoader() {
    return this.loader;
  }
  
  public String getId() {
    return this.id;
  }
  
  public String getResource() {
    return this.resource;
  }
  
  public Object[] getArguments() {
    return this.arguments.getArguments();
  }
  
  public void setExtraArgument(Object paramObject) {
    setExtraArguments(new Object[] { paramObject });
  }
  
  public void setExtraArguments(Object[] paramArrayOfObject) {
    if (paramArrayOfObject != null) {
      this.extraArgs = new FilteredArguments(paramArrayOfObject);
      this.extraArgs.setFilter(this.filter);
    } else {
      this.extraArgs = null;
    } 
  }
  
  public Object[] getExtraArgs() {
    return (this.extraArgs == null) ? null : this.extraArgs.getArguments();
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("Resource: \"").append(this.resource);
    stringBuffer.append("\" Id: \"").append(this.id).append("\"");
    stringBuffer.append(" Arguments: ").append((this.arguments.getArguments()).length).append(" normal");
    if (this.extraArgs != null && (this.extraArgs.getArguments()).length > 0)
      stringBuffer.append(", ").append((this.extraArgs.getArguments()).length).append(" extra"); 
    stringBuffer.append(" Encoding: ").append(this.encoding);
    stringBuffer.append(" ClassLoader: ").append(this.loader);
    return stringBuffer.toString();
  }
  
  protected class FilteredArguments {
    protected static final int NO_FILTER = 0;
    
    protected static final int FILTER = 1;
    
    protected static final int FILTER_URL = 2;
    
    protected Filter filter = null;
    
    protected boolean[] isLocaleSpecific;
    
    protected int[] argFilterType;
    
    protected Object[] arguments;
    
    protected Object[] unpackedArgs;
    
    protected Object[] filteredArgs;
    
    FilteredArguments() {
      this(new Object[0]);
    }
    
    FilteredArguments(Object[] param1ArrayOfObject) {
      this.arguments = param1ArrayOfObject;
      this.unpackedArgs = new Object[param1ArrayOfObject.length];
      this.filteredArgs = new Object[param1ArrayOfObject.length];
      this.isLocaleSpecific = new boolean[param1ArrayOfObject.length];
      this.argFilterType = new int[param1ArrayOfObject.length];
      for (byte b = 0; b < param1ArrayOfObject.length; b++) {
        if (param1ArrayOfObject[b] instanceof TrustedInput) {
          this.unpackedArgs[b] = ((TrustedInput)param1ArrayOfObject[b]).getInput();
          this.argFilterType[b] = 0;
        } else if (param1ArrayOfObject[b] instanceof UntrustedInput) {
          this.unpackedArgs[b] = ((UntrustedInput)param1ArrayOfObject[b]).getInput();
          if (param1ArrayOfObject[b] instanceof org.bouncycastle.i18n.filter.UntrustedUrlInput) {
            this.argFilterType[b] = 2;
          } else {
            this.argFilterType[b] = 1;
          } 
        } else {
          this.unpackedArgs[b] = param1ArrayOfObject[b];
          this.argFilterType[b] = 1;
        } 
        this.isLocaleSpecific[b] = this.unpackedArgs[b] instanceof LocaleString;
      } 
    }
    
    public boolean isEmpty() {
      return (this.unpackedArgs.length == 0);
    }
    
    public Object[] getArguments() {
      return this.arguments;
    }
    
    public Object[] getFilteredArgs(Locale param1Locale) {
      Object[] arrayOfObject = new Object[this.unpackedArgs.length];
      for (byte b = 0; b < this.unpackedArgs.length; b++) {
        Object object;
        if (this.filteredArgs[b] != null) {
          object = this.filteredArgs[b];
        } else {
          object = this.unpackedArgs[b];
          if (this.isLocaleSpecific[b]) {
            object = ((LocaleString)object).getLocaleString(param1Locale);
            object = filter(this.argFilterType[b], object);
          } else {
            object = filter(this.argFilterType[b], object);
            this.filteredArgs[b] = object;
          } 
        } 
        arrayOfObject[b] = object;
      } 
      return arrayOfObject;
    }
    
    private Object filter(int param1Int, Object param1Object) {
      if (this.filter != null) {
        Object object = (null == param1Object) ? "null" : param1Object;
        switch (param1Int) {
          case 0:
            return object;
          case 1:
            return this.filter.doFilter(object.toString());
          case 2:
            return this.filter.doFilterUrl(object.toString());
        } 
        return null;
      } 
      return param1Object;
    }
    
    public Filter getFilter() {
      return this.filter;
    }
    
    public void setFilter(Filter param1Filter) {
      if (param1Filter != this.filter)
        for (byte b = 0; b < this.unpackedArgs.length; b++)
          this.filteredArgs[b] = null;  
      this.filter = param1Filter;
    }
  }
}
