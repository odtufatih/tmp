package tr.com.aktifbank.servicetester.servicecall;

import java.io.Serializable;

import tr.com.aktifbank.servicetester.Channel;
import tr.com.aktifbank.servicetester.Environment;
import tr.com.aktifbank.servicetester.Server;
import tr.com.aktifbank.servicetester.data.MapDataContainer;

public class CallParameter implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -227089284766337957L;
    
    private final String serviceName;
    private final MapDataContainer mapDataContainer;
    private final Environment environment;
    private final Channel channel;
    private final Server server;
    
    public CallParameter(
            String serviceName,
            MapDataContainer mapDataContainer,
            Environment environment,
            Channel channel,
            Server server) {
        this.serviceName = serviceName;
        this.mapDataContainer = mapDataContainer;
        this.environment = environment;
        this.channel = channel;
        this.server = server;
    }
    
    public String getServiceName() {
        return serviceName;
    }
    
    public MapDataContainer getMapDataContainer() {
        return mapDataContainer;
    }

    public Channel getChannel() {
        return channel;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public Server getServer() {
        return server;
    }
    
    @Override
    public String toString() {
        return environment + " " + server + " " + channel + " " + mapDataContainer.getMap();
    }

}
