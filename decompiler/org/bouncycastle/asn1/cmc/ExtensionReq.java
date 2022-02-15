package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.Extension;

public class ExtensionReq extends ASN1Object {
  private final Extension[] extensions;
  
  public static ExtensionReq getInstance(Object paramObject) {
    return (paramObject instanceof ExtensionReq) ? (ExtensionReq)paramObject : ((paramObject != null) ? new ExtensionReq(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public static ExtensionReq getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public ExtensionReq(Extension paramExtension) {
    this.extensions = new Extension[] { paramExtension };
  }
  
  public ExtensionReq(Extension[] paramArrayOfExtension) {
    this.extensions = Utils.clone(paramArrayOfExtension);
  }
  
  private ExtensionReq(ASN1Sequence paramASN1Sequence) {
    this.extensions = new Extension[paramASN1Sequence.size()];
    for (byte b = 0; b != paramASN1Sequence.size(); b++)
      this.extensions[b] = Extension.getInstance(paramASN1Sequence.getObjectAt(b)); 
  }
  
  public Extension[] getExtensions() {
    return Utils.clone(this.extensions);
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DERSequence((ASN1Encodable[])this.extensions);
  }
}
