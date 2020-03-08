/*
 * Copyright 2018 lkishalmi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.netbeans.gradle.nbsupport.nb.support;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author lkishalmi
 */
public class NbCluster extends DependencyItem<NbCluster>{
    
    final Set<String> modules = new LinkedHashSet<>();
    final File dir;

    public NbCluster(String name, File dir) {
        super(name);
        this.dir = dir;
    }

    public void project(String project) {
        modules.add(project);
    }

    public File getDir() {
        return dir;
    }
}
