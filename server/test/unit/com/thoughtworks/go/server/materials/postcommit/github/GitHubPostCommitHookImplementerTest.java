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

import com.thoughtworks.go.config.materials.git.GitMaterial;
import com.thoughtworks.go.domain.materials.Material;
import com.thoughtworks.go.util.command.UrlArgument;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GitHubPostCommitHookImplementerTest {

    private GitHubPostCommitHookImplementer implementer;
    private String URL_ENCODED_GITHUB_ORGANISATION_PAYLOAD = "%7B%0A%20%20%22ref%22%3A%20%22refs%2Fheads%2Fmaster%22%2C%0A%20%20%22before%22%3A%20%2213b791c0bb5959f4717bc060820abbef9e953713%22%2C%0A%20%20%22after%22%3A%20%2245c6afe2fb45cb8aad5860a421835d1d691aeef5%22%2C%0A%20%20%22created%22%3A%20false%2C%0A%20%20%22deleted%22%3A%20false%2C%0A%20%20%22forced%22%3A%20false%2C%0A%20%20%22base_ref%22%3A%20null%2C%0A%20%20%22compare%22%3A%20%22https%3A%2F%2Fgithub.atcloud.io%2Ftesting-webhooks%2Ftesting-webhooks%2Fcompare%2F13b791c0bb59...45c6afe2fb45%22%2C%0A%20%20%22commits%22%3A%20%5B%5D%2C%0A%20%20%22head_commit%22%3A%20%7B%0A%20%20%20%20%22id%22%3A%20%2245c6afe2fb45cb8aad5860a421835d1d691aeef5%22%2C%0A%20%20%20%20%22tree_id%22%3A%20%225f48dc48de3bacead579ae80f5882124c83b3a02%22%2C%0A%20%20%20%20%22distinct%22%3A%20true%2C%0A%20%20%20%20%22message%22%3A%20%22Update%20README.md%22%2C%0A%20%20%20%20%22timestamp%22%3A%20%222017-04-05T18%3A43%3A03%2B01%3A00%22%2C%0A%20%20%20%20%22url%22%3A%20%22https%3A%2F%2Fgithub.atcloud.io%2Ftesting-webhooks%2Ftesting-webhooks%2Fcommit%2F45c6afe2fb45cb8aad5860a421835d1d691aeef5%22%2C%0A%20%20%20%20%22author%22%3A%20%7B%0A%20%20%20%20%20%20%22name%22%3A%20%22Stephen%20Murby%22%2C%0A%20%20%20%20%20%20%22email%22%3A%20%22Stephen.Murby%40autotrader.co.uk%22%2C%0A%20%20%20%20%20%20%22username%22%3A%20%22Stephen-Murby%22%0A%20%20%20%20%7D%2C%0A%20%20%20%20%22committer%22%3A%20%7B%0A%20%20%20%20%20%20%22name%22%3A%20%22GitHub%20Enterprise%22%2C%0A%20%20%20%20%20%20%22email%22%3A%20%22noreply%40github.atcloud.io%22%0A%20%20%20%20%7D%2C%0A%20%20%20%20%22added%22%3A%20%5B%0A%20%20%20%20%20%20%0A%20%20%20%20%5D%2C%0A%20%20%20%20%22removed%22%3A%20%5B%0A%20%20%20%20%20%20%0A%20%20%20%20%5D%2C%0A%20%20%20%20%22modified%22%3A%20%5B%0A%20%20%20%20%20%20%22README.md%22%0A%20%20%20%20%5D%0A%20%20%7D%2C%0A%20%20%22repository%22%3A%20%7B%0A%20%20%20%20%22full_name%22%3A%20%22organisation%2Frepository%22%2C%0A%20%20%20%20%22url%22%3A%20%22https%3A%2F%2Fgithub.com%2Forganisation%2Frepository%22%2C%0A%20%20%20%20%22git_url%22%3A%20%22git%3A%2F%2Fgithub.com%2Forganisation%2Frepository.git%22%2C%0A%20%20%20%20%22ssh_url%22%3A%20%22git%40github.com%3Aorganisation%2Frepository.git%22%2C%0A%20%20%20%20%22clone_url%22%3A%20%22https%3A%2F%2Fgithub.com%2Forganisation%2Frepository.git%22%2C%0A%20%20%20%20%22svn_url%22%3A%20%22https%3A%2F%2Fgithub.com%2Forganisation%2Frepository%22%0A%7D%2C%0A%20%20%22pusher%22%3A%20%7B%0A%20%20%20%20%22name%22%3A%20%22Stephen-Murby%22%2C%0A%20%20%20%20%22email%22%3A%20%22Stephen.Murby%40autotrader.co.uk%22%0A%20%20%7D%2C%0A%20%20%22organization%22%3A%20%7B%0A%20%20%20%20%22login%22%3A%20%22testing-webhooks%22%2C%0A%20%20%20%20%22description%22%3A%20null%0A%20%20%7D%2C%0A%20%20%22sender%22%3A%20%7B%0A%20%20%20%20%22login%22%3A%20%22Stephen-Murby%22%0A%20%20%7D%0A%7D";
    private Set<Material> CONFIGURED_MATERIALS;

    @Before
    public void setUp() {
        implementer = new GitHubPostCommitHookImplementer();
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
    public void givenKnownMaterial_andRequestParamsContainingGithubPayload_returnMaterialsMatchingGitUrlInPayload() {
        Map requestParameters = new HashMap<String, String>();
        requestParameters.put("payload", URL_ENCODED_GITHUB_ORGANISATION_PAYLOAD);

        Set<Material> matchingMaterials = implementer.prune(CONFIGURED_MATERIALS, requestParameters);

        assertThat(matchingMaterials.size(), is(8));
    }

    @Test
    public void givenWebhookPayload_forEventOtherThanPush() {
        
    }
}