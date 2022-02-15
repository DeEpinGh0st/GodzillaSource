package org.bouncycastle.asn1.x509;

import java.io.IOException;
import java.util.StringTokenizer;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.util.IPAddress;

public class GeneralName extends ASN1Object implements ASN1Choice {
  public static final int otherName = 0;
  
  public static final int rfc822Name = 1;
  
  public static final int dNSName = 2;
  
  public static final int x400Address = 3;
  
  public static final int directoryName = 4;
  
  public static final int ediPartyName = 5;
  
  public static final int uniformResourceIdentifier = 6;
  
  public static final int iPAddress = 7;
  
  public static final int registeredID = 8;
  
  private ASN1Encodable obj;
  
  private int tag;
  
  public GeneralName(X509Name paramX509Name) {
    this.obj = (ASN1Encodable)X500Name.getInstance(paramX509Name);
    this.tag = 4;
  }
  
  public GeneralName(X500Name paramX500Name) {
    this.obj = (ASN1Encodable)paramX500Name;
    this.tag = 4;
  }
  
  public GeneralName(int paramInt, ASN1Encodable paramASN1Encodable) {
    this.obj = paramASN1Encodable;
    this.tag = paramInt;
  }
  
  public GeneralName(int paramInt, String paramString) {
    this.tag = paramInt;
    if (paramInt == 1 || paramInt == 2 || paramInt == 6) {
      this.obj = (ASN1Encodable)new DERIA5String(paramString);
    } else if (paramInt == 8) {
      this.obj = (ASN1Encodable)new ASN1ObjectIdentifier(paramString);
    } else if (paramInt == 4) {
      this.obj = (ASN1Encodable)new X500Name(paramString);
    } else if (paramInt == 7) {
      byte[] arrayOfByte = toGeneralNameEncoding(paramString);
      if (arrayOfByte != null) {
        this.obj = (ASN1Encodable)new DEROctetString(arrayOfByte);
      } else {
        throw new IllegalArgumentException("IP Address is invalid");
      } 
    } else {
      throw new IllegalArgumentException("can't process String for tag: " + paramInt);
    } 
  }
  
