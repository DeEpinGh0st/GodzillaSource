package com.intellij.uiDesigner.lw;

import com.intellij.uiDesigner.core.GridConstraints;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.jdom.Element;











































public abstract class LwComponent
  implements IComponent
{
  private String myId;
  private String myBinding;
  private final String myClassName;
  private LwContainer myParent;
  private final GridConstraints myConstraints;
  private Object myCustomLayoutConstraints;
  private final Rectangle myBounds;
  private final HashMap myIntrospectedProperty2Value;
  private Element myErrorComponentProperties;
  protected final HashMap myClientProperties;
  protected final HashMap myDelegeeClientProperties;
  private boolean myCustomCreate = false;
  private boolean myDefaultBinding = false;
  
  public LwComponent(String className) {
    if (className == null) {
      throw new IllegalArgumentException("className cannot be null");
    }
    this.myBounds = new Rectangle();
    this.myConstraints = new GridConstraints();
    this.myIntrospectedProperty2Value = new HashMap();
    this.myClassName = className;
    this.myClientProperties = new HashMap();
    this.myDelegeeClientProperties = new HashMap();
  }
  
  public final String getId() {
    return this.myId;
  }
  
  public final void setId(String id) {
    if (id == null) {
      throw new IllegalArgumentException("id cannot be null");
    }
    this.myId = id;
  }
  
  public final String getBinding() {
    return this.myBinding;
  }
  
  public final void setBinding(String binding) {
    this.myBinding = binding;
  }
  
  public final Object getCustomLayoutConstraints() {
    return this.myCustomLayoutConstraints;
  }
  
  public final void setCustomLayoutConstraints(Object customLayoutConstraints) {
    this.myCustomLayoutConstraints = customLayoutConstraints;
  }



  
  public final String getComponentClassName() {
    return this.myClassName;
  }
  
  public IProperty[] getModifiedProperties() {
    return (IProperty[])getAssignedIntrospectedProperties();
  }




  
  public final Rectangle getBounds() {
    return (Rectangle)this.myBounds.clone();
  }




  
  public final GridConstraints getConstraints() {
    return this.myConstraints;
  }
  
  public boolean isCustomCreate() {
    return this.myCustomCreate;
  }
  
  public boolean isDefaultBinding() {
    return this.myDefaultBinding;
  }
  
  public boolean accept(ComponentVisitor visitor) {
    return visitor.visit(this);
  }
  
  public boolean areChildrenExclusive() {
    return false;
  }
  
  public final LwContainer getParent() {
    return this.myParent;
  }
  
  public IContainer getParentContainer() {
    return this.myParent;
  }
  
  protected final void setParent(LwContainer parent) {
    this.myParent = parent;
  }
  
  public final void setBounds(Rectangle bounds) {
    this.myBounds.setBounds(bounds);
  }
  
  public final Object getPropertyValue(LwIntrospectedProperty property) {
    return this.myIntrospectedProperty2Value.get(property);
  }
  
  public final void setPropertyValue(LwIntrospectedProperty property, Object value) {
    this.myIntrospectedProperty2Value.put(property, value);
  }




  
  public final Element getErrorComponentProperties() {
    return this.myErrorComponentProperties;
  }
  
  public final LwIntrospectedProperty[] getAssignedIntrospectedProperties() {
    LwIntrospectedProperty[] properties = new LwIntrospectedProperty[this.myIntrospectedProperty2Value.size()];
    Iterator iterator = this.myIntrospectedProperty2Value.keySet().iterator();
    
    for (int i = 0; iterator.hasNext(); i++) {
      properties[i] = iterator.next();
    }
    return properties;
  }




  
  protected final void readBase(Element element) {
    setId(LwXmlReader.getRequiredString(element, "id"));
    setBinding(element.getAttributeValue("binding"));
    this.myCustomCreate = LwXmlReader.getOptionalBoolean(element, "custom-create", false);
    this.myDefaultBinding = LwXmlReader.getOptionalBoolean(element, "default-binding", false);
  }




  
  protected final void readProperties(Element element, PropertiesProvider provider) {
    if (provider == null) {
      return;
    }

    
    Element propertiesElement = LwXmlReader.getChild(element, "properties");
    if (propertiesElement == null) {
      propertiesElement = new Element("properties", element.getNamespace());
    }
    
    HashMap name2property = provider.getLwProperties(getComponentClassName());
    if (name2property == null) {
      this.myErrorComponentProperties = (Element)propertiesElement.clone();
      
      return;
    } 
    List propertyElements = propertiesElement.getChildren();
    for (int i = 0; i < propertyElements.size(); i++) {
      Element t = propertyElements.get(i);
      String name = t.getName();
      LwIntrospectedProperty property = (LwIntrospectedProperty)name2property.get(name);
      if (property != null) {
        
        try {
          
          Object value = property.read(t);
          setPropertyValue(property, value);
        }
        catch (Exception exc) {}
      }
    } 

    
    readClientProperties(element);
  }
  
  private void readClientProperties(Element element) {
    Element propertiesElement = LwXmlReader.getChild(element, "clientProperties");
    if (propertiesElement == null)
      return;  List clientPropertyList = propertiesElement.getChildren();
    for (int i = 0; i < clientPropertyList.size(); i++) {
      LwIntrospectedProperty lwProp; Element prop = clientPropertyList.get(i);
      String propName = prop.getName();
      String className = LwXmlReader.getRequiredString(prop, "class");

      
      if (className.equals(Integer.class.getName())) {
        lwProp = new LwIntroIntProperty(propName);
      }
      else if (className.equals(Boolean.class.getName())) {
        lwProp = new LwIntroBooleanProperty(propName);
      }
      else if (className.equals(Double.class.getName())) {
        lwProp = new LwIntroPrimitiveTypeProperty(propName, Double.class);
      } else {
        Class propClass;
        
        try {
          propClass = Class.forName(className);
        }
        catch (ClassNotFoundException e) {}

        
        lwProp = CompiledClassPropertiesProvider.propertyFromClass(propClass, propName);
      } 
      
      if (lwProp != null) {
        Object value;
        try {
          value = lwProp.read(prop);
        }
        catch (Exception e) {}

        
        this.myDelegeeClientProperties.put(propName, value);
      } 
    } 
  }



  
  protected final void readConstraints(Element element) {
    LwContainer parent = getParent();
    if (parent == null) {
      throw new IllegalStateException("component must be in LW tree: " + this);
    }
    parent.readConstraintsForChild(element, this);
  }








  
  public final Object getClientProperty(Object key) {
    if (key == null) {
      throw new IllegalArgumentException("key cannot be null");
    }
    return this.myClientProperties.get(key);
  }



  
  public final void putClientProperty(Object key, Object value) {
    if (key == null) {
      throw new IllegalArgumentException("key cannot be null");
    }
    this.myClientProperties.put(key, value);
  }
  
  public HashMap getDelegeeClientProperties() {
    return this.myDelegeeClientProperties;
  }
  
  public abstract void read(Element paramElement, PropertiesProvider paramPropertiesProvider) throws Exception;
}
