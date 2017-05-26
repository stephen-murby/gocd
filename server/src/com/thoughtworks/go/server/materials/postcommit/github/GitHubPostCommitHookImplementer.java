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
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GitHubPostCommitHookImplementer implements PostCommitHookImplementer {

    private final GitHubWebHookMessageParser gitHubWebHookMessageParser;
    private static final Logger LOGGER = Logger.getLogger(GitHubPostCommitHookImplementer.class);

    public GitHubPostCommitHookImplementer(GitHubWebHookMessageParser gitHubWebHookMessageParser) {
        this.gitHubWebHookMessageParser = gitHubWebHookMessageParser;
    }

    @Override
    public Set<Material> prune(Set<Material> materials, Map params) {
        GitHubRepository gitHubRepository;
        try {
            GitHubPushEvent githubPushEvent = gitHubWebHookMessageParser.parse(params);
            if (hasRepositoryFields(githubPushEvent)) {
                gitHubRepository = githubPushEvent.getRepository();
                return materials.stream()
                        .filter(new Predicate<Material>() {
                            @Override
                            public boolean test(Material material) {
                                return gitHubRepository.matchesMaterial(material);
                            }
                        })
                        .collect(Collectors.toSet());
            }
            return Sets.newHashSet();
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Unable to parse the GitHub webhook payload.");
            return Sets.newHashSet();
        }
    }

    private boolean hasRepositoryFields(GitHubPushEvent event) {
        return event != null
                && event.getRepository() != null
                && event.getRepository().getUrl() != null
                && event.getRepository().getFullName() != null;
    }
}
