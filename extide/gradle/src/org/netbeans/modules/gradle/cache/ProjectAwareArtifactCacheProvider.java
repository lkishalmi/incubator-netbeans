/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.netbeans.modules.gradle.cache;

import java.io.File;
import java.util.Set;
import org.netbeans.api.project.Project;
import org.netbeans.modules.gradle.GradleModuleFileCache21;
import org.netbeans.modules.gradle.GradleModuleFileCache21.CachedArtifactVersion;
import org.netbeans.modules.gradle.api.NbGradleProject;
import org.netbeans.modules.gradle.spi.GradleSettings;
import org.netbeans.modules.gradle.spi.execute.GradleDistributionProvider;
import org.netbeans.spi.project.ProjectServiceProvider;

/**
 *
 * @author lkishalmi
 */
@ProjectServiceProvider(service = GradleCachedArtifactProvider.class, projectType = NbGradleProject.GRADLE_PROJECT_TYPE)
public class ProjectAwareArtifactCacheProvider implements GradleCachedArtifactProvider {

    final Project project;

    public ProjectAwareArtifactCacheProvider(Project project) {
        this.project = project;
    }

    @Override
    public File getBinary(String moduleId) {
         CachedArtifactVersion av = getArtifactVersion(moduleId);
         return av.getBinary() != null ? av.getBinary().getPath().toFile() : null;
    }

    @Override
    public File getJavaDoc(String moduleId) {
         CachedArtifactVersion av = getArtifactVersion(moduleId);
         return av.getJavaDoc() != null ? av.getJavaDoc().getPath().toFile() : null;
    }

    @Override
    public File getSources(String moduleId) {
         CachedArtifactVersion av = getArtifactVersion(moduleId);
         return av.getSources() != null ? av.getSources().getPath().toFile() : null;
    }

    private CachedArtifactVersion getArtifactVersion(String moduleId) {
        GradleDistributionProvider pvd = project.getLookup().lookup(GradleDistributionProvider.class);
        File gradleHome = pvd != null ? pvd.getGradleDistribution().getGradleUserHome() : GradleSettings.getDefault().getGradleUserHome();
        GradleModuleFileCache21 cache = GradleModuleFileCache21.getGradleFileCache(gradleHome.toPath());
        return cache.resolveModule(moduleId);
    }
    
}
