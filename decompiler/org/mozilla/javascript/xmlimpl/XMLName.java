package org.mozilla.javascript.xmlimpl;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.Ref;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Undefined;

class XMLName extends Ref {
  static final long serialVersionUID = 3832176310755686977L;
  private XmlNode.QName qname;
  
  private static boolean isNCNameStartChar(int c) {
    if ((c & 0xFFFFFF80) == 0) {
      
      if (c >= 97)
        return (c <= 122); 
      if (c >= 65) {
        if (c <= 90) {
          return true;
        }
        return (c == 95);
      } 
    } else if ((c & 0xFFFFE000) == 0) {
      return ((192 <= c && c <= 214) || (216 <= c && c <= 246) || (248 <= c && c <= 767) || (880 <= c && c <= 893) || 895 <= c);
    } 



    
    return ((8204 <= c && c <= 8205) || (8304 <= c && c <= 8591) || (11264 <= c && c <= 12271) || (12289 <= c && c <= 55295) || (63744 <= c && c <= 64975) || (65008 <= c && c <= 65533) || (65536 <= c && c <= 983039));
  }

  
  private boolean isAttributeName;
  
  private boolean isDescendants;
  private XMLObjectImpl xmlObject;
  
  private static boolean isNCNameChar(int c) {
    if ((c & 0xFFFFFF80) == 0) {
      
      if (c >= 97)
        return (c <= 122); 
      if (c >= 65) {
        if (c <= 90) {
          return true;
        }
        return (c == 95);
      }  if (c >= 48) {
        return (c <= 57);
      }
      return (c == 45 || c == 46);
    } 
    if ((c & 0xFFFFE000) == 0) {
      return (isNCNameStartChar(c) || c == 183 || (768 <= c && c <= 879));
    }
    
    return (isNCNameStartChar(c) || (8255 <= c && c <= 8256));
  }


  
  static boolean accept(Object nameObj) {
    String name;
    try {
      name = ScriptRuntime.toString(nameObj);
    } catch (EcmaError ee) {
      if ("TypeError".equals(ee.getName())) {
        return false;
      }
      throw ee;
    } 

    
    int length = name.length();
    if (length != 0 && 
      isNCNameStartChar(name.charAt(0))) {
      for (int i = 1; i != length; i++) {
        if (!isNCNameChar(name.charAt(i))) {
          return false;
        }
      } 
      return true;
    } 

    
    return false;
  }








  
  static XMLName formStar() {
    XMLName rv = new XMLName();
    rv.qname = XmlNode.QName.create(null, null);
    return rv;
  }

  
  @Deprecated
  static XMLName formProperty(XmlNode.Namespace namespace, String localName) {
    if (localName != null && localName.equals("*")) localName = null; 
    XMLName rv = new XMLName();
    rv.qname = XmlNode.QName.create(namespace, localName);
    return rv;
  }

  
  static XMLName formProperty(String uri, String localName) {
    return formProperty(XmlNode.Namespace.create(uri), localName);
  }

  
  static XMLName create(String defaultNamespaceUri, String name) {
    if (name == null) {
      throw new IllegalArgumentException();
    }
    int l = name.length();
    if (l != 0) {
      char firstChar = name.charAt(0);
      if (firstChar == '*') {
        if (l == 1) {
          return formStar();
        }
      } else if (firstChar == '@') {
        XMLName xmlName = formProperty("", name.substring(1));
        xmlName.setAttributeName();
        return xmlName;
      } 
    } 
    
    return formProperty(defaultNamespaceUri, name);
  }
  
  static XMLName create(XmlNode.QName qname, boolean attribute, boolean descendants) {
    XMLName rv = new XMLName();
    rv.qname = qname;
    rv.isAttributeName = attribute;
    rv.isDescendants = descendants;
    return rv;
  }

  
  @Deprecated
  static XMLName create(XmlNode.QName qname) {
    return create(qname, false, false);
  }
  
  void initXMLObject(XMLObjectImpl xmlObject) {
    if (xmlObject == null) throw new IllegalArgumentException(); 
    if (this.xmlObject != null) throw new IllegalStateException(); 
    this.xmlObject = xmlObject;
  }
  
  String uri() {
    if (this.qname.getNamespace() == null) return null; 
    return this.qname.getNamespace().getUri();
  }
  
  String localName() {
    if (this.qname.getLocalName() == null) return "*"; 
    return this.qname.getLocalName();
  }
  
  private void addDescendantChildren(XMLList list, XML target) {
    XMLName xmlName = this;
    if (target.isElement()) {
      XML[] children = target.getChildren();
      for (int i = 0; i < children.length; i++) {
        if (xmlName.matches(children[i])) {
          list.addToList(children[i]);
        }
        addDescendantChildren(list, children[i]);
      } 
    } 
  }
  
  void addMatchingAttributes(XMLList list, XML target) {
    XMLName name = this;
    if (target.isElement()) {
      XML[] attributes = target.getAttributes();
      for (int i = 0; i < attributes.length; i++) {
        if (name.matches(attributes[i])) {
          list.addToList(attributes[i]);
        }
      } 
    } 
  }
  
  private void addDescendantAttributes(XMLList list, XML target) {
    if (target.isElement()) {
      addMatchingAttributes(list, target);
      XML[] children = target.getChildren();
      for (int i = 0; i < children.length; i++) {
        addDescendantAttributes(list, children[i]);
      }
    } 
  }
  
  XMLList matchDescendantAttributes(XMLList rv, XML target) {
    rv.setTargets(target, null);
    addDescendantAttributes(rv, target);
    return rv;
  }
  
