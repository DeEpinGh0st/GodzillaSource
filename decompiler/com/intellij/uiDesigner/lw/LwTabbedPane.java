package com.intellij.uiDesigner.lw;

import java.awt.LayoutManager;
import org.jdom.Element;



















public final class LwTabbedPane
  extends LwContainer
  implements ITabbedPane
{
  public LwTabbedPane(String className) {
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
    Element tabbedPaneChild = LwXmlReader.getRequiredChild(constraintsElement, "tabbedpane");
    
    StringDescriptor descriptor = LwXmlReader.getStringDescriptor(tabbedPaneChild, "title", "title-resource-bundle", "title-key");


    
    if (descriptor == null) {
      throw new IllegalArgumentException("String descriptor value required");
    }
    Constraints constraints = new Constraints(descriptor);
    
    Element tooltipElement = LwXmlReader.getChild(tabbedPaneChild, "tooltip");
    if (tooltipElement != null) {
      constraints.myToolTip = LwXmlReader.getStringDescriptor(tooltipElement, "value", "resource-bundle", "key");
    }



    
    String icon = tabbedPaneChild.getAttributeValue("icon");
    if (icon != null) {
      constraints.myIcon = new IconDescriptor(icon);
    }
    icon = tabbedPaneChild.getAttributeValue("disabled-icon");
    if (icon != null) {
      constraints.myDisabledIcon = new IconDescriptor(icon);
    }
    constraints.myEnabled = LwXmlReader.getOptionalBoolean(tabbedPaneChild, "enabled", true);
    
    component.setCustomLayoutConstraints(constraints);
  }

  
  public static final class Constraints
  {
    public StringDescriptor myTitle;
    
    public StringDescriptor myToolTip;
    public IconDescriptor myIcon;
    public IconDescriptor myDisabledIcon;
    public boolean myEnabled = true;
    
    public Constraints(StringDescriptor title) {
      if (title == null) {
        throw new IllegalArgumentException("title cannot be null");
      }
      this.myTitle = title;
    }
    
    public StringDescriptor getProperty(String propName) {
      if (propName.equals("Tab Title")) {
        return this.myTitle;
      }
      if (propName.equals("Tab Tooltip")) {
        return this.myToolTip;
      }
      throw new IllegalArgumentException("Unknown property name " + propName);
    }
  }
  
  public StringDescriptor getTabProperty(IComponent component, String propName) {
    LwComponent lwComponent = (LwComponent)component;
    Constraints constraints = (Constraints)lwComponent.getCustomLayoutConstraints();
    if (constraints == null) {
      return null;
    }
    return constraints.getProperty(propName);
  }
  
  public boolean areChildrenExclusive() {
    return true;
  }
}
