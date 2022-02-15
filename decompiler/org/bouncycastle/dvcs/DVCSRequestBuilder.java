package org.bouncycastle.dvcs;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.dvcs.DVCSObjectIdentifiers;
import org.bouncycastle.asn1.dvcs.DVCSRequest;
import org.bouncycastle.asn1.dvcs.DVCSRequestInformationBuilder;
import org.bouncycastle.asn1.dvcs.Data;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cms.CMSSignedDataGenerator;

public abstract class DVCSRequestBuilder {
  private final ExtensionsGenerator extGenerator = new ExtensionsGenerator();
  
  private final CMSSignedDataGenerator signedDataGen = new CMSSignedDataGenerator();
  
  protected final DVCSRequestInformationBuilder requestInformationBuilder;
  
  protected DVCSRequestBuilder(DVCSRequestInformationBuilder paramDVCSRequestInformationBuilder) {
    this.requestInformationBuilder = paramDVCSRequestInformationBuilder;
  }
  
  public void setNonce(BigInteger paramBigInteger) {
    this.requestInformationBuilder.setNonce(paramBigInteger);
  }
  
  public void setRequester(GeneralName paramGeneralName) {
    this.requestInformationBuilder.setRequester(paramGeneralName);
  }
  
  public void setDVCS(GeneralName paramGeneralName) {
    this.requestInformationBuilder.setDVCS(paramGeneralName);
  }
  
  public void setDVCS(GeneralNames paramGeneralNames) {
    this.requestInformationBuilder.setDVCS(paramGeneralNames);
  }
  
  public void setDataLocations(GeneralName paramGeneralName) {
    this.requestInformationBuilder.setDataLocations(paramGeneralName);
  }
  
  public void setDataLocations(GeneralNames paramGeneralNames) {
    this.requestInformationBuilder.setDataLocations(paramGeneralNames);
  }
  
  public void addExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier, boolean paramBoolean, ASN1Encodable paramASN1Encodable) throws DVCSException {
    try {
      this.extGenerator.addExtension(paramASN1ObjectIdentifier, paramBoolean, paramASN1Encodable);
    } catch (IOException iOException) {
      throw new DVCSException("cannot encode extension: " + iOException.getMessage(), iOException);
    } 
  }
  
  protected DVCSRequest createDVCRequest(Data paramData) throws DVCSException {
    if (!this.extGenerator.isEmpty())
      this.requestInformationBuilder.setExtensions(this.extGenerator.generate()); 
    DVCSRequest dVCSRequest = new DVCSRequest(this.requestInformationBuilder.build(), paramData);
    return new DVCSRequest(new ContentInfo(DVCSObjectIdentifiers.id_ct_DVCSRequestData, (ASN1Encodable)dVCSRequest));
  }
}
