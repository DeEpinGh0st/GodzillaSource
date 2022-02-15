package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.AuthenticatedData;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.io.TeeOutputStream;

public class CMSAuthenticatedDataGenerator extends CMSAuthenticatedGenerator {
  public CMSAuthenticatedData generate(CMSTypedData paramCMSTypedData, MacCalculator paramMacCalculator) throws CMSException {
    return generate(paramCMSTypedData, paramMacCalculator, (DigestCalculator)null);
  }
  
  public CMSAuthenticatedData generate(CMSTypedData paramCMSTypedData, MacCalculator paramMacCalculator, final DigestCalculator digestCalculator) throws CMSException {
    AuthenticatedData authenticatedData;
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    for (RecipientInfoGenerator recipientInfoGenerator : this.recipientInfoGenerators)
      aSN1EncodableVector.add((ASN1Encodable)recipientInfoGenerator.generate(paramMacCalculator.getKey())); 
    if (digestCalculator != null) {
      BEROctetString bEROctetString;
      DEROctetString dEROctetString;
      try {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        TeeOutputStream teeOutputStream = new TeeOutputStream(digestCalculator.getOutputStream(), byteArrayOutputStream);
        paramCMSTypedData.write((OutputStream)teeOutputStream);
        teeOutputStream.close();
        bEROctetString = new BEROctetString(byteArrayOutputStream.toByteArray());
      } catch (IOException iOException) {
        throw new CMSException("unable to perform digest calculation: " + iOException.getMessage(), iOException);
      } 
      Map<?, ?> map = getBaseParameters(paramCMSTypedData.getContentType(), digestCalculator.getAlgorithmIdentifier(), paramMacCalculator.getAlgorithmIdentifier(), digestCalculator.getDigest());
      if (this.authGen == null)
        this.authGen = new DefaultAuthenticatedAttributeTableGenerator(); 
      DERSet dERSet = new DERSet(this.authGen.getAttributes(Collections.unmodifiableMap(map)).toASN1EncodableVector());
      try {
        OutputStream outputStream = paramMacCalculator.getOutputStream();
        outputStream.write(dERSet.getEncoded("DER"));
        outputStream.close();
        dEROctetString = new DEROctetString(paramMacCalculator.getMac());
      } catch (IOException iOException) {
        throw new CMSException("exception decoding algorithm parameters.", iOException);
      } 
      BERSet bERSet = (this.unauthGen != null) ? new BERSet(this.unauthGen.getAttributes(Collections.unmodifiableMap(map)).toASN1EncodableVector()) : null;
      ContentInfo contentInfo1 = new ContentInfo(CMSObjectIdentifiers.data, (ASN1Encodable)bEROctetString);
      authenticatedData = new AuthenticatedData(this.originatorInfo, (ASN1Set)new DERSet(aSN1EncodableVector), paramMacCalculator.getAlgorithmIdentifier(), digestCalculator.getAlgorithmIdentifier(), contentInfo1, (ASN1Set)dERSet, (ASN1OctetString)dEROctetString, (ASN1Set)bERSet);
    } else {
      BEROctetString bEROctetString;
      DEROctetString dEROctetString;
      try {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        TeeOutputStream teeOutputStream = new TeeOutputStream(byteArrayOutputStream, paramMacCalculator.getOutputStream());
        paramCMSTypedData.write((OutputStream)teeOutputStream);
        teeOutputStream.close();
        bEROctetString = new BEROctetString(byteArrayOutputStream.toByteArray());
        dEROctetString = new DEROctetString(paramMacCalculator.getMac());
      } catch (IOException iOException) {
        throw new CMSException("exception decoding algorithm parameters.", iOException);
      } 
      BERSet bERSet = (this.unauthGen != null) ? new BERSet(this.unauthGen.getAttributes(new HashMap<Object, Object>()).toASN1EncodableVector()) : null;
      ContentInfo contentInfo1 = new ContentInfo(CMSObjectIdentifiers.data, (ASN1Encodable)bEROctetString);
      authenticatedData = new AuthenticatedData(this.originatorInfo, (ASN1Set)new DERSet(aSN1EncodableVector), paramMacCalculator.getAlgorithmIdentifier(), null, contentInfo1, null, (ASN1OctetString)dEROctetString, (ASN1Set)bERSet);
    } 
    ContentInfo contentInfo = new ContentInfo(CMSObjectIdentifiers.authenticatedData, (ASN1Encodable)authenticatedData);
    return new CMSAuthenticatedData(contentInfo, new DigestCalculatorProvider() {
          public DigestCalculator get(AlgorithmIdentifier param1AlgorithmIdentifier) throws OperatorCreationException {
            return digestCalculator;
          }
        });
  }
}
