package org.bouncycastle.asn1;

import java.io.IOException;

public class DERBitString extends ASN1BitString {
  public static DERBitString getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof DERBitString)
      return (DERBitString)paramObject; 
    if (paramObject instanceof DLBitString)
      return new DERBitString(((DLBitString)paramObject).data, ((DLBitString)paramObject).padBits); 
    if (paramObject instanceof byte[])
      try {
        return (DERBitString)fromByteArray((byte[])paramObject);
      } catch (Exception exception) {
        throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
      }  
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static DERBitString getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    ASN1Primitive aSN1Primitive = paramASN1TaggedObject.getObject();
    return (paramBoolean || aSN1Primitive instanceof DERBitString) ? getInstance(aSN1Primitive) : fromOctetString(((ASN1OctetString)aSN1Primitive).getOctets());
  }
  
  protected DERBitString(byte paramByte, int paramInt) {
    this(toByteArray(paramByte), paramInt);
  }
  
  private static byte[] toByteArray(byte paramByte) {
    byte[] arrayOfByte = new byte[1];
    arrayOfByte[0] = paramByte;
    return arrayOfByte;
  }
  
  public DERBitString(byte[] paramArrayOfbyte, int paramInt) {
    super(paramArrayOfbyte, paramInt);
  }
  
  public DERBitString(byte[] paramArrayOfbyte) {
    this(paramArrayOfbyte, 0);
  }
  
  public DERBitString(int paramInt) {
    super(getBytes(paramInt), getPadBits(paramInt));
  }
  
  public DERBitString(ASN1Encodable paramASN1Encodable) throws IOException {
    super(paramASN1Encodable.toASN1Primitive().getEncoded("DER"), 0);
  }
  
  boolean isConstructed() {
    return false;
  }
  
  int encodedLength() {
    return 1 + StreamUtil.calculateBodyLength(this.data.length + 1) + this.data.length + 1;
  }
  
  void encode(ASN1OutputStream paramASN1OutputStream) throws IOException {
    byte[] arrayOfByte1 = derForm(this.data, this.padBits);
    byte[] arrayOfByte2 = new byte[arrayOfByte1.length + 1];
    arrayOfByte2[0] = (byte)getPadBits();
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 1, arrayOfByte2.length - 1);
    paramASN1OutputStream.writeEncoded(3, arrayOfByte2);
  }
  
  static DERBitString fromOctetString(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length < 1)
      throw new IllegalArgumentException("truncated BIT STRING detected"); 
    byte b = paramArrayOfbyte[0];
    byte[] arrayOfByte = new byte[paramArrayOfbyte.length - 1];
    if (arrayOfByte.length != 0)
      System.arraycopy(paramArrayOfbyte, 1, arrayOfByte, 0, paramArrayOfbyte.length - 1); 
    return new DERBitString(arrayOfByte, b);
  }
}
