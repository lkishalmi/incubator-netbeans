/*
 * Copyright 2020 lkishalmi.
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author lkishalmi
 */
public final class NbBuildExtension {

    Set<String> annotationProcessors = new HashSet<>(Arrays.asList(
        "org.openide.util.lookup",
        "org.openide.util"
    ));


    public NbBuildExtension() {
    }

    public Set<String> getAnnotationProcessors() {
        return annotationProcessors;
    }

    public void setAnnotationProcessors(Set<String> annotationProcessors) {
        this.annotationProcessors = annotationProcessors;
    }

    public void annotationProcessor(String proc) {
        annotationProcessors.add(proc);
    }
}
