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
package org.jboss.as.console.client.shared.viewframework;


import static org.jboss.dmr.client.ModelDescriptionConstants.ADDRESS;
import static org.jboss.dmr.client.ModelDescriptionConstants.CHILD_TYPE;
import static org.jboss.dmr.client.ModelDescriptionConstants.INCLUDE_RUNTIME;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP;
import static org.jboss.dmr.client.ModelDescriptionConstants.RECURSIVE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import junit.framework.Assert;

import org.jboss.as.console.client.shared.dispatch.Action;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.DispatchRequest;
import org.jboss.as.console.client.shared.dispatch.Result;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.widgets.forms.AddressBinding;
import org.jboss.as.console.client.widgets.forms.BeanMetaData;
import org.jboss.as.console.client.widgets.forms.EntityFactory;
import org.jboss.as.console.client.widgets.forms.Getter;
import org.jboss.as.console.client.widgets.forms.Mutator;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.as.console.client.widgets.forms.PropertyMetaData;
import org.jboss.as.console.client.widgets.forms.Setter;
import org.jboss.ballroom.client.widgets.forms.EditListener;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;
import org.jboss.ballroom.client.widgets.forms.FormValidation;
import org.jboss.dmr.client.ModelDescriptionConstants;
import org.jboss.dmr.client.ModelNode;
import org.junit.Test;

/**
 * @author David Bosschaert
 */
