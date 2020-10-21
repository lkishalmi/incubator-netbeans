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

package org.netbeans.modules.gradle.api;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.modules.gradle.GradleModuleFileCache21;
import org.netbeans.modules.gradle.spi.GradleFiles;
import org.netbeans.modules.gradle.spi.GradleSettings;
import org.netbeans.modules.gradle.spi.execute.GradleDistributionProvider;

/**
 * Utility methods working with Gradle projects and Artifacts.
 *
 * @since 1.0
 * @author Laszlo Kishalmi
 */
public final class GradleProjects {

    private GradleProjects() {}

    /**
     * Get the Source artifact for the given binary if available.
     * @param binary the location of the binary artifact.
     * @return the location of the Source artifact or {@code null} if that is
     * not available.
     */
    @Deprecated
    public static File getSources(File binary) {
        return findSources(GradleSettings.getDefault().getGradleUserHome(), binary);
    }

    /**
     * Get the JavaDoc artifact for the given binary if available.
     * @param binary the location of the binary artifact.
     * @return the location of the JavaDoc artifact or {@code null} if that is
     * not available.
     */
    @Deprecated
    public static File getJavadoc(File binary) {
        return findJavadoc(GradleSettings.getDefault().getGradleUserHome(), binary);
    }

    /**
     * Get the Source artifact for the given binary if available.
     * @param binary the location of the binary artifact.
     * @return the location of the Source artifact or {@code null} if that is
     * not available.
     */
    public static File getSources(Project project, File binary) {
        GradleDistributionProvider pvd = project.getLookup().lookup(GradleDistributionProvider.class);
        return pvd != null ? findSources(pvd.getGradleDistribution().getGradleUserHome(), binary) : null;
    }

    /**
     * Get the JavaDoc artifact for the given binary if available.
     * @param binary the location of the binary artifact.
     * @return the location of the JavaDoc artifact or {@code null} if that is
     * not available.
     */
    public static File getJavadoc(Project project, File binary) {
        GradleDistributionProvider pvd = project.getLookup().lookup(GradleDistributionProvider.class);
        return pvd != null ? findJavadoc(pvd.getGradleDistribution().getGradleUserHome(), binary) : null;
    }

    /**
     * Returns all the siblings project of the given project which are opened in the IDE.
     * It gives empty result on non-Gradle projects and root Gradle projects.
     * The map also won't contain the given project itself.
     *
     * @param project a project.
     * @return an unmodifiable {@code <name, project>} map of the siblings, never {@code null}
     */
    public static Map<String, Project> openedSiblings(Project project) {
        Map<String, Project> ret = new HashMap<>();
        GradleBaseProject gbp = GradleBaseProject.get(project);
        if (gbp != null) {
            for (Project openProject : OpenProjects.getDefault().getOpenProjects()) {
                GradleBaseProject test = GradleBaseProject.get(openProject);
                if ((test != null) && gbp.isSibling(test)) {
                    ret.put(test.getName(), openProject);
                }
            }
        }

        return Collections.unmodifiableMap(ret);
    }

    /**
     * Returns all the projects on which the given project depends and are opened in the IDE.
     * It gives empty result on non-Gradle projects. On root project it returns all its
     * opened sub-projects. The map also won't contain the given project itself.
     *
     * @param project a project.
     * @return an unmodifiable {@code <name, project>} map of dependencies, never {@code null}
     */
    public static Map<String, Project> openedProjectDependencies(Project project) {
        Map<String, Project> ret = new HashMap<>();
        GradleBaseProject gbp = GradleBaseProject.get(project);
        if (gbp != null) {
            if (gbp.isRoot()) {
                Map<String, File> subProjects = gbp.getSubProjects();
                for (Project openProject : OpenProjects.getDefault().getOpenProjects()) {
                    GradleBaseProject test = GradleBaseProject.get(openProject);
                    if ((test != null) && gbp.isRootOf(test)) {
                        ret.put(test.getName(), openProject);
                    }
                }

            } else {
                Map<String, Project> siblings = openedSiblings(project);
                collectProjectDependencies(ret, siblings, project);
            }
        }

        return Collections.unmodifiableMap(ret);
    }

    /**
     * Try to determine if the given directory belongs to a Gradle project.
     * This method use heuristics and usual project layout of project files.
     * The returned value is not necessary correct.
     *
     * @param dir the directory to test
     * @return true if the given directory is suspected as a Gradle project.
     */
    public static boolean testForProject(File dir) {
        return new GradleFiles(dir).isProject();
    }

    /**
     * Try to determine if the given directory belongs to a Gradle root project.
     * This method use heuristics and usual project layout of project files.
     * The returned value is not necessary correct.
     *
     * @param dir the directory to test
     * @return true if the given directory is suspected as a Gradle root project.
     */
    public static boolean testForRootProject(File dir) {
        return new GradleFiles(dir).isRootProject();
    }

    private static void collectProjectDependencies(final Map<String, Project> ret, Map<String, Project> siblings, final Project prj) {
        GradleBaseProject gbp = GradleBaseProject.get(prj);
        for (GradleDependency.ProjectDependency dep : gbp.getProjectDependencies()) {
            String id = dep.getId();
            if (!ret.containsKey(id) && siblings.containsKey(id)) {
                Project test = siblings.get(id);
                ret.put(id, test);
                collectProjectDependencies(ret, siblings, test);
            }
        }
    }

    private static File findSources(File gradleHome, File binary) {
        File ret = null;
        GradleModuleFileCache21 cache = GradleModuleFileCache21.getGradleFileCache(gradleHome.toPath());
        if (cache.contains(binary.toPath())) {
            try {
                GradleModuleFileCache21.CachedArtifactVersion v = cache.resolveCachedArtifactVersion(binary.toPath());
                GradleModuleFileCache21.CachedArtifactVersion.Entry sources = v.getSources();
                ret = sources != null ? sources.getPath().toFile() : null;
            } catch (IllegalArgumentException ex) {}
        }
        return ret != null ? ret : checkM2Heuristic(binary, "sources"); //NOI18N
    }

    private static File findJavadoc(File gradleHome, File binary) {
        File ret = null;
        GradleModuleFileCache21 cache = GradleModuleFileCache21.getGradleFileCache(gradleHome.toPath());
        if (cache.contains(binary.toPath())) {
            try {
                GradleModuleFileCache21.CachedArtifactVersion v = cache.resolveCachedArtifactVersion(binary.toPath());
                GradleModuleFileCache21.CachedArtifactVersion.Entry javadoc = v.getJavaDoc();
                ret = javadoc != null ? javadoc.getPath().toFile() : null;
            } catch (IllegalArgumentException ex) {}
        }
        return ret != null ? ret : checkM2Heuristic(binary, "javadoc"); //NOI18N
    }

    private static File checkM2Heuristic(File mainJar, String classifier) {
        File ret = null;
        String fname = mainJar.getName();
        StringBuilder guessName = new StringBuilder(fname);
        if (fname.endsWith(".jar")) {                                       //NOI18N
            guessName = guessName.delete(guessName.length() - 4, guessName.length());
            guessName.append('-').append(classifier).append(".jar");        //NOI18N
            File guess = new File(mainJar.getParentFile(), guessName.toString());
            if (guess.isFile()) {
                ret = guess;
            }
        }
        return ret;
    }
}
