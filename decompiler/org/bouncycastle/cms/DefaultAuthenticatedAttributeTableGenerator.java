package org.bouncycastle.cms;

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
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class DefaultAuthenticatedAttributeTableGenerator implements CMSAttributeTableGenerator {
  private final Hashtable table;
  
  public DefaultAuthenticatedAttributeTableGenerator() {
    this.table = new Hashtable<Object, Object>();
  }
  
  public DefaultAuthenticatedAttributeTableGenerator(AttributeTable paramAttributeTable) {
    if (paramAttributeTable != null) {
      this.table = paramAttributeTable.toHashtable();
    } else {
      this.table = new Hashtable<Object, Object>();
    } 
  }
  
  protected Hashtable createStandardAttributeTable(Map paramMap) {
    Hashtable<Object, Object> hashtable = new Hashtable<Object, Object>();
    Enumeration<Object> enumeration = this.table.keys();
    while (enumeration.hasMoreElements()) {
      Object object = enumeration.nextElement();
      hashtable.put(object, this.table.get(object));
    } 
    if (!hashtable.containsKey(CMSAttributes.contentType)) {
      ASN1ObjectIdentifier aSN1ObjectIdentifier = ASN1ObjectIdentifier.getInstance(paramMap.get("contentType"));
      Attribute attribute = new Attribute(CMSAttributes.contentType, (ASN1Set)new DERSet((ASN1Encodable)aSN1ObjectIdentifier));
      hashtable.put(attribute.getAttrType(), attribute);
    } 
    if (!hashtable.containsKey(CMSAttributes.messageDigest)) {
      byte[] arrayOfByte = (byte[])paramMap.get("digest");
      Attribute attribute = new Attribute(CMSAttributes.messageDigest, (ASN1Set)new DERSet((ASN1Encodable)new DEROctetString(arrayOfByte)));
      hashtable.put(attribute.getAttrType(), attribute);
    } 
    if (!hashtable.contains(CMSAttributes.cmsAlgorithmProtect)) {
      Attribute attribute = new Attribute(CMSAttributes.cmsAlgorithmProtect, (ASN1Set)new DERSet((ASN1Encodable)new CMSAlgorithmProtection((AlgorithmIdentifier)paramMap.get("digestAlgID"), 2, (AlgorithmIdentifier)paramMap.get("macAlgID"))));
      hashtable.put(attribute.getAttrType(), attribute);
    } 
    return hashtable;
  }
  
  public AttributeTable getAttributes(Map paramMap) {
    return new AttributeTable(createStandardAttributeTable(paramMap));
  }
}
