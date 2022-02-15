package org.bouncycastle.tsp;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.tsp.Accuracy;
import org.bouncycastle.asn1.tsp.TSTInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;

public class TimeStampTokenInfo {
  TSTInfo tstInfo;
  
  Date genTime;
  
  TimeStampTokenInfo(TSTInfo paramTSTInfo) throws TSPException, IOException {
    this.tstInfo = paramTSTInfo;
    try {
      this.genTime = paramTSTInfo.getGenTime().getDate();
    } catch (ParseException parseException) {
      throw new TSPException("unable to parse genTime field");
    } 
  }
  
  public boolean isOrdered() {
    return this.tstInfo.getOrdering().isTrue();
  }
  
  public Accuracy getAccuracy() {
    return this.tstInfo.getAccuracy();
  }
  
  public Date getGenTime() {
    return this.genTime;
  }
  
  public GenTimeAccuracy getGenTimeAccuracy() {
    return (getAccuracy() != null) ? new GenTimeAccuracy(getAccuracy()) : null;
  }
  
  public ASN1ObjectIdentifier getPolicy() {
    return this.tstInfo.getPolicy();
  }
  
  public BigInteger getSerialNumber() {
    return this.tstInfo.getSerialNumber().getValue();
  }
  
  public GeneralName getTsa() {
    return this.tstInfo.getTsa();
  }
  
  public Extensions getExtensions() {
    return this.tstInfo.getExtensions();
  }
  
  public BigInteger getNonce() {
    return (this.tstInfo.getNonce() != null) ? this.tstInfo.getNonce().getValue() : null;
  }
  
  public AlgorithmIdentifier getHashAlgorithm() {
    return this.tstInfo.getMessageImprint().getHashAlgorithm();
  }
  
  public ASN1ObjectIdentifier getMessageImprintAlgOID() {
    return this.tstInfo.getMessageImprint().getHashAlgorithm().getAlgorithm();
  }
  
  public byte[] getMessageImprintDigest() {
    return this.tstInfo.getMessageImprint().getHashedMessage();
  }
  
  public byte[] getEncoded() throws IOException {
    return this.tstInfo.getEncoded();
  }
  
  public TSTInfo toTSTInfo() {
    return this.tstInfo;
  }
  
  public TSTInfo toASN1Structure() {
    return this.tstInfo;
  }
}
