import SpringDataExample.SpringDataFirstPlay.CustomModel;
import com.ibm.as400.access.AS400JDBCDataSource;
import org.testng.collections.Lists;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class jdbcAS400Connection {

   public static void main(String[] args) throws SQLException {
//***************************************************************************************************
       HashMap<String, List<CustomModel>> o = new HashMap<>();
       final String SERVER_NAME = "SERVER_NAME";
       AS400JDBCDataSource dataSource = new AS400JDBCDataSource(SERVER_NAME);
       dataSource.setNaming("System");
       dataSource.setUser("user");
       dataSource.setPassword("password");

//*****************************************************************************************************
       Connection connection = dataSource.getConnection();
       Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
       ResultSet resultSet = statement.executeQuery("SELECT * FROM library/table");
       ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

       int columnCount = resultSetMetaData.getColumnCount();

       //uzupelniamy klucze = nazwy kolumn
       for(int a = 1; a < columnCount; a++){
           String columnLabel = resultSetMetaData.getColumnLabel(a);
           o.put(columnLabel, Lists.newArrayList());
           while(resultSet.next()) {
               o.get(columnLabel).add(
                       new CustomModel(resultSet.getString(columnLabel),
                                       resultSetMetaData.getColumnTypeName(a),
                               resultSetMetaData.getColumnDisplaySize(a)));
           }
           resultSet.beforeFirst();
       }

       System.out.println(String.format("rozmiar hashMapy: %d elementow.",o.size()));

       for(Map.Entry<String, List<CustomModel>> e : o.entrySet()){
           System.out.println(String.format("%nKlucz: %s.",e.getKey()));
           e.getValue()
                   .stream()
                   .forEach(value -> System.out.print(
                           String.format("%s (%s, %d), "
                                   ,value.getRowValue()
                                   ,value.getRowType()
                                   ,value.getRowLength()
                           )));
       }


//*****************************************************************************************************
       resultSet.close();
       statement.close();
       connection.close();

       if(connection.isClosed()){
           System.out.println("%nPolaczenie zamkniete.");
       }
   }
}
