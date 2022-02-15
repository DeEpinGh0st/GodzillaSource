package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

public abstract class ASN1BitString extends ASN1Primitive implements ASN1String {
  private static final char[] table = new char[] { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'A', 'B', 'C', 'D', 'E', 'F' };
  
  protected final byte[] data;
  
  protected final int padBits;
  
  protected static int getPadBits(int paramInt) {
    int i = 0;
    byte b;
    for (b = 3; b >= 0; b--) {
      if (b != 0) {
        if (paramInt >> b * 8 != 0) {
          i = paramInt >> b * 8 & 0xFF;
          break;
        } 
      } else if (paramInt != 0) {
        i = paramInt & 0xFF;
        break;
      } 
    } 
    if (i == 0)
      return 0; 
    for (b = 1; ((i <<= 1) & 0xFF) != 0; b++);
    return 8 - b;
  }
  
  protected static byte[] getBytes(int paramInt) {
    if (paramInt == 0)
      return new byte[0]; 
    byte b1 = 4;
    for (byte b2 = 3; b2 >= 1 && (paramInt & 255 << b2 * 8) == 0; b2--)
      b1--; 
    byte[] arrayOfByte = new byte[b1];
    for (byte b3 = 0; b3 < b1; b3++)
      arrayOfByte[b3] = (byte)(paramInt >> b3 * 8 & 0xFF); 
    return arrayOfByte;
  }
  
  public ASN1BitString(byte[] paramArrayOfbyte, int paramInt) {
    if (paramArrayOfbyte == null)
      throw new NullPointerException("data cannot be null"); 
    if (paramArrayOfbyte.length == 0 && paramInt != 0)
      throw new IllegalArgumentException("zero length data with non-zero pad bits"); 
    if (paramInt > 7 || paramInt < 0)
      throw new IllegalArgumentException("pad bits cannot be greater than 7 or less than 0"); 
    this.data = Arrays.clone(paramArrayOfbyte);
    this.padBits = paramInt;
  }
  
  public String getString() {
    StringBuffer stringBuffer = new StringBuffer("#");
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ASN1OutputStream aSN1OutputStream = new ASN1OutputStream(byteArrayOutputStream);
    try {
      aSN1OutputStream.writeObject(this);
    } catch (IOException iOException) {
      throw new ASN1ParsingException("Internal error encoding BitString: " + iOException.getMessage(), iOException);
    } 
    byte[] arrayOfByte = byteArrayOutputStream.toByteArray();
    for (byte b = 0; b != arrayOfByte.length; b++) {
      stringBuffer.append(table[arrayOfByte[b] >>> 4 & 0xF]);
      stringBuffer.append(table[arrayOfByte[b] & 0xF]);
    } 
    return stringBuffer.toString();
  }
  
  public int intValue() {
    int i = 0;
    byte[] arrayOfByte = this.data;
    if (this.padBits > 0 && this.data.length <= 4)
      arrayOfByte = derForm(this.data, this.padBits); 
    for (byte b = 0; b != arrayOfByte.length && b != 4; b++)
      i |= (arrayOfByte[b] & 0xFF) << 8 * b; 
    return i;
  }
  
  public byte[] getOctets() {
    if (this.padBits != 0)
      throw new IllegalStateException("attempt to get non-octet aligned data from BIT STRING"); 
    return Arrays.clone(this.data);
  }
  
  public byte[] getBytes() {
    return derForm(this.data, this.padBits);
  }
  
  public int getPadBits() {
    return this.padBits;
  }
  
  public String toString() {
    return getString();
  }
  
  public int hashCode() {
    return this.padBits ^ Arrays.hashCode(getBytes());
  }
  
  protected boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    if (!(paramASN1Primitive instanceof ASN1BitString))
      return false; 
    ASN1BitString aSN1BitString = (ASN1BitString)paramASN1Primitive;
    return (this.padBits == aSN1BitString.padBits && Arrays.areEqual(getBytes(), aSN1BitString.getBytes()));
  }
  
  protected static byte[] derForm(byte[] paramArrayOfbyte, int paramInt) {
    byte[] arrayOfByte = Arrays.clone(paramArrayOfbyte);
    if (paramInt > 0)
      arrayOfByte[paramArrayOfbyte.length - 1] = (byte)(arrayOfByte[paramArrayOfbyte.length - 1] & 255 << paramInt); 
    return arrayOfByte;
  }
  
  static ASN1BitString fromInputStream(int paramInt, InputStream paramInputStream) throws IOException {
    if (paramInt < 1)
      throw new IllegalArgumentException("truncated BIT STRING detected"); 
    int i = paramInputStream.read();
    byte[] arrayOfByte = new byte[paramInt - 1];
    if (arrayOfByte.length != 0) {
      if (Streams.readFully(paramInputStream, arrayOfByte) != arrayOfByte.length)
        throw new EOFException("EOF encountered in middle of BIT STRING"); 
      if (i > 0 && i < 8 && arrayOfByte[arrayOfByte.length - 1] != (byte)(arrayOfByte[arrayOfByte.length - 1] & 255 << i))
        return new DLBitString(arrayOfByte, i); 
    } 
    return new DERBitString(arrayOfByte, i);
  }
  
  public ASN1Primitive getLoadedObject() {
    return toASN1Primitive();
  }
  
  ASN1Primitive toDERObject() {
    return new DERBitString(this.data, this.padBits);
  }
  
  ASN1Primitive toDLObject() {
    return new DLBitString(this.data, this.padBits);
  }
  
  abstract void encode(ASN1OutputStream paramASN1OutputStream) throws IOException;
}
