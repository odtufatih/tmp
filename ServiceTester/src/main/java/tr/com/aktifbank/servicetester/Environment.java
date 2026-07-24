package tr.com.aktifbank.servicetester;

import java.io.Serializable;

public class Environment implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -4769932821027224898L;
    
    private final String oid;
    private final String name;
    
    public Environment(String oid, String name) {
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
        result = prime * result
                + ((name == null) ? 0 : name.hashCode());
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
        Environment other = (Environment) obj;
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
