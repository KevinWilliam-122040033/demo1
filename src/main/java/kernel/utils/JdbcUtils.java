package kernel.utils;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import kernel.models.Admin;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcUtils {
    private static final String USER_NAME = "root";
    private static final String PASSWORD = "sirlolipop13";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/newsSystem?allowPublicKeyRetrieval=true&useSSL=false&characterEncoding=utf-8";
    private Connection connection;
    private PreparedStatement pstmt;
    private ResultSet resultSet;

    public JdbcUtils() {
        try {
            Class.forName(DRIVER);
            System.out.println("DB connected");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得数据库的连接   obtain connection to database
     * @return Connection 数据库连接对象  object of DB connection
     */
    public Connection getConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 数据库数据的增加，删除，修改        addtion,deletion,modification of database data
     * @param sql 待填充参数的数据库查询语句      sql command awaits parameters
     * @param params 准备填充的参数列表          parameters to fill
     * @throws SQLException 数据库更新异常      exception of DB updates
     * @return 是否更新成功                     success or not
     */
    public boolean updateByPreparedStatement(String sql, List<Object> params) throws SQLException {
        int resultLineNumber = -1; // 如果行号小于零说明更新失败 if line number <0 means failed
        pstmt = connection.prepareStatement(sql);
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i+1, params.get(i));
            }
        }
        resultLineNumber = pstmt.executeUpdate();
        return resultLineNumber > 0;
    }

    /**
     * 查询单条数据库数据，并打包成字典的形式返回    query single DB data, return the data packed as dictionary
     * @param sql 待填充参数的数据库查询语句      sql command awaits filling with parameters
     * @param params 准备填充的参数列表          parameters to fill
     * @throws SQLException 数据库操作异常      exception of DB operations
     * @return 单个数据库信息的字典              queried info
     */
    public Map<String, Object> findSimpleResult(String sql, List<Object> params) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        pstmt = connection.prepareStatement(sql);
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i+1, params.get(i)); //这里要处理序号问题，所以第一个参数是i+1
            }
        }
        resultSet = pstmt.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData(); // 获取元数据,目的是获得行号
        int col = metaData.getColumnCount();
        while (resultSet.next()) {
            for (int i = 0; i < col; i++) {
                String colName = metaData.getColumnLabel(i+1);
                Object colValue = resultSet.getObject(colName);
                if (colValue == null) {
                    colValue = "";
                }
                map.put(colName, colValue);
            }
        }
        return map;
    }

    /**
     * 查询多条数据库数据，打包成字典，并放入列表中返回         query multiple lines and return them as a list of dicionaries
     * @param sql 待填充参数的数据库查询语句                 sql command awaits filling
     * @param params 准备填充的参数列表                      parameters to fill
     * @throws SQLException 数据库操作异常                  sql exception
     * @return 装有多组数据map的list                        list of dictionaries
     */
    public List<Map<String, Object>> findModeResult(String sql, List<Object> params) throws SQLException {
        List<Map<String, Object>> maps = new ArrayList<>();
        pstmt = connection.prepareStatement(sql);
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i+1, params.get(i));
            }
        }
        resultSet = pstmt.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int col = metaData.getColumnCount();
        while (resultSet.next()) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < col; i++) {
                String colName = metaData.getColumnLabel(i+1);
                Object colValue = resultSet.getObject(colName);
                if (colValue == null) {
                    colValue = "";
                }
                map.put(colName, colValue);
            }
            maps.add(map);
        }
        return maps;
    }

    /**
     * 通过反射机制查询单条数据库数据，处理完绑定后打包，并直接返回包装好的对象       using reflection to query single line and return packed object
     * @param sql 待填充参数的数据库查询语句                                 sql command awaits filling
     * @param params 带填充的参数列表                                      params to fill
     * @param cls 要打包的对象的类型                                       type of object to pack
     * @throws Exception 返回一个异常                                     exception of mysql
     * @return 直接返回包装好的数据                                         packed data
     */
    public <T> T findSimpleProResult(String sql, List<Object> params, Class<T> cls) throws Exception {
        T resultObject = null;
        pstmt = connection.prepareStatement(sql);
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i+1, params.get(i));
            }
        }
        resultSet = pstmt.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int col = metaData.getColumnCount();
        while (resultSet.next()) {

            resultObject = cls.newInstance(); // 创建一个该类的一个新实例

            // 对每项数据依次处理
            for (int i = 0; i < col; i++) {
                String colName = metaData.getColumnName(i+1);
                Object colValue = resultSet.getObject(colName);
                Object newValue = null;
                if (colValue == null) {
                    newValue = "";
                } else if (colValue.getClass() == Integer.class) {
                    newValue = new SimpleIntegerProperty(Integer.parseInt(colValue.toString()));
                } else if (colValue.getClass() == String.class) {
                    newValue = new SimpleStringProperty(colValue.toString());
                }
                Field field = cls.getDeclaredField(colName);
                field.setAccessible(true);
                field.set(resultObject, newValue);
            }
        }
        return resultObject;
    }

    /**
     * 通过反射机制查询多条数据库数据，处理双向绑定并将对象封装好后填如list中返回  using reflection to query multiple lines and return packed object in a list
     * @param sql 数据库查询语句                                         sql command awaits filling params
     * @param params 数据库查询参数                                      params to fill
     * @param cls 要打包的对象的类型                                      type of object to pack
     * @throws Exception 返回一个异常                                    mysql exception
     * @return 装有cls相应对象的列表                                      list of packed objects
     */
    public <T> List<T> findMoreProResult(String sql, List<Object> params, Class<T> cls) throws Exception {
        List<T> list = new ArrayList<>();
        pstmt = connection.prepareStatement(sql);
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i+1, params.get(i));
            }
        }

        resultSet = pstmt.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
