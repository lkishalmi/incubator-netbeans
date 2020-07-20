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
package org.netbeans.modules.cpplite.project.ui;

import java.io.File;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import org.netbeans.modules.cpplite.project.CPPLiteProject;
import org.netbeans.spi.project.ui.support.ProjectCustomizer;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.Category;
import org.netbeans.spi.project.ui.support.ProjectCustomizer.CompositeCategoryProvider;
import org.openide.util.Lookup;

/**
 *
 * @author lahvac
 */
public class Editor extends javax.swing.JPanel {

    /**
     * Creates new form Editor
     */
    public Editor() {
        initComponents();
    }

    public void load(String compileCommands) {
        this.compileCommands.setText(compileCommands);
    }
    
    public String save() {
        return this.compileCommands.getText();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        compileCommands = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(Editor.class, "Editor.jLabel1.text")); // NOI18N

        compileCommands.setText(org.openide.util.NbBundle.getMessage(Editor.class, "Editor.compileCommands.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(Editor.class, "Editor.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(compileCommands, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jButton1)
                .addComponent(compileCommands, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel1))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setSelectedFile(new File(compileCommands.getText()));
        if (fc.showDialog(this, "Select") == JFileChooser.APPROVE_OPTION) {
            compileCommands.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField compileCommands;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables

    @CompositeCategoryProvider.Registration(projectType=CPPLiteProject.PROJECT_KEY, position=100)
    public static CompositeCategoryProvider createCategoryProvider() {
        return new CompositeCategoryProvider() {
            @Override
            public Category createCategory(Lookup context) {
                return Category.create("editor", "Editor", null);
            }
            @Override
            public JComponent createComponent(ProjectCustomizer.Category category, Lookup context) {
                CPPLiteProject prj = context.lookup(CPPLiteProject.class);
                Editor panel = new Editor();
                panel.load(prj.getCompileCommands());
                String[] compileCommands = new String[1];
                category.setOkButtonListener(evt -> {
                    compileCommands[0] = panel.save();
                });
                category.setStoreListener(evt -> prj.setCompileCommands(compileCommands[0]));
                return panel;
            }
        };
    }
}
