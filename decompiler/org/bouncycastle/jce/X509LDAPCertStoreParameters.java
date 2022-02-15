package org.bouncycastle.jce;

import java.security.cert.CertStoreParameters;
import java.security.cert.LDAPCertStoreParameters;
import org.bouncycastle.x509.X509StoreParameters;

public class X509LDAPCertStoreParameters implements X509StoreParameters, CertStoreParameters {
  private String ldapURL;
  
  private String baseDN;
  
  private String userCertificateAttribute;
  
  private String cACertificateAttribute;
  
  private String crossCertificateAttribute;
  
  private String certificateRevocationListAttribute;
  
  private String deltaRevocationListAttribute;
  
  private String authorityRevocationListAttribute;
  
  private String attributeCertificateAttributeAttribute;
  
  private String aACertificateAttribute;
  
  private String attributeDescriptorCertificateAttribute;
  
  private String attributeCertificateRevocationListAttribute;
  
  private String attributeAuthorityRevocationListAttribute;
  
  private String ldapUserCertificateAttributeName;
  
  private String ldapCACertificateAttributeName;
  
  private String ldapCrossCertificateAttributeName;
  
  private String ldapCertificateRevocationListAttributeName;
  
  private String ldapDeltaRevocationListAttributeName;
  
  private String ldapAuthorityRevocationListAttributeName;
  
  private String ldapAttributeCertificateAttributeAttributeName;
  
  private String ldapAACertificateAttributeName;
  
  private String ldapAttributeDescriptorCertificateAttributeName;
  
  private String ldapAttributeCertificateRevocationListAttributeName;
  
  private String ldapAttributeAuthorityRevocationListAttributeName;
  
  private String userCertificateSubjectAttributeName;
  
  private String cACertificateSubjectAttributeName;
  
  private String crossCertificateSubjectAttributeName;
  
  private String certificateRevocationListIssuerAttributeName;
  
  private String deltaRevocationListIssuerAttributeName;
  
  private String authorityRevocationListIssuerAttributeName;
  
  private String attributeCertificateAttributeSubjectAttributeName;
  
  private String aACertificateSubjectAttributeName;
  
  private String attributeDescriptorCertificateSubjectAttributeName;
  
  private String attributeCertificateRevocationListIssuerAttributeName;
  
  private String attributeAuthorityRevocationListIssuerAttributeName;
  
  private String searchForSerialNumberIn;
  
