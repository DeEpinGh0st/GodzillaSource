package org.mozilla.javascript.xmlimpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;
import org.xml.sax.SAXException;



class XmlNode
  implements Serializable
{
  private static final String XML_NAMESPACES_NAMESPACE_URI = "http://www.w3.org/2000/xmlns/";
  private static final String USER_DATA_XMLNODE_KEY = XmlNode.class.getName();
  
  private static final boolean DOM_LEVEL_3 = true;
  private static final long serialVersionUID = 1L;
  
  private static XmlNode getUserData(Node node) {
    return (XmlNode)node.getUserData(USER_DATA_XMLNODE_KEY);
  }



  
  private static void setUserData(Node node, XmlNode wrap) {
    node.setUserData(USER_DATA_XMLNODE_KEY, wrap, wrap.events);
  }

  
  private static XmlNode createImpl(Node node) {
    if (node instanceof Document) throw new IllegalArgumentException(); 
    XmlNode rv = null;
    if (getUserData(node) == null) {
      rv = new XmlNode();
      rv.dom = node;
      setUserData(node, rv);
    } else {
      rv = getUserData(node);
    } 
    return rv;
  }
  
  static XmlNode newElementWithText(XmlProcessor processor, XmlNode reference, QName qname, String value) {
    if (reference instanceof Document) throw new IllegalArgumentException("Cannot use Document node as reference"); 
    Document document = null;
    if (reference != null) {
      document = reference.dom.getOwnerDocument();
    } else {
      document = processor.newDocument();
    } 
    Node referenceDom = (reference != null) ? reference.dom : null;
    Namespace ns = qname.getNamespace();
    Element e = (ns == null || ns.getUri().length() == 0) ? document.createElementNS(null, qname.getLocalName()) : document.createElementNS(ns.getUri(), qname.qualify(referenceDom));


    
    if (value != null) {
      e.appendChild(document.createTextNode(value));
    }
    return createImpl(e);
  }
  
  static XmlNode createText(XmlProcessor processor, String value) {
    return createImpl(processor.newDocument().createTextNode(value));
  }
  
  static XmlNode createElementFromNode(Node node) {
    if (node instanceof Document)
      node = ((Document)node).getDocumentElement(); 
    return createImpl(node);
  }
  
  static XmlNode createElement(XmlProcessor processor, String namespaceUri, String xml) throws SAXException {
    return createImpl(processor.toXml(namespaceUri, xml));
  }
  
  static XmlNode createEmpty(XmlProcessor processor) {
    return createText(processor, "");
  }
  
  private static XmlNode copy(XmlNode other) {
    return createImpl(other.dom.cloneNode(true));
  }


  
  private UserDataHandler events = new XmlNodeUserDataHandler();

  
  private Node dom;

  
  private XML xml;

  
  String debug() {
    XmlProcessor raw = new XmlProcessor();
    raw.setIgnoreComments(false);
    raw.setIgnoreProcessingInstructions(false);
    raw.setIgnoreWhitespace(false);
    raw.setPrettyPrinting(false);
    return raw.ecmaToXmlString(this.dom);
  }

  
  public String toString() {
    return "XmlNode: type=" + this.dom.getNodeType() + " dom=" + this.dom.toString();
  }
  
  XML getXml() {
    return this.xml;
  }
  
  void setXml(XML xml) {
    this.xml = xml;
  }
  
  int getChildCount() {
    return this.dom.getChildNodes().getLength();
  }
  
  XmlNode parent() {
    Node domParent = this.dom.getParentNode();
    if (domParent instanceof Document) return null; 
    if (domParent == null) return null; 
    return createImpl(domParent);
  }
  
  int getChildIndex() {
    if (isAttributeType()) return -1; 
    if (parent() == null) return -1; 
    NodeList siblings = this.dom.getParentNode().getChildNodes();
    for (int i = 0; i < siblings.getLength(); i++) {
      if (siblings.item(i) == this.dom) {
        return i;
      }
    } 
    
    throw new RuntimeException("Unreachable.");
  }
  
  void removeChild(int index) {
    this.dom.removeChild(this.dom.getChildNodes().item(index));
  }
  
  String toXmlString(XmlProcessor processor) {
    return processor.ecmaToXmlString(this.dom);
  }

  
  String ecmaValue() {
    if (isTextType())
      return ((Text)this.dom).getData(); 
    if (isAttributeType())
      return ((Attr)this.dom).getValue(); 
    if (isProcessingInstructionType())
      return ((ProcessingInstruction)this.dom).getData(); 
    if (isCommentType())
      return ((Comment)this.dom).getNodeValue(); 
    if (isElementType()) {
      throw new RuntimeException("Unimplemented ecmaValue() for elements.");
    }
    throw new RuntimeException("Unimplemented for node " + this.dom);
  }

  
  void deleteMe() {
    if (this.dom instanceof Attr) {
      Attr attr = (Attr)this.dom;
      attr.getOwnerElement().getAttributes().removeNamedItemNS(attr.getNamespaceURI(), attr.getLocalName());
    }
    else if (this.dom.getParentNode() != null) {
      this.dom.getParentNode().removeChild(this.dom);
    } 
  }




  
  void normalize() {
    this.dom.normalize();
  }
  
  void insertChildAt(int index, XmlNode node) {
    Node parent = this.dom;
    Node child = parent.getOwnerDocument().importNode(node.dom, true);
    if (parent.getChildNodes().getLength() < index)
    {
      throw new IllegalArgumentException("index=" + index + " length=" + parent.getChildNodes().getLength());
    }
    if (parent.getChildNodes().getLength() == index) {
      parent.appendChild(child);
    } else {
      parent.insertBefore(child, parent.getChildNodes().item(index));
    } 
  }
  
  void insertChildrenAt(int index, XmlNode[] nodes) {
    for (int i = 0; i < nodes.length; i++) {
      insertChildAt(index + i, nodes[i]);
    }
  }
  
  XmlNode getChild(int index) {
    Node child = this.dom.getChildNodes().item(index);
    return createImpl(child);
  }

  
  boolean hasChildElement() {
    NodeList nodes = this.dom.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++) {
      if (nodes.item(i).getNodeType() == 1) return true; 
    } 
    return false;
  }

  
  boolean isSameNode(XmlNode other) {
    return (this.dom == other.dom);
  }
  
  private String toUri(String ns) {
    return (ns == null) ? "" : ns;
  }
  
  private void addNamespaces(Namespaces rv, Element element) {
    if (element == null) throw new RuntimeException("element must not be null"); 
    String myDefaultNamespace = toUri(element.lookupNamespaceURI(null));
    String parentDefaultNamespace = "";
    if (element.getParentNode() != null) {
      parentDefaultNamespace = toUri(element.getParentNode().lookupNamespaceURI(null));
    }
    if (!myDefaultNamespace.equals(parentDefaultNamespace) || !(element.getParentNode() instanceof Element)) {
      rv.declare(Namespace.create("", myDefaultNamespace));
    }
    NamedNodeMap attributes = element.getAttributes();
    for (int i = 0; i < attributes.getLength(); i++) {
      Attr attr = (Attr)attributes.item(i);
      if (attr.getPrefix() != null && attr.getPrefix().equals("xmlns")) {
        rv.declare(Namespace.create(attr.getLocalName(), attr.getValue()));
      }
    } 
  }
  
  private Namespaces getAllNamespaces() {
    Namespaces rv = new Namespaces();
    
    Node target = this.dom;
    if (target instanceof Attr) {
      target = ((Attr)target).getOwnerElement();
    }
    while (target != null) {
      if (target instanceof Element) {
        addNamespaces(rv, (Element)target);
      }
      target = target.getParentNode();
    } 
    
    rv.declare(Namespace.create("", ""));
    return rv;
  }
  
  Namespace[] getInScopeNamespaces() {
    Namespaces rv = getAllNamespaces();
    return rv.getNamespaces();
  }

  
  Namespace[] getNamespaceDeclarations() {
    if (this.dom instanceof Element) {
      Namespaces rv = new Namespaces();
      addNamespaces(rv, (Element)this.dom);
      return rv.getNamespaces();
    } 
    return new Namespace[0];
  }

  
  Namespace getNamespaceDeclaration(String prefix) {
    if (prefix.equals("") && this.dom instanceof Attr)
    {
      return Namespace.create("", "");
    }
    Namespaces rv = getAllNamespaces();
    return rv.getNamespace(prefix);
  }
  
  Namespace getNamespaceDeclaration() {
    if (this.dom.getPrefix() == null) return getNamespaceDeclaration(""); 
    return getNamespaceDeclaration(this.dom.getPrefix());
  }
  
  static class XmlNodeUserDataHandler
    implements UserDataHandler, Serializable {
    private static final long serialVersionUID = 4666895518900769588L;
    
    public void handle(short operation, String key, Object data, Node src, Node dest) {}
  }
  
  private static class Namespaces {
    private Map<String, String> map = new HashMap<String, String>();
    private Map<String, String> uriToPrefix = new HashMap<String, String>();



    
    void declare(XmlNode.Namespace n) {
      if (this.map.get(n.prefix) == null) {
        this.map.put(n.prefix, n.uri);
      }

      
      if (this.uriToPrefix.get(n.uri) == null) {
        this.uriToPrefix.put(n.uri, n.prefix);
      }
    }
    
    XmlNode.Namespace getNamespaceByUri(String uri) {
      if (this.uriToPrefix.get(uri) == null) return null; 
      return XmlNode.Namespace.create(uri, this.uriToPrefix.get(uri));
    }
    
    XmlNode.Namespace getNamespace(String prefix) {
      if (this.map.get(prefix) == null) return null; 
      return XmlNode.Namespace.create(prefix, this.map.get(prefix));
    }
    
    XmlNode.Namespace[] getNamespaces() {
      ArrayList<XmlNode.Namespace> rv = new ArrayList<XmlNode.Namespace>();
      for (String prefix : this.map.keySet()) {
        String uri = this.map.get(prefix);
        XmlNode.Namespace n = XmlNode.Namespace.create(prefix, uri);
        if (!n.isEmpty()) {
          rv.add(n);
        }
      } 
      return rv.<XmlNode.Namespace>toArray(new XmlNode.Namespace[rv.size()]);
    }
  }
  
  final XmlNode copy() {
    return copy(this);
  }

  
  final boolean isParentType() {
    return isElementType();
  }
  
  final boolean isTextType() {
    return (this.dom.getNodeType() == 3 || this.dom.getNodeType() == 4);
  }
  
  final boolean isAttributeType() {
    return (this.dom.getNodeType() == 2);
  }
  
  final boolean isProcessingInstructionType() {
    return (this.dom.getNodeType() == 7);
  }
  
  final boolean isCommentType() {
    return (this.dom.getNodeType() == 8);
  }
  
  final boolean isElementType() {
    return (this.dom.getNodeType() == 1);
  }
  
  final void renameNode(QName qname) {
    this.dom = this.dom.getOwnerDocument().renameNode(this.dom, qname.getNamespace().getUri(), qname.qualify(this.dom));
  }
  
  void invalidateNamespacePrefix() {
    if (!(this.dom instanceof Element)) throw new IllegalStateException(); 
    String prefix = this.dom.getPrefix();
    QName after = QName.create(this.dom.getNamespaceURI(), this.dom.getLocalName(), null);
    renameNode(after);
    NamedNodeMap attrs = this.dom.getAttributes();
    for (int i = 0; i < attrs.getLength(); i++) {
      if (attrs.item(i).getPrefix().equals(prefix)) {
        createImpl(attrs.item(i)).renameNode(QName.create(attrs.item(i).getNamespaceURI(), attrs.item(i).getLocalName(), null));
      }
    } 
  }
  
  private void declareNamespace(Element e, String prefix, String uri) {
    if (prefix.length() > 0) {
      e.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, uri);
    } else {
      e.setAttribute("xmlns", uri);
    } 
  }
  
  void declareNamespace(String prefix, String uri) {
    if (!(this.dom instanceof Element)) throw new IllegalStateException(); 
    if (this.dom.lookupNamespaceURI(uri) == null || !this.dom.lookupNamespaceURI(uri).equals(prefix)) {

      
      Element e = (Element)this.dom;
      declareNamespace(e, prefix, uri);
    } 
  }
  
  private Namespace getDefaultNamespace() {
    String prefix = "";
    String uri = (this.dom.lookupNamespaceURI(null) == null) ? "" : this.dom.lookupNamespaceURI(null);
    return Namespace.create(prefix, uri);
  }
  
  private String getExistingPrefixFor(Namespace namespace) {
    if (getDefaultNamespace().getUri().equals(namespace.getUri())) {
      return "";
    }
    return this.dom.lookupPrefix(namespace.getUri());
  }
  
  private Namespace getNodeNamespace() {
    String uri = this.dom.getNamespaceURI();
    String prefix = this.dom.getPrefix();
    if (uri == null) uri = ""; 
    if (prefix == null) prefix = ""; 
    return Namespace.create(prefix, uri);
  }
  
  Namespace getNamespace() {
    return getNodeNamespace();
  }
  
  void removeNamespace(Namespace namespace) {
    Namespace current = getNodeNamespace();

    
    if (namespace.is(current))
      return;  NamedNodeMap attrs = this.dom.getAttributes();
    for (int i = 0; i < attrs.getLength(); i++) {
      XmlNode attr = createImpl(attrs.item(i));
      if (namespace.is(attr.getNodeNamespace())) {
        return;
      }
    } 
    String existingPrefix = getExistingPrefixFor(namespace);
    if (existingPrefix != null) {
      if (namespace.isUnspecifiedPrefix()) {

        
        declareNamespace(existingPrefix, getDefaultNamespace().getUri());
      }
      else if (existingPrefix.equals(namespace.getPrefix())) {
        declareNamespace(existingPrefix, getDefaultNamespace().getUri());
      } 
    }
  }



  
  private void setProcessingInstructionName(String localName) {
    ProcessingInstruction pi = (ProcessingInstruction)this.dom;
    
    pi.getParentNode().replaceChild(pi, pi.getOwnerDocument().createProcessingInstruction(localName, pi.getData()));
  }



  
  final void setLocalName(String localName) {
    if (this.dom instanceof ProcessingInstruction) {
      setProcessingInstructionName(localName);
    } else {
      String prefix = this.dom.getPrefix();
      if (prefix == null) prefix = ""; 
      this.dom = this.dom.getOwnerDocument().renameNode(this.dom, this.dom.getNamespaceURI(), QName.qualify(prefix, localName));
    } 
  }
  
  final QName getQname() {
    String uri = (this.dom.getNamespaceURI() == null) ? "" : this.dom.getNamespaceURI();
    String prefix = (this.dom.getPrefix() == null) ? "" : this.dom.getPrefix();
    return QName.create(uri, this.dom.getLocalName(), prefix);
  }
  
  void addMatchingChildren(XMLList result, Filter filter) {
    Node node = this.dom;
    NodeList children = node.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      Node childnode = children.item(i);
      XmlNode child = createImpl(childnode);
      if (filter.accept(childnode)) {
        result.addToList(child);
      }
    } 
  }
  
  XmlNode[] getMatchingChildren(Filter filter) {
    ArrayList<XmlNode> rv = new ArrayList<XmlNode>();
    NodeList nodes = this.dom.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++) {
      Node node = nodes.item(i);
      if (filter.accept(node)) {
        rv.add(createImpl(node));
      }
    } 
    return rv.<XmlNode>toArray(new XmlNode[rv.size()]);
  }
  
  XmlNode[] getAttributes() {
    NamedNodeMap attrs = this.dom.getAttributes();
    
    if (attrs == null) throw new IllegalStateException("Must be element."); 
    XmlNode[] rv = new XmlNode[attrs.getLength()];
    for (int i = 0; i < attrs.getLength(); i++) {
      rv[i] = createImpl(attrs.item(i));
    }
    return rv;
  }
  
  String getAttributeValue() {
    return ((Attr)this.dom).getValue();
  }
  
  void setAttribute(QName name, String value) {
    if (!(this.dom instanceof Element)) throw new IllegalStateException("Can only set attribute on elements."); 
    name.setAttribute((Element)this.dom, value);
  }
  
  void replaceWith(XmlNode other) {
    Node replacement = other.dom;
    if (replacement.getOwnerDocument() != this.dom.getOwnerDocument()) {
      replacement = this.dom.getOwnerDocument().importNode(replacement, true);
    }
    this.dom.getParentNode().replaceChild(replacement, this.dom);
  }
  
  String ecmaToXMLString(XmlProcessor processor) {
    if (isElementType()) {
      Element copy = (Element)this.dom.cloneNode(true);
      Namespace[] inScope = getInScopeNamespaces();
      for (int i = 0; i < inScope.length; i++) {
        declareNamespace(copy, inScope[i].getPrefix(), inScope[i].getUri());
      }
      return processor.ecmaToXmlString(copy);
    } 
    return processor.ecmaToXmlString(this.dom);
  }


  
  static class Namespace
    implements Serializable
  {
    private static final long serialVersionUID = 4073904386884677090L;

    
    static Namespace create(String prefix, String uri) {
      if (prefix == null) {
        throw new IllegalArgumentException("Empty string represents default namespace prefix");
      }
      
      if (uri == null) {
        throw new IllegalArgumentException("Namespace may not lack a URI");
      }
      
      Namespace rv = new Namespace();
      rv.prefix = prefix;
      rv.uri = uri;
      return rv;
    }
    
    static Namespace create(String uri) {
      Namespace rv = new Namespace();
      rv.uri = uri;

      
      if (uri == null || uri.length() == 0) {
        rv.prefix = "";
      }
      
      return rv;
    }
    
    static final Namespace GLOBAL = create("", "");

    
    private String prefix;

    
    private String uri;

    
    public String toString() {
      if (this.prefix == null) return "XmlNode.Namespace [" + this.uri + "]"; 
      return "XmlNode.Namespace [" + this.prefix + "{" + this.uri + "}]";
    }
    
    boolean isUnspecifiedPrefix() {
      return (this.prefix == null);
    }
    
    boolean is(Namespace other) {
      return (this.prefix != null && other.prefix != null && this.prefix.equals(other.prefix) && this.uri.equals(other.uri));
    }
    
    boolean isEmpty() {
      return (this.prefix != null && this.prefix.equals("") && this.uri.equals(""));
    }
    
    boolean isDefault() {
      return (this.prefix != null && this.prefix.equals(""));
    }
    
    boolean isGlobal() {
      return (this.uri != null && this.uri.equals(""));
    }


    
    private void setPrefix(String prefix) {
      if (prefix == null) throw new IllegalArgumentException(); 
      this.prefix = prefix;
    }
    
    String getPrefix() {
      return this.prefix;
    }
    
    String getUri() {
      return this.uri;
    }
  }
  
  static class QName
    implements Serializable {
    private static final long serialVersionUID = -6587069811691451077L;
    private XmlNode.Namespace namespace;
    private String localName;
    
    static QName create(XmlNode.Namespace namespace, String localName) {
      if (localName != null && localName.equals("*")) throw new RuntimeException("* is not valid localName"); 
      QName rv = new QName();
      rv.namespace = namespace;
      rv.localName = localName;
      return rv;
    }

    
    @Deprecated
    static QName create(String uri, String localName, String prefix) {
      return create(XmlNode.Namespace.create(prefix, uri), localName);
    }
    
    static String qualify(String prefix, String localName) {
      if (prefix == null) throw new IllegalArgumentException("prefix must not be null"); 
      if (prefix.length() > 0) return prefix + ":" + localName; 
      return localName;
    }







    
    public String toString() {
      return "XmlNode.QName [" + this.localName + "," + this.namespace + "]";
    }
    
    private boolean equals(String one, String two) {
      if (one == null && two == null) return true; 
      if (one == null || two == null) return false; 
      return one.equals(two);
    }
    
    private boolean namespacesEqual(XmlNode.Namespace one, XmlNode.Namespace two) {
      if (one == null && two == null) return true; 
      if (one == null || two == null) return false; 
      return equals(one.getUri(), two.getUri());
    }
    
    final boolean equals(QName other) {
      if (!namespacesEqual(this.namespace, other.namespace)) return false; 
      if (!equals(this.localName, other.localName)) return false; 
      return true;
    }

    
    public boolean equals(Object obj) {
      if (!(obj instanceof QName)) {
        return false;
      }
      return equals((QName)obj);
    }

    
    public int hashCode() {
      return (this.localName == null) ? 0 : this.localName.hashCode();
    }
    
    void lookupPrefix(Node node) {
      if (node == null) throw new IllegalArgumentException("node must not be null"); 
      String prefix = node.lookupPrefix(this.namespace.getUri());
      if (prefix == null) {
        
        String defaultNamespace = node.lookupNamespaceURI(null);
        if (defaultNamespace == null) defaultNamespace = ""; 
        String nodeNamespace = this.namespace.getUri();
        if (nodeNamespace.equals(defaultNamespace)) {
          prefix = "";
        }
      } 
      int i = 0;
      while (prefix == null) {
        String generatedPrefix = "e4x_" + i++;
        String generatedUri = node.lookupNamespaceURI(generatedPrefix);
        if (generatedUri == null) {
          prefix = generatedPrefix;
          Node top = node;
          while (top.getParentNode() != null && top.getParentNode() instanceof Element) {
            top = top.getParentNode();
          }
          ((Element)top).setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, this.namespace.getUri());
        } 
      } 
      this.namespace.setPrefix(prefix);
    }
    
    String qualify(Node node) {
      if (this.namespace.getPrefix() == null) {
        if (node != null) {
          lookupPrefix(node);
        }
        else if (this.namespace.getUri().equals("")) {
          this.namespace.setPrefix("");
        }
        else {
          
          this.namespace.setPrefix("");
        } 
      }
      
      return qualify(this.namespace.getPrefix(), this.localName);
    }
    
    void setAttribute(Element element, String value) {
      if (this.namespace.getPrefix() == null) lookupPrefix(element); 
      element.setAttributeNS(this.namespace.getUri(), qualify(this.namespace.getPrefix(), this.localName), value);
    }
    
    XmlNode.Namespace getNamespace() {
      return this.namespace;
    }
    
    String getLocalName() {
      return this.localName;
    }
  }

  
  static class InternalList
    implements Serializable
  {
    private static final long serialVersionUID = -3633151157292048978L;
    private List<XmlNode> list = new ArrayList<XmlNode>();

    
    private void _add(XmlNode n) {
      this.list.add(n);
    }
    
    XmlNode item(int index) {
      return this.list.get(index);
    }
    
    void remove(int index) {
      this.list.remove(index);
    }
    
    void add(InternalList other) {
      for (int i = 0; i < other.length(); i++) {
        _add(other.item(i));
      }
    }
    
    void add(InternalList from, int startInclusive, int endExclusive) {
      for (int i = startInclusive; i < endExclusive; i++) {
        _add(from.item(i));
      }
    }
    
    void add(XmlNode node) {
      _add(node);
    }

    
    void add(XML xml) {
      _add(xml.getAnnotation());
    }

    
    void addToList(Object toAdd) {
      if (toAdd instanceof org.mozilla.javascript.Undefined) {
        return;
      }

      
      if (toAdd instanceof XMLList) {
        XMLList xmlSrc = (XMLList)toAdd;
        for (int i = 0; i < xmlSrc.length(); i++) {
          _add(xmlSrc.item(i).getAnnotation());
        }
      } else if (toAdd instanceof XML) {
        _add(((XML)toAdd).getAnnotation());
      } else if (toAdd instanceof XmlNode) {
        _add((XmlNode)toAdd);
      } 
    }
    
    int length() {
      return this.list.size();
    }
  }
  
  static abstract class Filter {
    static final Filter COMMENT = new Filter()
      {
        boolean accept(Node node) {
          return (node.getNodeType() == 8);
        }
      };
    static final Filter TEXT = new Filter()
      {
        boolean accept(Node node) {
          return (node.getNodeType() == 3);
        }
      };
    static Filter PROCESSING_INSTRUCTION(final XMLName name) {
      return new Filter()
        {
          boolean accept(Node node) {
            if (node.getNodeType() == 7) {
              ProcessingInstruction pi = (ProcessingInstruction)node;
              return name.matchesLocalName(pi.getTarget());
            } 
            return false;
          }
        };
    }
    static Filter ELEMENT = new Filter()
      {
        boolean accept(Node node) {
          return (node.getNodeType() == 1);
        }
      };
    static Filter TRUE = new Filter()
      {
        boolean accept(Node node) {
          return true;
        }
      };
    
    abstract boolean accept(Node param1Node);
  }
  
  Node toDomNode() {
    return this.dom;
  }
}
