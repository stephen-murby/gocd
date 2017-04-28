/*
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thoughtworks.go.config.materials.github;

import com.thoughtworks.go.config.materials.ScmMaterial;
import com.thoughtworks.go.config.materials.SubprocessExecutionContext;
import com.thoughtworks.go.domain.MaterialInstance;
import com.thoughtworks.go.domain.materials.RevisionContext;
import com.thoughtworks.go.util.command.ConsoleOutputStreamConsumer;
import com.thoughtworks.go.util.command.UrlArgument;

import java.io.File;
import java.util.Map;

public class GithubMaterial extends ScmMaterial {


    public static final String TYPE = "GithubMaterial";
    private final UrlArgument url;

    public GithubMaterial(String url) {
        super(TYPE);
        this.url = new UrlArgument(url);
    }

    @Override
    public void updateTo(ConsoleOutputStreamConsumer outputStreamConsumer, File baseDir, RevisionContext revisionContext, SubprocessExecutionContext execCtx) {

    }

    @Override
    public MaterialInstance createMaterialInstance() {
        return null;
    }

    @Override
    public String getTypeForDisplay() {
        return null;
    }

    @Override
    public Class getInstanceType() {
        return null;
    }

    @Override
    public String getLongDescription() {
        return null;
    }

    @Override
    public String getUserName() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getEncryptedPassword() {
        return null;
    }

    @Override
    public boolean isCheckExternals() {
        return false;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public UrlArgument getUrlArgument() {
        return null;
    }

    @Override
    protected String getLocation() {
        return null;
    }

    @Override
    protected void appendCriteria(Map<String, Object> parameters) {

    }

    @Override
    protected void appendAttributes(Map<String, Object> parameters) {

    }
}
