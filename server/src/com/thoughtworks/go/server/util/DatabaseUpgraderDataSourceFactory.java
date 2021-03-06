/*************************GO-LICENSE-START*********************************
 * Copyright 2014 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *************************GO-LICENSE-END***********************************/

package com.thoughtworks.go.server.util;

import java.sql.SQLException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import com.thoughtworks.go.database.Database;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @understands data source creation
 */
public class DatabaseUpgraderDataSourceFactory {

    private DataSource dataSource;
    private Database database;

    @Autowired
    public DatabaseUpgraderDataSourceFactory(Database database) {
        this.database = database;
        this.database.startDatabase();
    }

    public DataSource dataSource() {
        if (dataSource == null) {
            this.dataSource = database.createDataSource();
        }
        return dataSource;
    }

    @PostConstruct
    public void upgradeDb() throws SQLException {
        database.upgrade();
    }

    @PreDestroy
    public void shutdownDatabase() throws SQLException {
        //if (Environment.getProperty("DB_NO_UPGRADE") != null) { return; }
        database.shutdown();
    }
}
