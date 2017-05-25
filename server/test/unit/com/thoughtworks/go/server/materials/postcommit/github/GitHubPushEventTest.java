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

import com.google.gson.JsonArray;
import org.junit.Test;

import static org.junit.Assert.*;

public class GitHubPushEventTest {
    @Test
    public void emptyObjectEquals() throws Exception {
        GitHubPushEvent expectedEvent = new GitHubPushEvent(null, null);
        GitHubPushEvent actualEvent = new GitHubPushEvent(null, null);

        assertEquals(actualEvent, expectedEvent);
    }

    @Test
    public void completeObjectEquals() throws Exception {
        GitHubRepository REPOSITORY = new GitHubRepository("url", "name");

        GitHubPushEvent expectedEvent = new GitHubPushEvent(new JsonArray(), REPOSITORY);
        GitHubPushEvent actualEvent = new GitHubPushEvent(new JsonArray(), REPOSITORY);

        assertEquals(actualEvent, expectedEvent);
    }
}