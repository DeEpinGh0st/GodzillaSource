package org.bouncycastle.x509;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.Collection;
import org.bouncycastle.x509.util.StreamParser;
import org.bouncycastle.x509.util.StreamParsingException;

public class X509StreamParser implements StreamParser {
  private Provider _provider;
  
  private X509StreamParserSpi _spi;
  
  public static X509StreamParser getInstance(String paramString) throws NoSuchParserException {
    try {
      X509Util.Implementation implementation = X509Util.getImplementation("X509StreamParser", paramString);
      return createParser(implementation);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new NoSuchParserException(noSuchAlgorithmException.getMessage());
    } 
  }
  
  public static X509StreamParser getInstance(String paramString1, String paramString2) throws NoSuchParserException, NoSuchProviderException {
    return getInstance(paramString1, X509Util.getProvider(paramString2));
  }
  
  public static X509StreamParser getInstance(String paramString, Provider paramProvider) throws NoSuchParserException {
    try {
      X509Util.Implementation implementation = X509Util.getImplementation("X509StreamParser", paramString, paramProvider);
      return createParser(implementation);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new NoSuchParserException(noSuchAlgorithmException.getMessage());
    } 
  }
  
  private static X509StreamParser createParser(X509Util.Implementation paramImplementation) {
    X509StreamParserSpi x509StreamParserSpi = (X509StreamParserSpi)paramImplementation.getEngine();
    return new X509StreamParser(paramImplementation.getProvider(), x509StreamParserSpi);
  }
  
  private X509StreamParser(Provider paramProvider, X509StreamParserSpi paramX509StreamParserSpi) {
    this._provider = paramProvider;
    this._spi = paramX509StreamParserSpi;
  }
  
  public Provider getProvider() {
    return this._provider;
  }
  
  public void init(InputStream paramInputStream) {
    this._spi.engineInit(paramInputStream);
  }
  
  public void init(byte[] paramArrayOfbyte) {
    this._spi.engineInit(new ByteArrayInputStream(paramArrayOfbyte));
  }
  
  public Object read() throws StreamParsingException {
    return this._spi.engineRead();
  }
  
  public Collection readAll() throws StreamParsingException {
    return this._spi.engineReadAll();
  }
}
