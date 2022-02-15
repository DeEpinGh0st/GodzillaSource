package org.bouncycastle.asn1.x9;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.field.PolynomialExtensionField;

public class X9ECParameters extends ASN1Object implements X9ObjectIdentifiers {
  private static final BigInteger ONE = BigInteger.valueOf(1L);
  
  private X9FieldID fieldID;
  
  private ECCurve curve;
  
  private X9ECPoint g;
  
  private BigInteger n;
  
  private BigInteger h;
  
  private byte[] seed;
  
  private X9ECParameters(ASN1Sequence paramASN1Sequence) {
    if (!(paramASN1Sequence.getObjectAt(0) instanceof ASN1Integer) || !((ASN1Integer)paramASN1Sequence.getObjectAt(0)).getValue().equals(ONE))
      throw new IllegalArgumentException("bad version in X9ECParameters"); 
    X9Curve x9Curve = new X9Curve(X9FieldID.getInstance(paramASN1Sequence.getObjectAt(1)), ASN1Sequence.getInstance(paramASN1Sequence.getObjectAt(2)));
    this.curve = x9Curve.getCurve();
    ASN1Encodable aSN1Encodable = paramASN1Sequence.getObjectAt(3);
    if (aSN1Encodable instanceof X9ECPoint) {
      this.g = (X9ECPoint)aSN1Encodable;
    } else {
      this.g = new X9ECPoint(this.curve, (ASN1OctetString)aSN1Encodable);
    } 
    this.n = ((ASN1Integer)paramASN1Sequence.getObjectAt(4)).getValue();
    this.seed = x9Curve.getSeed();
    if (paramASN1Sequence.size() == 6)
      this.h = ((ASN1Integer)paramASN1Sequence.getObjectAt(5)).getValue(); 
  }
  
  public static X9ECParameters getInstance(Object paramObject) {
    return (paramObject instanceof X9ECParameters) ? (X9ECParameters)paramObject : ((paramObject != null) ? new X9ECParameters(ASN1Sequence.getInstance(paramObject)) : null);
  }
  
  public X9ECParameters(ECCurve paramECCurve, ECPoint paramECPoint, BigInteger paramBigInteger) {
    this(paramECCurve, paramECPoint, paramBigInteger, (BigInteger)null, (byte[])null);
  }
  
  public X9ECParameters(ECCurve paramECCurve, X9ECPoint paramX9ECPoint, BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    this(paramECCurve, paramX9ECPoint, paramBigInteger1, paramBigInteger2, (byte[])null);
  }
  
  public X9ECParameters(ECCurve paramECCurve, ECPoint paramECPoint, BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    this(paramECCurve, paramECPoint, paramBigInteger1, paramBigInteger2, (byte[])null);
  }
  
  public X9ECParameters(ECCurve paramECCurve, ECPoint paramECPoint, BigInteger paramBigInteger1, BigInteger paramBigInteger2, byte[] paramArrayOfbyte) {
    this(paramECCurve, new X9ECPoint(paramECPoint), paramBigInteger1, paramBigInteger2, paramArrayOfbyte);
  }
  
  public X9ECParameters(ECCurve paramECCurve, X9ECPoint paramX9ECPoint, BigInteger paramBigInteger1, BigInteger paramBigInteger2, byte[] paramArrayOfbyte) {
    this.curve = paramECCurve;
    this.g = paramX9ECPoint;
    this.n = paramBigInteger1;
    this.h = paramBigInteger2;
    this.seed = paramArrayOfbyte;
    if (ECAlgorithms.isFpCurve(paramECCurve)) {
      this.fieldID = new X9FieldID(paramECCurve.getField().getCharacteristic());
    } else if (ECAlgorithms.isF2mCurve(paramECCurve)) {
      PolynomialExtensionField polynomialExtensionField = (PolynomialExtensionField)paramECCurve.getField();
      int[] arrayOfInt = polynomialExtensionField.getMinimalPolynomial().getExponentsPresent();
      if (arrayOfInt.length == 3) {
        this.fieldID = new X9FieldID(arrayOfInt[2], arrayOfInt[1]);
      } else if (arrayOfInt.length == 5) {
        this.fieldID = new X9FieldID(arrayOfInt[4], arrayOfInt[1], arrayOfInt[2], arrayOfInt[3]);
      } else {
        throw new IllegalArgumentException("Only trinomial and pentomial curves are supported");
      } 
    } else {
      throw new IllegalArgumentException("'curve' is of an unsupported type");
    } 
  }
  
  public ECCurve getCurve() {
    return this.curve;
  }
  
  public ECPoint getG() {
    return this.g.getPoint();
  }
  
  public BigInteger getN() {
    return this.n;
  }
  
  public BigInteger getH() {
    return this.h;
  }
  
  public byte[] getSeed() {
    return this.seed;
  }
  
  public X9Curve getCurveEntry() {
    return new X9Curve(this.curve, this.seed);
  }
  
  public X9FieldID getFieldIDEntry() {
    return this.fieldID;
  }
  
  public X9ECPoint getBaseEntry() {
    return this.g;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(ONE));
    aSN1EncodableVector.add((ASN1Encodable)this.fieldID);
    aSN1EncodableVector.add((ASN1Encodable)new X9Curve(this.curve, this.seed));
    aSN1EncodableVector.add((ASN1Encodable)this.g);
    aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(this.n));
    if (this.h != null)
      aSN1EncodableVector.add((ASN1Encodable)new ASN1Integer(this.h)); 
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector);
  }
}
