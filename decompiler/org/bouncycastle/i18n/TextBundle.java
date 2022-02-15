package org.bouncycastle.i18n;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.TimeZone;

public class TextBundle extends LocalizedMessage {
  public static final String TEXT_ENTRY = "text";
  
  public TextBundle(String paramString1, String paramString2) throws NullPointerException {
    super(paramString1, paramString2);
  }
  
  public TextBundle(String paramString1, String paramString2, String paramString3) throws NullPointerException, UnsupportedEncodingException {
    super(paramString1, paramString2, paramString3);
  }
  
  public TextBundle(String paramString1, String paramString2, Object[] paramArrayOfObject) throws NullPointerException {
    super(paramString1, paramString2, paramArrayOfObject);
  }
  
  public TextBundle(String paramString1, String paramString2, String paramString3, Object[] paramArrayOfObject) throws NullPointerException, UnsupportedEncodingException {
    super(paramString1, paramString2, paramString3, paramArrayOfObject);
  }
  
  public String getText(Locale paramLocale, TimeZone paramTimeZone) throws MissingEntryException {
    return getEntry("text", paramLocale, paramTimeZone);
  }
  
  public String getText(Locale paramLocale) throws MissingEntryException {
    return getEntry("text", paramLocale, TimeZone.getDefault());
  }
}
