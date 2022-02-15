package org.bouncycastle.tsp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.tsp.TimeStampReq;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;

public class TimeStampRequest {
  private static Set EMPTY_SET = Collections.unmodifiableSet(new HashSet());
  
  private TimeStampReq req;
  
  private Extensions extensions;
  
  public TimeStampRequest(TimeStampReq paramTimeStampReq) {
    this.req = paramTimeStampReq;
    this.extensions = paramTimeStampReq.getExtensions();
  }
  
  public TimeStampRequest(byte[] paramArrayOfbyte) throws IOException {
    this(new ByteArrayInputStream(paramArrayOfbyte));
  }
  
  public TimeStampRequest(InputStream paramInputStream) throws IOException {
    this(loadRequest(paramInputStream));
  }
  
  private static TimeStampReq loadRequest(InputStream paramInputStream) throws IOException {
    try {
      return TimeStampReq.getInstance((new ASN1InputStream(paramInputStream)).readObject());
    } catch (ClassCastException classCastException) {
      throw new IOException("malformed request: " + classCastException);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new IOException("malformed request: " + illegalArgumentException);
    } 
  }
  
  public int getVersion() {
    return this.req.getVersion().getValue().intValue();
  }
  
  public ASN1ObjectIdentifier getMessageImprintAlgOID() {
    return this.req.getMessageImprint().getHashAlgorithm().getAlgorithm();
  }
  
  public byte[] getMessageImprintDigest() {
    return this.req.getMessageImprint().getHashedMessage();
  }
  
  public ASN1ObjectIdentifier getReqPolicy() {
    return (this.req.getReqPolicy() != null) ? this.req.getReqPolicy() : null;
  }
  
  public BigInteger getNonce() {
    return (this.req.getNonce() != null) ? this.req.getNonce().getValue() : null;
  }
  
  public boolean getCertReq() {
    return (this.req.getCertReq() != null) ? this.req.getCertReq().isTrue() : false;
  }
  
  public void validate(Set paramSet1, Set paramSet2, Set paramSet3) throws TSPException {
    paramSet1 = convert(paramSet1);
    paramSet2 = convert(paramSet2);
    paramSet3 = convert(paramSet3);
    if (!paramSet1.contains(getMessageImprintAlgOID()))
      throw new TSPValidationException("request contains unknown algorithm", 128); 
    if (paramSet2 != null && getReqPolicy() != null && !paramSet2.contains(getReqPolicy()))
      throw new TSPValidationException("request contains unknown policy", 256); 
    if (getExtensions() != null && paramSet3 != null) {
      Enumeration<ASN1ObjectIdentifier> enumeration = getExtensions().oids();
      while (enumeration.hasMoreElements()) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = enumeration.nextElement();
        if (!paramSet3.contains(aSN1ObjectIdentifier))
          throw new TSPValidationException("request contains unknown extension", 8388608); 
      } 
    } 
    int i = TSPUtil.getDigestLength(getMessageImprintAlgOID().getId());
    if (i != (getMessageImprintDigest()).length)
      throw new TSPValidationException("imprint digest the wrong length", 4); 
  }
  
  public byte[] getEncoded() throws IOException {
    return this.req.getEncoded();
  }
  
  Extensions getExtensions() {
    return this.extensions;
  }
  
  public boolean hasExtensions() {
    return (this.extensions != null);
  }
  
  public Extension getExtension(ASN1ObjectIdentifier paramASN1ObjectIdentifier) {
    return (this.extensions != null) ? this.extensions.getExtension(paramASN1ObjectIdentifier) : null;
  }
  
  public List getExtensionOIDs() {
    return TSPUtil.getExtensionOIDs(this.extensions);
  }
  
  public Set getNonCriticalExtensionOIDs() {
    return (this.extensions == null) ? EMPTY_SET : Collections.unmodifiableSet(new HashSet(Arrays.asList((Object[])this.extensions.getNonCriticalExtensionOIDs())));
  }
  
  public Set getCriticalExtensionOIDs() {
    return (this.extensions == null) ? EMPTY_SET : Collections.unmodifiableSet(new HashSet(Arrays.asList((Object[])this.extensions.getCriticalExtensionOIDs())));
  }
  
  private Set convert(Set paramSet) {
    if (paramSet == null)
      return paramSet; 
    HashSet<ASN1ObjectIdentifier> hashSet = new HashSet(paramSet.size());
    for (String str : paramSet) {
      if (str instanceof String) {
        hashSet.add(new ASN1ObjectIdentifier(str));
        continue;
      } 
      hashSet.add(str);
    } 
    return hashSet;
  }
}
