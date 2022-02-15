package com.jediterm.terminal.emulator.charset;

import org.jetbrains.annotations.NotNull;












public class GraphicSetState
{
  private final GraphicSet[] myGraphicSets = new GraphicSet[4]; public GraphicSetState() {
    for (int i = 0; i < this.myGraphicSets.length; i++) {
      this.myGraphicSets[i] = new GraphicSet(i);
    }
    
    resetState();
  }

  
  private GraphicSet myGL;
  
  private GraphicSet myGR;
  private GraphicSet myGlOverride;
  
  public void designateGraphicSet(@NotNull GraphicSet graphicSet, char designator) {
    if (graphicSet == null) $$$reportNull$$$0(0);  graphicSet.setDesignation(CharacterSet.valueOf(designator));
  }

  
  public void designateGraphicSet(int num, CharacterSet characterSet) {
    getGraphicSet(num).setDesignation(characterSet);
  }



  
  @NotNull
  public GraphicSet getGL() {
    GraphicSet result = this.myGL;
    if (this.myGlOverride != null) {
      result = this.myGlOverride;
      this.myGlOverride = null;
    } 
    if (result == null) $$$reportNull$$$0(1);  return result;
  }



  
  @NotNull
  public GraphicSet getGR() {
    if (this.myGR == null) $$$reportNull$$$0(2);  return this.myGR;
  }





  
  @NotNull
  public GraphicSet getGraphicSet(int index) {
    if (this.myGraphicSets[index % 4] == null) $$$reportNull$$$0(3);  return this.myGraphicSets[index % 4];
  }






  
  public char map(char ch) {
    return CharacterSets.getChar(ch, getGL(), getGR());
  }





  
  public void overrideGL(int index) {
    this.myGlOverride = getGraphicSet(index);
  }



  
  public void resetState() {
    for (int i = 0; i < this.myGraphicSets.length; i++) {
      this.myGraphicSets[i].setDesignation(CharacterSet.valueOf((i == 1) ? 48 : 66));
    }
    this.myGL = this.myGraphicSets[0];
    this.myGR = this.myGraphicSets[1];
    this.myGlOverride = null;
  }





  
  public void setGL(int index) {
    this.myGL = getGraphicSet(index);
  }





  
  public void setGR(int index) {
    this.myGR = getGraphicSet(index);
  }

  
  public int getGLOverrideIndex() {
    return (this.myGlOverride != null) ? this.myGlOverride.getIndex() : -1;
  }
}
