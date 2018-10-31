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

package org.netbeans.modules.gradle.java.queries;

import org.netbeans.modules.gradle.java.api.GradleJavaSourceSet;
import org.netbeans.modules.gradle.java.api.GradleJavaSourceSet.SourceType;
import static org.netbeans.modules.gradle.java.api.GradleJavaSourceSet.SourceType.*;
import org.netbeans.modules.gradle.api.NbGradleProject;
import org.netbeans.modules.gradle.java.api.GradleJavaProject;
import static org.netbeans.modules.gradle.java.api.GradleJavaSourceSet.MAIN_SOURCESET_NAME;
import static org.netbeans.modules.gradle.java.api.GradleJavaSourceSet.TEST_SOURCESET_NAME;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.netbeans.api.java.queries.SourceForBinaryQuery;
import org.netbeans.api.project.Project;
import org.netbeans.spi.java.queries.SourceForBinaryQueryImplementation;
import org.netbeans.spi.java.queries.SourceForBinaryQueryImplementation2;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Utilities;

/**
 *
 * @author Laszlo Kishalmi
 */
@ProjectServiceProvider(service = {SourceForBinaryQueryImplementation.class, SourceForBinaryQueryImplementation2.class}, projectType = NbGradleProject.GRADLE_PLUGIN_TYPE + "/java-base")
public class GradleSourceForBinary implements SourceForBinaryQueryImplementation2 {

    private final Project project;
    private final Map<URL, Res> cache = new HashMap<>();

    public GradleSourceForBinary(Project project) {
        this.project = project;
    }

    @Override
    public Result findSourceRoots2(URL binaryRoot) {
        Res ret = cache.get(binaryRoot);
        if (ret == null) {
            try {
                NbGradleProject watcher = NbGradleProject.get(project);
                if (watcher.getQuality().atLeast(NbGradleProject.Quality.FULL)) {
                    GradleJavaProject prj = GradleJavaProject.get(watcher);
                    switch (binaryRoot.getProtocol()) {
                        case "file": {  //NOI18N
                            File root = FileUtil.normalizeFile(Utilities.toFile(binaryRoot.toURI()));
                            for (GradleJavaSourceSet ss : prj.getSourceSets().values()) {
                                for (File dir : ss.getOutputClassDirs()) {
                                    if (root.equals(dir)) {
                                        ret = new Res(watcher, ss.getName(), EnumSet.of(JAVA, GROOVY, SCALA));
                                        break;
                                    }
                                }
                                if (root.equals(ss.getOutputResources())) {
                                    ret = new Res(watcher, ss.getName(), EnumSet.of(RESOURCES));
                                }
                                if (ret != null) {
                                    break;
                                }
                            }
                            break;
                        }
                        case "jar": { //NOI18N
                            File jar = FileUtil.normalizeFile(Utilities.toFile(FileUtil.getArchiveFile(binaryRoot).toURI()));
                            if (jar.equals(prj.getMainJar()) && prj.getSourceSets().containsKey(MAIN_SOURCESET_NAME)) {
                                ret = new Res(watcher, MAIN_SOURCESET_NAME, EnumSet.allOf(SourceType.class));
                            } else if (jar.equals(prj.getArchive(GradleJavaProject.CLASSIFIER_TESTS)) && prj.getSourceSets().containsKey(TEST_SOURCESET_NAME)) {
                                ret = new Res(watcher, TEST_SOURCESET_NAME, EnumSet.allOf(SourceType.class));
                            }
                            break;
                        }
                    }
                }
                if (ret != null) {
                    cache.put(binaryRoot, ret);
                }

            } catch (URISyntaxException ex) {

            }
        }
        return ret;
    }

    @Override
    public SourceForBinaryQuery.Result findSourceRoots(URL binaryRoot) {
        return findSourceRoots2(binaryRoot);
    }

    public static class Res implements Result {

        private final NbGradleProject project;
        private final String sourceSet;
        private final Set<SourceType> sourceTypes;

        public Res(NbGradleProject project, String sourceSet, Set<SourceType> sourceTypes) {
            this.project = project;
            this.sourceSet = sourceSet;
            this.sourceTypes = sourceTypes;
        }

        @Override
        public boolean preferSources() {
            return true;
        }

        @Override
        public FileObject[] getRoots() {
            List<FileObject> roots = new ArrayList<>();
            GradleJavaSourceSet ss = GradleJavaProject.get(project) != null
                    ? GradleJavaProject.get(project).getSourceSets().get(sourceSet)
                    : null;
            if (ss != null) {
                for (SourceType type : sourceTypes) {
                    Set<File> dirs = ss.getSourceDirs(type);
                    for (File dir : dirs) {
                        FileObject fo = FileUtil.toFileObject(dir);
                        if (fo != null) {
                            roots.add(fo);
                        }
                    }
                }
            }
            return roots.toArray(new FileObject[roots.size()]);
        }

        @Override
        public void addChangeListener(ChangeListener l) {
        }

        @Override
        public void removeChangeListener(ChangeListener l) {
        }

    }
}