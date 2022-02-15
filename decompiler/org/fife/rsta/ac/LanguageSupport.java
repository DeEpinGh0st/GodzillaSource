package org.fife.rsta.ac;

import javax.swing.ListCellRenderer;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

public interface LanguageSupport {
  public static final String PROPERTY_LANGUAGE_PARSER = "org.fife.rsta.ac.LanguageSupport.LanguageParser";
  
  int getAutoActivationDelay();
  
  ListCellRenderer<Object> getDefaultCompletionCellRenderer();
  
  boolean getShowDescWindow();
  
  boolean isAutoActivationEnabled();
  
  boolean isAutoCompleteEnabled();
  
  void install(RSyntaxTextArea paramRSyntaxTextArea);
  
  boolean isParameterAssistanceEnabled();
  
  void setAutoActivationDelay(int paramInt);
  
  void setAutoActivationEnabled(boolean paramBoolean);
  
  void setAutoCompleteEnabled(boolean paramBoolean);
  
  void setDefaultCompletionCellRenderer(ListCellRenderer<Object> paramListCellRenderer);
  
  void setParameterAssistanceEnabled(boolean paramBoolean);
  
  void setShowDescWindow(boolean paramBoolean);
  
  void uninstall(RSyntaxTextArea paramRSyntaxTextArea);
}
