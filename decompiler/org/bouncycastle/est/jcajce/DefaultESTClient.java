package org.bouncycastle.est.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.est.ESTClient;
import org.bouncycastle.est.ESTClientSourceProvider;
import org.bouncycastle.est.ESTException;
import org.bouncycastle.est.ESTRequest;
import org.bouncycastle.est.ESTRequestBuilder;
import org.bouncycastle.est.ESTResponse;
import org.bouncycastle.est.Source;
import org.bouncycastle.util.Properties;

class DefaultESTClient implements ESTClient {
  private static final Charset utf8 = Charset.forName("UTF-8");
  
  private static byte[] CRLF = new byte[] { 13, 10 };
  
  private final ESTClientSourceProvider sslSocketProvider;
  
  public DefaultESTClient(ESTClientSourceProvider paramESTClientSourceProvider) {
    this.sslSocketProvider = paramESTClientSourceProvider;
  }
  
  private static void writeLine(OutputStream paramOutputStream, String paramString) throws IOException {
    paramOutputStream.write(paramString.getBytes());
    paramOutputStream.write(CRLF);
  }
  
  public ESTResponse doRequest(ESTRequest paramESTRequest) throws IOException {
    ESTResponse eSTResponse = null;
    ESTRequest eSTRequest = paramESTRequest;
    byte b = 15;
    do {
      eSTResponse = performRequest(eSTRequest);
      eSTRequest = redirectURL(eSTResponse);
    } while (eSTRequest != null && --b > 0);
    if (b == 0)
      throw new ESTException("Too many redirects.."); 
    return eSTResponse;
  }
  
  protected ESTRequest redirectURL(ESTResponse paramESTResponse) throws IOException {
    ESTRequest eSTRequest = null;
    if (paramESTResponse.getStatusCode() >= 300 && paramESTResponse.getStatusCode() <= 399) {
      String str;
      ESTRequestBuilder eSTRequestBuilder;
      URL uRL;
      switch (paramESTResponse.getStatusCode()) {
        case 301:
        case 302:
        case 303:
        case 306:
        case 307:
          str = paramESTResponse.getHeader("Location");
          if ("".equals(str))
            throw new ESTException("Redirect status type: " + paramESTResponse.getStatusCode() + " but no location header"); 
          eSTRequestBuilder = new ESTRequestBuilder(paramESTResponse.getOriginalRequest());
          if (str.startsWith("http")) {
            eSTRequest = eSTRequestBuilder.withURL(new URL(str)).build();
            break;
          } 
          uRL = paramESTResponse.getOriginalRequest().getURL();
          eSTRequest = eSTRequestBuilder.withURL(new URL(uRL.getProtocol(), uRL.getHost(), uRL.getPort(), str)).build();
          break;
        default:
          throw new ESTException("Client does not handle http status code: " + paramESTResponse.getStatusCode());
      } 
    } 
    if (eSTRequest != null)
      paramESTResponse.close(); 
    return eSTRequest;
  }
  
  public ESTResponse performRequest(ESTRequest paramESTRequest) throws IOException {
    ESTResponse eSTResponse = null;
    Source source = null;
    try {
      source = this.sslSocketProvider.makeSource(paramESTRequest.getURL().getHost(), paramESTRequest.getURL().getPort());
      if (paramESTRequest.getListener() != null)
        paramESTRequest = paramESTRequest.getListener().onConnection(source, paramESTRequest); 
      OutputStream outputStream = null;
      Set set = Properties.asKeySet("org.bouncycastle.debug.est");
      if (set.contains("output") || set.contains("all")) {
        outputStream = new PrintingOutputStream(source.getOutputStream());
      } else {
        outputStream = source.getOutputStream();
      } 
      String str = paramESTRequest.getURL().getPath() + ((paramESTRequest.getURL().getQuery() != null) ? paramESTRequest.getURL().getQuery() : "");
      ESTRequestBuilder eSTRequestBuilder = new ESTRequestBuilder(paramESTRequest);
      Map map = paramESTRequest.getHeaders();
      if (!map.containsKey("Connection"))
        eSTRequestBuilder.addHeader("Connection", "close"); 
      URL uRL = paramESTRequest.getURL();
      if (uRL.getPort() > -1) {
        eSTRequestBuilder.setHeader("Host", String.format("%s:%d", new Object[] { uRL.getHost(), Integer.valueOf(uRL.getPort()) }));
      } else {
        eSTRequestBuilder.setHeader("Host", uRL.getHost());
      } 
      ESTRequest eSTRequest = eSTRequestBuilder.build();
      writeLine(outputStream, eSTRequest.getMethod() + " " + str + " HTTP/1.1");
      for (Map.Entry entry : eSTRequest.getHeaders().entrySet()) {
        String[] arrayOfString = (String[])entry.getValue();
        for (byte b = 0; b != arrayOfString.length; b++)
          writeLine(outputStream, (String)entry.getKey() + ": " + arrayOfString[b]); 
      } 
      outputStream.write(CRLF);
      outputStream.flush();
      eSTRequest.writeData(outputStream);
      outputStream.flush();
      if (eSTRequest.getHijacker() != null) {
        eSTResponse = eSTRequest.getHijacker().hijack(eSTRequest, source);
        return eSTResponse;
      } 
      eSTResponse = new ESTResponse(eSTRequest, source);
      return eSTResponse;
    } finally {
      if (source != null && eSTResponse == null)
        source.close(); 
    } 
  }
  
  private class PrintingOutputStream extends OutputStream {
    private final OutputStream tgt;
    
    public PrintingOutputStream(OutputStream param1OutputStream) {
      this.tgt = param1OutputStream;
    }
    
    public void write(int param1Int) throws IOException {
      System.out.print(String.valueOf((char)param1Int));
      this.tgt.write(param1Int);
    }
  }
}
