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
package org.seasar.extension.jdbc.gen.internal.model;

import java.sql.Types;

import org.seasar.extension.jdbc.gen.desc.DatabaseDesc;
import org.seasar.extension.jdbc.gen.desc.SequenceDesc;
import org.seasar.extension.jdbc.gen.desc.TableDesc;
import org.seasar.extension.jdbc.gen.dialect.GenDialect;
import org.seasar.extension.jdbc.gen.model.DdlModel;
import org.seasar.extension.jdbc.gen.model.DdlModelFactory;

/**
 * {@link DdlModelFactory}の実装クラスです。
 * 
 * @author taedium
 */
public class DdlModelFactoryImpl implements DdlModelFactory {

    /** 方言 */
    protected GenDialect dialect;

    /** SQLステートメントの区切り文字 */
    protected char statementDelimiter;

    /** スキーマ情報を格納するテーブル名 */
    protected String schemaInfoFullTableName;

    /** スキーマのバージョン番号を格納するカラム名 */
    protected String schemaInfoColumnName;

    /** テーブルオプション */
    protected String tableOption;

    /**
     * インスタンスを構築します。
     * 
     * @param dialect
     *            方言
     * @param statementDelimiter
     *            SQLステートメントの区切り文字
     * @param schemaInfoFullTableName
     *            スキーマ情報を格納するテーブル名
     * @param schemaInfoColumnName
     *            スキーマのバージョン番号を格納するカラム名
     * @param tableOption
     *            テーブルオプション、存在しない場合は{@code null}
     * 
     */
    public DdlModelFactoryImpl(GenDialect dialect, char statementDelimiter,
            String schemaInfoFullTableName, String schemaInfoColumnName,
            String tableOption) {
        if (dialect == null) {
            throw new NullPointerException("dialect");
        }
        if (schemaInfoFullTableName == null) {
            throw new NullPointerException("schemaInfoFullTableName");
        }
        if (schemaInfoColumnName == null) {
            throw new NullPointerException("schemaInfoColumnName");
        }
        this.dialect = dialect;
        this.schemaInfoFullTableName = schemaInfoFullTableName;
        this.schemaInfoColumnName = schemaInfoColumnName;
        this.statementDelimiter = statementDelimiter;
        this.tableOption = tableOption;
    }

    public DdlModel getDdlModel(DatabaseDesc databaseDesc, int versionNo) {
        DdlModel model = new DdlModel();
        model.setDialect(dialect);
        model.setDelimiter(statementDelimiter);
        model.setTableOption(tableOption);
        for (TableDesc tableDesc : databaseDesc.getTableDescList()) {
            model.addTableDesc(tableDesc);
            for (SequenceDesc sequenceDesc : tableDesc.getSequenceDescList()) {
                model.addSequenceDesc(sequenceDesc);
            }
        }
        model.setSchemaInfoFullTableName(schemaInfoFullTableName);
        model.setSchemaInfoColumnName(schemaInfoColumnName);
        model.setVersionNo(versionNo);
        String columnDefinition = dialect.getSqlType(Types.INTEGER)
                .getColumnDefinition(0, 10, 0, false);
        model.setSchemaInfoColumnDefinition(columnDefinition);
        return model;
    }
}
