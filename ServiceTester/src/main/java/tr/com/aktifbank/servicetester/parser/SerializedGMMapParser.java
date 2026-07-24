package tr.com.aktifbank.servicetester.parser;

import tr.com.aktifbank.servicetester.data.MapDataContainer;
import tr.com.aktifbank.servicetester.data.PrimitiveDataContainer;
import tr.com.aktifbank.servicetester.data.TableDataContainer;

public class SerializedGMMapParser {
    
    private static final String NULL = "null";
	
	private static class DataBlock {
	    private int endIndex = -1;
	    private String data;
	}
	
	public MapDataContainer parse(String serialized) throws ParseException {
	    DataBlock dataBlock = extractBlock(serialized, '{', '}');
	    MapDataContainer parseMap = parseMap(dataBlock.data);
	    
	    return parseMap;
	}

	private static String nullValue(String value) {
	    if (value.equals(NULL)) {
	        return null;
	    }
	    return value;
	}

    private int findFirstOccurence(String str, char c, boolean raiseException) throws ParseException {
        int index = str.indexOf(c);
        if (raiseException && index == -1) {
            throw parseException();
        }
        return index;
    }

	private MapDataContainer parseMap(String block) throws ParseException {
	    MapDataContainer mapDataContainer = new MapDataContainer();
	    
	    while (!block.isEmpty()) {
	        int beginIndex = 0;
	        int assignIndex = findFirstOccurence(block, '=', true);
            int redundantCommaIndex = findFirstOccurence(block, ',', false);
            
            if ((redundantCommaIndex != -1)
                    && (redundantCommaIndex < assignIndex)) {
                beginIndex = redundantCommaIndex + 1;
            }
            
            String key = block.substring(beginIndex, assignIndex).trim();
            
            block = block.substring(assignIndex + 1).trim();
            
            if (block.isEmpty()) {
                mapDataContainer.putData(key, new PrimitiveDataContainer(""));
            } else {
                char c = block.charAt(0);
                if (c == '{') {
                    DataBlock dataBlock = extractBlock(block, '{', '}');
                    mapDataContainer.putData(key, parseMap(dataBlock.data));
                    block = block.substring(dataBlock.endIndex + 1).trim();
                } else if (c == '[') {
                    DataBlock dataBlock = extractBlock(block, '[', ']');
                    mapDataContainer.putData(key, parseTable(dataBlock.data));
                    block = block.substring(dataBlock.endIndex + 1).trim();
                } else {
                    int commaIndex = findFirstOccurence(block, ',', false);
                    if (commaIndex == -1) {
                        mapDataContainer.putData(key, new PrimitiveDataContainer(nullValue(block)));
                        block = "";
                    } else {
                        mapDataContainer.putData(key, new PrimitiveDataContainer(nullValue(block.substring(0, commaIndex).trim())));
                        block = block.substring(commaIndex + 1).trim();
                    }
                }
            }
	    }
	    
	    return mapDataContainer;
	}
	
	private TableDataContainer parseTable(String block) throws ParseException {
	    TableDataContainer tableDataContainer = new TableDataContainer();
	    int index = 0;
	    
        while (!block.isEmpty()) {
            DataBlock dataBlock = extractBlock(block, '{', '}');
            tableDataContainer.putData(index++, parseMap(dataBlock.data));
            block = block.substring(dataBlock.endIndex + 1).trim();
        }
	    
        return tableDataContainer;
	}
	
	private DataBlock extractBlock(String str, char openItem, char closeItem) throws ParseException {
	    int beginIndex = findFirstOccurence(str, openItem, true);
	    int index = beginIndex;
	    int unmatchCount = 1;
	    
	    while (++index < str.length()) {
	        char c = str.charAt(index);
	        if (c == openItem) {
	            unmatchCount++;
	        }
	        if (c == closeItem) {
	            if (--unmatchCount == 0) {
	                DataBlock dataBlock = new DataBlock();
	                dataBlock.endIndex = index;
	                dataBlock.data = str.substring(beginIndex + 1, index);
	                
	                return dataBlock;
	            }
	        }
	    }
	    
	    throw parseException();
	}
	
	private ParseException parseException() throws ParseException {
	    throw new ParseException("GMMap string is not appropriate");
	}

}
