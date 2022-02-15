package org.mozilla.javascript.xmlimpl;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionCall;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.NativeWith;
import org.mozilla.javascript.Ref;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.xml.XMLObject;



abstract class XMLObjectImpl
  extends XMLObject
{
  private static final Object XMLOBJECT_TAG = "XMLObject"; private XMLLibImpl lib; private boolean prototypeFlag; private static final int Id_constructor = 1; private static final int Id_addNamespace = 2; private static final int Id_appendChild = 3; private static final int Id_attribute = 4; private static final int Id_attributes = 5; private static final int Id_child = 6; private static final int Id_childIndex = 7; private static final int Id_children = 8; private static final int Id_comments = 9; private static final int Id_contains = 10; private static final int Id_copy = 11; private static final int Id_descendants = 12; private static final int Id_elements = 13; private static final int Id_inScopeNamespaces = 14; private static final int Id_insertChildAfter = 15; private static final int Id_insertChildBefore = 16;
  private static final int Id_hasOwnProperty = 17;
  private static final int Id_hasComplexContent = 18;
  private static final int Id_hasSimpleContent = 19;
  private static final int Id_length = 20;
  
  protected XMLObjectImpl(XMLLibImpl lib, Scriptable scope, XMLObject prototype) {
    initialize(lib, scope, prototype);
  }
  private static final int Id_localName = 21; private static final int Id_name = 22; private static final int Id_namespace = 23; private static final int Id_namespaceDeclarations = 24; private static final int Id_nodeKind = 25; private static final int Id_normalize = 26; private static final int Id_parent = 27; private static final int Id_prependChild = 28; private static final int Id_processingInstructions = 29; private static final int Id_propertyIsEnumerable = 30;
  private static final int Id_removeNamespace = 31;
  
  final void initialize(XMLLibImpl lib, Scriptable scope, XMLObject prototype) {
    setParentScope(scope);
    setPrototype((Scriptable)prototype);
    this.prototypeFlag = (prototype == null);
    this.lib = lib;
  }
  private static final int Id_replace = 32; private static final int Id_setChildren = 33; private static final int Id_setLocalName = 34; private static final int Id_setName = 35; private static final int Id_setNamespace = 36; private static final int Id_text = 37; private static final int Id_toString = 38; private static final int Id_toSource = 39; private static final int Id_toXMLString = 40; private static final int Id_valueOf = 41; private static final int MAX_PROTOTYPE_ID = 41;
  final boolean isPrototype() {
    return this.prototypeFlag;
  }
  
  XMLLibImpl getLib() {
    return this.lib;
  }
  
  final XML newXML(XmlNode node) {
    return this.lib.newXML(node);
  }
  
  XML xmlFromNode(XmlNode node) {
    if (node.getXml() == null) {
      node.setXml(newXML(node));
    }
    return node.getXml();
  }
  
  final XMLList newXMLList() {
    return this.lib.newXMLList();
  }
  
  final XMLList newXMLListFrom(Object o) {
    return this.lib.newXMLListFrom(o);
  }
  
  final XmlProcessor getProcessor() {
    return this.lib.getProcessor();
  }
  
  final QName newQName(String uri, String localName, String prefix) {
    return this.lib.newQName(uri, localName, prefix);
  }
  
  final QName newQName(XmlNode.QName name) {
    return this.lib.newQName(name);
  }
  
  final Namespace createNamespace(XmlNode.Namespace declaration) {
    if (declaration == null) return null; 
    return this.lib.createNamespaces(new XmlNode.Namespace[] { declaration })[0];
  }
  
  final Namespace[] createNamespaces(XmlNode.Namespace[] declarations) {
    return this.lib.createNamespaces(declarations);
  }


  
  public final Scriptable getPrototype() {
    return super.getPrototype();
  }

  
  public final void setPrototype(Scriptable prototype) {
    super.setPrototype(prototype);
  }

  
  public final Scriptable getParentScope() {
    return super.getParentScope();
  }

  
  public final void setParentScope(Scriptable parent) {
    super.setParentScope(parent);
  }

  
  public final Object getDefaultValue(Class<?> hint) {
    return toString();
  }

  
  public final boolean hasInstance(Scriptable scriptable) {
    return super.hasInstance(scriptable);
  }



  
  abstract boolean hasXMLProperty(XMLName paramXMLName);



  
  abstract Object getXMLProperty(XMLName paramXMLName);



  
  abstract void putXMLProperty(XMLName paramXMLName, Object paramObject);



  
  abstract void deleteXMLProperty(XMLName paramXMLName);



  
  abstract boolean equivalentXml(Object paramObject);


  
  abstract void addMatches(XMLList paramXMLList, XMLName paramXMLName);


  
  private XMLList getMatches(XMLName name) {
    XMLList rv = newXMLList();
    addMatches(rv, name);
    return rv;
  }
  abstract XML getXML();
  abstract XMLList child(int paramInt);
  abstract XMLList child(XMLName paramXMLName);
  abstract XMLList children();
  abstract XMLList comments();
  
  abstract boolean contains(Object paramObject);
  
  abstract XMLObjectImpl copy();
  
  abstract XMLList elements(XMLName paramXMLName);
  
  abstract boolean hasOwnProperty(XMLName paramXMLName);
  
  abstract boolean hasComplexContent();
  
  abstract boolean hasSimpleContent();
  
  abstract int length();
  
  abstract void normalize();
  
  abstract Object parent();
  
  abstract XMLList processingInstructions(XMLName paramXMLName);
  
  abstract boolean propertyIsEnumerable(Object paramObject);
  
  abstract XMLList text();
  
  public abstract String toString();
  
  abstract String toSource(int paramInt);
  
  abstract String toXMLString();
  
  abstract Object valueOf();
  
  protected abstract Object jsConstructor(Context paramContext, boolean paramBoolean, Object[] paramArrayOfObject);
  
  protected final Object equivalentValues(Object value) {
    boolean result = equivalentXml(value);
    return result ? Boolean.TRUE : Boolean.FALSE;
  }










  
  public final boolean has(Context cx, Object id) {
    if (cx == null) cx = Context.getCurrentContext(); 
    XMLName xmlName = this.lib.toXMLNameOrIndex(cx, id);
    if (xmlName == null) {
      long index = ScriptRuntime.lastUint32Result(cx);
      
      return has((int)index, (Scriptable)this);
    } 
    return hasXMLProperty(xmlName);
  }

  
  public boolean has(String name, Scriptable start) {
    Context cx = Context.getCurrentContext();
    return hasXMLProperty(this.lib.toXMLNameFromString(cx, name));
  }



  
  public final Object get(Context cx, Object id) {
    if (cx == null) cx = Context.getCurrentContext(); 
    XMLName xmlName = this.lib.toXMLNameOrIndex(cx, id);
    if (xmlName == null) {
      long index = ScriptRuntime.lastUint32Result(cx);
      
      Object result = get((int)index, (Scriptable)this);
      if (result == Scriptable.NOT_FOUND) {
        result = Undefined.instance;
      }
      return result;
    } 
    return getXMLProperty(xmlName);
  }

  
  public Object get(String name, Scriptable start) {
    Context cx = Context.getCurrentContext();
    return getXMLProperty(this.lib.toXMLNameFromString(cx, name));
  }



  
  public final void put(Context cx, Object id, Object value) {
    if (cx == null) cx = Context.getCurrentContext(); 
    XMLName xmlName = this.lib.toXMLNameOrIndex(cx, id);
    if (xmlName == null) {
      long index = ScriptRuntime.lastUint32Result(cx);
      
      put((int)index, (Scriptable)this, value);
      return;
    } 
    putXMLProperty(xmlName, value);
  }

  
  public void put(String name, Scriptable start, Object value) {
    Context cx = Context.getCurrentContext();
    putXMLProperty(this.lib.toXMLNameFromString(cx, name), value);
  }



  
  public final boolean delete(Context cx, Object id) {
    if (cx == null) cx = Context.getCurrentContext(); 
    XMLName xmlName = this.lib.toXMLNameOrIndex(cx, id);
    if (xmlName == null) {
      long index = ScriptRuntime.lastUint32Result(cx);
      
      delete((int)index);
      return true;
    } 
    deleteXMLProperty(xmlName);
    return true;
  }


  
  public void delete(String name) {
    Context cx = Context.getCurrentContext();
    deleteXMLProperty(this.lib.toXMLNameFromString(cx, name));
  }

  
  public Object getFunctionProperty(Context cx, int id) {
    if (isPrototype()) {
      return get(id, (Scriptable)this);
    }
    Scriptable proto = getPrototype();
    if (proto instanceof XMLObject) {
      return ((XMLObject)proto).getFunctionProperty(cx, id);
    }
    
    return NOT_FOUND;
  }

  
  public Object getFunctionProperty(Context cx, String name) {
    if (isPrototype()) {
      return super.get(name, (Scriptable)this);
    }
    Scriptable proto = getPrototype();
    if (proto instanceof XMLObject) {
      return ((XMLObject)proto).getFunctionProperty(cx, name);
    }
    
    return NOT_FOUND;
  }


  
  public Ref memberRef(Context cx, Object elem, int memberTypeFlags) {
    boolean attribute = ((memberTypeFlags & 0x2) != 0);
    boolean descendants = ((memberTypeFlags & 0x4) != 0);
    if (!attribute && !descendants)
    {

      
      throw Kit.codeBug();
    }
    XmlNode.QName qname = this.lib.toNodeQName(cx, elem, attribute);
    XMLName rv = XMLName.create(qname, attribute, descendants);
    rv.initXMLObject(this);
    return rv;
  }






  
  public Ref memberRef(Context cx, Object namespace, Object elem, int memberTypeFlags) {
    boolean attribute = ((memberTypeFlags & 0x2) != 0);
    boolean descendants = ((memberTypeFlags & 0x4) != 0);
    XMLName rv = XMLName.create(this.lib.toNodeQName(cx, namespace, elem), attribute, descendants);
    
    rv.initXMLObject(this);
    return rv;
  }

  
  public NativeWith enterWith(Scriptable scope) {
    return new XMLWithScope(this.lib, scope, this);
  }

  
  public NativeWith enterDotQuery(Scriptable scope) {
    XMLWithScope xws = new XMLWithScope(this.lib, scope, this);
    xws.initAsDotQuery();
    return xws;
  }


  
  public final Object addValues(Context cx, boolean thisIsLeft, Object value) {
    if (value instanceof XMLObject) {
      XMLObject v1; XMLObject v2;
      if (thisIsLeft) {
        v1 = this;
        v2 = (XMLObject)value;
      } else {
        v1 = (XMLObject)value;
        v2 = this;
      } 
      return this.lib.addXMLObjects(cx, v1, v2);
    } 
    if (value == Undefined.instance)
    {
      return ScriptRuntime.toString(this);
    }
    
    return super.addValues(cx, thisIsLeft, value);
  }






  
  final void exportAsJSClass(boolean sealed) {
    this.prototypeFlag = true;
    exportAsJSClass(41, getParentScope(), sealed);
  }


















































  
  protected int findPrototypeId(String s) {
    int c, id = 0; String X = null;
    switch (s.length()) { case 4:
        c = s.charAt(0);
        if (c == 99) { X = "copy"; id = 11; break; }
         if (c == 110) { X = "name"; id = 22; break; }
         if (c == 116) { X = "text"; id = 37; }  break;
      case 5:
        X = "child"; id = 6; break;
      case 6: c = s.charAt(0);
        if (c == 108) { X = "length"; id = 20; break; }
         if (c == 112) { X = "parent"; id = 27; }  break;
      case 7:
        c = s.charAt(0);
        if (c == 114) { X = "replace"; id = 32; break; }
         if (c == 115) { X = "setName"; id = 35; break; }
         if (c == 118) { X = "valueOf"; id = 41; }  break;
      case 8:
        switch (s.charAt(2)) { case 'S':
            c = s.charAt(7);
            if (c == 101) { X = "toSource"; id = 39; break; }
             if (c == 103) { X = "toString"; id = 38; }  break;
          case 'd':
            X = "nodeKind"; id = 25; break;
          case 'e': X = "elements"; id = 13; break;
          case 'i': X = "children"; id = 8; break;
          case 'm': X = "comments"; id = 9; break;
          case 'n': X = "contains"; id = 10; break; }  break;
      case 9:
        switch (s.charAt(2)) { case 'c':
            X = "localName"; id = 21; break;
          case 'm': X = "namespace"; id = 23; break;
          case 'r': X = "normalize"; id = 26; break;
          case 't': X = "attribute"; id = 4; break; }  break;
      case 10:
        c = s.charAt(0);
        if (c == 97) { X = "attributes"; id = 5; break; }
         if (c == 99) { X = "childIndex"; id = 7; }  break;
      case 11:
        switch (s.charAt(0)) { case 'a':
            X = "appendChild"; id = 3; break;
          case 'c': X = "constructor"; id = 1; break;
          case 'd': X = "descendants"; id = 12; break;
          case 's': X = "setChildren"; id = 33; break;
          case 't': X = "toXMLString"; id = 40; break; }  break;
      case 12:
        c = s.charAt(0);
        if (c == 97) { X = "addNamespace"; id = 2; break; }
         if (c == 112) { X = "prependChild"; id = 28; break; }
         if (c == 115) {
          c = s.charAt(3);
          if (c == 76) { X = "setLocalName"; id = 34; break; }
           if (c == 78) { X = "setNamespace"; id = 36; } 
        }  break;
      case 14:
        X = "hasOwnProperty"; id = 17; break;
      case 15: X = "removeNamespace"; id = 31; break;
      case 16: c = s.charAt(0);
        if (c == 104) { X = "hasSimpleContent"; id = 19; break; }
         if (c == 105) { X = "insertChildAfter"; id = 15; }  break;
      case 17:
        c = s.charAt(3);
        if (c == 67) { X = "hasComplexContent"; id = 18; break; }
         if (c == 99) { X = "inScopeNamespaces"; id = 14; break; }
         if (c == 101) { X = "insertChildBefore"; id = 16; }  break;
      case 20:
        X = "propertyIsEnumerable"; id = 30; break;
      case 21: X = "namespaceDeclarations"; id = 24; break;
      case 22: X = "processingInstructions"; id = 29; break; }
    
    if (X != null && X != s && !X.equals(s)) id = 0;


    
    return id;
  }

  
  protected void initPrototypeId(int id) {
    String s;
    int arity;
    IdFunctionObject ctor;
    switch (id) {
      
      case 1:
        if (this instanceof XML) {
          ctor = new XMLCtor((XML)this, XMLOBJECT_TAG, id, 1);
        } else {
          ctor = new IdFunctionObject((IdFunctionCall)this, XMLOBJECT_TAG, id, 1);
        } 
        initPrototypeConstructor(ctor);
        return;
      
      case 2:
        arity = 1; s = "addNamespace"; break;
      case 3: arity = 1; s = "appendChild"; break;
      case 4: arity = 1; s = "attribute"; break;
      case 5: arity = 0; s = "attributes"; break;
      case 6: arity = 1; s = "child"; break;
      case 7: arity = 0; s = "childIndex"; break;
      case 8: arity = 0; s = "children"; break;
      case 9: arity = 0; s = "comments"; break;
      case 10: arity = 1; s = "contains"; break;
      case 11: arity = 0; s = "copy"; break;
      case 12: arity = 1; s = "descendants"; break;
      case 13: arity = 1; s = "elements"; break;
      case 18: arity = 0; s = "hasComplexContent"; break;
      case 17: arity = 1; s = "hasOwnProperty"; break;
      case 19: arity = 0; s = "hasSimpleContent"; break;
      case 14: arity = 0; s = "inScopeNamespaces"; break;
      case 15: arity = 2; s = "insertChildAfter"; break;
      case 16: arity = 2; s = "insertChildBefore"; break;
      case 20: arity = 0; s = "length"; break;
      case 21: arity = 0; s = "localName"; break;
      case 22: arity = 0; s = "name"; break;
      case 23: arity = 1; s = "namespace"; break;
      case 24:
        arity = 0; s = "namespaceDeclarations"; break;
      case 25: arity = 0; s = "nodeKind"; break;
      case 26: arity = 0; s = "normalize"; break;
      case 27: arity = 0; s = "parent"; break;
      case 28: arity = 1; s = "prependChild"; break;
      case 29:
        arity = 1; s = "processingInstructions"; break;
      case 30:
        arity = 1; s = "propertyIsEnumerable"; break;
      case 31: arity = 1; s = "removeNamespace"; break;
      case 32: arity = 2; s = "replace"; break;
      case 33: arity = 1; s = "setChildren"; break;
      case 34: arity = 1; s = "setLocalName"; break;
      case 35: arity = 1; s = "setName"; break;
      case 36: arity = 1; s = "setNamespace"; break;
      case 37: arity = 0; s = "text"; break;
      case 38: arity = 0; s = "toString"; break;
      case 39: arity = 1; s = "toSource"; break;
      case 40: arity = 1; s = "toXMLString"; break;
      case 41: arity = 0; s = "valueOf"; break;
      default:
        throw new IllegalArgumentException(String.valueOf(id));
    } 
    initPrototypeMethod(XMLOBJECT_TAG, id, s, arity);
  }
  
  private Object[] toObjectArray(Object[] typed) {
    Object[] rv = new Object[typed.length];
    for (int i = 0; i < rv.length; i++) {
      rv[i] = typed[i];
    }
    return rv;
  }
  
  private void xmlMethodNotFound(Object object, String name) {
    throw ScriptRuntime.notFunctionError(object, name); } public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) { Namespace namespace2; Object arg0; String prefix; Namespace array[], namespace1; XMLName xMLName2; String localName; Object arg; Namespace ns; XMLName xMLName1;
    XmlNode.QName qname;
    XMLName xmlName;
    int indent;
    Namespace rv;
    Object arg1, object1;
    QName qName;
    if (!f.hasTag(XMLOBJECT_TAG)) {
      return super.execIdCall(f, cx, scope, thisObj, args);
    }
    int id = f.methodId();
    if (id == 1) {
      return jsConstructor(cx, (thisObj == null), args);
    }

    
    if (!(thisObj instanceof XMLObjectImpl))
      throw incompatibleCallError(f); 
    XMLObjectImpl realThis = (XMLObjectImpl)thisObj;
    
    XML xml = realThis.getXML();
    switch (id) {
      case 3:
        if (xml == null) xmlMethodNotFound(realThis, "appendChild"); 
        return xml.appendChild(arg(args, 0));
      
      case 2:
        if (xml == null) xmlMethodNotFound(realThis, "addNamespace"); 
        namespace2 = this.lib.castToNamespace(cx, arg(args, 0));
        return xml.addNamespace(namespace2);
      
      case 7:
        if (xml == null) xmlMethodNotFound(realThis, "childIndex"); 
        return ScriptRuntime.wrapInt(xml.childIndex());
      
      case 14:
        if (xml == null) xmlMethodNotFound(realThis, "inScopeNamespaces"); 
        return cx.newArray(scope, toObjectArray((Object[])xml.inScopeNamespaces()));
      
      case 15:
        if (xml == null) xmlMethodNotFound(realThis, "insertChildAfter"); 
        arg0 = arg(args, 0);
        if (arg0 == null || arg0 instanceof XML) {
          return xml.insertChildAfter((XML)arg0, arg(args, 1));
        }
        return Undefined.instance;
      
      case 16:
        if (xml == null) xmlMethodNotFound(realThis, "insertChildBefore"); 
        arg0 = arg(args, 0);
        if (arg0 == null || arg0 instanceof XML) {
          return xml.insertChildBefore((XML)arg0, arg(args, 1));
        }
        return Undefined.instance;
      
      case 21:
        if (xml == null) xmlMethodNotFound(realThis, "localName"); 
        return xml.localName();
      
      case 22:
        if (xml == null) xmlMethodNotFound(realThis, "name"); 
        return xml.name();
      
      case 23:
        if (xml == null) xmlMethodNotFound(realThis, "namespace"); 
        prefix = (args.length > 0) ? ScriptRuntime.toString(args[0]) : null;
        rv = xml.namespace(prefix);
        if (rv == null) {
          return Undefined.instance;
        }
        return rv;

      
      case 24:
        if (xml == null) xmlMethodNotFound(realThis, "namespaceDeclarations"); 
        array = xml.namespaceDeclarations();
        return cx.newArray(scope, toObjectArray((Object[])array));
      
      case 25:
        if (xml == null) xmlMethodNotFound(realThis, "nodeKind"); 
        return xml.nodeKind();
      
      case 28:
        if (xml == null) xmlMethodNotFound(realThis, "prependChild"); 
        return xml.prependChild(arg(args, 0));
      
      case 31:
        if (xml == null) xmlMethodNotFound(realThis, "removeNamespace"); 
        namespace1 = this.lib.castToNamespace(cx, arg(args, 0));
        return xml.removeNamespace(namespace1);
      
      case 32:
        if (xml == null) xmlMethodNotFound(realThis, "replace"); 
        xMLName2 = this.lib.toXMLNameOrIndex(cx, arg(args, 0));
        arg1 = arg(args, 1);
        if (xMLName2 == null) {
          
          int index = (int)ScriptRuntime.lastUint32Result(cx);
          return xml.replace(index, arg1);
        } 
        return xml.replace(xMLName2, arg1);

      
      case 33:
        if (xml == null) xmlMethodNotFound(realThis, "setChildren"); 
        return xml.setChildren(arg(args, 0));
      
      case 34:
        if (xml == null) xmlMethodNotFound(realThis, "setLocalName");
        
        object1 = arg(args, 0);
        if (object1 instanceof QName) {
          localName = ((QName)object1).localName();
        } else {
          localName = ScriptRuntime.toString(object1);
        } 
        xml.setLocalName(localName);
        return Undefined.instance;
      
      case 35:
        if (xml == null) xmlMethodNotFound(realThis, "setName"); 
        arg = (args.length != 0) ? args[0] : Undefined.instance;
        qName = this.lib.constructQName(cx, arg);
        xml.setName(qName);
        return Undefined.instance;
      
      case 36:
        if (xml == null) xmlMethodNotFound(realThis, "setNamespace"); 
        ns = this.lib.castToNamespace(cx, arg(args, 0));
        xml.setNamespace(ns);
        return Undefined.instance;

      
      case 4:
        xMLName1 = XMLName.create(this.lib.toNodeQName(cx, arg(args, 0), true), true, false);
        return realThis.getMatches(xMLName1);
      
      case 5:
        return realThis.getMatches(XMLName.create(XmlNode.QName.create(null, null), true, false));
      case 6:
        xMLName1 = this.lib.toXMLNameOrIndex(cx, arg(args, 0));
        if (xMLName1 == null) {
          
          int index = (int)ScriptRuntime.lastUint32Result(cx);
          return realThis.child(index);
        } 
        return realThis.child(xMLName1);

      
      case 8:
        return realThis.children();
      case 9:
        return realThis.comments();
      case 10:
        return ScriptRuntime.wrapBoolean(realThis.contains(arg(args, 0)));
      
      case 11:
        return realThis.copy();
      case 12:
        qname = (args.length == 0) ? XmlNode.QName.create(null, null) : this.lib.toNodeQName(cx, args[0], false);
        return realThis.getMatches(XMLName.create(qname, false, true));
      
      case 13:
        xmlName = (args.length == 0) ? XMLName.formStar() : this.lib.toXMLName(cx, args[0]);

        
        return realThis.elements(xmlName);
      
      case 17:
        xmlName = this.lib.toXMLName(cx, arg(args, 0));
        return ScriptRuntime.wrapBoolean(realThis.hasOwnProperty(xmlName));

      
      case 18:
        return ScriptRuntime.wrapBoolean(realThis.hasComplexContent());
      case 19:
        return ScriptRuntime.wrapBoolean(realThis.hasSimpleContent());
      case 20:
        return ScriptRuntime.wrapInt(realThis.length());
      case 26:
        realThis.normalize();
        return Undefined.instance;
      case 27:
        return realThis.parent();
      case 29:
        xmlName = (args.length > 0) ? this.lib.toXMLName(cx, args[0]) : XMLName.formStar();

        
        return realThis.processingInstructions(xmlName);
      
      case 30:
        return ScriptRuntime.wrapBoolean(realThis.propertyIsEnumerable(arg(args, 0)));

      
      case 37:
        return realThis.text();
      case 38:
        return realThis.toString();
      case 39:
        indent = ScriptRuntime.toInt32(args, 0);
        return realThis.toSource(indent);
      case 40:
        return realThis.toXMLString();
      
      case 41:
        return realThis.valueOf();
    } 
    throw new IllegalArgumentException(String.valueOf(id)); }

  
  private static Object arg(Object[] args, int i) {
    return (i < args.length) ? args[i] : Undefined.instance;
  }
  
  final XML newTextElementXML(XmlNode reference, XmlNode.QName qname, String value) {
    return this.lib.newTextElementXML(reference, qname, value);
  }

  
  final XML newXMLFromJs(Object inputObject) {
    return this.lib.newXMLFromJs(inputObject);
  }
  
  final XML ecmaToXml(Object object) {
    return this.lib.ecmaToXml(object);
  }

  
  final String ecmaEscapeAttributeValue(String s) {
    String quoted = this.lib.escapeAttributeValue(s);
    return quoted.substring(1, quoted.length() - 1);
  }
  
  final XML createEmptyXML() {
    return newXML(XmlNode.createEmpty(getProcessor()));
  }
}
