package org.fife.rsta.ac.java;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.FieldInfo;
import org.fife.rsta.ac.java.classreader.MethodInfo;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DescWindowCallback;
import org.fife.ui.autocomplete.ExternalURLHandler;
import org.fife.ui.autocomplete.Util;































public class JavadocUrlHandler
  implements ExternalURLHandler
{
  private String doBackups(String pkg, int backupCount) {
    int lastDot = pkg.length();
    while (lastDot > -1 && backupCount > 0) {
      lastDot = pkg.lastIndexOf('.', lastDot);
      backupCount--;
    } 
    return (lastDot > -1) ? pkg.substring(0, lastDot) : "";
  }






  
  private JavaLanguageSupport getJavaLanguageSupport() {
    return (JavaLanguageSupport)LanguageSupportFactory.get()
      .getSupportFor("text/java");
  }







  
  private static final String getAnchor(String url) {
    int pound = url.indexOf('#');
    return (pound > -1) ? url.substring(pound + 1) : null;
  }








  
  private static final String[] getArgs(String methodSignature) {
    String[] args = null;
    
    int lparen = methodSignature.indexOf('(');
    if (lparen > -1) {
      int rparen = methodSignature.indexOf(')', lparen);
      if (rparen > -1 && rparen > lparen + 1) {
        String temp = methodSignature.substring(lparen, rparen);
        args = temp.split("\\s*,\\s*");
      } 
    } 
    
    if (args == null) {
      args = new String[0];
    }
    return args;
  }







  
  private String getClass(Completion c, String desc) {
    String clazz = null;
    
    if (c instanceof ClassCompletion) {
      clazz = ((ClassCompletion)c).getClassName(true);
    }
    else if (c instanceof MemberCompletion) {
      MemberCompletion mc = (MemberCompletion)c;
      clazz = mc.getEnclosingClassName(true);
    } else {
      
      System.err.println("Can't determine class from completion type: " + c
          .getClass() + " (" + c.toString() + ") - href: " + desc);
    } 
    
    return clazz;
  }











  
  private String getPackage(Completion c, String desc) {
    String pkg = null;
    
    if (c instanceof ClassCompletion) {
      pkg = ((ClassCompletion)c).getPackageName();
    }
    else if (c instanceof MemberCompletion) {
      String definedIn = ((MemberCompletion)c).getEnclosingClassName(true);
      if (definedIn != null) {
        int lastDot = definedIn.lastIndexOf('.');
        if (lastDot > -1) {
          pkg = definedIn.substring(0, lastDot);
        }
      } 
    } else {
      
      System.err.println("Can't determine package from completion type: " + c
          .getClass() + " (" + c.toString() + ") - href: " + desc);
    } 
    
    return pkg;
  }









  
  private boolean isRelativeUrl(String text) {
    String[] EXTS = { ".html", ".htm" };
    for (int i = 0; i < EXTS.length; i++) {
      if (text.endsWith(EXTS[i]) || text.contains(EXTS[i] + "#") || text
        .contains(EXTS[i] + "?")) {
        return true;
      }
    } 
    return false;
  }









  
  public void urlClicked(HyperlinkEvent e, Completion c, DescWindowCallback callback) {
    URL url = e.getURL();
    if (url != null) {
      
      try {
        Util.browse(new URI(url.toString()));
      } catch (URISyntaxException ioe) {
        UIManager.getLookAndFeel().provideErrorFeedback(null);
        ioe.printStackTrace();
      } 

      
      return;
    } 

    
    String desc = e.getDescription();
    
    if (desc != null)
    {
      if (isRelativeUrl(desc)) {
        int ext = desc.indexOf(".htm");
        if (ext > -1)
        {


          
          String anchor = getAnchor(desc);
          String clazz = desc.substring(0, ext);
          int backups = 0;
          while (clazz.startsWith("../")) {
            backups++;
            clazz = clazz.substring(3);
          } 
          clazz = clazz.replace('/', '.');
          
          String pkg = getPackage(c, desc);
          if (pkg != null) {
            clazz = doBackups(pkg, backups) + "." + clazz;
            JavaLanguageSupport jls = getJavaLanguageSupport();
            ClassFile cf = jls.getJarManager().getClassEntry(clazz);
            if (cf != null) {
              ClassCompletion cc = new ClassCompletion(c.getProvider(), cf);
              callback.showSummaryFor(cc, anchor);
            
            }
          
          }
        
        }
      
      }
      else {
        
        JavaLanguageSupport jls = getJavaLanguageSupport();
        
        String clazz = desc;
        String member = null;
        int pound = desc.indexOf('#');
        if (pound > -1) {
          member = clazz.substring(pound + 1);
          clazz = clazz.substring(0, pound);
        } 

        
        if (member == null) {
          boolean guessedPackage = false;
          if (clazz.indexOf('.') == -1) {
            String pkg = getPackage(c, desc);
            if (pkg != null) {
              clazz = pkg + "." + clazz;
            }
            guessedPackage = true;
          } 
          ClassFile cf = jls.getJarManager().getClassEntry(clazz);
          if (cf == null && guessedPackage) {
            
            int lastDot = clazz.lastIndexOf('.');
            clazz = "java.lang." + clazz.substring(lastDot + 1);
            cf = jls.getJarManager().getClassEntry(clazz);
          } 
          if (cf != null) {
            ClassCompletion cc = new ClassCompletion(c.getProvider(), cf);
            callback.showSummaryFor(cc, null);
          } else {
            
            UIManager.getLookAndFeel().provideErrorFeedback(null);
            System.err.println("Unknown class: " + clazz);
          
          }

        
        }
        else {
          
          boolean guessedPackage = false;
          
          if (pound == 0) {
            
            clazz = getClass(c, desc);


          
          }
          else if (clazz.indexOf('.') == -1) {
            String pkg = getPackage(c, desc);
            if (pkg != null) {
              clazz = pkg + "." + clazz;
            }
            guessedPackage = true;
          } 

          
          ClassFile cf = jls.getJarManager().getClassEntry(clazz);
          if (cf == null && guessedPackage) {
            
            int lastDot = clazz.lastIndexOf('.');
            clazz = "java.lang." + clazz.substring(lastDot + 1);
            cf = jls.getJarManager().getClassEntry(clazz);
          } 
          if (cf != null) {
            
            Completion memberCompletion = null;
            
            int lparen = member.indexOf('(');
            if (lparen == -1) {
              FieldInfo fi = cf.getFieldInfoByName(member);
              if (fi != null) {
                memberCompletion = new FieldCompletion(c.getProvider(), fi);
              } else {
                
                List<MethodInfo> miList = cf.getMethodInfoByName(member, -1);
                if (miList != null && miList.size() > 0) {
                  MethodInfo mi = miList.get(0);
                  memberCompletion = new MethodCompletion(c.getProvider(), mi);
                }
              
              } 
            } else {
              
              String[] args = getArgs(member);
              String methodName = member.substring(0, lparen);
              List<MethodInfo> miList = cf.getMethodInfoByName(methodName, args.length);
              if (miList != null && miList.size() > 0) {
                if (miList.size() > 1) {
                  
                  System.err.println("Multiple overload support not yet implemented");
                } else {
                  
                  MethodInfo mi = miList.get(0);
                  memberCompletion = new MethodCompletion(c.getProvider(), mi);
                } 
              }
            } 
            
            if (memberCompletion != null) {
              callback.showSummaryFor(memberCompletion, null);
            }
          }
          else {
            
            UIManager.getLookAndFeel().provideErrorFeedback(null);
            System.err.println("Unknown class: " + clazz + " (href: " + desc + ")");
          } 
        } 
      } 
    }
  }
}
