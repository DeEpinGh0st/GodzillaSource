package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;

public class DERBMPString extends ASN1Primitive implements ASN1String {
  private final char[] string;
  
  public static DERBMPString getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof DERBMPString)
      return (DERBMPString)paramObject; 
    if (paramObject instanceof byte[])
      try {
        return (DERBMPString)fromByteArray((byte[])paramObject);
      } catch (Exception exception) {
        throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
      }  
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static DERBMPString getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    ASN1Primitive aSN1Primitive = paramASN1TaggedObject.getObject();
    return (paramBoolean || aSN1Primitive instanceof DERBMPString) ? getInstance(aSN1Primitive) : new DERBMPString(ASN1OctetString.getInstance(aSN1Primitive).getOctets());
  }
  
  DERBMPString(byte[] paramArrayOfbyte) {
    char[] arrayOfChar = new char[paramArrayOfbyte.length / 2];
    for (byte b = 0; b != arrayOfChar.length; b++)
      arrayOfChar[b] = (char)(paramArrayOfbyte[2 * b] << 8 | paramArrayOfbyte[2 * b + 1] & 0xFF); 
    this.string = arrayOfChar;
  }
  
  DERBMPString(char[] paramArrayOfchar) {
    this.string = paramArrayOfchar;
  }
  
  public DERBMPString(String paramString) {
    this.string = paramString.toCharArray();
  }
  
  public String getString() {
    return new String(this.string);
  }
  
  public String toString() {
    return getString();
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.string);
  }
  
  protected boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    if (!(paramASN1Primitive instanceof DERBMPString))
      return false; 
    DERBMPString dERBMPString = (DERBMPString)paramASN1Primitive;
    return Arrays.areEqual(this.string, dERBMPString.string);
  }
  
  boolean isConstructed() {
    return false;
  }
  
  int encodedLength() {
    return 1 + StreamUtil.calculateBodyLength(this.string.length * 2) + this.string.length * 2;
  }
  
  void encode(ASN1OutputStream paramASN1OutputStream) throws IOException {
    paramASN1OutputStream.write(30);
    paramASN1OutputStream.writeLength(this.string.length * 2);
    for (byte b = 0; b != this.string.length; b++) {
      char c = this.string[b];
      paramASN1OutputStream.write((byte)(c >> 8));
      paramASN1OutputStream.write((byte)c);
    } 
  }
}
