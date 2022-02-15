package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.util.Arrays;

public abstract class AbstractTlsServer extends AbstractTlsPeer implements TlsServer {
  protected TlsCipherFactory cipherFactory;
  
  protected TlsServerContext context;
  
  protected ProtocolVersion clientVersion;
  
  protected int[] offeredCipherSuites;
  
  protected short[] offeredCompressionMethods;
  
  protected Hashtable clientExtensions;
  
  protected boolean encryptThenMACOffered;
  
  protected short maxFragmentLengthOffered;
  
  protected boolean truncatedHMacOffered;
  
  protected Vector supportedSignatureAlgorithms;
  
  protected boolean eccCipherSuitesOffered;
  
  protected int[] namedCurves;
  
  protected short[] clientECPointFormats;
  
  protected short[] serverECPointFormats;
  
  protected ProtocolVersion serverVersion;
  
  protected int selectedCipherSuite;
  
  protected short selectedCompressionMethod;
  
  protected Hashtable serverExtensions;
  
  public AbstractTlsServer() {
    this(new DefaultTlsCipherFactory());
  }
  
  public AbstractTlsServer(TlsCipherFactory paramTlsCipherFactory) {
    this.cipherFactory = paramTlsCipherFactory;
  }
  
  protected boolean allowEncryptThenMAC() {
    return true;
  }
  
  protected boolean allowTruncatedHMac() {
    return false;
  }
  
  protected Hashtable checkServerExtensions() {
    return this.serverExtensions = TlsExtensionsUtils.ensureExtensionsInitialised(this.serverExtensions);
  }
  
  protected abstract int[] getCipherSuites();
  
  protected short[] getCompressionMethods() {
    return new short[] { 0 };
  }
  
  protected ProtocolVersion getMaximumVersion() {
    return ProtocolVersion.TLSv11;
  }
  
  protected ProtocolVersion getMinimumVersion() {
    return ProtocolVersion.TLSv10;
  }
  
  protected boolean supportsClientECCCapabilities(int[] paramArrayOfint, short[] paramArrayOfshort) {
    if (paramArrayOfint == null)
      return TlsECCUtils.hasAnySupportedNamedCurves(); 
    for (byte b = 0; b < paramArrayOfint.length; b++) {
      int i = paramArrayOfint[b];
      if (NamedCurve.isValid(i) && (!NamedCurve.refersToASpecificNamedCurve(i) || TlsECCUtils.isSupportedNamedCurve(i)))
        return true; 
    } 
    return false;
  }
  
  public void init(TlsServerContext paramTlsServerContext) {
    this.context = paramTlsServerContext;
  }
  
  public void notifyClientVersion(ProtocolVersion paramProtocolVersion) throws IOException {
    this.clientVersion = paramProtocolVersion;
  }
  
  public void notifyFallback(boolean paramBoolean) throws IOException {
    if (paramBoolean && getMaximumVersion().isLaterVersionOf(this.clientVersion))
      throw new TlsFatalAlert((short)86); 
  }
  
  public void notifyOfferedCipherSuites(int[] paramArrayOfint) throws IOException {
    this.offeredCipherSuites = paramArrayOfint;
    this.eccCipherSuitesOffered = TlsECCUtils.containsECCCipherSuites(this.offeredCipherSuites);
  }
  
  public void notifyOfferedCompressionMethods(short[] paramArrayOfshort) throws IOException {
    this.offeredCompressionMethods = paramArrayOfshort;
  }
  
  public void processClientExtensions(Hashtable paramHashtable) throws IOException {
    this.clientExtensions = paramHashtable;
    if (paramHashtable != null) {
      this.encryptThenMACOffered = TlsExtensionsUtils.hasEncryptThenMACExtension(paramHashtable);
      this.maxFragmentLengthOffered = TlsExtensionsUtils.getMaxFragmentLengthExtension(paramHashtable);
      if (this.maxFragmentLengthOffered >= 0 && !MaxFragmentLength.isValid(this.maxFragmentLengthOffered))
        throw new TlsFatalAlert((short)47); 
      this.truncatedHMacOffered = TlsExtensionsUtils.hasTruncatedHMacExtension(paramHashtable);
      this.supportedSignatureAlgorithms = TlsUtils.getSignatureAlgorithmsExtension(paramHashtable);
      if (this.supportedSignatureAlgorithms != null && !TlsUtils.isSignatureAlgorithmsExtensionAllowed(this.clientVersion))
        throw new TlsFatalAlert((short)47); 
      this.namedCurves = TlsECCUtils.getSupportedEllipticCurvesExtension(paramHashtable);
      this.clientECPointFormats = TlsECCUtils.getSupportedPointFormatsExtension(paramHashtable);
    } 
  }
  
