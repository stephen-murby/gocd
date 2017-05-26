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

import java.util.Objects;

public class GitHubPushEvent {

    private JsonArray commits;
    private GitHubRepository repository;

    public GitHubPushEvent(JsonArray commits, GitHubRepository repository) {
        this.commits = commits;
        this.repository = repository;
    }

    public JsonArray getCommits() {
        return commits;
    }

    public boolean hasCommits() {
        return this.commits != null;
    }

    public GitHubRepository getRepository() {
        return repository;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GitHubPushEvent that = (GitHubPushEvent) o;
        return Objects.equals(commits, that.commits) &&
                Objects.equals(repository, that.repository);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commits, repository);
    }
}
