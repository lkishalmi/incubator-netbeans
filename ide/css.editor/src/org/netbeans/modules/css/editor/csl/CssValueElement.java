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

package org.netbeans.modules.css.editor.csl;

import org.netbeans.modules.css.lib.api.properties.GrammarElement;
import org.netbeans.modules.css.lib.api.properties.PropertyDefinition;

/**
 * Represents CSS property value. Just one item in case of multivalues e.g. background: red 1px; 
 *
 * @author mfukala@netbeans.org
 */
public class CssValueElement extends CssPropertyElement {
    
    private GrammarElement value; 
    
    public CssValueElement(PropertyDefinition property, GrammarElement value) {
        super(property);
        this.value = value;
    }
    
    @Override
    public String getName() {
        return value.toString();
    }

}
