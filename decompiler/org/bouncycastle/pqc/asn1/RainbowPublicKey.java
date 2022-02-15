package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.pqc.crypto.rainbow.util.RainbowUtil;

public class RainbowPublicKey extends ASN1Object {
  private ASN1Integer version;
  
  private ASN1ObjectIdentifier oid;
  
  private ASN1Integer docLength;
  
  private byte[][] coeffQuadratic;
  
  private byte[][] coeffSingular;
  
  private byte[] coeffScalar;
  
  private RainbowPublicKey(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.getObjectAt(0) instanceof ASN1Integer) {
      this.version = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(0));
    } else {
      this.oid = ASN1ObjectIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
    } 
    this.docLength = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(1));
    ASN1Sequence aSN1Sequence1 = ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(2));
    this.coeffQuadratic = new byte[aSN1Sequence1.size()][];
    for (byte b1 = 0; b1 < aSN1Sequence1.size(); b1++)
      this.coeffQuadratic[b1] = ASN1OctetString.getInstance(aSN1Sequence1.getObjectAt(b1)).getOctets(); 
    ASN1Sequence aSN1Sequence2 = (ASN1Sequence)paramASN1Sequence.getObjectAt(3);
    this.coeffSingular = new byte[aSN1Sequence2.size()][];
    for (byte b2 = 0; b2 < aSN1Sequence2.size(); b2++)
      this.coeffSingular[b2] = ASN1OctetString.getInstance(aSN1Sequence2.getObjectAt(b2)).getOctets(); 
    ASN1Sequence aSN1Sequence3 = (ASN1Sequence)paramASN1Sequence.getObjectAt(4);
    this.coeffScalar = ASN1OctetString.getInstance(aSN1Sequence3.getObjectAt(0)).getOctets();
  }
  
  public RainbowPublicKey(int paramInt, short[][] paramArrayOfshort1, short[][] paramArrayOfshort2, short[] paramArrayOfshort) {
    this.version = new ASN1Integer(0L);
    this.docLength = new ASN1Integer(paramInt);
    this.coeffQuadratic = RainbowUtil.convertArray(paramArrayOfshort1);
    this.coeffSingular = RainbowUtil.convertArray(paramArrayOfshort2);
    this.coeffScalar = RainbowUtil.convertArray(paramArrayOfshort);
  }
  
  public static RainbowPublicKey getInstance(Object paramObject) {
    return (paramObject instanceof RainbowPublicKey) ? (RainbowPublicKey)paramObject : ((paramObject != null) ? new RainbowPublicKey(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public ASN1Integer getVersion() {
    return this.version;
  }
  
  public int getDocLength() {
    return this.docLength.getValue().intValue();
  }
  
  public short[][] getCoeffQuadratic() {
    return RainbowUtil.convertArray(this.coeffQuadratic);
  }
  
  public short[][] getCoeffSingular() {
    return RainbowUtil.convertArray(this.coeffSingular);
  }
  
  public short[] getCoeffScalar() {
    return RainbowUtil.convertArray(this.coeffScalar);
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
    if (this.version != null) {
      aSN1EncodableVector1.add((ASN1Encodable)this.version);
    } else {
      aSN1EncodableVector1.add((ASN1Encodable)this.oid);
    } 
    aSN1EncodableVector1.add((ASN1Encodable)this.docLength);
    ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
    for (byte b1 = 0; b1 < this.coeffQuadratic.length; b1++)
      aSN1EncodableVector2.add((ASN1Encodable)new DEROctetString(this.coeffQuadratic[b1])); 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector2));
    ASN1EncodableVector aSN1EncodableVector3 = new ASN1EncodableVector();
    for (byte b2 = 0; b2 < this.coeffSingular.length; b2++)
      aSN1EncodableVector3.add((ASN1Encodable)new DEROctetString(this.coeffSingular[b2])); 
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector3));
    ASN1EncodableVector aSN1EncodableVector4 = new ASN1EncodableVector();
    aSN1EncodableVector4.add((ASN1Encodable)new DEROctetString(this.coeffScalar));
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector4));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector1);
  }
}
