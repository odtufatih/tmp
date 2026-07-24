package tr.com.aktifbank.servicetester.ui;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;

import tr.com.aktifbank.servicetester.data.IDataContainer;

public class OutputMapLayout extends MapLayout {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1208642753526725626L;
    
    private final MapLayout transferLayout;

	public OutputMapLayout(MapLayout transferLayout) {
		super();
		
		table.addContainerProperty("transferButtonColumn", Button.class, null, "", null, Align.CENTER);
		table.setColumnExpandRatio("transferButtonColumn", 1.0f);
		table.setVisibleColumns(new Object[] {"transferButtonColumn", "buttonColumn", "keyColumn", "valueColumn"});
		
		this.transferLayout = transferLayout;
	}
    
    protected void addItem(String key, ValueComponent valueComponent) {
        final Button button = new Button("-");
        final Button transferButton = new Button("<");
        final TextField keyField = new TextField();
        
        keyField.setValue(key);
        keyField.setNullRepresentation("");

        Object item = table.addItem(new Object[] {transferButton, button, keyField, valueComponent}, null);
        
        transferButton.setData(item);
        
        button.setData(item);
        button.setDescription("Remove");
        button.addClickListener(new ClickListener() {

            /**
             * 
             */
            private static final long serialVersionUID = -4699844680421478938L;

            @Override
            public void buttonClick(ClickEvent event) {
                table.removeItem(event.getButton().getData());
            }
        });
        
        transferButton.setDescription("To input");
        transferButton.addClickListener(new ClickListener() {
            
            /**
             * 
             */
            private static final long serialVersionUID = -3395808654887009720L;

            @Override
            public void buttonClick(ClickEvent event) {
                if (transferLayout != null) {
                    Item item = table.getItem(event.getButton().getData());
                    String key = ((TextField) item.getItemProperty("keyColumn").getValue()).getValue();
                    IDataContainer dataContainer = ((ValueComponent) item.getItemProperty("valueColumn").getValue()).getDataContainer();
    
                    ValueComponent valueComponent = new ValueComponent();
                    valueComponent.setDataContainer(dataContainer);
                    
                    transferLayout.addItem(key, valueComponent);
                }
            }
        });
    }

}
