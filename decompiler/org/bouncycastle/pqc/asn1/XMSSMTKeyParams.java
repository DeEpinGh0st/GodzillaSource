package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class XMSSMTKeyParams extends ASN1Object {
  private final ASN1Integer version = new ASN1Integer(0L);
  
  private final int height;
  
  private final int layers;
  
  private final AlgorithmIdentifier treeDigest;
  
  public XMSSMTKeyParams(int paramInt1, int paramInt2, AlgorithmIdentifier paramAlgorithmIdentifier) {
    this.height = paramInt1;
    this.layers = paramInt2;
    this.treeDigest = paramAlgorithmIdentifier;
  }
  
  private XMSSMTKeyParams(ASN1Sequence paramASN1Sequence) {
    this.height = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(1)).getValue().intValue();
    this.layers = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(2)).getValue().intValue();
    this.treeDigest = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(3));
  }
  
  public static XMSSMTKeyParams getInstance(Object paramObject) {
    return (paramObject instanceof XMSSMTKeyParams) ? (XMSSMTKeyParams)paramObject : ((paramObject != null) ? new XMSSMTKeyParams(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public int getHeight() {
    return this.height;
  }
  
  public int getLayers() {
    return this.layers;
  }
  
  public AlgorithmIdentifier getTreeDigest() {
    return this.treeDigest;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.version);
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(this.height));
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(this.layers));
    aSN1EncodableVector.add((ASN1Encodable)this.treeDigest);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
