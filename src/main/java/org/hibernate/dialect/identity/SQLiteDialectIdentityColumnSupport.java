package org.hibernate.dialect.identity;

public class SQLiteDialectIdentityColumnSupport extends IdentityColumnSupportImpl {
  @Override
  public boolean supportsIdentityColumns() {
    return true;
  }

  /*
  public boolean supportsInsertSelectIdentity() {
    return true; // As specified in NHibernate dialect
  }
  */

  @Override
  public boolean hasDataTypeInIdentityColumn() {
    return false; // As specified in NHibernate dialect // FIXME true
  }

  /*
  public String appendIdentitySelectToInsert(String insertString) {
    return new StringBuffer(insertString.length()+30). // As specified in NHibernate dialect
      append(insertString).
      append("; ").append(getIdentitySelectString()).
      toString();
  }
  */

  @Override
  public String getIdentitySelectString(String table, String column, int type) {
    return "select last_insert_rowid()";
  }

  @Override
  public String getIdentityColumnString(int type) {
    // return "integer primary key autoincrement";
    return "integer"; // FIXME "autoincrement"
  }
}
