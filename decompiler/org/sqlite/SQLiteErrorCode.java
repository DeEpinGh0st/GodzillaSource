package org.sqlite;
































public enum SQLiteErrorCode
{
  UNKNOWN_ERROR(-1, "unknown error"),
  SQLITE_OK(0, "Successful result"),
  
  SQLITE_ERROR(1, "SQL error or missing database"),
  SQLITE_INTERNAL(2, "Internal logic error in SQLite"),
  SQLITE_PERM(3, " Access permission denied"),
  SQLITE_ABORT(4, " Callback routine requested an abort"),
  SQLITE_BUSY(5, " The database file is locked"),
  SQLITE_LOCKED(6, " A table in the database is locked"),
  SQLITE_NOMEM(7, " A malloc() failed"),
  SQLITE_READONLY(8, " Attempt to write a readonly database"),
  SQLITE_INTERRUPT(9, " Operation terminated by sqlite3_interrupt()"),
  SQLITE_IOERR(10, " Some kind of disk I/O error occurred"),
  SQLITE_CORRUPT(11, " The database disk image is malformed"),
  SQLITE_NOTFOUND(12, " NOT USED. Table or record not found"),
  SQLITE_FULL(13, " Insertion failed because database is full"),
  SQLITE_CANTOPEN(14, " Unable to open the database file"),
  SQLITE_PROTOCOL(15, " NOT USED. Database lock protocol error"),
  SQLITE_EMPTY(16, " Database is empty"),
  SQLITE_SCHEMA(17, " The database schema changed"),
  SQLITE_TOOBIG(18, " String or BLOB exceeds size limit"),
  SQLITE_CONSTRAINT(19, " Abort due to constraint violation"),
  SQLITE_MISMATCH(20, " Data type mismatch"),
  SQLITE_MISUSE(21, " Library used incorrectly"),
  SQLITE_NOLFS(22, " Uses OS features not supported on host"),
  SQLITE_AUTH(23, " Authorization denied"),
  SQLITE_FORMAT(24, " Auxiliary database format error"),
  SQLITE_RANGE(25, " 2nd parameter to sqlite3_bind out of range"),
  SQLITE_NOTADB(26, " File opened that is not a database file"),
  SQLITE_ROW(100, " sqlite3_step() has another row ready"),
  SQLITE_DONE(101, " sqlite3_step() has finished executing"),
  
  SQLITE_BUSY_RECOVERY(261, " Another process is busy recovering a WAL mode database file following a crash"),
  SQLITE_LOCKED_SHAREDCACHE(262, " Contention with a different database connection that shares the cache"),
  SQLITE_READONLY_RECOVERY(264, " The database file needs to be recovered"),
  SQLITE_IOERR_READ(266, " I/O error in the VFS layer while trying to read from a file on disk"),
  SQLITE_CORRUPT_VTAB(267, " Content in the virtual table is corrupt"),
  SQLITE_CONSTRAINT_CHECK(275, " A CHECK constraint failed"),
  SQLITE_ABORT_ROLLBACK(516, " The transaction that was active when the SQL statement first started was rolled back"),
  SQLITE_BUSY_SNAPSHOT(517, " Another database connection has already written to the database"),
  SQLITE_READONLY_CANTLOCK(520, " The shared-memory file associated with that database is read-only"),
  SQLITE_IOERR_SHORT_READ(522, " The VFS layer was unable to obtain as many bytes as was requested"),
  SQLITE_CANTOPEN_ISDIR(526, " The file is really a directory"),
  SQLITE_CONSTRAINT_COMMITHOOK(531, " A commit hook callback returned non-zero"),
  SQLITE_READONLY_ROLLBACK(776, "  Hot journal needs to be rolled back"),
  SQLITE_IOERR_WRITE(778, " I/O error in the VFS layer while trying to write to a file on disk"),
  SQLITE_CANTOPEN_FULLPATH(782, " The operating system was unable to convert the filename into a full pathname"),
  SQLITE_CONSTRAINT_FOREIGNKEY(787, " A foreign key constraint failed"),
  SQLITE_READONLY_DBMOVED(1032, " The database file has been moved since it was opened"),
  SQLITE_IOERR_FSYNC(1034, " I/O error in the VFS layer while trying to flush previously written content"),
  SQLITE_CANTOPEN_CONVPATH(1038, " cygwin_conv_path() system call failed while trying to open a file"),
  SQLITE_CONSTRAINT_FUNCTION(1043, " Error reported by extension function"),
  SQLITE_IOERR_DIR_FSYNC(1290, " I/O error in the VFS layer while trying to invoke fsync() on a directory"),
  SQLITE_CONSTRAINT_NOTNULL(1299, " A NOT NULL constraint failed"),
  SQLITE_IOERR_TRUNCATE(1546, " I/O error in the VFS layer while trying to truncate a file to a smaller size"),
  SQLITE_CONSTRAINT_PRIMARYKEY(1555, " A PRIMARY KEY constraint failed"),
  SQLITE_IOERR_FSTAT(1802, " I/O error in the VFS layer while trying to invoke fstat()"),
  SQLITE_CONSTRAINT_TRIGGER(1811, " A RAISE function within a trigger fired, causing the SQL statement to abort"),
  SQLITE_IOERR_UNLOCK(2058, " I/O error within xUnlock"),
  SQLITE_CONSTRAINT_UNIQUE(2067, " A UNIQUE constraint failed"),
  SQLITE_IOERR_RDLOCK(2314, " I/O error within xLock"),
  SQLITE_CONSTRAINT_VTAB(2323, " Error reported by application-defined virtual table"),
  SQLITE_IOERR_DELETE(2570, " I/O error within xDelete"),
  SQLITE_CONSTRAINT_ROWID(2579, " rowid is not unique"),
  SQLITE_IOERR_NOMEM(3082, " Unable to allocate sufficient memory"),
  SQLITE_IOERR_ACCESS(3338, " I/O error within the xAccess"),
  SQLITE_IOERR_CHECKRESERVEDLOCK(3594, " I/O error within xCheckReservedLock"),
  SQLITE_IOERR_LOCK(3850, " I/O error in the advisory file locking logic"),
  SQLITE_IOERR_CLOSE(4106, " I/O error within xClose"),
  SQLITE_IOERR_SHMOPEN(4618, " I/O error within xShmMap while trying to open a new shared memory segment"),
  SQLITE_IOERR_SHMSIZE(4874, " I/O error within xShmMap while trying to resize an existing shared memory segment"),
  SQLITE_IOERR_SHMMAP(5386, " I/O error within xShmMap while trying to map a shared memory segment"),
  SQLITE_IOERR_SEEK(5642, " I/O error while trying to seek a file descriptor"),
  SQLITE_IOERR_DELETE_NOENT(5898, " The file being deleted does not exist"),
  SQLITE_IOERR_MMAP(6154, " I/O error while trying to map or unmap part of the database file"),
  SQLITE_IOERR_GETTEMPPATH(6410, " Unable to determine a suitable directory in which to place temporary files"),
  SQLITE_IOERR_CONVPATH(6666, " cygwin_conv_path() system call failed");


  
  public final int code;

  
  public final String message;


  
  SQLiteErrorCode(int code, String message) {
    this.code = code;
    this.message = message;
  }





  
  public static SQLiteErrorCode getErrorCode(int errorCode) {
    for (SQLiteErrorCode each : values()) {
      
      if (errorCode == each.code)
        return each; 
    } 
    return UNKNOWN_ERROR;
  }





  
  public String toString() {
    return String.format("[%s] %s", new Object[] { name(), this.message });
  }
}
