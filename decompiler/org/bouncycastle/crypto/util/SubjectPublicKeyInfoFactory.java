package org.bouncycastle.crypto.util;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;

public class SubjectPublicKeyInfoFactory {
  public static SubjectPublicKeyInfo createSubjectPublicKeyInfo(AsymmetricKeyParameter paramAsymmetricKeyParameter) throws IOException {
    if (paramAsymmetricKeyParameter instanceof RSAKeyParameters) {
      RSAKeyParameters rSAKeyParameters = (RSAKeyParameters)paramAsymmetricKeyParameter;
      return new SubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, (ASN1Encodable)DERNull.INSTANCE), (ASN1Encodable)new RSAPublicKey(rSAKeyParameters.getModulus(), rSAKeyParameters.getExponent()));
    } 
    if (paramAsymmetricKeyParameter instanceof DSAPublicKeyParameters) {
      DSAPublicKeyParameters dSAPublicKeyParameters = (DSAPublicKeyParameters)paramAsymmetricKeyParameter;
      DSAParameter dSAParameter = null;
      DSAParameters dSAParameters = dSAPublicKeyParameters.getParameters();
      if (dSAParameters != null)
        dSAParameter = new DSAParameter(dSAParameters.getP(), dSAParameters.getQ(), dSAParameters.getG()); 
      return new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, (ASN1Encodable)dSAParameter), (ASN1Encodable)new ASN1Integer(dSAPublicKeyParameters.getY()));
    } 
    if (paramAsymmetricKeyParameter instanceof ECPublicKeyParameters) {
      X962Parameters x962Parameters;
      ECPublicKeyParameters eCPublicKeyParameters = (ECPublicKeyParameters)paramAsymmetricKeyParameter;
      ECDomainParameters eCDomainParameters = eCPublicKeyParameters.getParameters();
      if (eCDomainParameters == null) {
        x962Parameters = new X962Parameters((ASN1Null)DERNull.INSTANCE);
      } else if (eCDomainParameters instanceof ECNamedDomainParameters) {
        x962Parameters = new X962Parameters(((ECNamedDomainParameters)eCDomainParameters).getName());
      } else {
        X9ECParameters x9ECParameters = new X9ECParameters(eCDomainParameters.getCurve(), eCDomainParameters.getG(), eCDomainParameters.getN(), eCDomainParameters.getH(), eCDomainParameters.getSeed());
        x962Parameters = new X962Parameters(x9ECParameters);
      } 
      ASN1OctetString aSN1OctetString = (ASN1OctetString)(new X9ECPoint(eCPublicKeyParameters.getQ())).toASN1Primitive();
      return new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, (ASN1Encodable)x962Parameters), aSN1OctetString.getOctets());
    } 
    throw new IOException("key parameters not recognised.");
  }
}
