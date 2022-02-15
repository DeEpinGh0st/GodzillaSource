package javassist.bytecode;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javassist.bytecode.annotation.AnnotationsWriter;
import javassist.bytecode.annotation.TypeAnnotationsWriter;























public class TypeAnnotationsAttribute
  extends AttributeInfo
{
  public static final String visibleTag = "RuntimeVisibleTypeAnnotations";
  public static final String invisibleTag = "RuntimeInvisibleTypeAnnotations";
  
  public TypeAnnotationsAttribute(ConstPool cp, String attrname, byte[] info) {
    super(cp, attrname, info);
  }





  
  TypeAnnotationsAttribute(ConstPool cp, int n, DataInputStream in) throws IOException {
    super(cp, n, in);
  }



  
  public int numAnnotations() {
    return ByteArray.readU16bit(this.info, 0);
  }




  
  public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
    Copier copier = new Copier(this.info, this.constPool, newCp, classnames);
    try {
      copier.annotationArray();
      return new TypeAnnotationsAttribute(newCp, getName(), copier.close());
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }





  
  void renameClass(String oldname, String newname) {
    Map<String, String> map = new HashMap<>();
    map.put(oldname, newname);
    renameClass(map);
  }

  
  void renameClass(Map<String, String> classnames) {
    Renamer renamer = new Renamer(this.info, getConstPool(), classnames);
    try {
      renamer.annotationArray();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }
  
  void getRefClasses(Map<String, String> classnames) {
    renameClass(classnames);
  }


  
  static class TAWalker
    extends AnnotationsAttribute.Walker
  {
    TypeAnnotationsAttribute.SubWalker subWalker;

    
    TAWalker(byte[] attrInfo) {
      super(attrInfo);
      this.subWalker = new TypeAnnotationsAttribute.SubWalker(attrInfo);
    }

    
    int annotationArray(int pos, int num) throws Exception {
      for (int i = 0; i < num; i++) {
        int targetType = this.info[pos] & 0xFF;
        pos = this.subWalker.targetInfo(pos + 1, targetType);
        pos = this.subWalker.typePath(pos);
        pos = annotation(pos);
      } 
      
      return pos;
    }
  }
  
  static class SubWalker {
    byte[] info;
    
    SubWalker(byte[] attrInfo) {
      this.info = attrInfo; } final int targetInfo(int pos, int type) throws Exception { int j; int param; int i; int len; int index;
      int offset;
      int bound;
      int k;
      switch (type) {
        case 0:
        case 1:
          j = this.info[pos] & 0xFF;
          typeParameterTarget(pos, type, j);
          return pos + 1;
        case 16:
          j = ByteArray.readU16bit(this.info, pos);
          supertypeTarget(pos, j);
          return pos + 2;
        case 17:
        case 18:
          param = this.info[pos] & 0xFF;
          bound = this.info[pos + 1] & 0xFF;
          typeParameterBoundTarget(pos, type, param, bound);
          return pos + 2;
        case 19:
        case 20:
        case 21:
          emptyTarget(pos, type);
          return pos;
        case 22:
          i = this.info[pos] & 0xFF;
          formalParameterTarget(pos, i);
          return pos + 1;
        case 23:
          i = ByteArray.readU16bit(this.info, pos);
          throwsTarget(pos, i);
          return pos + 2;
        case 64:
        case 65:
          len = ByteArray.readU16bit(this.info, pos);
          return localvarTarget(pos + 2, type, len);
        case 66:
          index = ByteArray.readU16bit(this.info, pos);
          catchTarget(pos, index);
          return pos + 2;
        case 67:
        case 68:
        case 69:
        case 70:
          offset = ByteArray.readU16bit(this.info, pos);
          offsetTarget(pos, type, offset);
          return pos + 2;
        case 71:
        case 72:
        case 73:
        case 74:
        case 75:
          offset = ByteArray.readU16bit(this.info, pos);
          k = this.info[pos + 2] & 0xFF;
          typeArgumentTarget(pos, type, offset, k);
          return pos + 3;
      } 
      throw new RuntimeException("invalid target type: " + type); }


    
    void typeParameterTarget(int pos, int targetType, int typeParameterIndex) throws Exception {}

    
    void supertypeTarget(int pos, int superTypeIndex) throws Exception {}

    
    void typeParameterBoundTarget(int pos, int targetType, int typeParameterIndex, int boundIndex) throws Exception {}
    
    void emptyTarget(int pos, int targetType) throws Exception {}
    
    void formalParameterTarget(int pos, int formalParameterIndex) throws Exception {}
    
    void throwsTarget(int pos, int throwsTypeIndex) throws Exception {}
    
    int localvarTarget(int pos, int targetType, int tableLength) throws Exception {
      for (int i = 0; i < tableLength; i++) {
        int start = ByteArray.readU16bit(this.info, pos);
        int length = ByteArray.readU16bit(this.info, pos + 2);
        int index = ByteArray.readU16bit(this.info, pos + 4);
        localvarTarget(pos, targetType, start, length, index);
        pos += 6;
      } 
      
      return pos;
    }

    
    void localvarTarget(int pos, int targetType, int startPc, int length, int index) throws Exception {}

    
    void catchTarget(int pos, int exceptionTableIndex) throws Exception {}
    
    void offsetTarget(int pos, int targetType, int offset) throws Exception {}
    
    void typeArgumentTarget(int pos, int targetType, int offset, int typeArgumentIndex) throws Exception {}
    
    final int typePath(int pos) throws Exception {
      int len = this.info[pos++] & 0xFF;
      return typePath(pos, len);
    }
    
    int typePath(int pos, int pathLength) throws Exception {
      for (int i = 0; i < pathLength; i++) {
        int kind = this.info[pos] & 0xFF;
        int index = this.info[pos + 1] & 0xFF;
        typePath(pos, kind, index);
        pos += 2;
      } 
      
      return pos;
    }
    
    void typePath(int pos, int typePathKind, int typeArgumentIndex) throws Exception {}
  }
  
  static class Renamer extends AnnotationsAttribute.Renamer {
    TypeAnnotationsAttribute.SubWalker sub;
    
    Renamer(byte[] attrInfo, ConstPool cp, Map<String, String> map) {
      super(attrInfo, cp, map);
      this.sub = new TypeAnnotationsAttribute.SubWalker(attrInfo);
    }

    
    int annotationArray(int pos, int num) throws Exception {
      for (int i = 0; i < num; i++) {
        int targetType = this.info[pos] & 0xFF;
        pos = this.sub.targetInfo(pos + 1, targetType);
        pos = this.sub.typePath(pos);
        pos = annotation(pos);
      } 
      
      return pos;
    }
  }
  
  static class Copier extends AnnotationsAttribute.Copier {
    TypeAnnotationsAttribute.SubCopier sub;
    
    Copier(byte[] attrInfo, ConstPool src, ConstPool dest, Map<String, String> map) {
      super(attrInfo, src, dest, map, false);
      TypeAnnotationsWriter w = new TypeAnnotationsWriter(this.output, dest);
      this.writer = (AnnotationsWriter)w;
      this.sub = new TypeAnnotationsAttribute.SubCopier(attrInfo, src, dest, map, w);
    }

    
    int annotationArray(int pos, int num) throws Exception {
      this.writer.numAnnotations(num);
      for (int i = 0; i < num; i++) {
        int targetType = this.info[pos] & 0xFF;
        pos = this.sub.targetInfo(pos + 1, targetType);
        pos = this.sub.typePath(pos);
        pos = annotation(pos);
      } 
      
      return pos;
    }
  }
  
  static class SubCopier
    extends SubWalker {
    ConstPool srcPool;
    ConstPool destPool;
    Map<String, String> classnames;
    TypeAnnotationsWriter writer;
    
    SubCopier(byte[] attrInfo, ConstPool src, ConstPool dest, Map<String, String> map, TypeAnnotationsWriter w) {
      super(attrInfo);
      this.srcPool = src;
      this.destPool = dest;
      this.classnames = map;
      this.writer = w;
    }



    
    void typeParameterTarget(int pos, int targetType, int typeParameterIndex) throws Exception {
      this.writer.typeParameterTarget(targetType, typeParameterIndex);
    }

    
    void supertypeTarget(int pos, int superTypeIndex) throws Exception {
      this.writer.supertypeTarget(superTypeIndex);
    }




    
    void typeParameterBoundTarget(int pos, int targetType, int typeParameterIndex, int boundIndex) throws Exception {
      this.writer.typeParameterBoundTarget(targetType, typeParameterIndex, boundIndex);
    }

    
    void emptyTarget(int pos, int targetType) throws Exception {
      this.writer.emptyTarget(targetType);
    }

    
    void formalParameterTarget(int pos, int formalParameterIndex) throws Exception {
      this.writer.formalParameterTarget(formalParameterIndex);
    }

    
    void throwsTarget(int pos, int throwsTypeIndex) throws Exception {
      this.writer.throwsTarget(throwsTypeIndex);
    }

    
    int localvarTarget(int pos, int targetType, int tableLength) throws Exception {
      this.writer.localVarTarget(targetType, tableLength);
      return super.localvarTarget(pos, targetType, tableLength);
    }



    
    void localvarTarget(int pos, int targetType, int startPc, int length, int index) throws Exception {
      this.writer.localVarTargetTable(startPc, length, index);
    }

    
    void catchTarget(int pos, int exceptionTableIndex) throws Exception {
      this.writer.catchTarget(exceptionTableIndex);
    }

    
    void offsetTarget(int pos, int targetType, int offset) throws Exception {
      this.writer.offsetTarget(targetType, offset);
    }



    
    void typeArgumentTarget(int pos, int targetType, int offset, int typeArgumentIndex) throws Exception {
      this.writer.typeArgumentTarget(targetType, offset, typeArgumentIndex);
    }

    
    int typePath(int pos, int pathLength) throws Exception {
      this.writer.typePath(pathLength);
      return super.typePath(pos, pathLength);
    }

    
    void typePath(int pos, int typePathKind, int typeArgumentIndex) throws Exception {
      this.writer.typePathPath(typePathKind, typeArgumentIndex);
    }
  }
}
