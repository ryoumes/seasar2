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
package org.seasar.extension.jdbc.gen.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.sql.DataSource;

import org.seasar.extension.jdbc.EntityMeta;
import org.seasar.extension.jdbc.EntityMetaFactory;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.gen.ColumnDescFactory;
import org.seasar.extension.jdbc.gen.DdlVersion;
import org.seasar.extension.jdbc.gen.EntityMetaReader;
import org.seasar.extension.jdbc.gen.FileHandler;
import org.seasar.extension.jdbc.gen.ForeignKeyDescFactory;
import org.seasar.extension.jdbc.gen.GenDialect;
import org.seasar.extension.jdbc.gen.Generator;
import org.seasar.extension.jdbc.gen.IdTableDescFactory;
import org.seasar.extension.jdbc.gen.Loader;
import org.seasar.extension.jdbc.gen.PrimaryKeyDescFactory;
import org.seasar.extension.jdbc.gen.SchemaVersion;
import org.seasar.extension.jdbc.gen.SequenceDescFactory;
import org.seasar.extension.jdbc.gen.SqlExecutionContext;
import org.seasar.extension.jdbc.gen.SqlFileExecutor;
import org.seasar.extension.jdbc.gen.TableDesc;
import org.seasar.extension.jdbc.gen.TableDescFactory;
import org.seasar.extension.jdbc.gen.UniqueKeyDescFactory;
import org.seasar.extension.jdbc.gen.desc.ColumnDescFactoryImpl;
import org.seasar.extension.jdbc.gen.desc.ForeignKeyDescFactoryImpl;
import org.seasar.extension.jdbc.gen.desc.IdTableDescFactoryImpl;
import org.seasar.extension.jdbc.gen.desc.PrimaryKeyDescFactoryImpl;
import org.seasar.extension.jdbc.gen.desc.SequenceDescFactoryImpl;
import org.seasar.extension.jdbc.gen.desc.TableDescFactoryImpl;
import org.seasar.extension.jdbc.gen.desc.UniqueKeyDescFactoryImpl;
import org.seasar.extension.jdbc.gen.dialect.GenDialectManager;
import org.seasar.extension.jdbc.gen.exception.RequiredPropertyNullRuntimeException;
import org.seasar.extension.jdbc.gen.meta.EntityMetaReaderImpl;
import org.seasar.extension.jdbc.gen.migration.DumpFileHandler;
import org.seasar.extension.jdbc.gen.migration.SqlFileHandler;
import org.seasar.extension.jdbc.gen.sql.LoaderImpl;
import org.seasar.extension.jdbc.gen.sql.SqlExecutionContextImpl;
import org.seasar.extension.jdbc.gen.sql.SqlFileExecutorImpl;
import org.seasar.extension.jdbc.gen.util.ExclusionFilenameFilter;
import org.seasar.extension.jdbc.gen.util.SingletonS2ContainerFactorySupport;
import org.seasar.extension.jdbc.gen.util.VersionUtil;
import org.seasar.extension.jdbc.gen.version.DdlVersionImpl;
import org.seasar.extension.jdbc.gen.version.SchemaVersionImpl;
import org.seasar.extension.jdbc.manager.JdbcManagerImplementor;
import org.seasar.framework.container.SingletonS2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;
import org.seasar.framework.log.Logger;
import org.seasar.framework.util.ClassUtil;

/**
 * @author taedium
 * 
 */
public class MigrateCommand extends AbstractCommand {

    /** ロガー */
    protected Logger logger = Logger.getLogger(MigrateCommand.class);

    /** クラスパスのルートとなるディレクトリ */
    protected File classpathDir;

    /** ルートパッケージ名 */
    protected String rootPackageName = "";

    /** エンティティクラスのパッケージ名 */
    protected String entityPackageName = "entity";

    /** 対象とするエンティティ名の正規表現 */
    protected String entityNamePattern = ".*";

    /** 対象としないエンティティ名の正規表現 */
    protected String ignoreEntityNamePattern = "";

    /** SQLステートメントの区切り文字 */
    protected char statementDelimiter = ';';

