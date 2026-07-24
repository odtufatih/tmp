package tr.com.aktifbank.servicetester.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import tr.com.aktifbank.servicetester.data.IDataContainer;
import tr.com.aktifbank.servicetester.data.IDataContainerHandler;
import tr.com.aktifbank.servicetester.data.MapDataContainer;
import tr.com.aktifbank.servicetester.data.TableDataContainer;

public class TableLayout extends VerticalLayout implements IDataContainerHandler<TableDataContainer> {
    
    /**
     * 
     */
    private static final long serialVersionUID = -3840958557657352890L;

    private static class ErrorContainer {
        private static final UserError NUMBER_ERROR = new UserError("Enter an integer");
    }
	
    private final Table table;

	private int lastIndex;
    private LinkedHashMap<Integer, AtomicInteger> indexItemCount = new LinkedHashMap<Integer, AtomicInteger>();

	public TableLayout() {
		super();
		
		lastIndex = -1;

		table = new Table("Map");
		
		table.addContainerProperty("", Button.class, null);
		table.addContainerProperty("Index", TextField.class, null);
		table.addContainerProperty("Key", TextField.class, null);
		table.addContainerProperty("Value", ValueComponent.class, null);
		
        table.setSelectable(true);
        table.setSizeFull();

		addComponent(table);

		setExpandRatio(table, 10.0f);

		setMargin(true);
		setSpacing(true);
		setSizeFull();
	}

    @Override
    public TableDataContainer getDataContainer() {
        TableDataContainer tableDataContainer = new TableDataContainer();
        
        for (Object itemId : table.getItemIds()) {
            Item item = table.getItem(itemId);
            
            int index = Integer.valueOf(((TextField) item.getItemProperty("Index").getValue()).getValue());
            String key = ((TextField) item.getItemProperty("Key").getValue()).getValue();
            IDataContainer data = ((ValueComponent) item.getItemProperty("Value").getValue()).getDataContainer();
            
            tableDataContainer.putData(index, key, data);
        }
        return tableDataContainer;
    }

    @Override
    public void setDataContainer(TableDataContainer tableDataContainer) {
        removeAll();
        
        ArrayList<MapDataContainer> dataList = tableDataContainer.getList();
        
        for (MapDataContainer mapDataContainer : dataList) {
            HashMap<String, IDataContainer> map = mapDataContainer.getMap();
            
            lastIndex++;
            
            for (Entry<String, IDataContainer> entry : map.entrySet()) {
                String key = entry.getKey();
                ValueComponent valueComponent = new ValueComponent();
                IDataContainer dataContainer = entry.getValue();
                valueComponent.setDataContainer(dataContainer);
                
                addItem(lastIndex, key, valueComponent);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private void createItemComponents(Object itemId, int index, String key, ValueComponent valueComponent) {
        final TextField indexField = new TextField();
        final Button button = new Button("-");
        final TextField keyField = new TextField();
        
        keyField.setNullRepresentation("");
        keyField.setValue(key);
        
        indexField.setEnabled(false);
        indexField.setValue(String.valueOf(index));
        indexField.addValueChangeListener(new ValueChangeListener() {
            
            /**
             * 
             */
            private static final long serialVersionUID = 3582584393689841024L;
            
            @Override
            public void valueChange(ValueChangeEvent event) {
                try {
                    Object value = event.getProperty().getValue();
                    Integer.parseInt(value.toString());
                    indexField.setComponentError(null);
                } catch (NumberFormatException e) {
                    indexField.setValue("");
                    indexField.setComponentError(ErrorContainer.NUMBER_ERROR);
                }
            }
        });
        
        table.getItem(itemId).getItemProperty("").setValue(button);
        table.getItem(itemId).getItemProperty("Index").setValue(indexField);
        table.getItem(itemId).getItemProperty("Key").setValue(keyField);
        table.getItem(itemId).getItemProperty("Value").setValue(valueComponent);
        
        button.setData(itemId);
        button.addClickListener(new ClickListener() {
            
            /**
             * 
             */
            private static final long serialVersionUID = 8806907983729113788L;
            
            @Override
            public void buttonClick(ClickEvent event) {
                Object itemId = event.getButton().getData();
                int index = Integer.valueOf(((TextField) table.getItem(itemId).getItemProperty("Index").getValue()).getValue());
                
                if ((index == lastIndex)
                        || (indexItemCount.get(index).get() > 1)) {
                    table.removeItem(itemId);

                    int count = indexItemCount.get(index).decrementAndGet();
                    if (count == 0) {
                        lastIndex--;
                    }
                }
            }
        });
    }
    
    private void completeAddItem(Object itemId, int index, String key, ValueComponent valueComponent) {
        createItemComponents(itemId, index, key, valueComponent);
        
        if (indexItemCount.get(index) == null) {
            indexItemCount.put(lastIndex, new AtomicInteger(1));
        } else {
            indexItemCount.get(index).getAndIncrement();
        }
    }
    
    private void addItem(int index, String key, ValueComponent valueComponent) {
        Object itemId = table.addItem();
        completeAddItem(itemId, index, key, valueComponent);
    }
    
    private void addItemAfter(Object itemId, int index, String key, ValueComponent valueComponent) {
        Object newItemId = table.addItemAfter(itemId);
        completeAddItem(newItemId, index, key, valueComponent);
    }

    protected void addNewItem() {
        if (lastIndex == -1) {
            addIndexAndNewItem();
            return;
        }
        
        final ValueComponent valueComponent = new ValueComponent();
        Object itemId = table.getValue();
        int index = lastIndex;
        
        if (itemId != null) {
            index = Integer.valueOf(((TextField) table.getItem(itemId).getItemProperty("Index").getValue()).getValue());
            addItemAfter(itemId, index, null, valueComponent);
        } else {
            addItem(index, null, valueComponent);
        }
    }
    
    protected void addIndexAndNewItem() {
        final ValueComponent valueComponent = new ValueComponent();
        addItem(++lastIndex, null, valueComponent);
    }
    
    protected void removeAll() {
        table.removeAllItems();
        indexItemCount.clear();
        lastIndex = -1;
    }

}
