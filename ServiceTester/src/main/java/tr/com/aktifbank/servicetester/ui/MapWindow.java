package tr.com.aktifbank.servicetester.ui;

import com.vaadin.ui.Window;

import tr.com.aktifbank.servicetester.data.IDataContainerHandler;
import tr.com.aktifbank.servicetester.data.MapDataContainer;

public class MapWindow extends Window implements IDataContainerHandler<MapDataContainer> {
	
	/**
     * 
     */
    private static final long serialVersionUID = 409641078283515603L;
    
    private final InputMapLayout mapLayout;

	public MapWindow() {
		super("Map");

		mapLayout = new InputMapLayout();
		
		setCaption("Map");
		setWidth(50, Unit.PERCENTAGE);
		setHeight(50, Unit.PERCENTAGE);
		setModal(true);
		setContent(mapLayout);
	}

    @Override
    public MapDataContainer getDataContainer() {
        return mapLayout.getDataContainer();
    }

    @Override
    public void setDataContainer(MapDataContainer dataContainer) {
        mapLayout.setDataContainer(dataContainer);
    }

}
