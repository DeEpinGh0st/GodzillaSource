package org.bouncycastle.asn1.eac;

import java.math.BigInteger;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Arrays;

public class ECDSAPublicKey extends PublicKeyDataObject {
  private ASN1ObjectIdentifier usage;
  
  private BigInteger primeModulusP;
  
  private BigInteger firstCoefA;
  
  private BigInteger secondCoefB;
  
  private byte[] basePointG;
  
  private BigInteger orderOfBasePointR;
  
  private byte[] publicPointY;
  
  private BigInteger cofactorF;
  
  private int options;
  
  private static final int P = 1;
  
  private static final int A = 2;
  
  private static final int B = 4;
  
  private static final int G = 8;
  
  private static final int R = 16;
  
  private static final int Y = 32;
  
  private static final int F = 64;
  
  ECDSAPublicKey(ASN1Sequence paramASN1Sequence) throws IllegalArgumentException {
    Enumeration<Object> enumeration = paramASN1Sequence.getObjects();
    this.usage = ASN1ObjectIdentifier.getInstance(enumeration.nextElement());
    this.options = 0;
    while (enumeration.hasMoreElements()) {
      ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)enumeration.nextElement();
      if (aSN1TaggedObject instanceof ASN1TaggedObject) {
        ASN1TaggedObject aSN1TaggedObject1 = aSN1TaggedObject;
        switch (aSN1TaggedObject1.getTagNo()) {
          case 1:
            setPrimeModulusP(UnsignedInteger.getInstance(aSN1TaggedObject1).getValue());
            continue;
          case 2:
            setFirstCoefA(UnsignedInteger.getInstance(aSN1TaggedObject1).getValue());
            continue;
          case 3:
            setSecondCoefB(UnsignedInteger.getInstance(aSN1TaggedObject1).getValue());
            continue;
          case 4:
            setBasePointG(ASN1OctetString.getInstance(aSN1TaggedObject1, false));
            continue;
          case 5:
            setOrderOfBasePointR(UnsignedInteger.getInstance(aSN1TaggedObject1).getValue());
            continue;
          case 6:
            setPublicPointY(ASN1OctetString.getInstance(aSN1TaggedObject1, false));
            continue;
          case 7:
            setCofactorF(UnsignedInteger.getInstance(aSN1TaggedObject1).getValue());
            continue;
        } 
        this.options = 0;
        throw new IllegalArgumentException("Unknown Object Identifier!");
      } 
      throw new IllegalArgumentException("Unknown Object Identifier!");
    } 
    if (this.options != 32 && this.options != 127)
      throw new IllegalArgumentException("All options must be either present or absent!"); 
  }
  
  public ECDSAPublicKey(ASN1ObjectIdentifier paramASN1ObjectIdentifier, byte[] paramArrayOfbyte) throws IllegalArgumentException {
    this.usage = paramASN1ObjectIdentifier;
    setPublicPointY((ASN1OctetString)new DEROctetString(paramArrayOfbyte));
  }
  
  public ECDSAPublicKey(ASN1ObjectIdentifier paramASN1ObjectIdentifier, BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, byte[] paramArrayOfbyte1, BigInteger paramBigInteger4, byte[] paramArrayOfbyte2, int paramInt) {
    this.usage = paramASN1ObjectIdentifier;
    setPrimeModulusP(paramBigInteger1);
    setFirstCoefA(paramBigInteger2);
    setSecondCoefB(paramBigInteger3);
    setBasePointG((ASN1OctetString)new DEROctetString(paramArrayOfbyte1));
    setOrderOfBasePointR(paramBigInteger4);
    setPublicPointY((ASN1OctetString)new DEROctetString(paramArrayOfbyte2));
    setCofactorF(BigInteger.valueOf(paramInt));
  }
  
  public ASN1ObjectIdentifier getUsage() {
    return this.usage;
  }
  
  public byte[] getBasePointG() {
    return ((this.options & 0x8) != 0) ? Arrays.clone(this.basePointG) : null;
  }
  
  private void setBasePointG(ASN1OctetString paramASN1OctetString) throws IllegalArgumentException {
    if ((this.options & 0x8) == 0) {
      this.options |= 0x8;
      this.basePointG = paramASN1OctetString.getOctets();
    } else {
      throw new IllegalArgumentException("Base Point G already set");
    } 
  }
  
  public BigInteger getCofactorF() {
    return ((this.options & 0x40) != 0) ? this.cofactorF : null;
  }
  
  private void setCofactorF(BigInteger paramBigInteger) throws IllegalArgumentException {
    if ((this.options & 0x40) == 0) {
      this.options |= 0x40;
      this.cofactorF = paramBigInteger;
    } else {
      throw new IllegalArgumentException("Cofactor F already set");
    } 
  }
  
  public BigInteger getFirstCoefA() {
    return ((this.options & 0x2) != 0) ? this.firstCoefA : null;
  }
  
  private void setFirstCoefA(BigInteger paramBigInteger) throws IllegalArgumentException {
    if ((this.options & 0x2) == 0) {
      this.options |= 0x2;
      this.firstCoefA = paramBigInteger;
    } else {
      throw new IllegalArgumentException("First Coef A already set");
    } 
  }
  
  public BigInteger getOrderOfBasePointR() {
    return ((this.options & 0x10) != 0) ? this.orderOfBasePointR : null;
  }
  
  private void setOrderOfBasePointR(BigInteger paramBigInteger) throws IllegalArgumentException {
    if ((this.options & 0x10) == 0) {
      this.options |= 0x10;
      this.orderOfBasePointR = paramBigInteger;
    } else {
      throw new IllegalArgumentException("Order of base point R already set");
    } 
  }
  
  public BigInteger getPrimeModulusP() {
    return ((this.options & 0x1) != 0) ? this.primeModulusP : null;
  }
  
  private void setPrimeModulusP(BigInteger paramBigInteger) {
    if ((this.options & 0x1) == 0) {
      this.options |= 0x1;
      this.primeModulusP = paramBigInteger;
    } else {
      throw new IllegalArgumentException("Prime Modulus P already set");
    } 
  }
  
  public byte[] getPublicPointY() {
    return ((this.options & 0x20) != 0) ? Arrays.clone(this.publicPointY) : null;
  }
  
  private void setPublicPointY(ASN1OctetString paramASN1OctetString) throws IllegalArgumentException {
    if ((this.options & 0x20) == 0) {
      this.options |= 0x20;
      this.publicPointY = paramASN1OctetString.getOctets();
    } else {
      throw new IllegalArgumentException("Public Point Y already set");
    } 
  }
  
  public BigInteger getSecondCoefB() {
    return ((this.options & 0x4) != 0) ? this.secondCoefB : null;
  }
  
  private void setSecondCoefB(BigInteger paramBigInteger) throws IllegalArgumentException {
    if ((this.options & 0x4) == 0) {
      this.options |= 0x4;
      this.secondCoefB = paramBigInteger;
    } else {
      throw new IllegalArgumentException("Second Coef B already set");
    } 
  }
  
  public boolean hasParameters() {
    return (this.primeModulusP != null);
  }
  
  public ASN1EncodableVector getASN1EncodableVector(ASN1ObjectIdentifier paramASN1ObjectIdentifier, boolean paramBoolean) {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)paramASN1ObjectIdentifier);
    if (!paramBoolean) {
      aSN1EncodableVector.add((ASN1Encodable)new UnsignedInteger(1, getPrimeModulusP()));
      aSN1EncodableVector.add((ASN1Encodable)new UnsignedInteger(2, getFirstCoefA()));
      aSN1EncodableVector.add((ASN1Encodable)new UnsignedInteger(3, getSecondCoefB()));
      aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 4, (ASN1Encodable)new DEROctetString(getBasePointG())));
      aSN1EncodableVector.add((ASN1Encodable)new UnsignedInteger(5, getOrderOfBasePointR()));
    } 
    aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(false, 6, (ASN1Encodable)new DEROctetString(getPublicPointY())));
    if (!paramBoolean)
      aSN1EncodableVector.add((ASN1Encodable)new UnsignedInteger(7, getCofactorF())); 
    return aSN1EncodableVector;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)new DERSequence(getASN1EncodableVector(this.usage, !hasParameters()));
  }
}
