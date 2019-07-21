package com.can.store.shopping.commons.kizz.db.mysql;

import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class WhereBuilder {
    /**
     * 条件列表
     */
    protected List<WhereClause> _where;

    public WhereBuilder clear() {
        if (null == this._where) {
            this._where = new ArrayList<>();
        } else {
            this._where.clear();
        }
        return this;
    }

    public static WhereBuilder getInstance() {
        WhereBuilder builder = new WhereBuilder();
        builder._where = new ArrayList<>();
        return builder;
    }

    /**
     * 同时设置多个键值对为条件，每个键值对之间依据clause实情以and或or连接
     *
     * @param statements
     * @param clause
     * @return
     */
    public WhereBuilder where(Map<String, Object> statements, boolean clause) {
        for (String field : statements.keySet()) {
            WhereClause where = WhereClause.getInsance(field);
            where.clause = clause;
            where.filter = statements.get(field);
            this._where.add(where);
        }
        return this;
    }

    public WhereBuilder where(WhereClause clause) {
        this._where.add(clause);
        return this;
    }

    public WhereBuilder subWhere(String field, String operator, String subWhere) {
        WhereClause where = WhereClause.getInsance();
        where.subQuery = subWhere;
        if (null != operator) {
            where.operator = operator;
        }
        where.field = field;
        this._where.add(where);
        return this;
    }

    public WhereBuilder subWhere(String operator, String subWhere) {
        WhereClause where = WhereClause.getInsance();
        where.subQuery = subWhere;
        if (null != operator) {
            where.operator = operator;
        }
        this._where.add(where);
        return this;
    }

    public WhereBuilder where(List<WhereClause> clauses) {
        for (int i = 0; i < clauses.size(); i++) {
            this._where.add(clauses.get(i));
        }
        return this;
    }

    public WhereBuilder where(String field, String operator, Object filter) {
        WhereClause where = WhereClause.getInsance(field);
        where.operator = operator;
        where.filter = filter;
        this._where.add(where);
        return this;
    }

    public WhereBuilder where(String field, Object filter) {
        WhereClause where = WhereClause.getInsance(field);
        where.filter = filter;
        this._where.add(where);
        return this;
    }

    public WhereBuilder whereIn(String field, Collection filterSet) {
        WhereClause where = WhereClause.getInsance(field);
        where.operator = "IN";
        where.filterSet = filterSet;
        this._where.add(where);
        return this;
    }

    public WhereBuilder whereInSubQuery(String field, String subQuery) {
        WhereClause where = WhereClause.getInsance(field);
        where.operator = "IN";
        where.subQuery = subQuery;
        this._where.add(where);
        return this;
    }

    public WhereBuilder whereNotIn(String field, Collection filterSet) {
        WhereClause where = WhereClause.getInsance(field);
        where.operator = "NOT IN";
        where.filterSet = filterSet;
        this._where.add(where);
        return this;
    }

    public WhereBuilder whereNotInSubQuery(String field, String subQuery) {
        WhereClause where = WhereClause.getInsance(field);
        where.operator = "NOT IN";
        where.subQuery = subQuery;
        this._where.add(where);
        return this;
    }

    public WhereBuilder subWhereOr(String field, String operator, String subWhere) {
        WhereClause where = WhereClause.getInsance();
        where.subQuery = subWhere;
        where.clause = false;
        if (null != operator) {
            where.operator = operator;
        }
        where.field = field;
        this._where.add(where);
        return this;
    }

    public WhereBuilder subWhereOr(String operator, String subWhere) {
        WhereClause where = WhereClause.getInsance();
        where.subQuery = subWhere;
        where.clause = false;
        if (null != operator) {
            where.operator = operator;
        }
        this._where.add(where);
        return this;
    }

    public WhereBuilder whereOr(String field, Integer filter) {
        return this.whereOr(field, "" + filter);
    }

    public WhereBuilder whereOr(String field, Object filter) {
        WhereClause where = WhereClause.getInsance(field);
        where.clause = false;
        where.filter = filter;
        this._where.add(where);
        return this;
    }

    public WhereBuilder whereOr(String field, String operator, Object filter) {
        WhereClause where = WhereClause.getInsance(field);
        where.operator = operator;
        where.clause = false;
        where.filter = filter;
        this._where.add(where);
        return this;
    }

    public WhereBuilder whereOrIn(String field, ArrayList filterSet) {
        WhereClause where = WhereClause.getInsance(field);
        where.operator = "in";
        where.clause = false;
        where.filterSet = filterSet;
        this._where.add(where);
        return this;
    }

    public WhereBuilder whereOrInSubQuery(String field, String subQuery) {
        WhereClause where = WhereClause.getInsance(field);
        where.operator = "IN";
        where.clause = false;
        where.subQuery = subQuery;
        this._where.add(where);
        return this;
    }

    public WhereBuilder whereOrNotIn(String field, ArrayList filterSet) {
        WhereClause where = WhereClause.getInsance(field);
        where.operator = "not in";
        where.clause = false;
        where.filterSet = filterSet;
        this._where.add(where);
        return this;
    }

    public WhereBuilder whereOrNotInSubQuery(String field, String subQuery) {
        WhereClause where = WhereClause.getInsance(field);
        where.operator = "NOT IN";
        where.clause = false;
        where.subQuery = subQuery;
        this._where.add(where);
        return this;
    }

    public String buildWhere() {
        return this.buildWhere(this._where);
    }

    public String buildWhere(List<WhereClause> where) {
        if (null == where || 0 == where.size()) {
            return "";
        }

        String spr = "";
        String sql = "";

        try {
            for (int i = 0; i < where.size(); i++) {
                if (StringUtils.isBlank(where.get(i).field) && StringUtils.isBlank(where.get(i).subQuery)) {
                    continue;
                }

                WhereClause clause = where.get(i);
                if (0 < i) {
                    if (clause.clause) {
                        spr = " AND ";
                    } else {
                        spr = " OR ";
                    }
                }

                String field = null == clause.field ? "" : clause.field.trim();
                String filter = null == clause.filter ? "" : clause.filter.toString().trim();
                String operator = null == clause.operator ? "" : clause.operator.trim().toUpperCase();

                String filter_t = filter.toUpperCase();
                if (filter_t.equals("IS NULL") || filter_t.equals("IS NOT NULL")) {
                    //sql+=spr+"("+field+" "+filter+")";
                    sql += spr + field + " " + filter_t;
                    continue;
                }

                //
                //注意：
                // 不论是否subQuery和filter都为空，都不要忽略不要抛出错误，即不能掩盖使用者的意图。
                //  如果没有传入正确的参数，构造出的sql就是错误的，查询时会得到错误提示。
                //

                //if(operator.equals("") && !field.equals("")){
                if (operator.equals("") && !field.equals("")) {
                    //字段不为空时才设定默认的操作符。
                    // 多个条件的结果作为条件，或者子查询作为条件时，字段和操作符都可能是空的
                    // where field1=true and (field1=false or field2=true)
                    operator = "=";
                }

                if (operator.equals("IN") || operator.equals("NOT IN")) {
                    if (null == clause.subQuery) {
                        String filter_str = "";
                        String spr_t = "";
                        Iterator iterator = clause.filterSet.iterator();
                        for (int ii = 0; ii < clause.filterSet.size(); ii++) {
                            filter_str += spr_t + "'" + iterator.next() + "'";
                            spr_t = ",";
                        }
                        filter = "(" + filter_str + ")";
                        operator = " " + operator + " ";
                    } else {
                        operator = " " + operator + " ";
                        filter = "(" + clause.subQuery + ")";
                    }
                } else if (operator.equals("LIKE")) {
                    filter = "'" + filter + "'";
                    operator = " " + operator + " ";
                } else if (operator.equals("=") || operator.equals("<>") || operator.equals("!=")) {
                    if (null == clause.subQuery) {
                        if (!StringUtils.isBlank(clause.exp_field)) {
                            filter = clause.exp_field;
                        } else if (!this.isRawField(filter)) {
                            filter = "'" + filter + "'";
                        }
                    } else {
                        filter = "(" + clause.subQuery + ")";
                    }
                } else if (operator.equals(">") || operator.equals("<")) {
                    if (null == clause.subQuery) {
                        if (!StringUtils.isBlank(clause.exp_field)) {
                            filter = clause.exp_field;
                        } else if (-1 != filter.indexOf(" ")) {
                            filter = "'" + filter + "'";
                        }
                    } else {
                        filter = "(" + clause.subQuery + ")";
                    }
                } else {
                    if (null != clause.subQuery) {
                        sql += spr + "(" + clause.subQuery + ")";
                    }
                    continue;
                }

                //sql+=spr+"("+field+""+operator+filter+")";
                sql += spr + field + operator + filter;
            }

            return sql;
        } catch (Exception e) {
            e.printStackTrace();
            //抛出异常的情况，返回一个逻辑矛盾的语句
            sql = "1<>1";
            return sql;
        }
    }

    public boolean isRawField(String field) {
        return null != field && "`" == field.substring(0, 0);
    }
}