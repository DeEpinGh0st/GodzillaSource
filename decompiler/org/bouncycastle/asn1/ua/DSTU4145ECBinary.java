package org.bouncycastle.asn1.ua;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.util.Arrays;

public class DSTU4145ECBinary extends ASN1Object {
  BigInteger version = BigInteger.valueOf(0L);
  
  DSTU4145BinaryField f;
  
  ASN1Integer a;
  
  ASN1OctetString b;
  
  ASN1Integer n;
  
  ASN1OctetString bp;
  
  public DSTU4145ECBinary(ECDomainParameters paramECDomainParameters) {
    ECCurve eCCurve = paramECDomainParameters.getCurve();
    if (!ECAlgorithms.isF2mCurve(eCCurve))
      throw new IllegalArgumentException("only binary domain is possible"); 
    PolynomialExtensionField polynomialExtensionField = (PolynomialExtensionField)eCCurve.getField();
    int[] arrayOfInt = polynomialExtensionField.getMinimalPolynomial().getExponentsPresent();
    if (arrayOfInt.length == 3) {
      this.f = new DSTU4145BinaryField(arrayOfInt[2], arrayOfInt[1]);
    } else if (arrayOfInt.length == 5) {
      this.f = new DSTU4145BinaryField(arrayOfInt[4], arrayOfInt[1], arrayOfInt[2], arrayOfInt[3]);
    } else {
      throw new IllegalArgumentException("curve must have a trinomial or pentanomial basis");
    } 
    this.a = new ASN1Integer(eCCurve.getA().toBigInteger());
    this.b = (ASN1OctetString)new DEROctetString(eCCurve.getB().getEncoded());
    this.n = new ASN1Integer(paramECDomainParameters.getN());
    this.bp = (ASN1OctetString)new DEROctetString(DSTU4145PointEncoder.encodePoint(paramECDomainParameters.getG()));
  }
  
  private DSTU4145ECBinary(ASN1Sequence paramASN1Sequence) {
    byte b = 0;
    if (paramASN1Sequence.getObjectAt(b) instanceof ASN1TaggedObject) {
      ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)paramASN1Sequence.getObjectAt(b);
      if (aSN1TaggedObject.isExplicit() && 0 == aSN1TaggedObject.getTagNo()) {
        this.version = ASN1Integer.getInstance(aSN1TaggedObject.getLoadedObject()).getValue();
        b++;
      } else {
        throw new IllegalArgumentException("object parse error");
      } 
    } 
    this.f = DSTU4145BinaryField.getInstance(paramASN1Sequence.getObjectAt(b));
    this.a = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(++b));
    this.b = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(++b));
    this.n = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(++b));
    this.bp = ASN1OctetString.getInstance(paramASN1Sequence.getObjectAt(++b));
  }
  
  public static DSTU4145ECBinary getInstance(Object paramObject) {
    return (paramObject instanceof DSTU4145ECBinary) ? (DSTU4145ECBinary)paramObject : ((paramObject != null) ? new DSTU4145ECBinary(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public DSTU4145BinaryField getField() {
    return this.f;
  }
  
  public BigInteger getA() {
    return this.a.getValue();
  }
  
  public byte[] getB() {
    return Arrays.clone(this.b.getOctets());
  }
  
  public BigInteger getN() {
    return this.n.getValue();
  }
  
  public byte[] getG() {
    return Arrays.clone(this.bp.getOctets());
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    if (0 != this.version.compareTo(BigInteger.valueOf(0L)))
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)new ASN1Integer(this.version))); 
    aSN1EncodableVector.add((ASN1Encodable)this.f);
    aSN1EncodableVector.add((ASN1Encodable)this.a);
    aSN1EncodableVector.add((ASN1Encodable)this.b);
    aSN1EncodableVector.add((ASN1Encodable)this.n);
    aSN1EncodableVector.add((ASN1Encodable)this.bp);
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
