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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thoughtworks.go.config.materials.git.GitMaterial;
import com.thoughtworks.go.domain.materials.Material;
import com.thoughtworks.go.server.materials.postcommit.PostCommitHookImplementer;
import com.thoughtworks.go.server.materials.postcommit.UrlMatchers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class GitHubPostCommitHookImplementer implements PostCommitHookImplementer {

    private final String REPO_GIT_URL_PARAM_KEY = "url";
    private final String GITHUB_REPOSITORY = "repository";
    private final String GITHUB_PUSH_EVENT_IDENTIFIER = "commits";

    private final UrlMatchers validators = new UrlMatchers();

    @Override
    public Set<Material> prune(Set<Material> materials, Map params) {

        HashSet<Material> prunedCollection = new HashSet<>();
        JsonObject webhookPayload;
        try {
            webhookPayload = parsePayload(decodePayload(params));
        } catch (UnsupportedEncodingException e) {
            return prunedCollection;
        }

        if (isGitHubPushEvent(webhookPayload)) {
            JsonObject repository = webhookPayload.getAsJsonObject(GITHUB_REPOSITORY);
            String repositoryUrl = repository.get("url").getAsString();
            String repositoryFullName = repository.get("full_name").getAsString();

            return materials.stream()
                    .filter(material -> this.matchesGithubRepository(material, repositoryUrl, repositoryFullName))
                    .collect(Collectors.toSet());
        }
        return Sets.newHashSet();
    }

    private boolean matchesGithubRepository(Material material, String repoUrl, String repoFullName) {
        if ( material instanceof GitMaterial
                && isUrlEqual(repoUrl, (GitMaterial) material, repoFullName)) {
            return true;
        } else return false;
    }

    private boolean hasRepositoryInPayload(JsonObject decodedParams) {
        return decodedParams.getAsJsonObject("repository") != null
                && decodedParams.getAsJsonObject(GITHUB_REPOSITORY).get(REPO_GIT_URL_PARAM_KEY).getAsString() != null
                && !decodedParams.getAsJsonObject(GITHUB_REPOSITORY).get(REPO_GIT_URL_PARAM_KEY).getAsString().isEmpty();
    }

    private boolean isGitHubPushEvent(JsonObject webhookPayload) {
        return webhookPayload.has(GITHUB_PUSH_EVENT_IDENTIFIER) && hasRepositoryInPayload(webhookPayload);
    }

    private JsonObject parsePayload(String payload) {
        JsonParser parser = new JsonParser();
        return parser.parse(payload).getAsJsonObject();
    }

    private String decodePayload(Map params) throws UnsupportedEncodingException {
        String payload = ((String)params.get("payload"));
        return URLDecoder.decode(payload, "utf-8");
    }

    private boolean isUrlEqual(String paramRepoUrl, GitMaterial material, String repoFullName) {

        URI repoUri = null;
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
