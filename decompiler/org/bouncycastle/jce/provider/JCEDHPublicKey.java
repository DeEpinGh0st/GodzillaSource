package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.DHDomainParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;

public class JCEDHPublicKey implements DHPublicKey {
  static final long serialVersionUID = -216691575254424324L;
  
  private BigInteger y;
  
  private DHParameterSpec dhSpec;
  
  private SubjectPublicKeyInfo info;
  
  JCEDHPublicKey(DHPublicKeySpec paramDHPublicKeySpec) {
    this.y = paramDHPublicKeySpec.getY();
    this.dhSpec = new DHParameterSpec(paramDHPublicKeySpec.getP(), paramDHPublicKeySpec.getG());
  }
  
  JCEDHPublicKey(DHPublicKey paramDHPublicKey) {
    this.y = paramDHPublicKey.getY();
    this.dhSpec = paramDHPublicKey.getParams();
  }
  
  JCEDHPublicKey(DHPublicKeyParameters paramDHPublicKeyParameters) {
    this.y = paramDHPublicKeyParameters.getY();
    this.dhSpec = new DHParameterSpec(paramDHPublicKeyParameters.getParameters().getP(), paramDHPublicKeyParameters.getParameters().getG(), paramDHPublicKeyParameters.getParameters().getL());
  }
  
  JCEDHPublicKey(BigInteger paramBigInteger, DHParameterSpec paramDHParameterSpec) {
    this.y = paramBigInteger;
    this.dhSpec = paramDHParameterSpec;
  }
  
  JCEDHPublicKey(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    ASN1Integer aSN1Integer;
    this.info = paramSubjectPublicKeyInfo;
    try {
      aSN1Integer = (ASN1Integer)paramSubjectPublicKeyInfo.parsePublicKey();
    } catch (IOException iOException) {
      throw new IllegalArgumentException("invalid info structure in DH public key");
    } 
    this.y = aSN1Integer.getValue();
    ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(paramSubjectPublicKeyInfo.getAlgorithmId().getParameters());
    ASN1ObjectIdentifier aSN1ObjectIdentifier = paramSubjectPublicKeyInfo.getAlgorithmId().getAlgorithm();
    if (aSN1ObjectIdentifier.equals(PKCSObjectIdentifiers.dhKeyAgreement) || isPKCSParam(aSN1Sequence)) {
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
  
  public String getAlgorithm() {
    return "DH";
  }
  
  public String getFormat() {
    return "X.509";
  }
  
  public byte[] getEncoded() {
    return (this.info != null) ? KeyUtil.getEncodedSubjectPublicKeyInfo(this.info) : KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.dhKeyAgreement, (ASN1Encodable)new DHParameter(this.dhSpec.getP(), this.dhSpec.getG(), this.dhSpec.getL())), (ASN1Encodable)new ASN1Integer(this.y));
  }
  
  public DHParameterSpec getParams() {
    return this.dhSpec;
  }
  
  public BigInteger getY() {
    return this.y;
  }
  
  private boolean isPKCSParam(ASN1Sequence paramASN1Sequence) {
    if (paramASN1Sequence.size() == 2)
      return true; 
    if (paramASN1Sequence.size() > 3)
      return false; 
    ASN1Integer aSN1Integer1 = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(2));
    ASN1Integer aSN1Integer2 = ASN1Integer.getInstance(paramASN1Sequence.getObjectAt(0));
    return !(aSN1Integer1.getValue().compareTo(BigInteger.valueOf(aSN1Integer2.getValue().bitLength())) > 0);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    this.y = (BigInteger)paramObjectInputStream.readObject();
    this.dhSpec = new DHParameterSpec((BigInteger)paramObjectInputStream.readObject(), (BigInteger)paramObjectInputStream.readObject(), paramObjectInputStream.readInt());
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.writeObject(getY());
    paramObjectOutputStream.writeObject(this.dhSpec.getP());
    paramObjectOutputStream.writeObject(this.dhSpec.getG());
    paramObjectOutputStream.writeInt(this.dhSpec.getL());
  }
}
