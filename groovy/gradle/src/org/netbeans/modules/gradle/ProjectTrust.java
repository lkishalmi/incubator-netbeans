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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.prefs.Preferences;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.netbeans.api.project.Project;
import org.openide.util.NbPreferences;

/**
 *
 * @author lkishalmi
 */
public class ProjectTrust {
    private static final String KEY_SALT     = "salt";     //NOI18N
    private static final String NODE_PROJECT = "projects"; //NOI18N
    private static final String NODE_TRUST   = "trust";    //NOI18N

    private static final String HMAC_SHA256  = "HmacSHA256"; //NOI18N

    private static ProjectTrust instance;

    private final Mac hmac;
    final Preferences projectTrust;
    final byte[] salt;

    ProjectTrust(Preferences prefs) {
        byte[] buf = prefs.getByteArray(KEY_SALT, null);
        if (buf == null) {
            buf = new byte[16];
            new Random().nextBytes(buf);
            prefs.putByteArray(KEY_SALT, buf);
        }
        salt = buf;
        projectTrust = prefs.node(NODE_PROJECT);
        try {
            hmac = Mac.getInstance(HMAC_SHA256);
            Key key = new SecretKeySpec(salt, HMAC_SHA256);
            hmac.init(key);
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            // Shall not happen on JVM-s fulfilling the specs.
            // This throw line is not expected to be called, but let hmac be final
            throw new IllegalArgumentException("JDK has issues with HMAC_SHA256: " + ex.getMessage());
        }
    }
    
    public boolean isTrusted(Project project) {
        String pathId = getPathId(project);
        String projectId = projectTrust.get(pathId, null);
        if (projectId == null) {
            return false;
        }
        boolean ret = false;
        Path trustFile = getProjectTrustFile(project);
        try {
            List<String> trust = Files.readAllLines(trustFile);
            String hash = hmacSha256(fromHex(projectId));
            ret = trust.size() == 1 && trust.iterator().next().equals(hash);
        } catch (IOException ex) {
        }
        return ret;        
    }

    public void trustProject(Project project) {
        String pathId = getPathId(project);
        Path trustFile = getProjectTrustFile(project);
        byte[] rnd = new byte[16];
        new Random().nextBytes(rnd);
        String projectId = toHex(rnd);
        projectTrust.put(pathId, projectId);
        try {
            Files.createDirectories(trustFile.getParent());
            Files.write(trustFile, Collections.singletonList(hmacSha256(rnd)));
        } catch (IOException ex) {}
    }

    public void distrustProject(Project project) {
        String pathId = getPathId(project);
        projectTrust.remove(pathId);
        Path trustFile = getProjectTrustFile(project);
        if (trustFile != null) {
            try {
                Files.delete(trustFile);
            } catch (IOException ex) {
            }
        }

    }

    public static ProjectTrust getDefault() {
        if (instance == null) {
            Preferences p = NbPreferences.forModule(ProjectTrust.class).node(NODE_TRUST);
            instance = new ProjectTrust(p);
        }
        return instance;
    }

    protected Path getProjectTrustPath(Project project) {
        if (project instanceof NbGradleProjectImpl) {
            return ((NbGradleProjectImpl) project).getGradleFiles().getRootDir().toPath();
        }
        throw new IllegalArgumentException("Project shall be an NbGradleProjectImpl instance."); //NOI18N
    }

    protected Path getProjectTrustFilePath(Project project) {
        if (project instanceof NbGradleProjectImpl) {
            Path root = getProjectTrustPath(project);
            return root == null ? null : root.resolve(".gradle/nb-cache/trust"); //NOI18N
        }
        throw new IllegalArgumentException("Project shall be an NbGradleProjectImpl instance."); //NOI18N
    }

    Path getProjectTrustFile(Project project) {
        String pathId = getPathId(project);
        Path trustFilePath = getProjectTrustFilePath(project);
        return trustFilePath.resolve(pathId);
    }

    String getPathId(Project project) {
        Path path = getProjectTrustPath(project);
        path = path.normalize().toAbsolutePath();
        return sha256(path.toString().getBytes(StandardCharsets.UTF_8));
    }

    String hmacSha256(byte[] buf) {
        byte[] out;
        synchronized (hmac) {
            out = hmac.doFinal(buf);
        }
        return toHex(out);
    }

    String sha256(byte[] buf) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256"); //NOI18N
            digest.update(salt);
            digest.update(buf);
            return toHex(digest.digest());
        } catch (NoSuchAlgorithmException nae) {
        }
        return null;
    }

    static byte[] fromHex(String hex) {
        int len = hex.length();
        byte[] ret = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            ret[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));

        }
        return ret;
    }

    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.UTF_8); //NOI18N
    static String toHex(byte[] b) {
        byte[] hexChars = new byte[b.length * 2];
        for (int j = 0; j < b.length; j++) {
            int v = b[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }

}
