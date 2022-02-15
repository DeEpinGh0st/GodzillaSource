package javassist.bytecode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javassist.ClassPool;
import javassist.bytecode.stackmap.MapMaker;



























































public class MethodInfo
{
  ConstPool constPool;
  int accessFlags;
  int name;
  String cachedName;
  int descriptor;
  List<AttributeInfo> attribute;
  public static boolean doPreverify = false;
  public static final String nameInit = "<init>";
  public static final String nameClinit = "<clinit>";
  
  private MethodInfo(ConstPool cp) {
    this.constPool = cp;
    this.attribute = null;
  }












  
  public MethodInfo(ConstPool cp, String methodname, String desc) {
    this(cp);
    this.accessFlags = 0;
    this.name = cp.addUtf8Info(methodname);
    this.cachedName = methodname;
    this.descriptor = this.constPool.addUtf8Info(desc);
  }
  
  MethodInfo(ConstPool cp, DataInputStream in) throws IOException {
    this(cp);
    read(in);
  }





















  
  public MethodInfo(ConstPool cp, String methodname, MethodInfo src, Map<String, String> classnameMap) throws BadBytecode {
    this(cp);
    read(src, methodname, classnameMap);
  }




  
  public String toString() {
    return getName() + " " + getDescriptor();
  }








  
  void compact(ConstPool cp) {
    this.name = cp.addUtf8Info(getName());
    this.descriptor = cp.addUtf8Info(getDescriptor());
    this.attribute = AttributeInfo.copyAll(this.attribute, cp);
    this.constPool = cp;
  }
  
  void prune(ConstPool cp) {
    List<AttributeInfo> newAttributes = new ArrayList<>();

    
    AttributeInfo invisibleAnnotations = getAttribute("RuntimeInvisibleAnnotations");
    if (invisibleAnnotations != null) {
      invisibleAnnotations = invisibleAnnotations.copy(cp, null);
      newAttributes.add(invisibleAnnotations);
    } 

    
    AttributeInfo visibleAnnotations = getAttribute("RuntimeVisibleAnnotations");
    if (visibleAnnotations != null) {
      visibleAnnotations = visibleAnnotations.copy(cp, null);
      newAttributes.add(visibleAnnotations);
    } 

    
    AttributeInfo parameterInvisibleAnnotations = getAttribute("RuntimeInvisibleParameterAnnotations");
    if (parameterInvisibleAnnotations != null) {
      parameterInvisibleAnnotations = parameterInvisibleAnnotations.copy(cp, null);
      newAttributes.add(parameterInvisibleAnnotations);
    } 

    
    AttributeInfo parameterVisibleAnnotations = getAttribute("RuntimeVisibleParameterAnnotations");
    if (parameterVisibleAnnotations != null) {
      parameterVisibleAnnotations = parameterVisibleAnnotations.copy(cp, null);
      newAttributes.add(parameterVisibleAnnotations);
    } 

    
    AnnotationDefaultAttribute defaultAttribute = (AnnotationDefaultAttribute)getAttribute("AnnotationDefault");
    if (defaultAttribute != null) {
      newAttributes.add(defaultAttribute);
    }
    ExceptionsAttribute ea = getExceptionsAttribute();
    if (ea != null) {
      newAttributes.add(ea);
    }
    
    AttributeInfo signature = getAttribute("Signature");
    if (signature != null) {
      signature = signature.copy(cp, null);
      newAttributes.add(signature);
    } 
    
    this.attribute = newAttributes;
    this.name = cp.addUtf8Info(getName());
    this.descriptor = cp.addUtf8Info(getDescriptor());
    this.constPool = cp;
  }



  
  public String getName() {
    if (this.cachedName == null) {
      this.cachedName = this.constPool.getUtf8Info(this.name);
    }
    return this.cachedName;
  }



  
  public void setName(String newName) {
    this.name = this.constPool.addUtf8Info(newName);
    this.cachedName = newName;
  }




  
  public boolean isMethod() {
    String n = getName();
    return (!n.equals("<init>") && !n.equals("<clinit>"));
  }



  
  public ConstPool getConstPool() {
    return this.constPool;
  }



  
  public boolean isConstructor() {
    return getName().equals("<init>");
  }



  
  public boolean isStaticInitializer() {
    return getName().equals("<clinit>");
  }





  
  public int getAccessFlags() {
    return this.accessFlags;
  }





  
  public void setAccessFlags(int acc) {
    this.accessFlags = acc;
  }





  
  public String getDescriptor() {
    return this.constPool.getUtf8Info(this.descriptor);
  }





  
  public void setDescriptor(String desc) {
    if (!desc.equals(getDescriptor())) {
      this.descriptor = this.constPool.addUtf8Info(desc);
    }
  }









  
  public List<AttributeInfo> getAttributes() {
    if (this.attribute == null) {
      this.attribute = new ArrayList<>();
    }
    return this.attribute;
  }













  
  public AttributeInfo getAttribute(String name) {
    return AttributeInfo.lookup(this.attribute, name);
  }







  
  public AttributeInfo removeAttribute(String name) {
    return AttributeInfo.remove(this.attribute, name);
  }






  
  public void addAttribute(AttributeInfo info) {
    if (this.attribute == null) {
      this.attribute = new ArrayList<>();
    }
    AttributeInfo.remove(this.attribute, info.getName());
    this.attribute.add(info);
  }





  
  public ExceptionsAttribute getExceptionsAttribute() {
    AttributeInfo info = AttributeInfo.lookup(this.attribute, "Exceptions");
    
    return (ExceptionsAttribute)info;
  }





  
  public CodeAttribute getCodeAttribute() {
    AttributeInfo info = AttributeInfo.lookup(this.attribute, "Code");
    return (CodeAttribute)info;
  }



  
  public void removeExceptionsAttribute() {
    AttributeInfo.remove(this.attribute, "Exceptions");
  }







  
  public void setExceptionsAttribute(ExceptionsAttribute cattr) {
    removeExceptionsAttribute();
    if (this.attribute == null) {
      this.attribute = new ArrayList<>();
    }
    this.attribute.add(cattr);
  }



  
  public void removeCodeAttribute() {
    AttributeInfo.remove(this.attribute, "Code");
  }







  
  public void setCodeAttribute(CodeAttribute cattr) {
    removeCodeAttribute();
    if (this.attribute == null) {
      this.attribute = new ArrayList<>();
    }
    this.attribute.add(cattr);
  }















  
  public void rebuildStackMapIf6(ClassPool pool, ClassFile cf) throws BadBytecode {
    if (cf.getMajorVersion() >= 50) {
      rebuildStackMap(pool);
    }
    if (doPreverify) {
      rebuildStackMapForME(pool);
    }
  }








  
  public void rebuildStackMap(ClassPool pool) throws BadBytecode {
    CodeAttribute ca = getCodeAttribute();
    if (ca != null) {
      StackMapTable smt = MapMaker.make(pool, this);
      ca.setAttribute(smt);
    } 
  }









  
  public void rebuildStackMapForME(ClassPool pool) throws BadBytecode {
    CodeAttribute ca = getCodeAttribute();
    if (ca != null) {
      StackMap sm = MapMaker.make2(pool, this);
      ca.setAttribute(sm);
    } 
  }









  
  public int getLineNumber(int pos) {
    CodeAttribute ca = getCodeAttribute();
    if (ca == null) {
      return -1;
    }
    
    LineNumberAttribute ainfo = (LineNumberAttribute)ca.getAttribute("LineNumberTable");
    if (ainfo == null) {
      return -1;
    }
    return ainfo.toLineNumber(pos);
  }





















  
  public void setSuperclass(String superclass) throws BadBytecode {
    if (!isConstructor()) {
      return;
    }
    CodeAttribute ca = getCodeAttribute();
    byte[] code = ca.getCode();
    CodeIterator iterator = ca.iterator();
    int pos = iterator.skipSuperConstructor();
    if (pos >= 0) {
      ConstPool cp = this.constPool;
      int mref = ByteArray.readU16bit(code, pos + 1);
      int nt = cp.getMethodrefNameAndType(mref);
      int sc = cp.addClassInfo(superclass);
      int mref2 = cp.addMethodrefInfo(sc, nt);
      ByteArray.write16bit(mref2, code, pos + 1);
    } 
  }
  
