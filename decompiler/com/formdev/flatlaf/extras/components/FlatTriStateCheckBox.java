package com.formdev.flatlaf.extras.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import javax.swing.JCheckBox;
import javax.swing.JToggleButton;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;














































public class FlatTriStateCheckBox
  extends JCheckBox
{
  private State state;
  
  public enum State
  {
    UNSELECTED, INDETERMINATE, SELECTED;
  }
  
  private boolean allowIndeterminate = true;
  private boolean altStateCycleOrder = UIManager.getBoolean("FlatTriStateCheckBox.altStateCycleOrder");
  
  public FlatTriStateCheckBox() {
    this((String)null);
  }
  
  public FlatTriStateCheckBox(String text) {
    this(text, State.INDETERMINATE);
  }
  
  public FlatTriStateCheckBox(String text, State initialState) {
    super(text);
    
    setModel(new JToggleButton.ToggleButtonModel()
        {
          public boolean isSelected() {
            return (FlatTriStateCheckBox.this.state != FlatTriStateCheckBox.State.UNSELECTED);
          }

          
          public void setSelected(boolean b) {
            FlatTriStateCheckBox.this.setState(FlatTriStateCheckBox.this.nextState(FlatTriStateCheckBox.this.state));
            
            fireStateChanged();
            fireItemStateChanged(new ItemEvent(this, 701, this, 
                  isSelected() ? 1 : 2));
          }
        });
    
    setState(initialState);
  }






  
  public State getState() {
    return this.state;
  }



  
  public void setState(State state) {
    if (this.state == state) {
      return;
    }
    State oldState = this.state;
    this.state = state;
    
    putClientProperty("JButton.selectedState", (state == State.INDETERMINATE) ? "indeterminate" : null);
    
    firePropertyChange("state", oldState, state);
    repaint();
  }




  
  protected State nextState(State state) {
    if (!this.altStateCycleOrder) {
      
      switch (state)
      { default:
          return this.allowIndeterminate ? State.INDETERMINATE : State.SELECTED;
        case INDETERMINATE: return State.SELECTED;
        case SELECTED: break; }  return State.UNSELECTED;
    } 

    
    switch (state)
    { default:
        return State.SELECTED;
      case INDETERMINATE: return State.UNSELECTED;
      case SELECTED: break; }  return this.allowIndeterminate ? State.INDETERMINATE : State.UNSELECTED;
  }









  
  public Boolean getChecked() {
    switch (this.state)
    { default:
        return Boolean.valueOf(false);
      case INDETERMINATE: return null;
      case SELECTED: break; }  return Boolean.valueOf(true);
  }





  
  public void setChecked(Boolean value) {
    setState((value == null) ? State.INDETERMINATE : (value.booleanValue() ? State.SELECTED : State.UNSELECTED));
  }

  
  public void setSelected(boolean b) {
    setState(b ? State.SELECTED : State.UNSELECTED);
  }



  
  public boolean isIndeterminate() {
    return (this.state == State.INDETERMINATE);
  }



  
  public void setIndeterminate(boolean indeterminate) {
    if (indeterminate) {
      setState(State.INDETERMINATE);
    } else if (this.state == State.INDETERMINATE) {
      setState(State.UNSELECTED);
    } 
  }





  
  public boolean isAllowIndeterminate() {
    return this.allowIndeterminate;
  }






  
  public void setAllowIndeterminate(boolean allowIndeterminate) {
    this.allowIndeterminate = allowIndeterminate;
  }



  
  public boolean isAltStateCycleOrder() {
    return this.altStateCycleOrder;
  }



  
  public void setAltStateCycleOrder(boolean altStateCycleOrder) {
    this.altStateCycleOrder = altStateCycleOrder;
  }

  
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    if (this.state == State.INDETERMINATE && !isIndeterminateStateSupported()) {
      paintIndeterminateState(g);
    }
  }




  
  protected void paintIndeterminateState(Graphics g) {
    g.setColor(Color.magenta);
    g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
  }




  
  protected boolean isIndeterminateStateSupported() {
    LookAndFeel laf = UIManager.getLookAndFeel();
    return (laf instanceof com.formdev.flatlaf.FlatLaf || laf.getClass().getName().equals("com.apple.laf.AquaLookAndFeel"));
  }
}
