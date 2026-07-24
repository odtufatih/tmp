package tr.com.aktifbank.servicetester.servicecall;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.graymound.util.GMMap;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;

import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import tr.com.aktifbank.servicetester.Converter;
import tr.com.aktifbank.servicetester.DataConnection;
import tr.com.aktifbank.servicetester.Server;
import tr.com.aktifbank.servicetester.data.MapDataContainer;

public class ServiceCaller {

    public static MapDataContainer call(CallParameter parameter) throws ServiceCallException {
        String endpoint = endpoint(parameter);
        
        try {
            String sessionId = getSessionId(parameter, endpoint);
            GMMap paramsMap = Converter.convertToMap(parameter.getMapDataContainer());
            
            StringEntity serviceCallEntity = createServiceCallEntity(paramsMap, parameter.getServiceName(), sessionId);
            JSONObject serviceResponse = call(serviceCallEntity, endpoint);
            
            setSessionId(serviceResponse);
    
            MapDataContainer response = Converter.convertToMapDataContainer(serviceResponse.getJSONObject("result"));
            
            return response;
        } catch (ServiceTimeoutException e) {
            try {
                clearSessionId();
                createSessionId(parameter, endpoint);
                
                return call(parameter);
            } catch (ServiceTimeoutException e1) {
                throw new ServiceCallException(e);
            }
        }
    }

    private static String getSessionId(CallParameter parameter, String endpoint)
            throws ServiceCallException, ServiceTimeoutException {
        VaadinSession session = getSession();
        
        if ((session.getAttribute("SESSION_ID") == null)
                || (session.getAttribute("CHANNEL") != parameter.getChannel())
                || (session.getAttribute("SERVER") != parameter.getServer())
                || (session.getAttribute("ENVIRONMENT") != parameter.getEnvironment())) {
            createSessionId(parameter, endpoint);
        }
        return (String) session.getAttribute("SESSION_ID");
    }

    private static VaadinSession getSession() {
        VaadinSession session = VaadinSession.getCurrent();
        return session;
    }

    private static void createSessionId(CallParameter parameter, String endpoint)
            throws ServiceCallException, ServiceTimeoutException {
        VaadinSession session = getSession();
        
        StringEntity authenticationEntity = createAuthenticationEntity(parameter);
        JSONObject authResponse = call(authenticationEntity, endpoint);
        
        String sessionId = authResponse.getJSONObject("result").getString("sessionId");
        session.setAttribute("SESSION_ID", sessionId);
        session.setAttribute("CHANNEL", parameter.getChannel());
        session.setAttribute("SERVER", parameter.getServer());
        session.setAttribute("ENVIRONMENT", parameter.getEnvironment());
    }

    private static void setSessionId(JSONObject serviceResponse) {
        VaadinSession session = getSession();
        
        String sessionId = serviceResponse.getJSONObject("result").getString("sessionId");
        session.setAttribute("SESSION_ID", sessionId);
    }

    private static void clearSessionId() {
        VaadinSession session = getSession();
        session.setAttribute("SESSION_ID", null);
    }
    
    private static JSONObject call(HttpEntity entity, String endpoint)
            throws ServiceCallException, ServiceTimeoutException {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(endpoint);
            post.setEntity(entity);
            
            HttpResponse response = client.execute(post);

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(), Charset.forName("UTF-8")));
            JSONObject jsonObject = JSONObject.fromObject(rd.readLine());
            
            JSONObject error = jsonObject.getJSONObject("error");
            if (!JSONNull.getInstance().equals(error)) {
                String serviceMessage = error.getString("message");
                
                if (serviceMessage.equals("Session Timeout")) {
                    throw new ServiceTimeoutException();
                }

                String message = "Service Error: " + serviceMessage;
                throw new ServiceCallException(message);
            }
            
            return jsonObject;
        } catch (ServiceCallException e) {
            throw e;
        } catch (ServiceTimeoutException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceCallException(e);
        }
    }
    
    private static StringEntity createServiceCallEntity(GMMap params, String serviceName, String sessionId) {
        GMMap serverData = new GMMap();
        serverData.put("serviceName", serviceName);
        if (sessionId != null) {
            serverData.put("sessionId", sessionId);
        }
        
        GMMap map = new GMMap();
        map.put("method", "EXECUTE_SERVICE");
        map.put("id", "1");
        map.put("params", 0, params);
        map.put("server_data", serverData);
        
        String json = Converter.convertToJSONString(map);
        
        return new StringEntity(json.toString(), Charset.forName("UTF-8"));
    }
   
    private static StringEntity createAuthenticationEntity(CallParameter parameter) throws ServiceCallException {
        GMMap map = createAuthenticationMap(parameter);

        return createServiceCallEntity(map, "BNSPR_USER_AUTHENTICATE", null);
    }

    private static GMMap createAuthenticationMap(CallParameter parameter) throws ServiceCallException {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DataConnection.getConnection();
            statement = connection.prepareStatement(
                    "select user_name, password from servicelog.service_tester_channel_auth where channel_oid=? and environment_oid=?");
            
            statement.setString(1, parameter.getChannel().getOid());
            statement.setString(2, parameter.getEnvironment().getOid());
            
            String user_name = null;
            String password = null;
            
            if (statement.execute()) {
                ResultSet resultSet = statement.getResultSet();
                
                if (resultSet != null) {
                    if (resultSet.next()) {
                        user_name  = resultSet.getString("user_name");
                        password = resultSet.getString("password");
                    }
                }
            }
            
            if (user_name == null) {
                throw new ServiceCallException("Channel authentication parameters cannot be found");
            }
            
            GMMap map = new GMMap();
            map.put("USERNAME", user_name);
            map.put("PASSWORD", password);
            map.put("CHANNEL", parameter.getChannel().getName());
            map.put("LANGUAGE", "tr");
            
            return map;
        } catch (Exception e) {
            throw new ServiceCallException(e);
        } finally {
            DataConnection.closeConnection(connection);
            DataConnection.closeStatement(statement);
        }
    }
    
    private static String endpoint(CallParameter parameter) throws ServiceCallException {
        if (Server.LOCALHOST_OID.equals(parameter.getServer().getOid())) {
            String localhost = VaadinService.getCurrentRequest().getRemoteAddr();
            
            return "http://" + localhost + ":8080/GMServer/Server/JSON";
        }
        
        return hostEndpoint(parameter);
    }
    
    private static String hostEndpoint(CallParameter parameter) throws ServiceCallException {
        Connection connection = null;
        PreparedStatement statement = null;
        String endpoint = null;
        
        try {
            connection = DataConnection.getConnection();
            statement = connection.prepareStatement(
                    "select endpoint from servicelog.service_tester_endpoints where environment_oid=? and server_oid=?");
            
            statement.setString(1, parameter.getEnvironment().getOid());
            statement.setString(2, parameter.getServer().getOid());
            
            if (statement.execute()) {
                ResultSet resultSet = statement.getResultSet();
                
                if (resultSet != null) {
                    if (resultSet.next()) {
                        endpoint = resultSet.getString("endpoint");
                    }
                }
            }
            
            if (endpoint == null) {
                throw new ServiceCallException("Endpoint cannot be found");
            }
            
            return endpoint;
        } catch (Exception e) {
            throw new ServiceCallException(e);
        } finally {
            DataConnection.closeConnection(connection);
            DataConnection.closeStatement(statement);
        }
    }

}
