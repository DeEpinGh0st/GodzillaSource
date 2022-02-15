package util.http;

import com.httpProxy.server.response.HttpResponseHeader;
import com.httpProxy.server.response.HttpResponseStatus;
import core.ApplicationContext;
import core.shell.ShellEntity;
import core.ui.component.dialog.HttpProgressBar;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;











public class HttpResponse
{
  private byte[] result;
  private final ShellEntity shellEntity;
  private Map<String, List<String>> headerMap;
  private String message;
  private int responseCode;
  
  public byte[] getResult() {
    return this.result;
  }
  public Map<String, List<String>> getHeaderMap() {
    return this.headerMap;
  }
  public void setResult(byte[] result) {
    this.result = result;
  }
  public void setHeaderMap(Map<String, List<String>> headerMap) {
    this.headerMap = headerMap;
  }
  
  public HttpResponse(HttpURLConnection http, ShellEntity shellEntity) throws IOException {
    this.shellEntity = shellEntity;
    handleHeader(http.getHeaderFields());
    ReadAllData(getInputStream(http));
  }
  protected void handleHeader(Map<String, List<String>> map) {
    this.headerMap = map;
    try {
      this.message = ((List<String>)map.get(null)).get(0);
      Http http = this.shellEntity.getHttp();
      http.getCookieManager().put(http.getUri(), map);
      http.getCookieManager().getCookieStore().get(http.getUri());
      
      List<HttpCookie> cookies = http.getCookieManager().getCookieStore().get(http.getUri());
      StringBuilder sb = new StringBuilder();
      
      cookies.forEach(cookie -> sb.append(String.format(" %s=%s;", new Object[] { cookie.getName(), cookie.getValue() })));
      
      if (sb.length() > 0) {
        this.shellEntity.getHeaders().put("Cookie", sb.toString().trim());
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  protected InputStream getInputStream(HttpURLConnection httpURLConnection) throws IOException {
    InputStream inputStream = httpURLConnection.getErrorStream();
    if (inputStream != null) {
      return inputStream;
    }
    return httpURLConnection.getInputStream();
  }
  
  protected void ReadAllData(InputStream inputStream) throws IOException {
    int maxLen = 0;
    try {
      if (this.headerMap.get("Content-Length") != null && ((List)this.headerMap.get("Content-Length")).size() > 0) {
        maxLen = Integer.parseInt(((List<String>)this.headerMap.get("Content-Length")).get(0));
        this.result = ReadKnownNumData(inputStream, maxLen);
      } else {
        this.result = ReadUnknownNumData(inputStream);
      } 
    } catch (NumberFormatException e) {
      this.result = ReadUnknownNumData(inputStream);
    } 
    this.result = this.shellEntity.getCryptionModule().decode(this.result);
  }

  
  protected byte[] ReadKnownNumData(InputStream inputStream, int num) throws IOException {
    if (num > 0) {
      byte[] temp = new byte[5120];
      int readOneNum = 0;
      int readNum = 0;
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      Boolean isShowBar = ApplicationContext.isShowHttpProgressBar.get();
      if (isShowBar != null && isShowBar.booleanValue()) {
        HttpProgressBar httpProgressBar = new HttpProgressBar("download threadId:" + Thread.currentThread().getId(), num);
        while ((readOneNum = inputStream.read(temp)) != -1) {
          bos.write(temp, 0, readOneNum);
          readNum += readOneNum;
          httpProgressBar.setValue(readNum);
        } 
      } else {
        while ((readOneNum = inputStream.read(temp)) != -1) {
          bos.write(temp, 0, readOneNum);
        }
      } 
      return bos.toByteArray();
    } 
    if (num == 0) {
      return ReadUnknownNumData(inputStream);
    }
    return null;
  }







  
  protected byte[] ReadUnknownNumData(InputStream inputStream) throws IOException {
    byte[] temp = new byte[5120];
    int readOneNum = 0;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    while ((readOneNum = inputStream.read(temp)) != -1) {
      bos.write(temp, 0, readOneNum);
    }
    return bos.toByteArray();
  }


  
  public com.httpProxy.server.response.HttpResponse parseHttpResponse() {
    com.httpProxy.server.response.HttpResponse httpResponse = new com.httpProxy.server.response.HttpResponse(new HttpResponseStatus(this.responseCode));
    httpResponse.setResponseData(this.result);
    HttpResponseHeader responseHeader = httpResponse.getHttpResponseHeader();
    
    Iterator<String> headerKeys = this.headerMap.keySet().iterator();
    
    while (headerKeys.hasNext()) {
      String keyString = headerKeys.next();
      
      List<String> headList = this.headerMap.get(keyString);
      if (headList != null) {
        headList.parallelStream().forEach(v -> responseHeader.addHeader(keyString, v));
      }
    } 

    
    return httpResponse;
  }
}
