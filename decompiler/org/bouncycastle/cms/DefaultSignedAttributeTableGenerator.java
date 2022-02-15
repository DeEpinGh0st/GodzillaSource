package org.bouncycastle.cms;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAlgorithmProtection;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.Time;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class DefaultSignedAttributeTableGenerator implements CMSAttributeTableGenerator {
  private final Hashtable table;
  
  public DefaultSignedAttributeTableGenerator() {
    this.table = new Hashtable<Object, Object>();
  }
  
  public DefaultSignedAttributeTableGenerator(AttributeTable paramAttributeTable) {
    if (paramAttributeTable != null) {
      this.table = paramAttributeTable.toHashtable();
    } else {
      this.table = new Hashtable<Object, Object>();
    } 
  }
  
  protected Hashtable createStandardAttributeTable(Map paramMap) {
    Hashtable<ASN1ObjectIdentifier, Attribute> hashtable = copyHashTable(this.table);
    if (!hashtable.containsKey(CMSAttributes.contentType)) {
      ASN1ObjectIdentifier aSN1ObjectIdentifier = ASN1ObjectIdentifier.getInstance(paramMap.get("contentType"));
      if (aSN1ObjectIdentifier != null) {
        Attribute attribute = new Attribute(CMSAttributes.contentType, (ASN1Set)new DERSet((ASN1Encodable)aSN1ObjectIdentifier));
        hashtable.put(attribute.getAttrType(), attribute);
      } 
    } 
    if (!hashtable.containsKey(CMSAttributes.signingTime)) {
      Date date = new Date();
      Attribute attribute = new Attribute(CMSAttributes.signingTime, (ASN1Set)new DERSet((ASN1Encodable)new Time(date)));
      hashtable.put(attribute.getAttrType(), attribute);
    } 
    if (!hashtable.containsKey(CMSAttributes.messageDigest)) {
      byte[] arrayOfByte = (byte[])paramMap.get("digest");
      Attribute attribute = new Attribute(CMSAttributes.messageDigest, (ASN1Set)new DERSet((ASN1Encodable)new DEROctetString(arrayOfByte)));
      hashtable.put(attribute.getAttrType(), attribute);
    } 
    if (!hashtable.contains(CMSAttributes.cmsAlgorithmProtect)) {
      Attribute attribute = new Attribute(CMSAttributes.cmsAlgorithmProtect, (ASN1Set)new DERSet((ASN1Encodable)new CMSAlgorithmProtection((AlgorithmIdentifier)paramMap.get("digestAlgID"), 1, (AlgorithmIdentifier)paramMap.get("signatureAlgID"))));
      hashtable.put(attribute.getAttrType(), attribute);
    } 
    return hashtable;
  }
  
  public AttributeTable getAttributes(Map paramMap) {
    return new AttributeTable(createStandardAttributeTable(paramMap));
  }
  
  private static Hashtable copyHashTable(Hashtable paramHashtable) {
    Hashtable<Object, Object> hashtable = new Hashtable<Object, Object>();
    Enumeration<Object> enumeration = paramHashtable.keys();
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      hashtable.put(object, paramHashtable.get(object));
    } 
    return hashtable;
  }
}
