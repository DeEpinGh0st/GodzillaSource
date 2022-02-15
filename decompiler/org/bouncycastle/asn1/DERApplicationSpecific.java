package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bouncycastle.util.encoders.Hex;

public class DERApplicationSpecific extends ASN1ApplicationSpecific {
  DERApplicationSpecific(boolean paramBoolean, int paramInt, byte[] paramArrayOfbyte) {
    super(paramBoolean, paramInt, paramArrayOfbyte);
  }
  
  public DERApplicationSpecific(int paramInt, byte[] paramArrayOfbyte) {
    this(false, paramInt, paramArrayOfbyte);
  }
  
  public DERApplicationSpecific(int paramInt, ASN1Encodable paramASN1Encodable) throws IOException {
    this(true, paramInt, paramASN1Encodable);
  }
  
  public DERApplicationSpecific(boolean paramBoolean, int paramInt, ASN1Encodable paramASN1Encodable) throws IOException {
    super((paramBoolean || paramASN1Encodable.toASN1Primitive().isConstructed()), paramInt, getEncoding(paramBoolean, paramASN1Encodable));
  }
  
  private static byte[] getEncoding(boolean paramBoolean, ASN1Encodable paramASN1Encodable) throws IOException {
    byte[] arrayOfByte1 = paramASN1Encodable.toASN1Primitive().getEncoded("DER");
    if (paramBoolean)
      return arrayOfByte1; 
    int i = getLengthOfHeader(arrayOfByte1);
    byte[] arrayOfByte2 = new byte[arrayOfByte1.length - i];
    System.arraycopy(arrayOfByte1, i, arrayOfByte2, 0, arrayOfByte2.length);
    return arrayOfByte2;
  }
  
  public DERApplicationSpecific(int paramInt, ASN1EncodableVector paramASN1EncodableVector) {
    super(true, paramInt, getEncodedVector(paramASN1EncodableVector));
  }
  
  private static byte[] getEncodedVector(ASN1EncodableVector paramASN1EncodableVector) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    for (byte b = 0; b != paramASN1EncodableVector.size(); b++) {
      try {
        byteArrayOutputStream.write(((ASN1Object)paramASN1EncodableVector.get(b)).getEncoded("DER"));
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
    paramASN1OutputStream.writeEncoded(i, this.tag, this.octets);
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[");
    if (isConstructed())
      stringBuffer.append("CONSTRUCTED "); 
    stringBuffer.append("APPLICATION ");
    stringBuffer.append(Integer.toString(getApplicationTag()));
    stringBuffer.append("]");
    if (this.octets != null) {
      stringBuffer.append(" #");
      stringBuffer.append(Hex.toHexString(this.octets));
    } else {
      stringBuffer.append(" #null");
    } 
    stringBuffer.append(" ");
    return stringBuffer.toString();
  }
}
