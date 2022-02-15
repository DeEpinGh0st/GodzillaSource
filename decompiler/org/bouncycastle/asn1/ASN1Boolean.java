package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.util.Arrays;

public class ASN1Boolean extends ASN1Primitive {
  private static final byte[] TRUE_VALUE = new byte[] { -1 };
  
  private static final byte[] FALSE_VALUE = new byte[] { 0 };
  
  private final byte[] value;
  
  public static final ASN1Boolean FALSE = new ASN1Boolean(false);
  
  public static final ASN1Boolean TRUE = new ASN1Boolean(true);
  
  public static ASN1Boolean getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof ASN1Boolean)
      return (ASN1Boolean)paramObject; 
    if (paramObject instanceof byte[]) {
      byte[] arrayOfByte = (byte[])paramObject;
      try {
        return (ASN1Boolean)fromByteArray(arrayOfByte);
      } catch (IOException iOException) {
        throw new IllegalArgumentException("failed to construct boolean from byte[]: " + iOException.getMessage());
      } 
    } 
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static ASN1Boolean getInstance(boolean paramBoolean) {
    return paramBoolean ? TRUE : FALSE;
  }
  
  public static ASN1Boolean getInstance(int paramInt) {
    return (paramInt != 0) ? TRUE : FALSE;
  }
  
  public static ASN1Boolean getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    ASN1Primitive aSN1Primitive = paramASN1TaggedObject.getObject();
    return (paramBoolean || aSN1Primitive instanceof ASN1Boolean) ? getInstance(aSN1Primitive) : fromOctetString(((ASN1OctetString)aSN1Primitive).getOctets());
  }
  
  ASN1Boolean(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length != 1)
      throw new IllegalArgumentException("byte value should have 1 byte in it"); 
    if (paramArrayOfbyte[0] == 0) {
      this.value = FALSE_VALUE;
    } else if ((paramArrayOfbyte[0] & 0xFF) == 255) {
      this.value = TRUE_VALUE;
    } else {
      this.value = Arrays.clone(paramArrayOfbyte);
    } 
  }
  
  public ASN1Boolean(boolean paramBoolean) {
    this.value = paramBoolean ? TRUE_VALUE : FALSE_VALUE;
  }
  
  public boolean isTrue() {
    return (this.value[0] != 0);
  }
  
  boolean isConstructed() {
    return false;
  }
  
  int encodedLength() {
    return 3;
  }
  
  void encode(ASN1OutputStream paramASN1OutputStream) throws IOException {
    paramASN1OutputStream.writeEncoded(1, this.value);
  }
  
  protected boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    return (paramASN1Primitive instanceof ASN1Boolean) ? ((this.value[0] == ((ASN1Boolean)paramASN1Primitive).value[0])) : false;
  }
  
  public int hashCode() {
    return this.value[0];
  }
  
  public String toString() {
    return (this.value[0] != 0) ? "TRUE" : "FALSE";
  }
  
  static ASN1Boolean fromOctetString(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length != 1)
      throw new IllegalArgumentException("BOOLEAN value should have 1 byte in it"); 
    return (paramArrayOfbyte[0] == 0) ? FALSE : (((paramArrayOfbyte[0] & 0xFF) == 255) ? TRUE : new ASN1Boolean(paramArrayOfbyte));
  }
}
