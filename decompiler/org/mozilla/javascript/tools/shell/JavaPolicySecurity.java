package org.mozilla.javascript.tools.shell;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.Enumeration;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.GeneratedClassLoader;
import org.mozilla.javascript.Scriptable;

public class JavaPolicySecurity extends SecurityProxy {
  public Class<?> getStaticSecurityDomainClassInternal() {
    return ProtectionDomain.class;
  }
  
  private static class Loader
    extends ClassLoader
    implements GeneratedClassLoader {
    private ProtectionDomain domain;
    
    Loader(ClassLoader parent, ProtectionDomain domain) {
      super((parent != null) ? parent : getSystemClassLoader());
      this.domain = domain;
    }
    
    public Class<?> defineClass(String name, byte[] data) {
      return defineClass(name, data, 0, data.length, this.domain);
    }
    
    public void linkClass(Class<?> cl) {
      resolveClass(cl);
    }
  }


  
  private static class ContextPermissions
    extends PermissionCollection
  {
    static final long serialVersionUID = -1721494496320750721L;

    
    AccessControlContext _context = AccessController.getContext(); ContextPermissions(ProtectionDomain staticDomain) {
      if (staticDomain != null) {
        this._statisPermissions = staticDomain.getPermissions();
      }
      setReadOnly();
    }
    PermissionCollection _statisPermissions;
    
    public void add(Permission permission) {
      throw new RuntimeException("NOT IMPLEMENTED");
    }

    
    public boolean implies(Permission permission) {
      if (this._statisPermissions != null && 
        !this._statisPermissions.implies(permission)) {
        return false;
      }
      
      try {
        this._context.checkPermission(permission);
        return true;
      } catch (AccessControlException ex) {
        return false;
      } 
    }


    
    public Enumeration<Permission> elements() {
      return new Enumeration<Permission>() {
          public boolean hasMoreElements() { return false; } public Permission nextElement() {
            return null;
          }
        };
    }
    
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append(getClass().getName());
      sb.append('@');
      sb.append(Integer.toHexString(System.identityHashCode(this)));
      sb.append(" (context=");
      sb.append(this._context);
      sb.append(", static_permitions=");
      sb.append(this._statisPermissions);
      sb.append(')');
      return sb.toString();
    }
  }





  
  public JavaPolicySecurity() {
    new CodeSource(null, (Certificate[])null);
  }




  
  protected void callProcessFileSecure(final Context cx, final Scriptable scope, final String filename) {
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            URL url = JavaPolicySecurity.this.getUrlObj(filename);
            ProtectionDomain staticDomain = JavaPolicySecurity.this.getUrlDomain(url);
            try {
              Main.processFileSecure(cx, scope, url.toExternalForm(), staticDomain);
            }
            catch (IOException ioex) {
              throw new RuntimeException(ioex);
            } 
            return null;
          }
        });
  }

  
  private URL getUrlObj(String url) {
    URL uRL;
    try {
      uRL = new URL(url);
    } catch (MalformedURLException ex) {

      
      String curDir = System.getProperty("user.dir");
      curDir = curDir.replace('\\', '/');
      if (!curDir.endsWith("/")) {
        curDir = curDir + '/';
      }
      try {
        URL curDirURL = new URL("file:" + curDir);
        uRL = new URL(curDirURL, url);
      } catch (MalformedURLException ex2) {
        throw new RuntimeException("Can not construct file URL for '" + url + "':" + ex2.getMessage());
      } 
    } 

    
    return uRL;
  }


  
  private ProtectionDomain getUrlDomain(URL url) {
    CodeSource cs = new CodeSource(url, (Certificate[])null);
    PermissionCollection pc = Policy.getPolicy().getPermissions(cs);
    return new ProtectionDomain(cs, pc);
  }



  
  public GeneratedClassLoader createClassLoader(final ClassLoader parentLoader, Object securityDomain) {
    final ProtectionDomain domain = (ProtectionDomain)securityDomain;
    return AccessController.<GeneratedClassLoader>doPrivileged((PrivilegedAction)new PrivilegedAction<Loader>() {
          public JavaPolicySecurity.Loader run() {
            return new JavaPolicySecurity.Loader(parentLoader, domain);
          }
        });
  }


  
  public Object getDynamicSecurityDomain(Object securityDomain) {
    ProtectionDomain staticDomain = (ProtectionDomain)securityDomain;
    return getDynamicDomain(staticDomain);
  }
  
  private ProtectionDomain getDynamicDomain(ProtectionDomain staticDomain) {
    ContextPermissions p = new ContextPermissions(staticDomain);
    ProtectionDomain contextDomain = new ProtectionDomain(null, p);
    return contextDomain;
  }







  
  public Object callWithDomain(Object securityDomain, final Context cx, final Callable callable, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
    ProtectionDomain staticDomain = (ProtectionDomain)securityDomain;














    
    ProtectionDomain dynamicDomain = getDynamicDomain(staticDomain);
    ProtectionDomain[] tmp = { dynamicDomain };
    AccessControlContext restricted = new AccessControlContext(tmp);
    
    PrivilegedAction<Object> action = new PrivilegedAction() {
        public Object run() {
          return callable.call(cx, scope, thisObj, args);
        }
      };
    
    return AccessController.doPrivileged(action, restricted);
  }
}
