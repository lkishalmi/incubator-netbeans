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

package org.netbeans.modules.gradle.actions;

import org.netbeans.modules.gradle.api.GradleBaseProject;
import org.netbeans.modules.gradle.api.NbGradleProject;
import org.netbeans.modules.gradle.api.execute.ActionMapping;
import org.netbeans.modules.gradle.api.execute.ProjectActionMappingProvider;
import org.netbeans.modules.gradle.spi.WatchedResourceProvider;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectServiceProvider;
import org.openide.filesystems.FileUtil;
import org.openide.util.WeakListeners;

import static org.netbeans.modules.gradle.api.NbGradleProject.PROP_PROJECT_INFO;
import static org.netbeans.modules.gradle.api.NbGradleProject.PROP_RESOURCES;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Laszlo Kishalmi
 */
@ProjectServiceProvider(service = {
    ProjectActionMappingProvider.class, WatchedResourceProvider.class
}, projectType = NbGradleProject.GRADLE_PROJECT_TYPE)
public class ProjectActionMappingProviderImpl implements ProjectActionMappingProvider, WatchedResourceProvider {

    private static final Logger LOG = Logger.getLogger(ProjectActionMappingProviderImpl.class.getName());
    
    private static final String NB_ACTIONS = "nb-actions.xml";
    
    final Project project;
    final PropertyChangeListener pcl;
    final File projectMappingFile;
    
    Set<String> plugins;
    final Map<String, ActionMapping> projectMappings = new HashMap<>();
    final Set<String> noMappingCache = new HashSet<>();
    final Map<String, ActionMapping> mappingCache = new HashMap<>();

    public ProjectActionMappingProviderImpl(Project project) {
        this.project = project;
        pcl = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case PROP_PROJECT_INFO:
                        cleanCache();
                        break;
                    case PROP_RESOURCES:
                        if (projectMappingFile.toURI().equals(evt.getNewValue())) {
                            loadProjectCustomMappings();
                        }
                }
            }
        };
        NbGradleProject.addPropertyChangeListener(project, WeakListeners.propertyChange(pcl, null));
        File projectDir = FileUtil.toFile(project.getProjectDirectory());
        projectMappingFile = new File(projectDir, NB_ACTIONS);
        loadProjectCustomMappings();
    }

    @Override
    public ActionMapping findMapping(String action) {
        // first we check the custom mappings
        ActionMapping result = projectMappings.get(action);
        if (result != null) {
            return result;
        }
        
        // then we check the plugin-aware cache
        result = mappingCache.get(action);
        if ((result != null) || noMappingCache.contains(action)) {
            return result;
        }
        // for last we turn to the defaults
        result = MappingContainer.getDefault().findMapping(action, getPlugins());
        synchronized (this) {
            if (result != null) {
                mappingCache.put(action, result);
            } else {
                noMappingCache.add(action);
            }
        }
        return result;
    }

    private synchronized Set<String> getPlugins() {
        if (plugins == null) {
            GradleBaseProject gbp = GradleBaseProject.get(project);
            plugins = gbp.getPlugins();
        }
        return plugins;
    }
    
    private synchronized void cleanCache() {
        plugins = null;
        noMappingCache.clear();
        mappingCache.clear();        
    }
    
    private synchronized void loadProjectCustomMappings()  {
        projectMappings.clear();
        if (projectMappingFile.canRead()) {
            try (InputStream is = new FileInputStream(projectMappingFile)) {
                Set<ActionMapping> customMappings = ActionMappingScanner.loadMappings(is);
                for (ActionMapping mapping : customMappings) {
                    projectMappings.put(mapping.getName(), mapping);
                }
            } catch (IOException | ParserConfigurationException | SAXException ex) {
                
            }
        }
    }

    @Override
    public Set<File> getWatchedResources() {
        return Collections.singleton(projectMappingFile);
    }

    @Override
    public Set<String> customizedActions() {
        return projectMappings.keySet();
    }
    
    
}
