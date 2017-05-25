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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class GitHubWebHookMessageParser {

    public GitHubPushEvent parse(Map params) throws UnsupportedEncodingException {
        String pushEventPayload = urlDecodePayload(params);
        Gson gson = new GsonBuilder().create();
        GitHubPushEvent gitHubPushEvent = gson.fromJson(pushEventPayload, GitHubPushEvent.class);
        // A push event will have a commits field, other event type payloads will not.
        return gitHubPushEvent.hasCommits() == null ? null : gitHubPushEvent;
    }

    private String urlDecodePayload(Map params) throws UnsupportedEncodingException {
        String payload = ((String)params.get("payload"));
        return URLDecoder.decode(payload, "utf-8");
    }


}
