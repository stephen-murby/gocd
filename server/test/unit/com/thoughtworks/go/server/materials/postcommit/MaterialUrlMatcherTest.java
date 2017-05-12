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

package com.thoughtworks.go.server.materials.postcommit;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class MaterialUrlMatcherTest {

    private MaterialUrlMatcher matcher;

    @Before
    public void setUp() {
        this.matcher = new MaterialUrlMatcher();
    }

    @Test
    public void givenGitHubRepositoryUrl_whenPerform_regexMatchesAnyMaterialVariationForThatRepository() {
    //    The variations of acceptable material urls for the same repository
        Sets.newHashSet(
                "git://github.com/gocd/gocd.git",
                "git://github.com/gocd/gocd",
                "git@github.com:gocd/gocd.git",
                "git@github.com:gocd/gocd",
                "https://github.com/gocd/gocd.git",
                "https://github.com/gocd/gocd",
                "http://github.com/gocd/gocd.git",
                "http://github.com/gocd/gocd"
        ).forEach(variant -> assertTrue(
            "Unable to match the repository url to a corresponding material url in the form " + variant,
            matcher.isValid("git://github.com/gocd/gocd.git", variant))
        );
    }

}