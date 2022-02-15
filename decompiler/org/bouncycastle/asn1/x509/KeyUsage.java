package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERBitString;

public class KeyUsage extends ASN1Object {
  public static final int digitalSignature = 128;
  
  public static final int nonRepudiation = 64;
  
  public static final int keyEncipherment = 32;
  
  public static final int dataEncipherment = 16;
  
  public static final int keyAgreement = 8;
  
  public static final int keyCertSign = 4;
  
  public static final int cRLSign = 2;
  
  public static final int encipherOnly = 1;
  
  public static final int decipherOnly = 32768;
  
  private DERBitString bitString;
  
  public static KeyUsage getInstance(Object paramObject) {
    return (paramObject instanceof KeyUsage) ? (KeyUsage)paramObject : ((paramObject != null) ? new KeyUsage(DERBitString.getInstance(paramObject)) : null);
  }
  
  public static KeyUsage fromExtensions(Extensions paramExtensions) {
    return getInstance(paramExtensions.getExtensionParsedValue(Extension.keyUsage));
  }
  
  public KeyUsage(int paramInt) {
    this.bitString = new DERBitString(paramInt);
  }
  
  private KeyUsage(DERBitString paramDERBitString) {
    this.bitString = paramDERBitString;
  }
  
  public boolean hasUsages(int paramInt) {
    return ((this.bitString.intValue() & paramInt) == paramInt);
  }
  
  public byte[] getBytes() {
    return this.bitString.getBytes();
  }
  
  public int getPadBits() {
    return this.bitString.getPadBits();
  }
  
  public String toString() {
    byte[] arrayOfByte = this.bitString.getBytes();
    return (arrayOfByte.length == 1) ? ("KeyUsage: 0x" + Integer.toHexString(arrayOfByte[0] & 0xFF)) : ("KeyUsage: 0x" + Integer.toHexString((arrayOfByte[1] & 0xFF) << 8 | arrayOfByte[0] & 0xFF));
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.bitString;
  }
}
