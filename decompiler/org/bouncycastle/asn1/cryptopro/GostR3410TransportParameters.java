package org.bouncycastle.asn1.cryptopro;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.util.Arrays;

public class GostR3410TransportParameters extends ASN1Object {
  private final ASN1ObjectIdentifier encryptionParamSet;
  
  private final SubjectPublicKeyInfo ephemeralPublicKey;
  
  private final byte[] ukm;
  
  public GostR3410TransportParameters(ASN1ObjectIdentifier paramASN1ObjectIdentifier, SubjectPublicKeyInfo paramSubjectPublicKeyInfo, byte[] paramArrayOfbyte) {
    this.encryptionParamSet = paramASN1ObjectIdentifier;
    this.ephemeralPublicKey = paramSubjectPublicKeyInfo;
    this.ukm = Arrays.clone(paramArrayOfbyte);
  }
  
  private GostR3410TransportParameters(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() == 2) {
      this.encryptionParamSet = ASN1ObjectIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
      this.ukm = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(1)).getOctets();
      this.ephemeralPublicKey = null;
    } else if (paramASN1Sequence.size() == 3) {
      this.encryptionParamSet = ASN1ObjectIdentifier.getInstance(paramASN1Sequence.getObjectAt(0));
      this.ephemeralPublicKey = SubjectPublicKeyInfo.getInstance(ASN1TaggedObject.getInstance(paramASN1Sequence.getObjectAt(1)), false);
      this.ukm = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(2)).getOctets();
    } else {
      throw new IllegalArgumentException("unknown sequence length: " + paramASN1Sequence.size());
    } 
  }
  
  public static GostR3410TransportParameters getInstance(Object paramObject) {
    return (paramObject instanceof GostR3410TransportParameters) ? (GostR3410TransportParameters)paramObject : ((paramObject != null) ? new GostR3410TransportParameters(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public static GostR3410TransportParameters getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return new GostR3410TransportParameters(ASN1Sequence.getInstance(paramASN1TaggedObject, paramBoolean));
  }
  
  public ASN1ObjectIdentifier getEncryptionParamSet() {
    return this.encryptionParamSet;
  }
  
  public SubjectPublicKeyInfo getEphemeralPublicKey() {
    return this.ephemeralPublicKey;
  }
  
  public byte[] getUkm() {
    return Arrays.clone(this.ukm);
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)this.encryptionParamSet);
    if (this.ephemeralPublicKey != null)
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.ephemeralPublicKey)); 
    aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(this.ukm));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
