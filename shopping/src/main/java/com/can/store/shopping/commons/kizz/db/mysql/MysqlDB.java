package com.can.store.shopping.commons.kizz.db.mysql;

import com.can.store.shopping.commons.kizz.db.DataObject;
import com.can.store.shopping.commons.kizz.http.response.ResponsePaginate;
import com.can.store.shopping.commons.kizz.lib.utils.Paginate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class MysqlDB {
    private static Logger logger = LoggerFactory.getLogger(MysqlDB.class);
    /**
     * 数据库连接对象索引hash。用来归还数据库对象
     */
    protected String connectionHash;

    public String JDBC_DRIVER = "";
    public String JDBC_URL = "";
    public String JDBC_USER = "";
    public String JDBC_PASS = "";

    protected String _statement = "select";
    protected String _lastSql = "";

    protected Connection _dbconn;
    protected Statement _stmt;
    protected ResultSet _resultSet;

    /**
     * 插入数据对象
     */
    protected List<Map<String, Object>> _insert_data;
    protected Map<String, Object> _insertRow;

    /**
     * 更新数据对象
     */
    protected List<UpdateObject> _update_data;

    /**
     * 条件列表
     */
    protected WhereBuilder _whereBuilder;

    /**
     * 排序
     */
    protected ArrayList _orderBy;

    /**
     * 分组
     */
    protected ArrayList _groupBy;

    /**
     * 查询结果集
     */
    protected List<DataObject> _data_resultSet;

    /**
     * 查询结果以map返回
     */
    protected Map<String, DataObject> _data_resultSet_map;

    protected int _data_resultSet_count = 0;

    protected String _table = "";
    protected String _strWhere = "";

    protected String _strFields = "*";
    protected String _forUpdate = "";
    protected String _strLimit = "";
    protected String _strOrderBy = "";
    protected String _strGroupBy = "";

    protected boolean _insertIgnore = false;

    protected boolean _onTransAction = false;
    protected boolean _queryIsFail = false;

    protected long _last_insertId = -1;
    protected String _last_insertId_String = "";

    protected int _last_affectedRows = -1;
    protected int _last_matchedRows = -1;

    protected String _sqlState = "";

    protected String _strLeftJoin = "";
    protected boolean _with_leftJoin = false;
    protected List<LeftJoinObject> _leftJoinList;
    protected int _aliasChar = 65;

    /**
     * 数据表别名。用于连表操作时
     */
    protected String _tablealias = "";

    /**
     * 数据库错误信息对象
     */
    protected String _dbError;
    protected int _dbError_code = 0;

    public MysqlDB(String driver, String host, String user, String pass) {
        this.JDBC_DRIVER = driver;
        this.JDBC_URL = host;
        this.JDBC_USER = user;
        this.JDBC_PASS = pass;

        this._insert_data = new ArrayList<>();
        this._insertRow = new HashMap<>();
        this._update_data = new ArrayList<>();
        this._whereBuilder = WhereBuilder.getInstance();
        this._orderBy = new ArrayList();
        this._groupBy = new ArrayList();
        this._data_resultSet = new ArrayList<>();
        this._data_resultSet_map = new HashMap<>();
        this._leftJoinList = new ArrayList<>();
        this.connect();
    }

    public String[] getConfig() {
        String[] config = new String[4];
        config[0] = JDBC_URL;
        config[1] = JDBC_USER;
        config[2] = JDBC_PASS;
        config[3] = JDBC_DRIVER;
        return config;
    }

    /**
     * 连接数据库。
     * <p>
     * 在非事务提交的情况下，每次查询完成后马上将数据、错误消息提取，然后free释放数据
     * 库连接资源。如果需要查询多次，需要重新连接。
     * <p>
     * 在事务提交的情况下，回滚或提交之后，也会提取数据和错误信息，释放数据库资源。
     */
    public MysqlDB connect() {
        connectionHash = MysqlConnection.create(this.JDBC_DRIVER, this.JDBC_URL, this.JDBC_USER, this.JDBC_PASS, "");
        this._dbconn = MysqlConnection.getConnection(connectionHash);
        this.clear();
        return this;
    }

    public MysqlDB clear() {
        this._statement = "";
        this._table = "";
        this._strWhere = "";
        this._strFields = "*";
        this._forUpdate = "";
        this._strLimit = "";
        this._strGroupBy = "";
        this._strOrderBy = "";

        this._lastSql = "";

        this._insert_data.clear();
        this._insertRow.clear();
        this._update_data.clear();
        this._whereBuilder.clear();
        this._orderBy.clear();
        this._groupBy.clear();
        this._data_resultSet_count = 0;
        this._data_resultSet.clear();

        this._insertIgnore = false;
        //this._onTransAction=false;//只能在提交或回滚方法内修改
        this._queryIsFail = true;

        this._last_insertId = -1;
        this._last_insertId_String = "";
        this._last_affectedRows = -1;
        this._last_matchedRows = -1;
        this._sqlState = "";

        this._strLeftJoin = "";
        this._leftJoinList.clear();
        this._with_leftJoin = false;
        this._aliasChar = 65;
        this._tablealias = "";
        this._dbError_code = 0;

        if (null != this._stmt) {
            try {
                this._stmt.close();
                this._stmt = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public MysqlDB fields(String fieldlist) {
        this._statement = "select";
        this._strFields = fieldlist;
        return this;
    }

    public MysqlDB fields(String[] fieldlist) {
        this._statement = "select";
        String fields_str = "";
        String spr = "";
        for (int i = 0; i < fieldlist.length; i++) {
            fields_str += spr + fieldlist[i];
            spr = ",";
        }

        this._strFields = fields_str;
        return this;
    }

    public MysqlDB appendData(Map<String, Object> data) {
        Map<String, Object> row_t = new HashMap<>();
        row_t.putAll(data);
        this._insert_data.add(row_t);
        return this;
    }

    public MysqlDB appendData(List<Map<String, Object>> data) {
        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> row_t = new HashMap<>();
            row_t.putAll(data.get(i));
            this._insert_data.add(row_t);
        }
        return this;
    }

    public MysqlDB beginTransaction() {
        try {
            this._dbconn.setAutoCommit(false);
            this._onTransAction = true;
        } catch (SQLException e) {

        }
        return this;
    }

    public String realEscapeString(String sql) {
        return sql;
    }

    public String buildInsertSQL(String table, Map<String, Object> data, boolean ignore) {
        String spr = "";
        String str_field = "";
        String str_value = "";

        for (String field : data.keySet()) {
            str_field += spr + "`" + field + "`";

            String value = "";
            if (data.get(field) instanceof String) {
                value = (String) data.get(field);
            } else {
                value = "" + data.get(field);
            }
            str_value += spr + "'" + value + "'";
            spr = ",";
        }

        String sql_ignore = "";
        if (ignore) {
            sql_ignore = " ignore";
        }

        String sql = "insert" + sql_ignore + " into " + table + " (" + str_field + ") values (" + str_value + ")";
        return sql;
    }

    public String buildInsertSQL(String table, List<Map<String, Object>> data, boolean ignore) {
        if (0 == data.size()) {
            return "";
        }

        String sql_rows = "";
        String spr_rows = "";
        String str_field = "";
        int ii = 0;
        for (int jj = 0; jj < data.size(); jj++) {
            Map<String, Object> row = data.get(jj);
            String spr = "";
            String str_value = "";

            if (0 == row.size()) {
                return "";
            }
            for (String field : row.keySet()) {
                if (0 == ii) {
                    str_field += spr + "`" + field + "`";
                }
                String value = "";
                if (row.get(field) instanceof String) {
                    value = (String) row.get(field);
                } else {
                    value = "" + row.get(field);
                }
                str_value += spr + "'" + value + "'";
                spr = ",";
            }

            sql_rows += spr_rows + "(" + str_value + ")";
            spr_rows = ",";
            ii++;
        }

        String sql_ignore = "";
        if (ignore) {
            sql_ignore = " ignore";
        }
        String sql = "insert" + sql_ignore + " into " + table + " (" + str_field + ") values " + sql_rows;
        return sql;
    }

    public String buildUpdateSQL(String table, Map<String, Object> data) {
        String spr = "";
        String sql0 = "";

        for (String field : data.keySet()) {
            sql0 += spr + "`" + field + "`=" + data.get(field);
            spr = ",";
        }

        String sql = "update " + table + " set " + sql0;

        return sql;
    }

    public String buildUpdateSQL(String table, List<UpdateObject> data) {
        String spr = "";
        String sql0 = "";
        for (int i = 0; i < data.size(); i++) {
            UpdateObject one = data.get(i);
            if (one.exp.equals("")) {
                sql0 += spr + one.field + "='" + one.value + "'";
            } else {
                String field = one.field;
                if (!one.exp_field.equals("")) {
                    field = one.exp_field;
                }
                sql0 += spr + "`" + one.field + "`=" + field + one.exp + one.value;
            }
            spr = ",";
        }

        String sql = "update " + table + " set " + sql0;
        return sql;
    }

    public MysqlDB rollBack() {
        try {
            if (!this._dbconn.getAutoCommit()) {
                this._dbconn.rollback();
            }
            this._onTransAction = false;
            this._dbconn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MysqlDB commit() {
        try {
            this._dbconn.commit();
            this._dbconn.setAutoCommit(true);
            this._onTransAction = false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return this;
    }

    public MysqlDB count() {
        this._statement = "count";
        return this;
    }

    public MysqlDB count(String table) {
        this._statement = "count";
        this._table = table;
        return this;
    }

    public MysqlDB putUpdateData(String field, String operator, Object value) {
        UpdateObject obj = new UpdateObject();
        obj.exp = operator;
        obj.field = field;
        obj.value = value;
        this._update_data.add(obj);
        return this;
    }

    /**
     * @param field
     * @param value
     * @return
     */
    public MysqlDB put(String field, Object value) {
        return this.data(field, value);
    }

    /**
     * 如果是insert语句，此方法的参数应该是完整一行数据
     *
     * @param data
     * @return
     */
    public MysqlDB put(Map<String, Object> data) {
        return this.data(data);
    }

    public MysqlDB data(String field, Object value) {
        if (this._statement.equals("insert")) {
            this._insertRow.put(field, value);
        } else {
            Map<String, Object> one = new HashMap<>();
            one.put(field, value);
            this.data(one);
        }

        return this;
    }

    public MysqlDB appendToCurrRow(Map<String, Object> data) {
        if (this._statement.equals("update")) {
            this.data(data);
        } else if (this._statement.equals("insert")) {
            for (String key : data.keySet()) {
                this._insertRow.put(key, data.get(key));
            }
        }

        return this;
    }

    public MysqlDB data(Map<String, Object> data) {
        if (this._statement.equals("update")) {
            for (String field : data.keySet()) {
                UpdateObject one = new UpdateObject();
                one.field = field;
                one.value = "";
                if (data.get(field) instanceof String) {
                    one.value = (String) data.get(field);
                } else {
                    one.value = "" + data.get(field);
                }
                this._update_data.add(one);
            }
        } else if (this._statement.equals("insert")) {
            Map<String, Object> data_swap = new HashMap<>();
            data_swap.putAll(data);
            this._insert_data.add(data_swap);
        }

        return this;
    }

    /**
     * 如果是insert语句，此方法的参数应该是多行完整的数据
     *
     * @param data
     * @return
     */
    public MysqlDB data(List<Map<String, Object>> data) {
        if (this._statement.equals("insert")) {
            for (int i = 0; i < data.size(); i++) {
                Map<String, Object> data_swap = new HashMap<>();
                data_swap.putAll(data.get(i));
                this._insert_data.add(data_swap);
            }
        }

        return this;
    }

    public MysqlDB appendNewRow() {
        if (this._insertRow.size() > 0) {
            //不可以clear！map浅复制，在类作用域之内clear，后面insert语句将不能使用数据
            Map<String, Object> row = new HashMap<>();
            row.putAll(this._insertRow);
            this._insert_data.add(row);
            this._insertRow.clear();
        }
        return this;
    }

    public int dbErrorCode() {
        return this._dbError_code;
    }

    public String dbError() {
        return this._sqlState;
    }

    public String dbErrorMessage() {
        return this._dbError;
    }

    public boolean dbErrorDuplicateEntry() {
        return 1062 == this._dbError_code;
    }

    public Map<String, String> dbErrorDuplicateInfo() {
        if (1062 != this._dbError_code) {
            return null;
        }

        //Duplicate entry '12' for key 'name'
        Pattern p = Pattern.compile("Duplicate entry '(.*)' for key '(\\w+)'");
        Matcher m = p.matcher(this._dbError);
        if (!m.find()) {
            return null;
        }
        String field = m.group(2);
        String value = m.group(1);
        p = Pattern.compile("-[^-]*$");
        m = p.matcher(value);
        if (m.find()) {
            value = m.replaceAll("");
        }
        Map<String, String> result = new HashMap<>();
        result.put("field", field);
        result.put("value", value);
        return result;
    }

    public MysqlDB delete(String table) {
        this._statement = "delete";
        this._table = table;
        return this;
    }

    public void free() {
        //System.out.println("MysqlDB::free called.");
        if (this._onTransAction) {
            this.rollBack();
            //throw new Exception("事务未提交");
        }

        try {
            if (null != this._resultSet) {
                this._resultSet.close();
                this._resultSet = null;
            }

            if (null != this._stmt) {
                this._stmt.close();
                this._stmt = null;
            }

            if (null != connectionHash) {
                MysqlConnection.returnResource(connectionHash);
                connectionHash = null;
                _dbconn = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isOnTransAction() {
        return this._onTransAction;
    }

    public boolean isClosed() {
        if (null == this._dbconn) {
            return true;
        }
        try {
            return this._dbconn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    protected void finalize() {
        //System.out.println("MysqlDB:finalize");
        this.free();
    }

    public DataObject first() {
        this.limit(1, 1).get();
        if (null == this._data_resultSet || 0 == this._data_resultSet.size()
                || null == this._data_resultSet.get(0).data
        ) {
            return null;
        }

        DataObject result = DataObject.getInstance();
        result.data.putAll(this._data_resultSet.get(0).data);
        return result;
    }

    public MysqlDB forUpdate() {
        this._forUpdate = " for update ";
        return this;
    }

    public MysqlDB from(String tablename) {
        if ("count" == this._statement) {
            //this._statement="select";
        }
        this._table = tablename;
        return this;
    }

    public MysqlDB from(String tablename, String tablealias) {
        this._table = tablename;
        this._tablealias = tablealias;
        return this;
    }

    public int getCountResult() {
        if (this.queryIsFalse()) {
            return 0;
        }

        return this._data_resultSet_count;
    }

    public int getCount() {
        String sql = this.getRawSql();
        this.query(sql);
        if (this.queryIsFalse()) {
            return 0;
        }

        return this._data_resultSet_count;
    }

    public List<DataObject> get() {
        String sql = this.getRawSql();
        this.query(sql);

        if (null == this._data_resultSet || 0 == this._data_resultSet.size()) {
            return null;
        }

        //List<DataObject> result = this._data_resultSet;
        List<DataObject> result = new ArrayList<>();
        result.addAll(this._data_resultSet);//阻断引用的影响
        return result;
    }

    public Map<String, DataObject> getMap(String key) {
        String sql = this.getRawSql();
        this.queryMap(sql, key);

        if (null == this._data_resultSet_map || 0 == this._data_resultSet_map.size()) {
            return null;
        }
        Map<String, DataObject> result = new HashMap<>();
        result.putAll(this._data_resultSet_map);//阻断引用的影响
        return result;
    }

    public int getLastAffectedRows() {
        return this._last_affectedRows;
    }

    public long getLastInsertId() {
        return this._last_insertId;
    }

    public String getLastInsertIdString() {
        return this._last_insertId_String;
    }

    public String getLastSql() {
        return this._lastSql;
    }

    /**
     *
     */
    public List<DataObject> getResultSet() {
        if (this._data_resultSet.size() == 0) {
            return null;
        }

        List<DataObject> result = new ArrayList<>();
        result.addAll(this._data_resultSet);
        return result;
    }

    /**
     * 已多行的map形式返回结果集合
     */
    public List<Map<String, Object>> getResultArray() {
        if (this._data_resultSet.size() == 0) {
            return null;
        }

        List<Map<String, Object>> datalist = new ArrayList<>();
        for (int i = 0; i < this._data_resultSet.size(); i++) {
            datalist.add(this._data_resultSet.get(i).data);
        }
        return datalist;
    }

    public String buildWhere() {
        return this._whereBuilder.buildWhere();
    }

    protected MysqlDB _compileGroup() {
        if (0 == this._groupBy.size()) {
            return this;
        }

        String spr = "";
        for (int i = 0; i < this._groupBy.size(); i++) {
            if (0 == i) {
                this._strGroupBy = " GROUP BY " + this._groupBy.get(i);
            } else {
                this._strGroupBy += spr + this._groupBy.get(i);
            }
            spr = ",";
        }

        return this;
    }

    protected MysqlDB _compileLeftJoin() {
        if (0 == this._leftJoinList.size()) {
            return this;
        }

        if (this._tablealias.equals("")) {
            this._tablealias = "" + ((char) this._aliasChar++);
        }
        this._strLeftJoin = " " + this._tablealias + " ";
        for (int i = 0; i < this._leftJoinList.size(); i++) {
            LeftJoinObject obj = this._leftJoinList.get(i);
            String sqlParsed = "";
            if (0 < obj.mField2sField.size()) {
                String spr_t = "";
                for (String mField_t : obj.mField2sField.keySet()) {
                    String sField_t = obj.mField2sField.get(mField_t);

                    if (-1 == mField_t.indexOf(".")) {
                        mField_t = this._tablealias + "." + mField_t;
                    }
                    String[] sArr = sField_t.split("\\.");
                    if (sArr.length > 1) {
                        sField_t = sArr[1];
                    }
                    sField_t = obj.aliasname + "." + sField_t;

                    sqlParsed += spr_t + mField_t + "=" + sField_t;
                    spr_t = " AND ";
                }
            } else {
                String mField_t = obj.mField;
                if (-1 == mField_t.indexOf(".")) {
                    mField_t = this._tablealias + "." + mField_t;
                }
                //String field = obj.sField;
                String[] sArr = obj.sField.split("\\.");
                if (sArr.length > 1) {
                    obj.sField = sArr[1];
                }
                String sField_t = obj.aliasname + "." + obj.sField;
                sqlParsed = mField_t + "=" + sField_t;
            }

            this._strLeftJoin += " LEFT JOIN " + obj.tablename + " " + obj.aliasname + " on " + sqlParsed;
        }
        return this;
    }

    protected MysqlDB _compileOrder() {
        if (0 == this._orderBy.size()) {
            return this;
        }

        String spr = "";
        for (int i = 0; i < this._orderBy.size(); i++) {
            if (0 == i) {
                this._strOrderBy = " ORDER BY " + this._orderBy.get(i);
            } else {
                this._strOrderBy += spr + this._orderBy.get(i);
            }
            spr = ",";
        }

        return this;
    }

    protected MysqlDB _compileWhere() {
        this._strWhere = this.buildWhere();
        if (!this._strWhere.equals("")) {
            this._strWhere = " WHERE " + this._strWhere;
        }
        return this;
    }

    public String getRawSql() {
        String sql = "";
        this._compileWhere();
        if (this._statement.equals("update")) {
            if (this._strWhere.equals("")) {
                this._queryIsFail = true;
                this._sqlState = "试图执行无条件update操作";
                //throw new Exception(this._sqlState);
                logger.error(this._sqlState);
                return null;
            }
            sql = this.buildUpdateSQL(this._table, this._update_data);
            sql += this._strWhere;
        } else if (this._statement.equals("delete")) {
            if (this._strWhere.equals("")) {
                this._queryIsFail = true;
                this._sqlState = "试图执行无条件的删除操作";
                //throw new Exception(this._sqlState);
                logger.error(this._sqlState);
                return null;
            }
            sql = "delete from " + this._table + this._strWhere;
        } else if (this._statement.equals("insert")) {
            this.appendNewRow();
            if (0 == this._insert_data.size()) {
                this._queryIsFail = true;
                this._sqlState = "insert操作语句为空";
                //throw new Exception(this._sqlState);
                logger.error(this._sqlState);
                return null;
            }

            sql = this.buildInsertSQL(this._table, this._insert_data, this._insertIgnore);
        } else if (this._statement.equals("count")) {
            this._compileGroup();
            sql = "select count(*) as count from " + this._table
                    + " " + this._tablealias + " "
                    + this._strWhere
                    + this._strGroupBy;
        } else if (this._statement.equals("select")) {
            this._compileLeftJoin();
            this._compileGroup();
            this._compileOrder();

            String alias_fix = "";
            if (!StringUtils.isBlank(this._tablealias)) {
                if (StringUtils.isBlank(this._strLeftJoin)) {
                    alias_fix = " " + this._tablealias + " ";
                }
            }
            String fields = this._strFields.replace("`", "");
            sql = "SELECT " + fields + " FROM " + this._table
                    + alias_fix
                    + this._strLeftJoin
                    + this._strWhere
                    + this._strGroupBy
                    + this._strOrderBy
                    + this._strLimit
                    + this._forUpdate;
        }

        //logger.error("getRawSql:"+sql);
        logger.debug("getRawSql:" + sql);
        return sql;
    }

    /**
     * 分组
     *
     * @param group
     * @return
     */
    public MysqlDB groupBy(String group) {
        this._groupBy.add(group);
        return this;
    }

    public MysqlDB insert(String table) {
        this._statement = "insert";
        this._table = table;
        return this;
    }

    public MysqlDB insertIgnore(String table) {
        this._statement = "insert";
        this._insertIgnore = true;
        this._table = table;
        return this;
    }

    public boolean isRawField(String field) {
        return null != field && "`" == field.substring(0, 0);
    }

    public MysqlDB limit(Integer page, Integer rows) {
        if (null == page) {
            page = 1;
        }
        if (null == rows) {
            rows = 20;
        }
        this._strLimit = " LIMIT " + ((page - 1) * rows) + "," + rows;
        return this;
    }

    public MysqlDB orderBy(String order) {
        this._orderBy.add(order);
        return this;
    }

    public ResponsePaginate paginate(Integer page, Integer rows) {
        ResponsePaginate res = ResponsePaginate.getInstance();
        this.count().get();
        int count = this.getCountResult();
        if (null == page) {
            page = 1;
        }
        if (null == rows) {
            rows = 20;
        }

        if (0 < count) {
            this._statement = "select";
            res.data.paginate = new Paginate();
            if (res.data.paginate.init(count, rows, page)) {
                this.limit(page, rows).get();
                res.data.datalist = this.getResultArray();
                if (this.queryIsFalse()) {
                    res.message = "Database Query Error ";
                    res.code = 500;
                    res.status = 500;
                } else {
                    res.message = "success";
                    res.code = 0;
                    res.status = 0;
                }
            } else {
                res.message = "Out of range";
                res.code = 400;
                res.status = 400;
            }
        } else {
            if (this.queryIsFalse()) {
                res.message = "Database query error";
                res.status = 501;
                res.code = 501;
            } else {
                res.message = "empty ";
                res.status = 0;
                res.code = 0;
            }
        }

        return res;
    }

    public MysqlDB wideQuery(String statement, String sql) {
        this._statement = statement;
        return this.query(sql);
    }

    public MysqlDB query(String sql) {
        if (null == sql || sql.equals("")) {
            this._queryIsFail = true;
            this._sqlState = "查询语句为空";
            return this;
        }

        this._lastSql = sql;
        if (null == this._dbconn) {
            this._queryIsFail = true;
            this._sqlState = "连接数据库失败";
            return this;
        }
        this._queryIsFail = false;
        try {
            this._stmt = this._dbconn.createStatement();
            if ("insert" == this._statement) {
                long beginT = (new java.util.Date()).getTime();
                this._last_matchedRows = this._stmt.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
                long endT = (new java.util.Date()).getTime();
                if (endT - beginT >= 2000) {
                    // 慢查询
                    logger.info("慢查询 >>> " + sql);
                }
                this._last_affectedRows = this._stmt.getUpdateCount();
                this._last_insertId = -1;
                ResultSet rs = this._stmt.getGeneratedKeys();
                if (rs.next()) {
                    this._last_insertId = rs.getLong(1);
                }
            } else if ("delete" == this._statement || "update" == this._statement) {
                this._resultSet = null;

                long beginT = (new java.util.Date()).getTime();
                this._last_affectedRows = this._stmt.executeUpdate(sql);
                long endT = (new java.util.Date()).getTime();
                if (endT - beginT >= 2000) {
                    // 慢查询
                    logger.info("慢查询 >>> " + sql);
                }

                //this._last_affectedRows=this._stmt.getUpdateCount();
            } else if (this._statement.equals("count")) {
                this._data_resultSet_count = 0;

                long beginT = System.currentTimeMillis();
                this._resultSet = this._stmt.executeQuery(sql);
                long endT = System.currentTimeMillis();
                if (endT - beginT >= 2000) {
                    // 慢查询
                    logger.info("慢查询 >>> " + sql);
                }

                if (null != this._resultSet) {
                    this._resultSet.next();
                    this._data_resultSet_count = this._resultSet.getInt(1);
                }
            } else if (this._statement.equals("select")) {
                long beginT = System.currentTimeMillis();
                this._resultSet = this._stmt.executeQuery(sql);
                long endT = System.currentTimeMillis();
                if (endT - beginT >= 2000) {
                    // 慢查询
                    logger.info("慢查询 >>> " + sql);
                }

                if (null != this._resultSet) {
                    ResultSetMetaData metaData = this._resultSet.getMetaData();
                    while (this._resultSet.next()) {
                        DataObject dm = DataObject.getInstance();
                        for (int i = 0; i < metaData.getColumnCount(); i++) {
                            // resultSet数据下标从1开始
                            String columnName = metaData.getColumnName(i + 1);
                            Object value = this._resultSet.getObject(i + 1);
                            dm.put(columnName, value);
                        }
                        if (0 == dm.data.size()) {
                            continue;
                        }
                        this._data_resultSet.add(dm);
                    }
                }
            }
        }
//        catch (MySQLTransactionRollbackException e){
//
//        }
        catch (SQLException e) {
            this._dbError = e.getMessage();
            this._queryIsFail = true;
            this._sqlState = e.getSQLState();
            this._dbError_code = e.getErrorCode();
            e.printStackTrace();
            logger.error("失败的SQL >>> " + this._lastSql);
        }

        return this;
    }

    public MysqlDB queryMap(String sql, String key) {
        if (null == sql || sql.equals("")) {
            this._queryIsFail = true;
            this._sqlState = "查询语句为空";
            return this;
        }

        this._lastSql = sql;
        if (null == this._dbconn) {
            this._queryIsFail = true;
            this._sqlState = "连接数据库失败";
            return this;
        }
        this._queryIsFail = false;
        try {
            this._stmt = this._dbconn.createStatement();
            if (this._statement.equals("select")) {
                long beginT = System.currentTimeMillis();
                this._resultSet = this._stmt.executeQuery(sql);
                long endT = System.currentTimeMillis();
                if (endT - beginT >= 2000) {
                    // 慢查询
                    logger.info("慢查询 >>> " + sql);
                }

                if (null != this._resultSet) {
                    ResultSetMetaData metaData = this._resultSet.getMetaData();
                    while (this._resultSet.next()) {
                        DataObject dm = DataObject.getInstance();
                        for (int i = 0; i < metaData.getColumnCount(); i++) {
                            // resultSet数据下标从1开始
                            String columnName = metaData.getColumnName(i + 1);
                            Object value = this._resultSet.getObject(i + 1);
                            dm.put(columnName, value);
                        }
                        if (0 == dm.data.size()) {
                            continue;
                        }
                        String keyValue = dm.getString(key);
                        this._data_resultSet_map.put(keyValue, dm);
                    }
                }
            }
        } catch (SQLException e) {
            this._dbError = e.getMessage();
            this._queryIsFail = true;
            this._sqlState = e.getSQLState();
            this._dbError_code = e.getErrorCode();
            e.printStackTrace();
            logger.error("失败的SQL >>> " + this._lastSql);
        }

        return this;
    }

    public boolean queryIsFalse() {
        return this._queryIsFail;
    }

    public String rawField(String field) {
        return "`" + field + "`";
    }

    public boolean save() {
        String sql = this.getRawSql();
        if (null == sql) {
            return false;
        }
        return !(this.query(sql).queryIsFalse());
    }

    public MysqlDB select() {
        return this.select("*");
    }

    public MysqlDB select(String fields) {
        if (StringUtils.isBlank(fields)) {
            fields = "*";
        }
        this._statement = "select";
        this._strFields = fields;
        return this;
    }

    public MysqlDB update(String table, List<UpdateObject> data) {
        this.update(table);
        if (null != data && 0 < data.size()) {
            for (int i = 0; i < data.size(); i++) {
                this._update_data.add(data.get(i));
            }
        }
        return this;
    }

    public MysqlDB update(String table, UpdateObject data) {
        this.update(table);
        if (null != data) {
            this._update_data.add(data);
        }
        return this;
    }

    public MysqlDB update(String table) {
        this._statement = "update";
        this._table = table;
        return this;
    }

    /**
     * 同时设置多个键值对为条件，每个键值对之间依据clause实情以and或or连接
     *
     * @param statements
     * @param clause
     * @return
     */
    public MysqlDB where(Map<String, Object> statements, boolean clause) {
        this._whereBuilder.where(statements, clause);
        return this;
    }

    public MysqlDB where(WhereClause clause) {
        this._whereBuilder.where(clause);
        return this;
    }

    public MysqlDB where(List<WhereClause> clauses) {
        this._whereBuilder.where(clauses);
        return this;
    }

    public MysqlDB subWwhere(String field, String operator, String subWhere) {
        this._whereBuilder.subWhere(field, operator, subWhere);
        return this;
    }

    public MysqlDB subWhere(String operator, String subWhere) {
        this._whereBuilder.subWhere(operator, subWhere);
        return this;
    }

    public MysqlDB where(String field, String operator, Object filter) {
        this._whereBuilder.where(field, operator, filter);
        return this;
    }

    public MysqlDB where(String field, Object filter) {
        this._whereBuilder.where(field, filter);
        return this;
    }

    public MysqlDB whereIn(String field, Collection filterSet) {
        this._whereBuilder.whereIn(field, filterSet);
        return this;
    }

    public MysqlDB whereInSubQuery(String field, String subQuery) {
        this._whereBuilder.whereInSubQuery(field, subQuery);
        return this;
    }

    public MysqlDB whereIn(String field, MysqlDB subQuery) {
        String sql = subQuery.getRawSql();
        if (null == sql) {
            return this;
        }

        this._whereBuilder.whereInSubQuery(field, sql);
        return this;
    }

    public MysqlDB whereNotIn(String field, Collection filterSet) {
        this._whereBuilder.whereNotIn(field, filterSet);
        return this;
    }

    public MysqlDB whereNotInSubQuery(String field, String subQuery) {
        this._whereBuilder.whereNotInSubQuery(field, subQuery);
        return this;
    }

    public MysqlDB whereNotIn(String field, MysqlDB subQuery) {
        String sql = subQuery.getRawSql();
        if (null == sql) {
            return this;
        }

        this._whereBuilder.whereNotInSubQuery(field, sql);
        return this;
    }

    public MysqlDB subWhereOr(String field, String operator, String subWhere) {
        this._whereBuilder.subWhereOr(field, operator, subWhere);
        return this;
    }

    public MysqlDB subWhereOr(String operator, String subWhere) {
        this._whereBuilder.subWhereOr(operator, subWhere);
        return this;
    }

    public MysqlDB whereOr(String field, Integer filter) {
        this._whereBuilder.whereOr(field, filter);
        return this;
    }

    public MysqlDB whereOr(String field, Object filter) {
        this._whereBuilder.whereOr(field, filter);
        return this;
    }

    public MysqlDB whereOr(String field, String operator, Object filter) {
        this._whereBuilder.whereOr(field, operator, filter);
        return this;
    }

    public MysqlDB whereOrIn(String field, ArrayList filterSet) {
        this._whereBuilder.whereOrIn(field, filterSet);
        return this;
    }

    public MysqlDB whereOrInSubQuery(String field, String subQuery) {
        this._whereBuilder.whereOrInSubQuery(field, subQuery);
        return this;
    }

    public MysqlDB whereOrIn(String field, MysqlDB subQuery) {
        String sql = subQuery.getRawSql();
        if (null == sql) {
            return this;
        }

        this._whereBuilder.whereOrInSubQuery(field, sql);
        return this;
    }

    public MysqlDB whereOrNotIn(String field, ArrayList filterSet) {
        this._whereBuilder.whereOrNotIn(field, filterSet);
        return this;
    }

    public MysqlDB whereOrNotInSubQuery(String field, String subQuery) {
        this._whereBuilder.whereOrNotInSubQuery(field, subQuery);
        return this;
    }

    public MysqlDB whereOrNotIn(String field, MysqlDB subQuery) {
        String sql = subQuery.getRawSql();
        if (null == sql) {
            return this;
        }

        this._whereBuilder.whereOrNotInSubQuery(field, sql);
        return this;
    }

    /**
     * 连表操作
     * <p>
     * //left join T b on a.f1=b.f1
     * <p>
     * select("a.f1,a.f2,b.f1 as b_f1, b.f2 as b_f2,c.f1 as c_f1")
     * .from("T1", "a")
     * .leftjoin("T2","b","f1","f1")
     * .leftjoin("T3","c","f1","f1")
     * .where("a.f1", 1)
     * .get()
     *
     * @param table  连接目标数据表
     * @param alias  连接目标数据表别名。如果查询字段列表需要指定部分字段的，别名不应该留空
     * @param mField 主表on操作的字段名，a.f1
     * @param sField 连接目标数据表on操作的字段名，b.f1
     * @return
     */
    public MysqlDB leftJoin(String table, String alias, String mField, String sField) {
        if (alias.equals("")) {
            alias = "t" + ((char) this._aliasChar++);
        }
        LeftJoinObject obj = LeftJoinObject.getInstance(table, alias, mField, sField);
        this._leftJoinList.add(obj);
        return this;
    }

    public MysqlDB leftJoin(String table, String alias, Map<String, String> mField2sField) {
        if (alias.equals("")) {
            alias = "t" + ((char) this._aliasChar++);
        }
        LeftJoinObject obj = LeftJoinObject.getInstance(table, alias, null, null);
        obj.mField2sField = mField2sField;
        this._leftJoinList.add(obj);
        return this;
    }

    /**
     * 连表操作连表的选择字段
     *
     * @param fieldlist
     * @return
     */
    public MysqlDB joinFields(String fieldlist) {
        String prefix = "";
        if (!this._strFields.equals("")) {
            prefix = ",";
        }

        this._strFields += prefix + fieldlist;
        return this;
    }

    /**
     * 连表操作连表的选择字段
     *
     * @param fieldlist
     * @return
     */
    public MysqlDB joinFields(String[] fieldlist) {
        String fields_str = "";
        String spr = "";
        if (!this._strFields.equals("")) {
            spr = ",";
        }

        for (int i = 0; i < fieldlist.length; i++) {
            fields_str += spr + fieldlist[i];
            spr = ",";
        }

        this._strFields = fields_str;
        return this;
    }

    /**
     * 展开t.*这样的字段列表为模型的字段列表。
     * <p>
     * 注意： 需要配合模型使用，并且必须在select("*")/fields(...)之后方法调用
     *
     * @param fieldlist
     * @param tableAlias
     * @return
     */
    public MysqlDB expandModelFields(String[] fieldlist, String tableAlias) {
        String pattern_str = "\\*";
        if (null != tableAlias && !tableAlias.equals("")) {
            tableAlias = tableAlias + ".";
        } else {
            tableAlias = "";
        }
        pattern_str = tableAlias + pattern_str;
        Pattern p = Pattern.compile(pattern_str);
        Matcher m = p.matcher(this._strFields);
        if (m.find()) {
            String fieldlist_str = "";
            String spr = "";
            for (int i = 0; i < fieldlist.length; i++) {
                fieldlist_str += spr + tableAlias + fieldlist[i];
                spr = ",";
            }
            this._strFields = m.replaceAll(fieldlist_str);
        }
        return this;
    }

    /**
     * 剔除select字段列表中的某些字段。
     * <p>
     * 注意： 需要配合模型使用，并且必须在expandModelFields(...)之后方法调用
     * <p>
     * select("*").excludeFields("f1,f2")....
     * select("a.*,b.*").excludeFields("a.f1,b.f3")....
     * select().fields("a.*,b.*").excludeFields("a.f1,b.f1")...
     *
     * @return
     */
    public MysqlDB excluceFields(String fieldlist) {
        // 不支持复杂函数结果作为字段,例如,对于ifnull(field,0) as field_alias 这类是会出错的。
        // 在设计数据库时，所有字段都应该设置为not null
        return excluceFields(fieldlist.split(","));
    }

    public MysqlDB excluceFields(String[] fieldlist) {
        for (int i = 0; i < fieldlist.length; i++) {
            String pattern_str = "(?:\\w+\\.)?\\w(?:\\s+as\\s+\\w+)?";
            Pattern p0 = Pattern.compile("^\\s*" + pattern_str + "\\s*,");
            Matcher m0 = p0.matcher(this._strFields);
            if (m0.find()) {
                this._strFields = m0.replaceAll("");
            } else {
                Pattern p1 = Pattern.compile(",\\s*" + pattern_str);
                Matcher m1 = p1.matcher(this._strFields);
                if (m1.find()) {
                    this._strFields = m1.replaceAll("");
                }
            }
        }
        return this;
    }
}
