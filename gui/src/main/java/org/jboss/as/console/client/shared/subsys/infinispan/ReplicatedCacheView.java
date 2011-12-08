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

package org.jboss.as.console.client.shared.subsys.infinispan;

import javax.inject.Inject;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.subsys.deploymentscanner.model.DeploymentScanner;
import org.jboss.as.console.client.shared.subsys.infinispan.model.ReplicatedCache;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.FormAdapter;

/**
 * Main view class for Infinispan Replicated Cache.
 * 
 * @author Stan Silvert
 */
public class ReplicatedCacheView extends AbstractCacheView<ReplicatedCache> implements ReplicatedCachePresenter.MyView {

    @Inject
    public ReplicatedCacheView(ApplicationMetaData propertyMetaData, DispatchAsync dispatcher) {
        super(ReplicatedCache.class, propertyMetaData, dispatcher);
    }
    
    @Override
    protected String getEntityDisplayName() {
        return Console.CONSTANTS.subsys_infinispan_replicatedCache();
    }

    @Override
    protected FormAdapter<ReplicatedCache> makeAddEntityForm() {
        Form<ReplicatedCache> form = new Form(DeploymentScanner.class);
        form.setNumColumns(1);
        form.setFields(getFormMetaData().findAttribute("name").getFormItemForAdd());
        return form;
    }

}
