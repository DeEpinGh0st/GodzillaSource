package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bouncycastle.util.Arrays;

public class DERUniversalString extends ASN1Primitive implements ASN1String {
  private static final char[] table = new char[] { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'A', 'B', 'C', 'D', 'E', 'F' };
  
  private final byte[] string;
  
  public static DERUniversalString getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof DERUniversalString)
      return (DERUniversalString)paramObject; 
    if (paramObject instanceof byte[])
      try {
        return (DERUniversalString)fromByteArray((byte[])paramObject);
      } catch (Exception exception) {
        throw new IllegalArgumentException("encoding error getInstance: " + exception.toString());
      }  
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static DERUniversalString getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    ASN1Primitive aSN1Primitive = paramASN1TaggedObject.getObject();
    return (paramBoolean || aSN1Primitive instanceof DERUniversalString) ? getInstance(aSN1Primitive) : new DERUniversalString(((ASN1OctetString)aSN1Primitive).getOctets());
  }
  
  public DERUniversalString(byte[] paramArrayOfbyte) {
    this.string = Arrays.clone(paramArrayOfbyte);
  }
  
  public String getString() {
    StringBuffer stringBuffer = new StringBuffer("#");
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ASN1OutputStream aSN1OutputStream = new ASN1OutputStream(byteArrayOutputStream);
    try {
      aSN1OutputStream.writeObject(this);
    } catch (IOException iOException) {
      throw new ASN1ParsingException("internal error encoding BitString");
    } 
    byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
    for (byte b = 0; b != arrayOfByte.length; b++) {
      stringBuffer.append(table[arrayOfByte[b] >>> 4 & 0xF]);
      stringBuffer.append(table[arrayOfByte[b] & 0xF]);
    } 
    return stringBuffer.toString();
  }
  
  public String toString() {
    return getString();
  }
  
  public byte[] getOctets() {
    return Arrays.clone(this.string);
  }
  
  boolean isConstructed() {
    return false;
  }
  
  int encodedLength() {
    return 1 + StreamUtil.calculateBodyLength(this.string.length) + this.string.length;
  }
  
  void encode(ASN1OutputStream paramASN1OutputStream) throws IOException {
    paramASN1OutputStream.writeEncoded(28, getOctets());
  }
  
  boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    return !(paramASN1Primitive instanceof DERUniversalString) ? false : Arrays.areEqual(this.string, ((DERUniversalString)paramASN1Primitive).string);
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.string);
  }
}
