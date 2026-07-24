package tr.com.aktifbank.servicetester.data;

import java.io.Serializable;
import java.util.HashMap;

public class MapDataContainer implements IDataContainer, Serializable {
	
	/**
     * 
     */
    private static final long serialVersionUID = -8334491345958196434L;
    
    private final HashMap<String, IDataContainer> map;

	public MapDataContainer() {
		map = new HashMap<String, IDataContainer>();
	}

	@Override
	public DataType getDataType() {
		return DataType.MAP;
	}
	
	public HashMap<String, IDataContainer> getMap() {
		return map;
	}
	
	public void putData(String key, IDataContainer data) {
		map.put(key, data);
	}
	
	@Override
	public String toString() {
	    return map.toString();
	}

}
