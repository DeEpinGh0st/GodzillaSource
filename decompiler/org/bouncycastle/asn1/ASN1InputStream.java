package org.bouncycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.io.Streams;

public class ASN1InputStream extends FilterInputStream implements BERTags {
  private final int limit;
  
  private final boolean lazyEvaluate;
  
  private final byte[][] tmpBuffers;
  
  public ASN1InputStream(InputStream paramInputStream) {
    this(paramInputStream, StreamUtil.findLimit(paramInputStream));
  }
  
  public ASN1InputStream(byte[] paramArrayOfbyte) {
    this(new ByteArrayInputStream(paramArrayOfbyte), paramArrayOfbyte.length);
  }
  
  public ASN1InputStream(byte[] paramArrayOfbyte, boolean paramBoolean) {
    this(new ByteArrayInputStream(paramArrayOfbyte), paramArrayOfbyte.length, paramBoolean);
  }
  
  public ASN1InputStream(InputStream paramInputStream, int paramInt) {
    this(paramInputStream, paramInt, false);
  }
  
  public ASN1InputStream(InputStream paramInputStream, boolean paramBoolean) {
    this(paramInputStream, StreamUtil.findLimit(paramInputStream), paramBoolean);
  }
  
  public ASN1InputStream(InputStream paramInputStream, int paramInt, boolean paramBoolean) {
    super(paramInputStream);
    this.limit = paramInt;
    this.lazyEvaluate = paramBoolean;
    this.tmpBuffers = new byte[11][];
  }
  
  int getLimit() {
    return this.limit;
  }
  
  protected int readLength() throws IOException {
    return readLength(this, this.limit);
  }
  
  protected void readFully(byte[] paramArrayOfbyte) throws IOException {
    if (Streams.readFully(this, paramArrayOfbyte) != paramArrayOfbyte.length)
      throw new EOFException("EOF encountered in middle of object"); 
  }
  
  protected ASN1Primitive buildObject(int paramInt1, int paramInt2, int paramInt3) throws IOException {
    boolean bool = ((paramInt1 & 0x20) != 0) ? true : false;
    DefiniteLengthInputStream definiteLengthInputStream = new DefiniteLengthInputStream(this, paramInt3);
    if ((paramInt1 & 0x40) != 0)
      return new DERApplicationSpecific(bool, paramInt2, definiteLengthInputStream.toByteArray()); 
    if ((paramInt1 & 0x80) != 0)
      return (new ASN1StreamParser(definiteLengthInputStream)).readTaggedObject(bool, paramInt2); 
    if (bool) {
      ASN1EncodableVector aSN1EncodableVector;
      ASN1OctetString[] arrayOfASN1OctetString;
      byte b;
      switch (paramInt2) {
        case 4:
          aSN1EncodableVector = buildDEREncodableVector(definiteLengthInputStream);
          arrayOfASN1OctetString = new ASN1OctetString[aSN1EncodableVector.size()];
          for (b = 0; b != arrayOfASN1OctetString.length; b++)
            arrayOfASN1OctetString[b] = (ASN1OctetString)aSN1EncodableVector.get(b); 
          return new BEROctetString(arrayOfASN1OctetString);
        case 16:
          return this.lazyEvaluate ? new LazyEncodedSequence(definiteLengthInputStream.toByteArray()) : DERFactory.createSequence(buildDEREncodableVector(definiteLengthInputStream));
        case 17:
          return DERFactory.createSet(buildDEREncodableVector(definiteLengthInputStream));
        case 8:
          return new DERExternal(buildDEREncodableVector(definiteLengthInputStream));
      } 
      throw new IOException("unknown tag " + paramInt2 + " encountered");
    } 
    return createPrimitiveDERObject(paramInt2, definiteLengthInputStream, this.tmpBuffers);
  }
  
  ASN1EncodableVector buildEncodableVector() throws IOException {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    ASN1Primitive aSN1Primitive;
    while ((aSN1Primitive = readObject()) != null)
      aSN1EncodableVector.add(aSN1Primitive); 
    return aSN1EncodableVector;
  }
  
  ASN1EncodableVector buildDEREncodableVector(DefiniteLengthInputStream paramDefiniteLengthInputStream) throws IOException {
    return (new ASN1InputStream(paramDefiniteLengthInputStream)).buildEncodableVector();
  }
  
  public ASN1Primitive readObject() throws IOException {
    int i = read();
    if (i <= 0) {
      if (i == 0)
        throw new IOException("unexpected end-of-contents marker"); 
      return null;
    } 
    int j = readTagNumber(this, i);
    boolean bool = ((i & 0x20) != 0) ? true : false;
    int k = readLength();
    if (k < 0) {
      if (!bool)
        throw new IOException("indefinite-length primitive encoding encountered"); 
      IndefiniteLengthInputStream indefiniteLengthInputStream = new IndefiniteLengthInputStream(this, this.limit);
      ASN1StreamParser aSN1StreamParser = new ASN1StreamParser(indefiniteLengthInputStream, this.limit);
      if ((i & 0x40) != 0)
        return (new BERApplicationSpecificParser(j, aSN1StreamParser)).getLoadedObject(); 
      if ((i & 0x80) != 0)
        return (new BERTaggedObjectParser(true, j, aSN1StreamParser)).getLoadedObject(); 
      switch (j) {
        case 4:
          return (new BEROctetStringParser(aSN1StreamParser)).getLoadedObject();
        case 16:
          return (new BERSequenceParser(aSN1StreamParser)).getLoadedObject();
        case 17:
          return (new BERSetParser(aSN1StreamParser)).getLoadedObject();
        case 8:
          return (new DERExternalParser(aSN1StreamParser)).getLoadedObject();
      } 
      throw new IOException("unknown BER object encountered");
    } 
    try {
      return buildObject(i, j, k);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new ASN1Exception("corrupted stream detected", illegalArgumentException);
    } 
  }
  