  private X509LDAPCertStoreParameters(Builder paramBuilder) {
    this.ldapURL = paramBuilder.ldapURL;
    this.baseDN = paramBuilder.baseDN;
    this.userCertificateAttribute = paramBuilder.userCertificateAttribute;
    this.cACertificateAttribute = paramBuilder.cACertificateAttribute;
    this.crossCertificateAttribute = paramBuilder.crossCertificateAttribute;
    this.certificateRevocationListAttribute = paramBuilder.certificateRevocationListAttribute;
    this.deltaRevocationListAttribute = paramBuilder.deltaRevocationListAttribute;
    this.authorityRevocationListAttribute = paramBuilder.authorityRevocationListAttribute;
    this.attributeCertificateAttributeAttribute = paramBuilder.attributeCertificateAttributeAttribute;
    this.aACertificateAttribute = paramBuilder.aACertificateAttribute;
    this.attributeDescriptorCertificateAttribute = paramBuilder.attributeDescriptorCertificateAttribute;
    this.attributeCertificateRevocationListAttribute = paramBuilder.attributeCertificateRevocationListAttribute;
    this.attributeAuthorityRevocationListAttribute = paramBuilder.attributeAuthorityRevocationListAttribute;
    this.ldapUserCertificateAttributeName = paramBuilder.ldapUserCertificateAttributeName;
    this.ldapCACertificateAttributeName = paramBuilder.ldapCACertificateAttributeName;
    this.ldapCrossCertificateAttributeName = paramBuilder.ldapCrossCertificateAttributeName;
    this.ldapCertificateRevocationListAttributeName = paramBuilder.ldapCertificateRevocationListAttributeName;
    this.ldapDeltaRevocationListAttributeName = paramBuilder.ldapDeltaRevocationListAttributeName;
    this.ldapAuthorityRevocationListAttributeName = paramBuilder.ldapAuthorityRevocationListAttributeName;
    this.ldapAttributeCertificateAttributeAttributeName = paramBuilder.ldapAttributeCertificateAttributeAttributeName;
    this.ldapAACertificateAttributeName = paramBuilder.ldapAACertificateAttributeName;
    this.ldapAttributeDescriptorCertificateAttributeName = paramBuilder.ldapAttributeDescriptorCertificateAttributeName;
    this.ldapAttributeCertificateRevocationListAttributeName = paramBuilder.ldapAttributeCertificateRevocationListAttributeName;
    this.ldapAttributeAuthorityRevocationListAttributeName = paramBuilder.ldapAttributeAuthorityRevocationListAttributeName;
    this.userCertificateSubjectAttributeName = paramBuilder.userCertificateSubjectAttributeName;
    this.cACertificateSubjectAttributeName = paramBuilder.cACertificateSubjectAttributeName;
    this.crossCertificateSubjectAttributeName = paramBuilder.crossCertificateSubjectAttributeName;
    this.certificateRevocationListIssuerAttributeName = paramBuilder.certificateRevocationListIssuerAttributeName;
    this.deltaRevocationListIssuerAttributeName = paramBuilder.deltaRevocationListIssuerAttributeName;
    this.authorityRevocationListIssuerAttributeName = paramBuilder.authorityRevocationListIssuerAttributeName;
    this.attributeCertificateAttributeSubjectAttributeName = paramBuilder.attributeCertificateAttributeSubjectAttributeName;
    this.aACertificateSubjectAttributeName = paramBuilder.aACertificateSubjectAttributeName;
    this.attributeDescriptorCertificateSubjectAttributeName = paramBuilder.attributeDescriptorCertificateSubjectAttributeName;
    this.attributeCertificateRevocationListIssuerAttributeName = paramBuilder.attributeCertificateRevocationListIssuerAttributeName;
    this.attributeAuthorityRevocationListIssuerAttributeName = paramBuilder.attributeAuthorityRevocationListIssuerAttributeName;
    this.searchForSerialNumberIn = paramBuilder.searchForSerialNumberIn;
  }
  
  public Object clone() {
    return this;
  }
  
