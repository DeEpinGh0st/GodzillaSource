package com.intellij.uiDesigner.lw;

import com.intellij.uiDesigner.compiler.AlienFormFileException;
import com.intellij.uiDesigner.compiler.UnexpectedFormElementException;
import java.util.ArrayList;
import java.util.Iterator;
import org.jdom.Element;





















public final class LwRootContainer
  extends LwContainer
  implements IRootContainer
{
  private String myClassToBind;
  private String myMainComponentBinding;
  private ArrayList myButtonGroups = new ArrayList();
  private ArrayList myInspectionSuppressions = new ArrayList();
  
  public LwRootContainer() throws Exception {
    super("javax.swing.JPanel");
    this.myLayoutSerializer = XYLayoutSerializer.INSTANCE;
  }
  
  public String getMainComponentBinding() {
    return this.myMainComponentBinding;
  }
  
  public String getClassToBind() {
    return this.myClassToBind;
  }
  
  public void setClassToBind(String classToBind) {
    this.myClassToBind = classToBind;
  }
  
  public void read(Element element, PropertiesProvider provider) throws Exception {
    if (element == null) {
      throw new IllegalArgumentException("element cannot be null");
    }
    if (!"form".equals(element.getName())) {
      throw new UnexpectedFormElementException("unexpected element: " + element);
    }
    
    if (!"http://www.intellij.com/uidesigner/form/".equals(element.getNamespace().getURI())) {
      throw new AlienFormFileException();
    }
    
    setId("root");
    
    this.myClassToBind = element.getAttributeValue("bind-to-class");

    
    for (Iterator i = element.getChildren().iterator(); i.hasNext(); ) {
      Element child = i.next();
      if (child.getName().equals("buttonGroups")) {
        readButtonGroups(child); continue;
      } 
      if (child.getName().equals("inspectionSuppressions")) {
        readInspectionSuppressions(child);
        continue;
      } 
      LwComponent component = createComponentFromTag(child);
      addComponent(component);
      component.read(child, provider);
    } 

    
    this.myMainComponentBinding = element.getAttributeValue("stored-main-component-binding");
  }
  
  private void readButtonGroups(Element element) {
    for (Iterator i = element.getChildren().iterator(); i.hasNext(); ) {
      Element child = i.next();
      LwButtonGroup group = new LwButtonGroup();
      group.read(child);
      this.myButtonGroups.add(group);
    } 
  }
  
  private void readInspectionSuppressions(Element element) {
    for (Iterator i = element.getChildren().iterator(); i.hasNext(); ) {
      Element child = i.next();
      String inspectionId = LwXmlReader.getRequiredString(child, "inspection");
      String componentId = LwXmlReader.getString(child, "id");
      this.myInspectionSuppressions.add(new LwInspectionSuppression(inspectionId, componentId));
    } 
  }
  
  public IButtonGroup[] getButtonGroups() {
    return (IButtonGroup[])this.myButtonGroups.toArray((Object[])new LwButtonGroup[this.myButtonGroups.size()]);
  }
  
  public String getButtonGroupName(IComponent component) {
    for (int i = 0; i < this.myButtonGroups.size(); i++) {
      LwButtonGroup group = this.myButtonGroups.get(i);
      String[] ids = group.getComponentIds();
      for (int j = 0; j < ids.length; j++) {
        if (ids[j].equals(component.getId())) {
          return group.getName();
        }
      } 
    } 
    return null;
  }
  
  public String[] getButtonGroupComponentIds(String groupName) {
    for (int i = 0; i < this.myButtonGroups.size(); i++) {
      LwButtonGroup group = this.myButtonGroups.get(i);
      if (group.getName().equals(groupName)) {
        return group.getComponentIds();
      }
    } 
    throw new IllegalArgumentException("Cannot find group " + groupName);
  }
  
  public boolean isInspectionSuppressed(String inspectionId, String componentId) {
    for (Iterator iterator = this.myInspectionSuppressions.iterator(); iterator.hasNext(); ) {
      LwInspectionSuppression suppression = iterator.next();
      if ((suppression.getComponentId() == null || suppression.getComponentId().equals(componentId)) && suppression.getInspectionId().equals(inspectionId))
      {
        return true;
      }
    } 
    return false;
  }
  
  public LwInspectionSuppression[] getInspectionSuppressions() {
    return (LwInspectionSuppression[])this.myInspectionSuppressions.toArray((Object[])new LwInspectionSuppression[this.myInspectionSuppressions.size()]);
  }
}
