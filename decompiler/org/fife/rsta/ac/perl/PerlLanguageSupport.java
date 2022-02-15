package org.fife.rsta.ac.perl;

import java.io.File;
import javax.swing.ListCellRenderer;
import javax.swing.text.JTextComponent;
import org.fife.rsta.ac.AbstractLanguageSupport;
import org.fife.rsta.ac.IOUtil;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rtextarea.ToolTipSupplier;













































public class PerlLanguageSupport
  extends AbstractLanguageSupport
{
  private PerlCompletionProvider provider;
  private PerlParser parser;
  private static File perlInstallLoc;
  private static File DEFAULT_PERL_INSTALL_LOC;
  private static boolean useParensWithFunctions;
  private static boolean useSystemPerldoc;
  
  static {
    String path = IOUtil.getEnvSafely("PATH");
    
    if (path != null) {
      
      String perlLoc = "perl";
      if (File.separatorChar == '\\') {
        perlLoc = perlLoc + ".exe";
      }
      
      String[] dirs = path.split(File.pathSeparator);
      for (int i = 0; i < dirs.length; i++) {
        File temp = new File(dirs[i], perlLoc);
        
        if (temp.isFile()) {
          DEFAULT_PERL_INSTALL_LOC = (new File(dirs[i])).getParentFile();
          
          break;
        } 
      } 
      perlInstallLoc = DEFAULT_PERL_INSTALL_LOC;
    } 
  }






  
  public PerlLanguageSupport() {
    setParameterAssistanceEnabled(true);
    setShowDescWindow(true);
    setAutoCompleteEnabled(true);
    setAutoActivationEnabled(true);
    setAutoActivationDelay(800);
  }





  
  protected ListCellRenderer<Object> createDefaultCompletionCellRenderer() {
    CompletionCellRenderer ccr = new CompletionCellRenderer();
    ccr.setShowTypes(false);
    return (ListCellRenderer<Object>)ccr;
  }








  
  public static File getDefaultPerlInstallLocation() {
    return DEFAULT_PERL_INSTALL_LOC;
  }








  
  public static File getPerlInstallLocation() {
    return perlInstallLoc;
  }






  
  private PerlParser getParser() {
    if (this.parser == null) {
      this.parser = new PerlParser();
    }
    return this.parser;
  }










  
  public PerlParser getParser(RSyntaxTextArea textArea) {
    Object parser = textArea.getClientProperty("org.fife.rsta.ac.LanguageSupport.LanguageParser");
    if (parser instanceof PerlParser) {
      return (PerlParser)parser;
    }
    return null;
  }






  
  private PerlCompletionProvider getProvider() {
    if (this.provider == null) {
      this.provider = new PerlCompletionProvider();
    }
    return this.provider;
  }








  
  public String getPerl5LibOverride() {
    return getParser().getPerl5LibOverride();
  }







  
  public boolean getUseParensWithFunctions() {
    return useParensWithFunctions;
  }














  
  public static boolean getUseSystemPerldoc() {
    return useSystemPerldoc;
  }







  
  public boolean getWarningsEnabled() {
    return getParser().getWarningsEnabled();
  }






  
  public void install(RSyntaxTextArea textArea) {
    PerlCompletionProvider provider = getProvider();
    AutoCompletion ac = createAutoCompletion((CompletionProvider)provider);
    ac.install((JTextComponent)textArea);
    installImpl(textArea, ac);
    
    textArea.setToolTipSupplier((ToolTipSupplier)provider);
    
    PerlParser parser = getParser();
    textArea.addParser((Parser)parser);
    textArea.putClientProperty("org.fife.rsta.ac.LanguageSupport.LanguageParser", parser);
  }













  
  public boolean isParsingEnabled() {
    return getParser().isEnabled();
  }







  
  public boolean isTaintModeEnabled() {
    return getParser().isTaintModeEnabled();
  }








  
  public void setParsingEnabled(boolean enabled) {
    getParser().setEnabled(enabled);
  }









  
  public void setPerl5LibOverride(String override) {
    getParser().setPerl5LibOverride(override);
  }








  
  public static void setPerlInstallLocation(File loc) {
    perlInstallLoc = loc;
  }







  
  public void setTaintModeEnabled(boolean enabled) {
    getParser().setTaintModeEnabled(enabled);
  }







  
  public void setWarningsEnabled(boolean enabled) {
    getParser().setWarningsEnabled(enabled);
  }







  
  public void setUseParensWithFunctions(boolean use) {
    if (use != useParensWithFunctions) {
      useParensWithFunctions = use;
      if (this.provider != null) {
        this.provider.setUseParensWithFunctions(use);
      }
    } 
  }














  
  public static void setUseSystemPerldoc(boolean use) {
    useSystemPerldoc = use;
  }






  
  public void uninstall(RSyntaxTextArea textArea) {
    uninstallImpl(textArea);
    
    PerlParser parser = getParser(textArea);
    if (parser != null)
      textArea.removeParser((Parser)parser); 
  }
}
