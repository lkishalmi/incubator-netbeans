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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.netbeans.modules.gradle.GradleProject;
import org.netbeans.modules.gradle.api.GradleConfiguration;
import org.netbeans.modules.gradle.api.GradleDependency;
import org.netbeans.modules.gradle.api.NbGradleProject;
import org.netbeans.modules.gradle.cache.ArtifactDiskCache.ArtifactInfo;

/**
 *
 * @author lkishalmi
 */
public class ArtifactDiskCache extends AbstractDiskCache<File, ArtifactInfo> {
    private static final String ARTIFACT_CACHE_FILE_NAME = ".gradle/nb-cache/artifacts.ser"; //NOI18N
    private static final int COMPATIBLE_CACHE_VERSION = 1;

    private static Map<File, ArtifactDiskCache> diskCaches = new WeakHashMap<>();

    protected ArtifactDiskCache(File key) {
        super(key);
    }
    
    @Override
    protected int cacheVersion() {
        return COMPATIBLE_CACHE_VERSION;
    }

    @Override
    protected File cacheFile() {
        return new File(key, ARTIFACT_CACHE_FILE_NAME);
    }

    @Override
    protected Set<File> cacheInvalidators() {
        return Collections.emptySet();
    }

    public static ArtifactDiskCache get(File key) {
        ArtifactDiskCache ret = diskCaches.get(key);
        if (ret == null) {
            ret = new ArtifactDiskCache(key);
            diskCaches.put(key, ret);
        }
        return ret;
    }

    public ArtifactInfo newArtifactInfo() {
        return  new ArtifactInfo();
    }

    public class ArtifactInfo implements Serializable {
        private final Map<File, String> fileToModules = new WeakHashMap<>();
        private final Map<String, ModuleStore> modules = new ConcurrentHashMap<>();

        protected ArtifactInfo() {
        }

        public boolean updateInfo(GradleProject gp) {
            if (gp.getQuality().worseThan(NbGradleProject.Quality.FULL)) {
                return false; //Do not trust a questionable source
            }
            boolean changed = false;
            for (GradleConfiguration conf : gp.getBaseProject().getConfigurations().values()) {
                for (GradleDependency.ModuleDependency module : conf.getModules()) {
                    ModuleStore newStore = new ModuleStore(module);
                    ModuleStore oldStore = modules.get(module.getId());
                    if (!newStore.equals(oldStore)) {
                        changed = true;
                        if (oldStore != null) {
                            oldStore.merge(newStore);
                        } else {
                            modules.put(module.getId(), newStore);
                        }
                        for (File file : newStore.getFiles()) {
                            fileToModules.put(file, newStore.id);
                        }
                    }
                }
            }
            return changed;
        }
    }

    public final class ModuleStore implements Serializable {
        final String id;
        Set<File> binaries;
        File source;
        File javadoc;

        public ModuleStore(GradleDependency.ModuleDependency module) {
            id = module.getId();
            binaries = new HashSet<>(module.getArtifacts());
            source = module.getSources().isEmpty() ? null : module.getSources().iterator().next();
            javadoc = module.getJavadoc().isEmpty() ? null : module.getJavadoc().iterator().next();
        }

        public Collection<File> getFiles() {
            ArrayList<File> ret = new ArrayList<>(binaries);
            if (source != null) ret.add(source);
            if (javadoc != null) ret.add(javadoc);
            return ret;
        }

        public void merge(ModuleStore s) {
            binaries.addAll(s.binaries);
            source = source == null ? s.source : null;
            javadoc = javadoc == null ? s.javadoc : null;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        public boolean validate() {
            Iterator<File> it = binaries.iterator();
            while (it.hasNext()) {
                if (!it.next().isFile()) it.remove();
            }
            if (source != null) source = source.isFile() ? source : null;
            if (javadoc != null) javadoc = javadoc.isFile() ? javadoc : null;
            return !binaries.isEmpty();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ModuleStore other = (ModuleStore) obj;
            if (!Objects.equals(this.binaries, other.binaries)) {
                return false;
            }
            if (!Objects.equals(this.source, other.source)) {
                return false;
            }
            return Objects.equals(this.javadoc, other.javadoc);
        }

    }

}
