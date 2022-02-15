package org.bouncycastle.voms;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.x509.Attribute;
import org.bouncycastle.asn1.x509.IetfAttrSyntax;
import org.bouncycastle.cert.X509AttributeCertificateHolder;

public class VOMSAttribute {
  public static final String VOMS_ATTR_OID = "1.3.6.1.4.1.8005.100.100.4";
  
  private X509AttributeCertificateHolder myAC;
  
  private String myHostPort;
  
  private String myVo;
  
  private List myStringList = new ArrayList();
  
  private List myFQANs = new ArrayList();
  
  public VOMSAttribute(X509AttributeCertificateHolder paramX509AttributeCertificateHolder) {
    if (paramX509AttributeCertificateHolder == null)
      throw new IllegalArgumentException("VOMSAttribute: AttributeCertificate is NULL"); 
    this.myAC = paramX509AttributeCertificateHolder;
    Attribute[] arrayOfAttribute = paramX509AttributeCertificateHolder.getAttributes(new ASN1ObjectIdentifier("1.3.6.1.4.1.8005.100.100.4"));
    if (arrayOfAttribute == null)
      return; 
    try {
      for (byte b = 0; b != arrayOfAttribute.length; b++) {
        IetfAttrSyntax ietfAttrSyntax = IetfAttrSyntax.getInstance(arrayOfAttribute[b].getAttributeValues()[0]);
        String str = ((DERIA5String)ietfAttrSyntax.getPolicyAuthority().getNames()[0].getName()).getString();
        int i = str.indexOf("://");
        if (i < 0 || i == str.length() - 1)
          throw new IllegalArgumentException("Bad encoding of VOMS policyAuthority : [" + str + "]"); 
        this.myVo = str.substring(0, i);
        this.myHostPort = str.substring(i + 3);
        if (ietfAttrSyntax.getValueType() != 1)
          throw new IllegalArgumentException("VOMS attribute values are not encoded as octet strings, policyAuthority = " + str); 
        ASN1OctetString[] arrayOfASN1OctetString = (ASN1OctetString[])ietfAttrSyntax.getValues();
        for (byte b1 = 0; b1 != arrayOfASN1OctetString.length; b1++) {
          String str1 = new String(arrayOfASN1OctetString[b1].getOctets());
          FQAN fQAN = new FQAN(str1);
          if (!this.myStringList.contains(str1) && str1.startsWith("/" + this.myVo + "/")) {
            this.myStringList.add(str1);
            this.myFQANs.add(fQAN);
          } 
        } 
      } 
    } catch (IllegalArgumentException illegalArgumentException) {
      throw illegalArgumentException;
    } catch (Exception exception) {
      throw new IllegalArgumentException("Badly encoded VOMS extension in AC issued by " + paramX509AttributeCertificateHolder.getIssuer());
    } 
  }
  
  public X509AttributeCertificateHolder getAC() {
    return this.myAC;
  }
  
  public List getFullyQualifiedAttributes() {
    return this.myStringList;
  }
  
  public List getListOfFQAN() {
    return this.myFQANs;
  }
  
  public String getHostPort() {
    return this.myHostPort;
  }
  
  public String getVO() {
    return this.myVo;
  }
  
  public String toString() {
    return "VO      :" + this.myVo + "\n" + "HostPort:" + this.myHostPort + "\n" + "FQANs   :" + this.myFQANs;
  }
  
  public class FQAN {
    String fqan;
    
    String group;
    
    String role;
    
    String capability;
    
    public FQAN(String param1String) {
      this.fqan = param1String;
    }
    
    public FQAN(String param1String1, String param1String2, String param1String3) {
      this.group = param1String1;
      this.role = param1String2;
      this.capability = param1String3;
    }
    
    public String getFQAN() {
      if (this.fqan != null)
        return this.fqan; 
      this.fqan = this.group + "/Role=" + ((this.role != null) ? this.role : "") + ((this.capability != null) ? ("/Capability=" + this.capability) : "");
      return this.fqan;
    }
    
    protected void split() {
      int i = this.fqan.length();
      int j = this.fqan.indexOf("/Role=");
      if (j < 0)
        return; 
      this.group = this.fqan.substring(0, j);
      int k = this.fqan.indexOf("/Capability=", j + 6);
      String str = (k < 0) ? this.fqan.substring(j + 6) : this.fqan.substring(j + 6, k);
      this.role = (str.length() == 0) ? null : str;
      str = (k < 0) ? null : this.fqan.substring(k + 12);
      this.capability = (str == null || str.length() == 0) ? null : str;
    }
    
    public String getGroup() {
      if (this.group == null && this.fqan != null)
        split(); 
      return this.group;
    }
    
    public String getRole() {
      if (this.group == null && this.fqan != null)
        split(); 
      return this.role;
    }
    
    public String getCapability() {
      if (this.group == null && this.fqan != null)
        split(); 
      return this.capability;
    }
    
    public String toString() {
      return getFQAN();
    }
  }
}
