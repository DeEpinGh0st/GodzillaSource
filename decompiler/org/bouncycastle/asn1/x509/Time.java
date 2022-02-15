package org.bouncycastle.asn1.x509;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DERGeneralizedTime;
import org.bouncycastle.asn1.DERUTCTime;

public class Time extends ASN1Object implements ASN1Choice {
  ASN1Primitive time;
  
  public static Time getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(paramASN1TaggedObject.getObject());
  }
  
  public Time(ASN1Primitive paramASN1Primitive) {
    if (!(paramASN1Primitive instanceof ASN1UTCTime) && !(paramASN1Primitive instanceof ASN1GeneralizedTime))
      throw new IllegalArgumentException("unknown object passed to Time"); 
    this.time = paramASN1Primitive;
  }
  
  public Time(Date paramDate) {
    SimpleTimeZone simpleTimeZone = new SimpleTimeZone(0, "Z");
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    simpleDateFormat.setTimeZone(simpleTimeZone);
    String str = simpleDateFormat.format(paramDate) + "Z";
    int i = Integer.parseInt(str.substring(0, 4));
    if (i < 1950 || i > 2049) {
      this.time = (ASN1Primitive)new DERGeneralizedTime(str);
    } else {
      this.time = (ASN1Primitive)new DERUTCTime(str.substring(2));
    } 
  }
  
  public Time(Date paramDate, Locale paramLocale) {
    SimpleTimeZone simpleTimeZone = new SimpleTimeZone(0, "Z");
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", paramLocale);
    simpleDateFormat.setTimeZone(simpleTimeZone);
    String str = simpleDateFormat.format(paramDate) + "Z";
    int i = Integer.parseInt(str.substring(0, 4));
    if (i < 1950 || i > 2049) {
      this.time = (ASN1Primitive)new DERGeneralizedTime(str);
    } else {
      this.time = (ASN1Primitive)new DERUTCTime(str.substring(2));
    } 
  }
  
  public static Time getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof Time)
      return (Time)paramObject; 
    if (paramObject instanceof ASN1UTCTime)
      return new Time((ASN1Primitive)paramObject); 
    if (paramObject instanceof ASN1GeneralizedTime)
      return new Time((ASN1Primitive)paramObject); 
    throw new IllegalArgumentException("unknown object in factory: " + paramObject.getClass().getName());
  }
  
  public String getTime() {
    return (this.time instanceof ASN1UTCTime) ? ((ASN1UTCTime)this.time).getAdjustedTime() : ((ASN1GeneralizedTime)this.time).getTime();
  }
  
  public Date getDate() {
    try {
      return (this.time instanceof ASN1UTCTime) ? ((ASN1UTCTime)this.time).getAdjustedDate() : ((ASN1GeneralizedTime)this.time).getDate();
    } catch (ParseException parseException) {
      throw new IllegalStateException("invalid date string: " + parseException.getMessage());
    } 
  }
  
  public ASN1Primitive toASN1Primitive() {
    return this.time;
  }
  
  public String toString() {
    return getTime();
  }
}
