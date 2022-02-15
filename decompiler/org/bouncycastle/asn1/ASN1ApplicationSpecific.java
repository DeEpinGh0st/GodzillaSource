package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;

public abstract class ASN1ApplicationSpecific extends ASN1Primitive {
  protected final boolean isConstructed;
  
  protected final int tag;
  
  protected final byte[] octets;
  
  ASN1ApplicationSpecific(boolean paramBoolean, int paramInt, byte[] paramArrayOfbyte) {
    this.isConstructed = paramBoolean;
    this.tag = paramInt;
    this.octets = Arrays.clone(paramArrayOfbyte);
  }
  
  public static ASN1ApplicationSpecific getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof ASN1ApplicationSpecific)
      return (ASN1ApplicationSpecific)paramObject; 
    if (paramObject instanceof byte[])
      try {
        return getInstance(ASN1Primitive.fromByteArray((byte[])paramObject));
      } catch (IOException iOException) {
        throw new IllegalArgumentException("Failed to construct object from byte[]: " + iOException.getMessage());
      }  
    throw new IllegalArgumentException("unknown object in getInstance: " + paramObject.getClass().getName());
  }
  
  protected static int getLengthOfHeader(byte[] paramArrayOfbyte) {
    int i = paramArrayOfbyte[1] & 0xFF;
    if (i == 128)
      return 2; 
    if (i > 127) {
      int j = i & 0x7F;
      if (j > 4)
        throw new IllegalStateException("DER length more than 4 bytes: " + j); 
      return j + 2;
    } 
    return 2;
  }
  
  public boolean isConstructed() {
    return this.isConstructed;
  }
  
  public byte[] getContents() {
    return Arrays.clone(this.octets);
  }
  
  public int getApplicationTag() {
    return this.tag;
  }
  
  public ASN1Primitive getObject() throws IOException {
    return ASN1Primitive.fromByteArray(getContents());
  }
  
  public ASN1Primitive getObject(int paramInt) throws IOException {
    if (paramInt >= 31)
      throw new IOException("unsupported tag number"); 
    byte[] arrayOfByte1 = getEncoded();
    byte[] arrayOfByte2 = replaceTagNumber(paramInt, arrayOfByte1);
    if ((arrayOfByte1[0] & 0x20) != 0)
      arrayOfByte2[0] = (byte)(arrayOfByte2[0] | 0x20); 
    return ASN1Primitive.fromByteArray(arrayOfByte2);
  }
  
  int encodedLength() throws IOException {
    return StreamUtil.calculateTagLength(this.tag) + StreamUtil.calculateBodyLength(this.octets.length) + this.octets.length;
  }
  
  void encode(ASN1OutputStream paramASN1OutputStream) throws IOException {
    int i = 64;
    if (this.isConstructed)
      i |= 0x20; 
    paramASN1OutputStream.writeEncoded(i, this.tag, this.octets);
  }
  
  boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    if (!(paramASN1Primitive instanceof ASN1ApplicationSpecific))
      return false; 
    ASN1ApplicationSpecific aSN1ApplicationSpecific = (ASN1ApplicationSpecific)paramASN1Primitive;
    return (this.isConstructed == aSN1ApplicationSpecific.isConstructed && this.tag == aSN1ApplicationSpecific.tag && Arrays.areEqual(this.octets, aSN1ApplicationSpecific.octets));
  }
  
  public int hashCode() {
    return (this.isConstructed ? 1 : 0) ^ this.tag ^ Arrays.hashCode(this.octets);
  }
  
  private byte[] replaceTagNumber(int paramInt, byte[] paramArrayOfbyte) throws IOException {
    int i = paramArrayOfbyte[0] & 0x1F;
    byte b = 1;
    if (i == 31) {
      i = 0;
      int j = paramArrayOfbyte[b++] & 0xFF;
      if ((j & 0x7F) == 0)
        throw new ASN1ParsingException("corrupted stream - invalid high tag number found"); 
      while (j >= 0 && (j & 0x80) != 0) {
        i |= j & 0x7F;
        i <<= 7;
        j = paramArrayOfbyte[b++] & 0xFF;
      } 
    } 
    byte[] arrayOfByte = new byte[paramArrayOfbyte.length - b + 1];
    System.arraycopy(paramArrayOfbyte, b, arrayOfByte, 1, arrayOfByte.length - 1);
    arrayOfByte[0] = (byte)paramInt;
    return arrayOfByte;
  }
}
