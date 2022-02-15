package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class NameConstraints extends ASN1Object {
  private GeneralSubtree[] permitted;
  
  private GeneralSubtree[] excluded;
  
  public static NameConstraints getInstance(Object paramObject) {
    return (paramObject instanceof NameConstraints) ? (NameConstraints)paramObject : ((paramObject != null) ? new NameConstraints(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  private NameConstraints(ASN1Sequence paramASN1Sequence) {
    Enumeration enumeration = paramASN1Sequence.getObjects();
    while (enumeration.hasMoreElements()) {
      ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(enumeration.nextElement());
      switch (aSN1TaggedObject.getTagNo()) {
        case 0:
          this.permitted = createArray(ASN1Sequence.getInstance(aSN1TaggedObject, false));
          continue;
        case 1:
          this.excluded = createArray(ASN1Sequence.getInstance(aSN1TaggedObject, false));
          continue;
      } 
      throw new IllegalArgumentException("Unknown tag encountered: " + aSN1TaggedObject.getTagNo());
    } 
  }
  
  public NameConstraints(GeneralSubtree[] paramArrayOfGeneralSubtree1, GeneralSubtree[] paramArrayOfGeneralSubtree2) {
    this.permitted = cloneSubtree(paramArrayOfGeneralSubtree1);
    this.excluded = cloneSubtree(paramArrayOfGeneralSubtree2);
  }
  
  private GeneralSubtree[] createArray(ASN1Sequence paramASN1Sequence) {
    GeneralSubtree[] arrayOfGeneralSubtree = new GeneralSubtree[paramASN1Sequence.size()];
    for (byte b = 0; b != arrayOfGeneralSubtree.length; b++)
      arrayOfGeneralSubtree[b] = GeneralSubtree.getInstance(paramASN1Sequence.getObjectAt(b)); 
    return arrayOfGeneralSubtree;
  }
  
  public GeneralSubtree[] getPermittedSubtrees() {
    return cloneSubtree(this.permitted);
  }
  
  public GeneralSubtree[] getExcludedSubtrees() {
    return cloneSubtree(this.excluded);
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.permitted != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)new DERSequence((ASN1Encodable[])this.permitted))); 
    if (this.excluded != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 1, (ASN1Encodable)new DERSequence((ASN1Encodable[])this.excluded))); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  private static GeneralSubtree[] cloneSubtree(GeneralSubtree[] paramArrayOfGeneralSubtree) {
    if (paramArrayOfGeneralSubtree != null) {
      GeneralSubtree[] arrayOfGeneralSubtree = new GeneralSubtree[paramArrayOfGeneralSubtree.length];
      System.arraycopy(paramArrayOfGeneralSubtree, 0, arrayOfGeneralSubtree, 0, arrayOfGeneralSubtree.length);
      return arrayOfGeneralSubtree;
    } 
    return null;
  }
}
