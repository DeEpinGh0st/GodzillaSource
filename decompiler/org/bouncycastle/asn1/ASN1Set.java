package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Iterable;

public abstract class ASN1Set extends ASN1Primitive implements Iterable<ASN1Encodable> {
  private Vector set = new Vector();
  
  private boolean isSorted = false;
  
  public static ASN1Set getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof ASN1Set)
      return (ASN1Set)paramObject; 
    if (paramObject instanceof ASN1SetParser)
      return getInstance(((ASN1SetParser)paramObject).toASN1Primitive()); 
    if (paramObject instanceof byte[])
      try {
        return getInstance(ASN1Primitive.fromByteArray((byte[])paramObject));
      } catch (IOException iOException) {
        throw new IllegalArgumentException("failed to construct set from byte[]: " + iOException.getMessage());
      }  
    if (paramObject instanceof ASN1Encodable) {
      ASN1Primitive aSN1Primitive = ((ASN1Encodable)paramObject).toASN1Primitive();
      if (aSN1Primitive instanceof ASN1Set)
        return (ASN1Set)aSN1Primitive; 
    } 
    throw new IllegalArgumentException("unknown object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static ASN1Set getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    if (paramBoolean) {
      if (!paramASN1TaggedObject.isExplicit())
        throw new IllegalArgumentException("object implicit - explicit expected."); 
      return (ASN1Set)paramASN1TaggedObject.getObject();
    } 
    if (paramASN1TaggedObject.isExplicit())
      return (ASN1Set)((paramASN1TaggedObject instanceof BERTaggedObject) ? new BERSet(paramASN1TaggedObject.getObject()) : new DLSet(paramASN1TaggedObject.getObject())); 
    if (paramASN1TaggedObject.getObject() instanceof ASN1Set)
      return (ASN1Set)paramASN1TaggedObject.getObject(); 
    if (paramASN1TaggedObject.getObject() instanceof ASN1Sequence) {
      ASN1Sequence aSN1Sequence = (ASN1Sequence)paramASN1TaggedObject.getObject();
      return (ASN1Set)((paramASN1TaggedObject instanceof BERTaggedObject) ? new BERSet(aSN1Sequence.toArray()) : new DLSet(aSN1Sequence.toArray()));
    } 
    throw new IllegalArgumentException("unknown object in getInstance: " + paramASN1TaggedObject.getClass().getName());
  }
  
  protected ASN1Set() {}
  
  protected ASN1Set(ASN1Encodable paramASN1Encodable) {
    this.set.addElement(paramASN1Encodable);
  }
  
  protected ASN1Set(ASN1EncodableVector paramASN1EncodableVector, boolean paramBoolean) {
    for (byte b = 0; b != paramASN1EncodableVector.size(); b++)
      this.set.addElement(paramASN1EncodableVector.get(b)); 
    if (paramBoolean)
      sort(); 
  }
  
  protected ASN1Set(ASN1Encodable[] paramArrayOfASN1Encodable, boolean paramBoolean) {
    for (byte b = 0; b != paramArrayOfASN1Encodable.length; b++)
      this.set.addElement(paramArrayOfASN1Encodable[b]); 
    if (paramBoolean)
      sort(); 
  }
  
  public Enumeration getObjects() {
    return this.set.elements();
  }
  
  public ASN1Encodable getObjectAt(int paramInt) {
    return this.set.elementAt(paramInt);
  }
  
  public int size() {
    return this.set.size();
  }
  
  public ASN1Encodable[] toArray() {
    ASN1Encodable[] arrayOfASN1Encodable = new ASN1Encodable[size()];
    for (byte b = 0; b != size(); b++)
      arrayOfASN1Encodable[b] = getObjectAt(b); 
    return arrayOfASN1Encodable;
  }
  
  public ASN1SetParser parser() {
    final ASN1Set outer = this;
    return new ASN1SetParser() {
        private final int max = ASN1Set.this.size();
        
        private int index;
        
        public ASN1Encodable readObject() throws IOException {
          if (this.index == this.max)
            return null; 
          ASN1Encodable aSN1Encodable = ASN1Set.this.getObjectAt(this.index++);
          return (aSN1Encodable instanceof ASN1Sequence) ? ((ASN1Sequence)aSN1Encodable).parser() : ((aSN1Encodable instanceof ASN1Set) ? ((ASN1Set)aSN1Encodable).parser() : aSN1Encodable);
        }
        
        public ASN1Primitive getLoadedObject() {
          return outer;
        }
        
        public ASN1Primitive toASN1Primitive() {
          return outer;
        }
      };
  }
  
  public int hashCode() {
    Enumeration enumeration = getObjects();
    int i;
    for (i = size(); enumeration.hasMoreElements(); i ^= aSN1Encodable.hashCode()) {
      ASN1Encodable aSN1Encodable = getNext(enumeration);
      i *= 17;
    } 
    return i;
  }
  
  ASN1Primitive toDERObject() {
    if (this.isSorted) {
      DERSet dERSet1 = new DERSet();
      dERSet1.set = this.set;
      return dERSet1;
    } 
    Vector vector = new Vector();
    for (byte b = 0; b != this.set.size(); b++)
      vector.addElement(this.set.elementAt(b)); 
    DERSet dERSet = new DERSet();
    dERSet.set = vector;
    dERSet.sort();
    return dERSet;
  }
  
  ASN1Primitive toDLObject() {
    DLSet dLSet = new DLSet();
    dLSet.set = this.set;
    return dLSet;
  }
  
  boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    if (!(paramASN1Primitive instanceof ASN1Set))
      return false; 
    ASN1Set aSN1Set = (ASN1Set)paramASN1Primitive;
    if (size() != aSN1Set.size())
      return false; 
    Enumeration enumeration1 = getObjects();
    Enumeration enumeration2 = aSN1Set.getObjects();
    while (enumeration1.hasMoreElements()) {
      ASN1Encodable aSN1Encodable1 = getNext(enumeration1);
      ASN1Encodable aSN1Encodable2 = getNext(enumeration2);
      ASN1Primitive aSN1Primitive1 = aSN1Encodable1.toASN1Primitive();
      ASN1Primitive aSN1Primitive2 = aSN1Encodable2.toASN1Primitive();
      if (aSN1Primitive1 == aSN1Primitive2 || aSN1Primitive1.equals(aSN1Primitive2))
        continue; 
      return false;
    } 
    return true;
  }
  
  private ASN1Encodable getNext(Enumeration<ASN1Encodable> paramEnumeration) {
    ASN1Encodable aSN1Encodable = paramEnumeration.nextElement();
    return (aSN1Encodable == null) ? DERNull.INSTANCE : aSN1Encodable;
  }
  
  private boolean lessThanOrEqual(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    int i = Math.min(paramArrayOfbyte1.length, paramArrayOfbyte2.length);
    for (int j = 0; j != i; j++) {
      if (paramArrayOfbyte1[j] != paramArrayOfbyte2[j])
        return ((paramArrayOfbyte1[j] & 0xFF) < (paramArrayOfbyte2[j] & 0xFF)); 
    } 
    return (i == paramArrayOfbyte1.length);
  }
  
  private byte[] getDEREncoded(ASN1Encodable paramASN1Encodable) {
    try {
      return paramASN1Encodable.toASN1Primitive().getEncoded("DER");
    } catch (IOException iOException) {
      throw new IllegalArgumentException("cannot encode object added to SET");
    } 
  }
  
  protected void sort() {
    if (!this.isSorted) {
      this.isSorted = true;
      if (this.set.size() > 1) {
        boolean bool = true;
        for (int i = this.set.size() - 1; bool; i = k) {
          int j = 0;
          int k = 0;
          byte[] arrayOfByte = getDEREncoded(this.set.elementAt(0));
          bool = false;
          while (j != i) {
            byte[] arrayOfByte1 = getDEREncoded(this.set.elementAt(j + 1));
            if (lessThanOrEqual(arrayOfByte, arrayOfByte1)) {
              arrayOfByte = arrayOfByte1;
            } else {
              Object object = this.set.elementAt(j);
              this.set.setElementAt(this.set.elementAt(j + 1), j);
              this.set.setElementAt(object, j + 1);
              bool = true;
              k = j;
            } 
            j++;
          } 
        } 
      } 
    } 
  }
  
  boolean isConstructed() {
    return true;
  }
  
  abstract void encode(ASN1OutputStream paramASN1OutputStream) throws IOException;
  
  public String toString() {
    return this.set.toString();
  }
  
  public Iterator<ASN1Encodable> iterator() {
    return (Iterator<ASN1Encodable>)new Arrays.Iterator((Object[])toArray());
  }
}
