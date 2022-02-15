package org.bouncycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ASN1StreamParser {
  private final InputStream _in;
  
  private final int _limit;
  
  private final byte[][] tmpBuffers;
  
  public ASN1StreamParser(InputStream paramInputStream) {
    this(paramInputStream, StreamUtil.findLimit(paramInputStream));
  }
  
  public ASN1StreamParser(InputStream paramInputStream, int paramInt) {
    this._in = paramInputStream;
    this._limit = paramInt;
    this.tmpBuffers = new byte[11][];
  }
  
  public ASN1StreamParser(byte[] paramArrayOfbyte) {
    this(new ByteArrayInputStream(paramArrayOfbyte), paramArrayOfbyte.length);
  }
  
  ASN1Encodable readIndef(int paramInt) throws IOException {
    switch (paramInt) {
      case 8:
        return new DERExternalParser(this);
      case 4:
        return new BEROctetStringParser(this);
      case 16:
        return new BERSequenceParser(this);
      case 17:
        return new BERSetParser(this);
    } 
    throw new ASN1Exception("unknown BER object encountered: 0x" + Integer.toHexString(paramInt));
  }
  
  ASN1Encodable readImplicit(boolean paramBoolean, int paramInt) throws IOException {
    if (this._in instanceof IndefiniteLengthInputStream) {
      if (!paramBoolean)
        throw new IOException("indefinite-length primitive encoding encountered"); 
      return readIndef(paramInt);
    } 
    if (paramBoolean) {
      switch (paramInt) {
        case 17:
          return new DERSetParser(this);
        case 16:
          return new DERSequenceParser(this);
        case 4:
          return new BEROctetStringParser(this);
      } 
    } else {
      switch (paramInt) {
        case 17:
          throw new ASN1Exception("sequences must use constructed encoding (see X.690 8.9.1/8.10.1)");
        case 16:
          throw new ASN1Exception("sets must use constructed encoding (see X.690 8.11.1/8.12.1)");
        case 4:
          return new DEROctetStringParser((DefiniteLengthInputStream)this._in);
      } 
    } 
    throw new ASN1Exception("implicit tagging not implemented");
  }
  
  ASN1Primitive readTaggedObject(boolean paramBoolean, int paramInt) throws IOException {
    if (!paramBoolean) {
      DefiniteLengthInputStream definiteLengthInputStream = (DefiniteLengthInputStream)this._in;
      return new DERTaggedObject(false, paramInt, new DEROctetString(definiteLengthInputStream.toByteArray()));
    } 
    ASN1EncodableVector aSN1EncodableVector = readVector();
    return (this._in instanceof IndefiniteLengthInputStream) ? ((aSN1EncodableVector.size() == 1) ? new BERTaggedObject(true, paramInt, aSN1EncodableVector.get(0)) : new BERTaggedObject(false, paramInt, BERFactory.createSequence(aSN1EncodableVector))) : ((aSN1EncodableVector.size() == 1) ? new DERTaggedObject(true, paramInt, aSN1EncodableVector.get(0)) : new DERTaggedObject(false, paramInt, DERFactory.createSequence(aSN1EncodableVector)));
  }
  
  public ASN1Encodable readObject() throws IOException {
    int i = this._in.read();
    if (i == -1)
      return null; 
    set00Check(false);
    int j = ASN1InputStream.readTagNumber(this._in, i);
    boolean bool = ((i & 0x20) != 0) ? true : false;
    int k = ASN1InputStream.readLength(this._in, this._limit);
    if (k < 0) {
      if (!bool)
        throw new IOException("indefinite-length primitive encoding encountered"); 
      IndefiniteLengthInputStream indefiniteLengthInputStream = new IndefiniteLengthInputStream(this._in, this._limit);
      ASN1StreamParser aSN1StreamParser = new ASN1StreamParser(indefiniteLengthInputStream, this._limit);
      return ((i & 0x40) != 0) ? new BERApplicationSpecificParser(j, aSN1StreamParser) : (((i & 0x80) != 0) ? new BERTaggedObjectParser(true, j, aSN1StreamParser) : aSN1StreamParser.readIndef(j));
    } 
    DefiniteLengthInputStream definiteLengthInputStream = new DefiniteLengthInputStream(this._in, k);
    if ((i & 0x40) != 0)
      return new DERApplicationSpecific(bool, j, definiteLengthInputStream.toByteArray()); 
    if ((i & 0x80) != 0)
      return new BERTaggedObjectParser(bool, j, new ASN1StreamParser(definiteLengthInputStream)); 
    if (bool) {
      switch (j) {
        case 4:
          return new BEROctetStringParser(new ASN1StreamParser(definiteLengthInputStream));
        case 16:
          return new DERSequenceParser(new ASN1StreamParser(definiteLengthInputStream));
        case 17:
          return new DERSetParser(new ASN1StreamParser(definiteLengthInputStream));
        case 8:
          return new DERExternalParser(new ASN1StreamParser(definiteLengthInputStream));
      } 
      throw new IOException("unknown tag " + j + " encountered");
    } 
    switch (j) {
      case 4:
        return new DEROctetStringParser(definiteLengthInputStream);
    } 
    try {
      return ASN1InputStream.createPrimitiveDERObject(j, definiteLengthInputStream, this.tmpBuffers);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new ASN1Exception("corrupted stream detected", illegalArgumentException);
    } 
  }
  
  private void set00Check(boolean paramBoolean) {
    if (this._in instanceof IndefiniteLengthInputStream)
      ((IndefiniteLengthInputStream)this._in).setEofOn00(paramBoolean); 
  }
  
  ASN1EncodableVector readVector() throws IOException {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    ASN1Encodable aSN1Encodable;
    while ((aSN1Encodable = readObject()) != null) {
      if (aSN1Encodable instanceof InMemoryRepresentable) {
        aSN1EncodableVector.add(((InMemoryRepresentable)aSN1Encodable).getLoadedObject());
        continue;
      } 
      aSN1EncodableVector.add(aSN1Encodable.toASN1Primitive());
    } 
    return aSN1EncodableVector;
  }
}
