package org.bouncycastle.crypto.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.oiw.ElGamalParameter;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.DHPublicKey;
import org.bouncycastle.asn1.x9.DomainParameters;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.ValidationParams;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.crypto.params.DHValidationParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;

public class PublicKeyFactory {
  public static AsymmetricKeyParameter createKey(byte[] paramArrayOfbyte) throws IOException {
    return createKey(SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(paramArrayOfbyte)));
  }
  
  public static AsymmetricKeyParameter createKey(InputStream paramInputStream) throws IOException {
    return createKey(SubjectPublicKeyInfo.getInstance((new ASN1InputStream(paramInputStream)).readObject()));
  }
  
  public static AsymmetricKeyParameter createKey(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) throws IOException {
    AlgorithmIdentifier algorithmIdentifier = paramSubjectPublicKeyInfo.getAlgorithm();
    if (algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.rsaEncryption) || algorithmIdentifier.getAlgorithm().equals(X509ObjectIdentifiers.id_ea_rsa)) {
      RSAPublicKey rSAPublicKey = RSAPublicKey.getInstance(paramSubjectPublicKeyInfo.parsePublicKey());
      return (AsymmetricKeyParameter)new RSAKeyParameters(false, rSAPublicKey.getModulus(), rSAPublicKey.getPublicExponent());
    } 
    if (algorithmIdentifier.getAlgorithm().equals(X9ObjectIdentifiers.dhpublicnumber)) {
      DHPublicKey dHPublicKey = DHPublicKey.getInstance(paramSubjectPublicKeyInfo.parsePublicKey());
      BigInteger bigInteger1 = dHPublicKey.getY();
      DomainParameters domainParameters = DomainParameters.getInstance(algorithmIdentifier.getParameters());
      BigInteger bigInteger2 = domainParameters.getP();
      BigInteger bigInteger3 = domainParameters.getG();
      BigInteger bigInteger4 = domainParameters.getQ();
      BigInteger bigInteger5 = null;
      if (domainParameters.getJ() != null)
        bigInteger5 = domainParameters.getJ(); 
      DHValidationParameters dHValidationParameters = null;
      ValidationParams validationParams = domainParameters.getValidationParams();
      if (validationParams != null) {
        byte[] arrayOfByte = validationParams.getSeed();
        BigInteger bigInteger = validationParams.getPgenCounter();
        dHValidationParameters = new DHValidationParameters(arrayOfByte, bigInteger.intValue());
      } 
      return (AsymmetricKeyParameter)new DHPublicKeyParameters(bigInteger1, new DHParameters(bigInteger2, bigInteger3, bigInteger4, bigInteger5, dHValidationParameters));
    } 
    if (algorithmIdentifier.getAlgorithm().equals(PKCSObjectIdentifiers.dhKeyAgreement)) {
      DHParameter dHParameter = DHParameter.getInstance(algorithmIdentifier.getParameters());
      ASN1Integer aSN1Integer = (ASN1Integer)paramSubjectPublicKeyInfo.parsePublicKey();
      BigInteger bigInteger = dHParameter.getL();
      boolean bool = (bigInteger == null) ? false : bigInteger.intValue();
      DHParameters dHParameters = new DHParameters(dHParameter.getP(), dHParameter.getG(), null, bool);
      return (AsymmetricKeyParameter)new DHPublicKeyParameters(aSN1Integer.getValue(), dHParameters);
    } 
    if (algorithmIdentifier.getAlgorithm().equals(OIWObjectIdentifiers.elGamalAlgorithm)) {
      ElGamalParameter elGamalParameter = ElGamalParameter.getInstance(algorithmIdentifier.getParameters());
      ASN1Integer aSN1Integer = (ASN1Integer)paramSubjectPublicKeyInfo.parsePublicKey();
      return (AsymmetricKeyParameter)new ElGamalPublicKeyParameters(aSN1Integer.getValue(), new ElGamalParameters(elGamalParameter.getP(), elGamalParameter.getG()));
    } 
    if (algorithmIdentifier.getAlgorithm().equals(X9ObjectIdentifiers.id_dsa) || algorithmIdentifier.getAlgorithm().equals(OIWObjectIdentifiers.dsaWithSHA1)) {
      ASN1Integer aSN1Integer = (ASN1Integer)paramSubjectPublicKeyInfo.parsePublicKey();
      ASN1Encodable aSN1Encodable = algorithmIdentifier.getParameters();
      DSAParameters dSAParameters = null;
      if (aSN1Encodable != null) {
        DSAParameter dSAParameter = DSAParameter.getInstance(aSN1Encodable.toASN1Primitive());
        dSAParameters = new DSAParameters(dSAParameter.getP(), dSAParameter.getQ(), dSAParameter.getG());
      } 
      return (AsymmetricKeyParameter)new DSAPublicKeyParameters(aSN1Integer.getValue(), dSAParameters);
    } 
    if (algorithmIdentifier.getAlgorithm().equals(X9ObjectIdentifiers.id_ecPublicKey)) {
      X9ECParameters x9ECParameters;
      ECDomainParameters eCDomainParameters;
      X962Parameters x962Parameters = X962Parameters.getInstance(algorithmIdentifier.getParameters());
      if (x962Parameters.isNamedCurve()) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)x962Parameters.getParameters();
        x9ECParameters = CustomNamedCurves.getByOID(aSN1ObjectIdentifier);
        if (x9ECParameters == null)
          x9ECParameters = ECNamedCurveTable.getByOID(aSN1ObjectIdentifier); 
        ECNamedDomainParameters eCNamedDomainParameters = new ECNamedDomainParameters(aSN1ObjectIdentifier, x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH(), x9ECParameters.getSeed());
      } else {
        x9ECParameters = X9ECParameters.getInstance(x962Parameters.getParameters());
        eCDomainParameters = new ECDomainParameters(x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH(), x9ECParameters.getSeed());
      } 
      DEROctetString dEROctetString = new DEROctetString(paramSubjectPublicKeyInfo.getPublicKeyData().getBytes());
      X9ECPoint x9ECPoint = new X9ECPoint(x9ECParameters.getCurve(), (ASN1OctetString)dEROctetString);
      return (AsymmetricKeyParameter)new ECPublicKeyParameters(x9ECPoint.getPoint(), eCDomainParameters);
    } 
    throw new RuntimeException("algorithm identifier in key not recognised");
  }
}
