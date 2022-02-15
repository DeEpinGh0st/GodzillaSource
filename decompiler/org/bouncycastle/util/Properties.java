package org.bouncycastle.util;

import java.math.BigInteger;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class Properties {
  private static final ThreadLocal threadProperties = new ThreadLocal();
  
  public static boolean isOverrideSet(String paramString) {
    try {
      String str = fetchProperty(paramString);
      return (str != null) ? "true".equals(Strings.toLowerCase(str)) : false;
    } catch (AccessControlException accessControlException) {
      return false;
    } 
  }
  
  public static boolean setThreadOverride(String paramString, boolean paramBoolean) {
    boolean bool = isOverrideSet(paramString);
    Map<Object, Object> map = threadProperties.get();
    if (map == null)
      map = new HashMap<Object, Object>(); 
    map.put(paramString, paramBoolean ? "true" : "false");
    threadProperties.set(map);
    return bool;
  }
  
  public static boolean removeThreadOverride(String paramString) {
    boolean bool = isOverrideSet(paramString);
    Map map = threadProperties.get();
    if (map == null)
      return false; 
    map.remove(paramString);
    if (map.isEmpty()) {
      threadProperties.remove();
    } else {
      threadProperties.set(map);
    } 
    return bool;
  }
  
  public static BigInteger asBigInteger(String paramString) {
    String str = fetchProperty(paramString);
    return (str != null) ? new BigInteger(str) : null;
  }
  
  public static Set<String> asKeySet(String paramString) {
    HashSet<String> hashSet = new HashSet();
    String str = fetchProperty(paramString);
    if (str != null) {
      StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
      while (stringTokenizer.hasMoreElements())
        hashSet.add(Strings.toLowerCase(stringTokenizer.nextToken()).trim()); 
    } 
    return Collections.unmodifiableSet(hashSet);
  }
  
  private static String fetchProperty(final String propertyName) {
    return AccessController.<String>doPrivileged(new PrivilegedAction<String>() {
          public Object run() {
            Map map = Properties.threadProperties.get();
            return (map != null) ? map.get(propertyName) : System.getProperty(propertyName);
          }
        });
  }
}
