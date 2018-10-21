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

package org.netbeans.modules.gradle.customizer;

import org.netbeans.modules.gradle.api.GradleBaseProject;
import javax.swing.DefaultListModel;
import org.netbeans.api.project.Project;
import org.openide.util.NbBundle;

/**
 *
 * @author Laszlo Kishalmi
 */
public class ProjectInfoPanel extends javax.swing.JPanel {

    /**
     * Creates new form ProjectInfoPanel
     */
    @NbBundle.Messages("TXT_rootProject=This is a root project.")
    public ProjectInfoPanel(Project project) {
        initComponents();
        GradleBaseProject gp = GradleBaseProject.get(project);
        tfProjectFolder.setText(gp.getProjectDir().getAbsolutePath());
        tfName.setText(gp.getName());
        tfDescription.setText(gp.getDescription());
        tfVersion.setText(gp.getVersion());
        tfGroup.setText(gp.getGroup());
        tfParentProject.setText(gp.isRoot() ? Bundle.TXT_rootProject() : gp.getParentName());
        tfParentProject.setEnabled(!gp.isRoot());
        DefaultListModel<String> includedBuildModel = new DefaultListModel<>();
        for (String includedBuild : gp.getIncludedBuilds().keySet()) {
            includedBuildModel.addElement(includedBuild);
        }
        lsIncludedBuilds.setModel(includedBuildModel);
        DefaultListModel<String> pluginModel = new DefaultListModel<>();
        for (String plugin : gp.getPlugins()) {
            pluginModel.addElement(plugin);
        }
        lsPlugins.setModel(pluginModel);
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbProjectFolder = new javax.swing.JLabel();
        tfProjectFolder = new javax.swing.JTextField();
        lbName = new javax.swing.JLabel();
        tfName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        tfDescription = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tfGroup = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        tfVersion = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lsPlugins = new javax.swing.JList<>();
        tfParentProject = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lsIncludedBuilds = new javax.swing.JList<>();

        lbProjectFolder.setLabelFor(tfProjectFolder);
        org.openide.awt.Mnemonics.setLocalizedText(lbProjectFolder, org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lbProjectFolder.text")); // NOI18N

        tfProjectFolder.setEditable(false);
        tfProjectFolder.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.tfProjectFolder.text")); // NOI18N

        lbName.setLabelFor(tfName);
        org.openide.awt.Mnemonics.setLocalizedText(lbName, org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.lbName.text")); // NOI18N

        tfName.setEditable(false);
        tfName.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.tfName.text")); // NOI18N

        jLabel1.setLabelFor(tfDescription);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.jLabel1.text")); // NOI18N

        tfDescription.setEditable(false);
        tfDescription.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.tfDescription.text")); // NOI18N

        jLabel2.setLabelFor(tfGroup);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.jLabel2.text")); // NOI18N

        tfGroup.setEditable(false);
        tfGroup.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.tfGroup.text")); // NOI18N

        jLabel3.setLabelFor(tfVersion);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.jLabel3.text")); // NOI18N

        tfVersion.setEditable(false);
        tfVersion.setText(org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.tfVersion.text")); // NOI18N

        jLabel4.setLabelFor(tfParentProject);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.jLabel4.text")); // NOI18N

        jLabel5.setLabelFor(lsPlugins);
        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.jLabel5.text")); // NOI18N

        lsPlugins.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(lsPlugins);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(ProjectInfoPanel.class, "ProjectInfoPanel.jLabel6.text")); // NOI18N

        jScrollPane2.setViewportView(lsIncludedBuilds);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lbName, javax.swing.GroupLayout.PREFERRED_SIZE, 91, Short.MAX_VALUE))
                            .addComponent(lbProjectFolder))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfDescription)
                            .addComponent(tfGroup)
                            .addComponent(tfVersion)
                            .addComponent(tfProjectFolder)
                            .addComponent(tfName)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfParentProject))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbProjectFolder)
                    .addComponent(tfProjectFolder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbName)
                    .addComponent(tfName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tfDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tfGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tfVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfParentProject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE))))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbName;
    private javax.swing.JLabel lbProjectFolder;
    private javax.swing.JList<String> lsIncludedBuilds;
    private javax.swing.JList<String> lsPlugins;
    private javax.swing.JTextField tfDescription;
    private javax.swing.JTextField tfGroup;
    private javax.swing.JTextField tfName;
    private javax.swing.JTextField tfParentProject;
    private javax.swing.JTextField tfProjectFolder;
    private javax.swing.JTextField tfVersion;
    // End of variables declaration//GEN-END:variables
}
