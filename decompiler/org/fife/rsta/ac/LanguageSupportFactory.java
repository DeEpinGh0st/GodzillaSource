package org.fife.rsta.ac;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;





















public class LanguageSupportFactory
  implements PropertyChangeListener
{
  private static final LanguageSupportFactory INSTANCE = new LanguageSupportFactory();





  
  private Map<String, String> styleToSupportClass;




  
  private Map<String, LanguageSupport> styleToSupport;




  
  private static final String LANGUAGE_SUPPORT_PROPERTY = "org.fife.rsta.ac.LanguageSupport";





  
  private LanguageSupportFactory() {
    createSupportMap();
  }










  
  public void addLanguageSupport(String style, String lsClassName) {
    this.styleToSupportClass.put(style, lsClassName);
  }





  
  private void createSupportMap() {
    this.styleToSupport = new HashMap<>();
    this.styleToSupportClass = new HashMap<>();
    
    String prefix = "org.fife.rsta.ac.";
    
    addLanguageSupport("text/c", prefix + "c.CLanguageSupport");
    
    addLanguageSupport("text/css", prefix + "css.CssLanguageSupport");
    
    addLanguageSupport("text/groovy", prefix + "groovy.GroovyLanguageSupport");
    
    addLanguageSupport("text/html", prefix + "html.HtmlLanguageSupport");
    
    addLanguageSupport("text/java", prefix + "java.JavaLanguageSupport");
    
    addLanguageSupport("text/javascript", prefix + "js.JavaScriptLanguageSupport");
    
    addLanguageSupport("text/jsp", prefix + "jsp.JspLanguageSupport");
    
    addLanguageSupport("text/less", prefix + "less.LessLanguageSupport");
    
    addLanguageSupport("text/perl", prefix + "perl.PerlLanguageSupport");
    
    addLanguageSupport("text/php", prefix + "php.PhpLanguageSupport");
    
    addLanguageSupport("text/typescript", prefix + "ts.TypeScriptLanguageSupport");
    
    addLanguageSupport("text/unix", prefix + "sh.ShellLanguageSupport");
    
    addLanguageSupport("text/xml", prefix + "xml.XmlLanguageSupport");
  }








  
  public static LanguageSupportFactory get() {
    return INSTANCE;
  }










  
  public LanguageSupport getSupportFor(String style) {
    LanguageSupport support = this.styleToSupport.get(style);
    
    if (support == null) {
      String supportClazz = this.styleToSupportClass.get(style);
      if (supportClazz != null) {
        try {
          Class<?> clazz = Class.forName(supportClazz);
          support = (LanguageSupport)clazz.newInstance();
        } catch (RuntimeException re) {
          throw re;
        } catch (Exception e) {
          e.printStackTrace();
        } 
        this.styleToSupport.put(style, support);
        
        this.styleToSupportClass.remove(style);
      } 
    } 
    
    return support;
  }








  
  private void installSupport(RSyntaxTextArea textArea) {
    String style = textArea.getSyntaxEditingStyle();
    LanguageSupport support = getSupportFor(style);
    if (support != null) {
      support.install(textArea);
    }
    textArea.putClientProperty("org.fife.rsta.ac.LanguageSupport", support);
  }









  
  public void propertyChange(PropertyChangeEvent e) {
    RSyntaxTextArea source = (RSyntaxTextArea)e.getSource();
    String name = e.getPropertyName();
    if ("RSTA.syntaxStyle".equals(name)) {
      uninstallSupport(source);
      installSupport(source);
    } 
  }










  
  public void register(RSyntaxTextArea textArea) {
    installSupport(textArea);
    textArea.addPropertyChangeListener("RSTA.syntaxStyle", this);
  }








  
  private void uninstallSupport(RSyntaxTextArea textArea) {
    LanguageSupport support = (LanguageSupport)textArea.getClientProperty("org.fife.rsta.ac.LanguageSupport");
    
    if (support != null) {
      support.uninstall(textArea);
    }
  }








  
  public void unregister(RSyntaxTextArea textArea) {
    uninstallSupport(textArea);
    textArea.removePropertyChangeListener("RSTA.syntaxStyle", this);
  }
}
