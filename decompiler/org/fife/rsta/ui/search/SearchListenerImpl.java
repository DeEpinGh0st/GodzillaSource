package org.fife.rsta.ui.search;
import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

public class SearchListenerImpl implements SearchListener {
  public SearchListenerImpl(RSyntaxTextArea textArea) {
    this.textArea = textArea;
  }
  private RSyntaxTextArea textArea;
  public void searchEvent(SearchEvent e) {
    SearchResult result;
    SearchEvent.Type type = e.getType();
    SearchContext context = e.getSearchContext();

    
    switch (type) {
      
      default:
        result = SearchEngine.markAll((RTextArea)this.textArea, context);
        break;
      case FIND:
        result = SearchEngine.find((JTextArea)this.textArea, context);
        if (!result.wasFound() || result.isWrapped()) {
          UIManager.getLookAndFeel().provideErrorFeedback((Component)this.textArea);
        }
        break;
      case REPLACE:
        result = SearchEngine.replace((RTextArea)this.textArea, context);
        if (!result.wasFound() || result.isWrapped()) {
          UIManager.getLookAndFeel().provideErrorFeedback((Component)this.textArea);
        }
        break;
      case REPLACE_ALL:
        result = SearchEngine.replaceAll((RTextArea)this.textArea, context);
        JOptionPane.showMessageDialog(null, result.getCount() + " occurrences replaced.");
        break;
    } 


    
    if (result.wasFound()) {
      String text = "Text found; occurrences marked: " + result.getMarkedCount();
    }
    else if (type == SearchEvent.Type.MARK_ALL) {
      if (result.getMarkedCount() > 0) {
        String text = "Occurrences marked: " + result.getMarkedCount();
      } else {
        
        String text = "";
      } 
    } else {
      
      String text = "Text not found";
    } 
  }



  
  public String getSelectedText() {
    return this.textArea.getSelectedText();
  }
}
