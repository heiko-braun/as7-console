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

package org.jboss.as.console.client.shared.subsys.messaging;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.messaging.model.SecurityPattern;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.DisclosureGroupRenderer;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.dmr.client.ModelNode;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 5/10/11
 */
public class SecurityDetails {

    private MessagingPresenter presenter;
    private Form<SecurityPattern> form;

    private DefaultCellTable<SecurityPattern> secTable;

    public SecurityDetails(MessagingPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();

        secTable = new DefaultCellTable<SecurityPattern>(10);
        secTable.getElement().setAttribute("style", "margin-top:10px");

        Column<SecurityPattern, String> roleColumn = new Column<SecurityPattern, String>(new TextCell()) {
            @Override
            public String getValue(SecurityPattern object) {
                return object.getRole();
            }
        };


        Column<SecurityPattern, String> patternColumn = new Column<SecurityPattern, String>(new TextCell()) {
            @Override
            public String getValue(SecurityPattern object) {
                return object.getPattern();
            }
        };

        secTable.addColumn(patternColumn, "Pattern");
        secTable.addColumn(roleColumn, "Role");

        // ---

        form = new Form<SecurityPattern>(SecurityPattern.class);
        form.setNumColumns(2);
        form.bind(secTable);

        CheckBoxItem send = new CheckBoxItem("send", "Send?");
        CheckBoxItem consume = new CheckBoxItem("consume", "Consume?");
        CheckBoxItem manage= new CheckBoxItem("manage", "Manage?");

        CheckBoxItem createDQ = new CheckBoxItem("createDurableQueue", "CreateDurable?");
        CheckBoxItem deleteDQ = new CheckBoxItem("deleteDurableQueue", "DeleteDurable?");

        CheckBoxItem createNDQ = new CheckBoxItem("createNonDurableQueue", "CreateNonDurable?");
        CheckBoxItem deleteNDQ = new CheckBoxItem("deleteNonDurableQueue", "DeleteNonDurable?");


        form.setFields(send, consume, manage);
        form.setFieldsInGroup(Console.CONSTANTS.common_label_advanced(), new DisclosureGroupRenderer(), createDQ, deleteDQ, createNDQ, deleteNDQ);


        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback(){
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "messaging");
                address.add("hornetq-server", "*");
                address.add("security-setting", "*");
                address.add("role", "*");
                return address;
            }
        }, form);


        FormToolStrip<SecurityPattern> toolStrip = new FormToolStrip<SecurityPattern>(
                form,
                new FormToolStrip.FormCallback<SecurityPattern>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSaveSecDetails(form.getEditedEntity(), changeset);
                    }

                    @Override
                    public void onDelete(SecurityPattern entity) {
                        presenter.onDeleteSecDetails(entity);
                    }
                }
        );

        ToolButton addBtn = new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewSecDialogue();
            }
        });
        addBtn.ensureDebugId(Console.CONSTANTS.debug_label_add_securityDetails());
        toolStrip.addToolButtonRight(addBtn);

        // asembly
        layout.add(toolStrip.asWidget());
        layout.add(secTable);
        layout.add(helpPanel.asWidget());
        layout.add(form.asWidget());

        return layout;
    }

    public void setSecurityConfig(List<SecurityPattern> patterns) {

        secTable.setRowCount(patterns.size(), true);
        secTable.setRowData(0, patterns);
        if(!patterns.isEmpty())
            secTable.getSelectionModel().setSelected(patterns.get(0), true);

        form.setEnabled(false);
    }
}
