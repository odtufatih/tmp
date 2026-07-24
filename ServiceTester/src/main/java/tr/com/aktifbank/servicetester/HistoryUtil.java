package tr.com.aktifbank.servicetester;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.vaadin.server.VaadinService;

import tr.com.aktifbank.servicetester.servicecall.CallParameter;

public class HistoryUtil {
    
    private static final ThreadLocal<SimpleDateFormat> dateFormatter =
            new ThreadLocal<SimpleDateFormat>() {
        
                protected SimpleDateFormat initialValue() {
                    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                };
            };

    public static class HistoryItem {
        
        private Date date;
        private CallParameter callParameter;
        
        public HistoryItem(Date date, CallParameter callParameter) {
            this.date = date;
            this.callParameter = callParameter;
        }

        public Date getDate() {
            return date;
        }

        public CallParameter getCallParameter() {
            return callParameter;
        }
    
    }
    
    public static void save(CallParameter callParameter) throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DataConnection.getConnection();
            statement = connection.prepareStatement(
                    "insert into servicelog.service_tester_history values(?,?,?)");
            
            String remoteAddr = getRemoteAddress();
            byte[] byteArray = toByteArray(callParameter);
            Timestamp timestamp = new Timestamp(Calendar.getInstance().getTimeInMillis());
            
            statement.setString(1, remoteAddr);
            statement.setTimestamp(2, timestamp);
            statement.setBytes(3, byteArray);
            
            statement.execute();
            
        } finally {
            DataConnection.closeConnection(connection);
            DataConnection.closeStatement(statement);
        }
    }

    private static String getRemoteAddress() {
        return VaadinService.getCurrentRequest().getRemoteAddr();
    }
    
    private static byte[] toByteArray(CallParameter callParameter) {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            
            oos.writeObject(callParameter);
            
            return bos.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) { }
        }
    }
    
    private static CallParameter toCallParameter(byte[] byteArray) {
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        
        try {
            bis = new ByteArrayInputStream(byteArray);
            ois = new ObjectInputStream(bis);
            
            CallParameter callParameter = (CallParameter) ois.readObject();
            
            return callParameter;
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (Exception e) { }
        }
    }

    public static List<HistoryItem> getHistory() throws Exception {
        List<HistoryItem> list = new ArrayList<HistoryUtil.HistoryItem>();

        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DataConnection.getConnection();
            statement = connection.prepareStatement(
                    "select call_date, data from servicelog.service_tester_history where client_ip=? order by call_date desc");
            
            String remoteAddr = getRemoteAddress();
            
            statement.setString(1, remoteAddr);
            statement.execute();
            
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                Timestamp timestamp = resultSet.getTimestamp("call_date");
                byte[] byteArray = resultSet.getBytes("data");
                
                Date date = dateFormatter.get().parse(timestamp.toString());
                CallParameter callParameter = toCallParameter(byteArray);
                
                if (callParameter != null) {
                    HistoryItem historyItem = new HistoryItem(date, callParameter);
                    list.add(historyItem);
                }
            }
            return list;            

        } finally {
            DataConnection.closeConnection(connection);
            DataConnection.closeStatement(statement);
        }
    }
    
    public static void clearHistory() throws Exception {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = DataConnection.getConnection();
            statement = connection.prepareStatement(
                    "delete servicelog.service_tester_history where client_ip=?");
            
            String remoteAddr = getRemoteAddress();
            
            statement.setString(1, remoteAddr);
            statement.execute();
            
        } finally {
            DataConnection.closeConnection(connection);
            DataConnection.closeStatement(statement);
        }
    }
    
    public static String formatDate(Date date) {
        return dateFormatter.get().format(date);
    }

}
