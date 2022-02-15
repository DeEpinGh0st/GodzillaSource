package org.bouncycastle.est;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

public class HttpAuth implements ESTAuth {
  private static final DigestAlgorithmIdentifierFinder digestAlgorithmIdentifierFinder = (DigestAlgorithmIdentifierFinder)new DefaultDigestAlgorithmIdentifierFinder();
  
  private final String realm;
  
  private final String username;
  
  private final char[] password;
  
  private final SecureRandom nonceGenerator;
  
  private final DigestCalculatorProvider digestCalculatorProvider;
  
  private static final Set<String> validParts;
  
  public HttpAuth(String paramString, char[] paramArrayOfchar) {
    this(null, paramString, paramArrayOfchar, null, null);
  }
  
  public HttpAuth(String paramString1, String paramString2, char[] paramArrayOfchar) {
    this(paramString1, paramString2, paramArrayOfchar, null, null);
  }
  
  public HttpAuth(String paramString, char[] paramArrayOfchar, SecureRandom paramSecureRandom, DigestCalculatorProvider paramDigestCalculatorProvider) {
    this(null, paramString, paramArrayOfchar, paramSecureRandom, paramDigestCalculatorProvider);
  }
  
  public HttpAuth(String paramString1, String paramString2, char[] paramArrayOfchar, SecureRandom paramSecureRandom, DigestCalculatorProvider paramDigestCalculatorProvider) {
    this.realm = paramString1;
    this.username = paramString2;
    this.password = paramArrayOfchar;
    this.nonceGenerator = paramSecureRandom;
    this.digestCalculatorProvider = paramDigestCalculatorProvider;
  }
  
  public void applyAuth(ESTRequestBuilder paramESTRequestBuilder) {
    paramESTRequestBuilder.withHijacker(new ESTHijacker() {
          public ESTResponse hijack(ESTRequest param1ESTRequest, Source param1Source) throws IOException {
            ESTResponse eSTResponse = new ESTResponse(param1ESTRequest, param1Source);
            if (eSTResponse.getStatusCode() == 401) {
              String str = eSTResponse.getHeader("WWW-Authenticate");
              if (str == null)
                throw new ESTException("Status of 401 but no WWW-Authenticate header"); 
              str = Strings.toLowerCase(str);
              if (str.startsWith("digest")) {
                eSTResponse = HttpAuth.this.doDigestFunction(eSTResponse);
              } else if (str.startsWith("basic")) {
                eSTResponse.close();
                Map<String, String> map = HttpUtil.splitCSL("Basic", eSTResponse.getHeader("WWW-Authenticate"));
                if (HttpAuth.this.realm != null && !HttpAuth.this.realm.equals(map.get("realm")))
                  throw new ESTException("Supplied realm '" + HttpAuth.this.realm + "' does not match server realm '" + (String)map.get("realm") + "'", null, 401, null); 
                ESTRequestBuilder eSTRequestBuilder = (new ESTRequestBuilder(param1ESTRequest)).withHijacker(null);
                if (HttpAuth.this.realm != null && HttpAuth.this.realm.length() > 0)
                  eSTRequestBuilder.setHeader("WWW-Authenticate", "Basic realm=\"" + HttpAuth.this.realm + "\""); 
                if (HttpAuth.this.username.contains(":"))
                  throw new IllegalArgumentException("User must not contain a ':'"); 
                String str1 = HttpAuth.this.username + ":" + new String(HttpAuth.this.password);
                eSTRequestBuilder.setHeader("Authorization", "Basic " + Base64.toBase64String(str1.getBytes()));
                eSTResponse = param1ESTRequest.getClient().doRequest(eSTRequestBuilder.build());
              } else {
                throw new ESTException("Unknown auth mode: " + str);
              } 
              return eSTResponse;
            } 
            return eSTResponse;
          }
        });
  }
  
