package tr.com.aktifbank.servicetester.data;

import java.io.Serializable;
import java.util.ArrayList;

public class TableDataContainer implements IDataContainer, Serializable {
	
	/**
     * 
     */
    private static final long serialVersionUID = 2622411791254487542L;
    
    private final ArrayList<MapDataContainer> list;

	public TableDataContainer() {
		list = new ArrayList<MapDataContainer>();
	}

	@Override
	public DataType getDataType() {
		return DataType.TABLE;
	}

	public ArrayList<MapDataContainer> getList() {
		return list;
	}
	
	public void putData(int index, String key, IDataContainer data) {
	    MapDataContainer rowData = null;
		
	    if (index == list.size()) {
			rowData = new MapDataContainer();
			list.add(rowData);
		} else if (index < list.size()) {
		    rowData = list.get(index);
		} else {
		    throw new ArrayIndexOutOfBoundsException(index);
		}
		
	    rowData.putData(key, data);
	}
	
	public void putData(int index, MapDataContainer data) {
        if (index == list.size()) {
            list.add(data);
        } else if (index < list.size()) {
            list.set(index, data);
        } else {
            throw new ArrayIndexOutOfBoundsException(index);
        }
	}
	
	@Override
	public String toString() {
	    return list.toString();
	}

}
