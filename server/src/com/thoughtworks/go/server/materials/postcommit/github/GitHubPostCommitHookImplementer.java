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
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GithubPostCommitHookImplementer implements PostCommitHookImplementer {

    static final String REPO_URL_PARAM_KEY = "git_url";
    private final UrlMatchers validators = new UrlMatchers();
    private String REPO_OBJECT_KEY;

    @Override
    public Set<Material> prune(Set<Material> materials, Map params) {

        HashSet<Material> prunedCollection = new HashSet<>();
        JsonObject decodedParams;
        try {
            decodedParams = parsePayload(decodePayload(params));
        } catch (UnsupportedEncodingException e) {
            return prunedCollection;
        }

        REPO_OBJECT_KEY = "repository";
        if( decodedParams.getAsJsonObject("repository") != null
                && decodedParams.getAsJsonObject(REPO_OBJECT_KEY).get(REPO_URL_PARAM_KEY).getAsString() != null
                && !decodedParams.getAsJsonObject(REPO_OBJECT_KEY).get(REPO_URL_PARAM_KEY).getAsString().isEmpty()
                ) {
            String paramRepoUrl = decodedParams.getAsJsonObject(REPO_OBJECT_KEY).get(REPO_URL_PARAM_KEY).getAsString();
            for (Material material : materials) {
                if (material instanceof GitMaterial && isUrlEqual(paramRepoUrl, (GitMaterial) material)) {
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

    boolean isUrlEqual(String paramRepoUrl, GitMaterial material) {
        String materialUrl = material.getUrlArgument().forCommandline();
        return validators.perform(paramRepoUrl, materialUrl);
    }
}
