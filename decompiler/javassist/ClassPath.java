package javassist;

import java.io.InputStream;
import java.net.URL;

public interface ClassPath {
  InputStream openClassfile(String paramString) throws NotFoundException;
  
  URL find(String paramString);
}
