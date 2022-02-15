package org.bouncycastle.asn1;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public class ASN1UTCTime extends ASN1Primitive {
  private byte[] time;
  
  public static ASN1UTCTime getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof ASN1UTCTime)
      return (ASN1UTCTime)paramObject; 
    if (paramObject instanceof byte[])
      try {
        return (ASN1UTCTime)fromByteArray((byte[])paramObject);
      } catch (Exception exception) {
        throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
      }  
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static ASN1UTCTime getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    ASN1Primitive aSN1Primitive = paramASN1TaggedObject.getObject();
    return (paramBoolean || aSN1Primitive instanceof ASN1UTCTime) ? getInstance(aSN1Primitive) : new ASN1UTCTime(((ASN1OctetString)aSN1Primitive).getOctets());
  }
  
  public ASN1UTCTime(String paramString) {
    this.time = Strings.toByteArray(paramString);
    try {
      getDate();
    } catch (ParseException parseException) {
      throw new IllegalArgumentException("invalid date string: " + parseException.getMessage());
    } 
  }
  
  public ASN1UTCTime(Date paramDate) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss'Z'");
    simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
    this.time = Strings.toByteArray(simpleDateFormat.format(paramDate));
  }
  
  public ASN1UTCTime(Date paramDate, Locale paramLocale) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss'Z'", paramLocale);
    simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
    this.time = Strings.toByteArray(simpleDateFormat.format(paramDate));
  }
  
  ASN1UTCTime(byte[] paramArrayOfbyte) {
    this.time = paramArrayOfbyte;
  }
  
  public Date getDate() throws ParseException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmssz");
    return simpleDateFormat.parse(getTime());
  }
  
  public Date getAdjustedDate() throws ParseException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssz");
    simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
    return simpleDateFormat.parse(getAdjustedTime());
  }
  
  public String getTime() {
    String str1 = Strings.fromByteArray(this.time);
    if (str1.indexOf('-') < 0 && str1.indexOf('+') < 0)
      return (str1.length() == 11) ? (str1.substring(0, 10) + "00GMT+00:00") : (str1.substring(0, 12) + "GMT+00:00"); 
    int i = str1.indexOf('-');
    if (i < 0)
      i = str1.indexOf('+'); 
    String str2 = str1;
    if (i == str1.length() - 3)
      str2 = str2 + "00"; 
    return (i == 10) ? (str2.substring(0, 10) + "00GMT" + str2.substring(10, 13) + ":" + str2.substring(13, 15)) : (str2.substring(0, 12) + "GMT" + str2.substring(12, 15) + ":" + str2.substring(15, 17));
  }
  
  public String getAdjustedTime() {
    String str = getTime();
    return (str.charAt(0) < '5') ? ("20" + str) : ("19" + str);
  }
  
  boolean isConstructed() {
    return false;
  }
  
  int encodedLength() {
    int i = this.time.length;
    return 1 + StreamUtil.calculateBodyLength(i) + i;
  }
  
  void encode(ASN1OutputStream paramASN1OutputStream) throws IOException {
    paramASN1OutputStream.write(23);
    int i = this.time.length;
    paramASN1OutputStream.writeLength(i);
    for (int j = 0; j != i; j++)
      paramASN1OutputStream.write(this.time[j]); 
  }
  
  boolean asn1Equals(ASN1Primitive paramASN1Primitive) {
    return !(paramASN1Primitive instanceof ASN1UTCTime) ? false : Arrays.areEqual(this.time, ((ASN1UTCTime)paramASN1Primitive).time);
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.time);
  }
  
  public String toString() {
    return Strings.fromByteArray(this.time);
  }
}