//        System.out.println(metaData);
        int col = metaData.getColumnCount();
        while (resultSet.next()) {
            T resultObject = cls.newInstance();
            for (int i = 0; i < col; i++) {
                /*
                 *==================================================
                 *                 !!important!!
                 *     这里用getColumnLabel才能显示修改后的姓名
                 *     altered name will be shown after calling getColumnlabel
                 *
                 *     StackOverFlow大法好！！
                 *     HAIL the great StackOverFlow
                 *     https://stackoverflow.com/questions/6732736/rs-getmetadata-getcolumnnamei-with-aliased-columns-on-mysql
                 *==================================================
                 */
                String colName = metaData.getColumnLabel(i+1);
                Object colValue = resultSet.getObject(colName);
//                System.out.println(colName);
                Object newValue = null;
                if (colValue == null) {
                    colValue = "";
                } else if (colValue.getClass() == Integer.class) {
                    newValue = new SimpleIntegerProperty(Integer.parseInt(colValue.toString()));
                } else if (colValue.getClass() == String.class) {
                    newValue = new SimpleStringProperty(colValue.toString());
                }
                Field field = cls.getDeclaredField(colName);
                field.setAccessible(true);
                field.set(resultObject, newValue);
            }
            list.add(resultObject);
        }
        return list;
    }

    /**
     * 通过反射机制查询单条数据库数据，并直接返回包装好的对象using reflection to query single line and return packed object
     * @param sql 待填充参数的数据库查询语句                                 sql command awaits filling
     * @param params 带填充的参数列表                                      params to fill
     * @param cls 要打包的对象的类型                                       type of object to pack
     * @throws Exception 返回一个异常                                     exception of mysql
     * @return 直接返回包装好的数据                                         packed data
     */
    public <T> T findSimpleRefResult(String sql, List<Object> params, Class<T> cls) throws Exception {
        T resultObject = null;
        pstmt = connection.prepareStatement(sql);
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i+1, params.get(i));
            }
        }
        resultSet = pstmt.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int col = metaData.getColumnCount();
        while (resultSet.next()) {
            resultObject = cls.newInstance(); // 创建一个该类的一个新实例
            for (int i = 0; i < col; i++) {
                String colName = metaData.getColumnName(i+1);
                Object colValue = resultSet.getObject(colName);
                if (colValue == null) {
                    colValue = "";
                }
                Field field = cls.getDeclaredField(colName);
                field.setAccessible(true);
                field.set(resultObject, colValue);
            }
        }
        return resultObject;
    }

    /**
     * 通过反射机制查询多条数据库数据，将对象封装好后填如list中，并返回using reflection to query multiple lines and return packed object in a list
     * @param sql 数据库查询语句                                         sql command awaits filling params
     * @param params 数据库查询参数                                      params to fill
     * @param cls 要打包的对象的类型                                      type of object to pack
     * @throws Exception 返回一个异常                                    mysql exception
     * @return 装有cls相应对象的列表                                      list of packed objects
     */
    public <T> List<T> findMoreRefResult(String sql, List<Object> params, Class<T> cls) throws Exception {
        List<T> list = new ArrayList<>();
        pstmt = connection.prepareStatement(sql);
        if (params != null && !params.isEmpty()) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i+1, params.get(i));
            }
        }
        resultSet = pstmt.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int col = metaData.getColumnCount();
        while (resultSet.next()) {
            T resultObject = cls.newInstance();
            for (int i = 0; i < col; i++) {
                String colName = metaData.getColumnName(i+1);
                Object colValue = resultSet.getObject(colName);
                if (colValue == null) {
                    colValue = "";
                }
                Field field = cls.getDeclaredField(colName);
                field.setAccessible(true);
                field.set(resultObject, colValue);
            }
            list.add(resultObject);
        }
        return list;
    }

    /**
     * 释放数据库连接
     * release the DB connection
     */
    public void releaseConnection() {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 测试函数     testing function
     * @param args no args
     * @throws SQLException 抛出数据库异常     throw exception of DB
     */
    public static void main(String[] args) throws SQLException {
        JdbcUtils jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();

        // 利用反射查询单条记录
        // use reflection to query single line
        String sql = "select * from `admin` where username = ?";
        List<Object> params = new ArrayList<>();
        params.add("石佳欢");
        try {
            Admin admin = jdbcUtils.findSimpleRefResult(sql, params, Admin.class);
            System.out.println(jdbcUtils.findSimpleRefResult(sql, params, Admin.class));
            System.out.println(admin.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
