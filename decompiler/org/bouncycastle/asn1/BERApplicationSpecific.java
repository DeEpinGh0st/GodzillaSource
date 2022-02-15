package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BERApplicationSpecific extends ASN1ApplicationSpecific {
  BERApplicationSpecific(boolean paramBoolean, int paramInt, byte[] paramArrayOfbyte) {
    super(paramBoolean, paramInt, paramArrayOfbyte);
  }
  
  public BERApplicationSpecific(int paramInt, ASN1Encodable paramASN1Encodable) throws IOException {
    this(true, paramInt, paramASN1Encodable);
  }
  
  public BERApplicationSpecific(boolean paramBoolean, int paramInt, ASN1Encodable paramASN1Encodable) throws IOException {
    super((paramBoolean || paramASN1Encodable.toASN1Primitive().isConstructed()), paramInt, getEncoding(paramBoolean, paramASN1Encodable));
  }
  
  private static byte[] getEncoding(boolean paramBoolean, ASN1Encodable paramASN1Encodable) throws IOException {
    byte[] arrayOfByte1 = paramASN1Encodable.toASN1Primitive().getEncoded("BER");
    if (paramBoolean)
      return arrayOfByte1; 
    int i = getLengthOfHeader(arrayOfByte1);
    byte[] arrayOfByte2 = new byte[arrayOfByte1.length - i];
    System.arraycopy(arrayOfByte1, i, arrayOfByte2, 0, arrayOfByte2.length);
    return arrayOfByte2;
  }
  
  public BERApplicationSpecific(int paramInt, ASN1EncodableVector paramASN1EncodableVector) {
    super(true, paramInt, getEncodedVector(paramASN1EncodableVector));
  }
  
  private static byte[] getEncodedVector(ASN1EncodableVector paramASN1EncodableVector) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    for (byte b = 0; b != paramASN1EncodableVector.size(); b++) {
      try {
        byteArrayOutputStream.write(((ASN1Object)paramASN1EncodableVector.get(b)).getEncoded("BER"));
      } catch (IOException iOException) {
        throw new ASN1ParsingException("malformed object: " + iOException, iOException);
      } 
    } 
    return byteArrayOutputStream.toByteArray();
  }
  
  void encode(ASN1OutputStream paramASN1OutputStream) throws IOException {
    int i = 64;
    if (this.isConstructed)
      i |= 0x20; 
    paramASN1OutputStream.writeTag(i, this.tag);
    paramASN1OutputStream.write(128);
    paramASN1OutputStream.write(this.octets);
    paramASN1OutputStream.write(0);
    paramASN1OutputStream.write(0);
  }
}
