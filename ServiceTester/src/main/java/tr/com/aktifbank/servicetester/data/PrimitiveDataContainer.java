package tr.com.aktifbank.servicetester.data;

import java.io.Serializable;

public class PrimitiveDataContainer implements IDataContainer, Serializable {
	
	/**
     * 
     */
    private static final long serialVersionUID = 8011834046730795603L;
    
    private String data;
	
	public PrimitiveDataContainer(String data) {
	    this.data = data;
	}

	@Override
	public DataType getDataType() {
		return DataType.PRIMITIVE;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
	    return data;
	}

}
