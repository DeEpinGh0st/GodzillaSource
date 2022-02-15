package org.fife.ui.rsyntaxtextarea.folding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import org.fife.ui.rsyntaxtextarea.parser.Parser;
import org.fife.ui.rtextarea.RDocument;
import org.fife.ui.rtextarea.RTextArea;
































public class DefaultFoldManager
  implements FoldManager
{
  private RSyntaxTextArea textArea;
  private Parser rstaParser;
  private FoldParser foldParser;
  private List<Fold> folds;
  private boolean codeFoldingEnabled;
  private PropertyChangeSupport support;
  private Listener l;
  
  public DefaultFoldManager(RSyntaxTextArea textArea) {
    this.textArea = textArea;
    this.support = new PropertyChangeSupport(this);
    this.l = new Listener();
    textArea.getDocument().addDocumentListener(this.l);
    textArea.addPropertyChangeListener("RSTA.syntaxStyle", this.l);
    textArea.addPropertyChangeListener("document", this.l);
    this.folds = new ArrayList<>();
    updateFoldParser();
  }


  
  public void addPropertyChangeListener(PropertyChangeListener l) {
    this.support.addPropertyChangeListener(l);
  }


  
  public void clear() {
    this.folds.clear();
  }



  
  public boolean ensureOffsetNotInClosedFold(int offs) {
    boolean foldsOpened = false;
    Fold fold = getDeepestFoldContaining(offs);
    
    while (fold != null) {
      if (fold.isCollapsed()) {
        fold.setCollapsed(false);
        foldsOpened = true;
      } 
      fold = fold.getParent();
    } 
    
    if (foldsOpened) {
      RSyntaxUtilities.possiblyRepaintGutter((RTextArea)this.textArea);
    }
    
    return foldsOpened;
  }



  
  public Fold getDeepestFoldContaining(int offs) {
    Fold deepestFold = null;
    if (offs > -1) {
      for (int i = 0; i < this.folds.size(); i++) {
        Fold fold = getFold(i);
        if (fold.containsOffset(offs)) {
          deepestFold = fold.getDeepestFoldContaining(offs);
          break;
        } 
      } 
    }
    return deepestFold;
  }



  
  public Fold getDeepestOpenFoldContaining(int offs) {
    Fold deepestFold = null;
    
    if (offs > -1) {
      for (int i = 0; i < this.folds.size(); i++) {
        Fold fold = getFold(i);
        if (fold.containsOffset(offs)) {
          if (fold.isCollapsed()) {
            return null;
          }
          deepestFold = fold.getDeepestOpenFoldContaining(offs);
          
          break;
        } 
      } 
    }
    return deepestFold;
  }



  
  public Fold getFold(int index) {
    return this.folds.get(index);
  }


  
  public int getFoldCount() {
    return this.folds.size();
  }


  
  public Fold getFoldForLine(int line) {
    return getFoldForLineImpl(null, this.folds, line);
  }


  
  private Fold getFoldForLineImpl(Fold parent, List<Fold> folds, int line) {
    int low = 0;
    int high = folds.size() - 1;
    
    while (low <= high) {
      int mid = low + high >> 1;
      Fold midFold = folds.get(mid);
      int startLine = midFold.getStartLine();
      if (line == startLine) {
        return midFold;
      }
      if (line < startLine) {
        high = mid - 1;
        continue;
      } 
      int endLine = midFold.getEndLine();
      if (line >= endLine) {
        low = mid + 1;
        continue;
      } 
      List<Fold> children = midFold.getChildren();
      return (children != null) ? getFoldForLineImpl(midFold, children, line) : null;
    } 


    
    return null;
  }


  
  public int getHiddenLineCount() {
    int count = 0;
    for (Fold fold : this.folds) {
      count += fold.getCollapsedLineCount();
    }
    return count;
  }


  
  public int getHiddenLineCountAbove(int line) {
    return getHiddenLineCountAbove(line, false);
  }



  
  public int getHiddenLineCountAbove(int line, boolean physical) {
    int count = 0;
    
    for (Fold fold : this.folds) {
      int comp = physical ? (line + count) : line;
      if (fold.getStartLine() >= comp) {
        break;
      }
      count += getHiddenLineCountAboveImpl(fold, comp, physical);
    } 
    
    return count;
  }





















  
  private int getHiddenLineCountAboveImpl(Fold fold, int line, boolean physical) {
    int count = 0;
    
    if (fold.getEndLine() < line || (fold
      .isCollapsed() && fold.getStartLine() < line)) {
      count = fold.getCollapsedLineCount();
    } else {
      
      int childCount = fold.getChildCount();
      for (int i = 0; i < childCount; i++) {
        Fold child = fold.getChild(i);
        int comp = physical ? (line + count) : line;
        if (child.getStartLine() >= comp) {
          break;
        }
        count += getHiddenLineCountAboveImpl(child, comp, physical);
      } 
    } 
    
    return count;
  }




  
  public int getLastVisibleLine() {
    int lastLine = this.textArea.getLineCount() - 1;
    
    if (isCodeFoldingSupportedAndEnabled()) {
      int foldCount = getFoldCount();
      if (foldCount > 0) {
        Fold lastFold = getFold(foldCount - 1);
        if (lastFold.containsLine(lastLine)) {
          if (lastFold.isCollapsed()) {
            lastLine = lastFold.getStartLine();
          } else {
            
            while (lastFold.getHasChildFolds()) {
              lastFold = lastFold.getLastChild();
              if (lastFold.containsLine(lastLine)) {
                if (lastFold.isCollapsed()) {
                  lastLine = lastFold.getStartLine();

                  
                  break;
                } 
              }
            } 
          } 
        }
      } 
    } 

    
    return lastLine;
  }




  
  public int getVisibleLineAbove(int line) {
    if (line <= 0 || line >= this.textArea.getLineCount()) {
      return -1;
    }
    
    do {
      line--;
    } while (line >= 0 && isLineHidden(line));
    
    return line;
  }




  
  public int getVisibleLineBelow(int line) {
    int lineCount = this.textArea.getLineCount();
    if (line < 0 || line >= lineCount - 1) {
      return -1;
    }
    
    do {
      line++;
    } while (line < lineCount && isLineHidden(line));
    
    return (line == lineCount) ? -1 : line;
  }





























  
  public boolean isCodeFoldingEnabled() {
    return this.codeFoldingEnabled;
  }


  
  public boolean isCodeFoldingSupportedAndEnabled() {
    return (this.codeFoldingEnabled && this.foldParser != null);
  }


  
  public boolean isFoldStartLine(int line) {
    return (getFoldForLine(line) != null);
  }


  
  public boolean isLineHidden(int line) {
    for (Fold fold : this.folds) {
      if (fold.containsLine(line)) {
        if (fold.isCollapsed()) {
          return true;
        }
        
        return isLineHiddenImpl(fold, line);
      } 
    } 
    
    return false;
  }

  
  private boolean isLineHiddenImpl(Fold parent, int line) {
    for (int i = 0; i < parent.getChildCount(); i++) {
      Fold child = parent.getChild(i);
      if (child.containsLine(line)) {
        if (child.isCollapsed()) {
          return true;
        }
        
        return isLineHiddenImpl(child, line);
      } 
    } 
    
    return false;
  }

  
  private void keepFoldState(Fold newFold, List<Fold> oldFolds) {
    int previousLoc = Collections.binarySearch((List)oldFolds, newFold);
    
    if (previousLoc >= 0) {
      Fold prevFold = oldFolds.get(previousLoc);
      newFold.setCollapsed(prevFold.isCollapsed());
    }
    else {
      
      int insertionPoint = -(previousLoc + 1);
      if (insertionPoint > 0) {
        Fold possibleParentFold = oldFolds.get(insertionPoint - 1);
        if (possibleParentFold.containsOffset(newFold
            .getStartOffset())) {
          List<Fold> children = possibleParentFold.getChildren();
          if (children != null) {
            keepFoldState(newFold, children);
          }
        } 
      } 
    } 
  }

  
  private void keepFoldStates(List<Fold> newFolds, List<Fold> oldFolds) {
    for (Fold newFold : newFolds) {
      keepFoldState(newFold, this.folds);
      List<Fold> newChildFolds = newFold.getChildren();
      if (newChildFolds != null) {
        keepFoldStates(newChildFolds, oldFolds);
      }
    } 
  }


  
  public void removePropertyChangeListener(PropertyChangeListener l) {
    this.support.removePropertyChangeListener(l);
  }



  
  public void reparse() {
    if (this.codeFoldingEnabled && this.foldParser != null) {


      
      List<Fold> newFolds = this.foldParser.getFolds(this.textArea);
      if (newFolds == null) {
        newFolds = Collections.emptyList();
      } else {
        
        keepFoldStates(newFolds, this.folds);
      } 
      this.folds = newFolds;

      
      this.support.firePropertyChange("FoldsUpdated", (Object)null, this.folds);
      this.textArea.repaint();
    }
    else {
      
      this.folds.clear();
    } 
  }



  
  public void setCodeFoldingEnabled(boolean enabled) {
    if (enabled != this.codeFoldingEnabled) {
      this.codeFoldingEnabled = enabled;
      if (this.rstaParser != null) {
        this.textArea.removeParser(this.rstaParser);
      }
      if (enabled) {
        this.rstaParser = (Parser)new AbstractParser()
          {
            public ParseResult parse(RSyntaxDocument doc, String style) {
              DefaultFoldManager.this.reparse();
              return (ParseResult)new DefaultParseResult((Parser)this);
            }
          };
        this.textArea.addParser(this.rstaParser);
        this.support.firePropertyChange("FoldsUpdated", (Object)null, (Object)null);
      }
      else {
        
        this.folds = Collections.emptyList();
        this.textArea.repaint();
        this.support.firePropertyChange("FoldsUpdated", (Object)null, (Object)null);
      } 
    } 
  }


  
  public void setFolds(List<Fold> folds) {
    this.folds = folds;
  }





  
  private void updateFoldParser() {
    this.foldParser = FoldParserManager.get().getFoldParser(this.textArea
        .getSyntaxEditingStyle());
  }



  
  private class Listener
    implements DocumentListener, PropertyChangeListener
  {
    private Listener() {}


    
    public void changedUpdate(DocumentEvent e) {}


    
    public void insertUpdate(DocumentEvent e) {
      int startOffs = e.getOffset();
      int endOffs = startOffs + e.getLength();
      Document doc = e.getDocument();
      Element root = doc.getDefaultRootElement();
      int startLine = root.getElementIndex(startOffs);
      int endLine = root.getElementIndex(endOffs);
      if (startLine != endLine) {
        Fold fold = DefaultFoldManager.this.getFoldForLine(startLine);
        if (fold != null && fold.isCollapsed()) {
          fold.toggleCollapsedState();
        }
      } 
    }


    
    public void propertyChange(PropertyChangeEvent e) {
      String name = e.getPropertyName();
      
      if ("RSTA.syntaxStyle".equals(name)) {
        
        DefaultFoldManager.this.updateFoldParser();
        DefaultFoldManager.this.reparse();
      
      }
      else if ("document".equals(name)) {
        
        RDocument old = (RDocument)e.getOldValue();
        if (old != null) {
          old.removeDocumentListener(this);
        }
        RDocument newDoc = (RDocument)e.getNewValue();
        if (newDoc != null) {
          newDoc.addDocumentListener(this);
        }
        DefaultFoldManager.this.reparse();
      } 
    }





    
    public void removeUpdate(DocumentEvent e) {
      int offs = e.getOffset();
      try {
        int lastLineModified = DefaultFoldManager.this.textArea.getLineOfOffset(offs);
        
        Fold fold = DefaultFoldManager.this.getFoldForLine(lastLineModified);
        
        if (fold != null && fold.isCollapsed()) {
          fold.toggleCollapsedState();
        }
      } catch (BadLocationException ble) {
        ble.printStackTrace();
      } 
    }
  }
}
