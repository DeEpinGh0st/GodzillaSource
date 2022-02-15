package org.fife.rsta.ac;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionCellRenderer;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.Util;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;


























































public abstract class AbstractLanguageSupport
  implements LanguageSupport
{
  private Map<RSyntaxTextArea, AutoCompletion> textAreaToAutoCompletion;
  private boolean autoCompleteEnabled;
  private boolean autoActivationEnabled;
  private int autoActivationDelay;
  private boolean parameterAssistanceEnabled;
  private boolean showDescWindow;
  private ListCellRenderer<Object> renderer;
  
  protected AbstractLanguageSupport() {
    setDefaultCompletionCellRenderer(null);
    this.textAreaToAutoCompletion = new HashMap<>();
    this.autoCompleteEnabled = true;
    this.autoActivationEnabled = false;
    this.autoActivationDelay = 300;
  }








  
  protected AutoCompletion createAutoCompletion(CompletionProvider p) {
    AutoCompletion ac = new AutoCompletion(p);
    ac.setListCellRenderer(getDefaultCompletionCellRenderer());
    ac.setAutoCompleteEnabled(isAutoCompleteEnabled());
    ac.setAutoActivationEnabled(isAutoActivationEnabled());
    ac.setAutoActivationDelay(getAutoActivationDelay());
    ac.setParameterAssistanceEnabled(isParameterAssistanceEnabled());
    ac.setShowDescWindow(getShowDescWindow());
    return ac;
  }








  
  protected ListCellRenderer<Object> createDefaultCompletionCellRenderer() {
    return new DefaultListCellRenderer();
  }






  
  private void delegateToSubstanceRenderer(CompletionCellRenderer ccr) {
    try {
      ccr.delegateToSubstanceRenderer();
    } catch (Exception e) {
      
      e.printStackTrace();
    } 
  }


  
  public int getAutoActivationDelay() {
    return this.autoActivationDelay;
  }








  
  protected AutoCompletion getAutoCompletionFor(RSyntaxTextArea textArea) {
    return this.textAreaToAutoCompletion.get(textArea);
  }


  
  public ListCellRenderer<Object> getDefaultCompletionCellRenderer() {
    return this.renderer;
  }


  
  public boolean getShowDescWindow() {
    return this.showDescWindow;
  }






  
  protected Set<RSyntaxTextArea> getTextAreas() {
    return this.textAreaToAutoCompletion.keySet();
  }











  
  protected void installImpl(RSyntaxTextArea textArea, AutoCompletion ac) {
    this.textAreaToAutoCompletion.put(textArea, ac);
  }


  
  public boolean isAutoActivationEnabled() {
    return this.autoActivationEnabled;
  }


  
  public boolean isAutoCompleteEnabled() {
    return this.autoCompleteEnabled;
  }


  
  public boolean isParameterAssistanceEnabled() {
    return this.parameterAssistanceEnabled;
  }


  
  public void setAutoActivationDelay(int ms) {
    ms = Math.max(0, ms);
    if (ms != this.autoActivationDelay) {
      this.autoActivationDelay = ms;
      for (AutoCompletion ac : this.textAreaToAutoCompletion.values()) {
        ac.setAutoActivationDelay(this.autoActivationDelay);
      }
    } 
  }


  
  public void setAutoActivationEnabled(boolean enabled) {
    if (enabled != this.autoActivationEnabled) {
      this.autoActivationEnabled = enabled;
      for (AutoCompletion ac : this.textAreaToAutoCompletion.values()) {
        ac.setAutoActivationEnabled(enabled);
      }
    } 
  }


  
  public void setAutoCompleteEnabled(boolean enabled) {
    if (enabled != this.autoCompleteEnabled) {
      this.autoCompleteEnabled = enabled;
      for (AutoCompletion ac : this.textAreaToAutoCompletion.values()) {
        ac.setAutoCompleteEnabled(enabled);
      }
    } 
  }


  
  public void setDefaultCompletionCellRenderer(ListCellRenderer<Object> r) {
    if (r == null) {
      r = createDefaultCompletionCellRenderer();
    }
    if (r instanceof CompletionCellRenderer && 
      Util.getUseSubstanceRenderers() && 
      UIManager.getLookAndFeel().getClass().getName()
      .contains(".Substance")) {
      CompletionCellRenderer ccr = (CompletionCellRenderer)r;
      delegateToSubstanceRenderer(ccr);
    } 
    
    this.renderer = r;
  }


  
  public void setParameterAssistanceEnabled(boolean enabled) {
    if (enabled != this.parameterAssistanceEnabled) {
      this.parameterAssistanceEnabled = enabled;
      for (AutoCompletion ac : this.textAreaToAutoCompletion.values()) {
        ac.setParameterAssistanceEnabled(enabled);
      }
    } 
  }


  
  public void setShowDescWindow(boolean show) {
    if (show != this.showDescWindow) {
      this.showDescWindow = show;
      for (AutoCompletion ac : this.textAreaToAutoCompletion.values()) {
        ac.setShowDescWindow(show);
      }
    } 
  }










  
  protected void uninstallImpl(RSyntaxTextArea textArea) {
    AutoCompletion ac = getAutoCompletionFor(textArea);
    if (ac != null) {
      ac.uninstall();
    }
    this.textAreaToAutoCompletion.remove(textArea);
  }
}