  public ProtocolVersion getServerVersion() throws IOException {
    if (getMinimumVersion().isEqualOrEarlierVersionOf(this.clientVersion)) {
      ProtocolVersion protocolVersion = getMaximumVersion();
      if (this.clientVersion.isEqualOrEarlierVersionOf(protocolVersion))
        return this.serverVersion = this.clientVersion; 
      if (this.clientVersion.isLaterVersionOf(protocolVersion))
        return this.serverVersion = protocolVersion; 
    } 
    throw new TlsFatalAlert((short)70);
  }
  
  public int getSelectedCipherSuite() throws IOException {
    Vector vector = TlsUtils.getUsableSignatureAlgorithms(this.supportedSignatureAlgorithms);
    boolean bool = supportsClientECCCapabilities(this.namedCurves, this.clientECPointFormats);
    int[] arrayOfInt = getCipherSuites();
    for (byte b = 0; b < arrayOfInt.length; b++) {
      int i = arrayOfInt[b];
      if (Arrays.contains(this.offeredCipherSuites, i) && (bool || !TlsECCUtils.isECCCipherSuite(i)) && TlsUtils.isValidCipherSuiteForVersion(i, this.serverVersion) && TlsUtils.isValidCipherSuiteForSignatureAlgorithms(i, vector))
        return this.selectedCipherSuite = i; 
    } 
    throw new TlsFatalAlert((short)40);
  }
  
  public short getSelectedCompressionMethod() throws IOException {
    short[] arrayOfShort = getCompressionMethods();
    for (byte b = 0; b < arrayOfShort.length; b++) {
      if (Arrays.contains(this.offeredCompressionMethods, arrayOfShort[b]))
        return this.selectedCompressionMethod = arrayOfShort[b]; 
    } 
    throw new TlsFatalAlert((short)40);
  }
  
  public Hashtable getServerExtensions() throws IOException {
    if (this.encryptThenMACOffered && allowEncryptThenMAC() && TlsUtils.isBlockCipherSuite(this.selectedCipherSuite))
      TlsExtensionsUtils.addEncryptThenMACExtension(checkServerExtensions()); 
    if (this.maxFragmentLengthOffered >= 0 && MaxFragmentLength.isValid(this.maxFragmentLengthOffered))
      TlsExtensionsUtils.addMaxFragmentLengthExtension(checkServerExtensions(), this.maxFragmentLengthOffered); 
    if (this.truncatedHMacOffered && allowTruncatedHMac())
      TlsExtensionsUtils.addTruncatedHMacExtension(checkServerExtensions()); 
    if (this.clientECPointFormats != null && TlsECCUtils.isECCCipherSuite(this.selectedCipherSuite)) {
      this.serverECPointFormats = new short[] { 0, 1, 2 };
      TlsECCUtils.addSupportedPointFormatsExtension(checkServerExtensions(), this.serverECPointFormats);
    } 
    return this.serverExtensions;
  }
  
  public Vector getServerSupplementalData() throws IOException {
    return null;
  }
  
  public CertificateStatus getCertificateStatus() throws IOException {
    return null;
  }
  
  public CertificateRequest getCertificateRequest() throws IOException {
    return null;
  }
  
  public void processClientSupplementalData(Vector paramVector) throws IOException {
    if (paramVector != null)
      throw new TlsFatalAlert((short)10); 
  }
  
  public void notifyClientCertificate(Certificate paramCertificate) throws IOException {
    throw new TlsFatalAlert((short)80);
  }
  
  public TlsCompression getCompression() throws IOException {
    switch (this.selectedCompressionMethod) {
      case 0:
        return new TlsNullCompression();
    } 
    throw new TlsFatalAlert((short)80);
  }
  
  public TlsCipher getCipher() throws IOException {
    int i = TlsUtils.getEncryptionAlgorithm(this.selectedCipherSuite);
    int j = TlsUtils.getMACAlgorithm(this.selectedCipherSuite);
    return this.cipherFactory.createCipher(this.context, i, j);
  }
  
  public NewSessionTicket getNewSessionTicket() throws IOException {
    return new NewSessionTicket(0L, TlsUtils.EMPTY_BYTES);
  }
}
