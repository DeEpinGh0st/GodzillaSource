package core.socksServer;

import com.httpProxy.server.request.HttpRequest;
import com.httpProxy.server.response.HttpResponse;
import com.httpProxy.server.response.HttpResponseStatus;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import util.functions;




public class SimpleHttpRequestHandle
  implements HttpRequestHandle
{
  public HttpResponse sendHttpRequest(HttpRequest httpRequest) {
    HttpResponse ret = null;
    try {
      HttpURLConnection httpURLConnection = (HttpURLConnection)(new URL(httpRequest.getUrl())).openConnection();
      
      httpURLConnection.setRequestMethod(httpRequest.getMethod());
      httpURLConnection.setDoInput(true);
      httpURLConnection.setDoOutput(true);
      List<String[]> headers = httpRequest.getHttpRequestHeader().getHeaders();
      for (int i = 0; i < headers.size(); i++) {
        String[] hk = headers.get(i);
        httpURLConnection.setRequestProperty(hk[0], hk[1]);
      } 
      httpURLConnection.getOutputStream().write(httpRequest.getRequestData());
      httpURLConnection.getOutputStream().flush();
      HttpResponse httpResponse = new HttpResponse(new HttpResponseStatus(httpURLConnection.getResponseCode(), httpURLConnection.getResponseMessage()));
      Map<String, List<String>> headerFields = httpURLConnection.getHeaderFields();
      Iterator<String> iterator = headerFields.keySet().iterator();
      while (iterator.hasNext()) {
        String next = iterator.next();
        if (next != null) {
          List<String> values = headerFields.get(next);
          for (int j = 0; j < values.size(); j++) {
            String v = values.get(j);
            httpResponse.getHttpResponseHeader().addHeader(next, v);
          } 
        } 
      } 
      
      httpResponse.setResponseData(functions.readInputStream(httpURLConnection.getInputStream()));
      ret = httpResponse;
    } catch (Exception e) {
      e.printStackTrace();
    } 
    return ret;
  }
}
