package org.bouncycastle.asn1.eac;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import org.bouncycastle.util.Arrays;

public class PackedDate {
  private byte[] time;
  
  public PackedDate(String paramString) {
    this.time = convert(paramString);
  }
  
  public PackedDate(Date paramDate) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd'Z'");
    simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
    this.time = convert(simpleDateFormat.format(paramDate));
  }
  
  public PackedDate(Date paramDate, Locale paramLocale) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd'Z'", paramLocale);
    simpleDateFormat.setTimeZone(new SimpleTimeZone(0, "Z"));
    this.time = convert(simpleDateFormat.format(paramDate));
  }
  
  private byte[] convert(String paramString) {
    char[] arrayOfChar = paramString.toCharArray();
    byte[] arrayOfByte = new byte[6];
    for (byte b = 0; b != 6; b++)
      arrayOfByte[b] = (byte)(arrayOfChar[b] - 48); 
    return arrayOfByte;
  }
  
  PackedDate(byte[] paramArrayOfbyte) {
    this.time = paramArrayOfbyte;
  }
  
  public Date getDate() throws ParseException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
    return simpleDateFormat.parse("20" + toString());
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.time);
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof PackedDate))
      return false; 
    PackedDate packedDate = (PackedDate)paramObject;
    return Arrays.areEqual(this.time, packedDate.time);
  }
  
  public String toString() {
    char[] arrayOfChar = new char[this.time.length];
    for (byte b = 0; b != arrayOfChar.length; b++)
      arrayOfChar[b] = (char)((this.time[b] & 0xFF) + 48); 
    return new String(arrayOfChar);
  }
  
  public byte[] getEncoding() {
    return Arrays.clone(this.time);
  }
}
