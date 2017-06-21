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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class GitHubPushEventTest {
    @Test
    public void emptyObjectEquals() throws Exception {
        GitHubPushEvent expectedEvent = new GitHubPushEvent();
        GitHubPushEvent actualEvent = new GitHubPushEvent();

        assertEquals(actualEvent, expectedEvent);
    }

    @Test
    public void completeObjectEquals() throws Exception {
        GitHubPushEvent expectedEvent = new GitHubPushEvent();
        expectedEvent.setCommits(new JsonArray());
        expectedEvent.setRepository(new GitHubRepository());

        GitHubPushEvent actualEvent = new GitHubPushEvent();
        actualEvent.setCommits(new JsonArray());
        actualEvent.setRepository(new GitHubRepository());

        assertEquals(actualEvent, expectedEvent);
    }

    @Test
    public void string(){
        GitHubPushEvent expectedEvent = new GitHubPushEvent();
        expectedEvent.setCommits(new JsonArray());
        expectedEvent.setRepository(new GitHubRepository());

        Gson gson = new GsonBuilder().create();
        System.out.println(gson.toJson(expectedEvent));
    }
}