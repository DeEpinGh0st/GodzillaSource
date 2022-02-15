package org.bouncycastle.asn1.x500;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERT61String;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.DERUniversalString;

public class DirectoryString extends ASN1Object implements ASN1Choice, ASN1String {
  private ASN1String string;
  
  public static DirectoryString getInstance(Object paramObject) {
    if (paramObject == null || paramObject instanceof DirectoryString)
      return (DirectoryString)paramObject; 
    if (paramObject instanceof DERT61String)
      return new DirectoryString((DERT61String)paramObject); 
    if (paramObject instanceof DERPrintableString)
      return new DirectoryString((DERPrintableString)paramObject); 
    if (paramObject instanceof DERUniversalString)
      return new DirectoryString((DERUniversalString)paramObject); 
    if (paramObject instanceof DERUTF8String)
      return new DirectoryString((DERUTF8String)paramObject); 
    if (paramObject instanceof DERBMPString)
      return new DirectoryString((DERBMPString)paramObject); 
    throw new IllegalArgumentException("illegal object in getInstance: " + paramObject.getClass().getName());
  }
  
  public static DirectoryString getInstance(ASN1TaggedObject paramASN1TaggedObject, boolean paramBoolean) {
    if (!paramBoolean)
      throw new IllegalArgumentException("choice item must be explicitly tagged"); 
    return getInstance(paramASN1TaggedObject.getObject());
  }
  
  private DirectoryString(DERT61String paramDERT61String) {
    this.string = (ASN1String)paramDERT61String;
  }
  
  private DirectoryString(DERPrintableString paramDERPrintableString) {
    this.string = (ASN1String)paramDERPrintableString;
  }
  
  private DirectoryString(DERUniversalString paramDERUniversalString) {
    this.string = (ASN1String)paramDERUniversalString;
  }
  
  private DirectoryString(DERUTF8String paramDERUTF8String) {
    this.string = (ASN1String)paramDERUTF8String;
  }
  
  private DirectoryString(DERBMPString paramDERBMPString) {
    this.string = (ASN1String)paramDERBMPString;
  }
  
  public DirectoryString(String paramString) {
    this.string = (ASN1String)new DERUTF8String(paramString);
  }
  
  public String getString() {
    return this.string.getString();
  }
  
  public String toString() {
    return this.string.getString();
  }
  
  public ASN1Primitive toASN1Primitive() {
    return ((ASN1Encodable)this.string).toASN1Primitive();
  }
}
