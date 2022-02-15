package org.bouncycastle.asn1.est;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class CsrAttrs extends ASN1Object {
  private final AttrOrOID[] attrOrOIDs;
  
  public static CsrAttrs getInstance(Object paramObject) {
    return (paramObject instanceof CsrAttrs) ? (CsrAttrs)paramObject : ((paramObject != null) ? new CsrAttrs(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public static CsrAttrs getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public CsrAttrs(AttrOrOID paramAttrOrOID) {
    this.attrOrOIDs = new AttrOrOID[] { paramAttrOrOID };
  }
  
  public CsrAttrs(AttrOrOID[] paramArrayOfAttrOrOID) {
    this.attrOrOIDs = Utils.clone(paramArrayOfAttrOrOID);
  }
  
  private CsrAttrs(ASN1Sequence paramASN1Sequence) {
    this.attrOrOIDs = new AttrOrOID[paramASN1Sequence.size()];
    for (byte b = 0; b != paramASN1Sequence.size(); b++)
      this.attrOrOIDs[b] = AttrOrOID.getInstance(paramASN1Sequence.getObjectAt(b)); 
  }
  
  public AttrOrOID[] getAttrOrOIDs() {
    return Utils.clone(this.attrOrOIDs);
  }
  
  public int size() {
    return this.attrOrOIDs.length;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DERSequence((ASN1Encodable[])this.attrOrOIDs);
  }
}
