package org.bouncycastle.asn1.x500.style;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.X500NameStyle;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class IETFUtils {
  private static String unescape(String paramString) {
    if (paramString.length() == 0 || (paramString.indexOf('\\') < 0 && paramString.indexOf('"') < 0))
      return paramString.trim(); 
    char[] arrayOfChar = paramString.toCharArray();
    boolean bool1 = false;
    boolean bool2 = false;
    StringBuffer stringBuffer = new StringBuffer(paramString.length());
    byte b1 = 0;
    if (arrayOfChar[0] == '\\' && arrayOfChar[1] == '#') {
      b1 = 2;
      stringBuffer.append("\\#");
    } 
    boolean bool3 = false;
    int i = 0;
    char c = Character.MIN_VALUE;
    for (byte b2 = b1; b2 != arrayOfChar.length; b2++) {
      char c1 = arrayOfChar[b2];
      if (c1 != ' ')
        bool3 = true; 
      if (c1 == '"') {
        if (!bool1) {
          bool2 = !bool2 ? true : false;
        } else {
          stringBuffer.append(c1);
        } 
        bool1 = false;
      } else if (c1 == '\\' && !bool1 && !bool2) {
        bool1 = true;
        i = stringBuffer.length();
      } else if (c1 != ' ' || bool1 || bool3) {
        if (bool1 && isHexDigit(c1)) {
          if (c) {
            stringBuffer.append((char)(convertHex(c) * 16 + convertHex(c1)));
            bool1 = false;
            c = Character.MIN_VALUE;
          } else {
            c = c1;
          } 
        } else {
          stringBuffer.append(c1);
          bool1 = false;
        } 
      } 
    } 
    if (stringBuffer.length() > 0)
      while (stringBuffer.charAt(stringBuffer.length() - 1) == ' ' && i != stringBuffer.length() - 1)
        stringBuffer.setLength(stringBuffer.length() - 1);  
    return stringBuffer.toString();
  }
  
  private static boolean isHexDigit(char paramChar) {
    return (('0' <= paramChar && paramChar <= '9') || ('a' <= paramChar && paramChar <= 'f') || ('A' <= paramChar && paramChar <= 'F'));
  }
  
  private static int convertHex(char paramChar) {
    return ('0' <= paramChar && paramChar <= '9') ? (paramChar - 48) : (('a' <= paramChar && paramChar <= 'f') ? (paramChar - 97 + 10) : (paramChar - 65 + 10));
  }
  
  public static RDN[] rDNsFromString(String paramString, X500NameStyle paramX500NameStyle) {
    X500NameTokenizer x500NameTokenizer = new X500NameTokenizer(paramString);
    X500NameBuilder x500NameBuilder = new X500NameBuilder(paramX500NameStyle);
    while (x500NameTokenizer.hasMoreTokens()) {
      String str1 = x500NameTokenizer.nextToken();
      if (str1.indexOf('+') > 0) {
        X500NameTokenizer x500NameTokenizer2 = new X500NameTokenizer(str1, '+');
        X500NameTokenizer x500NameTokenizer3 = new X500NameTokenizer(x500NameTokenizer2.nextToken(), '=');
        String str4 = x500NameTokenizer3.nextToken();
        if (!x500NameTokenizer3.hasMoreTokens())
          throw new IllegalArgumentException("badly formatted directory string"); 
        String str5 = x500NameTokenizer3.nextToken();
        ASN1ObjectIdentifier aSN1ObjectIdentifier1 = paramX500NameStyle.attrNameToOID(str4.trim());
        if (x500NameTokenizer2.hasMoreTokens()) {
          Vector<ASN1ObjectIdentifier> vector = new Vector();
          Vector<String> vector1 = new Vector();
          vector.addElement(aSN1ObjectIdentifier1);
          vector1.addElement(unescape(str5));
          while (x500NameTokenizer2.hasMoreTokens()) {
            x500NameTokenizer3 = new X500NameTokenizer(x500NameTokenizer2.nextToken(), '=');
            str4 = x500NameTokenizer3.nextToken();
            if (!x500NameTokenizer3.hasMoreTokens())
              throw new IllegalArgumentException("badly formatted directory string"); 
            str5 = x500NameTokenizer3.nextToken();
            aSN1ObjectIdentifier1 = paramX500NameStyle.attrNameToOID(str4.trim());
            vector.addElement(aSN1ObjectIdentifier1);
            vector1.addElement(unescape(str5));
          } 
          x500NameBuilder.addMultiValuedRDN(toOIDArray(vector), toValueArray(vector1));
          continue;
        } 
        x500NameBuilder.addRDN(aSN1ObjectIdentifier1, unescape(str5));
        continue;
      } 
      X500NameTokenizer x500NameTokenizer1 = new X500NameTokenizer(str1, '=');
      String str2 = x500NameTokenizer1.nextToken();
      if (!x500NameTokenizer1.hasMoreTokens())
        throw new IllegalArgumentException("badly formatted directory string"); 
      String str3 = x500NameTokenizer1.nextToken();
      ASN1ObjectIdentifier aSN1ObjectIdentifier = paramX500NameStyle.attrNameToOID(str2.trim());
      x500NameBuilder.addRDN(aSN1ObjectIdentifier, unescape(str3));
    } 
    return x500NameBuilder.build().getRDNs();
  }
  
  private static String[] toValueArray(Vector<String> paramVector) {
    String[] arrayOfString = new String[paramVector.size()];
    for (byte b = 0; b != arrayOfString.length; b++)
      arrayOfString[b] = paramVector.elementAt(b); 
    return arrayOfString;
  }
  
  private static ASN1ObjectIdentifier[] toOIDArray(Vector<ASN1ObjectIdentifier> paramVector) {
    ASN1ObjectIdentifier[] arrayOfASN1ObjectIdentifier = new ASN1ObjectIdentifier[paramVector.size()];
    for (byte b = 0; b != arrayOfASN1ObjectIdentifier.length; b++)
      arrayOfASN1ObjectIdentifier[b] = paramVector.elementAt(b); 
    return arrayOfASN1ObjectIdentifier;
  }
  
  public static String[] findAttrNamesForOID(ASN1ObjectIdentifier paramASN1ObjectIdentifier, Hashtable paramHashtable) {
    byte b = 0;
    Enumeration enumeration = paramHashtable.elements();
    while (enumeration.hasMoreElements()) {
      if (paramASN1ObjectIdentifier.equals(enumeration.nextElement()))
        b++; 
    } 
    String[] arrayOfString = new String[b];
    b = 0;
    Enumeration<String> enumeration1 = paramHashtable.keys();
    while (enumeration1.hasMoreElements()) {
      String str = enumeration1.nextElement();
      if (paramASN1ObjectIdentifier.equals(paramHashtable.get(str)))
        arrayOfString[b++] = str; 
    } 
    return arrayOfString;
  }
  
  public static ASN1ObjectIdentifier decodeAttrName(String paramString, Hashtable paramHashtable) {
    if (Strings.toUpperCase(paramString).startsWith("OID."))
      return new ASN1ObjectIdentifier(paramString.substring(4)); 
    if (paramString.charAt(0) >= '0' && paramString.charAt(0) <= '9')
      return new ASN1ObjectIdentifier(paramString); 
    ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)paramHashtable.get(Strings.toLowerCase(paramString));
    if (aSN1ObjectIdentifier == null)
      throw new IllegalArgumentException("Unknown object id - " + paramString + " - passed to distinguished name"); 
    return aSN1ObjectIdentifier;
  }
  
  public static ASN1Encodable valueFromHexString(String paramString, int paramInt) throws IOException {
    byte[] arrayOfByte = new byte[(paramString.length() - paramInt) / 2];
    for (byte b = 0; b != arrayOfByte.length; b++) {
      char c1 = paramString.charAt(b * 2 + paramInt);
      char c2 = paramString.charAt(b * 2 + paramInt + 1);
      arrayOfByte[b] = (byte)(convertHex(c1) << 4 | convertHex(c2));
    } 
    return (ASN1Encodable)ASN1Primitive.fromByteArray(arrayOfByte);
  }
  
  public static void appendRDN(StringBuffer paramStringBuffer, RDN paramRDN, Hashtable paramHashtable) {
    if (paramRDN.isMultiValued()) {
      AttributeTypeAndValue[] arrayOfAttributeTypeAndValue = paramRDN.getTypesAndValues();
      boolean bool = true;
      for (byte b = 0; b != arrayOfAttributeTypeAndValue.length; b++) {
        if (bool) {
          bool = false;
        } else {
          paramStringBuffer.append('+');
        } 
        appendTypeAndValue(paramStringBuffer, arrayOfAttributeTypeAndValue[b], paramHashtable);
      } 
    } else if (paramRDN.getFirst() != null) {
      appendTypeAndValue(paramStringBuffer, paramRDN.getFirst(), paramHashtable);
    } 
  }
  
  public static void appendTypeAndValue(StringBuffer paramStringBuffer, AttributeTypeAndValue paramAttributeTypeAndValue, Hashtable paramHashtable) {
    String str = (String)paramHashtable.get(paramAttributeTypeAndValue.getType());
    if (str != null) {
      paramStringBuffer.append(str);
    } else {
      paramStringBuffer.append(paramAttributeTypeAndValue.getType().getId());
    } 
    paramStringBuffer.append('=');
    paramStringBuffer.append(valueToString(paramAttributeTypeAndValue.getValue()));
  }
  
  public static String valueToString(ASN1Encodable paramASN1Encodable) {
    StringBuffer stringBuffer = new StringBuffer();
    if (paramASN1Encodable instanceof ASN1String && !(paramASN1Encodable instanceof org.bouncycastle.asn1.DERUniversalString)) {
      String str = ((ASN1String)paramASN1Encodable).getString();
      if (str.length() > 0 && str.charAt(0) == '#') {
        stringBuffer.append("\\" + str);
      } else {
        stringBuffer.append(str);
      } 
    } else {
      try {
        stringBuffer.append("#" + bytesToString(Hex.encode(paramASN1Encodable.toASN1Primitive().getEncoded("DER"))));
      } catch (IOException iOException) {
        throw new IllegalArgumentException("Other value has no encoded form");
      } 
    } 
    int i = stringBuffer.length();
    int j = 0;
    if (stringBuffer.length() >= 2 && stringBuffer.charAt(0) == '\\' && stringBuffer.charAt(1) == '#')
      j += true; 
    while (j != i) {
      if (stringBuffer.charAt(j) == ',' || stringBuffer.charAt(j) == '"' || stringBuffer.charAt(j) == '\\' || stringBuffer.charAt(j) == '+' || stringBuffer.charAt(j) == '=' || stringBuffer.charAt(j) == '<' || stringBuffer.charAt(j) == '>' || stringBuffer.charAt(j) == ';') {
        stringBuffer.insert(j, "\\");
        j++;
        i++;
      } 
      j++;
    } 
    byte b = 0;
    if (stringBuffer.length() > 0)
      while (stringBuffer.length() > b && stringBuffer.charAt(b) == ' ') {
        stringBuffer.insert(b, "\\");
        b += 2;
      }  
    for (int k = stringBuffer.length() - 1; k >= 0 && stringBuffer.charAt(k) == ' '; k--)
      stringBuffer.insert(k, '\\'); 
    return stringBuffer.toString();
  }
  
  private static String bytesToString(byte[] paramArrayOfbyte) {
    char[] arrayOfChar = new char[paramArrayOfbyte.length];
    for (byte b = 0; b != arrayOfChar.length; b++)
      arrayOfChar[b] = (char)(paramArrayOfbyte[b] & 0xFF); 
    return new String(arrayOfChar);
  }
  
  public static String canonicalize(String paramString) {
    null = Strings.toLowerCase(paramString);
    if (null.length() > 0 && null.charAt(0) == '#') {
      ASN1Primitive aSN1Primitive = decodeObject(null);
      if (aSN1Primitive instanceof ASN1String)
        null = Strings.toLowerCase(((ASN1String)aSN1Primitive).getString()); 
    } 
    if (null.length() > 1) {
      byte b;
      for (b = 0; b + 1 < null.length() && null.charAt(b) == '\\' && null.charAt(b + 1) == ' '; b += 2);
      int i;
      for (i = null.length() - 1; i - 1 > 0 && null.charAt(i - 1) == '\\' && null.charAt(i) == ' '; i -= 2);
      if (b > 0 || i < null.length() - 1)
        null = null.substring(b, i + 1); 
    } 
    return stripInternalSpaces(null);
  }
  
  private static ASN1Primitive decodeObject(String paramString) {
    try {
      return ASN1Primitive.fromByteArray(Hex.decode(paramString.substring(1)));
    } catch (IOException iOException) {
      throw new IllegalStateException("unknown encoding in name: " + iOException);
    } 
  }
  
  public static String stripInternalSpaces(String paramString) {
    StringBuffer stringBuffer = new StringBuffer();
    if (paramString.length() != 0) {
      char c = paramString.charAt(0);
      stringBuffer.append(c);
      for (byte b = 1; b < paramString.length(); b++) {
        char c1 = paramString.charAt(b);
        if (c != ' ' || c1 != ' ')
          stringBuffer.append(c1); 
        c = c1;
      } 
    } 
    return stringBuffer.toString();
  }
  
  public static boolean rDNAreEqual(RDN paramRDN1, RDN paramRDN2) {
    if (paramRDN1.isMultiValued()) {
      if (paramRDN2.isMultiValued()) {
        AttributeTypeAndValue[] arrayOfAttributeTypeAndValue1 = paramRDN1.getTypesAndValues();
        AttributeTypeAndValue[] arrayOfAttributeTypeAndValue2 = paramRDN2.getTypesAndValues();
        if (arrayOfAttributeTypeAndValue1.length != arrayOfAttributeTypeAndValue2.length)
          return false; 
        for (byte b = 0; b != arrayOfAttributeTypeAndValue1.length; b++) {
          if (!atvAreEqual(arrayOfAttributeTypeAndValue1[b], arrayOfAttributeTypeAndValue2[b]))
            return false; 
        } 
      } else {
        return false;
      } 
    } else {
      return !paramRDN2.isMultiValued() ? atvAreEqual(paramRDN1.getFirst(), paramRDN2.getFirst()) : false;
    } 
    return true;
  }
  
  private static boolean atvAreEqual(AttributeTypeAndValue paramAttributeTypeAndValue1, AttributeTypeAndValue paramAttributeTypeAndValue2) {
    if (paramAttributeTypeAndValue1 == paramAttributeTypeAndValue2)
      return true; 
    if (paramAttributeTypeAndValue1 == null)
      return false; 
    if (paramAttributeTypeAndValue2 == null)
      return false; 
    ASN1ObjectIdentifier aSN1ObjectIdentifier1 = paramAttributeTypeAndValue1.getType();
    ASN1ObjectIdentifier aSN1ObjectIdentifier2 = paramAttributeTypeAndValue2.getType();
    if (!aSN1ObjectIdentifier1.equals(aSN1ObjectIdentifier2))
      return false; 
    String str1 = canonicalize(valueToString(paramAttributeTypeAndValue1.getValue()));
    String str2 = canonicalize(valueToString(paramAttributeTypeAndValue2.getValue()));
    return !!str1.equals(str2);
  }
}
