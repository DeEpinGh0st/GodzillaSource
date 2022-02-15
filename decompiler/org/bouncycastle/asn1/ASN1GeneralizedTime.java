package org.bouncycastle.asn1;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class ASN1GeneralizedTime extends ASN1Primitive {
  private byte[] time;
  
  public static ASN1GeneralizedTime getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof ASN1GeneralizedTime)
      return (ASN1GeneralizedTime)paramObject; 
    if (paramObject instanceof byte[])
      try {
        return (ASN1GeneralizedTime)fromByteArray((byte[])paramObject);
      } catch (Exception exception) {
        throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
      }  
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static ASN1GeneralizedTime getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    ASN1Primitive aSN1Primitive = paramASN1TaggedObject.getObject();
    return (paramBoolean || aSN1Primitive instanceof ASN1GeneralizedTime) ? getInstance(aSN1Primitive) : new ASN1GeneralizedTime(((ASN1OctetString)aSN1Primitive).getOctets());
  }
  
  public ASN1GeneralizedTime(String paramString) {
    this.time = Strings.toByteArray(paramString);
    try {
      getDate();
    } catch (ParseException parseException) {
      throw new IllegalArgumentException("invalid date string: " + parseException.getMessage());
    } 
  }
  
  public ASN1GeneralizedTime(Date paramDate) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
    simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
    this.time = Strings.toByteArray(simpleDateFormat.format(paramDate));
  }
  
  public ASN1GeneralizedTime(Date paramDate, Locale paramLocale) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss'Z'", paramLocale);
    simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
    this.time = Strings.toByteArray(simpleDateFormat.format(paramDate));
  }
  
  ASN1GeneralizedTime(byte[] paramArrayOfbyte) {
    this.time = paramArrayOfbyte;
  }
  
  public String getTimeString() {
    return Strings.fromByteArray(this.time);
  }
  
  public String getTime() {
    String str = Strings.fromByteArray(this.time);
    if (str.charAt(str.length() - 1) == 'Z')
      return str.substring(0, str.length() - 1) + "GMT+00:00"; 
    int i = str.length() - 5;
    char c = str.charAt(i);
    if (c == '-' || c == '+')
      return str.substring(0, i) + "GMT" + str.substring(i, i + 3) + ":" + str.substring(i + 3); 
    i = str.length() - 3;
    c = str.charAt(i);
    return (c == '-' || c == '+') ? (str.substring(0, i) + "GMT" + str.substring(i) + ":00") : (str + calculateGMTOffset());
  }
  
  private String calculateGMTOffset() {
    String str = "+";
    TimeZone timeZone = TimeZone.getDefault();
    int i = timeZone.getRawOffset();
    if (i < 0) {
      str = "-";
      i = -i;
    } 
    int j = i / 3600000;
    int k = (i - j * 60 * 60 * 1000) / 60000;
    try {
      if (timeZone.useDaylightTime() && timeZone.inDaylightTime(getDate()))
        j += str.equals("+") ? 1 : -1; 
    } catch (ParseException parseException) {}
    return "GMT" + str + convert(j) + ":" + convert(k);
  }
  
  private String convert(int paramInt) {
    return (paramInt < 10) ? ("0" + paramInt) : Integer.toString(paramInt);
  }
  
  public Date getDate() throws ParseException {
    SimpleDateFormat simpleDateFormat;
    String str1 = Strings.fromByteArray(this.time);
    String str2 = str1;
    if (str1.endsWith("Z")) {
      if (hasFractionalSeconds()) {
        simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss.SSS'Z'");
      } else {
        simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
      } 
      simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
    } else if (str1.indexOf('-') > 0 || str1.indexOf('+') > 0) {
      str2 = getTime();
      if (hasFractionalSeconds()) {
        simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss.SSSz");
      } else {
        simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssz");
      } 
      simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
    } else {
      if (hasFractionalSeconds()) {
        simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss.SSS");
      } else {
        simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
      } 
      simpleDateFormat.setTimeZone(new SimpleTimeZone(0, TimeZone.getDefault().getID()));
    } 
    if (hasFractionalSeconds()) {
      String str = str2.substring(14);
      byte b;
      for (b = 1; b < str.length(); b++) {
        char c = str.charAt(b);
        if ('0' > c || c > '9')
          break; 
      } 
      if (b - 1 > 3) {
        str = str.substring(0, 4) + str.substring(b);
        str2 = str2.substring(0, 14) + str;
      } else if (b - 1 == 1) {
        str = str.substring(0, b) + "00" + str.substring(b);
        str2 = str2.substring(0, 14) + str;
      } else if (b - 1 == 2) {
        str = str.substring(0, b) + "0" + str.substring(b);
        str2 = str2.substring(0, 14) + str;
      } 
    } 
    return simpleDateFormat.parse(str2);
  }
  
  private boolean hasFractionalSeconds() {
    for (byte b = 0; b != this.time.length; b++) {
      if (this.time[b] == 46 && b == 14)
        return true; 
    } 
    return false;
  }
  
  boolean isConstructed() {
    return false;
  }
  
  int encodedLength() {
    int i = this.time.length;
    return 1 + StreamUtil.calculateBodyLength(i) + i;
  }
  
  void encode(ASN1OutputStream paramASN1OutputStream) throws IOException {
    paramASN1OutputStream.writeEncoded(24, this.time);
  }
  
  boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    return !(paramASN1Primitive instanceof ASN1GeneralizedTime) ? false : Arrays.areEqual(this.time, ((ASN1GeneralizedTime)paramASN1Primitive).time);
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.time);
  }
}
