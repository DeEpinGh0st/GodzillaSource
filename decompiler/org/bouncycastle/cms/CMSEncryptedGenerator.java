package org.bouncycastle.cms;

public class CMSEncryptedGenerator {
  protected CMSAttributeTableGenerator unprotectedAttributeGenerator = null;
  
  public void setUnprotectedAttributeGenerator(CMSAttributeTableGenerator paramCMSAttributeTableGenerator) {
    this.unprotectedAttributeGenerator = paramCMSAttributeTableGenerator;
  }
}
