package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

public class BEROctetString extends ASN1OctetString {
  private static final int MAX_LENGTH = 1000;
  
  private ASN1OctetString[] octs;
  
  private static byte[] toBytes(ASN1OctetString[] paramArrayOfASN1OctetString) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    for (byte b = 0; b != paramArrayOfASN1OctetString.length; b++) {
      try {
        DEROctetString dEROctetString = (DEROctetString)paramArrayOfASN1OctetString[b];
        byteArrayOutputStream.write(dEROctetString.getOctets());
      } catch (ClassCastException classCastException) {
        throw new IllegalArgumentException(paramArrayOfASN1OctetString[b].getClass().getName() + " found in input should only contain DEROctetString");
      } catch (IOException iOException) {
        throw new IllegalArgumentException("exception converting octets " + iOException.toString());
      } 
    } 
    return byteArrayOutputStream.toByteArray();
  }
  
  public BEROctetString(byte[] paramArrayOfbyte) {
    super(paramArrayOfbyte);
  }
  
  public BEROctetString(ASN1OctetString[] paramArrayOfASN1OctetString) {
    super(toBytes(paramArrayOfASN1OctetString));
    this.octs = paramArrayOfASN1OctetString;
  }
  
  public byte[] getOctets() {
    return this.string;
  }
  
  public Enumeration getObjects() {
    return (this.octs == null) ? generateOcts().elements() : new Enumeration() {
        int counter = 0;
        
        public boolean hasMoreElements() {
          return (this.counter < BEROctetString.this.octs.length);
        }
        
        public Object nextElement() {
          return BEROctetString.this.octs[this.counter++];
        }
      };
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
  
  boolean isConstructed() {
    return true;
  }
  
  int encodedLength() throws IOException {
    int i = 0;
    Enumeration<ASN1Encodable> enumeration = getObjects();
    while (enumeration.hasMoreElements())
      i += ((ASN1Encodable)enumeration.nextElement()).toASN1Primitive().encodedLength(); 
    return 2 + i + 2;
  }
  
  public void encode(ASN1OutputStream paramASN1OutputStream) throws IOException {
    paramASN1OutputStream.write(36);
    paramASN1OutputStream.write(128);
    Enumeration<ASN1Encodable> enumeration = getObjects();
    while (enumeration.hasMoreElements())
      paramASN1OutputStream.writeObject(enumeration.nextElement()); 
    paramASN1OutputStream.write(0);
    paramASN1OutputStream.write(0);
  }
  
  static BEROctetString fromSequence(ASN1Sequence paramASN1Sequence) {
    ASN1OctetString[] arrayOfASN1OctetString = new ASN1OctetString[paramASN1Sequence.size()];
    Enumeration<ASN1OctetString> enumeration = paramASN1Sequence.getObjects();
    byte b = 0;
    while (enumeration.hasMoreElements())
      arrayOfASN1OctetString[b++] = enumeration.nextElement(); 
    return new BEROctetString(arrayOfASN1OctetString);
  }
}
