package org.bouncycastle.i18n;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.TimeZone;

public class MessageBundle extends TextBundle {
  public static final String TITLE_ENTRY = "title";
  
  public MessageBundle(String paramString1, String paramString2) throws NullPointerException {
    super(paramString1, paramString2);
  }
  
  public MessageBundle(String paramString1, String paramString2, String paramString3) throws NullPointerException, UnsupportedEncodingException {
    super(paramString1, paramString2, paramString3);
  }
  
  public MessageBundle(String paramString1, String paramString2, Object[] paramArrayOfObject) throws NullPointerException {
    super(paramString1, paramString2, paramArrayOfObject);
  }
  
  public MessageBundle(String paramString1, String paramString2, String paramString3, Object[] paramArrayOfObject) throws NullPointerException, UnsupportedEncodingException {
    super(paramString1, paramString2, paramString3, paramArrayOfObject);
  }
  
  public String getTitle(Locale paramLocale, TimeZone paramTimeZone) throws MissingEntryException {
    return getEntry("title", paramLocale, paramTimeZone);
  }
  
  public String getTitle(Locale paramLocale) throws MissingEntryException {
    return getEntry("title", paramLocale, TimeZone.getDefault());
  }
}
