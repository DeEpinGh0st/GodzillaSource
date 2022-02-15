package org.springframework.asm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;












































































































































public class ClassReader
{
  public static final int SKIP_CODE = 1;
  public static final int SKIP_DEBUG = 2;
  public static final int SKIP_FRAMES = 4;
  public static final int EXPAND_FRAMES = 8;
  static final int EXPAND_ASM_INSNS = 256;
  private static final int MAX_BUFFER_SIZE = 1048576;
  private static final int INPUT_STREAM_DATA_CHUNK_SIZE = 4096;
  @Deprecated
  public final byte[] b;
  public final int header;
  final byte[] classFileBuffer;
  private final int[] cpInfoOffsets;
  private final String[] constantUtf8Values;
  private final ConstantDynamic[] constantDynamicValues;
  private final int[] bootstrapMethodOffsets;
  private final int maxStringLength;
  
  public ClassReader(byte[] classFile) {
    this(classFile, 0, classFile.length);
  }










  
  public ClassReader(byte[] classFileBuffer, int classFileOffset, int classFileLength) {
    this(classFileBuffer, classFileOffset, true);
  }









  
  ClassReader(byte[] classFileBuffer, int classFileOffset, boolean checkClassVersion) {
    this.classFileBuffer = classFileBuffer;
    this.b = classFileBuffer;

    
    if (checkClassVersion && readShort(classFileOffset + 6) > 62) {
      throw new IllegalArgumentException("Unsupported class file major version " + 
          readShort(classFileOffset + 6));
    }

    
    int constantPoolCount = readUnsignedShort(classFileOffset + 8);
    this.cpInfoOffsets = new int[constantPoolCount];
    this.constantUtf8Values = new String[constantPoolCount];



    
    int currentCpInfoIndex = 1;
    int currentCpInfoOffset = classFileOffset + 10;
    int currentMaxStringLength = 0;
    boolean hasBootstrapMethods = false;
    boolean hasConstantDynamic = false;
    
    while (currentCpInfoIndex < constantPoolCount) {
      int cpInfoSize; this.cpInfoOffsets[currentCpInfoIndex++] = currentCpInfoOffset + 1;
      
      switch (classFileBuffer[currentCpInfoOffset]) {
        case 3:
        case 4:
        case 9:
        case 10:
        case 11:
        case 12:
          cpInfoSize = 5;
          break;
        case 17:
          cpInfoSize = 5;
          hasBootstrapMethods = true;
          hasConstantDynamic = true;
          break;
        case 18:
          cpInfoSize = 5;
          hasBootstrapMethods = true;
          break;
        case 5:
        case 6:
          cpInfoSize = 9;
          currentCpInfoIndex++;
          break;
        case 1:
          cpInfoSize = 3 + readUnsignedShort(currentCpInfoOffset + 1);
          if (cpInfoSize > currentMaxStringLength)
          {

            
            currentMaxStringLength = cpInfoSize;
          }
          break;
        case 15:
          cpInfoSize = 4;
          break;
        case 7:
        case 8:
        case 16:
        case 19:
        case 20:
          cpInfoSize = 3;
          break;
        default:
          throw new IllegalArgumentException();
      } 
      currentCpInfoOffset += cpInfoSize;
    } 
    this.maxStringLength = currentMaxStringLength;
    
    this.header = currentCpInfoOffset;

    
    this.constantDynamicValues = hasConstantDynamic ? new ConstantDynamic[constantPoolCount] : null;

    
    this
      .bootstrapMethodOffsets = hasBootstrapMethods ? readBootstrapMethodsAttribute(currentMaxStringLength) : null;
  }








  
  public ClassReader(InputStream inputStream) throws IOException {
    this(readStream(inputStream, false));
  }







  
  public ClassReader(String className) throws IOException {
    this(
        readStream(
          ClassLoader.getSystemResourceAsStream(className.replace('.', '/') + ".class"), true));
  }









  
  private static byte[] readStream(InputStream inputStream, boolean close) throws IOException {
    if (inputStream == null) {
      throw new IOException("Class not found");
    }
    int bufferSize = calculateBufferSize(inputStream);
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      byte[] data = new byte[bufferSize];
      
      int readCount = 0; int bytesRead;
      while ((bytesRead = inputStream.read(data, 0, bufferSize)) != -1) {
        outputStream.write(data, 0, bytesRead);
        readCount++;
      } 
      outputStream.flush();
      if (readCount == 1);



      
      return outputStream.toByteArray();
    } finally {
      if (close) {
        inputStream.close();
      }
    } 
  }
  
  private static int calculateBufferSize(InputStream inputStream) throws IOException {
    int expectedLength = inputStream.available();





    
    if (expectedLength < 256) {
      return 4096;
    }
    return Math.min(expectedLength, 1048576);
  }











  
  public int getAccess() {
    return readUnsignedShort(this.header);
  }







  
  public String getClassName() {
    return readClass(this.header + 2, new char[this.maxStringLength]);
  }








  
  public String getSuperName() {
    return readClass(this.header + 4, new char[this.maxStringLength]);
  }








  
  public String[] getInterfaces() {
    int currentOffset = this.header + 6;
    int interfacesCount = readUnsignedShort(currentOffset);
    String[] interfaces = new String[interfacesCount];
    if (interfacesCount > 0) {
      char[] charBuffer = new char[this.maxStringLength];
      for (int i = 0; i < interfacesCount; i++) {
        currentOffset += 2;
        interfaces[i] = readClass(currentOffset, charBuffer);
      } 
    } 
    return interfaces;
  }












  
  public void accept(ClassVisitor classVisitor, int parsingOptions) {
    accept(classVisitor, new Attribute[0], parsingOptions);
  }

















  
  public void accept(ClassVisitor classVisitor, Attribute[] attributePrototypes, int parsingOptions) {
    Context context = new Context();
    context.attributePrototypes = attributePrototypes;
    context.parsingOptions = parsingOptions;
    context.charBuffer = new char[this.maxStringLength];

    
    char[] charBuffer = context.charBuffer;
    int currentOffset = this.header;
    int accessFlags = readUnsignedShort(currentOffset);
    String thisClass = readClass(currentOffset + 2, charBuffer);
    String superClass = readClass(currentOffset + 4, charBuffer);
    String[] interfaces = new String[readUnsignedShort(currentOffset + 6)];
    currentOffset += 8;
    for (int i = 0; i < interfaces.length; i++) {
      interfaces[i] = readClass(currentOffset, charBuffer);
      currentOffset += 2;
    } 



    
    int innerClassesOffset = 0;
    
    int enclosingMethodOffset = 0;
    
    String signature = null;
    
    String sourceFile = null;
    
    String sourceDebugExtension = null;
    
    int runtimeVisibleAnnotationsOffset = 0;
    
    int runtimeInvisibleAnnotationsOffset = 0;
    
    int runtimeVisibleTypeAnnotationsOffset = 0;
    
    int runtimeInvisibleTypeAnnotationsOffset = 0;
    
    int moduleOffset = 0;
    
    int modulePackagesOffset = 0;
    
    String moduleMainClass = null;
    
    String nestHostClass = null;
    
    int nestMembersOffset = 0;
    
    int permittedSubclassesOffset = 0;
    
    int recordOffset = 0;

    
    Attribute attributes = null;
    
    int currentAttributeOffset = getFirstAttributeOffset();
    for (int j = readUnsignedShort(currentAttributeOffset - 2); j > 0; j--) {
      
      String attributeName = readUTF8(currentAttributeOffset, charBuffer);
      int attributeLength = readInt(currentAttributeOffset + 2);
      currentAttributeOffset += 6;

      
      if ("SourceFile".equals(attributeName)) {
        sourceFile = readUTF8(currentAttributeOffset, charBuffer);
      } else if ("InnerClasses".equals(attributeName)) {
        innerClassesOffset = currentAttributeOffset;
      } else if ("EnclosingMethod".equals(attributeName)) {
        enclosingMethodOffset = currentAttributeOffset;
      } else if ("NestHost".equals(attributeName)) {
        nestHostClass = readClass(currentAttributeOffset, charBuffer);
      } else if ("NestMembers".equals(attributeName)) {
        nestMembersOffset = currentAttributeOffset;
      } else if ("PermittedSubclasses".equals(attributeName)) {
        permittedSubclassesOffset = currentAttributeOffset;
      } else if ("Signature".equals(attributeName)) {
        signature = readUTF8(currentAttributeOffset, charBuffer);
      } else if ("RuntimeVisibleAnnotations".equals(attributeName)) {
        runtimeVisibleAnnotationsOffset = currentAttributeOffset;
      } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
        runtimeVisibleTypeAnnotationsOffset = currentAttributeOffset;
      } else if ("Deprecated".equals(attributeName)) {
        accessFlags |= 0x20000;
      } else if ("Synthetic".equals(attributeName)) {
        accessFlags |= 0x1000;
      } else if ("SourceDebugExtension".equals(attributeName)) {
        if (attributeLength > this.classFileBuffer.length - currentAttributeOffset) {
          throw new IllegalArgumentException();
        }
        
        sourceDebugExtension = readUtf(currentAttributeOffset, attributeLength, new char[attributeLength]);
      } else if ("RuntimeInvisibleAnnotations".equals(attributeName)) {
        runtimeInvisibleAnnotationsOffset = currentAttributeOffset;
      } else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
        runtimeInvisibleTypeAnnotationsOffset = currentAttributeOffset;
      } else if ("Record".equals(attributeName)) {
        recordOffset = currentAttributeOffset;
        accessFlags |= 0x10000;
      } else if ("Module".equals(attributeName)) {
        moduleOffset = currentAttributeOffset;
      } else if ("ModuleMainClass".equals(attributeName)) {
        moduleMainClass = readClass(currentAttributeOffset, charBuffer);
      } else if ("ModulePackages".equals(attributeName)) {
        modulePackagesOffset = currentAttributeOffset;
      } else if (!"BootstrapMethods".equals(attributeName)) {

        
        Attribute attribute = readAttribute(attributePrototypes, attributeName, currentAttributeOffset, attributeLength, charBuffer, -1, null);






        
        attribute.nextAttribute = attributes;
        attributes = attribute;
      } 
      currentAttributeOffset += attributeLength;
    } 


    
    classVisitor.visit(
        readInt(this.cpInfoOffsets[1] - 7), accessFlags, thisClass, signature, superClass, interfaces);

    
    if ((parsingOptions & 0x2) == 0 && (sourceFile != null || sourceDebugExtension != null))
    {
      classVisitor.visitSource(sourceFile, sourceDebugExtension);
    }

    
    if (moduleOffset != 0) {
      readModuleAttributes(classVisitor, context, moduleOffset, modulePackagesOffset, moduleMainClass);
    }


    
    if (nestHostClass != null) {
      classVisitor.visitNestHost(nestHostClass);
    }

    
    if (enclosingMethodOffset != 0) {
      String className = readClass(enclosingMethodOffset, charBuffer);
      int methodIndex = readUnsignedShort(enclosingMethodOffset + 2);
      String name = (methodIndex == 0) ? null : readUTF8(this.cpInfoOffsets[methodIndex], charBuffer);
      String type = (methodIndex == 0) ? null : readUTF8(this.cpInfoOffsets[methodIndex] + 2, charBuffer);
      classVisitor.visitOuterClass(className, name, type);
    } 

    
    if (runtimeVisibleAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
      int currentAnnotationOffset = runtimeVisibleAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;

        
        currentAnnotationOffset = readElementValues(classVisitor
            .visitAnnotation(annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
      } 
    } 




    
    if (runtimeInvisibleAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
      int currentAnnotationOffset = runtimeInvisibleAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;

        
        currentAnnotationOffset = readElementValues(classVisitor
            .visitAnnotation(annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
      } 
    } 




    
    if (runtimeVisibleTypeAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
      int currentAnnotationOffset = runtimeVisibleTypeAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        
        currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
        
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;

        
        currentAnnotationOffset = readElementValues(classVisitor
            .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
      } 
    } 








    
    if (runtimeInvisibleTypeAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
      int currentAnnotationOffset = runtimeInvisibleTypeAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        
        currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
        
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;

        
        currentAnnotationOffset = readElementValues(classVisitor
            .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
      } 
    } 








    
    while (attributes != null) {
      
      Attribute nextAttribute = attributes.nextAttribute;
      attributes.nextAttribute = null;
      classVisitor.visitAttribute(attributes);
      attributes = nextAttribute;
    } 

    
    if (nestMembersOffset != 0) {
      int numberOfNestMembers = readUnsignedShort(nestMembersOffset);
      int currentNestMemberOffset = nestMembersOffset + 2;
      while (numberOfNestMembers-- > 0) {
        classVisitor.visitNestMember(readClass(currentNestMemberOffset, charBuffer));
        currentNestMemberOffset += 2;
      } 
    } 

    
    if (permittedSubclassesOffset != 0) {
      int numberOfPermittedSubclasses = readUnsignedShort(permittedSubclassesOffset);
      int currentPermittedSubclassesOffset = permittedSubclassesOffset + 2;
      while (numberOfPermittedSubclasses-- > 0) {
        classVisitor.visitPermittedSubclass(
            readClass(currentPermittedSubclassesOffset, charBuffer));
        currentPermittedSubclassesOffset += 2;
      } 
    } 

    
    if (innerClassesOffset != 0) {
      int numberOfClasses = readUnsignedShort(innerClassesOffset);
      int currentClassesOffset = innerClassesOffset + 2;
      while (numberOfClasses-- > 0) {
        classVisitor.visitInnerClass(
            readClass(currentClassesOffset, charBuffer), 
            readClass(currentClassesOffset + 2, charBuffer), 
            readUTF8(currentClassesOffset + 4, charBuffer), 
            readUnsignedShort(currentClassesOffset + 6));
        currentClassesOffset += 8;
      } 
    } 

    
    if (recordOffset != 0) {
      int recordComponentsCount = readUnsignedShort(recordOffset);
      recordOffset += 2;
      while (recordComponentsCount-- > 0) {
        recordOffset = readRecordComponent(classVisitor, context, recordOffset);
      }
    } 

    
    int fieldsCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (fieldsCount-- > 0) {
      currentOffset = readField(classVisitor, context, currentOffset);
    }
    int methodsCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (methodsCount-- > 0) {
      currentOffset = readMethod(classVisitor, context, currentOffset);
    }

    
    classVisitor.visitEnd();
  }





















  
  private void readModuleAttributes(ClassVisitor classVisitor, Context context, int moduleOffset, int modulePackagesOffset, String moduleMainClass) {
    char[] buffer = context.charBuffer;

    
    int currentOffset = moduleOffset;
    String moduleName = readModule(currentOffset, buffer);
    int moduleFlags = readUnsignedShort(currentOffset + 2);
    String moduleVersion = readUTF8(currentOffset + 4, buffer);
    currentOffset += 6;
    ModuleVisitor moduleVisitor = classVisitor.visitModule(moduleName, moduleFlags, moduleVersion);
    if (moduleVisitor == null) {
      return;
    }

    
    if (moduleMainClass != null) {
      moduleVisitor.visitMainClass(moduleMainClass);
    }

    
    if (modulePackagesOffset != 0) {
      int packageCount = readUnsignedShort(modulePackagesOffset);
      int currentPackageOffset = modulePackagesOffset + 2;
      while (packageCount-- > 0) {
        moduleVisitor.visitPackage(readPackage(currentPackageOffset, buffer));
        currentPackageOffset += 2;
      } 
    } 

    
    int requiresCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (requiresCount-- > 0) {
      
      String requires = readModule(currentOffset, buffer);
      int requiresFlags = readUnsignedShort(currentOffset + 2);
      String requiresVersion = readUTF8(currentOffset + 4, buffer);
      currentOffset += 6;
      moduleVisitor.visitRequire(requires, requiresFlags, requiresVersion);
    } 

    
    int exportsCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (exportsCount-- > 0) {

      
      String exports = readPackage(currentOffset, buffer);
      int exportsFlags = readUnsignedShort(currentOffset + 2);
      int exportsToCount = readUnsignedShort(currentOffset + 4);
      currentOffset += 6;
      String[] exportsTo = null;
      if (exportsToCount != 0) {
        exportsTo = new String[exportsToCount];
        for (int i = 0; i < exportsToCount; i++) {
          exportsTo[i] = readModule(currentOffset, buffer);
          currentOffset += 2;
        } 
      } 
      moduleVisitor.visitExport(exports, exportsFlags, exportsTo);
    } 

    
    int opensCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (opensCount-- > 0) {
      
      String opens = readPackage(currentOffset, buffer);
      int opensFlags = readUnsignedShort(currentOffset + 2);
      int opensToCount = readUnsignedShort(currentOffset + 4);
      currentOffset += 6;
      String[] opensTo = null;
      if (opensToCount != 0) {
        opensTo = new String[opensToCount];
        for (int i = 0; i < opensToCount; i++) {
          opensTo[i] = readModule(currentOffset, buffer);
          currentOffset += 2;
        } 
      } 
      moduleVisitor.visitOpen(opens, opensFlags, opensTo);
    } 

    
    int usesCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (usesCount-- > 0) {
      moduleVisitor.visitUse(readClass(currentOffset, buffer));
      currentOffset += 2;
    } 

    
    int providesCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (providesCount-- > 0) {
      
      String provides = readClass(currentOffset, buffer);
      int providesWithCount = readUnsignedShort(currentOffset + 2);
      currentOffset += 4;
      String[] providesWith = new String[providesWithCount];
      for (int i = 0; i < providesWithCount; i++) {
        providesWith[i] = readClass(currentOffset, buffer);
        currentOffset += 2;
      } 
      moduleVisitor.visitProvide(provides, providesWith);
    } 

    
    moduleVisitor.visitEnd();
  }









  
  private int readRecordComponent(ClassVisitor classVisitor, Context context, int recordComponentOffset) {
    char[] charBuffer = context.charBuffer;
    
    int currentOffset = recordComponentOffset;
    String name = readUTF8(currentOffset, charBuffer);
    String descriptor = readUTF8(currentOffset + 2, charBuffer);
    currentOffset += 4;





    
    String signature = null;
    
    int runtimeVisibleAnnotationsOffset = 0;
    
    int runtimeInvisibleAnnotationsOffset = 0;
    
    int runtimeVisibleTypeAnnotationsOffset = 0;
    
    int runtimeInvisibleTypeAnnotationsOffset = 0;

    
    Attribute attributes = null;
    
    int attributesCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (attributesCount-- > 0) {
      
      String attributeName = readUTF8(currentOffset, charBuffer);
      int attributeLength = readInt(currentOffset + 2);
      currentOffset += 6;

      
      if ("Signature".equals(attributeName)) {
        signature = readUTF8(currentOffset, charBuffer);
      } else if ("RuntimeVisibleAnnotations".equals(attributeName)) {
        runtimeVisibleAnnotationsOffset = currentOffset;
      } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
        runtimeVisibleTypeAnnotationsOffset = currentOffset;
      } else if ("RuntimeInvisibleAnnotations".equals(attributeName)) {
        runtimeInvisibleAnnotationsOffset = currentOffset;
      } else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
        runtimeInvisibleTypeAnnotationsOffset = currentOffset;
      } else {
        
        Attribute attribute = readAttribute(context.attributePrototypes, attributeName, currentOffset, attributeLength, charBuffer, -1, null);






        
        attribute.nextAttribute = attributes;
        attributes = attribute;
      } 
      currentOffset += attributeLength;
    } 

    
    RecordComponentVisitor recordComponentVisitor = classVisitor.visitRecordComponent(name, descriptor, signature);
    if (recordComponentVisitor == null) {
      return currentOffset;
    }

    
    if (runtimeVisibleAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
      int currentAnnotationOffset = runtimeVisibleAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;

        
        currentAnnotationOffset = readElementValues(recordComponentVisitor
            .visitAnnotation(annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
      } 
    } 




    
    if (runtimeInvisibleAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
      int currentAnnotationOffset = runtimeInvisibleAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;

        
        currentAnnotationOffset = readElementValues(recordComponentVisitor
            .visitAnnotation(annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
      } 
    } 




    
    if (runtimeVisibleTypeAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
      int currentAnnotationOffset = runtimeVisibleTypeAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        
        currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
        
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;

        
        currentAnnotationOffset = readElementValues(recordComponentVisitor
            .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
      } 
    } 








    
    if (runtimeInvisibleTypeAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
      int currentAnnotationOffset = runtimeInvisibleTypeAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        
        currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
        
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;

        
        currentAnnotationOffset = readElementValues(recordComponentVisitor
            .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
      } 
    } 








    
    while (attributes != null) {
      
      Attribute nextAttribute = attributes.nextAttribute;
      attributes.nextAttribute = null;
      recordComponentVisitor.visitAttribute(attributes);
      attributes = nextAttribute;
    } 

    
    recordComponentVisitor.visitEnd();
    return currentOffset;
  }









  
  private int readField(ClassVisitor classVisitor, Context context, int fieldInfoOffset) {
    char[] charBuffer = context.charBuffer;

    
    int currentOffset = fieldInfoOffset;
    int accessFlags = readUnsignedShort(currentOffset);
    String name = readUTF8(currentOffset + 2, charBuffer);
    String descriptor = readUTF8(currentOffset + 4, charBuffer);
    currentOffset += 6;



    
    Object constantValue = null;
    
    String signature = null;
    
    int runtimeVisibleAnnotationsOffset = 0;
    
    int runtimeInvisibleAnnotationsOffset = 0;
    
    int runtimeVisibleTypeAnnotationsOffset = 0;
    
    int runtimeInvisibleTypeAnnotationsOffset = 0;

    
    Attribute attributes = null;
    
    int attributesCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (attributesCount-- > 0) {
      
      String attributeName = readUTF8(currentOffset, charBuffer);
      int attributeLength = readInt(currentOffset + 2);
      currentOffset += 6;

      
      if ("ConstantValue".equals(attributeName)) {
        int constantvalueIndex = readUnsignedShort(currentOffset);
        constantValue = (constantvalueIndex == 0) ? null : readConst(constantvalueIndex, charBuffer);
      } else if ("Signature".equals(attributeName)) {
        signature = readUTF8(currentOffset, charBuffer);
      } else if ("Deprecated".equals(attributeName)) {
        accessFlags |= 0x20000;
      } else if ("Synthetic".equals(attributeName)) {
        accessFlags |= 0x1000;
      } else if ("RuntimeVisibleAnnotations".equals(attributeName)) {
        runtimeVisibleAnnotationsOffset = currentOffset;
      } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
        runtimeVisibleTypeAnnotationsOffset = currentOffset;
      } else if ("RuntimeInvisibleAnnotations".equals(attributeName)) {
        runtimeInvisibleAnnotationsOffset = currentOffset;
      } else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
        runtimeInvisibleTypeAnnotationsOffset = currentOffset;
      } else {
        
        Attribute attribute = readAttribute(context.attributePrototypes, attributeName, currentOffset, attributeLength, charBuffer, -1, null);






        
        attribute.nextAttribute = attributes;
        attributes = attribute;
      } 
      currentOffset += attributeLength;
    } 


    
    FieldVisitor fieldVisitor = classVisitor.visitField(accessFlags, name, descriptor, signature, constantValue);
    if (fieldVisitor == null) {
      return currentOffset;
    }

    
    if (runtimeVisibleAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
      int currentAnnotationOffset = runtimeVisibleAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;

        
        currentAnnotationOffset = readElementValues(fieldVisitor
            .visitAnnotation(annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
      } 
    } 




    
    if (runtimeInvisibleAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
      int currentAnnotationOffset = runtimeInvisibleAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;

        
        currentAnnotationOffset = readElementValues(fieldVisitor
            .visitAnnotation(annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
      } 
    } 




    
    if (runtimeVisibleTypeAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
      int currentAnnotationOffset = runtimeVisibleTypeAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        
        currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
        
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;

        
        currentAnnotationOffset = readElementValues(fieldVisitor
            .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
      } 
    } 








    
    if (runtimeInvisibleTypeAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
      int currentAnnotationOffset = runtimeInvisibleTypeAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        
        currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
        
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;

        
        currentAnnotationOffset = readElementValues(fieldVisitor
            .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
      } 
    } 








    
    while (attributes != null) {
      
      Attribute nextAttribute = attributes.nextAttribute;
      attributes.nextAttribute = null;
      fieldVisitor.visitAttribute(attributes);
      attributes = nextAttribute;
    } 

    
    fieldVisitor.visitEnd();
    return currentOffset;
  }









  
  private int readMethod(ClassVisitor classVisitor, Context context, int methodInfoOffset) {
    char[] charBuffer = context.charBuffer;

    
    int currentOffset = methodInfoOffset;
    context.currentMethodAccessFlags = readUnsignedShort(currentOffset);
    context.currentMethodName = readUTF8(currentOffset + 2, charBuffer);
    context.currentMethodDescriptor = readUTF8(currentOffset + 4, charBuffer);
    currentOffset += 6;



    
    int codeOffset = 0;
    
    int exceptionsOffset = 0;
    
    String[] exceptions = null;
    
    boolean synthetic = false;
    
    int signatureIndex = 0;
    
    int runtimeVisibleAnnotationsOffset = 0;
    
    int runtimeInvisibleAnnotationsOffset = 0;
    
    int runtimeVisibleParameterAnnotationsOffset = 0;
    
    int runtimeInvisibleParameterAnnotationsOffset = 0;
    
    int runtimeVisibleTypeAnnotationsOffset = 0;
    
    int runtimeInvisibleTypeAnnotationsOffset = 0;
    
    int annotationDefaultOffset = 0;
    
    int methodParametersOffset = 0;

    
    Attribute attributes = null;
    
    int attributesCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (attributesCount-- > 0) {
      
      String attributeName = readUTF8(currentOffset, charBuffer);
      int attributeLength = readInt(currentOffset + 2);
      currentOffset += 6;

      
      if ("Code".equals(attributeName)) {
        if ((context.parsingOptions & 0x1) == 0) {
          codeOffset = currentOffset;
        }
      } else if ("Exceptions".equals(attributeName)) {
        exceptionsOffset = currentOffset;
        exceptions = new String[readUnsignedShort(exceptionsOffset)];
        int currentExceptionOffset = exceptionsOffset + 2;
        for (int i = 0; i < exceptions.length; i++) {
          exceptions[i] = readClass(currentExceptionOffset, charBuffer);
          currentExceptionOffset += 2;
        } 
      } else if ("Signature".equals(attributeName)) {
        signatureIndex = readUnsignedShort(currentOffset);
      } else if ("Deprecated".equals(attributeName)) {
        context.currentMethodAccessFlags |= 0x20000;
      } else if ("RuntimeVisibleAnnotations".equals(attributeName)) {
        runtimeVisibleAnnotationsOffset = currentOffset;
      } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
        runtimeVisibleTypeAnnotationsOffset = currentOffset;
      } else if ("AnnotationDefault".equals(attributeName)) {
        annotationDefaultOffset = currentOffset;
      } else if ("Synthetic".equals(attributeName)) {
        synthetic = true;
        context.currentMethodAccessFlags |= 0x1000;
      } else if ("RuntimeInvisibleAnnotations".equals(attributeName)) {
        runtimeInvisibleAnnotationsOffset = currentOffset;
      } else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
        runtimeInvisibleTypeAnnotationsOffset = currentOffset;
      } else if ("RuntimeVisibleParameterAnnotations".equals(attributeName)) {
        runtimeVisibleParameterAnnotationsOffset = currentOffset;
      } else if ("RuntimeInvisibleParameterAnnotations".equals(attributeName)) {
        runtimeInvisibleParameterAnnotationsOffset = currentOffset;
      } else if ("MethodParameters".equals(attributeName)) {
        methodParametersOffset = currentOffset;
      } else {
        
        Attribute attribute = readAttribute(context.attributePrototypes, attributeName, currentOffset, attributeLength, charBuffer, -1, null);






        
        attribute.nextAttribute = attributes;
        attributes = attribute;
      } 
      currentOffset += attributeLength;
    } 


    
    MethodVisitor methodVisitor = classVisitor.visitMethod(context.currentMethodAccessFlags, context.currentMethodName, context.currentMethodDescriptor, (signatureIndex == 0) ? null : 


        
        readUtf(signatureIndex, charBuffer), exceptions);
    
    if (methodVisitor == null) {
      return currentOffset;
    }




    
    if (methodVisitor instanceof MethodWriter) {
      MethodWriter methodWriter = (MethodWriter)methodVisitor;
      if (methodWriter.canCopyMethodAttributes(this, synthetic, ((context.currentMethodAccessFlags & 0x20000) != 0), 


          
          readUnsignedShort(methodInfoOffset + 4), signatureIndex, exceptionsOffset)) {

        
        methodWriter.setMethodAttributesSource(methodInfoOffset, currentOffset - methodInfoOffset);
        return currentOffset;
      } 
    } 

    
    if (methodParametersOffset != 0 && (context.parsingOptions & 0x2) == 0) {
      int parametersCount = readByte(methodParametersOffset);
      int currentParameterOffset = methodParametersOffset + 1;
      while (parametersCount-- > 0) {
        
        methodVisitor.visitParameter(
            readUTF8(currentParameterOffset, charBuffer), 
            readUnsignedShort(currentParameterOffset + 2));
        currentParameterOffset += 4;
      } 
    } 

    
    if (annotationDefaultOffset != 0) {
      AnnotationVisitor annotationVisitor = methodVisitor.visitAnnotationDefault();
      readElementValue(annotationVisitor, annotationDefaultOffset, null, charBuffer);
      if (annotationVisitor != null) {
        annotationVisitor.visitEnd();
      }
    } 

    
    if (runtimeVisibleAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
      int currentAnnotationOffset = runtimeVisibleAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;

        
        currentAnnotationOffset = readElementValues(methodVisitor
            .visitAnnotation(annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
      } 
    } 




    
    if (runtimeInvisibleAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
      int currentAnnotationOffset = runtimeInvisibleAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;

        
        currentAnnotationOffset = readElementValues(methodVisitor
            .visitAnnotation(annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
      } 
    } 




    
    if (runtimeVisibleTypeAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
      int currentAnnotationOffset = runtimeVisibleTypeAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        
        currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
        
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;

        
        currentAnnotationOffset = readElementValues(methodVisitor
            .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
      } 
    } 








    
    if (runtimeInvisibleTypeAnnotationsOffset != 0) {
      int numAnnotations = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
      int currentAnnotationOffset = runtimeInvisibleTypeAnnotationsOffset + 2;
      while (numAnnotations-- > 0) {
        
        currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
        
        String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
        currentAnnotationOffset += 2;

        
        currentAnnotationOffset = readElementValues(methodVisitor
            .visitTypeAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
      } 
    } 








    
    if (runtimeVisibleParameterAnnotationsOffset != 0) {
      readParameterAnnotations(methodVisitor, context, runtimeVisibleParameterAnnotationsOffset, true);
    }


    
    if (runtimeInvisibleParameterAnnotationsOffset != 0) {
      readParameterAnnotations(methodVisitor, context, runtimeInvisibleParameterAnnotationsOffset, false);
    }





    
    while (attributes != null) {
      
      Attribute nextAttribute = attributes.nextAttribute;
      attributes.nextAttribute = null;
      methodVisitor.visitAttribute(attributes);
      attributes = nextAttribute;
    } 

    
    if (codeOffset != 0) {
      methodVisitor.visitCode();
      readCode(methodVisitor, context, codeOffset);
    } 

    
    methodVisitor.visitEnd();
    return currentOffset;
  }













  
  private void readCode(MethodVisitor methodVisitor, Context context, int codeOffset) {
    int currentOffset = codeOffset;

    
    byte[] classBuffer = this.classFileBuffer;
    char[] charBuffer = context.charBuffer;
    int maxStack = readUnsignedShort(currentOffset);
    int maxLocals = readUnsignedShort(currentOffset + 2);
    int codeLength = readInt(currentOffset + 4);
    currentOffset += 8;
    if (codeLength > this.classFileBuffer.length - currentOffset) {
      throw new IllegalArgumentException();
    }

    
    int bytecodeStartOffset = currentOffset;
    int bytecodeEndOffset = currentOffset + codeLength;
    Label[] labels = context.currentMethodLabels = new Label[codeLength + 1];
    while (currentOffset < bytecodeEndOffset) {
      int numTableEntries, numSwitchCases, bytecodeOffset = currentOffset - bytecodeStartOffset;
      int opcode = classBuffer[currentOffset] & 0xFF;
      switch (opcode) {
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
        case 9:
        case 10:
        case 11:
        case 12:
        case 13:
        case 14:
        case 15:
        case 26:
        case 27:
        case 28:
        case 29:
        case 30:
        case 31:
        case 32:
        case 33:
        case 34:
        case 35:
        case 36:
        case 37:
        case 38:
        case 39:
        case 40:
        case 41:
        case 42:
        case 43:
        case 44:
        case 45:
        case 46:
        case 47:
        case 48:
        case 49:
        case 50:
        case 51:
        case 52:
        case 53:
        case 59:
        case 60:
        case 61:
        case 62:
        case 63:
        case 64:
        case 65:
        case 66:
        case 67:
        case 68:
        case 69:
        case 70:
        case 71:
        case 72:
        case 73:
        case 74:
        case 75:
        case 76:
        case 77:
        case 78:
        case 79:
        case 80:
        case 81:
        case 82:
        case 83:
        case 84:
        case 85:
        case 86:
        case 87:
        case 88:
        case 89:
        case 90:
        case 91:
        case 92:
        case 93:
        case 94:
        case 95:
        case 96:
        case 97:
        case 98:
        case 99:
        case 100:
        case 101:
        case 102:
        case 103:
        case 104:
        case 105:
        case 106:
        case 107:
        case 108:
        case 109:
        case 110:
        case 111:
        case 112:
        case 113:
        case 114:
        case 115:
        case 116:
        case 117:
        case 118:
        case 119:
        case 120:
        case 121:
        case 122:
        case 123:
        case 124:
        case 125:
        case 126:
        case 127:
        case 128:
        case 129:
        case 130:
        case 131:
        case 133:
        case 134:
        case 135:
        case 136:
        case 137:
        case 138:
        case 139:
        case 140:
        case 141:
        case 142:
        case 143:
        case 144:
        case 145:
        case 146:
        case 147:
        case 148:
        case 149:
        case 150:
        case 151:
        case 152:
        case 172:
        case 173:
        case 174:
        case 175:
        case 176:
        case 177:
        case 190:
        case 191:
        case 194:
        case 195:
          currentOffset++;
          continue;
        case 153:
        case 154:
        case 155:
        case 156:
        case 157:
        case 158:
        case 159:
        case 160:
        case 161:
        case 162:
        case 163:
        case 164:
        case 165:
        case 166:
        case 167:
        case 168:
        case 198:
        case 199:
          createLabel(bytecodeOffset + readShort(currentOffset + 1), labels);
          currentOffset += 3;
          continue;
        case 202:
        case 203:
        case 204:
        case 205:
        case 206:
        case 207:
        case 208:
        case 209:
        case 210:
        case 211:
        case 212:
        case 213:
        case 214:
        case 215:
        case 216:
        case 217:
        case 218:
        case 219:
          createLabel(bytecodeOffset + readUnsignedShort(currentOffset + 1), labels);
          currentOffset += 3;
          continue;
        case 200:
        case 201:
        case 220:
          createLabel(bytecodeOffset + readInt(currentOffset + 1), labels);
          currentOffset += 5;
          continue;
        case 196:
          switch (classBuffer[currentOffset + 1] & 0xFF) {
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 169:
              currentOffset += 4;
              continue;
            case 132:
              currentOffset += 6;
              continue;
          } 
          throw new IllegalArgumentException();


        
        case 170:
          currentOffset += 4 - (bytecodeOffset & 0x3);
          
          createLabel(bytecodeOffset + readInt(currentOffset), labels);
          numTableEntries = readInt(currentOffset + 8) - readInt(currentOffset + 4) + 1;
          currentOffset += 12;
          
          while (numTableEntries-- > 0) {
            createLabel(bytecodeOffset + readInt(currentOffset), labels);
            currentOffset += 4;
          } 
          continue;
        
        case 171:
          currentOffset += 4 - (bytecodeOffset & 0x3);
          
          createLabel(bytecodeOffset + readInt(currentOffset), labels);
          numSwitchCases = readInt(currentOffset + 4);
          currentOffset += 8;
          
          while (numSwitchCases-- > 0) {
            createLabel(bytecodeOffset + readInt(currentOffset + 4), labels);
            currentOffset += 8;
          } 
          continue;
        case 16:
        case 18:
        case 21:
        case 22:
        case 23:
        case 24:
        case 25:
        case 54:
        case 55:
        case 56:
        case 57:
        case 58:
        case 169:
        case 188:
          currentOffset += 2;
          continue;
        case 17:
        case 19:
        case 20:
        case 132:
        case 178:
        case 179:
        case 180:
        case 181:
        case 182:
        case 183:
        case 184:
        case 187:
        case 189:
        case 192:
        case 193:
          currentOffset += 3;
          continue;
        case 185:
        case 186:
          currentOffset += 5;
          continue;
        case 197:
          currentOffset += 4;
          continue;
      } 
      throw new IllegalArgumentException();
    } 



    
    int exceptionTableLength = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (exceptionTableLength-- > 0) {
      Label start = createLabel(readUnsignedShort(currentOffset), labels);
      Label end = createLabel(readUnsignedShort(currentOffset + 2), labels);
      Label handler = createLabel(readUnsignedShort(currentOffset + 4), labels);
      String catchType = readUTF8(this.cpInfoOffsets[readUnsignedShort(currentOffset + 6)], charBuffer);
      currentOffset += 8;
      methodVisitor.visitTryCatchBlock(start, end, handler, catchType);
    } 






    
    int stackMapFrameOffset = 0;
    
    int stackMapTableEndOffset = 0;
    
    boolean compressedFrames = true;
    
    int localVariableTableOffset = 0;
    
    int localVariableTypeTableOffset = 0;

    
    int[] visibleTypeAnnotationOffsets = null;

    
    int[] invisibleTypeAnnotationOffsets = null;

    
    Attribute attributes = null;
    
    int attributesCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (attributesCount-- > 0) {
      
      String attributeName = readUTF8(currentOffset, charBuffer);
      int attributeLength = readInt(currentOffset + 2);
      currentOffset += 6;
      if ("LocalVariableTable".equals(attributeName)) {
        if ((context.parsingOptions & 0x2) == 0) {
          localVariableTableOffset = currentOffset;
          
          int currentLocalVariableTableOffset = currentOffset;
          int localVariableTableLength = readUnsignedShort(currentLocalVariableTableOffset);
          currentLocalVariableTableOffset += 2;
          while (localVariableTableLength-- > 0) {
            int startPc = readUnsignedShort(currentLocalVariableTableOffset);
            createDebugLabel(startPc, labels);
            int length = readUnsignedShort(currentLocalVariableTableOffset + 2);
            createDebugLabel(startPc + length, labels);
            
            currentLocalVariableTableOffset += 10;
          } 
        } 
      } else if ("LocalVariableTypeTable".equals(attributeName)) {
        localVariableTypeTableOffset = currentOffset;
      
      }
      else if ("LineNumberTable".equals(attributeName)) {
        if ((context.parsingOptions & 0x2) == 0) {
          
          int currentLineNumberTableOffset = currentOffset;
          int lineNumberTableLength = readUnsignedShort(currentLineNumberTableOffset);
          currentLineNumberTableOffset += 2;
          while (lineNumberTableLength-- > 0) {
            int startPc = readUnsignedShort(currentLineNumberTableOffset);
            int lineNumber = readUnsignedShort(currentLineNumberTableOffset + 2);
            currentLineNumberTableOffset += 4;
            createDebugLabel(startPc, labels);
            labels[startPc].addLineNumber(lineNumber);
          } 
        } 
      } else if ("RuntimeVisibleTypeAnnotations".equals(attributeName)) {
        
        visibleTypeAnnotationOffsets = readTypeAnnotations(methodVisitor, context, currentOffset, true);




      
      }
      else if ("RuntimeInvisibleTypeAnnotations".equals(attributeName)) {
        
        invisibleTypeAnnotationOffsets = readTypeAnnotations(methodVisitor, context, currentOffset, false);
      }
      else if ("StackMapTable".equals(attributeName)) {
        if ((context.parsingOptions & 0x4) == 0) {
          stackMapFrameOffset = currentOffset + 2;
          stackMapTableEndOffset = currentOffset + attributeLength;



        
        }



      
      }
      else if ("StackMap".equals(attributeName)) {
        if ((context.parsingOptions & 0x4) == 0) {
          stackMapFrameOffset = currentOffset + 2;
          stackMapTableEndOffset = currentOffset + attributeLength;
          compressedFrames = false;
        
        }

      
      }
      else {
        
        Attribute attribute = readAttribute(context.attributePrototypes, attributeName, currentOffset, attributeLength, charBuffer, codeOffset, labels);






        
        attribute.nextAttribute = attributes;
        attributes = attribute;
      } 
      currentOffset += attributeLength;
    } 


    
    boolean expandFrames = ((context.parsingOptions & 0x8) != 0);
    if (stackMapFrameOffset != 0) {


      
      context.currentFrameOffset = -1;
      context.currentFrameType = 0;
      context.currentFrameLocalCount = 0;
      context.currentFrameLocalCountDelta = 0;
      context.currentFrameLocalTypes = new Object[maxLocals];
      context.currentFrameStackCount = 0;
      context.currentFrameStackTypes = new Object[maxStack];
      if (expandFrames) {
        computeImplicitFrame(context);
      }






      
      for (int offset = stackMapFrameOffset; offset < stackMapTableEndOffset - 2; offset++) {
        if (classBuffer[offset] == 8) {
          int potentialBytecodeOffset = readUnsignedShort(offset + 1);
          if (potentialBytecodeOffset >= 0 && potentialBytecodeOffset < codeLength && (classBuffer[bytecodeStartOffset + potentialBytecodeOffset] & 0xFF) == 187)
          {

            
            createLabel(potentialBytecodeOffset, labels);
          }
        } 
      } 
    } 
    if (expandFrames && (context.parsingOptions & 0x100) != 0)
    {




      
      methodVisitor.visitFrame(-1, maxLocals, null, 0, null);
    }





    
    int currentVisibleTypeAnnotationIndex = 0;

    
    int currentVisibleTypeAnnotationBytecodeOffset = getTypeAnnotationBytecodeOffset(visibleTypeAnnotationOffsets, 0);

    
    int currentInvisibleTypeAnnotationIndex = 0;

    
    int currentInvisibleTypeAnnotationBytecodeOffset = getTypeAnnotationBytecodeOffset(invisibleTypeAnnotationOffsets, 0);

    
    boolean insertFrame = false;



    
    int wideJumpOpcodeDelta = ((context.parsingOptions & 0x100) == 0) ? 33 : 0;

    
    currentOffset = bytecodeStartOffset;
    while (currentOffset < bytecodeEndOffset) {
      Label target, defaultLabel; int cpInfoOffset, low, numPairs, nameAndTypeCpInfoOffset, high, keys[]; String owner, name; Label[] table, values; String str1, descriptor; int i; String str2; int bootstrapMethodOffset; Handle handle; Object[] bootstrapMethodArguments; int j, currentBytecodeOffset = currentOffset - bytecodeStartOffset;

      
      Label currentLabel = labels[currentBytecodeOffset];
      if (currentLabel != null) {
        currentLabel.accept(methodVisitor, ((context.parsingOptions & 0x2) == 0));
      }

      
      while (stackMapFrameOffset != 0 && (context.currentFrameOffset == currentBytecodeOffset || context.currentFrameOffset == -1)) {



        
        if (context.currentFrameOffset != -1) {
          if (!compressedFrames || expandFrames) {
            methodVisitor.visitFrame(-1, context.currentFrameLocalCount, context.currentFrameLocalTypes, context.currentFrameStackCount, context.currentFrameStackTypes);

          
          }
          else {

            
            methodVisitor.visitFrame(context.currentFrameType, context.currentFrameLocalCountDelta, context.currentFrameLocalTypes, context.currentFrameStackCount, context.currentFrameStackTypes);
          } 






          
          insertFrame = false;
        } 
        if (stackMapFrameOffset < stackMapTableEndOffset) {
          
          stackMapFrameOffset = readStackMapFrame(stackMapFrameOffset, compressedFrames, expandFrames, context); continue;
        } 
        stackMapFrameOffset = 0;
      } 



      
      if (insertFrame) {
        if ((context.parsingOptions & 0x8) != 0) {
          methodVisitor.visitFrame(256, 0, null, 0, null);
        }
        insertFrame = false;
      } 

      
      int opcode = classBuffer[currentOffset] & 0xFF;
      switch (opcode) {
        case 0:
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
        case 9:
        case 10:
        case 11:
        case 12:
        case 13:
        case 14:
        case 15:
        case 46:
        case 47:
        case 48:
        case 49:
        case 50:
        case 51:
        case 52:
        case 53:
        case 79:
        case 80:
        case 81:
        case 82:
        case 83:
        case 84:
        case 85:
        case 86:
        case 87:
        case 88:
        case 89:
        case 90:
        case 91:
        case 92:
        case 93:
        case 94:
        case 95:
        case 96:
        case 97:
        case 98:
        case 99:
        case 100:
        case 101:
        case 102:
        case 103:
        case 104:
        case 105:
        case 106:
        case 107:
        case 108:
        case 109:
        case 110:
        case 111:
        case 112:
        case 113:
        case 114:
        case 115:
        case 116:
        case 117:
        case 118:
        case 119:
        case 120:
        case 121:
        case 122:
        case 123:
        case 124:
        case 125:
        case 126:
        case 127:
        case 128:
        case 129:
        case 130:
        case 131:
        case 133:
        case 134:
        case 135:
        case 136:
        case 137:
        case 138:
        case 139:
        case 140:
        case 141:
        case 142:
        case 143:
        case 144:
        case 145:
        case 146:
        case 147:
        case 148:
        case 149:
        case 150:
        case 151:
        case 152:
        case 172:
        case 173:
        case 174:
        case 175:
        case 176:
        case 177:
        case 190:
        case 191:
        case 194:
        case 195:
          methodVisitor.visitInsn(opcode);
          currentOffset++;
          break;
        case 26:
        case 27:
        case 28:
        case 29:
        case 30:
        case 31:
        case 32:
        case 33:
        case 34:
        case 35:
        case 36:
        case 37:
        case 38:
        case 39:
        case 40:
        case 41:
        case 42:
        case 43:
        case 44:
        case 45:
          opcode -= 26;
          methodVisitor.visitVarInsn(21 + (opcode >> 2), opcode & 0x3);
          currentOffset++;
          break;
        case 59:
        case 60:
        case 61:
        case 62:
        case 63:
        case 64:
        case 65:
        case 66:
        case 67:
        case 68:
        case 69:
        case 70:
        case 71:
        case 72:
        case 73:
        case 74:
        case 75:
        case 76:
        case 77:
        case 78:
          opcode -= 59;
          methodVisitor.visitVarInsn(54 + (opcode >> 2), opcode & 0x3);
          currentOffset++;
          break;
        case 153:
        case 154:
        case 155:
        case 156:
        case 157:
        case 158:
        case 159:
        case 160:
        case 161:
        case 162:
        case 163:
        case 164:
        case 165:
        case 166:
        case 167:
        case 168:
        case 198:
        case 199:
          methodVisitor.visitJumpInsn(opcode, labels[currentBytecodeOffset + 
                readShort(currentOffset + 1)]);
          currentOffset += 3;
          break;
        case 200:
        case 201:
          methodVisitor.visitJumpInsn(opcode - wideJumpOpcodeDelta, labels[currentBytecodeOffset + 
                
                readInt(currentOffset + 1)]);
          currentOffset += 5;
          break;






        
        case 202:
        case 203:
        case 204:
        case 205:
        case 206:
        case 207:
        case 208:
        case 209:
        case 210:
        case 211:
        case 212:
        case 213:
        case 214:
        case 215:
        case 216:
        case 217:
        case 218:
        case 219:
          opcode = (opcode < 218) ? (opcode - 49) : (opcode - 20);


          
          target = labels[currentBytecodeOffset + readUnsignedShort(currentOffset + 1)];
          if (opcode == 167 || opcode == 168) {
            
            methodVisitor.visitJumpInsn(opcode + 33, target);
          
          }
          else {
            
            opcode = (opcode < 167) ? ((opcode + 1 ^ 0x1) - 1) : (opcode ^ 0x1);
            Label endif = createLabel(currentBytecodeOffset + 3, labels);
            methodVisitor.visitJumpInsn(opcode, endif);
            methodVisitor.visitJumpInsn(200, target);

            
            insertFrame = true;
          } 
          currentOffset += 3;
          break;

        
        case 220:
          methodVisitor.visitJumpInsn(200, labels[currentBytecodeOffset + 
                readInt(currentOffset + 1)]);


          
          insertFrame = true;
          currentOffset += 5;
          break;
        case 196:
          opcode = classBuffer[currentOffset + 1] & 0xFF;
          if (opcode == 132) {
            methodVisitor.visitIincInsn(
                readUnsignedShort(currentOffset + 2), readShort(currentOffset + 4));
            currentOffset += 6; break;
          } 
          methodVisitor.visitVarInsn(opcode, readUnsignedShort(currentOffset + 2));
          currentOffset += 4;
          break;


        
        case 170:
          currentOffset += 4 - (currentBytecodeOffset & 0x3);
          
          defaultLabel = labels[currentBytecodeOffset + readInt(currentOffset)];
          low = readInt(currentOffset + 4);
          high = readInt(currentOffset + 8);
          currentOffset += 12;
          table = new Label[high - low + 1];
          for (i = 0; i < table.length; i++) {
            table[i] = labels[currentBytecodeOffset + readInt(currentOffset)];
            currentOffset += 4;
          } 
          methodVisitor.visitTableSwitchInsn(low, high, defaultLabel, table);
          break;


        
        case 171:
          currentOffset += 4 - (currentBytecodeOffset & 0x3);
          
          defaultLabel = labels[currentBytecodeOffset + readInt(currentOffset)];
          numPairs = readInt(currentOffset + 4);
          currentOffset += 8;
          keys = new int[numPairs];
          values = new Label[numPairs];
          for (i = 0; i < numPairs; i++) {
            keys[i] = readInt(currentOffset);
            values[i] = labels[currentBytecodeOffset + readInt(currentOffset + 4)];
            currentOffset += 8;
          } 
          methodVisitor.visitLookupSwitchInsn(defaultLabel, keys, values);
          break;
        
        case 21:
        case 22:
        case 23:
        case 24:
        case 25:
        case 54:
        case 55:
        case 56:
        case 57:
        case 58:
        case 169:
          methodVisitor.visitVarInsn(opcode, classBuffer[currentOffset + 1] & 0xFF);
          currentOffset += 2;
          break;
        case 16:
        case 188:
          methodVisitor.visitIntInsn(opcode, classBuffer[currentOffset + 1]);
          currentOffset += 2;
          break;
        case 17:
          methodVisitor.visitIntInsn(opcode, readShort(currentOffset + 1));
          currentOffset += 3;
          break;
        case 18:
          methodVisitor.visitLdcInsn(readConst(classBuffer[currentOffset + 1] & 0xFF, charBuffer));
          currentOffset += 2;
          break;
        case 19:
        case 20:
          methodVisitor.visitLdcInsn(readConst(readUnsignedShort(currentOffset + 1), charBuffer));
          currentOffset += 3;
          break;
        
        case 178:
        case 179:
        case 180:
        case 181:
        case 182:
        case 183:
        case 184:
        case 185:
          cpInfoOffset = this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)];
          nameAndTypeCpInfoOffset = this.cpInfoOffsets[readUnsignedShort(cpInfoOffset + 2)];
          owner = readClass(cpInfoOffset, charBuffer);
          str1 = readUTF8(nameAndTypeCpInfoOffset, charBuffer);
          str2 = readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
          if (opcode < 182) {
            methodVisitor.visitFieldInsn(opcode, owner, str1, str2);
          } else {
            boolean isInterface = (classBuffer[cpInfoOffset - 1] == 11);
            
            methodVisitor.visitMethodInsn(opcode, owner, str1, str2, isInterface);
          } 
          if (opcode == 185) {
            currentOffset += 5; break;
          } 
          currentOffset += 3;
          break;


        
        case 186:
          cpInfoOffset = this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)];
          nameAndTypeCpInfoOffset = this.cpInfoOffsets[readUnsignedShort(cpInfoOffset + 2)];
          name = readUTF8(nameAndTypeCpInfoOffset, charBuffer);
          descriptor = readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
          bootstrapMethodOffset = this.bootstrapMethodOffsets[readUnsignedShort(cpInfoOffset)];
          
          handle = (Handle)readConst(readUnsignedShort(bootstrapMethodOffset), charBuffer);
          
          bootstrapMethodArguments = new Object[readUnsignedShort(bootstrapMethodOffset + 2)];
          bootstrapMethodOffset += 4;
          for (j = 0; j < bootstrapMethodArguments.length; j++) {
            bootstrapMethodArguments[j] = 
              readConst(readUnsignedShort(bootstrapMethodOffset), charBuffer);
            bootstrapMethodOffset += 2;
          } 
          methodVisitor.visitInvokeDynamicInsn(name, descriptor, handle, bootstrapMethodArguments);
          
          currentOffset += 5;
          break;
        
        case 187:
        case 189:
        case 192:
        case 193:
          methodVisitor.visitTypeInsn(opcode, readClass(currentOffset + 1, charBuffer));
          currentOffset += 3;
          break;
        case 132:
          methodVisitor.visitIincInsn(classBuffer[currentOffset + 1] & 0xFF, classBuffer[currentOffset + 2]);
          
          currentOffset += 3;
          break;
        case 197:
          methodVisitor.visitMultiANewArrayInsn(
              readClass(currentOffset + 1, charBuffer), classBuffer[currentOffset + 3] & 0xFF);
          currentOffset += 4;
          break;
        default:
          throw new AssertionError();
      } 

      
      while (visibleTypeAnnotationOffsets != null && currentVisibleTypeAnnotationIndex < visibleTypeAnnotationOffsets.length && currentVisibleTypeAnnotationBytecodeOffset <= currentBytecodeOffset) {

        
        if (currentVisibleTypeAnnotationBytecodeOffset == currentBytecodeOffset) {

          
          int currentAnnotationOffset = readTypeAnnotationTarget(context, visibleTypeAnnotationOffsets[currentVisibleTypeAnnotationIndex]);

          
          String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
          currentAnnotationOffset += 2;
          
          readElementValues(methodVisitor
              .visitInsnAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, true), currentAnnotationOffset, true, charBuffer);
        } 







        
        currentVisibleTypeAnnotationBytecodeOffset = getTypeAnnotationBytecodeOffset(visibleTypeAnnotationOffsets, ++currentVisibleTypeAnnotationIndex);
      } 


      
      while (invisibleTypeAnnotationOffsets != null && currentInvisibleTypeAnnotationIndex < invisibleTypeAnnotationOffsets.length && currentInvisibleTypeAnnotationBytecodeOffset <= currentBytecodeOffset) {

        
        if (currentInvisibleTypeAnnotationBytecodeOffset == currentBytecodeOffset) {

          
          int currentAnnotationOffset = readTypeAnnotationTarget(context, invisibleTypeAnnotationOffsets[currentInvisibleTypeAnnotationIndex]);

          
          String annotationDescriptor = readUTF8(currentAnnotationOffset, charBuffer);
          currentAnnotationOffset += 2;
          
          readElementValues(methodVisitor
              .visitInsnAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, annotationDescriptor, false), currentAnnotationOffset, true, charBuffer);
        } 







        
        currentInvisibleTypeAnnotationBytecodeOffset = getTypeAnnotationBytecodeOffset(invisibleTypeAnnotationOffsets, ++currentInvisibleTypeAnnotationIndex);
      } 
    } 
    
    if (labels[codeLength] != null) {
      methodVisitor.visitLabel(labels[codeLength]);
    }

    
    if (localVariableTableOffset != 0 && (context.parsingOptions & 0x2) == 0) {
      
      int[] typeTable = null;
      if (localVariableTypeTableOffset != 0) {
        typeTable = new int[readUnsignedShort(localVariableTypeTableOffset) * 3];
        currentOffset = localVariableTypeTableOffset + 2;
        int typeTableIndex = typeTable.length;
        while (typeTableIndex > 0) {
          
          typeTable[--typeTableIndex] = currentOffset + 6;
          typeTable[--typeTableIndex] = readUnsignedShort(currentOffset + 8);
          typeTable[--typeTableIndex] = readUnsignedShort(currentOffset);
          currentOffset += 10;
        } 
      } 
      int localVariableTableLength = readUnsignedShort(localVariableTableOffset);
      currentOffset = localVariableTableOffset + 2;
      while (localVariableTableLength-- > 0) {
        int startPc = readUnsignedShort(currentOffset);
        int length = readUnsignedShort(currentOffset + 2);
        String name = readUTF8(currentOffset + 4, charBuffer);
        String descriptor = readUTF8(currentOffset + 6, charBuffer);
        int index = readUnsignedShort(currentOffset + 8);
        currentOffset += 10;
        String signature = null;
        if (typeTable != null) {
          for (int i = 0; i < typeTable.length; i += 3) {
            if (typeTable[i] == startPc && typeTable[i + 1] == index) {
              signature = readUTF8(typeTable[i + 2], charBuffer);
              break;
            } 
          } 
        }
        methodVisitor.visitLocalVariable(name, descriptor, signature, labels[startPc], labels[startPc + length], index);
      } 
    } 


    
    if (visibleTypeAnnotationOffsets != null) {
      for (int typeAnnotationOffset : visibleTypeAnnotationOffsets) {
        int targetType = readByte(typeAnnotationOffset);
        if (targetType == 64 || targetType == 65) {

          
          currentOffset = readTypeAnnotationTarget(context, typeAnnotationOffset);
          
          String annotationDescriptor = readUTF8(currentOffset, charBuffer);
          currentOffset += 2;
          
          readElementValues(methodVisitor
              .visitLocalVariableAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, context.currentLocalVariableAnnotationRangeStarts, context.currentLocalVariableAnnotationRangeEnds, context.currentLocalVariableAnnotationRangeIndices, annotationDescriptor, true), currentOffset, true, charBuffer);
        } 
      } 
    }











    
    if (invisibleTypeAnnotationOffsets != null) {
      for (int typeAnnotationOffset : invisibleTypeAnnotationOffsets) {
        int targetType = readByte(typeAnnotationOffset);
        if (targetType == 64 || targetType == 65) {

          
          currentOffset = readTypeAnnotationTarget(context, typeAnnotationOffset);
          
          String annotationDescriptor = readUTF8(currentOffset, charBuffer);
          currentOffset += 2;
          
          readElementValues(methodVisitor
              .visitLocalVariableAnnotation(context.currentTypeAnnotationTarget, context.currentTypeAnnotationTargetPath, context.currentLocalVariableAnnotationRangeStarts, context.currentLocalVariableAnnotationRangeEnds, context.currentLocalVariableAnnotationRangeIndices, annotationDescriptor, false), currentOffset, true, charBuffer);
        } 
      } 
    }











    
    while (attributes != null) {
      
      Attribute nextAttribute = attributes.nextAttribute;
      attributes.nextAttribute = null;
      methodVisitor.visitAttribute(attributes);
      attributes = nextAttribute;
    } 

    
    methodVisitor.visitMaxs(maxStack, maxLocals);
  }











  
  protected Label readLabel(int bytecodeOffset, Label[] labels) {
    if (bytecodeOffset >= labels.length) {
      return new Label();
    }
    
    if (labels[bytecodeOffset] == null) {
      labels[bytecodeOffset] = new Label();
    }
    return labels[bytecodeOffset];
  }









  
  private Label createLabel(int bytecodeOffset, Label[] labels) {
    Label label = readLabel(bytecodeOffset, labels);
    label.flags = (short)(label.flags & 0xFFFFFFFE);
    return label;
  }








  
  private void createDebugLabel(int bytecodeOffset, Label[] labels) {
    if (labels[bytecodeOffset] == null) {
      (readLabel(bytecodeOffset, labels)).flags = (short)((readLabel(bytecodeOffset, labels)).flags | 0x1);
    }
  }






















  
  private int[] readTypeAnnotations(MethodVisitor methodVisitor, Context context, int runtimeTypeAnnotationsOffset, boolean visible) {
    char[] charBuffer = context.charBuffer;
    int currentOffset = runtimeTypeAnnotationsOffset;
    
    int[] typeAnnotationsOffsets = new int[readUnsignedShort(currentOffset)];
    currentOffset += 2;
    
    for (int i = 0; i < typeAnnotationsOffsets.length; i++) {
      int tableLength; typeAnnotationsOffsets[i] = currentOffset;

      
      int targetType = readInt(currentOffset);
      switch (targetType >>> 24) {

        
        case 64:
        case 65:
          tableLength = readUnsignedShort(currentOffset + 1);
          currentOffset += 3;
          while (tableLength-- > 0) {
            int startPc = readUnsignedShort(currentOffset);
            int length = readUnsignedShort(currentOffset + 2);
            
            currentOffset += 6;
            createLabel(startPc, context.currentMethodLabels);
            createLabel(startPc + length, context.currentMethodLabels);
          } 
          break;
        case 71:
        case 72:
        case 73:
        case 74:
        case 75:
          currentOffset += 4;
          break;
        case 16:
        case 17:
        case 18:
        case 23:
        case 66:
        case 67:
        case 68:
        case 69:
        case 70:
          currentOffset += 3;
          break;






        
        default:
          throw new IllegalArgumentException();
      } 

      
      int pathLength = readByte(currentOffset);
      if (targetType >>> 24 == 66) {
        
        TypePath path = (pathLength == 0) ? null : new TypePath(this.classFileBuffer, currentOffset);
        currentOffset += 1 + 2 * pathLength;
        
        String annotationDescriptor = readUTF8(currentOffset, charBuffer);
        currentOffset += 2;

        
        currentOffset = readElementValues(methodVisitor
            .visitTryCatchAnnotation(targetType & 0xFFFFFF00, path, annotationDescriptor, visible), currentOffset, true, charBuffer);


      
      }
      else {


        
        currentOffset += 3 + 2 * pathLength;


        
        currentOffset = readElementValues(null, currentOffset, true, charBuffer);
      } 
    } 
    
    return typeAnnotationsOffsets;
  }











  
  private int getTypeAnnotationBytecodeOffset(int[] typeAnnotationOffsets, int typeAnnotationIndex) {
    if (typeAnnotationOffsets == null || typeAnnotationIndex >= typeAnnotationOffsets.length || 
      
      readByte(typeAnnotationOffsets[typeAnnotationIndex]) < 67) {
      return -1;
    }
    return readUnsignedShort(typeAnnotationOffsets[typeAnnotationIndex] + 1);
  }










  
  private int readTypeAnnotationTarget(Context context, int typeAnnotationOffset) {
    int tableLength, i, currentOffset = typeAnnotationOffset;
    
    int targetType = readInt(typeAnnotationOffset);
    switch (targetType >>> 24) {
      case 0:
      case 1:
      case 22:
        targetType &= 0xFFFF0000;
        currentOffset += 2;
        break;
      case 19:
      case 20:
      case 21:
        targetType &= 0xFF000000;
        currentOffset++;
        break;
      case 64:
      case 65:
        targetType &= 0xFF000000;
        tableLength = readUnsignedShort(currentOffset + 1);
        currentOffset += 3;
        context.currentLocalVariableAnnotationRangeStarts = new Label[tableLength];
        context.currentLocalVariableAnnotationRangeEnds = new Label[tableLength];
        context.currentLocalVariableAnnotationRangeIndices = new int[tableLength];
        for (i = 0; i < tableLength; i++) {
          int startPc = readUnsignedShort(currentOffset);
          int length = readUnsignedShort(currentOffset + 2);
          int index = readUnsignedShort(currentOffset + 4);
          currentOffset += 6;
          context.currentLocalVariableAnnotationRangeStarts[i] = 
            createLabel(startPc, context.currentMethodLabels);
          context.currentLocalVariableAnnotationRangeEnds[i] = 
            createLabel(startPc + length, context.currentMethodLabels);
          context.currentLocalVariableAnnotationRangeIndices[i] = index;
        } 
        break;
      case 71:
      case 72:
      case 73:
      case 74:
      case 75:
        targetType &= 0xFF0000FF;
        currentOffset += 4;
        break;
      case 16:
      case 17:
      case 18:
      case 23:
      case 66:
        targetType &= 0xFFFFFF00;
        currentOffset += 3;
        break;
      case 67:
      case 68:
      case 69:
      case 70:
        targetType &= 0xFF000000;
        currentOffset += 3;
        break;
      default:
        throw new IllegalArgumentException();
    } 
    context.currentTypeAnnotationTarget = targetType;
    
    int pathLength = readByte(currentOffset);
    context.currentTypeAnnotationTargetPath = (pathLength == 0) ? null : new TypePath(this.classFileBuffer, currentOffset);

    
    return currentOffset + 1 + 2 * pathLength;
  }















  
  private void readParameterAnnotations(MethodVisitor methodVisitor, Context context, int runtimeParameterAnnotationsOffset, boolean visible) {
    int currentOffset = runtimeParameterAnnotationsOffset;
    int numParameters = this.classFileBuffer[currentOffset++] & 0xFF;
    methodVisitor.visitAnnotableParameterCount(numParameters, visible);
    char[] charBuffer = context.charBuffer;
    for (int i = 0; i < numParameters; i++) {
      int numAnnotations = readUnsignedShort(currentOffset);
      currentOffset += 2;
      while (numAnnotations-- > 0) {
        
        String annotationDescriptor = readUTF8(currentOffset, charBuffer);
        currentOffset += 2;

        
        currentOffset = readElementValues(methodVisitor
            .visitParameterAnnotation(i, annotationDescriptor, visible), currentOffset, true, charBuffer);
      } 
    } 
  }





















  
  private int readElementValues(AnnotationVisitor annotationVisitor, int annotationOffset, boolean named, char[] charBuffer) {
    int currentOffset = annotationOffset;
    
    int numElementValuePairs = readUnsignedShort(currentOffset);
    currentOffset += 2;
    if (named) {
      
      while (numElementValuePairs-- > 0) {
        String elementName = readUTF8(currentOffset, charBuffer);
        
        currentOffset = readElementValue(annotationVisitor, currentOffset + 2, elementName, charBuffer);
      } 
    } else {
      
      while (numElementValuePairs-- > 0)
      {
        currentOffset = readElementValue(annotationVisitor, currentOffset, null, charBuffer);
      }
    } 
    if (annotationVisitor != null) {
      annotationVisitor.visitEnd();
    }
    return currentOffset;
  }
  
  private int readElementValue(AnnotationVisitor annotationVisitor, int elementValueOffset, String elementName, char[] charBuffer) {
    int numValues;
    byte[] byteValues;
    int i;
    boolean[] booleanValues;
    int j;
    short[] shortValues;
    int k;
    char[] charValues;
    int m, intValues[], n;
    long[] longValues;
    int i1;
    float[] floatValues;
    int i2;
    double[] doubleValues;
    int i3, currentOffset = elementValueOffset;
    if (annotationVisitor == null) {
      switch (this.classFileBuffer[currentOffset] & 0xFF) {
        case 101:
          return currentOffset + 5;
        case 64:
          return readElementValues(null, currentOffset + 3, true, charBuffer);
        case 91:
          return readElementValues(null, currentOffset + 1, false, charBuffer);
      } 
      return currentOffset + 3;
    } 
    
    switch (this.classFileBuffer[currentOffset++] & 0xFF) {
      case 66:
        annotationVisitor.visit(elementName, 
            Byte.valueOf((byte)readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset)])));
        currentOffset += 2;














































































































































        
        return currentOffset;case 67: annotationVisitor.visit(elementName, Character.valueOf((char)readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset)]))); currentOffset += 2; return currentOffset;case 68: case 70: case 73: case 74: annotationVisitor.visit(elementName, readConst(readUnsignedShort(currentOffset), charBuffer)); currentOffset += 2; return currentOffset;case 83: annotationVisitor.visit(elementName, Short.valueOf((short)readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset)]))); currentOffset += 2; return currentOffset;case 90: annotationVisitor.visit(elementName, (readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset)]) == 0) ? Boolean.FALSE : Boolean.TRUE); currentOffset += 2; return currentOffset;case 115: annotationVisitor.visit(elementName, readUTF8(currentOffset, charBuffer)); currentOffset += 2; return currentOffset;case 101: annotationVisitor.visitEnum(elementName, readUTF8(currentOffset, charBuffer), readUTF8(currentOffset + 2, charBuffer)); currentOffset += 4; return currentOffset;case 99: annotationVisitor.visit(elementName, Type.getType(readUTF8(currentOffset, charBuffer))); currentOffset += 2; return currentOffset;case 64: currentOffset = readElementValues(annotationVisitor.visitAnnotation(elementName, readUTF8(currentOffset, charBuffer)), currentOffset + 2, true, charBuffer); return currentOffset;case 91: numValues = readUnsignedShort(currentOffset); currentOffset += 2; if (numValues == 0) return readElementValues(annotationVisitor.visitArray(elementName), currentOffset - 2, false, charBuffer);  switch (this.classFileBuffer[currentOffset] & 0xFF) { case 66: byteValues = new byte[numValues]; for (i = 0; i < numValues; i++) { byteValues[i] = (byte)readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]); currentOffset += 3; }  annotationVisitor.visit(elementName, byteValues); return currentOffset;case 90: booleanValues = new boolean[numValues]; for (j = 0; j < numValues; j++) { booleanValues[j] = (readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]) != 0); currentOffset += 3; }  annotationVisitor.visit(elementName, booleanValues); return currentOffset;case 83: shortValues = new short[numValues]; for (k = 0; k < numValues; k++) { shortValues[k] = (short)readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]); currentOffset += 3; }  annotationVisitor.visit(elementName, shortValues); return currentOffset;case 67: charValues = new char[numValues]; for (m = 0; m < numValues; m++) { charValues[m] = (char)readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]); currentOffset += 3; }  annotationVisitor.visit(elementName, charValues); return currentOffset;case 73: intValues = new int[numValues]; for (n = 0; n < numValues; n++) { intValues[n] = readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]); currentOffset += 3; }  annotationVisitor.visit(elementName, intValues); return currentOffset;case 74: longValues = new long[numValues]; for (i1 = 0; i1 < numValues; i1++) { longValues[i1] = readLong(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)]); currentOffset += 3; }  annotationVisitor.visit(elementName, longValues); return currentOffset;case 70: floatValues = new float[numValues]; for (i2 = 0; i2 < numValues; i2++) { floatValues[i2] = Float.intBitsToFloat(readInt(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)])); currentOffset += 3; }  annotationVisitor.visit(elementName, floatValues); return currentOffset;case 68: doubleValues = new double[numValues]; for (i3 = 0; i3 < numValues; i3++) { doubleValues[i3] = Double.longBitsToDouble(readLong(this.cpInfoOffsets[readUnsignedShort(currentOffset + 1)])); currentOffset += 3; }  annotationVisitor.visit(elementName, doubleValues); return currentOffset; }  currentOffset = readElementValues(annotationVisitor.visitArray(elementName), currentOffset - 2, false, charBuffer); return currentOffset;
    } 
    throw new IllegalArgumentException();
  }








  
  private void computeImplicitFrame(Context context) {
    String methodDescriptor = context.currentMethodDescriptor;
    Object[] locals = context.currentFrameLocalTypes;
    int numLocal = 0;
    if ((context.currentMethodAccessFlags & 0x8) == 0) {
      if ("<init>".equals(context.currentMethodName)) {
        locals[numLocal++] = Opcodes.UNINITIALIZED_THIS;
      } else {
        locals[numLocal++] = readClass(this.header + 2, context.charBuffer);
      } 
    }

    
    int currentMethodDescritorOffset = 1;
    while (true) {
      int currentArgumentDescriptorStartOffset = currentMethodDescritorOffset;
      switch (methodDescriptor.charAt(currentMethodDescritorOffset++)) {
        case 'B':
        case 'C':
        case 'I':
        case 'S':
        case 'Z':
          locals[numLocal++] = Opcodes.INTEGER;
          continue;
        case 'F':
          locals[numLocal++] = Opcodes.FLOAT;
          continue;
        case 'J':
          locals[numLocal++] = Opcodes.LONG;
          continue;
        case 'D':
          locals[numLocal++] = Opcodes.DOUBLE;
          continue;
        case '[':
          while (methodDescriptor.charAt(currentMethodDescritorOffset) == '[') {
            currentMethodDescritorOffset++;
          }
          if (methodDescriptor.charAt(currentMethodDescritorOffset) == 'L') {
            currentMethodDescritorOffset++;
            while (methodDescriptor.charAt(currentMethodDescritorOffset) != ';') {
              currentMethodDescritorOffset++;
            }
          } 
          locals[numLocal++] = methodDescriptor
            .substring(currentArgumentDescriptorStartOffset, ++currentMethodDescritorOffset);
          continue;
        
        case 'L':
          while (methodDescriptor.charAt(currentMethodDescritorOffset) != ';') {
            currentMethodDescritorOffset++;
          }
          locals[numLocal++] = methodDescriptor
            .substring(currentArgumentDescriptorStartOffset + 1, currentMethodDescritorOffset++); continue;
      } 
      break;
    } 
    context.currentFrameLocalCount = numLocal;
  }





















  
  private int readStackMapFrame(int stackMapFrameOffset, boolean compressed, boolean expand, Context context) {
    int frameType, offsetDelta, currentOffset = stackMapFrameOffset;
    char[] charBuffer = context.charBuffer;
    Label[] labels = context.currentMethodLabels;
    
    if (compressed) {
      
      frameType = this.classFileBuffer[currentOffset++] & 0xFF;
    } else {
      frameType = 255;
      context.currentFrameOffset = -1;
    } 
    
    context.currentFrameLocalCountDelta = 0;
    if (frameType < 64) {
      offsetDelta = frameType;
      context.currentFrameType = 3;
      context.currentFrameStackCount = 0;
    } else if (frameType < 128) {
      offsetDelta = frameType - 64;
      
      currentOffset = readVerificationTypeInfo(currentOffset, context.currentFrameStackTypes, 0, charBuffer, labels);
      
      context.currentFrameType = 4;
      context.currentFrameStackCount = 1;
    } else if (frameType >= 247) {
      offsetDelta = readUnsignedShort(currentOffset);
      currentOffset += 2;
      if (frameType == 247) {
        
        currentOffset = readVerificationTypeInfo(currentOffset, context.currentFrameStackTypes, 0, charBuffer, labels);
        
        context.currentFrameType = 4;
        context.currentFrameStackCount = 1;
      } else if (frameType >= 248 && frameType < 251) {
        context.currentFrameType = 2;
        context.currentFrameLocalCountDelta = 251 - frameType;
        context.currentFrameLocalCount -= context.currentFrameLocalCountDelta;
        context.currentFrameStackCount = 0;
      } else if (frameType == 251) {
        context.currentFrameType = 3;
        context.currentFrameStackCount = 0;
      } else if (frameType < 255) {
        int local = expand ? context.currentFrameLocalCount : 0;
        for (int k = frameType - 251; k > 0; k--)
        {
          currentOffset = readVerificationTypeInfo(currentOffset, context.currentFrameLocalTypes, local++, charBuffer, labels);
        }
        
        context.currentFrameType = 1;
        context.currentFrameLocalCountDelta = frameType - 251;
        context.currentFrameLocalCount += context.currentFrameLocalCountDelta;
        context.currentFrameStackCount = 0;
      } else {
        int numberOfLocals = readUnsignedShort(currentOffset);
        currentOffset += 2;
        context.currentFrameType = 0;
        context.currentFrameLocalCountDelta = numberOfLocals;
        context.currentFrameLocalCount = numberOfLocals;
        for (int local = 0; local < numberOfLocals; local++)
        {
          currentOffset = readVerificationTypeInfo(currentOffset, context.currentFrameLocalTypes, local, charBuffer, labels);
        }
        
        int numberOfStackItems = readUnsignedShort(currentOffset);
        currentOffset += 2;
        context.currentFrameStackCount = numberOfStackItems;
        for (int stack = 0; stack < numberOfStackItems; stack++)
        {
          currentOffset = readVerificationTypeInfo(currentOffset, context.currentFrameStackTypes, stack, charBuffer, labels);
        }
      } 
    } else {
      
      throw new IllegalArgumentException();
    } 
    context.currentFrameOffset += offsetDelta + 1;
    createLabel(context.currentFrameOffset, labels);
    return currentOffset;
  }



















  
  private int readVerificationTypeInfo(int verificationTypeInfoOffset, Object[] frame, int index, char[] charBuffer, Label[] labels) {
    int currentOffset = verificationTypeInfoOffset;
    int tag = this.classFileBuffer[currentOffset++] & 0xFF;
    switch (tag) {
      case 0:
        frame[index] = Opcodes.TOP;





























        
        return currentOffset;case 1: frame[index] = Opcodes.INTEGER; return currentOffset;case 2: frame[index] = Opcodes.FLOAT; return currentOffset;case 3: frame[index] = Opcodes.DOUBLE; return currentOffset;case 4: frame[index] = Opcodes.LONG; return currentOffset;case 5: frame[index] = Opcodes.NULL; return currentOffset;case 6: frame[index] = Opcodes.UNINITIALIZED_THIS; return currentOffset;case 7: frame[index] = readClass(currentOffset, charBuffer); currentOffset += 2; return currentOffset;case 8: frame[index] = createLabel(readUnsignedShort(currentOffset), labels); currentOffset += 2; return currentOffset;
    } 
    throw new IllegalArgumentException();
  }











  
  final int getFirstAttributeOffset() {
    int currentOffset = this.header + 8 + readUnsignedShort(this.header + 6) * 2;

    
    int fieldsCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    
    while (fieldsCount-- > 0) {


      
      int attributesCount = readUnsignedShort(currentOffset + 6);
      currentOffset += 8;
      
      while (attributesCount-- > 0)
      {


        
        currentOffset += 6 + readInt(currentOffset + 2);
      }
    } 

    
    int methodsCount = readUnsignedShort(currentOffset);
    currentOffset += 2;
    while (methodsCount-- > 0) {
      int attributesCount = readUnsignedShort(currentOffset + 6);
      currentOffset += 8;
      while (attributesCount-- > 0) {
        currentOffset += 6 + readInt(currentOffset + 2);
      }
    } 

    
    return currentOffset + 2;
  }







  
  private int[] readBootstrapMethodsAttribute(int maxStringLength) {
    char[] charBuffer = new char[maxStringLength];
    int currentAttributeOffset = getFirstAttributeOffset();
    for (int i = readUnsignedShort(currentAttributeOffset - 2); i > 0; i--) {
      
      String attributeName = readUTF8(currentAttributeOffset, charBuffer);
      int attributeLength = readInt(currentAttributeOffset + 2);
      currentAttributeOffset += 6;
      if ("BootstrapMethods".equals(attributeName)) {
        
        int[] result = new int[readUnsignedShort(currentAttributeOffset)];
        
        int currentBootstrapMethodOffset = currentAttributeOffset + 2;
        for (int j = 0; j < result.length; j++) {
          result[j] = currentBootstrapMethodOffset;

          
          currentBootstrapMethodOffset += 4 + 
            readUnsignedShort(currentBootstrapMethodOffset + 2) * 2;
        } 
        return result;
      } 
      currentAttributeOffset += attributeLength;
    } 
    throw new IllegalArgumentException();
  }



























  
  private Attribute readAttribute(Attribute[] attributePrototypes, String type, int offset, int length, char[] charBuffer, int codeAttributeOffset, Label[] labels) {
    for (Attribute attributePrototype : attributePrototypes) {
      if (attributePrototype.type.equals(type)) {
        return attributePrototype.read(this, offset, length, charBuffer, codeAttributeOffset, labels);
      }
    } 
    
    return (new Attribute(type)).read(this, offset, length, null, -1, null);
  }









  
  public int getItemCount() {
    return this.cpInfoOffsets.length;
  }










  
  public int getItem(int constantPoolEntryIndex) {
    return this.cpInfoOffsets[constantPoolEntryIndex];
  }







  
  public int getMaxStringLength() {
    return this.maxStringLength;
  }







  
  public int readByte(int offset) {
    return this.classFileBuffer[offset] & 0xFF;
  }







  
  public int readUnsignedShort(int offset) {
    byte[] classBuffer = this.classFileBuffer;
    return (classBuffer[offset] & 0xFF) << 8 | classBuffer[offset + 1] & 0xFF;
  }







  
  public short readShort(int offset) {
    byte[] classBuffer = this.classFileBuffer;
    return (short)((classBuffer[offset] & 0xFF) << 8 | classBuffer[offset + 1] & 0xFF);
  }







  
  public int readInt(int offset) {
    byte[] classBuffer = this.classFileBuffer;
    return (classBuffer[offset] & 0xFF) << 24 | (classBuffer[offset + 1] & 0xFF) << 16 | (classBuffer[offset + 2] & 0xFF) << 8 | classBuffer[offset + 3] & 0xFF;
  }










  
  public long readLong(int offset) {
    long l1 = readInt(offset);
    long l0 = readInt(offset + 4) & 0xFFFFFFFFL;
    return l1 << 32L | l0;
  }












  
  public String readUTF8(int offset, char[] charBuffer) {
    int constantPoolEntryIndex = readUnsignedShort(offset);
    if (offset == 0 || constantPoolEntryIndex == 0) {
      return null;
    }
    return readUtf(constantPoolEntryIndex, charBuffer);
  }









  
  final String readUtf(int constantPoolEntryIndex, char[] charBuffer) {
    String value = this.constantUtf8Values[constantPoolEntryIndex];
    if (value != null) {
      return value;
    }
    int cpInfoOffset = this.cpInfoOffsets[constantPoolEntryIndex];
    this.constantUtf8Values[constantPoolEntryIndex] = 
      readUtf(cpInfoOffset + 2, readUnsignedShort(cpInfoOffset), charBuffer); return readUtf(cpInfoOffset + 2, readUnsignedShort(cpInfoOffset), charBuffer);
  }









  
  private String readUtf(int utfOffset, int utfLength, char[] charBuffer) {
    int currentOffset = utfOffset;
    int endOffset = currentOffset + utfLength;
    int strLength = 0;
    byte[] classBuffer = this.classFileBuffer;
    while (currentOffset < endOffset) {
      int currentByte = classBuffer[currentOffset++];
      if ((currentByte & 0x80) == 0) {
        charBuffer[strLength++] = (char)(currentByte & 0x7F); continue;
      }  if ((currentByte & 0xE0) == 192) {
        charBuffer[strLength++] = (char)(((currentByte & 0x1F) << 6) + (classBuffer[currentOffset++] & 0x3F));
        continue;
      } 
      charBuffer[strLength++] = (char)(((currentByte & 0xF) << 12) + ((classBuffer[currentOffset++] & 0x3F) << 6) + (classBuffer[currentOffset++] & 0x3F));
    } 




    
    return new String(charBuffer, 0, strLength);
  }















  
  private String readStringish(int offset, char[] charBuffer) {
    return readUTF8(this.cpInfoOffsets[readUnsignedShort(offset)], charBuffer);
  }











  
  public String readClass(int offset, char[] charBuffer) {
    return readStringish(offset, charBuffer);
  }











  
  public String readModule(int offset, char[] charBuffer) {
    return readStringish(offset, charBuffer);
  }











  
  public String readPackage(int offset, char[] charBuffer) {
    return readStringish(offset, charBuffer);
  }










  
  private ConstantDynamic readConstantDynamic(int constantPoolEntryIndex, char[] charBuffer) {
    ConstantDynamic constantDynamic = this.constantDynamicValues[constantPoolEntryIndex];
    if (constantDynamic != null) {
      return constantDynamic;
    }
    int cpInfoOffset = this.cpInfoOffsets[constantPoolEntryIndex];
    int nameAndTypeCpInfoOffset = this.cpInfoOffsets[readUnsignedShort(cpInfoOffset + 2)];
    String name = readUTF8(nameAndTypeCpInfoOffset, charBuffer);
    String descriptor = readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
    int bootstrapMethodOffset = this.bootstrapMethodOffsets[readUnsignedShort(cpInfoOffset)];
    Handle handle = (Handle)readConst(readUnsignedShort(bootstrapMethodOffset), charBuffer);
    Object[] bootstrapMethodArguments = new Object[readUnsignedShort(bootstrapMethodOffset + 2)];
    bootstrapMethodOffset += 4;
    for (int i = 0; i < bootstrapMethodArguments.length; i++) {
      bootstrapMethodArguments[i] = readConst(readUnsignedShort(bootstrapMethodOffset), charBuffer);
      bootstrapMethodOffset += 2;
    } 
    this.constantDynamicValues[constantPoolEntryIndex] = new ConstantDynamic(name, descriptor, handle, bootstrapMethodArguments); return new ConstantDynamic(name, descriptor, handle, bootstrapMethodArguments);
  }












  
  public Object readConst(int constantPoolEntryIndex, char[] charBuffer) {
    int referenceKind, referenceCpInfoOffset, nameAndTypeCpInfoOffset;
    String owner, name, descriptor;
    boolean isInterface;
    int cpInfoOffset = this.cpInfoOffsets[constantPoolEntryIndex];
    switch (this.classFileBuffer[cpInfoOffset - 1]) {
      case 3:
        return Integer.valueOf(readInt(cpInfoOffset));
      case 4:
        return Float.valueOf(Float.intBitsToFloat(readInt(cpInfoOffset)));
      case 5:
        return Long.valueOf(readLong(cpInfoOffset));
      case 6:
        return Double.valueOf(Double.longBitsToDouble(readLong(cpInfoOffset)));
      case 7:
        return Type.getObjectType(readUTF8(cpInfoOffset, charBuffer));
      case 8:
        return readUTF8(cpInfoOffset, charBuffer);
      case 16:
        return Type.getMethodType(readUTF8(cpInfoOffset, charBuffer));
      case 15:
        referenceKind = readByte(cpInfoOffset);
        referenceCpInfoOffset = this.cpInfoOffsets[readUnsignedShort(cpInfoOffset + 1)];
        nameAndTypeCpInfoOffset = this.cpInfoOffsets[readUnsignedShort(referenceCpInfoOffset + 2)];
        owner = readClass(referenceCpInfoOffset, charBuffer);
        name = readUTF8(nameAndTypeCpInfoOffset, charBuffer);
        descriptor = readUTF8(nameAndTypeCpInfoOffset + 2, charBuffer);
        isInterface = (this.classFileBuffer[referenceCpInfoOffset - 1] == 11);
        
        return new Handle(referenceKind, owner, name, descriptor, isInterface);
      case 17:
        return readConstantDynamic(constantPoolEntryIndex, charBuffer);
    } 
    throw new IllegalArgumentException();
  }
}