  public static GeneralName getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof GeneralName)
      return (GeneralName)paramObject; 
    if (paramObject instanceof ASN1TaggedObject) {
      ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)paramObject;
      int i = aSN1TaggedObject.getTagNo();
      switch (i) {
        case 0:
          return new GeneralName(i, (ASN1Encodable)ASN1Sequence.getInstance(aSN1TaggedObject, false));
        case 1:
          return new GeneralName(i, (ASN1Encodable)DERIA5String.getInstance(aSN1TaggedObject, false));
        case 2:
          return new GeneralName(i, (ASN1Encodable)DERIA5String.getInstance(aSN1TaggedObject, false));
        case 3:
          throw new IllegalArgumentException("unknown tag: " + i);
        case 4:
          return new GeneralName(i, (ASN1Encodable)X500Name.getInstance(aSN1TaggedObject, true));
        case 5:
          return new GeneralName(i, (ASN1Encodable)ASN1Sequence.getInstance(aSN1TaggedObject, false));
        case 6:
          return new GeneralName(i, (ASN1Encodable)DERIA5String.getInstance(aSN1TaggedObject, false));
        case 7:
          return new GeneralName(i, (ASN1Encodable)ASN1OctetString.getInstance(aSN1TaggedObject, false));
        case 8:
          return new GeneralName(i, (ASN1Encodable)ASN1ObjectIdentifier.getInstance(aSN1TaggedObject, false));
      } 
    } 
    if (paramObject instanceof byte[])
      try {
        return getInstance(ASN1Primitive.fromByteArray((byte[])paramObject));
      } catch (IOException iOException) {
        throw new IllegalArgumentException("unable to parse encoded general name");
      }  
    throw new IllegalArgumentException("unknown object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static GeneralName getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(ASN1TaggedObject.getInstance(paramASN1TaggedObject, true));
  }
  
  public int getTagNo() {
    return this.tag;
  }
  
  public ASN1Encodable getName() {
    return this.obj;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(this.tag);
    stringBuffer.append(": ");
    switch (this.tag) {
      case 1:
      case 2:
      case 6:
        stringBuffer.append(DERIA5String.getInstance(this.obj).getString());
        return stringBuffer.toString();
      case 4:
        stringBuffer.append(X500Name.getInstance(this.obj).toString());
        return stringBuffer.toString();
    } 
    stringBuffer.append(this.obj.toString());
    return stringBuffer.toString();
  }
  
  private byte[] toGeneralNameEncoding(String paramString) {
    if (IPAddress.isValidIPv6WithNetmask(paramString) || IPAddress.isValidIPv6(paramString)) {
      int i = paramString.indexOf('/');
      if (i < 0) {
        byte[] arrayOfByte1 = new byte[16];
        int[] arrayOfInt1 = parseIPv6(paramString);
        copyInts(arrayOfInt1, arrayOfByte1, 0);
        return arrayOfByte1;
      } 
      byte[] arrayOfByte = new byte[32];
      int[] arrayOfInt = parseIPv6(paramString.substring(0, i));
      copyInts(arrayOfInt, arrayOfByte, 0);
      String str = paramString.substring(i + 1);
      if (str.indexOf(':') > 0) {
        arrayOfInt = parseIPv6(str);
      } else {
        arrayOfInt = parseMask(str);
      } 
      copyInts(arrayOfInt, arrayOfByte, 16);
      return arrayOfByte;
    } 
    if (IPAddress.isValidIPv4WithNetmask(paramString) || IPAddress.isValidIPv4(paramString)) {
      int i = paramString.indexOf('/');
      if (i < 0) {
        byte[] arrayOfByte1 = new byte[4];
        parseIPv4(paramString, arrayOfByte1, 0);
        return arrayOfByte1;
      } 
      byte[] arrayOfByte = new byte[8];
      parseIPv4(paramString.substring(0, i), arrayOfByte, 0);
      String str = paramString.substring(i + 1);
      if (str.indexOf('.') > 0) {
        parseIPv4(str, arrayOfByte, 4);
      } else {
        parseIPv4Mask(str, arrayOfByte, 4);
      } 
      return arrayOfByte;
    } 
    return null;
  }
  
  private void parseIPv4Mask(String paramString, byte[] paramArrayOfbyte, int paramInt) {
    int i = Integer.parseInt(paramString);
    for (int j = 0; j != i; j++)
      paramArrayOfbyte[j / 8 + paramInt] = (byte)(paramArrayOfbyte[j / 8 + paramInt] | 1 << 7 - j % 8); 
  }
  
  private void parseIPv4(String paramString, byte[] paramArrayOfbyte, int paramInt) {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, "./");
    byte b = 0;
    while (stringTokenizer.hasMoreTokens())
      paramArrayOfbyte[paramInt + b++] = (byte)Integer.parseInt(stringTokenizer.nextToken()); 
  }
  
  private int[] parseMask(String paramString) {
    int[] arrayOfInt = new int[8];
    int i = Integer.parseInt(paramString);
    for (int j = 0; j != i; j++)
      arrayOfInt[j / 16] = arrayOfInt[j / 16] | 1 << 15 - j % 16; 
    return arrayOfInt;
  }
  
  private void copyInts(int[] paramArrayOfint, byte[] paramArrayOfbyte, int paramInt) {
    for (byte b = 0; b != paramArrayOfint.length; b++) {
      paramArrayOfbyte[b * 2 + paramInt] = (byte)(paramArrayOfint[b] >> 8);
      paramArrayOfbyte[b * 2 + 1 + paramInt] = (byte)paramArrayOfint[b];
    } 
  }
  
  private int[] parseIPv6(String paramString) {
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, ":", true);
    byte b = 0;
    int[] arrayOfInt = new int[8];
    if (paramString.charAt(0) == ':' && paramString.charAt(1) == ':')
      stringTokenizer.nextToken(); 
    byte b1 = -1;
    while (stringTokenizer.hasMoreTokens()) {
      String str = stringTokenizer.nextToken();
      if (str.equals(":")) {
        b1 = b;
        arrayOfInt[b++] = 0;
        continue;
      } 
      if (str.indexOf('.') < 0) {
        arrayOfInt[b++] = Integer.parseInt(str, 16);
        if (stringTokenizer.hasMoreTokens())
          stringTokenizer.nextToken(); 
        continue;
      } 
      StringTokenizer stringTokenizer1 = new StringTokenizer(str, ".");
      arrayOfInt[b++] = Integer.parseInt(stringTokenizer1.nextToken()) << 8 | Integer.parseInt(stringTokenizer1.nextToken());
      arrayOfInt[b++] = Integer.parseInt(stringTokenizer1.nextToken()) << 8 | Integer.parseInt(stringTokenizer1.nextToken());
    } 
    if (b != arrayOfInt.length) {
      System.arraycopy(arrayOfInt, b1, arrayOfInt, arrayOfInt.length - b - b1, b - b1);
      for (byte b2 = b1; b2 != arrayOfInt.length - b - b1; b2++)
        arrayOfInt[b2] = 0; 
    } 
    return arrayOfInt;
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)((this.tag == 4) ? new DERTaggedObject(true, this.tag, this.obj) : new DERTaggedObject(false, this.tag, this.obj));
  }
}
