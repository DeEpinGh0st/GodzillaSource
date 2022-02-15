package org.fife.ui.rsyntaxtextarea.folding;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Position;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;



























public class Fold
  implements Comparable<Fold>
{
  private int type;
  private RSyntaxTextArea textArea;
  private Position startOffs;
  private Position endOffs;
  private Fold parent;
  private List<Fold> children;
  private boolean collapsed;
  private int childCollapsedLineCount;
  private int lastStartOffs = -1;
  
  private int cachedStartLine;
  private int lastEndOffs = -1;
  
  private int cachedEndLine;

  
  public Fold(int type, RSyntaxTextArea textArea, int startOffs) throws BadLocationException {
    this.type = type;
    this.textArea = textArea;
    this.startOffs = textArea.getDocument().createPosition(startOffs);
  }










  
  public Fold createChild(int type, int startOffs) throws BadLocationException {
    Fold child = new Fold(type, this.textArea, startOffs);
    child.parent = this;
    if (this.children == null) {
      this.children = new ArrayList<>();
    }
    this.children.add(child);
    return child;
  }








  
  public int compareTo(Fold otherFold) {
    int result = -1;
    if (otherFold != null) {
      result = this.startOffs.getOffset() - otherFold.startOffs.getOffset();
    }
    
    return result;
  }












  
  public boolean containsLine(int line) {
    return (line > getStartLine() && line <= getEndLine());
  }









  
  public boolean containsOrStartsOnLine(int line) {
    return (line >= getStartLine() && line <= getEndLine());
  }










  
  public boolean containsOffset(int offs) {
    boolean contained = false;
    if (offs > getStartOffset()) {
      
      Element root = this.textArea.getDocument().getDefaultRootElement();
      int line = root.getElementIndex(offs);
      contained = (line <= getEndLine());
    } 
    return contained;
  }









  
  public boolean equals(Object otherFold) {
    return (otherFold instanceof Fold && compareTo((Fold)otherFold) == 0);
  }








  
  public Fold getChild(int index) {
    return this.children.get(index);
  }







  
  public int getChildCount() {
    return (this.children == null) ? 0 : this.children.size();
  }







  
  List<Fold> getChildren() {
    return this.children;
  }












  
  public int getCollapsedLineCount() {
    return this.collapsed ? getLineCount() : this.childCollapsedLineCount;
  }











  
  Fold getDeepestFoldContaining(int offs) {
    Fold deepestFold = this;
    for (int i = 0; i < getChildCount(); i++) {
      Fold fold = getChild(i);
      if (fold.containsOffset(offs)) {
        deepestFold = fold.getDeepestFoldContaining(offs);
        break;
      } 
    } 
    return deepestFold;
  }












  
  Fold getDeepestOpenFoldContaining(int offs) {
    Fold deepestFold = this;
    
    for (int i = 0; i < getChildCount(); i++) {
      Fold fold = getChild(i);
      if (fold.containsOffset(offs)) {
        if (fold.isCollapsed()) {
          break;
        }
        deepestFold = fold.getDeepestOpenFoldContaining(offs);
        
        break;
      } 
    } 
    return deepestFold;
  }















  
  public int getEndLine() {
    int endOffs = getEndOffset();
    if (this.lastEndOffs == endOffs) {
      return this.cachedEndLine;
    }
    this.lastEndOffs = endOffs;
    Element root = this.textArea.getDocument().getDefaultRootElement();
    return this.cachedEndLine = root.getElementIndex(endOffs);
  }
















  
  public int getEndOffset() {
    return (this.endOffs != null) ? this.endOffs.getOffset() : Integer.MAX_VALUE;
  }







  
  public int getFoldType() {
    return this.type;
  }







  
  public boolean getHasChildFolds() {
    return (getChildCount() > 0);
  }









  
  public Fold getLastChild() {
    int childCount = getChildCount();
    return (childCount == 0) ? null : getChild(childCount - 1);
  }









  
  public int getLineCount() {
    return getEndLine() - getStartLine();
  }







  
  public Fold getParent() {
    return this.parent;
  }













  
  public int getStartLine() {
    int startOffs = getStartOffset();
    if (this.lastStartOffs == startOffs) {
      return this.cachedStartLine;
    }
    this.lastStartOffs = startOffs;
    Element root = this.textArea.getDocument().getDefaultRootElement();
    return this.cachedStartLine = root.getElementIndex(startOffs);
  }














  
  public int getStartOffset() {
    return this.startOffs.getOffset();
  }


  
  public int hashCode() {
    return getStartLine();
  }








  
  public boolean isCollapsed() {
    return this.collapsed;
  }









  
  public boolean isOnSingleLine() {
    return (getStartLine() == getEndLine());
  }









  
  public boolean removeFromParent() {
    if (this.parent != null) {
      this.parent.removeMostRecentChild();
      this.parent = null;
      return true;
    } 
    return false;
  }

  
  private void removeMostRecentChild() {
    this.children.remove(this.children.size() - 1);
  }










  
  public void setCollapsed(boolean collapsed) {
    if (collapsed != this.collapsed) {

      
      int lineCount = getLineCount();
      int linesToCollapse = lineCount - this.childCollapsedLineCount;
      if (!collapsed) {
        linesToCollapse = -linesToCollapse;
      }

      
      this.collapsed = collapsed;
      if (this.parent != null) {
        this.parent.updateChildCollapsedLineCount(linesToCollapse);
      }


      
      if (collapsed) {
        int dot = this.textArea.getSelectionStart();
        Element root = this.textArea.getDocument().getDefaultRootElement();
        int dotLine = root.getElementIndex(dot);
        boolean updateCaret = containsLine(dotLine);
        if (!updateCaret) {
          int mark = this.textArea.getSelectionEnd();
          if (mark != dot) {
            int markLine = root.getElementIndex(mark);
            updateCaret = containsLine(markLine);
          } 
        } 
        if (updateCaret) {
          dot = root.getElement(getStartLine()).getEndOffset() - 1;
          this.textArea.setCaretPosition(dot);
        } 
      } 
      
      this.textArea.foldToggled(this);
    } 
  }














  
  public void setEndOffset(int endOffs) throws BadLocationException {
    this.endOffs = this.textArea.getDocument().createPosition(endOffs);
  }






  
  public void toggleCollapsedState() {
    setCollapsed(!this.collapsed);
  }

  
  private void updateChildCollapsedLineCount(int count) {
    this.childCollapsedLineCount += count;


    
    if (!this.collapsed && this.parent != null) {
      this.parent.updateChildCollapsedLineCount(count);
    }
  }







  
  public String toString() {
    return "[Fold: startOffs=" + 
      getStartOffset() + ", endOffs=" + 
      getEndOffset() + ", collapsed=" + this.collapsed + "]";
  }
}
