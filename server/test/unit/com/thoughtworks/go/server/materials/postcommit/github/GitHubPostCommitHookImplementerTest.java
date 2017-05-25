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
import com.thoughtworks.go.config.materials.git.GitMaterial;
import com.thoughtworks.go.domain.materials.Material;
import com.thoughtworks.go.util.command.UrlArgument;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class GitHubPostCommitHookImplementerTest {

    private GitHubPostCommitHookImplementer implementer;
    private Set<Material> CONFIGURED_MATERIALS;

    @Mock
    private GitHubWebHookMessageParser mockParser;

    @Before
    public void setUp() {
        initMocks(this);
        implementer = new GitHubPostCommitHookImplementer(mockParser);
        CONFIGURED_MATERIALS = setupMaterials();
    }

    private Set<Material> setupMaterials() {
        GitMaterial httpMaterialWithSuffix = mock(GitMaterial.class);
        GitMaterial httpMaterialNoSuffix = mock(GitMaterial.class);
        when(httpMaterialWithSuffix.getUrlArgument()).thenReturn(new UrlArgument("http://github.com/organisation/repository.git"));
        when(httpMaterialNoSuffix.getUrlArgument()).thenReturn(new UrlArgument("http://github.com/organisation/repository"));

        GitMaterial httpsMaterialWithSuffix = mock(GitMaterial.class);
        GitMaterial httpsMaterialNoSuffix = mock(GitMaterial.class);
        when(httpsMaterialWithSuffix.getUrlArgument()).thenReturn(new UrlArgument("https://github.com/organisation/repository.git"));
        when(httpsMaterialNoSuffix.getUrlArgument()).thenReturn(new UrlArgument("https://github.com/organisation/repository"));

        GitMaterial gitMaterialWithSuffix = mock(GitMaterial.class);
        GitMaterial gitMaterialNoSuffix = mock(GitMaterial.class);
        when(gitMaterialWithSuffix.getUrlArgument()).thenReturn(new UrlArgument("git://github.com/organisation/repository.git"));
        when(gitMaterialNoSuffix.getUrlArgument()).thenReturn(new UrlArgument("git://github.com/organisation/repository"));

        GitMaterial svnMaterialWithSuffix = mock(GitMaterial.class);
        GitMaterial svnMaterialNoSuffix = mock(GitMaterial.class);
        when(svnMaterialWithSuffix.getUrlArgument()).thenReturn(new UrlArgument("https://github.com/organisation/repository.git"));
        when(svnMaterialNoSuffix.getUrlArgument()).thenReturn(new UrlArgument("https://github.com/organisation/repository"));

        GitMaterial sshMaterialWithSuffix = mock(GitMaterial.class);
        GitMaterial sshMaterialNoSuffix = mock(GitMaterial.class);
        when(sshMaterialWithSuffix.getUrlArgument()).thenReturn(new UrlArgument("git@github.com/organisation/repository.git"));
        when(sshMaterialNoSuffix.getUrlArgument()).thenReturn(new UrlArgument("git@github.com/organisation/repository"));

        GitMaterial nonMatchingMaterial = mock(GitMaterial.class);
        when(nonMatchingMaterial.getUrlArgument()).thenReturn(new UrlArgument("https://github.com/organisation/some_other_repository"));

        Set<Material> configuredMaterials = new HashSet<>(
                Arrays.asList(
                        httpMaterialWithSuffix,
                        httpMaterialNoSuffix,
                        gitMaterialWithSuffix,
                        gitMaterialNoSuffix,
                        svnMaterialWithSuffix,
                        svnMaterialNoSuffix,
                        sshMaterialWithSuffix,
                        sshMaterialNoSuffix,
                        nonMatchingMaterial
                )
        );
        return configuredMaterials;
    }


    @Test
    public void givenKnownMaterial_andRequestParamsContainingGithubPayload_returnMaterialsMatchingGitUrlInPayload() throws UnsupportedEncodingException {
        Map requestParameters = new HashMap<String, String>();
        requestParameters.put("payload", "good_payload");

        GitHubPushEvent event = new GitHubPushEvent(new JsonArray(), new GitHubRepository("http://github.com/organisation/repository", "organisation/repository"));

        when(mockParser.parse(requestParameters)).thenReturn(event);

        Set<Material> matchingMaterials = implementer.prune(CONFIGURED_MATERIALS, requestParameters);

        assertThat(matchingMaterials.size(), is(8));
    }

    @Test
    public void givenWebhookPayload_forEventOtherThanPush() throws UnsupportedEncodingException {
        Map requestParameters = new HashMap<String, String>();
        requestParameters.put("payload", "bad_payload");

        GitHubPushEvent event = new GitHubPushEvent(null, null);

        when(mockParser.parse(requestParameters)).thenReturn(event);

        Set<Material> matchingMaterials = implementer.prune(CONFIGURED_MATERIALS, requestParameters);

        assertThat(matchingMaterials.size(), is(0));

    }
}