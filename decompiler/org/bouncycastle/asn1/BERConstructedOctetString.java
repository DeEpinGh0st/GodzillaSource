package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

public class BERConstructedOctetString extends BEROctetString {
  private static final int MAX_LENGTH = 1000;
  
  private Vector octs;
  
  private static byte[] toBytes(Vector<DEROctetString> paramVector) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    for (byte b = 0; b != paramVector.size(); b++) {
      try {
        DEROctetString dEROctetString = paramVector.elementAt(b);
        byteArrayOutputStream.write(dEROctetString.getOctets());
      } catch (ClassCastException classCastException) {
        throw new IllegalArgumentException(paramVector.elementAt(b).getClass().getName() + " found in input should only contain DEROctetString");
      } catch (IOException iOException) {
        throw new IllegalArgumentException("exception converting octets " + iOException.toString());
      } 
    } 
    return byteArrayOutputStream.toByteArray();
  }
  
  public BERConstructedOctetString(byte[] paramArrayOfbyte) {
    super(paramArrayOfbyte);
  }
  
  public BERConstructedOctetString(Vector paramVector) {
    super(toBytes(paramVector));
    this.octs = paramVector;
  }
  
  public BERConstructedOctetString(ASN1Primitive paramASN1Primitive) {
    super(toByteArray(paramASN1Primitive));
  }
  
  private static byte[] toByteArray(ASN1Primitive paramASN1Primitive) {
    try {
      return paramASN1Primitive.getEncoded();
    } catch (IOException iOException) {
      throw new IllegalArgumentException("Unable to encode object");
    } 
  }
  
  public BERConstructedOctetString(ASN1Encodable paramASN1Encodable) {
    this(paramASN1Encodable.toASN1Primitive());
  }
  
  public byte[] getOctets() {
    return this.string;
  }
  
  public Enumeration getObjects() {
    return (this.octs == null) ? generateOcts().elements() : this.octs.elements();
  }
  
  private Vector generateOcts() {
    Vector<DEROctetString> vector = new Vector();
    for (byte b = 0; b < this.string.length; b += 1000) {
      int i;
      if (b + 1000 > this.string.length) {
        i = this.string.length;
      } else {
        i = b + 1000;
      } 
      byte[] arrayOfByte = new byte[i - b];
      System.arraycopy(this.string, b, arrayOfByte, 0, arrayOfByte.length);
      vector.addElement(new DEROctetString(arrayOfByte));
    } 
    return vector;
  }
  
  public static BEROctetString fromSequence(ASN1Sequence paramASN1Sequence) {
    Vector vector = new Vector();
    Enumeration enumeration = paramASN1Sequence.getObjects();
    while (enumeration.hasMoreElements())
      vector.addElement(enumeration.nextElement()); 
    return new BERConstructedOctetString(vector);
  }
}
