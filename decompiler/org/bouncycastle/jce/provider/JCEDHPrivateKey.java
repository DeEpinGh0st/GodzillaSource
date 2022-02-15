package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Enumeration;
import javax.crypto.interfaces.DHPrivateKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPrivateKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.DHDomainParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;

public class JCEDHPrivateKey implements DHPrivateKey, PKCS12BagAttributeCarrier {
  static final long serialVersionUID = 311058815616901812L;
  
  BigInteger x;
  
  private DHParameterSpec dhSpec;
  
  private PrivateKeyInfo info;
  
  private PKCS12BagAttributeCarrier attrCarrier = (PKCS12BagAttributeCarrier)new PKCS12BagAttributeCarrierImpl();
  
  protected JCEDHPrivateKey() {}
  
  JCEDHPrivateKey(DHPrivateKey paramDHPrivateKey) {
    this.x = paramDHPrivateKey.getX();
    this.dhSpec = paramDHPrivateKey.getParams();
  }
  
  JCEDHPrivateKey(DHPrivateKeySpec paramDHPrivateKeySpec) {
    this.x = paramDHPrivateKeySpec.getX();
    this.dhSpec = new DHParameterSpec(paramDHPrivateKeySpec.getP(), paramDHPrivateKeySpec.getG());
  }
  
  JCEDHPrivateKey(PrivateKeyInfo paramPrivateKeyInfo) throws IOException {
    ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(paramPrivateKeyInfo.getAlgorithmId().getParameters());
    ASN1Integer aSN1Integer = ASN1Integer.getInstance(paramPrivateKeyInfo.parsePrivateKey());
    ASN1ObjectIdentifier aSN1ObjectIdentifier = paramPrivateKeyInfo.getAlgorithmId().getAlgorithm();
    this.info = paramPrivateKeyInfo;
    this.x = aSN1Integer.getValue();
    if (aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.dhKeyAgreement)) {
      DHParameter dHParameter = DHParameter.getInstance(aSN1Sequence);
      if (dHParameter.getL() != null) {
        this.dhSpec = new DHParameterSpec(dHParameter.getP(), dHParameter.getG(), dHParameter.getL().intValue());
      } else {
        this.dhSpec = new DHParameterSpec(dHParameter.getP(), dHParameter.getG());
      } 
    } else if (aSN1ObjectIdentifier.equals(X9ObjectIdentifiers.dhpublicnumber)) {
      DHDomainParameters dHDomainParameters = DHDomainParameters.getInstance(aSN1Sequence);
      this.dhSpec = new DHParameterSpec(dHDomainParameters.getP().getValue(), dHDomainParameters.getG().getValue());
    } else {
      throw new IllegalArgumentException("unknown algorithm type: " + aSN1ObjectIdentifier);
    } 
  }
  
  JCEDHPrivateKey(DHPrivateKeyParameters paramDHPrivateKeyParameters) {
    this.x = paramDHPrivateKeyParameters.getX();
    this.dhSpec = new DHParameterSpec(paramDHPrivateKeyParameters.getParameters().getP(), paramDHPrivateKeyParameters.getParameters().getG(), paramDHPrivateKeyParameters.getParameters().getL());
  }
  
  public String getAlgorithm() {
    return "DH";
  }
  
  public String getFormat() {
    return "PKCS#8";
  }
  
  public byte[] getEncoded() {
    try {
      if (this.info != null)
        return this.info.getEncoded("DER"); 
      PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.dhKeyAgreement, (ASN1Encodable)new DHParameter(this.dhSpec.getP(), this.dhSpec.getG(), this.dhSpec.getL())), (ASN1Encodable)new ASN1Integer(getX()));
      return privateKeyInfo.getEncoded("DER");
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public DHParameterSpec getParams() {
    return this.dhSpec;
  }
  
  public BigInteger getX() {
    return this.x;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    this.x = (BigInteger)paramObjectInputStream.readObject();
    this.dhSpec = new DHParameterSpec((BigInteger)paramObjectInputStream.readObject(), (BigInteger)paramObjectInputStream.readObject(), paramObjectInputStream.readInt());
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.writeObject(getX());
    paramObjectOutputStream.writeObject(this.dhSpec.getP());
    paramObjectOutputStream.writeObject(this.dhSpec.getG());
    paramObjectOutputStream.writeInt(this.dhSpec.getL());
  }
  
  public void setBagAttribute(ASN1ObjectIdentifier paramASN1ObjectIdentifier, ASN1Encodable paramASN1Encodable) {
    this.attrCarrier.setBagAttribute(paramASN1ObjectIdentifier, paramASN1Encodable);
  }
  
  public ASN1Encodable getBagAttribute(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return this.attrCarrier.getBagAttribute(paramASN1ObjectIdentifier);
  }
  
  public Enumeration getBagAttributeKeys() {
    return this.attrCarrier.getBagAttributeKeys();
  }
}