    /** SQLブロックの区切り文字 */
    protected String blockDelimiter = null;

    /** 設定ファイルのパス */
    protected String configPath = "s2jdbc.dicon";

    /** {@link JdbcManager}のコンポーネント名 */
    protected String jdbcManagerName = "jdbcManager";

    /** 環境名 */
    protected String env = "ut";

    /** エラー発生時に処理を中止する場合{@code true} */
    protected boolean haltOnError = false;

    /** スキーマ情報を格納するテーブル名 */
    protected String schemaInfoFullTableName = "SCHEMA_INFO";

    /** スキーマのバージョン番号を格納するカラム名 */
    protected String schemaInfoColumnName = "VERSION";

    /** テーブルを作成するDDLファイル名 */
    protected String createTableDdlFileName = "010-create-table.sql";

    /** DDLファイルのエンコーディング */
    protected String ddlFileEncoding = "UTF-8";

    /** マイグレーションのディレクトリ */
    protected File migrateDir = new File("db", "migrate");

    /** バージョン番号のパターン */
    protected String versionNoPattern = "0000";

    /** DDLのバージョンファイル */
    protected File ddlVersionFile = new File("db", "ddl-version.txt");

    /** マイグレーション先のバージョン */
    protected String version = "latest";

    /** スキーマ作成用のSQLファイルを格納するディレクトリ名 */
    protected String createDirName = "create";

    /** スキーマ削除用のSQLファイルを格納するディレクトリ名 */
    protected String dropDirName = "drop";

    /** ダンプファイルのエンコーディング */
    protected String dumpFileEncoding = "UTF-8";

    /** {@link SingletonS2ContainerFactory}のサポート */
    protected SingletonS2ContainerFactorySupport containerFactorySupport;

    /** データソース */
    protected DataSource dataSource;

    /** 方言 */
    protected GenDialect dialect;

    /** SQLファイルの実行者 */
    protected SqlFileExecutor sqlFileExecutor;

    /** スキーマのバージョン */
    protected SchemaVersion schemaVersion;

    /** DDLのバージョン */
    protected DdlVersion ddlVersion;

    protected Generator generator;

    /** エンティティメタデータのファクトリ */
    protected EntityMetaFactory entityMetaFactory;

    /** エンティティメタデータのリーダ */
    protected EntityMetaReader entityMetaReader;

    /** テーブル記述のファクトリ */
    protected TableDescFactory tableDescFactory;

    protected List<TableDesc> tableDescList;

    /**
     * @return Returns the statementDelimiter.
     */
    public char getStatementDelimiter() {
        return statementDelimiter;
    }

    /**
     * @param statementDelimiter
     *            The statementDelimiter to set.
     */
    public void setStatementDelimiter(char statementDelimiter) {
        this.statementDelimiter = statementDelimiter;
    }

    /**
     * @return Returns the blockDelimiter.
     */
    public String getBlockDelimiter() {
        return blockDelimiter;
    }

    /**
     * @param blockDelimiter
     *            The blockDelimiter to set.
     */
    public void setBlockDelimiter(String blockDelimiter) {
        this.blockDelimiter = blockDelimiter;
    }

    /**
     * @return Returns the configPath.
     */
    public String getConfigPath() {
        return configPath;
    }

    /**
     * @param configPath
     *            The configPath to set.
     */
    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    /**
     * @return Returns the jdbcManagerName.
     */
    public String getJdbcManagerName() {
        return jdbcManagerName;
    }

    /**
     * @param jdbcManagerName
     *            The jdbcManagerName to set.
     */
    public void setJdbcManagerName(String jdbcManagerName) {
        this.jdbcManagerName = jdbcManagerName;
    }

    /**
     * @return Returns the env.
     */
    public String getEnv() {
        return env;
    }

    /**
     * @param env
     *            The env to set.
     */
    public void setEnv(String env) {
        this.env = env;
    }

    /**
     * @return Returns the haltOnError.
     */
    public boolean isHaltOnError() {
        return haltOnError;
    }

