package org.springframework.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;












































public class UrlResource
  extends AbstractFileResolvingResource
{
  @Nullable
  private final URI uri;
  private final URL url;
  @Nullable
  private volatile URL cleanedUrl;
  
  public UrlResource(URI uri) throws MalformedURLException {
    Assert.notNull(uri, "URI must not be null");
    this.uri = uri;
    this.url = uri.toURL();
  }




  
  public UrlResource(URL url) {
    Assert.notNull(url, "URL must not be null");
    this.uri = null;
    this.url = url;
  }







  
  public UrlResource(String path) throws MalformedURLException {
    Assert.notNull(path, "Path must not be null");
    this.uri = null;
    this.url = new URL(path);
    this.cleanedUrl = getCleanedUrl(this.url, path);
  }










  
  public UrlResource(String protocol, String location) throws MalformedURLException {
    this(protocol, location, null);
  }












  
  public UrlResource(String protocol, String location, @Nullable String fragment) throws MalformedURLException {
    try {
      this.uri = new URI(protocol, location, fragment);
      this.url = this.uri.toURL();
    }
    catch (URISyntaxException ex) {
      MalformedURLException exToThrow = new MalformedURLException(ex.getMessage());
      exToThrow.initCause(ex);
      throw exToThrow;
    } 
  }








  
  private static URL getCleanedUrl(URL originalUrl, String originalPath) {
    String cleanedPath = StringUtils.cleanPath(originalPath);
    if (!cleanedPath.equals(originalPath)) {
      try {
        return new URL(cleanedPath);
      }
      catch (MalformedURLException malformedURLException) {}
    }

    
    return originalUrl;
  }




  
  private URL getCleanedUrl() {
    URL cleanedUrl = this.cleanedUrl;
    if (cleanedUrl != null) {
      return cleanedUrl;
    }
    cleanedUrl = getCleanedUrl(this.url, ((this.uri != null) ? this.uri : this.url).toString());
    this.cleanedUrl = cleanedUrl;
    return cleanedUrl;
  }










  
  public InputStream getInputStream() throws IOException {
    URLConnection con = this.url.openConnection();
    ResourceUtils.useCachesIfNecessary(con);
    try {
      return con.getInputStream();
    }
    catch (IOException ex) {
      
      if (con instanceof HttpURLConnection) {
        ((HttpURLConnection)con).disconnect();
      }
      throw ex;
    } 
  }




  
  public URL getURL() {
    return this.url;
  }





  
  public URI getURI() throws IOException {
    if (this.uri != null) {
      return this.uri;
    }
    
    return super.getURI();
  }


  
  public boolean isFile() {
    if (this.uri != null) {
      return isFile(this.uri);
    }
    
    return super.isFile();
  }







  
  public File getFile() throws IOException {
    if (this.uri != null) {
      return getFile(this.uri);
    }
    
    return super.getFile();
  }







  
  public Resource createRelative(String relativePath) throws MalformedURLException {
    return new UrlResource(createRelativeURL(relativePath));
  }








  
  protected URL createRelativeURL(String relativePath) throws MalformedURLException {
    if (relativePath.startsWith("/")) {
      relativePath = relativePath.substring(1);
    }
    
    relativePath = StringUtils.replace(relativePath, "#", "%23");
    
    return new URL(this.url, relativePath);
  }





  
  public String getFilename() {
    return StringUtils.getFilename(getCleanedUrl().getPath());
  }




  
  public String getDescription() {
    return "URL [" + this.url + "]";
  }





  
  public boolean equals(@Nullable Object other) {
    return (this == other || (other instanceof UrlResource && 
      getCleanedUrl().equals(((UrlResource)other).getCleanedUrl())));
  }




  
  public int hashCode() {
    return getCleanedUrl().hashCode();
  }
}
