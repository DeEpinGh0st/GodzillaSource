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
import org.bouncycastle.asn1.oiw.ElGamalParameter;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.jce.interfaces.ElGamalPublicKey;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;
import org.bouncycastle.jce.spec.ElGamalPublicKeySpec;

public class JCEElGamalPublicKey implements ElGamalPublicKey, DHPublicKey {
  static final long serialVersionUID = 8712728417091216948L;
  
  private BigInteger y;
  
  private ElGamalParameterSpec elSpec;
  
  JCEElGamalPublicKey(ElGamalPublicKeySpec paramElGamalPublicKeySpec) {
    this.y = paramElGamalPublicKeySpec.getY();
    this.elSpec = new ElGamalParameterSpec(paramElGamalPublicKeySpec.getParams().getP(), paramElGamalPublicKeySpec.getParams().getG());
  }
  
  JCEElGamalPublicKey(DHPublicKeySpec paramDHPublicKeySpec) {
    this.y = paramDHPublicKeySpec.getY();
    this.elSpec = new ElGamalParameterSpec(paramDHPublicKeySpec.getP(), paramDHPublicKeySpec.getG());
  }
  
  JCEElGamalPublicKey(ElGamalPublicKey paramElGamalPublicKey) {
    this.y = paramElGamalPublicKey.getY();
    this.elSpec = paramElGamalPublicKey.getParameters();
  }
  
  JCEElGamalPublicKey(DHPublicKey paramDHPublicKey) {
    this.y = paramDHPublicKey.getY();
    this.elSpec = new ElGamalParameterSpec(paramDHPublicKey.getParams().getP(), paramDHPublicKey.getParams().getG());
  }
  
  JCEElGamalPublicKey(ElGamalPublicKeyParameters paramElGamalPublicKeyParameters) {
    this.y = paramElGamalPublicKeyParameters.getY();
    this.elSpec = new ElGamalParameterSpec(paramElGamalPublicKeyParameters.getParameters().getP(), paramElGamalPublicKeyParameters.getParameters().getG());
  }
  
  JCEElGamalPublicKey(BigInteger paramBigInteger, ElGamalParameterSpec paramElGamalParameterSpec) {
    this.y = paramBigInteger;
    this.elSpec = paramElGamalParameterSpec;
  }
  
  JCEElGamalPublicKey(SubjectPublicKeyInfo paramSubjectPublicKeyInfo) {
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
    return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(OIWObjectIdentifiers.elGamalAlgorithm, (ASN1Encodable)new ElGamalParameter(this.elSpec.getP(), this.elSpec.getG())), (ASN1Encodable)new ASN1Integer(this.y));
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
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    this.y = (BigInteger)paramObjectInputStream.readObject();
    this.elSpec = new ElGamalParameterSpec((BigInteger)paramObjectInputStream.readObject(), (BigInteger)paramObjectInputStream.readObject());
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.writeObject(getY());
    paramObjectOutputStream.writeObject(this.elSpec.getP());
    paramObjectOutputStream.writeObject(this.elSpec.getG());
  }
}
