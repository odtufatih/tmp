package tr.com.aktifbank.servicetester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.graymound.util.GMMap;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import tr.com.aktifbank.servicetester.data.DataType;
import tr.com.aktifbank.servicetester.data.IDataContainer;
import tr.com.aktifbank.servicetester.data.MapDataContainer;
import tr.com.aktifbank.servicetester.data.PrimitiveDataContainer;
import tr.com.aktifbank.servicetester.data.TableDataContainer;

public class Converter {

    public static String convertToJSONString(Object obj) {
        JSON json = JSONSerializer.toJSON(obj);
        
        return json.toString();
    }
    
    public static GMMap convertToMap(JSONObject jsonData) {
        
        HashMap<String, Object> map = new HashMap<String, Object>();
        
        for (Iterator<?> iterator = jsonData.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iterator.next();
            String key = (String) entry.getKey();
            Object value = entry.getValue();
            
            if (value instanceof JSONArray) {
                JSONArray       array   = (JSONArray) value;
                int size = array.size();
                List<Object>    list    = new ArrayList<Object>(size);
                
                for (int i = 0; i < size; i++) {
                    Object someKindOfObject = array.get(i);
                    
                    if (someKindOfObject instanceof JSONObject) {
                        list.add(convertToMap((JSONObject)someKindOfObject));
                    } else {
                        list.add(someKindOfObject);
                    }
                }               
                map.put(key, list);              
            } else if (value instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) value;
                
                if (jsonObject.isNullObject()) {
                    map.put(key, null);
                } else {
                    map.put(key, convertToMap(jsonObject));
                }
            } else {
                map.put(key, value);
            }
        }
        
        return new GMMap(map);
    }
    
    public static MapDataContainer convertToMapDataContainer(JSONObject jsonData) {
        MapDataContainer mapDataContainer = new MapDataContainer();
        
        for (Iterator<?> iterator = jsonData.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) iterator.next();
            String key = (String) entry.getKey();
            Object value = entry.getValue();
            
            if (value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                TableDataContainer tableDataContainer = convertToTableDataContainer(array);
                mapDataContainer.putData(key, tableDataContainer);
            } else if (value instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) value;
                
                if (jsonObject.isNullObject()) {
                    mapDataContainer.putData(key, new PrimitiveDataContainer(null));
                } else {
                    mapDataContainer.putData(key, convertToMapDataContainer(jsonObject));
                }
            } else {
                mapDataContainer.putData(key, new PrimitiveDataContainer(String.valueOf(value)));
            }
        }
        
        return mapDataContainer;
    }

    private static TableDataContainer convertToTableDataContainer(JSONArray array) {
        TableDataContainer tableDataContainer = new TableDataContainer();
        
        int size = array.size();
        
        for (int i = 0; i < size; i++) {
            JSONObject arrayObject = (JSONObject) array.get(i);
            
            if (arrayObject.isEmpty()) {
                tableDataContainer.putData(i, null, new PrimitiveDataContainer(null));
            } else {
                for (Iterator<?> arrayIterator = arrayObject.entrySet().iterator(); arrayIterator.hasNext();) {
                    Map.Entry<?, ?> arrayEntry = (Map.Entry<?, ?>) arrayIterator.next();
                    String key = (String) arrayEntry.getKey();
                    Object value = arrayEntry.getValue();
                    
                    if (value instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) value;
                        if (jsonObject.isNullObject()) {
                            tableDataContainer.putData(i, key, new PrimitiveDataContainer(null));
                        } else {
                            tableDataContainer.putData(i, key, convertToMapDataContainer(jsonObject));
                        }
                    }else if (value instanceof JSONArray) {
                        tableDataContainer.putData(i, key, convertToTableDataContainer((JSONArray) value));
                    } else {
                        tableDataContainer.putData(i, key, new PrimitiveDataContainer(String.valueOf(value)));
                    }
                }
            }
        }
        return tableDataContainer;
    }

    public static GMMap convertToMap(MapDataContainer request) {
        GMMap map = new GMMap();
        HashMap<String, IDataContainer> requestMap = request.getMap();
        
        for (Entry<String, IDataContainer> entry : requestMap.entrySet()) {
            String key = entry.getKey();
            if (key != null) {
                IDataContainer dataContainer = entry.getValue();
                
                if (dataContainer.getDataType() == DataType.PRIMITIVE) {
                    map.put(key, ((PrimitiveDataContainer) dataContainer).getData());
                } else if (dataContainer.getDataType() == DataType.TABLE) {
                    map.put(key, extractTable((TableDataContainer) dataContainer));
                } else if (dataContainer.getDataType() == DataType.MAP) {
                    map.put(key, convertToMap((MapDataContainer) dataContainer));
                }
            }
        }
        
        return map;
    }
    
    private static List<GMMap> extractTable(TableDataContainer tableDataContainer) {
        ArrayList<GMMap> list = new ArrayList<GMMap>();
        ArrayList<MapDataContainer> dataList = tableDataContainer.getList();
        
        for (MapDataContainer mapDataContainer : dataList) {
            GMMap map = new GMMap();
            HashMap<String, IDataContainer> rowMap = mapDataContainer.getMap();
            
            for (Entry<String, IDataContainer> entry : rowMap.entrySet()) {
                String rowMapKey = entry.getKey();
                IDataContainer dataContainer = entry.getValue();
                
                if (dataContainer.getDataType() == DataType.PRIMITIVE) {
                    map.put(rowMapKey, ((PrimitiveDataContainer) dataContainer).getData());
                } else if (dataContainer.getDataType() == DataType.TABLE) {
                    map.put(rowMapKey, extractTable((TableDataContainer) dataContainer));
                } else if (dataContainer.getDataType() == DataType.MAP) {
                    map.put(rowMapKey, convertToMap((MapDataContainer) dataContainer));
                }
            }
            
            list.add(map);
        }
        return list;
    }

}
