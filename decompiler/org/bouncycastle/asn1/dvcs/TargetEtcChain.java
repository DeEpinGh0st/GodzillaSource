package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class TargetEtcChain extends ASN1Object {
  private CertEtcToken target;
  
  private ASN1Sequence chain;
  
  private PathProcInput pathProcInput;
  
  public TargetEtcChain(CertEtcToken paramCertEtcToken) {
    this(paramCertEtcToken, null, null);
  }
  
  public TargetEtcChain(CertEtcToken paramCertEtcToken, CertEtcToken[] paramArrayOfCertEtcToken) {
    this(paramCertEtcToken, paramArrayOfCertEtcToken, null);
  }
  
  public TargetEtcChain(CertEtcToken paramCertEtcToken, PathProcInput paramPathProcInput) {
    this(paramCertEtcToken, null, paramPathProcInput);
  }
  
  public TargetEtcChain(CertEtcToken paramCertEtcToken, CertEtcToken[] paramArrayOfCertEtcToken, PathProcInput paramPathProcInput) {
    this.target = paramCertEtcToken;
    if (paramArrayOfCertEtcToken != null)
      this.chain = (ASN1Sequence)new DERSequence((ASN1Encodable[])paramArrayOfCertEtcToken); 
    this.pathProcInput = paramPathProcInput;
  }
  
  private TargetEtcChain(ASN1Sequence paramASN1Sequence) {
    byte b = 0;
    ASN1Encodable aSN1Encodable = paramASN1Sequence.getObjectAt(b++);
    this.target = CertEtcToken.getInstance(aSN1Encodable);
    if (paramASN1Sequence.size() > 1) {
      aSN1Encodable = paramASN1Sequence.getObjectAt(b++);
      if (aSN1Encodable instanceof ASN1TaggedObject) {
        extractPathProcInput(aSN1Encodable);
      } else {
        this.chain = ASN1Sequence.getInstance(aSN1Encodable);
        if (paramASN1Sequence.size() > 2) {
          aSN1Encodable = paramASN1Sequence.getObjectAt(b);
          extractPathProcInput(aSN1Encodable);
        } 
      } 
    } 
  }
  
  private void extractPathProcInput(ASN1Encodable paramASN1Encodable) {
    ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(paramASN1Encodable);
    switch (aSN1TaggedObject.getTagNo()) {
      case 0:
        this.pathProcInput = PathProcInput.getInstance(aSN1TaggedObject, false);
        return;
    } 
    throw new IllegalArgumentException("Unknown tag encountered: " + aSN1TaggedObject.getTagNo());
  }
  
  public static TargetEtcChain getInstance(Object paramObject) {
    return (paramObject instanceof TargetEtcChain) ? (TargetEtcChain)paramObject : ((paramObject != null) ? new TargetEtcChain(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public static TargetEtcChain getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.target);
    if (this.chain != null)
      aSN1EncodableVector.add((ASN1Encodable)this.chain); 
    if (this.pathProcInput != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.pathProcInput)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("TargetEtcChain {\n");
    stringBuffer.append("target: " + this.target + "\n");
    if (this.chain != null)
      stringBuffer.append("chain: " + this.chain + "\n"); 
    if (this.pathProcInput != null)
      stringBuffer.append("pathProcInput: " + this.pathProcInput + "\n"); 
    stringBuffer.append("}\n");
    return stringBuffer.toString();
  }
  
  public CertEtcToken getTarget() {
    return this.target;
  }
  
  public CertEtcToken[] getChain() {
    return (this.chain != null) ? CertEtcToken.arrayFromSequence(this.chain) : null;
  }
  
  public PathProcInput getPathProcInput() {
    return this.pathProcInput;
  }
  
  public static TargetEtcChain[] arrayFromSequence(ASN1Sequence paramASN1Sequence) {
    TargetEtcChain[] arrayOfTargetEtcChain = new TargetEtcChain[paramASN1Sequence.size()];
    for (byte b = 0; b != arrayOfTargetEtcChain.length; b++)
      arrayOfTargetEtcChain[b] = getInstance(paramASN1Sequence.getObjectAt(b)); 
    return arrayOfTargetEtcChain;
  }
}
