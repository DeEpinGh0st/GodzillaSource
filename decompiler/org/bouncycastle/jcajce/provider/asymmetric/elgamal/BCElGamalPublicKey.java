package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.oiw.ElGamalParameter;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import org.bouncycastle.jce.interfaces.ElGamalPublicKey;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;
import org.bouncycastle.jce.spec.ElGamalPublicKeySpec;

public class BCElGamalPublicKey implements ElGamalPublicKey, DHPublicKey {
  static final long serialVersionUID = 8712728417091216948L;
  
  private BigInteger y;
  
  private transient ElGamalParameterSpec elSpec;
  
  BCElGamalPublicKey(ElGamalPublicKeySpec paramElGamalPublicKeySpec) {
    this.y = paramElGamalPublicKeySpec.getY();
    this.elSpec = new ElGamalParameterSpec(paramElGamalPublicKeySpec.getParams().getP(), paramElGamalPublicKeySpec.getParams().getG());
  }
  
  BCElGamalPublicKey(DHPublicKeySpec paramDHPublicKeySpec) {
    this.y = paramDHPublicKeySpec.getY();
    this.elSpec = new ElGamalParameterSpec(paramDHPublicKeySpec.getP(), paramDHPublicKeySpec.getG());
  }
  
  BCElGamalPublicKey(ElGamalPublicKey paramElGamalPublicKey) {
    this.y = paramElGamalPublicKey.getY();
    this.elSpec = paramElGamalPublicKey.getParameters();
  }
  
  BCElGamalPublicKey(DHPublicKey paramDHPublicKey) {
    this.y = paramDHPublicKey.getY();
    this.elSpec = new ElGamalParameterSpec(paramDHPublicKey.getParams().getP(), paramDHPublicKey.getParams().getG());
  }
  
  BCElGamalPublicKey(ElGamalPublicKeyParameters paramElGamalPublicKeyParameters) {
    this.y = paramElGamalPublicKeyParameters.getY();
    this.elSpec = new ElGamalParameterSpec(paramElGamalPublicKeyParameters.getParameters().getP(), paramElGamalPublicKeyParameters.getParameters().getG());
  }
  
  BCElGamalPublicKey(BigInteger paramBigInteger, ElGamalParameterSpec paramElGamalParameterSpec) {
    this.y = paramBigInteger;
    this.elSpec = paramElGamalParameterSpec;
  }
  
  BCElGamalPublicKey(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
    ElGamalParameter elGamalParameter = ElGamalParameter.getInstance(paramSubjectPublicKeyInfo.getAlgorithm().getParameters());
    ASN1Integer aSN1Integer = null;
    try {
      aSN1Integer = (ASN1Integer)paramSubjectPublicKeyInfo.parsePublicKey();
    } catch (IOException iOException) {
      throw new IllegalArgumentException("invalid info structure in DSA public key");
    } 
    this.y = aSN1Integer.getValue();
    this.elSpec = new ElGamalParameterSpec(elGamalParameter.getP(), elGamalParameter.getG());
  }
  
  public String getAlgorithm() {
    return "ElGamal";
  }
  
  public String getFormat() {
    return "X.509";
  }
  
  public byte[] getEncoded() {
    try {
      SubjectPublicKeyInfo subjectPublicKeyInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(OIWObjectIdentifiers.elGamalAlgorithm, (ASN1Encodable)new ElGamalParameter(this.elSpec.getP(), this.elSpec.getG())), (ASN1Encodable)new ASN1Integer(this.y));
      return subjectPublicKeyInfo.getEncoded("DER");
    } catch (IOException iOException) {
      return null;
    } 
  }
  
  public ElGamalParameterSpec getParameters() {
    return this.elSpec;
  }
  
  public DHParameterSpec getParams() {
    return new DHParameterSpec(this.elSpec.getP(), this.elSpec.getG());
  }
  
  public BigInteger getY() {
    return this.y;
  }
  
  public int hashCode() {
    return getY().hashCode() ^ getParams().getG().hashCode() ^ getParams().getP().hashCode() ^ getParams().getL();
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof DHPublicKey))
      return false; 
    DHPublicKey dHPublicKey = (DHPublicKey)paramObject;
    return (getY().equals(dHPublicKey.getY()) && getParams().getG().equals(dHPublicKey.getParams().getG()) && getParams().getP().equals(dHPublicKey.getParams().getP()) && getParams().getL() == dHPublicKey.getParams().getL());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.elSpec = new ElGamalParameterSpec((BigInteger)paramObjectInputStream.readObject(), (BigInteger)paramObjectInputStream.readObject());
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(this.elSpec.getP());
    paramObjectOutputStream.writeObject(this.elSpec.getG());
  }
}
