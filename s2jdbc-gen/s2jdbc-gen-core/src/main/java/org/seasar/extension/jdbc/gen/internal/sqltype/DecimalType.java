/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.extension.jdbc.gen.internal.sqltype;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.seasar.extension.jdbc.gen.sqltype.SqlType;
import org.seasar.framework.util.BigDecimalConversionUtil;
import org.seasar.framework.util.StringConversionUtil;

/**
 * {@link Types#DECIMAL}に対応する{@link SqlType}です。
 * 
 * @author taedium
 */
public class DecimalType extends AbstractSqlType {

    /**
     * インスタンスを構築します。
     */
    public DecimalType() {
        this("decimal");
    }

    /**
     * インスタンスを構築します。
     * 
     * @param columnDefinition
     *            カラム定義
     */
    public DecimalType(String columnDefinition) {
        super(columnDefinition);
    }

    public void bindValue(PreparedStatement ps, int index, String value)
            throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.DECIMAL);
        }
        ps.setBigDecimal(index, BigDecimalConversionUtil.toBigDecimal(value));
    }

    public String getValue(ResultSet resultSet, int index) throws SQLException {
        BigDecimal value = resultSet.getBigDecimal(index);
        return value != null ? StringConversionUtil.toString(value) : null;
    }

}