  private ESTResponse doDigestFunction(ESTResponse paramESTResponse) throws IOException {
    paramESTResponse.close();
    ESTRequest eSTRequest = paramESTResponse.getOriginalRequest();
    Map<String, String> map = null;
    try {
      map = HttpUtil.splitCSL("Digest", paramESTResponse.getHeader("WWW-Authenticate"));
    } catch (Throwable throwable) {
      throw new ESTException("Parsing WWW-Authentication header: " + throwable.getMessage(), throwable, paramESTResponse.getStatusCode(), new ByteArrayInputStream(paramESTResponse.getHeader("WWW-Authenticate").getBytes()));
    } 
    String str1 = null;
    try {
      str1 = eSTRequest.getURL().toURI().getPath();
    } catch (Exception exception) {
      throw new IOException("unable to process URL in request: " + exception.getMessage());
    } 
    for (String str : map.keySet()) {
      if (!validParts.contains(str))
        throw new ESTException("Unrecognised entry in WWW-Authenticate header: '" + str + "'"); 
    } 
    String str2 = eSTRequest.getMethod();
    String str3 = map.get("realm");
    String str4 = map.get("nonce");
    String str5 = map.get("opaque");
    String str6 = map.get("algorithm");
    String str7 = map.get("qop");
    ArrayList<String> arrayList = new ArrayList();
    if (this.realm != null && !this.realm.equals(str3))
      throw new ESTException("Supplied realm '" + this.realm + "' does not match server realm '" + str3 + "'", null, 401, null); 
    if (str6 == null)
      str6 = "MD5"; 
    if (str6.length() == 0)
      throw new ESTException("WWW-Authenticate no algorithm defined."); 
    str6 = Strings.toUpperCase(str6);
    if (str7 != null) {
      if (str7.length() == 0)
        throw new ESTException("QoP value is empty."); 
      str7 = Strings.toLowerCase(str7);
      String[] arrayOfString = str7.split(",");
      for (byte b = 0; b != arrayOfString.length; b++) {
        if (!arrayOfString[b].equals("auth") && !arrayOfString[b].equals("auth-int"))
          throw new ESTException("QoP value unknown: '" + b + "'"); 
        String str = arrayOfString[b].trim();
        if (!arrayList.contains(str))
          arrayList.add(str); 
      } 
    } else {
      throw new ESTException("Qop is not defined in WWW-Authenticate header.");
    } 
    AlgorithmIdentifier algorithmIdentifier = lookupDigest(str6);
    if (algorithmIdentifier == null || algorithmIdentifier.getAlgorithm() == null)
      throw new IOException("auth digest algorithm unknown: " + str6); 
    DigestCalculator digestCalculator1 = getDigestCalculator(str6, algorithmIdentifier);
    OutputStream outputStream1 = digestCalculator1.getOutputStream();
    String str8 = makeNonce(10);
    update(outputStream1, this.username);
    update(outputStream1, ":");
    update(outputStream1, str3);
    update(outputStream1, ":");
    update(outputStream1, this.password);
    outputStream1.close();
    byte[] arrayOfByte = digestCalculator1.getDigest();
    if (str6.endsWith("-SESS")) {
      DigestCalculator digestCalculator = getDigestCalculator(str6, algorithmIdentifier);
      OutputStream outputStream = digestCalculator.getOutputStream();
      String str = Hex.toHexString(arrayOfByte);
      update(outputStream, str);
      update(outputStream, ":");
      update(outputStream, str4);
      update(outputStream, ":");
      update(outputStream, str8);
      outputStream.close();
      arrayOfByte = digestCalculator.getDigest();
    } 
    String str9 = Hex.toHexString(arrayOfByte);
    DigestCalculator digestCalculator2 = getDigestCalculator(str6, algorithmIdentifier);
    OutputStream outputStream2 = digestCalculator2.getOutputStream();
    if (((String)arrayList.get(0)).equals("auth-int")) {
      DigestCalculator digestCalculator = getDigestCalculator(str6, algorithmIdentifier);
      OutputStream outputStream = digestCalculator.getOutputStream();
      eSTRequest.writeData(outputStream);
      outputStream.close();
      byte[] arrayOfByte1 = digestCalculator.getDigest();
      update(outputStream2, str2);
      update(outputStream2, ":");
      update(outputStream2, str1);
      update(outputStream2, ":");
      update(outputStream2, Hex.toHexString(arrayOfByte1));
    } else if (((String)arrayList.get(0)).equals("auth")) {
      update(outputStream2, str2);
      update(outputStream2, ":");
      update(outputStream2, str1);
    } 
    outputStream2.close();
    String str10 = Hex.toHexString(digestCalculator2.getDigest());
    DigestCalculator digestCalculator3 = getDigestCalculator(str6, algorithmIdentifier);
    OutputStream outputStream3 = digestCalculator3.getOutputStream();
    if (arrayList.contains("missing")) {
      update(outputStream3, str9);
      update(outputStream3, ":");
      update(outputStream3, str4);
      update(outputStream3, ":");
      update(outputStream3, str10);
    } else {
      update(outputStream3, str9);
      update(outputStream3, ":");
      update(outputStream3, str4);
      update(outputStream3, ":");
      update(outputStream3, "00000001");
      update(outputStream3, ":");
      update(outputStream3, str8);
      update(outputStream3, ":");
      if (((String)arrayList.get(0)).equals("auth-int")) {
        update(outputStream3, "auth-int");
      } else {
        update(outputStream3, "auth");
      } 
      update(outputStream3, ":");
      update(outputStream3, str10);
    } 
    outputStream3.close();
    String str11 = Hex.toHexString(digestCalculator3.getDigest());
    HashMap<Object, Object> hashMap = new HashMap<Object, Object>();
    hashMap.put("username", this.username);
    hashMap.put("realm", str3);
    hashMap.put("nonce", str4);
    hashMap.put("uri", str1);
    hashMap.put("response", str11);
    if (((String)arrayList.get(0)).equals("auth-int")) {
      hashMap.put("qop", "auth-int");
      hashMap.put("nc", "00000001");
      hashMap.put("cnonce", str8);
    } else if (((String)arrayList.get(0)).equals("auth")) {
      hashMap.put("qop", "auth");
      hashMap.put("nc", "00000001");
      hashMap.put("cnonce", str8);
    } 
    hashMap.put("algorithm", str6);
    if (str5 == null || str5.length() == 0)
      hashMap.put("opaque", makeNonce(20)); 
    ESTRequestBuilder eSTRequestBuilder = (new ESTRequestBuilder(eSTRequest)).withHijacker(null);
    eSTRequestBuilder.setHeader("Authorization", HttpUtil.mergeCSL("Digest", (Map)hashMap));
    return eSTRequest.getClient().doRequest(eSTRequestBuilder.build());
  }
  
