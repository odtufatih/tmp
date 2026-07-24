package tr.com.aktifbank.servicetester.ui;

import com.vaadin.ui.Window;

import tr.com.aktifbank.servicetester.data.IDataContainerHandler;
import tr.com.aktifbank.servicetester.data.TableDataContainer;

public class TableWindow extends Window implements IDataContainerHandler<TableDataContainer> {
	
	/**
     * 
     */
    private static final long serialVersionUID = -389936658940995845L;
    
    private final InputTableLayout tableLayout;

	public TableWindow() {
		super("Table");
		
		tableLayout = new InputTableLayout();
		
		setCaption("Table");
		setWidth(50, Unit.PERCENTAGE);
		setHeight(50, Unit.PERCENTAGE);
		setModal(true);
		setContent(tableLayout);
	}

    @Override
    public TableDataContainer getDataContainer() {
        return tableLayout.getDataContainer();
    }

    @Override
    public void setDataContainer(TableDataContainer dataContainer) {
        tableLayout.setDataContainer(dataContainer);
    }

}
