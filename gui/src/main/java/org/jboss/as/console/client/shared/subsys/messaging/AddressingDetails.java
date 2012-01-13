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
import org.jboss.as.console.client.shared.subsys.messaging.model.AddressingPattern;
import org.jboss.as.console.client.shared.subsys.messaging.model.MessagingProvider;
import org.jboss.as.console.client.shared.subsys.messaging.model.SecurityPattern;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.ballroom.client.widgets.tools.ToolButton;
import org.jboss.dmr.client.ModelNode;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 5/10/11
 */
public class AddressingDetails {

    private MessagingPresenter presenter;
    private Form<AddressingPattern> form;
    private MessagingProvider providerEntity;
    private DefaultCellTable<SecurityPattern> addrTable;

    public AddressingDetails(MessagingPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();

        addrTable = new DefaultCellTable<SecurityPattern>(10);
        addrTable.getElement().setAttribute("style", "margin-top:10px");

        Column<AddressingPattern, String> patternColumn = new Column<AddressingPattern, String>(new TextCell()) {
            @Override
            public String getValue(AddressingPattern object) {
                return object.getPattern();
            }
        };

        addrTable.addColumn(patternColumn, "Pattern");

        // ---

        form = new Form<AddressingPattern>(AddressingPattern.class);
        form.setNumColumns(2);
        form.bind(addrTable);

        TextBoxItem dlQ = new TextBoxItem("deadLetterQueue", "Dead Letter Address");
        TextBoxItem expQ= new TextBoxItem("expiryQueue", "Expiry Address");
        NumberBoxItem redelivery = new NumberBoxItem("redeliveryDelay", "Redelivery Delay");
        NumberBoxItem maxDelivery = new NumberBoxItem("maxDelivery", "Max Delivery Attepmts");

        form.setFields(dlQ, expQ, redelivery, maxDelivery);

        FormHelpPanel helpPanel = new FormHelpPanel(new FormHelpPanel.AddressCallback(){
            @Override
            public ModelNode getAddress() {
                ModelNode address = Baseadress.get();
                address.add("subsystem", "messaging");
                address.add("hornetq-server", "*");
                address.add("address-setting", "*");
                return address;
            }
        }, form);

        FormToolStrip<AddressingPattern> toolStrip = new FormToolStrip<AddressingPattern>(
                form,
                new FormToolStrip.FormCallback<AddressingPattern>() {
                    @Override
                    public void onSave(Map<String, Object> changeset) {
                        presenter.onSaveAddressDetails(form.getEditedEntity(), changeset);
                    }

                    @Override
                    public void onDelete(AddressingPattern entity) {
                        presenter.onDeleteAddressDetails(entity);
                    }
                }
        );

        ToolButton addBtn = new ToolButton(Console.CONSTANTS.common_label_add(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.launchNewAddrDialogue();
            }
        });
        addBtn.ensureDebugId(Console.CONSTANTS.debug_label_add_addressingDetails());
        toolStrip.addToolButtonRight(addBtn);

        layout.add(toolStrip.asWidget());
        layout.add(addrTable);

        layout.add(helpPanel.asWidget());
        layout.add(form.asWidget());

        return layout;
    }

    void setProvider(MessagingProvider provider)
    {
        this.providerEntity = provider;

    }

    public void setAddressingConfig(List<AddressingPattern> addrPatterns) {
        addrTable.setRowCount(addrPatterns.size(),true);
        addrTable.setRowData(0, addrPatterns);
        if(!addrPatterns.isEmpty())
            addrTable.getSelectionModel().setSelected(addrPatterns.get(0), true);

        form.setEnabled(false);
    }

}
