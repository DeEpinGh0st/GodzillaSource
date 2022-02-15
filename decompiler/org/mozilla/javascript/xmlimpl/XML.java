package org.mozilla.javascript.xmlimpl;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.xml.XMLObject;
import org.w3c.dom.Node;


class XML
  extends XMLObjectImpl
{
  static final long serialVersionUID = -630969919086449092L;
  private XmlNode node;
  
  XML(XMLLibImpl lib, Scriptable scope, XMLObject prototype, XmlNode node) {
    super(lib, scope, prototype);
    initialize(node);
  }
  
  void initialize(XmlNode node) {
    this.node = node;
    this.node.setXml(this);
  }

  
  final XML getXML() {
    return this;
  }



  
  void replaceWith(XML value) {
    if (this.node.parent() == null) {

      
      initialize(value.node);
      return;
    } 
    this.node.replaceWith(value.node);
  }
  XML makeXmlFromString(XMLName name, String value) {
    try {
      return newTextElementXML(this.node, name.toQname(), value);
    } catch (Exception e) {
      throw ScriptRuntime.typeError(e.getMessage());
    } 
  }

  
  XmlNode getAnnotation() {
    return this.node;
  }







  
  public Object get(int index, Scriptable start) {
    if (index == 0) {
      return this;
    }
    return Scriptable.NOT_FOUND;
  }


  
  public boolean has(int index, Scriptable start) {
    return (index == 0);
  }



  
  public void put(int index, Scriptable start, Object value) {
    throw ScriptRuntime.typeError("Assignment to indexed XML is not allowed");
  }

  
  public Object[] getIds() {
    if (isPrototype()) {
      return new Object[0];
    }
    return new Object[] { Integer.valueOf(0) };
  }



  
  public void delete(int index) {
    if (index == 0) {
      remove();
    }
  }





  
  boolean hasXMLProperty(XMLName xmlName) {
    return (getPropertyList(xmlName).length() > 0);
  }

  
  Object getXMLProperty(XMLName xmlName) {
    return getPropertyList(xmlName);
  }






  
  XmlNode.QName getNodeQname() {
    return this.node.getQname();
  }
  
  XML[] getChildren() {
    if (!isElement()) return null; 
    XmlNode[] children = this.node.getMatchingChildren(XmlNode.Filter.TRUE);
    XML[] rv = new XML[children.length];
    for (int i = 0; i < rv.length; i++) {
      rv[i] = toXML(children[i]);
    }
    return rv;
  }
  
  XML[] getAttributes() {
    XmlNode[] attributes = this.node.getAttributes();
    XML[] rv = new XML[attributes.length];
    for (int i = 0; i < rv.length; i++) {
      rv[i] = toXML(attributes[i]);
    }
    return rv;
  }

  
  XMLList getPropertyList(XMLName name) {
    return name.getMyValueOn(this);
  }

  
  void deleteXMLProperty(XMLName name) {
    XMLList list = getPropertyList(name);
    for (int i = 0; i < list.length(); i++) {
      (list.item(i)).node.deleteMe();
    }
  }

  
  void putXMLProperty(XMLName xmlName, Object value) {
    if (!isPrototype())
    {
      
      xmlName.setMyValueOn(this, value);
    }
  }

  
  boolean hasOwnProperty(XMLName xmlName) {
    boolean hasProperty = false;
    
    if (isPrototype()) {
      String property = xmlName.localName();
      hasProperty = (0 != findPrototypeId(property));
    } else {
      hasProperty = (getPropertyList(xmlName).length() > 0);
    } 
    
    return hasProperty;
  }

  
  protected Object jsConstructor(Context cx, boolean inNewExpr, Object[] args) {
    if (args.length == 0 || args[0] == null || args[0] == Undefined.instance) {
      args = new Object[] { "" };
    }
    
    XML toXml = ecmaToXml(args[0]);
    if (inNewExpr) {
      return toXml.copy();
    }
    return toXml;
  }



  
  public Scriptable getExtraMethodSource(Context cx) {
    if (hasSimpleContent()) {
      String src = toString();
      return ScriptRuntime.toObjectOrNull(cx, src);
    } 
    return null;
  }




  
  void removeChild(int index) {
    this.node.removeChild(index);
  }

  
  void normalize() {
    this.node.normalize();
  }
  
  private XML toXML(XmlNode node) {
    if (node.getXml() == null) {
      node.setXml(newXML(node));
    }
    return node.getXml();
  }
  
  void setAttribute(XMLName xmlName, Object value) {
    if (!isElement()) throw new IllegalStateException("Can only set attributes on elements.");
    
    if (xmlName.uri() == null && xmlName.localName().equals("*")) {
      throw ScriptRuntime.typeError("@* assignment not supported.");
    }
    this.node.setAttribute(xmlName.toQname(), ScriptRuntime.toString(value));
  }
  
  void remove() {
    this.node.deleteMe();
  }

  
  void addMatches(XMLList rv, XMLName name) {
    name.addMatches(rv, this);
  }

  
  XMLList elements(XMLName name) {
    XMLList rv = newXMLList();
    rv.setTargets(this, name.toQname());
    
    XmlNode[] elements = this.node.getMatchingChildren(XmlNode.Filter.ELEMENT);
    for (int i = 0; i < elements.length; i++) {
      if (name.matches(toXML(elements[i]))) {
        rv.addToList(toXML(elements[i]));
      }
    } 
    return rv;
  }



  
  XMLList child(XMLName xmlName) {
    XMLList rv = newXMLList();



    
    XmlNode[] elements = this.node.getMatchingChildren(XmlNode.Filter.ELEMENT);
    for (int i = 0; i < elements.length; i++) {
      if (xmlName.matchesElement(elements[i].getQname())) {
        rv.addToList(toXML(elements[i]));
      }
    } 
    rv.setTargets(this, xmlName.toQname());
    return rv;
  }
  
  XML replace(XMLName xmlName, Object xml) {
    putXMLProperty(xmlName, xml);
    return this;
  }

  
  XMLList children() {
    XMLList rv = newXMLList();
    XMLName all = XMLName.formStar();
    rv.setTargets(this, all.toQname());
    XmlNode[] children = this.node.getMatchingChildren(XmlNode.Filter.TRUE);
    for (int i = 0; i < children.length; i++) {
      rv.addToList(toXML(children[i]));
    }
    return rv;
  }


  
  XMLList child(int index) {
    XMLList result = newXMLList();
    result.setTargets(this, null);
    if (index >= 0 && index < this.node.getChildCount()) {
      result.addToList(getXmlChild(index));
    }
    return result;
  }
  
  XML getXmlChild(int index) {
    XmlNode child = this.node.getChild(index);
    if (child.getXml() == null) {
      child.setXml(newXML(child));
    }
    return child.getXml();
  }

  
  XML getLastXmlChild() {
    int pos = this.node.getChildCount() - 1;
    if (pos < 0) return null; 
    return getXmlChild(pos);
  }
  
  int childIndex() {
    return this.node.getChildIndex();
  }

  
  boolean contains(Object xml) {
    if (xml instanceof XML) {
      return equivalentXml(xml);
    }
    return false;
  }



  
  boolean equivalentXml(Object target) {
    boolean result = false;
    
    if (target instanceof XML)
    {
      return this.node.toXmlString(getProcessor()).equals(((XML)target).node.toXmlString(getProcessor())); } 
    if (target instanceof XMLList) {
      
      XMLList otherList = (XMLList)target;
      
      if (otherList.length() == 1) {
        result = equivalentXml(otherList.getXML());
      }
    } else if (hasSimpleContent()) {
      String otherStr = ScriptRuntime.toString(target);
      
      result = toString().equals(otherStr);
    } 
    
    return result;
  }

  
  XMLObjectImpl copy() {
    return newXML(this.node.copy());
  }

  
  boolean hasSimpleContent() {
    if (isComment() || isProcessingInstruction()) return false; 
    if (isText() || this.node.isAttributeType()) return true; 
    return !this.node.hasChildElement();
  }

  
  boolean hasComplexContent() {
    return !hasSimpleContent();
  }



  
  int length() {
    return 1;
  }

  
  boolean is(XML other) {
    return this.node.isSameNode(other.node);
  }
  
  Object nodeKind() {
    return ecmaClass();
  }

  
  Object parent() {
    XmlNode parent = this.node.parent();
    if (parent == null) return null; 
    return newXML(this.node.parent());
  }


  
  boolean propertyIsEnumerable(Object name) {
    boolean result;
    if (name instanceof Integer) {
      result = (((Integer)name).intValue() == 0);
    } else if (name instanceof Number) {
      double x = ((Number)name).doubleValue();
      
      result = (x == 0.0D && 1.0D / x > 0.0D);
    } else {
      result = ScriptRuntime.toString(name).equals("0");
    } 
    return result;
  }

  
  Object valueOf() {
    return this;
  }





  
  XMLList comments() {
    XMLList rv = newXMLList();
    this.node.addMatchingChildren(rv, XmlNode.Filter.COMMENT);
    return rv;
  }

  
  XMLList text() {
    XMLList rv = newXMLList();
    this.node.addMatchingChildren(rv, XmlNode.Filter.TEXT);
    return rv;
  }

  
  XMLList processingInstructions(XMLName xmlName) {
    XMLList rv = newXMLList();
    this.node.addMatchingChildren(rv, XmlNode.Filter.PROCESSING_INSTRUCTION(xmlName));
    return rv;
  }










  
  private XmlNode[] getNodesForInsert(Object value) {
    if (value instanceof XML)
      return new XmlNode[] { ((XML)value).node }; 
    if (value instanceof XMLList) {
      XMLList list = (XMLList)value;
      XmlNode[] rv = new XmlNode[list.length()];
      for (int i = 0; i < list.length(); i++) {
        rv[i] = (list.item(i)).node;
      }
      return rv;
    } 
    return new XmlNode[] { XmlNode.createText(getProcessor(), ScriptRuntime.toString(value)) };
  }



  
  XML replace(int index, Object xml) {
    XMLList xlChildToReplace = child(index);
    if (xlChildToReplace.length() > 0) {
      
      XML childToReplace = xlChildToReplace.item(0);
      insertChildAfter(childToReplace, xml);
      removeChild(index);
    } 
    return this;
  }
  
  XML prependChild(Object xml) {
    if (this.node.isParentType()) {
      this.node.insertChildrenAt(0, getNodesForInsert(xml));
    }
    return this;
  }
  
  XML appendChild(Object xml) {
    if (this.node.isParentType()) {
      XmlNode[] nodes = getNodesForInsert(xml);
      this.node.insertChildrenAt(this.node.getChildCount(), nodes);
    } 
    return this;
  }
  
  private int getChildIndexOf(XML child) {
    for (int i = 0; i < this.node.getChildCount(); i++) {
      if (this.node.getChild(i).isSameNode(child.node)) {
        return i;
      }
    } 
    return -1;
  }
  
  XML insertChildBefore(XML child, Object xml) {
    if (child == null) {
      
      appendChild(xml);
    } else {
      XmlNode[] toInsert = getNodesForInsert(xml);
      int index = getChildIndexOf(child);
      if (index != -1) {
        this.node.insertChildrenAt(index, toInsert);
      }
    } 
    
    return this;
  }
  
  XML insertChildAfter(XML child, Object xml) {
    if (child == null) {
      
      prependChild(xml);
    } else {
      XmlNode[] toInsert = getNodesForInsert(xml);
      int index = getChildIndexOf(child);
      if (index != -1) {
        this.node.insertChildrenAt(index + 1, toInsert);
      }
    } 
    
    return this;
  }

  
  XML setChildren(Object xml) {
    if (!isElement()) return this;
    
    while (this.node.getChildCount() > 0) {
      this.node.removeChild(0);
    }
    XmlNode[] toInsert = getNodesForInsert(xml);
    
    this.node.insertChildrenAt(0, toInsert);
    
    return this;
  }




  
  private void addInScopeNamespace(Namespace ns) {
    if (!isElement()) {
      return;
    }

    
    if (ns.prefix() != null) {
      if (ns.prefix().length() == 0 && ns.uri().length() == 0) {
        return;
      }
      if (this.node.getQname().getNamespace().getPrefix().equals(ns.prefix())) {
        this.node.invalidateNamespacePrefix();
      }
      this.node.declareNamespace(ns.prefix(), ns.uri());
    } else {
      return;
    } 
  }
  
  Namespace[] inScopeNamespaces() {
    XmlNode.Namespace[] inScope = this.node.getInScopeNamespaces();
    return createNamespaces(inScope);
  }
  
  private XmlNode.Namespace adapt(Namespace ns) {
    if (ns.prefix() == null) {
      return XmlNode.Namespace.create(ns.uri());
    }
    return XmlNode.Namespace.create(ns.prefix(), ns.uri());
  }

  
  XML removeNamespace(Namespace ns) {
    if (!isElement()) return this; 
    this.node.removeNamespace(adapt(ns));
    return this;
  }
  
  XML addNamespace(Namespace ns) {
    addInScopeNamespace(ns);
    return this;
  }
  
  QName name() {
    if (isText() || isComment()) return null; 
    if (isProcessingInstruction()) return newQName("", this.node.getQname().getLocalName(), null); 
    return newQName(this.node.getQname());
  }
  
  Namespace[] namespaceDeclarations() {
    XmlNode.Namespace[] declarations = this.node.getNamespaceDeclarations();
    return createNamespaces(declarations);
  }
  
  Namespace namespace(String prefix) {
    if (prefix == null) {
      return createNamespace(this.node.getNamespaceDeclaration());
    }
    return createNamespace(this.node.getNamespaceDeclaration(prefix));
  }

  
  String localName() {
    if (name() == null) return null; 
    return name().localName();
  }

  
  void setLocalName(String localName) {
    if (isText() || isComment())
      return;  this.node.setLocalName(localName);
  }

  
  void setName(QName name) {
    if (isText() || isComment())
      return;  if (isProcessingInstruction()) {

      
      this.node.setLocalName(name.localName());
      return;
    } 
    this.node.renameNode(name.getDelegate());
  }

  
  void setNamespace(Namespace ns) {
    if (isText() || isComment() || isProcessingInstruction())
      return;  setName(newQName(ns.uri(), localName(), ns.prefix()));
  }




  
  final String ecmaClass() {
    if (this.node.isTextType())
      return "text"; 
    if (this.node.isAttributeType())
      return "attribute"; 
    if (this.node.isCommentType())
      return "comment"; 
    if (this.node.isProcessingInstructionType())
      return "processing-instruction"; 
    if (this.node.isElementType()) {
      return "element";
    }
    throw new RuntimeException("Unrecognized type: " + this.node);
  }





  
  public String getClassName() {
    return "XML";
  }
  
  private String ecmaValue() {
    return this.node.ecmaValue();
  }

  
  private String ecmaToString() {
    if (isAttribute() || isText()) {
      return ecmaValue();
    }
    if (hasSimpleContent()) {
      StringBuilder rv = new StringBuilder();
      for (int i = 0; i < this.node.getChildCount(); i++) {
        XmlNode child = this.node.getChild(i);
        if (!child.isProcessingInstructionType() && !child.isCommentType()) {



          
          XML x = new XML(getLib(), getParentScope(), (XMLObject)getPrototype(), child);
          
          rv.append(x.toString());
        } 
      } 
      return rv.toString();
    } 
    return toXMLString();
  }

  
  public String toString() {
    return ecmaToString();
  }

  
  String toSource(int indent) {
    return toXMLString();
  }

  
  String toXMLString() {
    return this.node.ecmaToXMLString(getProcessor());
  }
  
  final boolean isAttribute() {
    return this.node.isAttributeType();
  }
  
  final boolean isComment() {
    return this.node.isCommentType();
  }
  
  final boolean isText() {
    return this.node.isTextType();
  }
  
  final boolean isElement() {
    return this.node.isElementType();
  }
  
  final boolean isProcessingInstruction() {
    return this.node.isProcessingInstructionType();
  }

  
  Node toDomNode() {
    return this.node.toDomNode();
  }
}
