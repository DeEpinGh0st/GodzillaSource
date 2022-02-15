package org.bouncycastle.jce.netscape;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class NetscapeCertRequest extends ASN1Object {
  AlgorithmIdentifier sigAlg;
  
  AlgorithmIdentifier keyAlg;
  
  byte[] sigBits;
  
  String challenge;
  
  DERBitString content;
  
  PublicKey pubkey;
  
  private static ASN1Sequence getReq(byte[] paramArrayOfbyte) throws IOException {
    ASN1InputStream aSN1InputStream = new ASN1InputStream(new ByteArrayInputStream(paramArrayOfbyte));
    return ASN1Sequence.getInstance(aSN1InputStream.readObject());
  }
  
  public NetscapeCertRequest(byte[] paramArrayOfbyte) throws IOException {
    this(getReq(paramArrayOfbyte));
  }
  
  public NetscapeCertRequest(ASN1Sequence paramASN1Sequence) {
    try {
      if (paramASN1Sequence.size() != 3)
        throw new IllegalArgumentException("invalid SPKAC (size):" + paramASN1Sequence.size()); 
      this.sigAlg = AlgorithmIdentifier.getInstance(paramASN1Sequence.getObjectAt(1));
      this.sigBits = ((DERBitString)paramASN1Sequence.getObjectAt(2)).getOctets();
      ASN1Sequence aSN1Sequence = (ASN1Sequence)paramASN1Sequence.getObjectAt(0);
      if (aSN1Sequence.size() != 2)
        throw new IllegalArgumentException("invalid PKAC (len): " + aSN1Sequence.size()); 
      this.challenge = ((DERIA5String)aSN1Sequence.getObjectAt(1)).getString();
      this.content = new DERBitString((ASN1Encodable)aSN1Sequence);
      SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(aSN1Sequence.getObjectAt(0));
      X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec((new DERBitString((ASN1Encodable)subjectPublicKeyInfo)).getBytes());
      this.keyAlg = subjectPublicKeyInfo.getAlgorithm();
      this.pubkey = KeyFactory.getInstance(this.keyAlg.getAlgorithm().getId(), "BC").generatePublic(x509EncodedKeySpec);
    } catch (Exception exception) {
      throw new IllegalArgumentException(exception.toString());
    } 
  }
  
  public NetscapeCertRequest(String paramString, AlgorithmIdentifier paramAlgorithmIdentifier, PublicKey paramPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
    this.challenge = paramString;
    this.sigAlg = paramAlgorithmIdentifier;
    this.pubkey = paramPublicKey;
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)getKeySpec());
    aSN1EncodableVector.add((ASN1Encodable)new DERIA5String(paramString));
    try {
      this.content = new DERBitString((ASN1Encodable)new DERSequence(aSN1EncodableVector));
    } catch (IOException iOException) {
      throw new InvalidKeySpecException("exception encoding key: " + iOException.toString());
    } 
  }
  
  public String getChallenge() {
    return this.challenge;
  }
  
  public void setChallenge(String paramString) {
    this.challenge = paramString;
  }
  
  public AlgorithmIdentifier getSigningAlgorithm() {
    return this.sigAlg;
  }
  
  public void setSigningAlgorithm(AlgorithmIdentifier paramAlgorithmIdentifier) {
    this.sigAlg = paramAlgorithmIdentifier;
  }
  
  public AlgorithmIdentifier getKeyAlgorithm() {
    return this.keyAlg;
  }
  
  public void setKeyAlgorithm(AlgorithmIdentifier paramAlgorithmIdentifier) {
    this.keyAlg = paramAlgorithmIdentifier;
  }
  
  public PublicKey getPublicKey() {
    return this.pubkey;
  }
  
  public void setPublicKey(PublicKey paramPublicKey) {
    this.pubkey = paramPublicKey;
  }
  
  public boolean verify(String paramString) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
    if (!paramString.equals(this.challenge))
      return false; 
    Signature signature = Signature.getInstance(this.sigAlg.getAlgorithm().getId(), "BC");
    signature.initVerify(this.pubkey);
    signature.update(this.content.getBytes());
    return signature.verify(this.sigBits);
  }
  
  public void sign(PrivateKey paramPrivateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException, InvalidKeySpecException {
    sign(paramPrivateKey, null);
  }
  
  public void sign(PrivateKey paramPrivateKey, SecureRandom paramSecureRandom) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException, InvalidKeySpecException {
    Signature signature = Signature.getInstance(this.sigAlg.getAlgorithm().getId(), "BC");
    if (paramSecureRandom != null) {
      signature.initSign(paramPrivateKey, paramSecureRandom);
    } else {
      signature.initSign(paramPrivateKey);
    } 
    ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
    aSN1EncodableVector.add((ASN1Encodable)getKeySpec());
    aSN1EncodableVector.add((ASN1Encodable)new DERIA5String(this.challenge));
    try {
      signature.update((new DERSequence(aSN1EncodableVector)).getEncoded("DER"));
    } catch (IOException iOException) {
      throw new SignatureException(iOException.getMessage());
    } 
    this.sigBits = signature.sign();
  }
  
  private ASN1Primitive getKeySpec() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ASN1Primitive aSN1Primitive = null;
    try {
      byteArrayOutputStream.write(this.pubkey.getEncoded());
      byteArrayOutputStream.close();
      ASN1InputStream aSN1InputStream = new ASN1InputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
      aSN1Primitive = aSN1InputStream.readObject();
    } catch (IOException iOException) {
      throw new InvalidKeySpecException(iOException.getMessage());
    } 
    return aSN1Primitive;
  }
  
  public ASN1Primitive toASN1Primitive() {
    ASN1EncodableVector aSN1EncodableVector1 = new ASN1EncodableVector();
    ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
    try {
      aSN1EncodableVector2.add((ASN1Encodable)getKeySpec());
    } catch (Exception exception) {}
    aSN1EncodableVector2.add((ASN1Encodable)new DERIA5String(this.challenge));
    aSN1EncodableVector1.add((ASN1Encodable)new DERSequence(aSN1EncodableVector2));
    aSN1EncodableVector1.add((ASN1Encodable)this.sigAlg);
    aSN1EncodableVector1.add((ASN1Encodable)new DERBitString(this.sigBits));
    return (ASN1Primitive)new DERSequence(aSN1EncodableVector1);
  }
}
