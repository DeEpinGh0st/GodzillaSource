package javassist.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.AnnotationsWriter;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ByteMemberValue;
import javassist.bytecode.annotation.CharMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.DoubleMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.FloatMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.LongMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;















































































































public class AnnotationsAttribute
  extends AttributeInfo
{
  public static final String visibleTag = "RuntimeVisibleAnnotations";
  public static final String invisibleTag = "RuntimeInvisibleAnnotations";
  
  public AnnotationsAttribute(ConstPool cp, String attrname, byte[] info) {
    super(cp, attrname, info);
  }











  
  public AnnotationsAttribute(ConstPool cp, String attrname) {
    this(cp, attrname, new byte[] { 0, 0 });
  }





  
  AnnotationsAttribute(ConstPool cp, int n, DataInputStream in) throws IOException {
    super(cp, n, in);
  }



  
  public int numAnnotations() {
    return ByteArray.readU16bit(this.info, 0);
  }




  
  public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
    Copier copier = new Copier(this.info, this.constPool, newCp, classnames);
    try {
      copier.annotationArray();
      return new AnnotationsAttribute(newCp, getName(), copier.close());
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }









  
  public Annotation getAnnotation(String type) {
    Annotation[] annotations = getAnnotations();
    for (int i = 0; i < annotations.length; i++) {
      if (annotations[i].getTypeName().equals(type)) {
        return annotations[i];
      }
    } 
    return null;
  }






  
  public void addAnnotation(Annotation annotation) {
    String type = annotation.getTypeName();
    Annotation[] annotations = getAnnotations();
    for (int i = 0; i < annotations.length; i++) {
      if (annotations[i].getTypeName().equals(type)) {
        annotations[i] = annotation;
        setAnnotations(annotations);
        
        return;
      } 
    } 
    Annotation[] newlist = new Annotation[annotations.length + 1];
    System.arraycopy(annotations, 0, newlist, 0, annotations.length);
    newlist[annotations.length] = annotation;
    setAnnotations(newlist);
  }









  
  public boolean removeAnnotation(String type) {
    Annotation[] annotations = getAnnotations();
    for (int i = 0; i < annotations.length; i++) {
      if (annotations[i].getTypeName().equals(type)) {
        Annotation[] newlist = new Annotation[annotations.length - 1];
        System.arraycopy(annotations, 0, newlist, 0, i);
        if (i < annotations.length - 1) {
          System.arraycopy(annotations, i + 1, newlist, i, annotations.length - i - 1);
        }
        
        setAnnotations(newlist);
        return true;
      } 
    } 
    return false;
  }









  
  public Annotation[] getAnnotations() {
    try {
      return (new Parser(this.info, this.constPool)).parseAnnotations();
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    } 
  }







  
  public void setAnnotations(Annotation[] annotations) {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    AnnotationsWriter writer = new AnnotationsWriter(output, this.constPool);
    try {
      int n = annotations.length;
      writer.numAnnotations(n);
      for (int i = 0; i < n; i++) {
        annotations[i].write(writer);
      }
      writer.close();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    } 
    
    set(output.toByteArray());
  }







  
  public void setAnnotation(Annotation annotation) {
    setAnnotations(new Annotation[] { annotation });
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



  
  public String toString() {
    Annotation[] a = getAnnotations();
    StringBuilder sbuf = new StringBuilder();
    int i = 0;
    while (i < a.length) {
      sbuf.append(a[i++].toString());
      if (i != a.length) {
        sbuf.append(", ");
      }
    } 
    return sbuf.toString();
  }
  
  static class Walker {
    byte[] info;
    
    Walker(byte[] attrInfo) {
      this.info = attrInfo;
    }
    
    final void parameters() throws Exception {
      int numParam = this.info[0] & 0xFF;
      parameters(numParam, 1);
    }
    
    void parameters(int numParam, int pos) throws Exception {
      for (int i = 0; i < numParam; i++)
        pos = annotationArray(pos); 
    }
    
    final void annotationArray() throws Exception {
      annotationArray(0);
    }
    
    final int annotationArray(int pos) throws Exception {
      int num = ByteArray.readU16bit(this.info, pos);
      return annotationArray(pos + 2, num);
    }
    
    int annotationArray(int pos, int num) throws Exception {
      for (int i = 0; i < num; i++) {
        pos = annotation(pos);
      }
      return pos;
    }
    
    final int annotation(int pos) throws Exception {
      int type = ByteArray.readU16bit(this.info, pos);
      int numPairs = ByteArray.readU16bit(this.info, pos + 2);
      return annotation(pos + 4, type, numPairs);
    }
    
    int annotation(int pos, int type, int numPairs) throws Exception {
      for (int j = 0; j < numPairs; j++) {
        pos = memberValuePair(pos);
      }
      return pos;
    }



    
    final int memberValuePair(int pos) throws Exception {
      int nameIndex = ByteArray.readU16bit(this.info, pos);
      return memberValuePair(pos + 2, nameIndex);
    }



    
    int memberValuePair(int pos, int nameIndex) throws Exception {
      return memberValue(pos);
    }



    
    final int memberValue(int pos) throws Exception {
      int tag = this.info[pos] & 0xFF;
      if (tag == 101) {
        int typeNameIndex = ByteArray.readU16bit(this.info, pos + 1);
        int constNameIndex = ByteArray.readU16bit(this.info, pos + 3);
        enumMemberValue(pos, typeNameIndex, constNameIndex);
        return pos + 5;
      } 
      if (tag == 99) {
        int i = ByteArray.readU16bit(this.info, pos + 1);
        classMemberValue(pos, i);
        return pos + 3;
      } 
      if (tag == 64)
        return annotationMemberValue(pos + 1); 
      if (tag == 91) {
        int num = ByteArray.readU16bit(this.info, pos + 1);
        return arrayMemberValue(pos + 3, num);
      } 
      
      int index = ByteArray.readU16bit(this.info, pos + 1);
      constValueMember(tag, index);
      return pos + 3;
    }




    
    void constValueMember(int tag, int index) throws Exception {}




    
    void enumMemberValue(int pos, int typeNameIndex, int constNameIndex) throws Exception {}




    
    void classMemberValue(int pos, int index) throws Exception {}



    
    int annotationMemberValue(int pos) throws Exception {
      return annotation(pos);
    }



    
    int arrayMemberValue(int pos, int num) throws Exception {
      for (int i = 0; i < num; i++) {
        pos = memberValue(pos);
      }
      
      return pos;
    }
  }



  
  static class Renamer
    extends Walker
  {
    ConstPool cpool;

    
    Map<String, String> classnames;


    
    Renamer(byte[] info, ConstPool cp, Map<String, String> map) {
      super(info);
      this.cpool = cp;
      this.classnames = map;
    }

    
    int annotation(int pos, int type, int numPairs) throws Exception {
      renameType(pos - 4, type);
      return super.annotation(pos, type, numPairs);
    }



    
    void enumMemberValue(int pos, int typeNameIndex, int constNameIndex) throws Exception {
      renameType(pos + 1, typeNameIndex);
      super.enumMemberValue(pos, typeNameIndex, constNameIndex);
    }

    
    void classMemberValue(int pos, int index) throws Exception {
      renameType(pos + 1, index);
      super.classMemberValue(pos, index);
    }
    
    private void renameType(int pos, int index) {
      String name = this.cpool.getUtf8Info(index);
      String newName = Descriptor.rename(name, this.classnames);
      if (!name.equals(newName)) {
        int index2 = this.cpool.addUtf8Info(newName);
        ByteArray.write16bit(index2, this.info, pos);
      } 
    }
  }


  
  static class Copier
    extends Walker
  {
    ByteArrayOutputStream output;

    
    AnnotationsWriter writer;
    
    ConstPool srcPool;
    
    ConstPool destPool;
    
    Map<String, String> classnames;

    
    Copier(byte[] info, ConstPool src, ConstPool dest, Map<String, String> map) {
      this(info, src, dest, map, true);
    }
    
    Copier(byte[] info, ConstPool src, ConstPool dest, Map<String, String> map, boolean makeWriter) {
      super(info);
      this.output = new ByteArrayOutputStream();
      if (makeWriter) {
        this.writer = new AnnotationsWriter(this.output, dest);
      }
      this.srcPool = src;
      this.destPool = dest;
      this.classnames = map;
    }
    
    byte[] close() throws IOException {
      this.writer.close();
      return this.output.toByteArray();
    }

    
    void parameters(int numParam, int pos) throws Exception {
      this.writer.numParameters(numParam);
      super.parameters(numParam, pos);
    }

    
    int annotationArray(int pos, int num) throws Exception {
      this.writer.numAnnotations(num);
      return super.annotationArray(pos, num);
    }

    
    int annotation(int pos, int type, int numPairs) throws Exception {
      this.writer.annotation(copyType(type), numPairs);
      return super.annotation(pos, type, numPairs);
    }

    
    int memberValuePair(int pos, int nameIndex) throws Exception {
      this.writer.memberValuePair(copy(nameIndex));
      return super.memberValuePair(pos, nameIndex);
    }

    
    void constValueMember(int tag, int index) throws Exception {
      this.writer.constValueIndex(tag, copy(index));
      super.constValueMember(tag, index);
    }



    
    void enumMemberValue(int pos, int typeNameIndex, int constNameIndex) throws Exception {
      this.writer.enumConstValue(copyType(typeNameIndex), copy(constNameIndex));
      super.enumMemberValue(pos, typeNameIndex, constNameIndex);
    }

    
    void classMemberValue(int pos, int index) throws Exception {
      this.writer.classInfoIndex(copyType(index));
      super.classMemberValue(pos, index);
    }

    
    int annotationMemberValue(int pos) throws Exception {
      this.writer.annotationValue();
      return super.annotationMemberValue(pos);
    }

    
    int arrayMemberValue(int pos, int num) throws Exception {
      this.writer.arrayValue(num);
      return super.arrayMemberValue(pos, num);
    }









    
    int copy(int srcIndex) {
      return this.srcPool.copy(srcIndex, this.destPool, this.classnames);
    }










    
    int copyType(int srcIndex) {
      String name = this.srcPool.getUtf8Info(srcIndex);
      String newName = Descriptor.rename(name, this.classnames);
      return this.destPool.addUtf8Info(newName);
    }
  }

  
  static class Parser
    extends Walker
  {
    ConstPool pool;
    
    Annotation[][] allParams;
    
    Annotation[] allAnno;
    
    Annotation currentAnno;
    
    MemberValue currentMember;
    
    Parser(byte[] info, ConstPool cp) {
      super(info);
      this.pool = cp;
    }
    
    Annotation[][] parseParameters() throws Exception {
      parameters();
      return this.allParams;
    }
    
    Annotation[] parseAnnotations() throws Exception {
      annotationArray();
      return this.allAnno;
    }
    
    MemberValue parseMemberValue() throws Exception {
      memberValue(0);
      return this.currentMember;
    }

    
    void parameters(int numParam, int pos) throws Exception {
      Annotation[][] params = new Annotation[numParam][];
      for (int i = 0; i < numParam; i++) {
        pos = annotationArray(pos);
        params[i] = this.allAnno;
      } 
      
      this.allParams = params;
    }

    
    int annotationArray(int pos, int num) throws Exception {
      Annotation[] array = new Annotation[num];
      for (int i = 0; i < num; i++) {
        pos = annotation(pos);
        array[i] = this.currentAnno;
      } 
      
      this.allAnno = array;
      return pos;
    }

    
    int annotation(int pos, int type, int numPairs) throws Exception {
      this.currentAnno = new Annotation(type, this.pool);
      return super.annotation(pos, type, numPairs);
    }

    
    int memberValuePair(int pos, int nameIndex) throws Exception {
      pos = super.memberValuePair(pos, nameIndex);
      this.currentAnno.addMemberValue(nameIndex, this.currentMember);
      return pos; } void constValueMember(int tag, int index) throws Exception { ByteMemberValue byteMemberValue; CharMemberValue charMemberValue; DoubleMemberValue doubleMemberValue; FloatMemberValue floatMemberValue;
      IntegerMemberValue integerMemberValue;
      LongMemberValue longMemberValue;
      ShortMemberValue shortMemberValue;
      BooleanMemberValue booleanMemberValue;
      StringMemberValue stringMemberValue;
      ConstPool cp = this.pool;
      switch (tag) {
        case 66:
          byteMemberValue = new ByteMemberValue(index, cp);
          break;
        case 67:
          charMemberValue = new CharMemberValue(index, cp);
          break;
        case 68:
          doubleMemberValue = new DoubleMemberValue(index, cp);
          break;
        case 70:
          floatMemberValue = new FloatMemberValue(index, cp);
          break;
        case 73:
          integerMemberValue = new IntegerMemberValue(index, cp);
          break;
        case 74:
          longMemberValue = new LongMemberValue(index, cp);
          break;
        case 83:
          shortMemberValue = new ShortMemberValue(index, cp);
          break;
        case 90:
          booleanMemberValue = new BooleanMemberValue(index, cp);
          break;
        case 115:
          stringMemberValue = new StringMemberValue(index, cp);
          break;
        default:
          throw new RuntimeException("unknown tag:" + tag);
      } 
      
      this.currentMember = (MemberValue)stringMemberValue;
      super.constValueMember(tag, index); }




    
    void enumMemberValue(int pos, int typeNameIndex, int constNameIndex) throws Exception {
      this.currentMember = (MemberValue)new EnumMemberValue(typeNameIndex, constNameIndex, this.pool);
      
      super.enumMemberValue(pos, typeNameIndex, constNameIndex);
    }

    
    void classMemberValue(int pos, int index) throws Exception {
      this.currentMember = (MemberValue)new ClassMemberValue(index, this.pool);
      super.classMemberValue(pos, index);
    }

    
    int annotationMemberValue(int pos) throws Exception {
      Annotation anno = this.currentAnno;
      pos = super.annotationMemberValue(pos);
      this.currentMember = (MemberValue)new AnnotationMemberValue(this.currentAnno, this.pool);
      this.currentAnno = anno;
      return pos;
    }

    
    int arrayMemberValue(int pos, int num) throws Exception {
      ArrayMemberValue amv = new ArrayMemberValue(this.pool);
      MemberValue[] elements = new MemberValue[num];
      for (int i = 0; i < num; i++) {
        pos = memberValue(pos);
        elements[i] = this.currentMember;
      } 
      
      amv.setValue(elements);
      this.currentMember = (MemberValue)amv;
      return pos;
    }
  }
}
