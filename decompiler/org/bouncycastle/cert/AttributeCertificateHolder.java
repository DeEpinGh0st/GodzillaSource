package org.bouncycastle.cert;

import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.Holder;
import org.bouncycastle.asn1.x509.IssuerSerial;
import org.bouncycastle.asn1.x509.ObjectDigestInfo;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;

public class AttributeCertificateHolder implements Selector {
  private static DigestCalculatorProvider digestCalculatorProvider;
  
  final Holder holder;
  
  AttributeCertificateHolder(ASN1Sequence paramASN1Sequence) {
    this.holder = Holder.getInstance(paramASN1Sequence);
  }
  
  public AttributeCertificateHolder(X500Name paramX500Name, BigInteger paramBigInteger) {
    this.holder = new Holder(new IssuerSerial(generateGeneralNames(paramX500Name), new ASN1Integer(paramBigInteger)));
  }
  
  public AttributeCertificateHolder(X509CertificateHolder paramX509CertificateHolder) {
    this.holder = new Holder(new IssuerSerial(generateGeneralNames(paramX509CertificateHolder.getIssuer()), new ASN1Integer(paramX509CertificateHolder.getSerialNumber())));
  }
  
  public AttributeCertificateHolder(X500Name paramX500Name) {
    this.holder = new Holder(generateGeneralNames(paramX500Name));
  }
  
  public AttributeCertificateHolder(int paramInt, ASN1ObjectIdentifier paramASN1ObjectIdentifier1, ASN1ObjectIdentifier paramASN1ObjectIdentifier2, byte[] paramArrayOfbyte) {
    this.holder = new Holder(new ObjectDigestInfo(paramInt, paramASN1ObjectIdentifier2, new AlgorithmIdentifier(paramASN1ObjectIdentifier1), Arrays.clone(paramArrayOfbyte)));
  }
  
  public int getDigestedObjectType() {
    return (this.holder.getObjectDigestInfo() != null) ? this.holder.getObjectDigestInfo().getDigestedObjectType().getValue().intValue() : -1;
  }
  
  public AlgorithmIdentifier getDigestAlgorithm() {
    return (this.holder.getObjectDigestInfo() != null) ? this.holder.getObjectDigestInfo().getDigestAlgorithm() : null;
  }
  
  public byte[] getObjectDigest() {
    return (this.holder.getObjectDigestInfo() != null) ? this.holder.getObjectDigestInfo().getObjectDigest().getBytes() : null;
  }
  
  public ASN1ObjectIdentifier getOtherObjectTypeID() {
    if (this.holder.getObjectDigestInfo() != null)
      new ASN1ObjectIdentifier(this.holder.getObjectDigestInfo().getOtherObjectTypeID().getId()); 
    return null;
  }
  
  private GeneralNames generateGeneralNames(X500Name paramX500Name) {
    return new GeneralNames(new GeneralName(paramX500Name));
  }
  
  private boolean matchesDN(X500Name paramX500Name, GeneralNames paramGeneralNames) {
    GeneralName[] arrayOfGeneralName = paramGeneralNames.getNames();
    for (byte b = 0; b != arrayOfGeneralName.length; b++) {
      GeneralName generalName = arrayOfGeneralName[b];
      if (generalName.getTagNo() == 4 && X500Name.getInstance(generalName.getName()).equals(paramX500Name))
        return true; 
    } 
    return false;
  }
  
  private X500Name[] getPrincipals(GeneralName[] paramArrayOfGeneralName) {
    ArrayList<X500Name> arrayList = new ArrayList(paramArrayOfGeneralName.length);
    for (byte b = 0; b != paramArrayOfGeneralName.length; b++) {
      if (paramArrayOfGeneralName[b].getTagNo() == 4)
        arrayList.add(X500Name.getInstance(paramArrayOfGeneralName[b].getName())); 
    } 
    return arrayList.<X500Name>toArray(new X500Name[arrayList.size()]);
  }
  
  public X500Name[] getEntityNames() {
    return (this.holder.getEntityName() != null) ? getPrincipals(this.holder.getEntityName().getNames()) : null;
  }
  
  public X500Name[] getIssuer() {
    return (this.holder.getBaseCertificateID() != null) ? getPrincipals(this.holder.getBaseCertificateID().getIssuer().getNames()) : null;
  }
  
  public BigInteger getSerialNumber() {
    return (this.holder.getBaseCertificateID() != null) ? this.holder.getBaseCertificateID().getSerial().getValue() : null;
  }
  
  public Object clone() {
    return new AttributeCertificateHolder((ASN1Sequence)this.holder.toASN1Primitive());
  }
  
  public boolean match(Object paramObject) {
    if (!(paramObject instanceof X509CertificateHolder))
      return false; 
    X509CertificateHolder x509CertificateHolder = (X509CertificateHolder)paramObject;
    if (this.holder.getBaseCertificateID() != null)
      return (this.holder.getBaseCertificateID().getSerial().getValue().equals(x509CertificateHolder.getSerialNumber()) && matchesDN(x509CertificateHolder.getIssuer(), this.holder.getBaseCertificateID().getIssuer())); 
    if (this.holder.getEntityName() != null && matchesDN(x509CertificateHolder.getSubject(), this.holder.getEntityName()))
      return true; 
    if (this.holder.getObjectDigestInfo() != null)
      try {
        DigestCalculator digestCalculator = digestCalculatorProvider.get(this.holder.getObjectDigestInfo().getDigestAlgorithm());
        OutputStream outputStream = digestCalculator.getOutputStream();
        switch (getDigestedObjectType()) {
          case 0:
            outputStream.write(x509CertificateHolder.getSubjectPublicKeyInfo().getEncoded());
            break;
          case 1:
            outputStream.write(x509CertificateHolder.getEncoded());
            break;
        } 
        outputStream.close();
        if (!Arrays.areEqual(digestCalculator.getDigest(), getObjectDigest()))
          return false; 
      } catch (Exception exception) {
        return false;
      }  
    return false;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof AttributeCertificateHolder))
      return false; 
    AttributeCertificateHolder attributeCertificateHolder = (AttributeCertificateHolder)paramObject;
    return this.holder.equals(attributeCertificateHolder.holder);
  }
  
  public int hashCode() {
    return this.holder.hashCode();
  }
  
  public static void setDigestCalculatorProvider(DigestCalculatorProvider paramDigestCalculatorProvider) {
    digestCalculatorProvider = paramDigestCalculatorProvider;
  }
}
