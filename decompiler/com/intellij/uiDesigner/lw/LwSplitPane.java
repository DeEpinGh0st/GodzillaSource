package com.intellij.uiDesigner.lw;

import com.intellij.uiDesigner.compiler.UnexpectedFormElementException;
import java.awt.LayoutManager;
import org.jdom.Element;



















public final class LwSplitPane
  extends LwContainer
{
  public static final String POSITION_LEFT = "left";
  public static final String POSITION_RIGHT = "right";
  
  public LwSplitPane(String className) {
    super(className);
  }
  
  protected LayoutManager createInitialLayout() {
    return null;
  }
  
  public void read(Element element, PropertiesProvider provider) throws Exception {
    readNoLayout(element, provider);
  }
  
  protected void readConstraintsForChild(Element element, LwComponent component) {
    Element constraintsElement = LwXmlReader.getRequiredChild(element, "constraints");
    Element splitterChild = LwXmlReader.getRequiredChild(constraintsElement, "splitpane");
    String position = LwXmlReader.getRequiredString(splitterChild, "position");
    if ("left".equals(position)) {
      component.setCustomLayoutConstraints("left");
    }
    else if ("right".equals(position)) {
      component.setCustomLayoutConstraints("right");
    } else {
      
      throw new UnexpectedFormElementException("unexpected position: " + position);
    } 
  }
}
