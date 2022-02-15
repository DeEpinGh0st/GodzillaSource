package org.mozilla.javascript;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;

















public class SecurityUtilities
{
  public static String getSystemProperty(final String name) {
    return AccessController.<String>doPrivileged(new PrivilegedAction<String>()
        {
          
          public String run()
          {
            return System.getProperty(name);
          }
        });
  }

  
  public static ProtectionDomain getProtectionDomain(final Class<?> clazz) {
    return AccessController.<ProtectionDomain>doPrivileged(new PrivilegedAction<ProtectionDomain>()
        {
          
          public ProtectionDomain run()
          {
            return clazz.getProtectionDomain();
          }
        });
  }







  
  public static ProtectionDomain getScriptProtectionDomain() {
    final SecurityManager securityManager = System.getSecurityManager();
    if (securityManager instanceof RhinoSecurityManager) {
      return AccessController.<ProtectionDomain>doPrivileged(new PrivilegedAction<ProtectionDomain>()
          {
            public ProtectionDomain run() {
              Class<?> c = ((RhinoSecurityManager)securityManager).getCurrentScriptClass();
              
              return (c == null) ? null : c.getProtectionDomain();
            }
          });
    }
    
    return null;
  }
}
