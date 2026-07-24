package tr.com.aktifbank.servicetester.model;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import tr.com.aktifbank.servicetester.Channel;
import tr.com.aktifbank.servicetester.DataConnection;
import tr.com.aktifbank.servicetester.Environment;
import tr.com.aktifbank.servicetester.ExceptionHandler;
import tr.com.aktifbank.servicetester.HistoryUtil;
import tr.com.aktifbank.servicetester.HistoryUtil.HistoryItem;
import tr.com.aktifbank.servicetester.Server;
import tr.com.aktifbank.servicetester.data.MapDataContainer;
import tr.com.aktifbank.servicetester.parser.ParseException;
import tr.com.aktifbank.servicetester.parser.SerializedGMMapParser;
import tr.com.aktifbank.servicetester.servicecall.CallParameter;
import tr.com.aktifbank.servicetester.servicecall.ServiceCaller;

public class ServiceTesterModel implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 4033866364154360701L;
    
    private String serviceName;
    private Environment environment;
    private Channel channel;
    private Server server;
    private final List<Server> servers;
    private final List<Environment> environments;
    private final ArrayList<Channel> channels;
    
    public ServiceTesterModel(ExceptionHandler exceptionHandler) {
        environments = new ArrayList<Environment>();
        servers = new ArrayList<Server>();
        channels = new ArrayList<Channel>();

        try {
            collectEnvironments();
            collectServers();
            collectChannels();
        } catch (Exception e) {
            exceptionHandler.handle(e);
        }
    }
    
    private void collectEnvironments() throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DataConnection.getConnection();
            statement = connection.prepareStatement(
                    "select oid, name from servicelog.service_tester_environments order by sort_order");
            
            if (statement.execute()) {
                ResultSet resultSet = statement.getResultSet();
                
                if (resultSet != null) {
                    while(resultSet.next()) {
                        String oid = resultSet.getString("oid");
                        String name = resultSet.getString("name");
                        
                        environments.add(new Environment(oid, name));
                    }
                }
            }
        } finally {
            DataConnection.closeConnection(connection);
            DataConnection.closeStatement(statement);
        }
    }

    private void collectChannels() throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DataConnection.getConnection();
            statement = connection.prepareStatement(
                    "select oid, name from servicelog.service_tester_channels order by name");
            
            if (statement.execute()) {
                ResultSet resultSet = statement.getResultSet();
                
                if (resultSet != null) {
                    while(resultSet.next()) {
                        String oid = resultSet.getString("oid");
                        String name = resultSet.getString("name");
                        
                        channels.add(new Channel(oid, name));
                    }
                }
            }
        } finally {
            DataConnection.closeConnection(connection);
            DataConnection.closeStatement(statement);
        }
    }

    private void collectServers() throws Exception {
        servers.add(new Server(Server.LOCALHOST_OID, "localhost"));

        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DataConnection.getConnection();
            statement = connection.prepareStatement(
                    "select oid, name from servicelog.service_tester_servers");
            
            if (statement.execute()) {
                ResultSet resultSet = statement.getResultSet();
                
                if (resultSet != null) {
                    while(resultSet.next()) {
                        String name = resultSet.getString("name");
                        String oid = resultSet.getString("oid");
                        
                        servers.add(new Server(oid, name));
                    }
                }
            }
        } finally {
            DataConnection.closeConnection(connection);
            DataConnection.closeStatement(statement);
        }
    }

    public MapDataContainer call(MapDataContainer request) throws Exception {
        CallParameter callParameter = new CallParameter(serviceName, request, environment, channel, server);
    
        HistoryUtil.save(callParameter);
        
        MapDataContainer response = ServiceCaller.call(callParameter);
        
        return response;
    }
    
    public MapDataContainer deserialize(String serialized) throws ParseException {
        MapDataContainer mapDataContainer = new SerializedGMMapParser().parse(serialized);
        
        return mapDataContainer;
    }
    
    public ArrayList<Object[]> getHistory() throws Exception {
        ArrayList<Object[]> list = new ArrayList<Object[]>();
        List<HistoryItem> historyItems = HistoryUtil.getHistory();
        for (HistoryItem historyItem : historyItems) {
            list.add(new Object[] {
                    HistoryUtil.formatDate(historyItem.getDate()),
                    historyItem.getCallParameter().getServiceName(),
                    historyItem.getCallParameter()});
        }
        
        return list;
    }
    
    public void clearHistory() throws Exception {
        HistoryUtil.clearHistory();
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public List<Server> getServers() {
        return servers;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public List<Environment> getEnvironments() {
        return environments;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

}
