package org.bouncycastle.crypto.util;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;

public class PrivateKeyInfoFactory {
  public static PrivateKeyInfo createPrivateKeyInfo(AsymmetricKeyParameter paramAsymmetricKeyParameter) throws IOException {
    if (paramAsymmetricKeyParameter instanceof org.bouncycastle.crypto.params.RSAKeyParameters) {
      RSAPrivateCrtKeyParameters rSAPrivateCrtKeyParameters = (RSAPrivateCrtKeyParameters)paramAsymmetricKeyParameter;
      return new PrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, (ASN1Encodable)DERNull.INSTANCE), (ASN1Encodable)new RSAPrivateKey(rSAPrivateCrtKeyParameters.getModulus(), rSAPrivateCrtKeyParameters.getPublicExponent(), rSAPrivateCrtKeyParameters.getExponent(), rSAPrivateCrtKeyParameters.getP(), rSAPrivateCrtKeyParameters.getQ(), rSAPrivateCrtKeyParameters.getDP(), rSAPrivateCrtKeyParameters.getDQ(), rSAPrivateCrtKeyParameters.getQInv()));
    } 
    if (paramAsymmetricKeyParameter instanceof DSAPrivateKeyParameters) {
      DSAPrivateKeyParameters dSAPrivateKeyParameters = (DSAPrivateKeyParameters)paramAsymmetricKeyParameter;
      DSAParameters dSAParameters = dSAPrivateKeyParameters.getParameters();
      return new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, (ASN1Encodable)new DSAParameter(dSAParameters.getP(), dSAParameters.getQ(), dSAParameters.getG())), (ASN1Encodable)new ASN1Integer(dSAPrivateKeyParameters.getX()));
    } 
    if (paramAsymmetricKeyParameter instanceof ECPrivateKeyParameters) {
      X962Parameters x962Parameters;
      int i;
      ECPrivateKeyParameters eCPrivateKeyParameters = (ECPrivateKeyParameters)paramAsymmetricKeyParameter;
      ECDomainParameters eCDomainParameters = eCPrivateKeyParameters.getParameters();
      if (eCDomainParameters == null) {
        x962Parameters = new X962Parameters((ASN1Null)DERNull.INSTANCE);
        i = eCPrivateKeyParameters.getD().bitLength();
      } else if (eCDomainParameters instanceof ECNamedDomainParameters) {
        x962Parameters = new X962Parameters(((ECNamedDomainParameters)eCDomainParameters).getName());
        i = eCDomainParameters.getN().bitLength();
      } else {
        X9ECParameters x9ECParameters = new X9ECParameters(eCDomainParameters.getCurve(), eCDomainParameters.getG(), eCDomainParameters.getN(), eCDomainParameters.getH(), eCDomainParameters.getSeed());
        x962Parameters = new X962Parameters(x9ECParameters);
        i = eCDomainParameters.getN().bitLength();
      } 
      return new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, (ASN1Encodable)x962Parameters), (ASN1Encodable)new ECPrivateKey(i, eCPrivateKeyParameters.getD(), (ASN1Encodable)x962Parameters));
    } 
    throw new IOException("key parameters not recognised.");
  }
}