  public boolean equal(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof X509LDAPCertStoreParameters))
      return false; 
    X509LDAPCertStoreParameters x509LDAPCertStoreParameters = (X509LDAPCertStoreParameters)paramObject;
    return (checkField(this.ldapURL, x509LDAPCertStoreParameters.ldapURL) && checkField(this.baseDN, x509LDAPCertStoreParameters.baseDN) && checkField(this.userCertificateAttribute, x509LDAPCertStoreParameters.userCertificateAttribute) && checkField(this.cACertificateAttribute, x509LDAPCertStoreParameters.cACertificateAttribute) && checkField(this.crossCertificateAttribute, x509LDAPCertStoreParameters.crossCertificateAttribute) && checkField(this.certificateRevocationListAttribute, x509LDAPCertStoreParameters.certificateRevocationListAttribute) && checkField(this.deltaRevocationListAttribute, x509LDAPCertStoreParameters.deltaRevocationListAttribute) && checkField(this.authorityRevocationListAttribute, x509LDAPCertStoreParameters.authorityRevocationListAttribute) && checkField(this.attributeCertificateAttributeAttribute, x509LDAPCertStoreParameters.attributeCertificateAttributeAttribute) && checkField(this.aACertificateAttribute, x509LDAPCertStoreParameters.aACertificateAttribute) && checkField(this.attributeDescriptorCertificateAttribute, x509LDAPCertStoreParameters.attributeDescriptorCertificateAttribute) && checkField(this.attributeCertificateRevocationListAttribute, x509LDAPCertStoreParameters.attributeCertificateRevocationListAttribute) && checkField(this.attributeAuthorityRevocationListAttribute, x509LDAPCertStoreParameters.attributeAuthorityRevocationListAttribute) && checkField(this.ldapUserCertificateAttributeName, x509LDAPCertStoreParameters.ldapUserCertificateAttributeName) && checkField(this.ldapCACertificateAttributeName, x509LDAPCertStoreParameters.ldapCACertificateAttributeName) && checkField(this.ldapCrossCertificateAttributeName, x509LDAPCertStoreParameters.ldapCrossCertificateAttributeName) && checkField(this.ldapCertificateRevocationListAttributeName, x509LDAPCertStoreParameters.ldapCertificateRevocationListAttributeName) && checkField(this.ldapDeltaRevocationListAttributeName, x509LDAPCertStoreParameters.ldapDeltaRevocationListAttributeName) && checkField(this.ldapAuthorityRevocationListAttributeName, x509LDAPCertStoreParameters.ldapAuthorityRevocationListAttributeName) && checkField(this.ldapAttributeCertificateAttributeAttributeName, x509LDAPCertStoreParameters.ldapAttributeCertificateAttributeAttributeName) && checkField(this.ldapAACertificateAttributeName, x509LDAPCertStoreParameters.ldapAACertificateAttributeName) && checkField(this.ldapAttributeDescriptorCertificateAttributeName, x509LDAPCertStoreParameters.ldapAttributeDescriptorCertificateAttributeName) && checkField(this.ldapAttributeCertificateRevocationListAttributeName, x509LDAPCertStoreParameters.ldapAttributeCertificateRevocationListAttributeName) && checkField(this.ldapAttributeAuthorityRevocationListAttributeName, x509LDAPCertStoreParameters.ldapAttributeAuthorityRevocationListAttributeName) && checkField(this.userCertificateSubjectAttributeName, x509LDAPCertStoreParameters.userCertificateSubjectAttributeName) && checkField(this.cACertificateSubjectAttributeName, x509LDAPCertStoreParameters.cACertificateSubjectAttributeName) && checkField(this.crossCertificateSubjectAttributeName, x509LDAPCertStoreParameters.crossCertificateSubjectAttributeName) && checkField(this.certificateRevocationListIssuerAttributeName, x509LDAPCertStoreParameters.certificateRevocationListIssuerAttributeName) && checkField(this.deltaRevocationListIssuerAttributeName, x509LDAPCertStoreParameters.deltaRevocationListIssuerAttributeName) && checkField(this.authorityRevocationListIssuerAttributeName, x509LDAPCertStoreParameters.authorityRevocationListIssuerAttributeName) && checkField(this.attributeCertificateAttributeSubjectAttributeName, x509LDAPCertStoreParameters.attributeCertificateAttributeSubjectAttributeName) && checkField(this.aACertificateSubjectAttributeName, x509LDAPCertStoreParameters.aACertificateSubjectAttributeName) && checkField(this.attributeDescriptorCertificateSubjectAttributeName, x509LDAPCertStoreParameters.attributeDescriptorCertificateSubjectAttributeName) && checkField(this.attributeCertificateRevocationListIssuerAttributeName, x509LDAPCertStoreParameters.attributeCertificateRevocationListIssuerAttributeName) && checkField(this.attributeAuthorityRevocationListIssuerAttributeName, x509LDAPCertStoreParameters.attributeAuthorityRevocationListIssuerAttributeName) && checkField(this.searchForSerialNumberIn, x509LDAPCertStoreParameters.searchForSerialNumberIn));
  }
  
  private boolean checkField(Object paramObject1, Object paramObject2) {
    return (paramObject1 == paramObject2) ? true : ((paramObject1 == null) ? false : paramObject1.equals(paramObject2));
  }
  
  public int hashCode() {
    null = 0;
    null = addHashCode(null, this.userCertificateAttribute);
    null = addHashCode(null, this.cACertificateAttribute);
    null = addHashCode(null, this.crossCertificateAttribute);
    null = addHashCode(null, this.certificateRevocationListAttribute);
    null = addHashCode(null, this.deltaRevocationListAttribute);
    null = addHashCode(null, this.authorityRevocationListAttribute);
    null = addHashCode(null, this.attributeCertificateAttributeAttribute);
    null = addHashCode(null, this.aACertificateAttribute);
    null = addHashCode(null, this.attributeDescriptorCertificateAttribute);
    null = addHashCode(null, this.attributeCertificateRevocationListAttribute);
    null = addHashCode(null, this.attributeAuthorityRevocationListAttribute);
    null = addHashCode(null, this.ldapUserCertificateAttributeName);
    null = addHashCode(null, this.ldapCACertificateAttributeName);
    null = addHashCode(null, this.ldapCrossCertificateAttributeName);
    null = addHashCode(null, this.ldapCertificateRevocationListAttributeName);
    null = addHashCode(null, this.ldapDeltaRevocationListAttributeName);
    null = addHashCode(null, this.ldapAuthorityRevocationListAttributeName);
    null = addHashCode(null, this.ldapAttributeCertificateAttributeAttributeName);
    null = addHashCode(null, this.ldapAACertificateAttributeName);
    null = addHashCode(null, this.ldapAttributeDescriptorCertificateAttributeName);
    null = addHashCode(null, this.ldapAttributeCertificateRevocationListAttributeName);
    null = addHashCode(null, this.ldapAttributeAuthorityRevocationListAttributeName);
    null = addHashCode(null, this.userCertificateSubjectAttributeName);
    null = addHashCode(null, this.cACertificateSubjectAttributeName);
    null = addHashCode(null, this.crossCertificateSubjectAttributeName);
    null = addHashCode(null, this.certificateRevocationListIssuerAttributeName);
    null = addHashCode(null, this.deltaRevocationListIssuerAttributeName);
    null = addHashCode(null, this.authorityRevocationListIssuerAttributeName);
    null = addHashCode(null, this.attributeCertificateAttributeSubjectAttributeName);
    null = addHashCode(null, this.aACertificateSubjectAttributeName);
    null = addHashCode(null, this.attributeDescriptorCertificateSubjectAttributeName);
    null = addHashCode(null, this.attributeCertificateRevocationListIssuerAttributeName);
    null = addHashCode(null, this.attributeAuthorityRevocationListIssuerAttributeName);
    return addHashCode(null, this.searchForSerialNumberIn);
  }
  
  private int addHashCode(int paramInt, Object paramObject) {
    return paramInt * 29 + ((paramObject == null) ? 0 : paramObject.hashCode());
  }
  
  public String getAACertificateAttribute() {
    return this.aACertificateAttribute;
  }
  
  public String getAACertificateSubjectAttributeName() {
    return this.aACertificateSubjectAttributeName;
  }
  
  public String getAttributeAuthorityRevocationListAttribute() {
    return this.attributeAuthorityRevocationListAttribute;
  }
  
  public String getAttributeAuthorityRevocationListIssuerAttributeName() {
    return this.attributeAuthorityRevocationListIssuerAttributeName;
  }
  
  public String getAttributeCertificateAttributeAttribute() {
    return this.attributeCertificateAttributeAttribute;
  }
  
  public String getAttributeCertificateAttributeSubjectAttributeName() {
    return this.attributeCertificateAttributeSubjectAttributeName;
  }
  
  public String getAttributeCertificateRevocationListAttribute() {
    return this.attributeCertificateRevocationListAttribute;
  }
  
  public String getAttributeCertificateRevocationListIssuerAttributeName() {
    return this.attributeCertificateRevocationListIssuerAttributeName;
  }
  
  public String getAttributeDescriptorCertificateAttribute() {
    return this.attributeDescriptorCertificateAttribute;
  }
  
  public String getAttributeDescriptorCertificateSubjectAttributeName() {
    return this.attributeDescriptorCertificateSubjectAttributeName;
  }
  
  public String getAuthorityRevocationListAttribute() {
    return this.authorityRevocationListAttribute;
  }
  
  public String getAuthorityRevocationListIssuerAttributeName() {
    return this.authorityRevocationListIssuerAttributeName;
  }
  
  public String getBaseDN() {
    return this.baseDN;
  }
  
  public String getCACertificateAttribute() {
    return this.cACertificateAttribute;
  }
  
  public String getCACertificateSubjectAttributeName() {
    return this.cACertificateSubjectAttributeName;
  }
  
  public String getCertificateRevocationListAttribute() {
    return this.certificateRevocationListAttribute;
  }
  
  public String getCertificateRevocationListIssuerAttributeName() {
    return this.certificateRevocationListIssuerAttributeName;
  }
  
  public String getCrossCertificateAttribute() {
    return this.crossCertificateAttribute;
  }
  
  public String getCrossCertificateSubjectAttributeName() {
    return this.crossCertificateSubjectAttributeName;
  }
  
  public String getDeltaRevocationListAttribute() {
    return this.deltaRevocationListAttribute;
  }
  
  public String getDeltaRevocationListIssuerAttributeName() {
    return this.deltaRevocationListIssuerAttributeName;
  }
  
  public String getLdapAACertificateAttributeName() {
    return this.ldapAACertificateAttributeName;
  }
  
  public String getLdapAttributeAuthorityRevocationListAttributeName() {
    return this.ldapAttributeAuthorityRevocationListAttributeName;
  }
  
  public String getLdapAttributeCertificateAttributeAttributeName() {
    return this.ldapAttributeCertificateAttributeAttributeName;
  }
  
  public String getLdapAttributeCertificateRevocationListAttributeName() {
    return this.ldapAttributeCertificateRevocationListAttributeName;
  }
  
  public String getLdapAttributeDescriptorCertificateAttributeName() {
    return this.ldapAttributeDescriptorCertificateAttributeName;
  }
  
  public String getLdapAuthorityRevocationListAttributeName() {
    return this.ldapAuthorityRevocationListAttributeName;
  }
  
  public String getLdapCACertificateAttributeName() {
    return this.ldapCACertificateAttributeName;
  }
  
  public String getLdapCertificateRevocationListAttributeName() {
    return this.ldapCertificateRevocationListAttributeName;
  }
  
  public String getLdapCrossCertificateAttributeName() {
    return this.ldapCrossCertificateAttributeName;
  }
  
  public String getLdapDeltaRevocationListAttributeName() {
    return this.ldapDeltaRevocationListAttributeName;
  }
  
  public String getLdapURL() {
    return this.ldapURL;
  }
  
  public String getLdapUserCertificateAttributeName() {
    return this.ldapUserCertificateAttributeName;
  }
  
  public String getSearchForSerialNumberIn() {
    return this.searchForSerialNumberIn;
  }
  
  public String getUserCertificateAttribute() {
    return this.userCertificateAttribute;
  }
  
  public String getUserCertificateSubjectAttributeName() {
    return this.userCertificateSubjectAttributeName;
  }
  
  public static X509LDAPCertStoreParameters getInstance(LDAPCertStoreParameters paramLDAPCertStoreParameters) {
    String str = "ldap://" + paramLDAPCertStoreParameters.getServerName() + ":" + paramLDAPCertStoreParameters.getPort();
    return (new Builder(str, "")).build();
  }
  
  public static class Builder {
    private String ldapURL;
    
    private String baseDN;
    
    private String userCertificateAttribute;
    
    private String cACertificateAttribute;
    
    private String crossCertificateAttribute;
    
    private String certificateRevocationListAttribute;
    
    private String deltaRevocationListAttribute;
    
    private String authorityRevocationListAttribute;
    
    private String attributeCertificateAttributeAttribute;
    
    private String aACertificateAttribute;
    
    private String attributeDescriptorCertificateAttribute;
    
    private String attributeCertificateRevocationListAttribute;
    
    private String attributeAuthorityRevocationListAttribute;
    
    private String ldapUserCertificateAttributeName;
    
    private String ldapCACertificateAttributeName;
    
    private String ldapCrossCertificateAttributeName;
    
    private String ldapCertificateRevocationListAttributeName;
    
    private String ldapDeltaRevocationListAttributeName;
    
    private String ldapAuthorityRevocationListAttributeName;
    
    private String ldapAttributeCertificateAttributeAttributeName;
    
    private String ldapAACertificateAttributeName;
    
    private String ldapAttributeDescriptorCertificateAttributeName;
    
    private String ldapAttributeCertificateRevocationListAttributeName;
    
    private String ldapAttributeAuthorityRevocationListAttributeName;
    
    private String userCertificateSubjectAttributeName;
    
    private String cACertificateSubjectAttributeName;
    
    private String crossCertificateSubjectAttributeName;
    
    private String certificateRevocationListIssuerAttributeName;
    
    private String deltaRevocationListIssuerAttributeName;
    
    private String authorityRevocationListIssuerAttributeName;
    
    private String attributeCertificateAttributeSubjectAttributeName;
    
    private String aACertificateSubjectAttributeName;
    
    private String attributeDescriptorCertificateSubjectAttributeName;
    
    private String attributeCertificateRevocationListIssuerAttributeName;
    
    private String attributeAuthorityRevocationListIssuerAttributeName;
    
    private String searchForSerialNumberIn;
    
    public Builder() {
      this("ldap://localhost:389", "");
    }
    
    public Builder(String param1String1, String param1String2) {
      this.ldapURL = param1String1;
      if (param1String2 == null) {
        this.baseDN = "";
      } else {
        this.baseDN = param1String2;
      } 
      this.userCertificateAttribute = "userCertificate";
      this.cACertificateAttribute = "cACertificate";
      this.crossCertificateAttribute = "crossCertificatePair";
      this.certificateRevocationListAttribute = "certificateRevocationList";
      this.deltaRevocationListAttribute = "deltaRevocationList";
      this.authorityRevocationListAttribute = "authorityRevocationList";
      this.attributeCertificateAttributeAttribute = "attributeCertificateAttribute";
      this.aACertificateAttribute = "aACertificate";
      this.attributeDescriptorCertificateAttribute = "attributeDescriptorCertificate";
      this.attributeCertificateRevocationListAttribute = "attributeCertificateRevocationList";
      this.attributeAuthorityRevocationListAttribute = "attributeAuthorityRevocationList";
      this.ldapUserCertificateAttributeName = "cn";
      this.ldapCACertificateAttributeName = "cn ou o";
      this.ldapCrossCertificateAttributeName = "cn ou o";
      this.ldapCertificateRevocationListAttributeName = "cn ou o";
      this.ldapDeltaRevocationListAttributeName = "cn ou o";
      this.ldapAuthorityRevocationListAttributeName = "cn ou o";
      this.ldapAttributeCertificateAttributeAttributeName = "cn";
      this.ldapAACertificateAttributeName = "cn o ou";
      this.ldapAttributeDescriptorCertificateAttributeName = "cn o ou";
      this.ldapAttributeCertificateRevocationListAttributeName = "cn o ou";
      this.ldapAttributeAuthorityRevocationListAttributeName = "cn o ou";
      this.userCertificateSubjectAttributeName = "cn";
      this.cACertificateSubjectAttributeName = "o ou";
      this.crossCertificateSubjectAttributeName = "o ou";
      this.certificateRevocationListIssuerAttributeName = "o ou";
      this.deltaRevocationListIssuerAttributeName = "o ou";
      this.authorityRevocationListIssuerAttributeName = "o ou";
      this.attributeCertificateAttributeSubjectAttributeName = "cn";
      this.aACertificateSubjectAttributeName = "o ou";
      this.attributeDescriptorCertificateSubjectAttributeName = "o ou";
      this.attributeCertificateRevocationListIssuerAttributeName = "o ou";
      this.attributeAuthorityRevocationListIssuerAttributeName = "o ou";
      this.searchForSerialNumberIn = "uid serialNumber cn";
    }
    
    public Builder setUserCertificateAttribute(String param1String) {
      this.userCertificateAttribute = param1String;
      return this;
    }
    
    public Builder setCACertificateAttribute(String param1String) {
      this.cACertificateAttribute = param1String;
      return this;
    }
    
    public Builder setCrossCertificateAttribute(String param1String) {
      this.crossCertificateAttribute = param1String;
      return this;
    }
    
    public Builder setCertificateRevocationListAttribute(String param1String) {
      this.certificateRevocationListAttribute = param1String;
      return this;
    }
    
    public Builder setDeltaRevocationListAttribute(String param1String) {
      this.deltaRevocationListAttribute = param1String;
      return this;
    }
    
    public Builder setAuthorityRevocationListAttribute(String param1String) {
      this.authorityRevocationListAttribute = param1String;
      return this;
    }
    
    public Builder setAttributeCertificateAttributeAttribute(String param1String) {
      this.attributeCertificateAttributeAttribute = param1String;
      return this;
    }
    
    public Builder setAACertificateAttribute(String param1String) {
      this.aACertificateAttribute = param1String;
      return this;
    }
    
    public Builder setAttributeDescriptorCertificateAttribute(String param1String) {
      this.attributeDescriptorCertificateAttribute = param1String;
      return this;
    }
    
    public Builder setAttributeCertificateRevocationListAttribute(String param1String) {
      this.attributeCertificateRevocationListAttribute = param1String;
      return this;
    }
    
    public Builder setAttributeAuthorityRevocationListAttribute(String param1String) {
      this.attributeAuthorityRevocationListAttribute = param1String;
      return this;
    }
    
    public Builder setLdapUserCertificateAttributeName(String param1String) {
      this.ldapUserCertificateAttributeName = param1String;
      return this;
    }
    
    public Builder setLdapCACertificateAttributeName(String param1String) {
      this.ldapCACertificateAttributeName = param1String;
      return this;
    }
    
    public Builder setLdapCrossCertificateAttributeName(String param1String) {
      this.ldapCrossCertificateAttributeName = param1String;
      return this;
    }
    
    public Builder setLdapCertificateRevocationListAttributeName(String param1String) {
      this.ldapCertificateRevocationListAttributeName = param1String;
      return this;
    }
    
    public Builder setLdapDeltaRevocationListAttributeName(String param1String) {
      this.ldapDeltaRevocationListAttributeName = param1String;
      return this;
    }
    
    public Builder setLdapAuthorityRevocationListAttributeName(String param1String) {
      this.ldapAuthorityRevocationListAttributeName = param1String;
      return this;
    }
    
    public Builder setLdapAttributeCertificateAttributeAttributeName(String param1String) {
      this.ldapAttributeCertificateAttributeAttributeName = param1String;
      return this;
    }
    
    public Builder setLdapAACertificateAttributeName(String param1String) {
      this.ldapAACertificateAttributeName = param1String;
      return this;
    }
    
    public Builder setLdapAttributeDescriptorCertificateAttributeName(String param1String) {
      this.ldapAttributeDescriptorCertificateAttributeName = param1String;
      return this;
    }
    
    public Builder setLdapAttributeCertificateRevocationListAttributeName(String param1String) {
      this.ldapAttributeCertificateRevocationListAttributeName = param1String;
      return this;
    }
    
    public Builder setLdapAttributeAuthorityRevocationListAttributeName(String param1String) {
      this.ldapAttributeAuthorityRevocationListAttributeName = param1String;
      return this;
    }
    
    public Builder setUserCertificateSubjectAttributeName(String param1String) {
      this.userCertificateSubjectAttributeName = param1String;
      return this;
    }
    
    public Builder setCACertificateSubjectAttributeName(String param1String) {
      this.cACertificateSubjectAttributeName = param1String;
      return this;
    }
    
    public Builder setCrossCertificateSubjectAttributeName(String param1String) {
      this.crossCertificateSubjectAttributeName = param1String;
      return this;
    }
    
    public Builder setCertificateRevocationListIssuerAttributeName(String param1String) {
      this.certificateRevocationListIssuerAttributeName = param1String;
      return this;
    }
    
    public Builder setDeltaRevocationListIssuerAttributeName(String param1String) {
      this.deltaRevocationListIssuerAttributeName = param1String;
      return this;
    }
    
    public Builder setAuthorityRevocationListIssuerAttributeName(String param1String) {
      this.authorityRevocationListIssuerAttributeName = param1String;
      return this;
    }
    
    public Builder setAttributeCertificateAttributeSubjectAttributeName(String param1String) {
      this.attributeCertificateAttributeSubjectAttributeName = param1String;
      return this;
    }
    
    public Builder setAACertificateSubjectAttributeName(String param1String) {
      this.aACertificateSubjectAttributeName = param1String;
      return this;
    }
    
    public Builder setAttributeDescriptorCertificateSubjectAttributeName(String param1String) {
      this.attributeDescriptorCertificateSubjectAttributeName = param1String;
      return this;
    }
    
    public Builder setAttributeCertificateRevocationListIssuerAttributeName(String param1String) {
      this.attributeCertificateRevocationListIssuerAttributeName = param1String;
      return this;
    }
    
    public Builder setAttributeAuthorityRevocationListIssuerAttributeName(String param1String) {
      this.attributeAuthorityRevocationListIssuerAttributeName = param1String;
      return this;
    }
    
    public Builder setSearchForSerialNumberIn(String param1String) {
      this.searchForSerialNumberIn = param1String;
      return this;
    }
    
    public X509LDAPCertStoreParameters build() {
      if (this.ldapUserCertificateAttributeName == null || this.ldapCACertificateAttributeName == null || this.ldapCrossCertificateAttributeName == null || this.ldapCertificateRevocationListAttributeName == null || this.ldapDeltaRevocationListAttributeName == null || this.ldapAuthorityRevocationListAttributeName == null || this.ldapAttributeCertificateAttributeAttributeName == null || this.ldapAACertificateAttributeName == null || this.ldapAttributeDescriptorCertificateAttributeName == null || this.ldapAttributeCertificateRevocationListAttributeName == null || this.ldapAttributeAuthorityRevocationListAttributeName == null || this.userCertificateSubjectAttributeName == null || this.cACertificateSubjectAttributeName == null || this.crossCertificateSubjectAttributeName == null || this.certificateRevocationListIssuerAttributeName == null || this.deltaRevocationListIssuerAttributeName == null || this.authorityRevocationListIssuerAttributeName == null || this.attributeCertificateAttributeSubjectAttributeName == null || this.aACertificateSubjectAttributeName == null || this.attributeDescriptorCertificateSubjectAttributeName == null || this.attributeCertificateRevocationListIssuerAttributeName == null || this.attributeAuthorityRevocationListIssuerAttributeName == null)
        throw new IllegalArgumentException("Necessary parameters not specified."); 
      return new X509LDAPCertStoreParameters(this);
    }
  }
}
