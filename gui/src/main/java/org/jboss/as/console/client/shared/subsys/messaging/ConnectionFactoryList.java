package org.jboss.as.console.client.shared.subsys.messaging;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import org.jboss.as.console.client.shared.help.FormHelpPanel;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.shared.subsys.messaging.forms.DefaultCFForm;
import org.jboss.as.console.client.shared.subsys.messaging.model.ConnectionFactory;
import org.jboss.as.console.client.shared.viewframework.builder.FormLayout;
import org.jboss.as.console.client.shared.viewframework.builder.MultipleToOneLayout;
import org.jboss.as.console.client.widgets.forms.BlankItem;
import org.jboss.as.console.client.widgets.forms.FormToolStrip;
import org.jboss.ballroom.client.widgets.ContentHeaderLabel;
import org.jboss.ballroom.client.widgets.forms.CheckBoxItem;
import org.jboss.ballroom.client.widgets.forms.Form;
import org.jboss.ballroom.client.widgets.forms.NumberBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextBoxItem;
import org.jboss.ballroom.client.widgets.forms.TextItem;
import org.jboss.ballroom.client.widgets.tables.DefaultCellTable;
import org.jboss.dmr.client.ModelNode;

import java.util.List;
import java.util.Map;

/**
 * @author Heiko Braun
 * @date 4/2/12
 */
public class ConnectionFactoryList {


    private ContentHeaderLabel serverName;
    private DefaultCellTable<ConnectionFactory> factoryTable;
    private ListDataProvider<ConnectionFactory> factoryProvider;
    private MsgDestinationsPresenter presenter;

    public ConnectionFactoryList(MsgDestinationsPresenter presenter) {
        this.presenter = presenter;
    }

    Widget asWidget() {


        serverName = new ContentHeaderLabel();

        factoryTable = new DefaultCellTable<ConnectionFactory>(10);
        factoryProvider = new ListDataProvider<ConnectionFactory>();
        factoryProvider.addDataDisplay(factoryTable);

        Column<ConnectionFactory, String> nameColumn = new Column<ConnectionFactory, String>(new TextCell()) {
            @Override
            public String getValue(ConnectionFactory object) {
                return object.getName();
            }
        };

        Column<ConnectionFactory, String> jndiColumn = new Column<ConnectionFactory, String>(new TextCell()) {
            @Override
            public String getValue(ConnectionFactory object) {
                return object.getJndiName();
            }
        };

        factoryTable.addColumn(nameColumn, "Name");
        factoryTable.addColumn(jndiColumn, "JNDI");


        // defaultAttributes
        DefaultCFForm defaultAttributes = new DefaultCFForm(new FormToolStrip.FormCallback<ConnectionFactory>() {
            @Override
            public void onSave(Map<String, Object> changeset) {

            }

            @Override
            public void onDelete(ConnectionFactory entity) {

            }
        });

        MultipleToOneLayout layout = new MultipleToOneLayout()
                .setPlain(true)
                .setHeadlineWidget(serverName)
                .setDescription("Connection factories for applications. Used to connect to the server using the JMS API.")
                .setMaster("Connection Factories", factoryTable)
                .addDetail("Attributes", defaultAttributes.asWidget())
                .addDetail("Connections", new HTML())
                .addDetail("Pool", new HTML())
                .addDetail("HA", new HTML());

        defaultAttributes.getForm().setEnabled(false);
        defaultAttributes.getForm().bind(factoryTable);

        return layout.build();
    }

    public void setFactories(List<ConnectionFactory> factories) {
        factoryProvider.setList(factories);
        serverName.setText("Connection Factories: Provider "+presenter.getCurrentServer());

        factoryTable.selectDefaultEntity();
    }

}