    /**
     * @param haltOnError
     *            The haltOnError to set.
     */
    public void setHaltOnError(boolean haltOnError) {
        this.haltOnError = haltOnError;
    }

    /**
     * @return Returns the schemaInfoFullTableName.
     */
    public String getSchemaInfoFullTableName() {
        return schemaInfoFullTableName;
    }

    /**
     * @param schemaInfoFullTableName
     *            The schemaInfoFullTableName to set.
     */
    public void setSchemaInfoFullTableName(String schemaInfoFullTableName) {
        this.schemaInfoFullTableName = schemaInfoFullTableName;
    }

    /**
     * @return Returns the schemaInfoColumnName.
     */
    public String getSchemaInfoColumnName() {
        return schemaInfoColumnName;
    }

    /**
     * @param schemaInfoColumnName
     *            The schemaInfoColumnName to set.
     */
    public void setSchemaInfoColumnName(String schemaInfoColumnName) {
        this.schemaInfoColumnName = schemaInfoColumnName;
    }

    /**
     * @return Returns the createTableDdlFileName.
     */
    public String getCreateTableDdlFileName() {
        return createTableDdlFileName;
    }

    /**
     * @param createTableDdlFileName
     *            The createTableDdlFileName to set.
     */
    public void setCreateTableDdlFileName(String createTableDdlFileName) {
        this.createTableDdlFileName = createTableDdlFileName;
    }

    /**
     * @return Returns the ddlFileEncoding.
     */
    public String getDdlFileEncoding() {
        return ddlFileEncoding;
    }

    /**
     * @param ddlFileEncoding
     *            The ddlFileEncoding to set.
     */
    public void setDdlFileEncoding(String ddlFileEncoding) {
        this.ddlFileEncoding = ddlFileEncoding;
    }

    /**
     * @return Returns the migrateDir.
     */
    public File getMigrateDir() {
        return migrateDir;
    }

    /**
     * @param migrateDir
     *            The migrateDir to set.
     */
    public void setMigrateDir(File migrateDir) {
        this.migrateDir = migrateDir;
    }

    /**
     * @return Returns the versionNoPattern.
     */
    public String getVersionNoPattern() {
        return versionNoPattern;
    }

    /**
     * @param versionNoPattern
     *            The versionNoPattern to set.
     */
    public void setVersionNoPattern(String versionNoPattern) {
        this.versionNoPattern = versionNoPattern;
    }

    /**
     * @return Returns the ddlVersionFile.
     */
    public File getDdlVersionFile() {
        return ddlVersionFile;
    }

    /**
     * @param ddlVersionFile
     *            The ddlVersionFileName to set.
     */
    public void setDdlVersionFile(File ddlVersionFile) {
        this.ddlVersionFile = ddlVersionFile;
    }

    /**
     * マイグレーション先のバージョンを返します。
     * 
     * @return マイグレーション先のバージョン
     */
    public String getVersion() {
        return version;
    }

    /**
     * マイグレーション先のバージョンを設定します。
     * 
     * @param version
     *            マイグレーション先のバージョン
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return Returns the classpathDir.
     */
    public File getClasspathDir() {
        return classpathDir;
    }

    /**
     * @param classpathDir
     *            The classpathDir to set.
     */
    public void setClasspathDir(File classpathDir) {
        this.classpathDir = classpathDir;
    }

    /**
     * @return Returns the rootPackageName.
     */
    public String getRootPackageName() {
        return rootPackageName;
    }

    /**
     * @param rootPackageName
     *            The rootPackageName to set.
     */
    public void setRootPackageName(String rootPackageName) {
        this.rootPackageName = rootPackageName;
    }

    /**
     * @return Returns the entityPackageName.
     */
    public String getEntityPackageName() {
        return entityPackageName;
    }

    /**
     * @param entityPackageName
     *            The entityPackageName to set.
     */
    public void setEntityPackageName(String entityPackageName) {
        this.entityPackageName = entityPackageName;
    }

    /**
     * @return Returns the entityNamePattern.
     */
    public String getEntityNamePattern() {
        return entityNamePattern;
    }

