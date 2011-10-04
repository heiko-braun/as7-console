/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.jboss.as.console.client.widgets.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Aggregator for PropertyBinding instances.  Allows searching and grouping.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2011 Red Hat Inc.
 */
public class FormMetaData {
    
    private List<PropertyBinding> baseAttributes = new ArrayList<PropertyBinding>();
    private Map<String, List<PropertyBinding>> groupedAttributes = new LinkedHashMap<String, List<PropertyBinding>>();
    
    FormMetaData(BeanMetaData beanMetaData) {
        for (PropertyBinding binding : beanMetaData.getProperties()) {
           String subgroup = binding.getSubgroup();
           if (subgroup.equals("")) {
               baseAttributes.add(binding);
           } else {
               List<PropertyBinding> subgroupData = groupedAttributes.get(subgroup);
               if (subgroupData == null) {
                   subgroupData = new ArrayList<PropertyBinding>();
                   groupedAttributes.put(subgroup, subgroupData);
               }
               subgroupData.add(binding);
           }
        }
    }
    
    public List<PropertyBinding> getBaseAttributes() {
        return Collections.unmodifiableList(this.baseAttributes);
    }
    
    public void setGroupedAttributes(String groupName, List<PropertyBinding> attributes) {
        groupedAttributes.put(groupName, attributes);
    }
    
    public List<PropertyBinding> getGroupedAttribtes(String groupName) {
        return groupedAttributes.get(groupName);
    }
    
    /**
     * Returns all group names.  Calling iterator() on the returned Set
     * will give you an iterator that maintains the names in the order they were added.
     * 
     * @return The group names.
     */
    public Set<String> getGroupNames() {
        return groupedAttributes.keySet();
    }
    
    /**
     * Find a PropertyBinding with the given bean (Java Bean Name) property.
     * @param beanPropName The name of the bean property.
     * @return The PropertyBinding
     * @throws IllegalArgumentException if the PropertyBinding is not found.
     */
    public PropertyBinding findAttribute(String beanPropName) {
        for (PropertyBinding attrib : baseAttributes) {
            if (attrib.getJavaName().equals(beanPropName)) return attrib;
        }
        
        for (Map.Entry<String, List<PropertyBinding>> entry : groupedAttributes.entrySet()) {
            for (PropertyBinding attrib : entry.getValue()) {
                if (attrib.getJavaName().equals(beanPropName)) return attrib;
            }
        }
        
        throw new IllegalArgumentException("Unknown Attribute with beanPropName name " + beanPropName);
    }
    
}
