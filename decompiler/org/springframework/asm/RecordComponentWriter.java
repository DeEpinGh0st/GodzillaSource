package org.springframework.asm;

















































































final class RecordComponentWriter
  extends RecordComponentVisitor
{
  private final SymbolTable symbolTable;
  private final int nameIndex;
  private final int descriptorIndex;
  private int signatureIndex;
  private AnnotationWriter lastRuntimeVisibleAnnotation;
  private AnnotationWriter lastRuntimeInvisibleAnnotation;
  private AnnotationWriter lastRuntimeVisibleTypeAnnotation;
  private AnnotationWriter lastRuntimeInvisibleTypeAnnotation;
  private Attribute firstAttribute;
  
  RecordComponentWriter(SymbolTable symbolTable, String name, String descriptor, String signature) {
    super(589824);
    this.symbolTable = symbolTable;
    this.nameIndex = symbolTable.addConstantUtf8(name);
    this.descriptorIndex = symbolTable.addConstantUtf8(descriptor);
    if (signature != null) {
      this.signatureIndex = symbolTable.addConstantUtf8(signature);
    }
  }





  
  public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
    if (visible) {
      return this
        .lastRuntimeVisibleAnnotation = AnnotationWriter.create(this.symbolTable, descriptor, this.lastRuntimeVisibleAnnotation);
    }
    return this
      .lastRuntimeInvisibleAnnotation = AnnotationWriter.create(this.symbolTable, descriptor, this.lastRuntimeInvisibleAnnotation);
  }



  
  public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
    if (visible) {
      return this
        .lastRuntimeVisibleTypeAnnotation = AnnotationWriter.create(this.symbolTable, typeRef, typePath, descriptor, this.lastRuntimeVisibleTypeAnnotation);
    }
    
    return this
      .lastRuntimeInvisibleTypeAnnotation = AnnotationWriter.create(this.symbolTable, typeRef, typePath, descriptor, this.lastRuntimeInvisibleTypeAnnotation);
  }




  
  public void visitAttribute(Attribute attribute) {
    attribute.nextAttribute = this.firstAttribute;
    this.firstAttribute = attribute;
  }








  
  public void visitEnd() {}







  
  int computeRecordComponentInfoSize() {
    int size = 6;
    size += Attribute.computeAttributesSize(this.symbolTable, 0, this.signatureIndex);
    size += 
      AnnotationWriter.computeAnnotationsSize(this.lastRuntimeVisibleAnnotation, this.lastRuntimeInvisibleAnnotation, this.lastRuntimeVisibleTypeAnnotation, this.lastRuntimeInvisibleTypeAnnotation);



    
    if (this.firstAttribute != null) {
      size += this.firstAttribute.computeAttributesSize(this.symbolTable);
    }
    return size;
  }






  
  void putRecordComponentInfo(ByteVector output) {
    output.putShort(this.nameIndex).putShort(this.descriptorIndex);

    
    int attributesCount = 0;
    if (this.signatureIndex != 0) {
      attributesCount++;
    }
    if (this.lastRuntimeVisibleAnnotation != null) {
      attributesCount++;
    }
    if (this.lastRuntimeInvisibleAnnotation != null) {
      attributesCount++;
    }
    if (this.lastRuntimeVisibleTypeAnnotation != null) {
      attributesCount++;
    }
    if (this.lastRuntimeInvisibleTypeAnnotation != null) {
      attributesCount++;
    }
    if (this.firstAttribute != null) {
      attributesCount += this.firstAttribute.getAttributeCount();
    }
    output.putShort(attributesCount);
    Attribute.putAttributes(this.symbolTable, 0, this.signatureIndex, output);
    AnnotationWriter.putAnnotations(this.symbolTable, this.lastRuntimeVisibleAnnotation, this.lastRuntimeInvisibleAnnotation, this.lastRuntimeVisibleTypeAnnotation, this.lastRuntimeInvisibleTypeAnnotation, output);





    
    if (this.firstAttribute != null) {
      this.firstAttribute.putAttributes(this.symbolTable, output);
    }
  }





  
  final void collectAttributePrototypes(Attribute.Set attributePrototypes) {
    attributePrototypes.addAttributes(this.firstAttribute);
  }
}
