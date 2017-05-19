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

import org.junit.Test;

import static org.junit.Assert.*;

public class GitHubRepositoryTest {
    @Test
    public void emptyObjectEquals() throws Exception {
        assertEquals(new GitHubRepository(), new GitHubRepository());
    }

    @Test
    public void completeObjectEquals() throws Exception {
        final String URL = "some_url";
        final String FULL_NAME = "some_url";

        GitHubRepository actual = new GitHubRepository();
        actual.setUrl(URL);
        actual.setFullName(FULL_NAME);

        GitHubRepository expected = new GitHubRepository();
        expected.setUrl(URL);
        expected.setFullName(FULL_NAME);

        assertEquals(actual, expected);
    }
}