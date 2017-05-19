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

import com.google.common.collect.Sets;
import com.thoughtworks.go.config.materials.git.GitMaterial;
import com.thoughtworks.go.domain.materials.Material;
import com.thoughtworks.go.server.materials.postcommit.PostCommitHookImplementer;
import com.thoughtworks.go.server.materials.postcommit.UrlMatchers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GitHubPostCommitHookImplementer implements PostCommitHookImplementer {

    private final GitHubWebHookMessageParser gitHubWebHookMessageParser;

    public GitHubPostCommitHookImplementer(GitHubWebHookMessageParser gitHubWebHookMessageParser) {
        this.gitHubWebHookMessageParser = gitHubWebHookMessageParser;
    }

    private final UrlMatchers validators = new UrlMatchers();

    @Override
    public Set<Material> prune(Set<Material> materials, Map params) {
        GitHubRepository gitHubRepository;
        try {
            gitHubRepository = gitHubWebHookMessageParser.parse(params).getRepository();
            if (hasRepositoryFields(gitHubRepository)) {
                return materials.stream()
                        .filter(material -> this.matchesGitHubRepository(material, gitHubRepository.getUrl(), gitHubRepository.getFullName()))
                        .collect(Collectors.toSet());
            }
            return Sets.newHashSet();
        } catch (UnsupportedEncodingException e) {
            return Sets.newHashSet();
        }
    }

    private boolean matchesGitHubRepository(Material material, String repoUrl, String repoFullName) {
        return material instanceof GitMaterial
                && isUrlEqual(repoUrl, (GitMaterial) material, repoFullName);
    }

    private boolean hasRepositoryFields(GitHubRepository repository) {
        return repository != null
                && repository.getUrl() != null
                && repository.getFullName() != null;
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
