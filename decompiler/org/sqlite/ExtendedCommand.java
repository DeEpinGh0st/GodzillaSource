package org.sqlite;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.sqlite.core.DB;






























public class ExtendedCommand
{
  public static SQLExtension parse(String sql) throws SQLException {
    if (sql == null)
      return null; 
    if (sql.length() > 5 && sql.substring(0, 6).toLowerCase().equals("backup"))
      return BackupCommand.parse(sql); 
    if (sql.length() > 6 && sql.substring(0, 7).toLowerCase().equals("restore")) {
      return RestoreCommand.parse(sql);
    }
    return null;
  }





  
  public static String removeQuotation(String s) {
    if (s == null) {
      return s;
    }
    if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
      return s.substring(1, s.length() - 1);
    }
    return s;
  }
  
  public static interface SQLExtension
  {
    void execute(DB param1DB) throws SQLException;
  }
  
  public static class BackupCommand
    implements SQLExtension {
    public final String srcDB;
    public final String destFile;
    
    public BackupCommand(String srcDB, String destFile) {
      this.srcDB = srcDB;
      this.destFile = destFile;
    }
    
    private static Pattern backupCmd = Pattern.compile("backup(\\s+(\"[^\"]*\"|'[^']*'|\\S+))?\\s+to\\s+(\"[^\"]*\"|'[^']*'|\\S+)", 2);







    
    public static BackupCommand parse(String sql) throws SQLException {
      if (sql != null) {
        Matcher m = backupCmd.matcher(sql);
        if (m.matches()) {
          String dbName = ExtendedCommand.removeQuotation(m.group(2));
          String dest = ExtendedCommand.removeQuotation(m.group(3));
          if (dbName == null || dbName.length() == 0) {
            dbName = "main";
          }
          return new BackupCommand(dbName, dest);
        } 
      } 
      throw new SQLException("syntax error: " + sql);
    }
    
    public void execute(DB db) throws SQLException {
      db.backup(this.srcDB, this.destFile, null);
    }
  }
  
  public static class RestoreCommand
    implements SQLExtension
  {
    public final String targetDB;
    public final String srcFile;
    private static Pattern restoreCmd = Pattern.compile("restore(\\s+(\"[^\"]*\"|'[^']*'|\\S+))?\\s+from\\s+(\"[^\"]*\"|'[^']*'|\\S+)", 2);






    
    public RestoreCommand(String targetDB, String srcFile) {
      this.targetDB = targetDB;
      this.srcFile = srcFile;
    }






    
    public static RestoreCommand parse(String sql) throws SQLException {
      if (sql != null) {
        Matcher m = restoreCmd.matcher(sql);
        if (m.matches()) {
          String dbName = ExtendedCommand.removeQuotation(m.group(2));
          String dest = ExtendedCommand.removeQuotation(m.group(3));
          if (dbName == null || dbName.length() == 0)
            dbName = "main"; 
          return new RestoreCommand(dbName, dest);
        } 
      } 
      throw new SQLException("syntax error: " + sql);
    }



    
    public void execute(DB db) throws SQLException {
      db.restore(this.targetDB, this.srcFile, null);
    }
  }
}
