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

package com.thoughtworks.go.server.materials.postcommit.github;

import com.thoughtworks.go.config.materials.git.GitMaterial;
import com.thoughtworks.go.domain.materials.Material;
import com.thoughtworks.go.server.materials.postcommit.UrlMatchers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.regex.Pattern;

public class GitHubRepository {
    private final String url;
    private final String fullName;
    private final UrlMatchers validators = new UrlMatchers();

    public GitHubRepository(String url, String fullName) {
        this.url = url;
        this.fullName = fullName;
    }
    public String getUrl() {
        return url;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean matchesMaterial(Material material) {
        return material instanceof GitMaterial
                && isUrlEqual(this.getUrl(), (GitMaterial) material, this.getFullName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GitHubRepository that = (GitHubRepository) o;
        return Objects.equals(url, that.url) &&
                Objects.equals(fullName, that.fullName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, fullName);
    }

    private boolean isUrlEqual(String paramRepoUrl, GitMaterial material, String repoFullName) {

        URI repoUri;
        String materialUrl = material.getUrlArgument().forCommandline();
        try {
            repoUri = new URI(paramRepoUrl);
            Pattern pattern = Pattern.compile("^(git://|git@|https://|http://)" + repoUri.getHost() + "[/:]" + repoFullName + "(.git)?$");
            if ( pattern.matcher(materialUrl).matches()) {
                return true;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return validators.perform(paramRepoUrl, materialUrl);
    }
}