public class EntityToDmrBridgeImplTest extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "org.jboss.as.console.App";
    }

    @Test
    public void testLoadEntitiesIncludeRuntime() {
        TestDispatchAsync testDispatcher = new TestDispatchAsync();

        List<PropertyBinding> properties = new ArrayList<PropertyBinding>();
        AddressBinding address = new AddressBinding();
        address.add("resource", "foo");
        BeanMetaData bm = new BeanMetaData(MyNamedEntity.class, address, properties);
        TestPropertyMetaData pm = new TestPropertyMetaData();
        pm.beanMetaData.put(MyNamedEntity.class, bm);

        EntityToDmrBridgeImpl<MyNamedEntity> bridge = new EntityToDmrBridgeImpl<MyNamedEntity>(pm, MyNamedEntity.class, null, testDispatcher);
        ModelNode baseAddress = new ModelNode();
        baseAddress.set("someroot");
        bridge.loadEntities("foobar", baseAddress);

        DMRAction action = (DMRAction) testDispatcher.lastExecuteAction;
        ModelNode op = action.getOperation();

        Set<String> expectedSet = new HashSet<String>(Arrays.asList(ADDRESS, CHILD_TYPE, OP, INCLUDE_RUNTIME));
        Assert.assertEquals(expectedSet, op.keys());
        Assert.assertEquals("someroot", op.get(ADDRESS).asString());
        Assert.assertEquals("resource", op.get(CHILD_TYPE).asString());
        Assert.assertEquals("read-children-resources", op.get(OP).asString());
        Assert.assertEquals(true, op.get(INCLUDE_RUNTIME).asBoolean());
    }

    @Test
    public void testLoadEntitiesRecursive() {
        TestDispatchAsync testDispatcher = new TestDispatchAsync();

        List<PropertyBinding> properties = new ArrayList<PropertyBinding>();
        // Note the '/' in the detyped name causes the recursion on the operation
        PropertyBinding binding = new PropertyBinding("x", "x/y", "JavaType", false, false);
        properties.add(binding);

        AddressBinding address = new AddressBinding();
        address.add("resource", "foo");
        BeanMetaData bm = new BeanMetaData(MyNamedEntity.class, address, properties);
        TestPropertyMetaData pm = new TestPropertyMetaData();
        pm.beanMetaData.put(MyNamedEntity.class, bm);

        EntityToDmrBridgeImpl<MyNamedEntity> bridge = new EntityToDmrBridgeImpl<MyNamedEntity>(pm, MyNamedEntity.class, null, testDispatcher);
        ModelNode baseAddress = new ModelNode();
        baseAddress.set("someroot");
        bridge.loadEntities("foobar", baseAddress);

        DMRAction action = (DMRAction) testDispatcher.lastExecuteAction;
        ModelNode op = action.getOperation();

        Set<String> expectedSet = new HashSet<String>(Arrays.asList(ADDRESS, CHILD_TYPE, OP, RECURSIVE));
        Assert.assertEquals(expectedSet, op.keys());
        Assert.assertEquals("someroot", op.get(ADDRESS).asString());
        Assert.assertEquals("resource", op.get(CHILD_TYPE).asString());
        Assert.assertEquals("read-children-resources", op.get(OP).asString());
        Assert.assertEquals(true, op.get(RECURSIVE).asBoolean());
    }

    @Test
    public void testOnAdd() {
        TestPropertyMetaData pm = new TestPropertyMetaData();
        List<PropertyBinding> properties = new ArrayList<PropertyBinding>();
        properties.add(new PropertyBinding("name", "name", String.class.getName(), null, pm, true, false, "", null, true, "TEXT", "TEXT", "", null, 1));
        properties.add(new PropertyBinding("myvalue", "myvalue", String.class.getName(), null, pm, false, false, "mydefault", null, true, "TEXT", "TEXT", "", null, 10));
        AddressBinding address = new AddressBinding();
        address.add("{resource=blah}", "foo");
        BeanMetaData bm = new BeanMetaData(MyNamedEntity.class, address, properties);
        pm.beanMetaData.put(MyNamedEntity.class, bm);

        final List<Object> result = new ArrayList<Object>();
        EntityToDmrBridgeImpl<MyNamedEntity> bridge = new EntityToDmrBridgeImpl<MyNamedEntity>(pm, MyNamedEntity.class, null, null) {
            @Override
            protected void execute(ModelNode operation, String nameEditedOrAdded, String successMessage) {
                result.add(operation);
                result.add(nameEditedOrAdded);
            }
        };

        MyNamedEntity updatedEntity = new MyNamedEntityImpl();
        MyFormAdapter form = new MyFormAdapter(updatedEntity);
        form.changedValues.put("name", "myval");

        Assert.assertEquals("Precondition", 0, result.size());
        bridge.onAdd(form);
        Assert.assertEquals(2, result.size());
        ModelNode operation = (ModelNode) result.get(0);
        Assert.assertEquals("myval", operation.get("name").asString());
        Assert.assertEquals("mydefault", operation.get("myvalue").asString());
        Assert.assertEquals(ModelDescriptionConstants.ADD, operation.get(ModelDescriptionConstants.OP).asString());
        Assert.assertEquals("myentity", result.get(1));
    }

    private static class TestDispatchAsync implements DispatchAsync {
        private Object lastExecuteAction;

        @Override
        public <A extends Action<R>, R extends Result> DispatchRequest execute(A action, AsyncCallback<R> callback) {
            lastExecuteAction = action;
            return null;
        }

        @Override
        public <A extends Action<R>, R extends Result> DispatchRequest undo(A action, R result, AsyncCallback<Void> callback) {
            return null;
        }
    }

    private static class TestPropertyMetaData implements PropertyMetaData {
        private Map<Class<?>, BeanMetaData> beanMetaData = new HashMap<Class<?>, BeanMetaData>();

        @Override
        public List<PropertyBinding> getBindingsForType(Class<?> type) {
            return null;
        }

        @Override
        public BeanMetaData getBeanMetaData(Class<?> type) {
            return beanMetaData.get(type);
        }

        @Override
        public Mutator<?> getMutator(Class<?> type) {
            Mutator<MyNamedEntity> mutator = new Mutator<MyNamedEntity>();
            mutator.register("myvalue", new Getter<MyNamedEntity>() {
                @Override
                public Object invoke(MyNamedEntity entity) {
                    return entity.getMyvalue();
                }
            });
            mutator.register("myvalue", new Setter<MyNamedEntity>() {
                @Override
                public void invoke(MyNamedEntity entity, Object value) {
                    entity.setMyvalue((String) value);
                }
            });

            return mutator;
        }

        @Override
        public <T> EntityFactory<T> getFactory(Class<T> type) {
            return null;
        }
    }

    private interface MyNamedEntity extends NamedEntity {
        String getMyvalue();
        void setMyvalue(String value);
    }

    private static class MyNamedEntityImpl implements MyNamedEntity {
        private String value;

        @Override
        public String getName() {
            return "myentity";
        }

        @Override
        public void setName(String name) { }

        @Override
        public String getMyvalue() {
            return value;
        }

        @Override
        public void setMyvalue(String value) {
            this.value = value;
        }
    }

    private static class MyFormAdapter implements FormAdapter<MyNamedEntity> {
        private final MyNamedEntity updatedEntity;
        private Map<String, Object> changedValues = new HashMap<String, Object>();

        private MyFormAdapter(MyNamedEntity updatedEntity) {
            this.updatedEntity = updatedEntity;
        }

        @Override
        public Widget asWidget() {
            return null;
        }

        @Override
        public void bind(CellTable<MyNamedEntity> instanceTable) {
        }

        @Override
        public void cancel() {
        }

        @Override
        public void edit(MyNamedEntity bean) {
        }

        @Override
        public void addEditListener(EditListener listener) {
        }

        @Override
        public void removeEditListener(EditListener listener) {
        }

        @Override
        public Map<String, Object> getChangedValues() {
            return changedValues;
        }

        @Override
        public Class<?> getConversionType() {
            return null;
        }

        @Override
        public MyNamedEntity getEditedEntity() {
            return null;
        }

        @Override
        public MyNamedEntity getUpdatedEntity() {
            return updatedEntity;
        }

        @Override
        public List<String> getFormItemNames() {
            return null;
        }

        @Override
        public void setEnabled(boolean b) {
        }

        @Override
        public FormValidation validate() {
            return null;
        }
    }
}
