package org.fife.rsta.ac.java.rjc.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




















public class Modifiers
{
  public static final Integer ABSTRACT = Integer.valueOf(1024);
  public static final Integer FINAL = Integer.valueOf(16);
  public static final Integer INTERFACE = Integer.valueOf(512);
  public static final Integer NATIVE = Integer.valueOf(256);
  public static final Integer PRIVATE = Integer.valueOf(2);
  public static final Integer PROTECTED = Integer.valueOf(4);
  public static final Integer PUBLIC = Integer.valueOf(1);
  public static final Integer STATIC = Integer.valueOf(8);
  public static final Integer STRICTFP = Integer.valueOf(2048);
  public static final Integer SYNCHRONIZED = Integer.valueOf(32);
  public static final Integer TRANSIENT = Integer.valueOf(128);
  public static final Integer VOLATILE = Integer.valueOf(64);




  
  private static final Map<Integer, String> MODIFIER_TEXT = new HashMap<Integer, String>()
    {
      private static final long serialVersionUID = 1L;
    };


















  
  private List<Integer> modifiers = new ArrayList<>(1);
  private List<Annotation> annotations = new ArrayList<>(0);


  
  public void addAnnotation(Annotation annotation) {
    this.annotations.add(annotation);
  }



  
  public boolean addModifier(int tokenType) {
    Integer key;
    switch (tokenType) {
      case 65537:
        key = ABSTRACT;
        break;
      case 65554:
        key = FINAL;
        break;
      case 65564:
        key = INTERFACE;
        break;
      case 65566:
        key = NATIVE;
        break;
      case 65569:
        key = PRIVATE;
        break;
      case 65570:
        key = PROTECTED;
        break;
      case 65571:
        key = PUBLIC;
        break;
      case 65574:
        key = STATIC;
        break;
      case 65575:
        key = STRICTFP;
        break;
      case 65578:
        key = SYNCHRONIZED;
        break;
      case 65582:
        key = TRANSIENT;
        break;
      case 65585:
        key = VOLATILE;
        break;
      default:
        throw new IllegalArgumentException("Invalid tokenType: " + tokenType);
    } 

    
    int pos = Collections.binarySearch((List)this.modifiers, key);
    if (pos < 0) {
      
      int insertionPoint = -(pos + 1);
      this.modifiers.add(insertionPoint, key);
    } 
    
    return (pos < 0);
  }


  
  private boolean containsModifier(Integer modifierKey) {
    return (Collections.binarySearch((List)this.modifiers, modifierKey) >= 0);
  }

  
  public boolean isAbstract() {
    return containsModifier(ABSTRACT);
  }

  
  public boolean isFinal() {
    return containsModifier(FINAL);
  }

  
  public boolean isPrivate() {
    return containsModifier(PRIVATE);
  }

  
  public boolean isProtected() {
    return containsModifier(PROTECTED);
  }

  
  public boolean isPublic() {
    return containsModifier(PUBLIC);
  }

  
  public boolean isStatic() {
    return containsModifier(STATIC);
  }


  
  public String toString() {
    StringBuilder sb = new StringBuilder(); int i;
    for (i = 0; i < this.annotations.size(); i++) {
      sb.append(((Annotation)this.annotations.get(i)).toString());
      if (i < this.annotations.size() - 1 || this.modifiers.size() > 0) {
        sb.append(' ');
      }
    } 
    for (i = 0; i < this.modifiers.size(); i++) {
      Integer modifier = this.modifiers.get(i);
      sb.append(MODIFIER_TEXT.get(modifier));
      if (i < this.modifiers.size() - 1) {
        sb.append(' ');
      }
    } 
    return sb.toString();
  }
}
