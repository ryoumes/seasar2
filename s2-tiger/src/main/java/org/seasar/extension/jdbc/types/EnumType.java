/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package org.seasar.extension.jdbc.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.seasar.extension.jdbc.ValueType;

/**
 * EnumをJDBCで扱うためのクラスです。
 * 
 * @author higa
 * 
 */
public class EnumType implements ValueType {

    @SuppressWarnings("unchecked")
    private final Class<? extends Enum> enumClass;

    /**
     * <code>EnumType</code>を返します。
     * 
     * @param enumClass
     */
    @SuppressWarnings("unchecked")
    public EnumType(Class<? extends Enum> enumClass) {
        this.enumClass = enumClass;
    }

    public Object getValue(ResultSet resultSet, int index) throws SQLException {
        return toEnum(resultSet.getString(index));
    }

    /**
     * {@link Enum}に変換します。
     * 
     * @param name
     * @return {@link Enum}
     */
    @SuppressWarnings("unchecked")
    protected Enum toEnum(String name) {
        if (name == null) {
            return null;
        }
        return Enum.valueOf(enumClass, name);
    }

    public Object getValue(ResultSet resultSet, String columnName)
            throws SQLException {

        return toEnum(resultSet.getString(columnName));
    }

    @SuppressWarnings("unchecked")
    public void bindValue(PreparedStatement ps, int index, Object value)
            throws SQLException {

        if (value == null) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, (Enum.class.cast(value)).name());
        }
    }

}