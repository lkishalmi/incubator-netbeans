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

package org.netbeans.modules.gradle;

import org.netbeans.modules.gradle.api.GradleConfiguration;
import org.netbeans.modules.gradle.api.GradleDependency;
import org.netbeans.modules.gradle.api.NbGradleProject.Quality;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.event.ChangeListener;
import org.openide.modules.OnStart;
import org.openide.modules.Places;
import org.openide.util.ChangeSupport;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Laszlo Kishalmi
 */
public class GradleArtifactStore {

    private static final String GRADLE_ARTIFACT_STORE_INFO = "gradle/artifact-store-info.ser";
    private static final String NO_MODULE = "NONE"; //NOI18N
    public static final RequestProcessor RP = new RequestProcessor("Gradle Artifact Store", 1); //NOI18
    public static final int STORE_VERSION = 2;

    private final Map<File, String> fileToModules = new WeakHashMap<>();
    private final Map<String, ModuleStore> modules = new ConcurrentHashMap<>();

    private static final GradleArtifactStore INSTANCE = new GradleArtifactStore();
    private final ChangeSupport cs = new ChangeSupport(this);
    private final RequestProcessor.Task notifyTask = RP.create(() -> {
        cs.fireChange();
    });

    @OnStart
    public static class Loader implements Runnable {

        @Override
        public void run() {
            getDefault().load();
        }
    }

    public static GradleArtifactStore getDefault() {
        return INSTANCE;
    }

    public Set<File> getBinaries(String id) {
        ModuleStore store = modules.get(id);
        return store != null ? store.binaries : null;
    }
    
    public File getSources(File binary) {
        File ret = null;
        String id = getModuleId(binary);
        if (id != null) {
            ModuleStore store = modules.get(id);
            ret = store != null ? store.source : null;
        }
        if (ret == null) {
            ret = checkM2Heuristic(binary, "sources"); //NOI18N
        }
        return ret;
    }

    public File getJavadoc(File binary) {
        File ret = null;
        String id = getModuleId(binary);
        if (id != null) {
            ModuleStore store = modules.get(id);
            ret = store != null ? store.javadoc : null;
        }
        if (ret == null) {
            ret = checkM2Heuristic(binary, "javadoc"); //NOI18N
        }
        return ret;
    }

    void processProject(GradleProject gp) {
        if (gp.getQuality().worseThan(Quality.FULL)) {
            return; //Do not trust a questionable source
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
        if (changed) {
            store();
            notifyTask.schedule(1000);
        }
    }

    public void clear() {
        modules.clear();
        fileToModules.clear();
        store();
    }

    public final void addChangeListener(ChangeListener l) {
        cs.addChangeListener(l);
    }
    
    public final void removeChangeListener(ChangeListener l) {
        cs.removeChangeListener(l);
    }
    
    @SuppressWarnings("unchecked")
    void load() {
        File cache = Places.getCacheSubfile(GRADLE_ARTIFACT_STORE_INFO);
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(cache))) {
            int cacheVersion = is.readInt();
            if (cacheVersion == STORE_VERSION) {
                Map<String, ModuleStore> archived = (Map<String, ModuleStore>) is.readObject();
                clear();
                for (Map.Entry<String, ModuleStore> entry : archived.entrySet()) {
                    if (entry.getValue().validate()) {
                        modules.put(entry.getKey(), entry.getValue());
                        for (File file : entry.getValue().getFiles()) {
                            fileToModules.put(file, entry.getKey());
                        }
                    }
                }
            }
        } catch (Throwable ex) {
            // Nothing to be done. Disk cache is invalid, it will be overwritten.
            clear();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    void store() {
        File cache = Places.getCacheSubfile(GRADLE_ARTIFACT_STORE_INFO);
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(cache))) {
            os.writeInt(STORE_VERSION);
            os.writeObject(new HashMap(modules));
        } catch (IOException ex) {

        }
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

    private static String gradleCacheHeuristic(File jar) {
        String ret = NO_MODULE;
        Path p = jar.toPath();
        int names = p.getNameCount();
        if ((names > 6) && "files-2.1".equals(p.getName(names - 6).toString())) { //NOI18N
            ret = p.getName(names - 5) + ":" + p.getName(names - 4) + ":" + p.getName(names - 3);
        }
        return ret;
    }

    private String getModuleId(File f) {
        String id = fileToModules.get(f);
        if (id == null) {
            id = gradleCacheHeuristic(f);
            fileToModules.put(f, id);
        }
        return id;
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
