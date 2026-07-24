package tr.com.aktifbank.servicetester.ui;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

import tr.com.aktifbank.servicetester.data.DataType;
import tr.com.aktifbank.servicetester.data.IDataContainer;
import tr.com.aktifbank.servicetester.data.IDataContainerHandler;
import tr.com.aktifbank.servicetester.data.MapDataContainer;
import tr.com.aktifbank.servicetester.data.PrimitiveDataContainer;
import tr.com.aktifbank.servicetester.data.TableDataContainer;

public class ValueComponent extends CustomComponent implements IDataContainerHandler<IDataContainer> {

	/**
     * 
     */
    private static final long serialVersionUID = 2735946981832565649L;
    
    private static final String MAP_STR = "[MAP]";
    private static final String TABLE_STR = "[TABLE]";
    private static final String NULL_STR = "<null>";
    
    private final TextField valueField;
	private final Button tableButton;
	private final Button mapButton;
	private TableWindow tableWindow;
	private MapWindow mapWindow;
	private DataType valueType = DataType.PRIMITIVE;
	
	public ValueComponent() {
		HorizontalLayout layout = new HorizontalLayout();
		
		valueField = new TextField();
		tableButton = new Button("T");
		mapButton = new Button("M");
		
		valueField.setNullSettingAllowed(true);
		valueField.setNullRepresentation(NULL_STR);
		
		layout.addComponent(valueField);
		layout.addComponent(tableButton);
		layout.addComponent(mapButton);
		
		layout.setSpacing(true);
		
		setCompositionRoot(layout);
		
		mapButton.setDescription("Map");
		mapButton.addClickListener(new ClickListener() {

            /**
             * 
             */
            private static final long serialVersionUID = -7516825575896443801L;

            @Override
            public void buttonClick(ClickEvent event) {
                setForMap();
                UI.getCurrent().addWindow(mapWindow);
            }
        });
		
		tableButton.setDescription("Table");
		tableButton.addClickListener(new ClickListener() {

            /**
             * 
             */
            private static final long serialVersionUID = -1978846160638499723L;

            @Override
            public void buttonClick(ClickEvent event) {
                setForTable();
                UI.getCurrent().addWindow(tableWindow);
            }
        });
		
		valueField.addValueChangeListener(new ValueChangeListener() {

            /**
             * 
             */
            private static final long serialVersionUID = -5790407695691873810L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (!MAP_STR.equals(event.getProperty().getValue())
                        && !TABLE_STR.equals(event.getProperty().getValue())) {
                    setForPrimitive();
                }
            }
        });
	}

    @Override
    public IDataContainer getDataContainer() {
        if (valueType == DataType.MAP) {
            return mapWindow.getDataContainer();
        }
        
        if (valueType == DataType.TABLE) {
            return tableWindow.getDataContainer();
        }
        
        return new PrimitiveDataContainer(valueField.getValue());
    }

    @Override
    public void setDataContainer(IDataContainer dataContainer) {
        if (dataContainer.getDataType() == DataType.MAP) {
            setForMap();
            mapWindow.setDataContainer((MapDataContainer) dataContainer);
        } else if (dataContainer.getDataType() == DataType.TABLE) {
            setForTable();
            tableWindow.setDataContainer((TableDataContainer) dataContainer);
        } else if (dataContainer.getDataType() == DataType.PRIMITIVE){
            setForPrimitive();
            valueField.setValue(((PrimitiveDataContainer) dataContainer).getData());
        }
    }
    
    private void setForTable() {
        if (tableWindow == null) {
            tableWindow = new TableWindow();
        }
        valueField.setValue(TABLE_STR);
        valueType = DataType.TABLE;

        valueField.setEnabled(false);
        mapButton.setEnabled(false);
    }
    
    private void setForMap() {
        if (mapWindow == null) {
            mapWindow = new MapWindow();
        }
        valueField.setValue(MAP_STR);
        valueType = DataType.MAP;
        
        valueField.setEnabled(false);
        tableButton.setEnabled(false);
    }
    
    private void setForPrimitive() {
        valueType = DataType.PRIMITIVE;

        mapButton.setEnabled(false);
        tableButton.setEnabled(false);
    }
	
}
