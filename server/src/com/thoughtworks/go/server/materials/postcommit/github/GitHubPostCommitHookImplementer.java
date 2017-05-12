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

public class GitHubPostCommitHookImplementer implements PostCommitHookImplementer {

//    final String REPO_URL_PARAM_KEY = "url";
//    final String REPO_SSH_URL_PARAM_KEY = "ssh_url";
//    final String REPO_CLONE_URL_PARAM_KEY = "clone_url";
//    final String REPO_SVN_URL_PARAM_KEY = "svn_url";

    final String REPO_GIT_URL_PARAM_KEY = "url";
    private String REPO_OBJECT_KEY = "repository";

    private final UrlMatchers validators = new UrlMatchers();

    @Override
    public Set<Material> prune(Set<Material> materials, Map params) {

        HashSet<Material> prunedCollection = new HashSet<>();
        JsonObject decodedParams;
        try {
            decodedParams = parsePayload(decodePayload(params));
        } catch (UnsupportedEncodingException e) {
            return prunedCollection;
        }

        if( decodedParams.getAsJsonObject("repository") != null
                && decodedParams.getAsJsonObject(REPO_OBJECT_KEY).get(REPO_GIT_URL_PARAM_KEY).getAsString() != null
                && !decodedParams.getAsJsonObject(REPO_OBJECT_KEY).get(REPO_GIT_URL_PARAM_KEY).getAsString().isEmpty()
                ) {
            String paramRepoUrl = decodedParams.getAsJsonObject(REPO_OBJECT_KEY).get("url").getAsString();
            String repoFullName = decodedParams.getAsJsonObject(REPO_OBJECT_KEY).get("full_name").getAsString();
            for (Material material : materials) {
                if (material instanceof GitMaterial && isUrlEqual(paramRepoUrl, (GitMaterial) material, repoFullName)) {
                    prunedCollection.add(material);
                }
            }
            return prunedCollection;
        }else {
            return prunedCollection;
        }
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
