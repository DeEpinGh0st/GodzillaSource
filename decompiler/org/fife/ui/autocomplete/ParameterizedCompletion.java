package org.fife.ui.autocomplete;

import javax.swing.text.JTextComponent;



































































public interface ParameterizedCompletion
  extends Completion
{
  String getDefinitionString();
  
  Parameter getParam(int paramInt);
  
  int getParamCount();
  
  ParameterizedCompletionInsertionInfo getInsertionInfo(JTextComponent paramJTextComponent, boolean paramBoolean);
  
  boolean getShowParameterToolTip();
  
  public static class Parameter
  {
    private String name;
    private Object type;
    private String desc;
    private boolean isEndParam;
    
    public Parameter(Object type, String name) {
      this(type, name, false);
    }

















    
    public Parameter(Object type, String name, boolean endParam) {
      this.name = name;
      this.type = type;
      this.isEndParam = endParam;
    }
    
    public String getDescription() {
      return this.desc;
    }
    
    public String getName() {
      return this.name;
    }





    
    public String getType() {
      return (this.type == null) ? null : this.type.toString();
    }





    
    public Object getTypeObject() {
      return this.type;
    }







    
    public boolean isEndParam() {
      return this.isEndParam;
    }
    
    public void setDescription(String desc) {
      this.desc = desc;
    }

    
    public String toString() {
      StringBuilder sb = new StringBuilder();
      if (getType() != null) {
        sb.append(getType());
      }
      if (getName() != null) {
        if (getType() != null) {
          sb.append(' ');
        }
        sb.append(getName());
      } 
      return sb.toString();
    }
  }
}
