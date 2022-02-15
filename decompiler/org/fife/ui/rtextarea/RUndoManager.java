package org.fife.ui.rtextarea;

import java.util.ResourceBundle;
import javax.swing.Action;
import javax.swing.UIManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;





























public class RUndoManager
  extends UndoManager
{
  private RCompoundEdit compoundEdit;
  private RTextArea textArea;
  private int lastOffset;
  private String cantUndoText;
  private String cantRedoText;
  private int internalAtomicEditDepth;
  private static final String MSG = "org.fife.ui.rtextarea.RTextArea";
  
  public RUndoManager(RTextArea textArea) {
    this.textArea = textArea;
    ResourceBundle msg = ResourceBundle.getBundle("org.fife.ui.rtextarea.RTextArea");
    this.cantUndoText = msg.getString("Action.CantUndo.Name");
    this.cantRedoText = msg.getString("Action.CantRedo.Name");
  }










  
  public void beginInternalAtomicEdit() {
    if (++this.internalAtomicEditDepth == 1) {
      if (this.compoundEdit != null) {
        this.compoundEdit.end();
      }
      this.compoundEdit = new RCompoundEdit();
    } 
  }






  
  public void endInternalAtomicEdit() {
    if (this.internalAtomicEditDepth > 0 && --this.internalAtomicEditDepth == 0) {
      addEdit(this.compoundEdit);
      this.compoundEdit.end();
      this.compoundEdit = null;
      updateActions();
    } 
  }







  
  public String getCantRedoText() {
    return this.cantRedoText;
  }







  
  public String getCantUndoText() {
    return this.cantUndoText;
  }


  
  public void redo() {
    super.redo();
    updateActions();
  }

  
  private RCompoundEdit startCompoundEdit(UndoableEdit edit) {
    this.lastOffset = this.textArea.getCaretPosition();
    this.compoundEdit = new RCompoundEdit();
    this.compoundEdit.addEdit(edit);
    addEdit(this.compoundEdit);
    return this.compoundEdit;
  }


  
  public void undo() {
    super.undo();
    updateActions();
  }





  
  public void undoableEditHappened(UndoableEditEvent e) {
    if (this.compoundEdit == null) {
      this.compoundEdit = startCompoundEdit(e.getEdit());
      updateActions();
      
      return;
    } 
    if (this.internalAtomicEditDepth > 0) {
      this.compoundEdit.addEdit(e.getEdit());


      
      return;
    } 


    
    int diff = this.textArea.getCaretPosition() - this.lastOffset;

    
    if (Math.abs(diff) <= 1) {
      this.compoundEdit.addEdit(e.getEdit());
      this.lastOffset += diff;


      
      return;
    } 


    
    this.compoundEdit.end();
    this.compoundEdit = startCompoundEdit(e.getEdit());
  }










  
  public void updateActions() {
    Action a = RTextArea.getAction(6);
    if (canUndo()) {
      a.setEnabled(true);
      String text = getUndoPresentationName();
      a.putValue("Name", text);
      a.putValue("ShortDescription", text);
    
    }
    else if (a.isEnabled()) {
      a.setEnabled(false);
      String text = this.cantUndoText;
      a.putValue("Name", text);
      a.putValue("ShortDescription", text);
    } 

    
    a = RTextArea.getAction(4);
    if (canRedo()) {
      a.setEnabled(true);
      String text = getRedoPresentationName();
      a.putValue("Name", text);
      a.putValue("ShortDescription", text);
    
    }
    else if (a.isEnabled()) {
      a.setEnabled(false);
      String text = this.cantRedoText;
      a.putValue("Name", text);
      a.putValue("ShortDescription", text);
    } 
  }





  
  class RCompoundEdit
    extends CompoundEdit
  {
    public String getUndoPresentationName() {
      return UIManager.getString("AbstractUndoableEdit.undoText");
    }

    
    public String getRedoPresentationName() {
      return UIManager.getString("AbstractUndoableEdit.redoText");
    }

    
    public boolean isInProgress() {
      return false;
    }

    
    public void undo() {
      if (RUndoManager.this.compoundEdit != null) {
        RUndoManager.this.compoundEdit.end();
      }
      super.undo();
      RUndoManager.this.compoundEdit = null;
    }
  }
}
