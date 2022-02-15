package org.mozilla.javascript.xmlimpl;

import java.util.ArrayList;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.xml.XMLObject;

class XMLList
  extends XMLObjectImpl implements Function {
  static final long serialVersionUID = -4543618751670781135L;
  private XmlNode.InternalList _annos;
  private XMLObjectImpl targetObject = null;
  private XmlNode.QName targetProperty = null;
  
  XMLList(XMLLibImpl lib, Scriptable scope, XMLObject prototype) {
    super(lib, scope, prototype);
    this._annos = new XmlNode.InternalList();
  }

  
  XmlNode.InternalList getNodeList() {
    return this._annos;
  }

  
  void setTargets(XMLObjectImpl object, XmlNode.QName property) {
    this.targetObject = object;
    this.targetProperty = property;
  }

  
  private XML getXmlFromAnnotation(int index) {
    return getXML(this._annos, index);
  }

  
  XML getXML() {
    if (length() == 1) return getXmlFromAnnotation(0); 
    return null;
  }
  
  private void internalRemoveFromList(int index) {
    this._annos.remove(index);
  }
  
  void replace(int index, XML xml) {
    if (index < length()) {
      XmlNode.InternalList newAnnoList = new XmlNode.InternalList();
      newAnnoList.add(this._annos, 0, index);
      newAnnoList.add(xml);
      newAnnoList.add(this._annos, index + 1, length());
      this._annos = newAnnoList;
    } 
  }
  
  private void insert(int index, XML xml) {
    if (index < length()) {
      XmlNode.InternalList newAnnoList = new XmlNode.InternalList();
      newAnnoList.add(this._annos, 0, index);
      newAnnoList.add(xml);
      newAnnoList.add(this._annos, index, length());
      this._annos = newAnnoList;
    } 
  }







  
  public String getClassName() {
    return "XMLList";
  }









  
  public Object get(int index, Scriptable start) {
    if (index >= 0 && index < length()) {
      return getXmlFromAnnotation(index);
    }
    return Scriptable.NOT_FOUND;
  }



  
  boolean hasXMLProperty(XMLName xmlName) {
    return (getPropertyList(xmlName).length() > 0);
  }

  
  public boolean has(int index, Scriptable start) {
    return (0 <= index && index < length());
  }




  
  void putXMLProperty(XMLName xmlName, Object value) {
    if (value == null) {
      value = "null";
    } else if (value instanceof Undefined) {
      value = "undefined";
    } 
    
    if (length() > 1) {
      throw ScriptRuntime.typeError("Assignment to lists with more than one item is not supported");
    }
    if (length() == 0) {

      
      if (this.targetObject != null && this.targetProperty != null && this.targetProperty.getLocalName() != null && this.targetProperty.getLocalName().length() > 0) {




        
        XML xmlValue = newTextElementXML(null, this.targetProperty, null);
        addToList(xmlValue);
        
        if (xmlName.isAttributeName()) {
          setAttribute(xmlName, value);
        } else {
          XML xml = item(0);
          xml.putXMLProperty(xmlName, value);

          
          replace(0, item(0));
        } 

        
        XMLName name2 = XMLName.formProperty(this.targetProperty.getNamespace().getUri(), this.targetProperty.getLocalName());

        
        this.targetObject.putXMLProperty(name2, this);
        replace(0, this.targetObject.getXML().getLastXmlChild());
      } else {
        throw ScriptRuntime.typeError("Assignment to empty XMLList without targets not supported");
      }
    
    } else if (xmlName.isAttributeName()) {
      setAttribute(xmlName, value);
    } else {
      XML xml = item(0);
      xml.putXMLProperty(xmlName, value);

      
      replace(0, item(0));
    } 
  }

  
  Object getXMLProperty(XMLName name) {
    return getPropertyList(name);
  }
  
  private void replaceNode(XML xml, XML with) {
    xml.replaceWith(with);
  }
  
  public void put(int index, Scriptable start, Object value) {
    XMLObject xmlValue;
    Object parent = Undefined.instance;



    
    if (value == null) {
      value = "null";
    } else if (value instanceof Undefined) {
      value = "undefined";
    } 
    
    if (value instanceof XMLObject) {
      xmlValue = (XMLObject)value;
    }
    else if (this.targetProperty == null) {
      xmlValue = newXMLFromJs(value.toString());
    
    }
    else {

      
      xmlValue = item(index);
      if (xmlValue == null) {
        XML x = item(0);
        xmlValue = (x == null) ? newTextElementXML(null, this.targetProperty, null) : x.copy();
      } 

      
      ((XML)xmlValue).setChildren(value);
    } 


    
    if (index < length()) {
      parent = item(index).parent();
    } else if (length() == 0) {
      parent = (this.targetObject != null) ? this.targetObject.getXML() : parent();
    } else {
      
      parent = parent();
    } 
    
    if (parent instanceof XML) {
      
      XML xmlParent = (XML)parent;
      
      if (index < length()) {
        
        XML xmlNode = getXmlFromAnnotation(index);
        
        if (xmlValue instanceof XML) {
          replaceNode(xmlNode, (XML)xmlValue);
          replace(index, xmlNode);
        } else if (xmlValue instanceof XMLList) {
          
          XMLList list = (XMLList)xmlValue;
          
          if (list.length() > 0) {
            int lastIndexAdded = xmlNode.childIndex();
            replaceNode(xmlNode, list.item(0));
            replace(index, list.item(0));
            
            for (int i = 1; i < list.length(); i++) {
              xmlParent.insertChildAfter(xmlParent.getXmlChild(lastIndexAdded), list.item(i));
              lastIndexAdded++;
              insert(index + i, list.item(i));
            } 
          } 
        } 
      } else {
        
        xmlParent.appendChild(xmlValue);
        addToList(xmlParent.getLastXmlChild());
      }
    
    }
    else if (index < length()) {
      XML xmlNode = getXML(this._annos, index);
      
      if (xmlValue instanceof XML) {
        replaceNode(xmlNode, (XML)xmlValue);
        replace(index, xmlNode);
      } else if (xmlValue instanceof XMLList) {
        
        XMLList list = (XMLList)xmlValue;
        
        if (list.length() > 0) {
          replaceNode(xmlNode, list.item(0));
          replace(index, list.item(0));
          
          for (int i = 1; i < list.length(); i++) {
            insert(index + i, list.item(i));
          }
        } 
      } 
    } else {
      addToList(xmlValue);
    } 
  }

  
  private XML getXML(XmlNode.InternalList _annos, int index) {
    if (index >= 0 && index < length()) {
      return xmlFromNode(_annos.item(index));
    }
    return null;
  }


  
  void deleteXMLProperty(XMLName name) {
    for (int i = 0; i < length(); i++) {
      XML xml = getXmlFromAnnotation(i);
      
      if (xml.isElement()) {
        xml.deleteXMLProperty(name);
      }
    } 
  }

  
  public void delete(int index) {
    if (index >= 0 && index < length()) {
      XML xml = getXmlFromAnnotation(index);
      
      xml.remove();
      
      internalRemoveFromList(index);
    } 
  }


  
  public Object[] getIds() {
    Object[] enumObjs;
    if (isPrototype()) {
      enumObjs = new Object[0];
    } else {
      enumObjs = new Object[length()];
      
      for (int i = 0; i < enumObjs.length; i++) {
        enumObjs[i] = Integer.valueOf(i);
      }
    } 
    
    return enumObjs;
  }
  
  public Object[] getIdsForDebug() {
    return getIds();
  }


  
  void remove() {
    int nLen = length();
    for (int i = nLen - 1; i >= 0; i--) {
      XML xml = getXmlFromAnnotation(i);
      if (xml != null) {
        xml.remove();
        internalRemoveFromList(i);
      } 
    } 
  }
  
  XML item(int index) {
    return (this._annos != null) ? getXmlFromAnnotation(index) : createEmptyXML();
  }

  
  private void setAttribute(XMLName xmlName, Object value) {
    for (int i = 0; i < length(); i++) {
      XML xml = getXmlFromAnnotation(i);
      xml.setAttribute(xmlName, value);
    } 
  }
  
  void addToList(Object toAdd) {
    this._annos.addToList(toAdd);
  }







  
  XMLList child(int index) {
    XMLList result = newXMLList();
    
    for (int i = 0; i < length(); i++) {
      result.addToList(getXmlFromAnnotation(i).child(index));
    }
    
    return result;
  }

  
  XMLList child(XMLName xmlName) {
    XMLList result = newXMLList();
    
    for (int i = 0; i < length(); i++) {
      result.addToList(getXmlFromAnnotation(i).child(xmlName));
    }
    
    return result;
  }

  
  void addMatches(XMLList rv, XMLName name) {
    for (int i = 0; i < length(); i++) {
      getXmlFromAnnotation(i).addMatches(rv, name);
    }
  }

  
  XMLList children() {
    ArrayList<XML> list = new ArrayList<XML>();
    
    for (int i = 0; i < length(); i++) {
      XML xml = getXmlFromAnnotation(i);
      
      if (xml != null) {
        XMLList childList = xml.children();
        
        int cChildren = childList.length();
        for (int k = 0; k < cChildren; k++) {
          list.add(childList.item(k));
        }
      } 
    } 
    
    XMLList allChildren = newXMLList();
    int sz = list.size();
    
    for (int j = 0; j < sz; j++) {
      allChildren.addToList(list.get(j));
    }
    
    return allChildren;
  }

  
  XMLList comments() {
    XMLList result = newXMLList();
    
    for (int i = 0; i < length(); i++) {
      XML xml = getXmlFromAnnotation(i);
      result.addToList(xml.comments());
    } 
    
    return result;
  }

  
  XMLList elements(XMLName name) {
    XMLList rv = newXMLList();
    for (int i = 0; i < length(); i++) {
      XML xml = getXmlFromAnnotation(i);
      rv.addToList(xml.elements(name));
    } 
    return rv;
  }

  
  boolean contains(Object xml) {
    boolean result = false;
    
    for (int i = 0; i < length(); i++) {
      XML member = getXmlFromAnnotation(i);
      
      if (member.equivalentXml(xml)) {
        result = true;
        
        break;
      } 
    } 
    return result;
  }

  
  XMLObjectImpl copy() {
    XMLList result = newXMLList();
    
    for (int i = 0; i < length(); i++) {
      XML xml = getXmlFromAnnotation(i);
      result.addToList(xml.copy());
    } 
    
    return result;
  }

  
  boolean hasOwnProperty(XMLName xmlName) {
    if (isPrototype()) {
      String property = xmlName.localName();
      return (findPrototypeId(property) != 0);
    } 
    return (getPropertyList(xmlName).length() > 0);
  }


  
  boolean hasComplexContent() {
    boolean complexContent;
    int length = length();
    
    if (length == 0) {
      complexContent = false;
    } else if (length == 1) {
      complexContent = getXmlFromAnnotation(0).hasComplexContent();
    } else {
      complexContent = false;
      
      for (int i = 0; i < length; i++) {
        XML nextElement = getXmlFromAnnotation(i);
        if (nextElement.isElement()) {
          complexContent = true;
          
          break;
        } 
      } 
    } 
    return complexContent;
  }

  
  boolean hasSimpleContent() {
    if (length() == 0)
      return true; 
    if (length() == 1) {
      return getXmlFromAnnotation(0).hasSimpleContent();
    }
    for (int i = 0; i < length(); i++) {
      XML nextElement = getXmlFromAnnotation(i);
      if (nextElement.isElement()) {
        return false;
      }
    } 
    return true;
  }


  
  int length() {
    int result = 0;
    
    if (this._annos != null) {
      result = this._annos.length();
    }
    
    return result;
  }

  
  void normalize() {
    for (int i = 0; i < length(); i++) {
      getXmlFromAnnotation(i).normalize();
    }
  }





  
  Object parent() {
    if (length() == 0) return Undefined.instance;
    
    XML candidateParent = null;
    
    for (int i = 0; i < length(); i++) {
      Object currParent = getXmlFromAnnotation(i).parent();
      if (!(currParent instanceof XML)) return Undefined.instance; 
      XML xml = (XML)currParent;
      if (i == 0) {
        
        candidateParent = xml;
      }
      else if (!candidateParent.is(xml)) {

        
        return Undefined.instance;
      } 
    } 
    
    return candidateParent;
  }

  
  XMLList processingInstructions(XMLName xmlName) {
    XMLList result = newXMLList();
    
    for (int i = 0; i < length(); i++) {
      XML xml = getXmlFromAnnotation(i);
      
      result.addToList(xml.processingInstructions(xmlName));
    } 
    
    return result;
  }

  
  boolean propertyIsEnumerable(Object name) {
    long index;
    if (name instanceof Integer) {
      index = ((Integer)name).intValue();
    } else if (name instanceof Number) {
      double x = ((Number)name).doubleValue();
      index = (long)x;
      if (index != x) {
        return false;
      }
      if (index == 0L && 1.0D / x < 0.0D)
      {
        return false;
      }
    } else {
      String s = ScriptRuntime.toString(name);
      index = ScriptRuntime.testUint32String(s);
    } 
    return (0L <= index && index < length());
  }

  
  XMLList text() {
    XMLList result = newXMLList();
    
    for (int i = 0; i < length(); i++) {
      result.addToList(getXmlFromAnnotation(i).text());
    }
    
    return result;
  }


  
  public String toString() {
    if (hasSimpleContent()) {
      StringBuilder sb = new StringBuilder();
      
      for (int i = 0; i < length(); i++) {
        XML next = getXmlFromAnnotation(i);
        if (!next.isComment() && !next.isProcessingInstruction())
        {
          
          sb.append(next.toString());
        }
      } 
      
      return sb.toString();
    } 
    return toXMLString();
  }


  
  String toSource(int indent) {
    return toXMLString();
  }


  
  String toXMLString() {
    StringBuilder sb = new StringBuilder();
    
    for (int i = 0; i < length(); i++) {
      if (getProcessor().isPrettyPrinting() && i != 0) {
        sb.append('\n');
      }
      sb.append(getXmlFromAnnotation(i).toXMLString());
    } 
    return sb.toString();
  }

  
  Object valueOf() {
    return this;
  }





  
  boolean equivalentXml(Object target) {
    boolean result = false;

    
    if (target instanceof Undefined && length() == 0) {
      result = true;
    } else if (length() == 1) {
      result = getXmlFromAnnotation(0).equivalentXml(target);
    } else if (target instanceof XMLList) {
      XMLList otherList = (XMLList)target;
      
      if (otherList.length() == length()) {
        result = true;
        
        for (int i = 0; i < length(); i++) {
          if (!getXmlFromAnnotation(i).equivalentXml(otherList.getXmlFromAnnotation(i))) {
            result = false;
            
            break;
          } 
        } 
      } 
    } 
    return result;
  }
  
  private XMLList getPropertyList(XMLName name) {
    XMLList propertyList = newXMLList();
    XmlNode.QName qname = null;
    
    if (!name.isDescendants() && !name.isAttributeName())
    {
      
      qname = name.toQname();
    }
    
    propertyList.setTargets(this, qname);
    
    for (int i = 0; i < length(); i++) {
      propertyList.addToList(getXmlFromAnnotation(i).getPropertyList(name));
    }

    
    return propertyList;
  }


  
  private Object applyOrCall(boolean isApply, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    String methodName = isApply ? "apply" : "call";
    if (!(thisObj instanceof XMLList) || ((XMLList)thisObj).targetProperty == null)
    {
      throw ScriptRuntime.typeError1("msg.isnt.function", methodName);
    }
    
    return ScriptRuntime.applyOrCall(isApply, cx, scope, thisObj, args);
  }



  
  protected Object jsConstructor(Context cx, boolean inNewExpr, Object[] args) {
    if (args.length == 0) {
      return newXMLList();
    }
    Object arg0 = args[0];
    if (!inNewExpr && arg0 instanceof XMLList)
    {
      return arg0;
    }
    return newXMLListFrom(arg0);
  }





  
  public Scriptable getExtraMethodSource(Context cx) {
    if (length() == 1) {
      return (Scriptable)getXmlFromAnnotation(0);
    }
    return null;
  }



  
  public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
    if (this.targetProperty == null) {
      throw ScriptRuntime.notFunctionError(this);
    }
    String methodName = this.targetProperty.getLocalName();
    
    boolean isApply = methodName.equals("apply");
    if (isApply || methodName.equals("call")) {
      return applyOrCall(isApply, cx, scope, thisObj, args);
    }
    if (!(thisObj instanceof XMLObject)) {
      throw ScriptRuntime.typeError1("msg.incompat.call", methodName);
    }
    Object func = null;
    Scriptable sobj = thisObj;
    
    while (sobj instanceof XMLObject) {
      XMLObject xmlObject = (XMLObject)sobj;
      func = xmlObject.getFunctionProperty(cx, methodName);
      if (func != Scriptable.NOT_FOUND) {
        break;
      }
      sobj = xmlObject.getExtraMethodSource(cx);
      if (sobj != null) {
        thisObj = sobj;
        if (!(sobj instanceof XMLObject)) {
          func = ScriptableObject.getProperty(sobj, methodName);
        }
      } 
    } 
    
    if (!(func instanceof Callable)) {
      throw ScriptRuntime.notFunctionError(thisObj, func, methodName);
    }
    return ((Callable)func).call(cx, scope, thisObj, args);
  }
  
  public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
    throw ScriptRuntime.typeError1("msg.not.ctor", "XMLList");
  }
}
