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

package org.netbeans.modules.gradle.java.classpath;

import org.netbeans.modules.gradle.api.NbGradleProject;
import org.netbeans.modules.gradle.api.execute.RunUtils;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.platform.JavaPlatform;
import org.netbeans.api.java.platform.JavaPlatformManager;
import org.netbeans.api.project.Project;
import org.openide.util.WeakListeners;

/**
 *
 * @author Laszlo Kishalmi
 */
public final class BootClassPathImpl extends AbstractGradleClassPathImpl implements PropertyChangeListener {

    JavaPlatformManager platformManager;

    @SuppressWarnings("LeakingThisInConstructor")
    public BootClassPathImpl(Project proj) {
        super(proj);
        platformManager = JavaPlatformManager.getDefault();
        platformManager.addPropertyChangeListener(WeakListeners.propertyChange(this, platformManager));
        NbGradleProject.getPreferences(project, false).addPreferenceChangeListener(new PreferenceChangeListener() {
            @Override
            public void preferenceChange(PreferenceChangeEvent evt) {
                if (RunUtils.PROP_JDK_PLATFORM.equals(evt.getKey())) {
                    clearResourceCache();
                }
            }
        });
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        clearResourceCache();
    }

    @Override
    protected List<URL> createPath() {
        JavaPlatform platform = RunUtils.getActivePlatform(project).second();
        List<URL> ret = new LinkedList<>();
        if (platform != null) {
            for (ClassPath.Entry entry : platform.getBootstrapLibraries().entries()) {
                ret.add(entry.getURL());
            }
        }
        return ret;
    }

    
}
