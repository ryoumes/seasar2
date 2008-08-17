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
package org.seasar.extension.jdbc.gen.task;

import java.io.File;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.gen.command.Command;
import org.seasar.extension.jdbc.gen.command.MigrateCommand;

/**
 * データベースのスキーマとデータを移行する{@link Task}です。
 * 
 * @author taedium
 * @see MigrateCommand
 */
public class MigrateTask extends AbstractTask {

    /** コマンド */
    protected MigrateCommand command = new MigrateCommand();

    @Override
    protected Command getCommand() {
        return command;
    }

    /**
     * SQLブロックの区切り文字を設定します。
     * 
     * @param blockDelimiter
     *            SQLブロックの区切り文字
     */
    public void setBlockDelimiter(String blockDelimiter) {
        command.setBlockDelimiter(blockDelimiter);
    }

    /**
     * 設定ファイルのパスを設定します。
     * 
     * @param configPath
     *            設定ファイルのパス
     */
    public void setConfigPath(String configPath) {
        command.setConfigPath(configPath);
    }

    /**
     * DDLファイルのエンコーディングを設定します。
     * 
     * @param ddlFileEncoding
     *            DDLファイルのエンコーディング
     */
    public void setDdlFileEncoding(String ddlFileEncoding) {
        command.setDdlFileEncoding(ddlFileEncoding);
    }

    /**
     * DDL情報ファイルを設定します。
     * 
     * @param ddlInfoFile
     *            DDL情報ファイル
     */
    public void setDdlInfoFile(File ddlInfoFile) {
        command.setDdlInfoFile(ddlInfoFile);
    }

    /**
     * 環境名を設定します。
     * 
     * @param env
     *            環境名
     */
    public void setEnv(String env) {
        command.setEnv(env);
    }

    /**
     * エラー発生時に処理を中止する場合{@code true}、中止しない場合{@code false}を設定します。
     * 
     * @param haltOnError
     *            エラー発生時に処理を中止する場合{@code true}、中止しない場合{@code false}
     */
    public void setHaltOnError(boolean haltOnError) {
        command.setHaltOnError(haltOnError);
    }

    /**
     * {@link JdbcManager}のコンポーネント名を設定します。
     * 
     * @param jdbcManagerName
     *            {@link JdbcManager}のコンポーネント名
     */
    public void setJdbcManagerName(String jdbcManagerName) {
        command.setJdbcManagerName(jdbcManagerName);
    }

    /**
     * マイグレーションのディレクトリを設定します。
     * 
     * @param migrateDir
     *            マイグレーションのディレクトリ
     */
    public void setMigrateDir(File migrateDir) {
        command.setMigrateDir(migrateDir);
    }

    /**
     * スキーマのバージョン番号を格納するカラム名を設定します。
     * 
     * @param schemaInfoColumnName
     *            スキーマのバージョン番号を格納するカラム名
     */
    public void setSchemaInfoColumnName(String schemaInfoColumnName) {
        command.setSchemaInfoColumnName(schemaInfoColumnName);
    }

    /**
     * スキーマ情報を格納するテーブル名を設定します。
     * 
     * @param schemaInfoFullTableName
     *            スキーマ情報を格納するテーブル名
     */
    public void setSchemaInfoFullTableName(String schemaInfoFullTableName) {
        command.setSchemaInfoFullTableName(schemaInfoFullTableName);
    }

    /**
     * SQLステートメントの区切り文字を設定します。
     * 
     * @param statementDelimiter
     *            SQLステートメントの区切り文字
     */
    public void setStatementDelimiter(char statementDelimiter) {
        command.setStatementDelimiter(statementDelimiter);
    }

    /**
     * マイグレーション先のバージョンを設定します。
     * 
     * @param version
     *            マイグレーション先のバージョン
     */
    public void setVersion(String version) {
        command.setVersion(version);
    }

    /**
     * バージョン番号のパターンを設定します。
     * 
     * @param versionNoPattern
     *            バージョン番号のパターン
     */
    public void setVersionNoPattern(String versionNoPattern) {
        command.setVersionNoPattern(versionNoPattern);
    }

    /**
     * クラスパスのディレクトリを設定します。
     * 
     * @param classpathDir
     *            クラスパスのディレクトリ
     */
    public void setClasspathDir(File classpathDir) {
        command.setClasspathDir(classpathDir);
    }

    /**
     * ダンプファイルのエンコーディングを設定します。
     * 
     * @param dumpFileEncoding
     *            ダンプファイルのエンコーディング
     */
    public void setDumpFileEncoding(String dumpFileEncoding) {
        command.setDumpFileEncoding(dumpFileEncoding);
    }

    /**
     * 対象とするエンティティ名の正規表現を設定します。
     * 
     * @param entityNamePattern
     *            対象とするエンティティ名の正規表現
     */
    public void setEntityNamePattern(String entityNamePattern) {
        command.setEntityNamePattern(entityNamePattern);
    }

    /**
     * エンティティクラスのパッケージ名を設定します。
     * 
     * @param entityPackageName
     *            エンティティクラスのパッケージ名
     */
    public void setEntityPackageName(String entityPackageName) {
        command.setEntityPackageName(entityPackageName);
    }

    /**
     * 対象としないエンティティ名の正規表現を設定します。
     * 
     * @param ignoreEntityNamePattern
     *            対象としないエンティティ名の正規表現
     */
    public void setIgnoreEntityNamePattern(String ignoreEntityNamePattern) {
        command.setIgnoreEntityNamePattern(ignoreEntityNamePattern);
    }

    /**
     * ルートパッケージ名を設定します。
     * 
     * @param rootPackageName
     *            ルートパッケージ名
     */
    public void setRootPackageName(String rootPackageName) {
        command.setRootPackageName(rootPackageName);
    }

    /**
     * データをロードする際のバッチサイズを設定します。
     * 
     * @param loadBatchSize
     *            データをロードする際のバッチサイズ
     */
    public void setLoadBatchSize(int loadBatchSize) {
        command.setLoadBatchSize(loadBatchSize);
    }
}
