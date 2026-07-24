package tr.com.aktifbank.servicetester.ui;

import java.util.HashMap;
import java.util.Map.Entry;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import tr.com.aktifbank.servicetester.data.IDataContainer;
import tr.com.aktifbank.servicetester.data.IDataContainerHandler;
import tr.com.aktifbank.servicetester.data.MapDataContainer;

public class MapLayout extends VerticalLayout implements IDataContainerHandler<MapDataContainer> {
	
	/**
     * 
     */
    private static final long serialVersionUID = 7911437766449316625L;
    
    protected final Table table;

	public MapLayout() {
		super();
		
		table = new Table("Map");
		
		table.addContainerProperty("buttonColumn", Button.class, null, "", null, Align.CENTER);
		table.addContainerProperty("keyColumn", TextField.class, null, "Key", null, Align.CENTER);
		table.addContainerProperty("valueColumn", ValueComponent.class, null, "Value", null, Align.CENTER);
		
		table.setColumnExpandRatio("buttonColumn", 1.0f);
		table.setColumnExpandRatio("keyColumn", 5.0f);
		table.setColumnExpandRatio("valueColumn", 6.0f);
		
		table.setPageLength(15);
		table.setEditable(false);
		table.setSizeFull();
		
		addComponent(table);

		setExpandRatio(table, 10.0f);

		setMargin(true);
		setSpacing(true);
		setSizeFull();
	}

    @Override
    public MapDataContainer getDataContainer() {
        MapDataContainer mapDataContainer = new MapDataContainer();
        
        for (Object itemId : table.getItemIds()) {
            Item item = table.getItem(itemId);
            String key = ((TextField) item.getItemProperty("keyColumn").getValue()).getValue();
            IDataContainer data = ((ValueComponent) item.getItemProperty("valueColumn").getValue()).getDataContainer();
            
            mapDataContainer.putData(key, data);
        }
        
        return mapDataContainer;
    }

    @Override
    public void setDataContainer(MapDataContainer mapDataContainer) {
        table.removeAllItems();
        
        HashMap<String, IDataContainer> dataMap = mapDataContainer.getMap();
        for (Entry<String, IDataContainer> entry : dataMap.entrySet()) {
            String key = entry.getKey();
            ValueComponent valueComponent = new ValueComponent();
            IDataContainer dataContainer = entry.getValue();
            valueComponent.setDataContainer(dataContainer);
            
            addItem(key, valueComponent);
        }
    }

    
    protected void addItem(String key, ValueComponent valueComponent) {
        final Button button = new Button("-");
        final TextField keyField = new TextField();
        
        keyField.setValue(key);
        keyField.setNullRepresentation("");

        Object item = table.addItem(new Object[] {button, keyField, valueComponent}, null);
        
        button.setData(item);
        button.setDescription("Remove");
        button.addClickListener(new ClickListener() {

            /**
             * 
             */
            private static final long serialVersionUID = 2196504131194783471L;

            @Override
            public void buttonClick(ClickEvent event) {
                table.removeItem(event.getButton().getData());
            }
        });
    }

}
