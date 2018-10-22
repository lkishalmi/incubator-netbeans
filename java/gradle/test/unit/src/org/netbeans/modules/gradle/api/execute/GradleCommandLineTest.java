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

package org.netbeans.modules.gradle.api.execute;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.gradle.tooling.ConfigurableLauncher;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Laszlo Kishalmi
 */
public class GradleCommandLineTest {
    
    @Test
    public void testGetSupportedCommandLine() {
        System.out.println("getSupportedCommandLine");
        GradleCommandLine instance = new GradleCommandLine("--offline", "--no-daemon");
        List<String> expResult = Arrays.asList("--offline");
        List<String> result = instance.getSupportedCommandLine();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetFullCommandLine() {
        System.out.println("getSupportedCommandLine");
        GradleCommandLine instance = new GradleCommandLine("--offline", "--no-daemon");
        List<String> expResult = Arrays.asList("--offline", "--no-daemon");
        List<String> result = instance.getFullCommandLine();
        assertEquals(expResult, result);
    }

    @Test
    public void testGetTasks() {
        System.out.println("getTasks");
        GradleCommandLine instance = new GradleCommandLine("-a", "clean", "build");
        List<String> expResult = Arrays.asList("clean", "build");
        List<String> result = instance.getTasks();
        assertEquals(expResult, result);
    }

    @Test
    public void testSetTasks() {
        System.out.println("setTasks");
        List<String> tasks = Arrays.asList("assemble");
        GradleCommandLine instance = new GradleCommandLine("-a", "clean", "build");
        instance.setTasks(tasks);
        assertEquals(tasks, instance.getTasks());
        assertTrue(instance.hasFlag(GradleCommandLine.Flag.NO_REBUILD));
    }

    @Test
    public void testRemoveTask() {
        System.out.println("removeTask");
        GradleCommandLine instance = new GradleCommandLine("-a", "clean", "build");
        instance.removeTask("clean");
        assertEquals(Arrays.asList("build"), instance.getTasks());
    }

    @Test
    public void testAddTask() {
        System.out.println("addTask");
        GradleCommandLine instance = new GradleCommandLine("-a", "clean");
        instance.addTask("build");
        assertEquals(Arrays.asList("clean", "build"), instance.getTasks());
    }

    @Test
    public void testHasTask() {
        System.out.println("hasTask");
        GradleCommandLine instance = new GradleCommandLine("-a", "clean");
        assertTrue(instance.hasTask("clean"));
        assertFalse(instance.hasTask("build"));
    }

    @Test
    public void testHasFlag() {
        System.out.println("hasFlag");
        GradleCommandLine.Flag flag = GradleCommandLine.Flag.CONFIGURE_ON_DEMAND;
        GradleCommandLine instance = new GradleCommandLine("--configure-on-demand", "build");
        assertTrue(instance.hasFlag(flag));
        assertFalse(instance.hasFlag(GradleCommandLine.Flag.OFFLINE));
    }

    @Test
    public void testAddFlag() {
        System.out.println("addFlag");
        GradleCommandLine.Flag flag = GradleCommandLine.Flag.NO_REBUILD;
        GradleCommandLine instance = new GradleCommandLine("--configure-on-demand", "build");
        instance.addFlag(flag);
        assertTrue(instance.hasFlag(flag));
        assertTrue(instance.hasFlag(GradleCommandLine.Flag.CONFIGURE_ON_DEMAND));
        assertFalse(instance.hasFlag(GradleCommandLine.Flag.OFFLINE));
    }

    @Test
    public void testRemoveFlag() {
        System.out.println("removeFlag");
        GradleCommandLine.Flag flag = GradleCommandLine.Flag.NO_REBUILD;
        GradleCommandLine instance = new GradleCommandLine("-a", "--configure-on-demand", "build");
        instance.removeFlag(flag);
        assertFalse(instance.hasFlag(flag));
        assertTrue(instance.hasFlag(GradleCommandLine.Flag.CONFIGURE_ON_DEMAND));
        assertFalse(instance.hasFlag(GradleCommandLine.Flag.OFFLINE));
    }

    @Test
    public void testSetFlag() {
        System.out.println("setFlag");
        GradleCommandLine instance = new GradleCommandLine("--configure-on-demand", "build");
        instance.setFlag(GradleCommandLine.Flag.CONFIGURE_ON_DEMAND, false);
        instance.setFlag(GradleCommandLine.Flag.NO_REBUILD, true);
        assertEquals(Arrays.asList("-a", "build"), instance.getSupportedCommandLine());
    }

    @Test
    public void testAddParameter() {
        System.out.println("addParameter");
        GradleCommandLine instance = new GradleCommandLine("--configure-on-demand", "build");
        instance.addParameter(GradleCommandLine.Parameter.EXCLUDE_TASK, "test");
        assertEquals(Arrays.asList("--configure-on-demand", "-x", "test", "build"), instance.getSupportedCommandLine());
    }

    @Test
    public void testGetFirstParameter() {
        System.out.println("getFirstParameter");
        GradleCommandLine instance = new GradleCommandLine("build", "-x", "test", "-x", "check");
        String result = instance.getFirstParameter(GradleCommandLine.Parameter.EXCLUDE_TASK);
        assertEquals("test", result);
    }

    @Test
    public void testHasParameter() {
        System.out.println("hasParameter");
        GradleCommandLine instance = new GradleCommandLine("build", "-x", "test", "-x", "check");
        assertTrue(instance.hasParameter(GradleCommandLine.Parameter.EXCLUDE_TASK));
        assertFalse(instance.hasParameter(GradleCommandLine.Parameter.CONSOLE));
    }

    @Test
    public void testGetParameters() {
        System.out.println("getParameters");
        GradleCommandLine instance = new GradleCommandLine("build", "-x", "test", "-x", "check");
        assertTrue(instance.hasParameter(GradleCommandLine.Parameter.EXCLUDE_TASK));
        assertFalse(instance.hasParameter(GradleCommandLine.Parameter.INIT_SCRIPT));
    }

    @Test
    public void testGetExcludedTasks() {
        System.out.println("getExcludedTasks");
        GradleCommandLine instance = new GradleCommandLine("build", "-x", "test", "-x", "check");
        Set<String> result = instance.getExcludedTasks();
        assertTrue(result.containsAll(Arrays.asList("test", "check")));
    }

    @Test
    public void testSetExcludedTasks() {
        System.out.println("setExcludedTasks");
        GradleCommandLine instance = new GradleCommandLine("build");
        instance.setExcludedTasks(Arrays.asList("check", "test"));
        assertEquals(Arrays.asList("-x", "check", "-x", "test", "build"), instance.getSupportedCommandLine());
    }

    @Test
    public void testRemoveParameters() {
        System.out.println("removeParameters");
        GradleCommandLine.Parameter param = GradleCommandLine.Parameter.INIT_SCRIPT;
        GradleCommandLine instance = new GradleCommandLine("--init-script", "init.gradle");
        instance.removeParameters(param);
        assertFalse(instance.hasParameter(param));
    }

    @Test
    public void testGetProperty() {
        System.out.println("getProperty");
        GradleCommandLine instance = new GradleCommandLine("--system-prop", "HELLO=NetBeans", "-Pgreet=World");
        assertEquals("NetBeans", instance.getProperty(GradleCommandLine.Property.SYSTEM, "HELLO"));
        assertEquals("World", instance.getProperty(GradleCommandLine.Property.PROJECT, "greet"));
    }

    @Test
    public void testGetLoglevel() {
        System.out.println("getLoglevel");
        assertEquals(GradleCommandLine.LogLevel.NORMAL, new GradleCommandLine().getLoglevel());
        assertEquals(GradleCommandLine.LogLevel.INFO, new GradleCommandLine("--info").getLoglevel());
        assertEquals(GradleCommandLine.LogLevel.DEBUG, new GradleCommandLine("-d").getLoglevel());
        assertEquals(GradleCommandLine.LogLevel.QUIET, new GradleCommandLine("-q").getLoglevel());
    }

    @Test
    public void testSetLogLevel() {
        System.out.println("setLogLevel");
        GradleCommandLine.LogLevel level = GradleCommandLine.LogLevel.QUIET;
        GradleCommandLine instance = new GradleCommandLine("--debug");
        instance.setLogLevel(level);
        assertEquals(GradleCommandLine.LogLevel.QUIET, instance.getLoglevel());
    }

    @Test
    public void testAddProjectProperty() {
        System.out.println("addProjectProperty");
        GradleCommandLine instance = new GradleCommandLine("build");
        instance.addProjectProperty("version", "1.0.0");
        assertEquals(Arrays.asList("-Pversion=1.0.0", "build"), instance.getFullCommandLine());
    }

    @Test
    public void testAddSystemProperty() {
        System.out.println("addSystemProperty");
        GradleCommandLine instance = new GradleCommandLine("build");
        instance.addSystemProperty("hello", "NetBeans");
        assertEquals(Arrays.asList("-Dhello=NetBeans", "build"), instance.getSupportedCommandLine());
    }

    @Test
    public void testGetStackTrace() {
        System.out.println("getStackTrace");
        assertEquals(GradleCommandLine.StackTrace.NONE, new GradleCommandLine().getStackTrace());
        assertEquals(GradleCommandLine.StackTrace.SHORT, new GradleCommandLine("-s").getStackTrace());
        assertEquals(GradleCommandLine.StackTrace.FULL, new GradleCommandLine("-S").getStackTrace());
    }

    @Test
    public void testSetStackTrace() {
        System.out.println("setStackTrace");
        GradleCommandLine.StackTrace st = GradleCommandLine.StackTrace.NONE;
        GradleCommandLine instance = new GradleCommandLine("-S");
        instance.setStackTrace(st);
        assertEquals(GradleCommandLine.StackTrace.NONE, instance.getStackTrace());
    }

}