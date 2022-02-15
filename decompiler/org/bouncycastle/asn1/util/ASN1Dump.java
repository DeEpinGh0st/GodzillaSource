package org.bouncycastle.asn1.util;

import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ApplicationSpecific;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERExternal;
import org.bouncycastle.asn1.DERGraphicString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERT61String;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.DERVideotexString;
import org.bouncycastle.asn1.DERVisibleString;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public class ASN1Dump {
  private static final String TAB = "    ";
  
  private static final int SAMPLE_SIZE = 32;
  
  static void _dumpAsString(String paramString, boolean paramBoolean, ASN1Primitive paramASN1Primitive, StringBuffer paramStringBuffer) {
    String str = Strings.lineSeparator();
    if (paramASN1Primitive instanceof ASN1Sequence) {
      Enumeration<Object> enumeration = ((ASN1Sequence)paramASN1Primitive).getObjects();
      String str1 = paramString + "    ";
      paramStringBuffer.append(paramString);
      if (paramASN1Primitive instanceof org.bouncycastle.asn1.BERSequence) {
        paramStringBuffer.append("BER Sequence");
      } else if (paramASN1Primitive instanceof org.bouncycastle.asn1.DERSequence) {
        paramStringBuffer.append("DER Sequence");
      } else {
        paramStringBuffer.append("Sequence");
      } 
      paramStringBuffer.append(str);
      while (enumeration.hasMoreElements()) {
        ASN1Primitive aSN1Primitive = (ASN1Primitive)enumeration.nextElement();
        if (aSN1Primitive == null || aSN1Primitive.equals(DERNull.INSTANCE)) {
          paramStringBuffer.append(str1);
          paramStringBuffer.append("NULL");
          paramStringBuffer.append(str);
          continue;
        } 
        if (aSN1Primitive instanceof ASN1Primitive) {
          _dumpAsString(str1, paramBoolean, aSN1Primitive, paramStringBuffer);
          continue;
        } 
        _dumpAsString(str1, paramBoolean, ((ASN1Encodable)aSN1Primitive).toASN1Primitive(), paramStringBuffer);
      } 
    } else if (paramASN1Primitive instanceof ASN1TaggedObject) {
      String str1 = paramString + "    ";
      paramStringBuffer.append(paramString);
      if (paramASN1Primitive instanceof org.bouncycastle.asn1.BERTaggedObject) {
        paramStringBuffer.append("BER Tagged [");
      } else {
        paramStringBuffer.append("Tagged [");
      } 
      ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)paramASN1Primitive;
      paramStringBuffer.append(Integer.toString(aSN1TaggedObject.getTagNo()));
      paramStringBuffer.append(']');
      if (!aSN1TaggedObject.isExplicit())
        paramStringBuffer.append(" IMPLICIT "); 
      paramStringBuffer.append(str);
      if (aSN1TaggedObject.isEmpty()) {
        paramStringBuffer.append(str1);
        paramStringBuffer.append("EMPTY");
        paramStringBuffer.append(str);
      } else {
        _dumpAsString(str1, paramBoolean, aSN1TaggedObject.getObject(), paramStringBuffer);
      } 
    } else if (paramASN1Primitive instanceof ASN1Set) {
      Enumeration<Object> enumeration = ((ASN1Set)paramASN1Primitive).getObjects();
      String str1 = paramString + "    ";
      paramStringBuffer.append(paramString);
      if (paramASN1Primitive instanceof org.bouncycastle.asn1.BERSet) {
        paramStringBuffer.append("BER Set");
      } else {
        paramStringBuffer.append("DER Set");
      } 
      paramStringBuffer.append(str);
      while (enumeration.hasMoreElements()) {
        ASN1Primitive aSN1Primitive = (ASN1Primitive)enumeration.nextElement();
        if (aSN1Primitive == null) {
          paramStringBuffer.append(str1);
          paramStringBuffer.append("NULL");
          paramStringBuffer.append(str);
          continue;
        } 
        if (aSN1Primitive instanceof ASN1Primitive) {
          _dumpAsString(str1, paramBoolean, aSN1Primitive, paramStringBuffer);
          continue;
        } 
        _dumpAsString(str1, paramBoolean, ((ASN1Encodable)aSN1Primitive).toASN1Primitive(), paramStringBuffer);
      } 
    } else if (paramASN1Primitive instanceof ASN1OctetString) {
      ASN1OctetString aSN1OctetString = (ASN1OctetString)paramASN1Primitive;
      if (paramASN1Primitive instanceof org.bouncycastle.asn1.BEROctetString) {
        paramStringBuffer.append(paramString + "BER Constructed Octet String" + "[" + (aSN1OctetString.getOctets()).length + "] ");
      } else {
        paramStringBuffer.append(paramString + "DER Octet String" + "[" + (aSN1OctetString.getOctets()).length + "] ");
      } 
      if (paramBoolean) {
        paramStringBuffer.append(dumpBinaryDataAsString(paramString, aSN1OctetString.getOctets()));
      } else {
        paramStringBuffer.append(str);
      } 
    } else if (paramASN1Primitive instanceof ASN1ObjectIdentifier) {
      paramStringBuffer.append(paramString + "ObjectIdentifier(" + ((ASN1ObjectIdentifier)paramASN1Primitive).getId() + ")" + str);
    } else if (paramASN1Primitive instanceof ASN1Boolean) {
      paramStringBuffer.append(paramString + "Boolean(" + ((ASN1Boolean)paramASN1Primitive).isTrue() + ")" + str);
    } else if (paramASN1Primitive instanceof ASN1Integer) {
      paramStringBuffer.append(paramString + "Integer(" + ((ASN1Integer)paramASN1Primitive).getValue() + ")" + str);
    } else if (paramASN1Primitive instanceof DERBitString) {
      DERBitString dERBitString = (DERBitString)paramASN1Primitive;
      paramStringBuffer.append(paramString + "DER Bit String" + "[" + (dERBitString.getBytes()).length + ", " + dERBitString.getPadBits() + "] ");
      if (paramBoolean) {
        paramStringBuffer.append(dumpBinaryDataAsString(paramString, dERBitString.getBytes()));
      } else {
        paramStringBuffer.append(str);
      } 
    } else if (paramASN1Primitive instanceof DERIA5String) {
      paramStringBuffer.append(paramString + "IA5String(" + ((DERIA5String)paramASN1Primitive).getString() + ") " + str);
    } else if (paramASN1Primitive instanceof DERUTF8String) {
      paramStringBuffer.append(paramString + "UTF8String(" + ((DERUTF8String)paramASN1Primitive).getString() + ") " + str);
    } else if (paramASN1Primitive instanceof DERPrintableString) {
      paramStringBuffer.append(paramString + "PrintableString(" + ((DERPrintableString)paramASN1Primitive).getString() + ") " + str);
    } else if (paramASN1Primitive instanceof DERVisibleString) {
      paramStringBuffer.append(paramString + "VisibleString(" + ((DERVisibleString)paramASN1Primitive).getString() + ") " + str);
    } else if (paramASN1Primitive instanceof DERBMPString) {
      paramStringBuffer.append(paramString + "BMPString(" + ((DERBMPString)paramASN1Primitive).getString() + ") " + str);
    } else if (paramASN1Primitive instanceof DERT61String) {
      paramStringBuffer.append(paramString + "T61String(" + ((DERT61String)paramASN1Primitive).getString() + ") " + str);
    } else if (paramASN1Primitive instanceof DERGraphicString) {
      paramStringBuffer.append(paramString + "GraphicString(" + ((DERGraphicString)paramASN1Primitive).getString() + ") " + str);
    } else if (paramASN1Primitive instanceof DERVideotexString) {
      paramStringBuffer.append(paramString + "VideotexString(" + ((DERVideotexString)paramASN1Primitive).getString() + ") " + str);
    } else if (paramASN1Primitive instanceof ASN1UTCTime) {
      paramStringBuffer.append(paramString + "UTCTime(" + ((ASN1UTCTime)paramASN1Primitive).getTime() + ") " + str);
    } else if (paramASN1Primitive instanceof ASN1GeneralizedTime) {
      paramStringBuffer.append(paramString + "GeneralizedTime(" + ((ASN1GeneralizedTime)paramASN1Primitive).getTime() + ") " + str);
    } else if (paramASN1Primitive instanceof org.bouncycastle.asn1.BERApplicationSpecific) {
      paramStringBuffer.append(outputApplicationSpecific("BER", paramString, paramBoolean, paramASN1Primitive, str));
    } else if (paramASN1Primitive instanceof org.bouncycastle.asn1.DERApplicationSpecific) {
      paramStringBuffer.append(outputApplicationSpecific("DER", paramString, paramBoolean, paramASN1Primitive, str));
    } else if (paramASN1Primitive instanceof ASN1Enumerated) {
      ASN1Enumerated aSN1Enumerated = (ASN1Enumerated)paramASN1Primitive;
      paramStringBuffer.append(paramString + "DER Enumerated(" + aSN1Enumerated.getValue() + ")" + str);
    } else if (paramASN1Primitive instanceof DERExternal) {
      DERExternal dERExternal = (DERExternal)paramASN1Primitive;
      paramStringBuffer.append(paramString + "External " + str);
      String str1 = paramString + "    ";
      if (dERExternal.getDirectReference() != null)
        paramStringBuffer.append(str1 + "Direct Reference: " + dERExternal.getDirectReference().getId() + str); 
      if (dERExternal.getIndirectReference() != null)
        paramStringBuffer.append(str1 + "Indirect Reference: " + dERExternal.getIndirectReference().toString() + str); 
      if (dERExternal.getDataValueDescriptor() != null)
        _dumpAsString(str1, paramBoolean, dERExternal.getDataValueDescriptor(), paramStringBuffer); 
      paramStringBuffer.append(str1 + "Encoding: " + dERExternal.getEncoding() + str);
      _dumpAsString(str1, paramBoolean, dERExternal.getExternalContent(), paramStringBuffer);
    } else {
      paramStringBuffer.append(paramString + paramASN1Primitive.toString() + str);
    } 
  }
  
  private static String outputApplicationSpecific(String paramString1, String paramString2, boolean paramBoolean, ASN1Primitive paramASN1Primitive, String paramString3) {
    ASN1ApplicationSpecific aSN1ApplicationSpecific = ASN1ApplicationSpecific.getInstance(paramASN1Primitive);
    StringBuffer stringBuffer = new StringBuffer();
    if (aSN1ApplicationSpecific.isConstructed()) {
      try {
        ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(aSN1ApplicationSpecific.getObject(16));
        stringBuffer.append(paramString2 + paramString1 + " ApplicationSpecific[" + aSN1ApplicationSpecific.getApplicationTag() + "]" + paramString3);
        Enumeration<ASN1Primitive> enumeration = aSN1Sequence.getObjects();
        while (enumeration.hasMoreElements())
          _dumpAsString(paramString2 + "    ", paramBoolean, enumeration.nextElement(), stringBuffer); 
      } catch (IOException iOException) {
        stringBuffer.append(iOException);
      } 
      return stringBuffer.toString();
    } 
    return paramString2 + paramString1 + " ApplicationSpecific[" + aSN1ApplicationSpecific.getApplicationTag() + "] (" + Strings.fromByteArray(Hex.encode(aSN1ApplicationSpecific.getContents())) + ")" + paramString3;
  }
  
  public static String dumpAsString(Object paramObject) {
    return dumpAsString(paramObject, false);
  }
  
  public static String dumpAsString(Object paramObject, boolean paramBoolean) {
    StringBuffer stringBuffer = new StringBuffer();
    if (paramObject instanceof ASN1Primitive) {
      _dumpAsString("", paramBoolean, (ASN1Primitive)paramObject, stringBuffer);
    } else if (paramObject instanceof ASN1Encodable) {
      _dumpAsString("", paramBoolean, ((ASN1Encodable)paramObject).toASN1Primitive(), stringBuffer);
    } else {
      return "unknown object type " + paramObject.toString();
    } 
    return stringBuffer.toString();
  }
  
  private static String dumpBinaryDataAsString(String paramString, byte[] paramArrayOfbyte) {
    String str = Strings.lineSeparator();
    StringBuffer stringBuffer = new StringBuffer();
    paramString = paramString + "    ";
    stringBuffer.append(str);
    for (byte b = 0; b < paramArrayOfbyte.length; b += 32) {
      if (paramArrayOfbyte.length - b > 32) {
        stringBuffer.append(paramString);
        stringBuffer.append(Strings.fromByteArray(Hex.encode(paramArrayOfbyte, b, 32)));
        stringBuffer.append("    ");
        stringBuffer.append(calculateAscString(paramArrayOfbyte, b, 32));
        stringBuffer.append(str);
      } else {
        stringBuffer.append(paramString);
        stringBuffer.append(Strings.fromByteArray(Hex.encode(paramArrayOfbyte, b, paramArrayOfbyte.length - b)));
        for (int i = paramArrayOfbyte.length - b; i != 32; i++)
          stringBuffer.append("  "); 
        stringBuffer.append("    ");
        stringBuffer.append(calculateAscString(paramArrayOfbyte, b, paramArrayOfbyte.length - b));
        stringBuffer.append(str);
      } 
    } 
    return stringBuffer.toString();
  }
  
  private static String calculateAscString(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    StringBuffer stringBuffer = new StringBuffer();
    for (int i = paramInt1; i != paramInt1 + paramInt2; i++) {
      if (paramArrayOfbyte[i] >= 32 && paramArrayOfbyte[i] <= 126)
        stringBuffer.append((char)paramArrayOfbyte[i]); 
    } 
    return stringBuffer.toString();
  }
}
