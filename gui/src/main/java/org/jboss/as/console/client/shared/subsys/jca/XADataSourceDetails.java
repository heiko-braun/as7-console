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

package org.jboss.as.console.client.shared.subsys.jca;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.jca.model.DataSource;
import org.jboss.as.console.client.shared.subsys.jca.model.XADataSource;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.as.console.client.widgets.forms.items.JndiNameItem;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.EditListener;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.StatusItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.ballroom.client.widgets.window.Feedback;
import org.jboss.dmr.client.ModelNode;

import java.util.Map;

/**
 * @author Heiko Braun
 * @date 5/4/11
 */
public class XADataSourceDetails {

    private Form<XADataSource> form;
    private DataSourcePresenter presenter;
    private ToolButton disableBtn;

    public XADataSourceDetails(DataSourcePresenter presenter) {
        this.presenter = presenter;
        form = new Form(XADataSource.class);
        form.setNumColumns(2);
    }

    public Widget asWidget() {

        form.addEditListener(new EditListener<DataSource>() {
            @Override
            public void editingBean(DataSource bean) {
                String nextState = bean.isEnabled() ? Console.CONSTANTS.common_label_disable():Console.CONSTANTS.common_label_enable();
                disableBtn.setText(nextState);
            }
        });

        ClickHandler disableHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

                final boolean doEnable = !form.getEditedEntity().isEnabled();
                Feedback.confirm(Console.MESSAGES.modify("XA datasource"), Console.MESSAGES.modifyConfirm("XA datasource " + form.getEditedEntity().getName()),
                        new Feedback.ConfirmationHandler() {
                            @Override
                            public void onConfirmation(boolean isConfirmed) {
                                if (isConfirmed) {
                                    presenter.onDisableXA(form.getEditedEntity(), doEnable);
                                }
                            }
                        });
            }
        };

        disableBtn = new ToolButton(Console.CONSTANTS.common_label_enOrDisable());
        disableBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_enOrDisable_xADataSourceDetails());
        disableBtn.addClickHandler(disableHandler);

        ToolButton verifyBtn = new ToolButton(Console.CONSTANTS.subsys_jca_dataSource_verify(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                presenter.verifyConnection(form.getEditedEntity().getName(), true);
            }
        });
        verifyBtn.ensureDebugId(Console.DEBUG_CONSTANTS.debug_label_verify_xADataSourceDetails());

        FormToolStrip<XADataSource> toolStrip = new FormToolStrip<XADataSource>(
                form,
                new FormToolStrip.FormCallback<XADataSource>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSaveXADetails(form.getEditedEntity().getName(), form.getChangedValues());
                    }

                    @Override
                    public void onDelete(XADataSource entity) {

                    }
                });

        toolStrip.providesDeleteOp(false);
        toolStrip.addToolButtonRight(disableBtn);

        if(Console.MODULES.getBootstrapContext().isStandalone())
            toolStrip.addToolButtonRight(verifyBtn);

        VerticalPanel panel = new VerticalPanel();
        panel.add(toolStrip.asWidget());

        final TextItem nameItem = new TextItem("name", "Name");
        TextBoxItem jndiItem = new JndiNameItem("jndiName", "JNDI");
        StatusItem enabledFlagItem = new StatusItem("enabled", "Is enabled?");
        TextItem driverItem = new TextItem("driverName", "Driver");

        CheckBoxItem shareStatements = new CheckBoxItem("sharePreparedStatements", "Share Prepared Statements");
        NumberBoxItem statementCacheSize = new NumberBoxItem("prepareStatementCacheSize", "Statement Cache Size");

        form.setFields(nameItem, jndiItem, enabledFlagItem, driverItem, shareStatements, statementCacheSize);

        form.setEnabled(false); // currently not editable


        final FormHelpPanel helpPanel = new FormHelpPanel(
                new FormHelpPanel.AddressCallback() {
                    @Override
                    public ModelNode getAddress() {
                        ModelNode address = Baseadress.get();
                        address.add("subsystem", "datasources");
                        address.add("xa-data-source", "*");
                        return address;
                    }
                }, form
        );
        panel.add(helpPanel.asWidget());

        Widget formWidget = form.asWidget();
        panel.add(formWidget);

        return panel;
    }


    public void setEnabled(boolean b) {
        form.setEnabled(b);
    }

    public void setSelectedRecord(XADataSource dataSource) {
        form.edit(dataSource);
    }

    public XADataSource getCurrentSelection() {
        return form.getEditedEntity();
    }
}
