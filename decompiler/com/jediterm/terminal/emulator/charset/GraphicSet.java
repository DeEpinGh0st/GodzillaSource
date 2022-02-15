package com.jediterm.terminal.emulator.charset;

import org.jetbrains.annotations.NotNull;





public class GraphicSet
{
  private final int myIndex;
  private CharacterSet myDesignation;
  
  public GraphicSet(int index) {
    if (index < 0 || index > 3)
    {
      throw new IllegalArgumentException("Invalid index!");
    }
    this.myIndex = index;
    
    this.myDesignation = CharacterSet.valueOf((index == 1) ? 48 : 66);
  }




  
  public CharacterSet getDesignation() {
    return this.myDesignation;
  }




  
  public int getIndex() {
    return this.myIndex;
  }











  
  public int map(char original, int index) {
    int result = this.myDesignation.map(index);
    if (result < 0)
    {
      
      result = original;
    }
    return result;
  }




  
  public void setDesignation(@NotNull CharacterSet designation) {
    if (designation == null) $$$reportNull$$$0(0);  this.myDesignation = designation;
  }
}
