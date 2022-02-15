package org.bouncycastle.asn1;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Iterable;

public abstract class ASN1Sequence extends ASN1Primitive implements Iterable<ASN1Encodable> {
  protected Vector seq = new Vector();
  
  public static ASN1Sequence getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof ASN1Sequence)
      return (ASN1Sequence)paramObject; 
    if (paramObject instanceof ASN1SequenceParser)
      return getInstance(((ASN1SequenceParser)paramObject).toASN1Primitive()); 
    if (paramObject instanceof byte[])
      try {
        return getInstance(fromByteArray((byte[])paramObject));
      } catch (IOException iOException) {
        throw new IllegalArgumentException("failed to construct sequence from byte[]: " + iOException.getMessage());
      }  
    if (paramObject instanceof ASN1Encodable) {
      ASN1Primitive aSN1Primitive = ((ASN1Encodable)paramObject).toASN1Primitive();
      if (aSN1Primitive instanceof ASN1Sequence)
        return (ASN1Sequence)aSN1Primitive; 
    } 
    throw new IllegalArgumentException("unknown object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static ASN1Sequence getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    if (paramBoolean) {
      if (!paramASN1TaggedObject.isExplicit())
        throw new IllegalArgumentException("object implicit - explicit expected."); 
      return getInstance(paramASN1TaggedObject.getObject().toASN1Primitive());
    } 
    if (paramASN1TaggedObject.isExplicit())
      return (ASN1Sequence)((paramASN1TaggedObject instanceof BERTaggedObject) ? new BERSequence(paramASN1TaggedObject.getObject()) : new DLSequence(paramASN1TaggedObject.getObject())); 
    if (paramASN1TaggedObject.getObject() instanceof ASN1Sequence)
      return (ASN1Sequence)paramASN1TaggedObject.getObject(); 
    throw new IllegalArgumentException("unknown object in getInstance: " + paramASN1TaggedObject.getClass().getName());
  }
  
  protected ASN1Sequence() {}
  
  protected ASN1Sequence(ASN1Encodable paramASN1Encodable) {
    this.seq.addElement(paramASN1Encodable);
  }
  
  protected ASN1Sequence(ASN1EncodableVector paramASN1EncodableVector) {
    for (byte b = 0; b != paramASN1EncodableVector.size(); b++)
      this.seq.addElement(paramASN1EncodableVector.get(b)); 
  }
  
  protected ASN1Sequence(ASN1Encodable[] paramArrayOfASN1Encodable) {
    for (byte b = 0; b != paramArrayOfASN1Encodable.length; b++)
      this.seq.addElement(paramArrayOfASN1Encodable[b]); 
  }
  
  public ASN1Encodable[] toArray() {
    ASN1Encodable[] arrayOfASN1Encodable = new ASN1Encodable[size()];
    for (byte b = 0; b != size(); b++)
      arrayOfASN1Encodable[b] = getObjectAt(b); 
    return arrayOfASN1Encodable;
  }
  
  public Enumeration getObjects() {
    return this.seq.elements();
  }
  
  public ASN1SequenceParser parser() {
    final ASN1Sequence outer = this;
    return new ASN1SequenceParser() {
        private final int max = ASN1Sequence.this.size();
        
        private int index;
        
        public ASN1Encodable readObject() throws IOException {
          if (this.index == this.max)
            return null; 
          ASN1Encodable aSN1Encodable = ASN1Sequence.this.getObjectAt(this.index++);
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
  
  public ASN1Encodable getObjectAt(int paramInt) {
    return this.seq.elementAt(paramInt);
  }
  
  public int size() {
    return this.seq.size();
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
  
  boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    if (!(paramASN1Primitive instanceof ASN1Sequence))
      return false; 
    ASN1Sequence aSN1Sequence = (ASN1Sequence)paramASN1Primitive;
    if (size() != aSN1Sequence.size())
      return false; 
    Enumeration enumeration1 = getObjects();
    Enumeration enumeration2 = aSN1Sequence.getObjects();
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
    return paramEnumeration.nextElement();
  }
  
  ASN1Primitive toDERObject() {
    DERSequence dERSequence = new DERSequence();
    dERSequence.seq = this.seq;
    return dERSequence;
  }
  
  ASN1Primitive toDLObject() {
    DLSequence dLSequence = new DLSequence();
    dLSequence.seq = this.seq;
    return dLSequence;
  }
  
  boolean isConstructed() {
    return true;
  }
  
  abstract void encode(ASN1OutputStream paramASN1OutputStream) throws IOException;
  
  public String toString() {
    return this.seq.toString();
  }
  
  public Iterator<ASN1Encodable> iterator() {
    return (Iterator<ASN1Encodable>)new Arrays.Iterator((Object[])toArray());
  }
}
