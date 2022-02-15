package org.bouncycastle.asn1.bc;

import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Iterable;

public class ObjectDataSequence extends ASN1Object implements Iterable<ASN1Encodable> {
  private final ASN1Encodable[] dataSequence;
  
  public ObjectDataSequence(ObjectData[] paramArrayOfObjectData) {
    this.dataSequence = new ASN1Encodable[paramArrayOfObjectData.length];
    System.arraycopy(paramArrayOfObjectData, 0, this.dataSequence, 0, paramArrayOfObjectData.length);
  }
  
  private ObjectDataSequence(ASN1Sequence paramASN1Sequence) {
    this.dataSequence = new ASN1Encodable[paramASN1Sequence.size()];
    for (byte b = 0; b != this.dataSequence.length; b++)
      this.dataSequence[b] = (ASN1Encodable)ObjectData.getInstance(paramASN1Sequence.getObjectAt(b)); 
  }
  
  public static ObjectDataSequence getInstance(Object paramObject) {
    return (paramObject instanceof ObjectDataSequence) ? (ObjectDataSequence)paramObject : ((paramObject != null) ? new ObjectDataSequence(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DERSequence(this.dataSequence);
  }
  
  public Iterator<ASN1Encodable> iterator() {
    return (Iterator<ASN1Encodable>)new Arrays.Iterator((Object[])this.dataSequence);
  }
}
