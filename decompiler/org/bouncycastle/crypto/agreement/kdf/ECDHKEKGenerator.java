package org.bouncycastle.crypto.agreement.kdf;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.DigestDerivationFunction;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.util.Pack;

public class ECDHKEKGenerator implements DigestDerivationFunction {
  private DigestDerivationFunction kdf;
  
  private ASN1ObjectIdentifier algorithm;
  
  private int keySize;
  
  private byte[] z;
  
  public ECDHKEKGenerator(Digest paramDigest) {
    this.kdf = (DigestDerivationFunction)new KDF2BytesGenerator(paramDigest);
  }
  
  public void init(DerivationParameters paramDerivationParameters) {
    DHKDFParameters dHKDFParameters = (DHKDFParameters)paramDerivationParameters;
    this.algorithm = dHKDFParameters.getAlgorithm();
    this.keySize = dHKDFParameters.getKeySize();
    this.z = dHKDFParameters.getZ();
  }
  
  public Digest getDigest() {
    return this.kdf.getDigest();
  }
  
  public int generateBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws DataLengthException, IllegalArgumentException {
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)new AlgorithmIdentifier(this.algorithm, (ASN1Encodable)DERNull.INSTANCE));
    aSN1EncodableVector.add((ASN1Encodable)new DERTaggedObject(true, 2, (ASN1Encodable)new DEROctetString(Pack.intToBigEndian(this.keySize))));
    try {
      this.kdf.init((DerivationParameters)new KDFParameters(this.z, (new DERSequence(aSN1EncodableVector)).getEncoded("DER")));
    } catch (IOException iOException) {
      throw new IllegalArgumentException("unable to initialise kdf: " + iOException.getMessage());
    } 
    return this.kdf.generateBytes(paramArrayOfbyte, paramInt1, paramInt2);
  }
}