  private DigestCalculator getDigestCalculator(String paramString, AlgorithmIdentifier paramAlgorithmIdentifier) throws IOException {
    DigestCalculator digestCalculator;
    try {
      digestCalculator = this.digestCalculatorProvider.get(paramAlgorithmIdentifier);
    } catch (OperatorCreationException operatorCreationException) {
      throw new IOException("cannot create digest calculator for " + paramString + ": " + operatorCreationException.getMessage());
    } 
    return digestCalculator;
  }
  
  private AlgorithmIdentifier lookupDigest(String paramString) {
    if (paramString.endsWith("-SESS"))
      paramString = paramString.substring(0, paramString.length() - "-SESS".length()); 
    return paramString.equals("SHA-512-256") ? new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256, (ASN1Encodable)DERNull.INSTANCE) : digestAlgorithmIdentifierFinder.find(paramString);
  }
  
  private void update(OutputStream paramOutputStream, char[] paramArrayOfchar) throws IOException {
    paramOutputStream.write(Strings.toUTF8ByteArray(paramArrayOfchar));
  }
  
  private void update(OutputStream paramOutputStream, String paramString) throws IOException {
    paramOutputStream.write(Strings.toUTF8ByteArray(paramString));
  }
  
  private String makeNonce(int paramInt) {
    byte[] arrayOfByte = new byte[paramInt];
    this.nonceGenerator.nextBytes(arrayOfByte);
    return Hex.toHexString(arrayOfByte);
  }
  
  static {
    HashSet<String> hashSet = new HashSet();
    hashSet.add("realm");
    hashSet.add("nonce");
    hashSet.add("opaque");
    hashSet.add("algorithm");
    hashSet.add("qop");
    validParts = Collections.unmodifiableSet(hashSet);
  }
}
