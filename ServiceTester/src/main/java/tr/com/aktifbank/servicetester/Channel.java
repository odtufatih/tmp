package tr.com.aktifbank.servicetester;

import java.io.Serializable;

public class Channel implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6016369688595593924L;
    
    private final String oid;
    private final String name;
    
    public Channel(String oid, String name) {
        this.oid = oid;
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public String getOid() {
        return oid;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result
                + ((oid == null) ? 0 : oid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Channel other = (Channel) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (oid == null) {
            if (other.oid != null)
                return false;
        } else if (!oid.equals(other.oid))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return name;
    }

}