    /**
     * @param entityNamePattern
     *            The entityNamePattern to set.
     */
    public void setEntityNamePattern(String entityNamePattern) {
        this.entityNamePattern = entityNamePattern;
    }

    /**
     * @return Returns the ignoreEntityNamePattern.
     */
    public String getIgnoreEntityNamePattern() {
        return ignoreEntityNamePattern;
    }

    /**
     * @param ignoreEntityNamePattern
     *            The ignoreEntityNamePattern to set.
     */
    public void setIgnoreEntityNamePattern(String ignoreEntityNamePattern) {
        this.ignoreEntityNamePattern = ignoreEntityNamePattern;
    }

    /**
     * @return Returns the dumpFileEncoding.
     */
    public String getDumpFileEncoding() {
        return dumpFileEncoding;
    }

    /**
     * @param dumpFileEncoding
     *            The dumpFileEncoding to set.
     */
    public void setDumpFileEncoding(String dumpFileEncoding) {
        this.dumpFileEncoding = dumpFileEncoding;
    }

    @Override
    protected void doValidate() {
        if (classpathDir == null) {
            throw new RequiredPropertyNullRuntimeException("classpathDir");
        }
    }

    @Override
    protected void doInit() {
        containerFactorySupport = new SingletonS2ContainerFactorySupport(
                configPath, env);
        containerFactorySupport.init();

        JdbcManagerImplementor jdbcManager = SingletonS2Container
                .getComponent(jdbcManagerName);
        entityMetaFactory = jdbcManager.getEntityMetaFactory();
        dataSource = jdbcManager.getDataSource();
        dialect = GenDialectManager.getGenDialect(jdbcManager.getDialect());
        sqlFileExecutor = createSqlFileExecutor();
        schemaVersion = createSchemaVersion();
        ddlVersion = createDdlVersion();
        entityMetaReader = createEntityMetaReader();
        tableDescFactory = createTableDescFactory();

        logger.log("DS2JDBCGen0005", new Object[] { dialect.getClass()
                .getName() });
    }

    @Override
    protected void doExecute() {
        int from = schemaVersion.getVersionNo();
        int to = "latest".equalsIgnoreCase(version) ? ddlVersion.getVersionNo()
                : VersionUtil.toInt(version);
        dropSchema(from);
        createSchema(to);
        logger.log("IS2JDBCGen0005", new Object[] { from, to });
    }

    @Override
    protected void doDestroy() {
        if (containerFactorySupport != null) {
            containerFactorySupport.destory();
        }
    }

    /**
     * スキーマからオブジェクトを削除します。
     * 
     * @param versionNo
     *            バージョン番号
     */
    protected void dropSchema(int versionNo) {
        String versionName = VersionUtil.toString(versionNo, versionNoPattern);
        File versionDir = new File(migrateDir, versionName);
        File dropDir = new File(versionDir, dropDirName);
        handleDir(dropDir);
    }

    /**
     * スキーマにオブジェクトを作成します。
     * 
     * @param versionNo
     *            バージョン番号
     */
    protected void createSchema(int versionNo) {
        String versionName = VersionUtil.toString(versionNo, versionNoPattern);
        File versionDir = new File(migrateDir, versionName);
        File createDir = new File(versionDir, createDirName);
        handleDir(createDir);
    }

    protected void handleDir(File dir) {
        SqlExecutionContext context = createSqlExecutionContext();
        try {
            try {
                for (FileHandler handler : createFileHandlerList(dir)) {
                    handler.handle(context);
                }
            } finally {
                if (!context.getExceptionList().isEmpty()) {
                    for (Exception e : context.getExceptionList()) {
                        logger.error(e.getMessage());
                    }
                    throw context.getExceptionList().get(0);
                }
            }
        } finally {
            context.destroy();
        }
    }

    protected List<FileHandler> createFileHandlerList(File dir) {
        if (!dir.exists()) {
            logger.log("DS2JDBCGen0010", new Object[] { dir.getPath() });
            return Collections.emptyList();
        }

        File[] files = dir.listFiles(new ExclusionFilenameFilter());
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {

            public int compare(File file1, File file2) {
                return file1.getName().compareTo(file2.getName());
            }
        });