  private void read(MethodInfo src, String methodname, Map<String, String> classnames) {
    ConstPool destCp = this.constPool;
    this.accessFlags = src.accessFlags;
    this.name = destCp.addUtf8Info(methodname);
    this.cachedName = methodname;
    ConstPool srcCp = src.constPool;
    String desc = srcCp.getUtf8Info(src.descriptor);
    String desc2 = Descriptor.rename(desc, classnames);
    this.descriptor = destCp.addUtf8Info(desc2);
    
    this.attribute = new ArrayList<>();
    ExceptionsAttribute eattr = src.getExceptionsAttribute();
    if (eattr != null) {
      this.attribute.add(eattr.copy(destCp, classnames));
    }
    CodeAttribute cattr = src.getCodeAttribute();
    if (cattr != null)
      this.attribute.add(cattr.copy(destCp, classnames)); 
  }
  
  private void read(DataInputStream in) throws IOException {
    this.accessFlags = in.readUnsignedShort();
    this.name = in.readUnsignedShort();
    this.descriptor = in.readUnsignedShort();
    int n = in.readUnsignedShort();
    this.attribute = new ArrayList<>();
    for (int i = 0; i < n; i++)
      this.attribute.add(AttributeInfo.read(this.constPool, in)); 
  }
  
  void write(DataOutputStream out) throws IOException {
    out.writeShort(this.accessFlags);
    out.writeShort(this.name);
    out.writeShort(this.descriptor);
    
    if (this.attribute == null) {
      out.writeShort(0);
    } else {
      out.writeShort(this.attribute.size());
      AttributeInfo.writeAll(this.attribute, out);
    } 
  }
}
