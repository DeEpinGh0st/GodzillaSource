package org.bouncycastle.asn1.ua;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class DSTU4145Params extends ASN1Object {
  private static final byte[] DEFAULT_DKE = new byte[] { 
      -87, -42, -21, 69, -15, 60, 112, -126, Byte.MIN_VALUE, -60, 
      -106, 123, 35, 31, 94, -83, -10, 88, -21, -92, 
      -64, 55, 41, 29, 56, -39, 107, -16, 37, -54, 
      78, 23, -8, -23, 114, 13, -58, 21, -76, 58, 
      40, -105, 95, 11, -63, -34, -93, 100, 56, -75, 
      100, -22, 44, 23, -97, -48, 18, 62, 109, -72, 
      -6, -59, 121, 4 };
  
  private ASN1ObjectIdentifier namedCurve;
  
  private DSTU4145ECBinary ecbinary;
  
  private byte[] dke = DEFAULT_DKE;
  
  public DSTU4145Params(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    this.namedCurve = paramASN1ObjectIdentifier;
  }
  
  public DSTU4145Params(ASN1ObjectIdentifier paramASN1ObjectIdentifier, byte[] paramArrayOfbyte) {
    this.namedCurve = paramASN1ObjectIdentifier;
    this.dke = Arrays.clone(paramArrayOfbyte);
  }
  
  public DSTU4145Params(DSTU4145ECBinary paramDSTU4145ECBinary) {
    this.ecbinary = paramDSTU4145ECBinary;
  }
  
  public boolean isNamedCurve() {
    return (this.namedCurve != null);
  }
  
  public DSTU4145ECBinary getECBinary() {
    return this.ecbinary;
  }
  
  public byte[] getDKE() {
    return this.dke;
  }
  
  public static byte[] getDefaultDKE() {
    return DEFAULT_DKE;
  }
  
  public ASN1ObjectIdentifier getNamedCurve() {
    return this.namedCurve;
  }
  
  public static DSTU4145Params getInstance(Object paramObject) {
    if (paramObject instanceof DSTU4145Params)
      return (DSTU4145Params)paramObject; 
    if (paramObject != null) {
      DSTU4145Params dSTU4145Params;
      ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(paramObject);
      if (aSN1Sequence.getObjectAt(0) instanceof ASN1ObjectIdentifier) {
        dSTU4145Params = new DSTU4145Params(ASN1ObjectIdentifier.getInstance(aSN1Sequence.getObjectAt(0)));
      } else {
        dSTU4145Params = new DSTU4145Params(DSTU4145ECBinary.getInstance(aSN1Sequence.getObjectAt(0)));
      } 
      if (aSN1Sequence.size() == 2) {
        dSTU4145Params.dke = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets();
        if (dSTU4145Params.dke.length != DEFAULT_DKE.length)
          throw new IllegalArgumentException("object parse error"); 
      } 
      return dSTU4145Params;
    } 
    throw new IllegalArgumentException("object parse error");
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (this.namedCurve != null) {
      aSN1EncodableVector.add((ASN1Encodable)this.namedCurve);
    } else {
      aSN1EncodableVector.add((ASN1Encodable)this.ecbinary);
    } 
    if (!Arrays.areEqual(this.dke, DEFAULT_DKE))
      aSN1EncodableVector.add((ASN1Encodable)new DEROctetString(this.dke)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
