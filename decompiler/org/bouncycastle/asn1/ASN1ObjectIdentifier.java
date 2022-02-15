package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bouncycastle.util.Arrays;

public class ASN1ObjectIdentifier extends ASN1Primitive {
  private final String identifier;
  
  private byte[] body;
  
  private static final long LONG_LIMIT = 72057594037927808L;
  
  private static final ConcurrentMap<OidHandle, ASN1ObjectIdentifier> pool = new ConcurrentHashMap<OidHandle, ASN1ObjectIdentifier>();
  
  public static ASN1ObjectIdentifier getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof ASN1ObjectIdentifier)
      return (ASN1ObjectIdentifier)paramObject; 
    if (paramObject instanceof ASN1Encodable && ((ASN1Encodable)paramObject).toASN1Primitive() instanceof ASN1ObjectIdentifier)
      return (ASN1ObjectIdentifier)((ASN1Encodable)paramObject).toASN1Primitive(); 
    if (paramObject instanceof byte[]) {
      byte[] arrayOfByte = (byte[])paramObject;
      try {
        return (ASN1ObjectIdentifier)fromByteArray(arrayOfByte);
      } catch (IOException iOException) {
        throw new IllegalArgumentException("failed to construct object identifier from byte[]: " + iOException.getMessage());
      } 
    } 
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static ASN1ObjectIdentifier getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    ASN1Primitive aSN1Primitive = paramASN1TaggedObject.getObject();
    return (paramBoolean || aSN1Primitive instanceof ASN1ObjectIdentifier) ? getInstance(aSN1Primitive) : fromOctetString(ASN1OctetString.getInstance(paramASN1TaggedObject.getObject()).getOctets());
  }
  
  ASN1ObjectIdentifier(byte[] paramArrayOfbyte) {
    StringBuffer stringBuffer = new StringBuffer();
    long l = 0L;
    BigInteger bigInteger = null;
    boolean bool = true;
    for (byte b = 0; b != paramArrayOfbyte.length; b++) {
      int i = paramArrayOfbyte[b] & 0xFF;
      if (l <= 72057594037927808L) {
        l += (i & 0x7F);
        if ((i & 0x80) == 0) {
          if (bool) {
            if (l < 40L) {
              stringBuffer.append('0');
            } else if (l < 80L) {
              stringBuffer.append('1');
              l -= 40L;
            } else {
              stringBuffer.append('2');
              l -= 80L;
            } 
            bool = false;
          } 
          stringBuffer.append('.');
          stringBuffer.append(l);
          l = 0L;
        } else {
          l <<= 7L;
        } 
      } else {
        if (bigInteger == null)
          bigInteger = BigInteger.valueOf(l); 
        bigInteger = bigInteger.or(BigInteger.valueOf((i & 0x7F)));
        if ((i & 0x80) == 0) {
          if (bool) {
            stringBuffer.append('2');
            bigInteger = bigInteger.subtract(BigInteger.valueOf(80L));
            bool = false;
          } 
          stringBuffer.append('.');
          stringBuffer.append(bigInteger);
          bigInteger = null;
          l = 0L;
        } else {
          bigInteger = bigInteger.shiftLeft(7);
        } 
      } 
    } 
    this.identifier = stringBuffer.toString();
    this.body = Arrays.clone(paramArrayOfbyte);
  }
  
  public ASN1ObjectIdentifier(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("'identifier' cannot be null"); 
    if (!isValidIdentifier(paramString))
      throw new IllegalArgumentException("string " + paramString + " not an OID"); 
    this.identifier = paramString;
  }
  
  ASN1ObjectIdentifier(ASN1ObjectIdentifier paramASN1ObjectIdentifier, String paramString) {
    if (!isValidBranchID(paramString, 0))
      throw new IllegalArgumentException("string " + paramString + " not a valid OID branch"); 
    this.identifier = paramASN1ObjectIdentifier.getId() + "." + paramString;
  }
  
  public String getId() {
    return this.identifier;
  }
  
  public ASN1ObjectIdentifier branch(String paramString) {
    return new ASN1ObjectIdentifier(this, paramString);
  }
  
  public boolean on(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    String str1 = getId();
    String str2 = paramASN1ObjectIdentifier.getId();
    return (str1.length() > str2.length() && str1.charAt(str2.length()) == '.' && str1.startsWith(str2));
  }
  
  private void writeField(ByteArrayOutputStream paramByteArrayOutputStream, long paramLong) {
    byte[] arrayOfByte = new byte[9];
    byte b = 8;
    arrayOfByte[b] = (byte)((int)paramLong & 0x7F);
    while (paramLong >= 128L) {
      paramLong >>= 7L;
      arrayOfByte[--b] = (byte)((int)paramLong & 0x7F | 0x80);
    } 
    paramByteArrayOutputStream.write(arrayOfByte, b, 9 - b);
  }
  
  private void writeField(ByteArrayOutputStream paramByteArrayOutputStream, BigInteger paramBigInteger) {
    int i = (paramBigInteger.bitLength() + 6) / 7;
    if (i == 0) {
      paramByteArrayOutputStream.write(0);
    } else {
      BigInteger bigInteger = paramBigInteger;
      byte[] arrayOfByte = new byte[i];
      for (int j = i - 1; j >= 0; j--) {
        arrayOfByte[j] = (byte)(bigInteger.intValue() & 0x7F | 0x80);
        bigInteger = bigInteger.shiftRight(7);
      } 
      arrayOfByte[i - 1] = (byte)(arrayOfByte[i - 1] & Byte.MAX_VALUE);
      paramByteArrayOutputStream.write(arrayOfByte, 0, arrayOfByte.length);
    } 
  }
  
  private void doOutput(ByteArrayOutputStream paramByteArrayOutputStream) {
    OIDTokenizer oIDTokenizer = new OIDTokenizer(this.identifier);
    int i = Integer.parseInt(oIDTokenizer.nextToken()) * 40;
    String str = oIDTokenizer.nextToken();
    if (str.length() <= 18) {
      writeField(paramByteArrayOutputStream, i + Long.parseLong(str));
    } else {
      writeField(paramByteArrayOutputStream, (new BigInteger(str)).add(BigInteger.valueOf(i)));
    } 
    while (oIDTokenizer.hasMoreTokens()) {
      String str1 = oIDTokenizer.nextToken();
      if (str1.length() <= 18) {
        writeField(paramByteArrayOutputStream, Long.parseLong(str1));
        continue;
      } 
      writeField(paramByteArrayOutputStream, new BigInteger(str1));
    } 
  }
  
  private synchronized byte[] getBody() {
    if (this.body == null) {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      doOutput(byteArrayOutputStream);
      this.body = byteArrayOutputStream.toByteArray();
    } 
    return this.body;
  }
  
  boolean isConstructed() {
    return false;
  }
  
  int encodedLength() throws IOException {
    int i = (getBody()).length;
    return 1 + StreamUtil.calculateBodyLength(i) + i;
  }
  
  void encode(ASN1OutputStream paramASN1OutputStream) throws IOException {
    byte[] arrayOfByte = getBody();
    paramASN1OutputStream.write(6);
    paramASN1OutputStream.writeLength(arrayOfByte.length);
    paramASN1OutputStream.write(arrayOfByte);
  }
  
  public int hashCode() {
    return this.identifier.hashCode();
  }
  
  boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    return (paramASN1Primitive == this) ? true : (!(paramASN1Primitive instanceof ASN1ObjectIdentifier) ? false : this.identifier.equals(((ASN1ObjectIdentifier)paramASN1Primitive).identifier));
  }
  
  public String toString() {
    return getId();
  }
  
  private static boolean isValidBranchID(String paramString, int paramInt) {
    boolean bool = false;
    int i = paramString.length();
    while (--i >= paramInt) {
      char c = paramString.charAt(i);
      if ('0' <= c && c <= '9') {
        bool = true;
        continue;
      } 
      if (c == '.') {
        if (!bool)
          return false; 
        bool = false;
        continue;
      } 
      return false;
    } 
    return bool;
  }
  
  private static boolean isValidIdentifier(String paramString) {
    if (paramString.length() < 3 || paramString.charAt(1) != '.')
      return false; 
    char c = paramString.charAt(0);
    return (c < '0' || c > '2') ? false : isValidBranchID(paramString, 2);
  }
  
  public ASN1ObjectIdentifier intern() {
    OidHandle oidHandle = new OidHandle(getBody());
    ASN1ObjectIdentifier aSN1ObjectIdentifier = pool.get(oidHandle);
    if (aSN1ObjectIdentifier == null) {
      aSN1ObjectIdentifier = pool.putIfAbsent(oidHandle, this);
      if (aSN1ObjectIdentifier == null)
        aSN1ObjectIdentifier = this; 
    } 
    return aSN1ObjectIdentifier;
  }
  
  static ASN1ObjectIdentifier fromOctetString(byte[] paramArrayOfbyte) {
    OidHandle oidHandle = new OidHandle(paramArrayOfbyte);
    ASN1ObjectIdentifier aSN1ObjectIdentifier = pool.get(oidHandle);
    return (aSN1ObjectIdentifier == null) ? new ASN1ObjectIdentifier(paramArrayOfbyte) : aSN1ObjectIdentifier;
  }
  
  private static class OidHandle {
    private final int key;
    
    private final byte[] enc;
    
    OidHandle(byte[] param1ArrayOfbyte) {
      this.key = Arrays.hashCode(param1ArrayOfbyte);
      this.enc = param1ArrayOfbyte;
    }
    
    public int hashCode() {
      return this.key;
    }
    
    public boolean equals(Object param1Object) {
      return (param1Object instanceof OidHandle) ? Arrays.areEqual(this.enc, ((OidHandle)param1Object).enc) : false;
    }
  }
}
