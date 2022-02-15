package org.fife.ui.autocomplete;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.Position;
import org.fife.ui.rsyntaxtextarea.DocumentRange;




























public class ParameterizedCompletionInsertionInfo
{
  private int minOffs;
  private Position maxOffs;
  private int defaultEnd = -1;
  private int selStart;
  private int selEnd;
  
  public void addReplacementCopy(String id, int start, int end) {
    if (this.replacementCopies == null) {
      this.replacementCopies = new ArrayList<>(1);
    }
    this.replacementCopies.add(new ReplacementCopy(id, start, end));
  }


  
  private String textToInsert;

  
  private List<DocumentRange> replacementLocations;
  
  private List<ReplacementCopy> replacementCopies;

  
  public void addReplacementLocation(int start, int end) {
    if (this.replacementLocations == null) {
      this.replacementLocations = new ArrayList<>(1);
    }
    this.replacementLocations.add(new DocumentRange(start, end));
  }

  
  public int getDefaultEndOffs() {
    return (this.defaultEnd > -1) ? this.defaultEnd : getMaxOffset().getOffset();
  }








  
  public Position getMaxOffset() {
    return this.maxOffs;
  }








  
  public int getMinOffset() {
    return this.minOffs;
  }

  
  public int getReplacementCopyCount() {
    return (this.replacementCopies == null) ? 0 : this.replacementCopies.size();
  }






  
  public int getReplacementCount() {
    return (this.replacementLocations == null) ? 0 : this.replacementLocations.size();
  }

  
  public ReplacementCopy getReplacementCopy(int index) {
    return this.replacementCopies.get(index);
  }









  
  public DocumentRange getReplacementLocation(int index) {
    return this.replacementLocations.get(index);
  }









  
  public int getSelectionEnd() {
    return this.selEnd;
  }









  
  public int getSelectionStart() {
    return this.selStart;
  }







  
  public String getTextToInsert() {
    return this.textToInsert;
  }







  
  public boolean hasSelection() {
    return (this.selEnd != this.selStart);
  }









  
  public void setInitialSelection(int selStart, int selEnd) {
    this.selStart = selStart;
    this.selEnd = selEnd;
  }











  
  public void setCaretRange(int minOffs, Position maxOffs) {
    this.minOffs = minOffs;
    this.maxOffs = maxOffs;
  }

  
  public void setDefaultEndOffs(int end) {
    this.defaultEnd = end;
  }







  
  public void setTextToInsert(String text) {
    this.textToInsert = text;
  }

  
  public static class ReplacementCopy
  {
    private String id;
    
    private int start;
    
    private int end;

    
    ReplacementCopy(String id, int start, int end) {
      this.id = id;
      this.start = start;
      this.end = end;
    }
    
    public int getEnd() {
      return this.end;
    }
    
    public String getId() {
      return this.id;
    }
    
    public int getStart() {
      return this.start;
    }
  }
}