  static int readTagNumber(InputStream paramInputStream, int paramInt) throws IOException {
    int i = paramInt & 0x1F;
    if (i == 31) {
      i = 0;
      int j = paramInputStream.read();
      if ((j & 0x7F) == 0)
        throw new IOException("corrupted stream - invalid high tag number found"); 
      while (j >= 0 && (j & 0x80) != 0) {
        i |= j & 0x7F;
        i <<= 7;
        j = paramInputStream.read();
      } 
      if (j < 0)
        throw new EOFException("EOF found inside tag value."); 
      i |= j & 0x7F;
    } 
    return i;
  }
  
  static int readLength(InputStream paramInputStream, int paramInt) throws IOException {
    int i = paramInputStream.read();
    if (i < 0)
      throw new EOFException("EOF found when length expected"); 
    if (i == 128)
      return -1; 
    if (i > 127) {
      int j = i & 0x7F;
      if (j > 4)
        throw new IOException("DER length more than 4 bytes: " + j); 
      i = 0;
      for (byte b = 0; b < j; b++) {
        int k = paramInputStream.read();
        if (k < 0)
          throw new EOFException("EOF found reading length"); 
        i = (i << 8) + k;
      } 
      if (i < 0)
        throw new IOException("corrupted stream - negative length found"); 
      if (i >= paramInt)
        throw new IOException("corrupted stream - out of bounds length found"); 
    } 
    return i;
  }
  
  private static byte[] getBuffer(DefiniteLengthInputStream paramDefiniteLengthInputStream, byte[][] paramArrayOfbyte) throws IOException {
    int i = paramDefiniteLengthInputStream.getRemaining();
    if (paramDefiniteLengthInputStream.getRemaining() < paramArrayOfbyte.length) {
      byte[] arrayOfByte = paramArrayOfbyte[i];
      if (arrayOfByte == null)
        arrayOfByte = paramArrayOfbyte[i] = new byte[i]; 
      Streams.readFully(paramDefiniteLengthInputStream, arrayOfByte);
      return arrayOfByte;
    } 
    return paramDefiniteLengthInputStream.toByteArray();
  }
  
  private static char[] getBMPCharBuffer(DefiniteLengthInputStream paramDefiniteLengthInputStream) throws IOException {
    int i = paramDefiniteLengthInputStream.getRemaining() / 2;
    char[] arrayOfChar = new char[i];
    byte b = 0;
    while (b < i) {
      int j = paramDefiniteLengthInputStream.read();
      if (j < 0)
        break; 
      int k = paramDefiniteLengthInputStream.read();
      if (k < 0)
        break; 
      arrayOfChar[b++] = (char)(j << 8 | k & 0xFF);
    } 
    return arrayOfChar;
  }
  
  static ASN1Primitive createPrimitiveDERObject(int paramInt, DefiniteLengthInputStream paramDefiniteLengthInputStream, byte[][] paramArrayOfbyte) throws IOException {
    switch (paramInt) {
      case 3:
        return ASN1BitString.fromInputStream(paramDefiniteLengthInputStream.getRemaining(), paramDefiniteLengthInputStream);
      case 30:
        return new DERBMPString(getBMPCharBuffer(paramDefiniteLengthInputStream));
      case 1:
        return ASN1Boolean.fromOctetString(getBuffer(paramDefiniteLengthInputStream, paramArrayOfbyte));
      case 10:
        return ASN1Enumerated.fromOctetString(getBuffer(paramDefiniteLengthInputStream, paramArrayOfbyte));
      case 24:
        return new ASN1GeneralizedTime(paramDefiniteLengthInputStream.toByteArray());
      case 27:
        return new DERGeneralString(paramDefiniteLengthInputStream.toByteArray());
      case 22:
        return new DERIA5String(paramDefiniteLengthInputStream.toByteArray());
      case 2:
        return new ASN1Integer(paramDefiniteLengthInputStream.toByteArray(), false);
      case 5:
        return DERNull.INSTANCE;
      case 18:
        return new DERNumericString(paramDefiniteLengthInputStream.toByteArray());
      case 6:
        return ASN1ObjectIdentifier.fromOctetString(getBuffer(paramDefiniteLengthInputStream, paramArrayOfbyte));
      case 4:
        return new DEROctetString(paramDefiniteLengthInputStream.toByteArray());
      case 19:
        return new DERPrintableString(paramDefiniteLengthInputStream.toByteArray());
      case 20:
        return new DERT61String(paramDefiniteLengthInputStream.toByteArray());
      case 28:
        return new DERUniversalString(paramDefiniteLengthInputStream.toByteArray());
      case 23:
        return new ASN1UTCTime(paramDefiniteLengthInputStream.toByteArray());
      case 12:
        return new DERUTF8String(paramDefiniteLengthInputStream.toByteArray());
      case 26:
        return new DERVisibleString(paramDefiniteLengthInputStream.toByteArray());
      case 25:
        return new DERGraphicString(paramDefiniteLengthInputStream.toByteArray());
      case 21:
        return new DERVideotexString(paramDefiniteLengthInputStream.toByteArray());
    } 
    throw new IOException("unknown tag " + paramInt + " encountered");
  }
}