  XMLList matchDescendantChildren(XMLList rv, XML target) {
    rv.setTargets(target, null);
    addDescendantChildren(rv, target);
    return rv;
  }
  
  void addDescendants(XMLList rv, XML target) {
    XMLName xmlName = this;
    if (xmlName.isAttributeName()) {
      matchDescendantAttributes(rv, target);
    } else {
      matchDescendantChildren(rv, target);
    } 
  }
  
  private void addAttributes(XMLList rv, XML target) {
    addMatchingAttributes(rv, target);
  }
  
  void addMatches(XMLList rv, XML target) {
    if (isDescendants()) {
      addDescendants(rv, target);
    } else if (isAttributeName()) {
      addAttributes(rv, target);
    } else {
      XML[] children = target.getChildren();
      if (children != null) {
        for (int i = 0; i < children.length; i++) {
          if (matches(children[i])) {
            rv.addToList(children[i]);
          }
        } 
      }
      rv.setTargets(target, toQname());
    } 
  }
  
  XMLList getMyValueOn(XML target) {
    XMLList rv = target.newXMLList();
    addMatches(rv, target);
    return rv;
  }

  
  void setMyValueOn(XML target, Object value) {
    if (value == null) {
      value = "null";
    } else if (value instanceof Undefined) {
      value = "undefined";
    } 
    
    XMLName xmlName = this;
    
    if (xmlName.isAttributeName()) {
      target.setAttribute(xmlName, value);
    } else if (xmlName.uri() == null && xmlName.localName().equals("*")) {
      target.setChildren(value);
    } else {
      
      XMLObjectImpl xmlValue = null;
      
      if (value instanceof XMLObjectImpl) {
        xmlValue = (XMLObjectImpl)value;

        
        if (xmlValue instanceof XML && (
          (XML)xmlValue).isAttribute()) {
          xmlValue = target.makeXmlFromString(xmlName, xmlValue.toString());
        }


        
        if (xmlValue instanceof XMLList) {
          for (int i = 0; i < xmlValue.length(); i++) {
            XML xml = ((XMLList)xmlValue).item(i);
            
            if (xml.isAttribute()) {
              ((XMLList)xmlValue).replace(i, target.makeXmlFromString(xmlName, xml.toString()));
            }
          } 
        }
      } else {
        xmlValue = target.makeXmlFromString(xmlName, ScriptRuntime.toString(value));
      } 
      
      XMLList matches = target.getPropertyList(xmlName);
      
      if (matches.length() == 0) {
        target.appendChild(xmlValue);
      } else {
        
        for (int i = 1; i < matches.length(); i++) {
          target.removeChild(matches.item(i).childIndex());
        }

        
        XML firstMatch = matches.item(0);
        target.replace(firstMatch.childIndex(), xmlValue);
      } 
    } 
  }

  
  public boolean has(Context cx) {
    if (this.xmlObject == null) {
      return false;
    }
    return this.xmlObject.hasXMLProperty(this);
  }

  
  public Object get(Context cx) {
    if (this.xmlObject == null) {
      throw ScriptRuntime.undefReadError(Undefined.instance, toString());
    }
    
    return this.xmlObject.getXMLProperty(this);
  }

  
  public Object set(Context cx, Object value) {
    if (this.xmlObject == null) {
      throw ScriptRuntime.undefWriteError(Undefined.instance, toString(), value);
    }



    
    if (this.isDescendants) throw Kit.codeBug(); 
    this.xmlObject.putXMLProperty(this, value);
    return value;
  }

  
  public boolean delete(Context cx) {
    if (this.xmlObject == null) {
      return true;
    }
    this.xmlObject.deleteXMLProperty(this);
    return !this.xmlObject.hasXMLProperty(this);
  }


  
  public String toString() {
    StringBuilder buff = new StringBuilder();
    if (this.isDescendants) buff.append(".."); 
    if (this.isAttributeName) buff.append('@'); 
    if (uri() == null) {
      buff.append('*');
      if (localName().equals("*")) {
        return buff.toString();
      }
    } else {
      buff.append('"').append(uri()).append('"');
    } 
    buff.append(':').append(localName());
    return buff.toString();
  }
  
  final XmlNode.QName toQname() {
    return this.qname;
  }
  
  final boolean matchesLocalName(String localName) {
    return (localName().equals("*") || localName().equals(localName));
  }
  
  final boolean matchesElement(XmlNode.QName qname) {
    if ((uri() == null || uri().equals(qname.getNamespace().getUri())) && (
      localName().equals("*") || localName().equals(qname.getLocalName()))) {
      return true;
    }
    
    return false;
  }
  
  final boolean matches(XML node) {
    XmlNode.QName qname = node.getNodeQname();
    String nodeUri = null;
    if (qname.getNamespace() != null) {
      nodeUri = qname.getNamespace().getUri();
    }
    if (this.isAttributeName) {
      if (node.isAttribute()) {
        if ((uri() == null || uri().equals(nodeUri)) && (
          localName().equals("*") || localName().equals(qname.getLocalName()))) {
          return true;
        }
        
        return false;
      } 

      
      return false;
    } 
    
    if (uri() == null || (node.isElement() && uri().equals(nodeUri))) {
      if (localName().equals("*")) return true; 
      if (node.isElement() && 
        localName().equals(qname.getLocalName())) return true;
    
    } 
    return false;
  }


  
  boolean isAttributeName() {
    return this.isAttributeName;
  }



  
  void setAttributeName() {
    this.isAttributeName = true;
  }

  
  boolean isDescendants() {
    return this.isDescendants;
  }



  
  @Deprecated
  void setIsDescendants() {
    this.isDescendants = true;
  }
}
