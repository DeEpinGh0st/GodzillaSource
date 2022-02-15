package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.DERVisibleString;

public class DisplayText extends ASN1Object implements ASN1Choice {
  public static final int CONTENT_TYPE_IA5STRING = 0;
  
  public static final int CONTENT_TYPE_BMPSTRING = 1;
  
  public static final int CONTENT_TYPE_UTF8STRING = 2;
  
  public static final int CONTENT_TYPE_VISIBLESTRING = 3;
  
  public static final int DISPLAY_TEXT_MAXIMUM_SIZE = 200;
  
  int contentType;
  
  ASN1String contents;
  
  public DisplayText(int paramInt, String paramString) {
    if (paramString.length() > 200)
      paramString = paramString.substring(0, 200); 
    this.contentType = paramInt;
    switch (paramInt) {
      case 0:
        this.contents = (ASN1String)new DERIA5String(paramString);
        return;
      case 2:
        this.contents = (ASN1String)new DERUTF8String(paramString);
        return;
      case 3:
        this.contents = (ASN1String)new DERVisibleString(paramString);
        return;
      case 1:
        this.contents = (ASN1String)new DERBMPString(paramString);
        return;
    } 
    this.contents = (ASN1String)new DERUTF8String(paramString);
  }
  
  public DisplayText(String paramString) {
    if (paramString.length() > 200)
      paramString = paramString.substring(0, 200); 
    this.contentType = 2;
    this.contents = (ASN1String)new DERUTF8String(paramString);
  }
  
  private DisplayText(ASN1String paramASN1String) {
    this.contents = paramASN1String;
    if (paramASN1String instanceof DERUTF8String) {
      this.contentType = 2;
    } else if (paramASN1String instanceof DERBMPString) {
      this.contentType = 1;
    } else if (paramASN1String instanceof DERIA5String) {
      this.contentType = 0;
    } else if (paramASN1String instanceof DERVisibleString) {
      this.contentType = 3;
    } else {
      throw new IllegalArgumentException("unknown STRING type in DisplayText");
    } 
  }
  
  public static DisplayText getInstance(Object paramObject) {
    if (paramObject instanceof ASN1String)
      return new DisplayText((ASN1String)paramObject); 
    if (paramObject == null || paramObject instanceof DisplayText)
      return (DisplayText)paramObject; 
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static DisplayText getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    return getInstance(paramASN1TaggedObject.getObject());
  }
  
  public ASN1Primitive toASN1Primitive() {
    return (ASN1Primitive)this.contents;
  }
  
  public String getString() {
    return this.contents.getString();
  }
}
