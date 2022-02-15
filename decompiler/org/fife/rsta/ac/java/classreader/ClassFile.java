package org.fife.rsta.ac.java.classreader;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.fife.rsta.ac.java.classreader.attributes.AttributeInfo;
import org.fife.rsta.ac.java.classreader.attributes.Signature;
import org.fife.rsta.ac.java.classreader.attributes.SourceFile;
import org.fife.rsta.ac.java.classreader.attributes.UnsupportedAttribute;
import org.fife.rsta.ac.java.classreader.constantpool.ConstantClassInfo;
import org.fife.rsta.ac.java.classreader.constantpool.ConstantPoolInfo;
import org.fife.rsta.ac.java.classreader.constantpool.ConstantPoolInfoFactory;
import org.fife.rsta.ac.java.classreader.constantpool.ConstantUtf8Info;













































































public class ClassFile
  implements AccessFlags
{
  private static final boolean DEBUG = false;
  private int minorVersion;
  private int majorVersion;
  private ConstantPoolInfo[] constantPool;
  private int accessFlags;
  private int thisClass;
  private int superClass;
  int[] interfaces;
  private FieldInfo[] fields;
  private MethodInfo[] methods;
  private boolean deprecated;
  private AttributeInfo[] attributes;
  private List<String> paramTypes;
  private Map<String, String> typeMap;
  public static final String DEPRECATED = "Deprecated";
  public static final String ENCLOSING_METHOD = "EnclosingMethod";
  public static final String INNER_CLASSES = "InnerClasses";
  public static final String RUNTIME_VISIBLE_ANNOTATIONS = "RuntimeVisibleAnnotations";
  public static final String SIGNATURE = "Signature";
  public static final String SOURCE_FILE = "SourceFile";
  public static final String BOOTSTRAP_METHODS = "BootstrapMethods";
  private static final byte[] HEADER = new byte[] { -54, -2, -70, -66 };


  
  public ClassFile(File classFile) throws IOException {
    try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(classFile)))) {
      
      init(in);
    } 
  }

  
  public ClassFile(DataInputStream in) throws IOException {
    init(in);
  }






  
  private void debugPrint(String text) {}






  
  public int getAccessFlags() {
    return this.accessFlags;
  }








  
  public AttributeInfo getAttribute(int index) {
    return this.attributes[index];
  }







  
  public int getAttributeCount() {
    return (this.attributes == null) ? 0 : this.attributes.length;
  }








  
  public String getClassName(boolean fullyQualified) {
    return getClassNameFromConstantPool(this.thisClass, fullyQualified);
  }













  
  protected String getClassNameFromConstantPool(int cpIndex, boolean fullyQualified) {
    ConstantPoolInfo cpi = getConstantPoolInfo(cpIndex);
    
    if (cpi instanceof ConstantClassInfo) {
      ConstantClassInfo cci = (ConstantClassInfo)cpi;
      int index = cci.getNameIndex();
      ConstantUtf8Info cui = (ConstantUtf8Info)getConstantPoolInfo(index);
      String className = cui.getRepresentedString(false);
      if (fullyQualified) {
        className = className.replace('/', '.');
      } else {
        
        className = className.substring(className.lastIndexOf('/') + 1);
      } 
      return className.replace('$', '.');
    } 

    
    throw new InternalError("Expected ConstantClassInfo, found " + cpi
        .getClass().toString());
  }








  
  public int getConstantPoolCount() {
    return this.constantPool.length + 1;
  }













  
  public ConstantPoolInfo getConstantPoolInfo(int index) {
    return (index != 0) ? this.constantPool[index - 1] : null;
  }







  
  public int getFieldCount() {
    return (this.fields == null) ? 0 : this.fields.length;
  }









  
  public FieldInfo getFieldInfo(int index) {
    return this.fields[index];
  }









  
  public FieldInfo getFieldInfoByName(String name) {
    for (int i = 0; i < getFieldCount(); i++) {
      if (name.equals(this.fields[i].getName())) {
        return this.fields[i];
      }
    } 
    return null;
  }







  
  public int getImplementedInterfaceCount() {
    return (this.interfaces == null) ? 0 : this.interfaces.length;
  }












  
  public String getImplementedInterfaceName(int index, boolean fullyQualified) {
    return getClassNameFromConstantPool(this.interfaces[index], fullyQualified);
  }








  
  public int getMethodCount() {
    return (this.methods == null) ? 0 : this.methods.length;
  }









  
  public MethodInfo getMethodInfo(int index) {
    return this.methods[index];
  }









  
  public List<MethodInfo> getMethodInfoByName(String name) {
    return getMethodInfoByName(name, -1);
  }













  
  public List<MethodInfo> getMethodInfoByName(String name, int argCount) {
    List<MethodInfo> methods = null;
    for (int i = 0; i < getMethodCount(); i++) {
      MethodInfo info = this.methods[i];
      if (name.equals(info.getName()) && (
        argCount < 0 || argCount == info.getParameterCount())) {
        if (methods == null) {
          methods = new ArrayList<>(1);
        }
        methods.add(info);
      } 
    } 
    
    return methods;
  }








  
  public String getPackageName() {
    String className = getClassName(true);
    int dot = className.lastIndexOf('.');
    return (dot == -1) ? null : className.substring(0, dot);
  }

  
  public List<String> getParamTypes() {
    return this.paramTypes;
  }














  
  public String getSuperClassName(boolean fullyQualified) {
    if (this.superClass == 0) {
      return null;
    }
    return getClassNameFromConstantPool(this.superClass, fullyQualified);
  }

















  
  public String getTypeArgument(String typeParam) {
    return (this.typeMap == null) ? "Object" : this.typeMap.get(typeParam);
  }










  
  public String getUtf8ValueFromConstantPool(int index) {
    ConstantPoolInfo cpi = getConstantPoolInfo(index);
    ConstantUtf8Info cui = (ConstantUtf8Info)cpi;
    return cui.getRepresentedString(false);
  }







  
  public String getVersionString() {
    return this.majorVersion + "." + this.minorVersion;
  }







  
  private void init(DataInputStream in) throws IOException {
    readHeader(in);
    readVersion(in);
    readConstantPoolInfos(in);
    readAccessFlags(in);
    readThisClass(in);
    readSuperClass(in);
    readInterfaces(in);
    readFields(in);
    readMethods(in);
    readAttributes(in);
  }






  
  public boolean isDeprecated() {
    return this.deprecated;
  }







  
  private void readAccessFlags(DataInputStream in) throws IOException {
    this.accessFlags = in.readUnsignedShort();
    debugPrint("Access flags: " + this.accessFlags);
  }








  
  private AttributeInfo readAttribute(DataInputStream in) throws IOException {
    UnsupportedAttribute unsupportedAttribute;
    AttributeInfo ai = null;
    
    int attributeNameIndex = in.readUnsignedShort();
    int attributeLength = in.readInt();
    
    String attrName = getUtf8ValueFromConstantPool(attributeNameIndex);
    debugPrint("Found class attribute: " + attrName);
    
    if ("SourceFile".equals(attrName)) {
      int sourceFileIndex = in.readUnsignedShort();
      SourceFile sf = new SourceFile(this, sourceFileIndex);
      SourceFile sourceFile1 = sf;
    
    }
    else if ("BootstrapMethods".equals(attrName)) {

      
      Util.skipBytes(in, attributeLength);

    
    }
    else if ("Signature".equals(attrName)) {
      int signatureIndex = in.readUnsignedShort();
      String sig = getUtf8ValueFromConstantPool(signatureIndex);
      
      Signature signature = new Signature(this, sig);
      this.paramTypes = signature.getClassParamTypes();
    
    }
    else if ("InnerClasses".equals(attrName)) {

      
      Util.skipBytes(in, attributeLength);

    
    }
    else if ("EnclosingMethod".equals(attrName)) {

      
      Util.skipBytes(in, attributeLength);

    
    }
    else if ("Deprecated".equals(attrName)) {
      
      this.deprecated = true;
    
    }
    else if ("RuntimeVisibleAnnotations".equals(attrName)) {

      
      Util.skipBytes(in, attributeLength);

    
    }
    else {

      
      debugPrint("Unsupported class attribute: " + attrName);
      unsupportedAttribute = AttributeInfo.readUnsupportedAttribute(this, in, attrName, attributeLength);
    } 

    
    return (AttributeInfo)unsupportedAttribute;
  }








  
  private void readAttributes(DataInputStream in) throws IOException {
    int attributeCount = in.readUnsignedShort();
    if (attributeCount > 0) {
      this.attributes = new AttributeInfo[attributeCount];
      for (int i = 0; i < attributeCount; i++) {
        this.attributes[i] = readAttribute(in);
      }
    } 
  }











  
  private void readConstantPoolInfos(DataInputStream in) throws IOException {
    int constantPoolCount = in.readUnsignedShort() - 1;
    debugPrint("Constant pool count: " + constantPoolCount);
    
    this.constantPool = new ConstantPoolInfo[constantPoolCount];
    
    for (int i = 0; i < constantPoolCount; i++) {
      ConstantPoolInfo cpi = ConstantPoolInfoFactory.readConstantPoolInfo(this, in);
      this.constantPool[i] = cpi;

      
      if (cpi instanceof org.fife.rsta.ac.java.classreader.constantpool.ConstantLongInfo || cpi instanceof org.fife.rsta.ac.java.classreader.constantpool.ConstantDoubleInfo)
      {
        i++;
      }
    } 
  }








  
  private void readFields(DataInputStream in) throws IOException {
    int fieldCount = in.readUnsignedShort();
    if (fieldCount > 0) {
      this.fields = new FieldInfo[fieldCount];
      for (int i = 0; i < fieldCount; i++) {
        this.fields[i] = FieldInfo.read(this, in);
      }
    } 
    debugPrint("fieldCount: " + fieldCount);
  }







  
  private void readHeader(DataInputStream in) throws IOException {
    for (byte b1 : HEADER) {
      byte b = in.readByte();
      if (b != b1) {
        throw new IOException("\"CAFEBABE\" header not found");
      }
    } 
  }








  
  private void readInterfaces(DataInputStream in) throws IOException {
    int interfaceCount = in.readUnsignedShort();
    if (interfaceCount > 0) {
      this.interfaces = new int[interfaceCount];
      for (int i = 0; i < interfaceCount; i++) {
        this.interfaces[i] = in.readUnsignedShort();
      }
    } 
    debugPrint("interfaceCount: " + interfaceCount);
  }

  
  private void readMethods(DataInputStream in) throws IOException {
    int methodCount = in.readUnsignedShort();
    if (methodCount > 0) {
      this.methods = new MethodInfo[methodCount];
      for (int i = 0; i < methodCount; i++) {
        this.methods[i] = MethodInfo.read(this, in);
      }
    } 
  }

  
  private void readSuperClass(DataInputStream in) throws IOException {
    this.superClass = in.readUnsignedShort();
    ConstantPoolInfo cpi = getConstantPoolInfo(this.superClass);
    debugPrint("superClass: " + cpi);
  }

  
  private void readThisClass(DataInputStream in) throws IOException {
    this.thisClass = in.readUnsignedShort();
    ConstantPoolInfo cpi = getConstantPoolInfo(this.thisClass);
    debugPrint("thisClass: " + cpi);
  }







  
  private void readVersion(DataInputStream in) throws IOException {
    this.minorVersion = in.readUnsignedShort();
    this.majorVersion = in.readUnsignedShort();
    debugPrint("Class file version: " + getVersionString());
  }












  
  public void setTypeParamsToTypeArgs(Map<String, String> typeMap) {
    this.typeMap = typeMap;
    for (int i = 0; i < getMethodCount(); i++) {
      getMethodInfo(i).clearParamTypeInfo();
    }
  }


  
  public String toString() {
    return "[ClassFile: accessFlags=" + this.accessFlags + "]";
  }
}