        List<FileHandler> handlerList = new ArrayList<FileHandler>();
        for (File file : fileList) {
            if (file.isDirectory()) {
                List<FileHandler> list = createFileHandlerList(file);
                handlerList.addAll(list);
            }
            String name = file.getName();
            if (name.endsWith(".sql") || name.endsWith(".ddl")) {
                handlerList.add(new SqlFileHandler(file, sqlFileExecutor));
            }
            if (name.endsWith(".csv")) {
                handlerList.add(new DumpFileHandler(file,
                        createLoader(getTableDescList())));
            }
        }
        return handlerList;
    }

    protected List<TableDesc> getTableDescList() {
        if (tableDescList != null) {
            return tableDescList;
        }
        List<EntityMeta> entityMetaList = entityMetaReader.read();
        tableDescList = new ArrayList<TableDesc>();
        for (EntityMeta entityMeta : entityMetaList) {
            TableDesc tableDesc = tableDescFactory.getTableDesc(entityMeta);
            if (!tableDescList.contains(tableDesc)) {
                tableDescList.add(tableDesc);
            }
            for (TableDesc idTableDesc : tableDesc.getIdTableDescList()) {
                if (!tableDescList.contains(idTableDesc)) {
                    tableDescList.add(idTableDesc);
                }
            }
        }
        return tableDescList;
    }

    /**
     * {@link EntityMetaReader}の実装を作成します。
     * 
     * @return {@link EntityMetaReader}の実装
     */
    protected EntityMetaReader createEntityMetaReader() {
        return new EntityMetaReaderImpl(classpathDir, ClassUtil.concatName(
                rootPackageName, entityPackageName), entityMetaFactory,
                entityNamePattern, ignoreEntityNamePattern);
    }

    /**
     * {@link TableDescFactory}の実装を作成します。
     * 
     * @return {@link TableDescFactory}の実装
     */
    protected TableDescFactory createTableDescFactory() {
        ColumnDescFactory colFactory = new ColumnDescFactoryImpl(dialect);
        PrimaryKeyDescFactory pkFactory = new PrimaryKeyDescFactoryImpl(dialect);
        UniqueKeyDescFactory ukFactory = new UniqueKeyDescFactoryImpl();
        ForeignKeyDescFactory fkFactory = new ForeignKeyDescFactoryImpl(
                entityMetaFactory);
        SequenceDescFactory seqFactory = new SequenceDescFactoryImpl(dialect);
        IdTableDescFactory idTabFactory = new IdTableDescFactoryImpl(dialect,
                colFactory, pkFactory, ukFactory);
        return new TableDescFactoryImpl(colFactory, pkFactory, ukFactory,
                fkFactory, seqFactory, idTabFactory);
    }

    /**
     * {@link SchemaVersion}の実装を作成します。
     * 
     * @return {@link SchemaVersion}の実装
     */
    protected SchemaVersion createSchemaVersion() {
        return new SchemaVersionImpl(dataSource, dialect,
                schemaInfoFullTableName, schemaInfoColumnName);
    }

    /**
     * {@link DdlVersion}の実装を作成します。
     * 
     * @return {@link DdlVersion}の実装
     */
    protected DdlVersion createDdlVersion() {
        return new DdlVersionImpl(ddlVersionFile);
    }

    /**
     * {@link SqlFileExecutor}の実装を作成します。
     * 
     * @return {@link SqlFileExecutor}の実装
     */
    protected SqlFileExecutor createSqlFileExecutor() {
        return new SqlFileExecutorImpl(dialect, ddlFileEncoding,
                statementDelimiter, blockDelimiter);
    }

    /**
     * {@link SqlExecutionContext}の実装を作成します。
     * 
     * @return {@link SqlExecutionContext}の実装
     */
    protected SqlExecutionContext createSqlExecutionContext() {
        return new SqlExecutionContextImpl(dataSource, haltOnError);
    }

    protected Loader createLoader(List<TableDesc> tableDescList) {
        return new LoaderImpl(dialect, dumpFileEncoding, tableDescList);
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

}
