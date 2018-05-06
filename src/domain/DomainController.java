package domain;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.sun.management.OperatingSystemMXBean;
import java.io.FileNotFoundException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.sql.SQLWarning;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class DomainController {

    private Database database;
    private List<Query> queries;
    private Connection connection;
    // private IDbConnection connection;
    private OperatingSystemMXBean osBean;
    private static final Logger LOGGER = Logger.getLogger(DomainController.class.getName());
    private final ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
    private int mSSQLamountOfExecutedQueries50 = 0;
    private int mySQLamountOfExecutedQueries50 = 0;
    private int mSSQLamountOfExecutedQueries = 0;
    private int mySQLamountOfExecutedQueries = 0;
    private String csvHeader = "DBMS-duration;JavaSystem-Duration;DBMS-cpuDurationUser;JavaCPU-Duration\n";
    private float totalCpuUser = 0;
    private float totalCpuSystem = 0;
    private float duration = 0;
    long startTime = 0;
    long stopTime = 0;
    long elapsedTime = 0;
    long elapsedCpuTime = 0;

    public DomainController() {
        queries = new ArrayList<>();
        osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        initialiseQueries();
    }

    public ObservableList<Query> getQueries() {
        return FXCollections.observableArrayList(queries);
    }

    private void initialiseQueries() {
        queries.add(new Query("Query 0", "This is a very simple query whose task is to retrieve rows without any conditions or joins",
                "SELECT * FROM Category"));
        queries.add(new Query("Query 1", "This is a very simple query whose task is to retrieve rows without any conditions or joins",
                "SELECT * FROM Item"));
        queries.add(new Query("Query 2", "This query employs the use of sophisticated conditions conjoined with logical operators",
                "SELECT * FROM InvoiceWHERE   Invoice.in_id   >   50   AND   Invoice.in_date   >   1/1/2006   AND   Invoice.in_date  "
                + " <   1/1/2007   AND Invoice.in_description  LIKE  '%ohp%'  AND  Invoice.in_totalinletter  LIKE"
                + "  '%USD'  AND  Invoice.in_total  = Invoice.in_totalafterdiscount AND Invoice.in_total <> 100 OR NOT"
                + " Invoice.in_cu_id >=5 AND Invoice.in_id BETWEEN 1  AND 10000 OR  Invoice.in_id > 49+1  "
                + "ANDInvoice.in_total+33 <> 5  AND  Invoice.in_total = -Invoice.in_totalafterdiscount * 2 ;"));
        queries.add(new Query("Query 3", "This query is used to test the join operation between different tables", "SELECT Customer.cu_id , Invoice.in_id , InvoiceDetail.ind_qty , Item.it_serialnumber , Movement.mo_description , Movement_Details.mo_it_id , Users.us_id , Users.us_code , PurchaseOrder.po_description , Supplier.su_name FROM Customer , Invoice , InvoiceDetail , Item , Movement , Movement_Details , Users , PurchaseOrder , SupplierWHERE Supplier.su_name = \"Mike\" AND Customer.cu_id = Invoice.in_cu_id AND InvoiceDetail.ind_in_id = Invoice.in_id     AND     InvoiceDetail.ind_it_id     =     Item.it_id     AND     Movement_Details.mod_mo_id     = Movement.mo_id  AND Movement.mo_us_id =  Users.us_id  AND PurchaseOrder.po_us_id =Users.us_id  AND PurchaseOrder.po_us_id = Users.us_id AND PurchaseOrder.po_su_id AND Supplier.su_id ;"));
        queries.add(new Query("Query 4", "This query is used to test the sorting operation foreach DBMS", "SELECT     Customer.cu_id ,     Customer.cu_name     ,     Customer.cu_telephone     ,     Customer.cu_fax     , Customer.cu_email    FROM    CustomerORDER    BY    Customer.cu_id    , Customer.cu_name    DESC    , Customer.cu_telephone DESC, Customer.cu_fax , Customer.cu_email DESC ;"));
        queries.add(new Query("Query 5", "The purpose of this query is to test computational capabilities of the DBMSs by  executing different arithmetic functions", "SELECT SUM(Invoice.in_total) , AVG(Invoice.in_totalafterdiscount) , MAX(Invoice.in_total) , COUNT(Customer.cu_id)  ,  SUM(InvoiceDetail.ind_qty)  FROM  Customer  ,  Invoice  ,  InvoiceDetailWHERE Customer.cu_id = Invoice.in_cu_id AND Invoice.in_id = InvoiceDetail.ind_in_idGROUP BY Invoice.in_id ;"));
        queries.add(new Query("Query 6", "This query adds to the previous query conditions after the HAVING clause", "SELECT SUM(Invoice.in_total), AVG(Invoice.in_totalafterdiscount) , MAX(Invoice.in_total) , COUNT(Customer.cu_id) , SUM(InvoiceDetail.ind_qty) FROM Customer , Invoice , InvoiceDetailWHERE  Customer.cu_id  =   Invoice.in_cu_id   AND   Invoice.in_id  =   InvoiceDetail.ind_in_idGROUP  BY Invoice.in_id HAVING COUNT(Invoice.in_id)>0 AND SUM(Invoice.in_total) = AVG(Invoice,in_totalafterdiscount) ;"));
        queries.add(new Query("Query 7", "This query tests the capabilities of each DBMS when inner nested SELECTs is used", "SELECT   Customer.cu_name   FROM   CustomerWHERE   Customer.cu_name   =   (SELECT   Users.us_nameFROM  Users  WHERE  Users.us_class  =  \"administrator\")  AND  Customer.cu_fax  =  (SELECT  Supplier.su_fax FROM    Supplier    WHERE    Supplier.su_phone    =    \"123456\")    ANDCustomer.cu_email    =    (SELECT Supplier.su_email FROM Suppliers WHERE Supplier.su_address LIKE \"%h%\") ;"));
        queries.add(new Query("Query 8", "Now comes the ultimate test which will combine all previous queries into a single atomic SQL query", "SELECT Customer.cu_id , Invoice.in_id , InvoiceDetail.ind_qty , Item.it_serialnumber , Movement.mo_description , Movement_Details.mo_it_id , Users.us_id , Users.us_code , PurchaseOrder.po_description , Supplier.su_name , SUM(Invoice.in_total) , AVG(Invoice.in_totalafterdiscount) , MAX(Invoice.in_total), COUNT(Customer.cu_id) , SUM(InvoiceDetail.ind_qty)  FROM Customer , Invoice , InvoiceDetail , Item , Movement , Movement_Details , Users , PurchaseOrder , SupplierWHERE Invoice.in_id >  50  AND  Invoice.in_date  >  1/1/2006  AND  Invoice.in_date  <  1/1/2007  AND  Invoice.in_description  LIKE '%ohp%'  AND  Invoice.in_totalinletter LIKE  '%USD'  AND  Invoice.in_total  =  Invoice.in_totalafterdiscount AND  Invoice.in_total  <>  100  OR  NOT  Invoice.in_cu_id  >=5  AND  Invoice.in_id  BETWEEN  1  AND  10000 OR  Invoice.in_id  >  49+1AND  Customer.cu_name  =  (SELECT   Users.us_name  FROM  Users   WHERE Users.us_class = \"administrator\") AND Customer.cu_fax = (SELECT Supplier.su_fax FROM Supplier WHERE Supplier.su_phone  =  \"123456\")  AND  Customer.cu_id  =  Invoice.in_cu_id  AND  InvoiceDetail.ind_in_id  = Invoice.in_id     AND     InvoiceDetail.ind_it_id     =     Item.it_id     AND     Movement_Details.mod_mo_id     = Movement.mo_id  AND Movement.mo_us_id =  Users.us_id  AND PurchaseOrder.po_us_id =Users.us_id  AND PurchaseOrder.po_us_id  =  Users.us_id  AND  PurchaseOrder.po_su_id  AND  Supplier.su_id  ;ORDER  BY Customer.cu_id  ,  Customer.cu_name  DESC  ,  Invoice.in_id  DESC,  Users.us_name  , Invoice.in_descriptionDESC   ;GROUP   BY   Customer.cu_id   ,   Invoice.in_id   ,   InvoiceDetail.ind_qty   ,   Item.it_serialnumber   , Movement.mo_description , Movement_Details.mo_it_id , Users.us_id , Users.us_code , PurchaseOrder.po_description , Supplier.su_nameHAVING COUNT(Invoice.in_id)>0 AND SUM(Invoice.in_total) = AVG(Invoice,in_totalafterdiscount) ;"));
    }

    public void connectToDatabase(Database database, boolean windowsAuth, String serverName, String serverAddress, String username, String password)
            throws SQLException {
        this.database = database;
        if (database == Database.MySQL) {
            connectToMySQLDatabase(serverName, serverAddress, username, password);
        } else {
            connectToSQLServerDatabase(windowsAuth, serverName, serverAddress, username, password);
        }
    }

    public void disconnectFromDatabas() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DomainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * MySQL server connection
     *
     * @param serverName
     * @param serverAddress
     * @param username
     * @param password
     * @throws SQLException
     */
    private void connectToMySQLDatabase(String serverName, String serverAddress, String username, String password) throws SQLException {
        try {
            String connectionString = "jdbc:mysql://" + serverAddress + ":3306/" + serverName;
            connection = DriverManager.getConnection(connectionString, username, username);
        } catch (SQLException ex) {
            throw ex;
        }
    }

    /**
     * SQL Server connection
     *
     * @param windowsAuth - Windows authentication in database: true of false
     * @param databaseName
     * @param serverAddress
     * @param username
     * @param password
     * @throws SQLException
     */
    private void connectToSQLServerDatabase(boolean windowsAuth, String databaseName, String serverAddress, String username, String password) throws SQLException {
        String connectionUrl = String.format("jdbc:sqlserver://%s:1433;databaseName=%s%s",
                serverAddress, databaseName, windowsAuth ? ";integratedSecurity=true" : "");
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println(username);
            connection = username.isEmpty() ? DriverManager.getConnection(connectionUrl)
                    : DriverManager.getConnection(connectionUrl, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File makeCsvFileWithResults(Query query, int amount) {
        LOGGER.fine(query.getQueryString());
        File file = new File(database.name() + "_" + query.getName().replaceAll("\\s+", "") + "_" + amount + ".csv");
        try {
            while (!file.createNewFile()) {
                String fileName = file.getName();
                int amount2 = 0;
                if (database.name().equalsIgnoreCase("MySQL")) {
                    amount2 = amount == 50 ? ++mySQLamountOfExecutedQueries50 : ++mySQLamountOfExecutedQueries;
                } else {
                    amount2 = amount == 50 ? ++mSSQLamountOfExecutedQueries50 : ++mSSQLamountOfExecutedQueries;
                }
                file.renameTo(new File(fileName.substring(0, fileName.length() - 4) + "(" + amount2 + ")" + ".csv"));
            }
            file.setWritable(true);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return file;
    }

    public void executeMSSQLQuery(Query query, int amount) {
        File file = makeCsvFileWithResults(query, amount);
        try (Formatter format = new Formatter(file)) {
            format.format(csvHeader);

            for (int i = 0; i < amount; i++) {
                try {
                    Statement statement = connection.createStatement();
                    System.out.println(query.getQueryString());

                    ResultSet resultSet = statement.executeQuery("SET STATISTICS TIME ON " + query.getQueryString() + " SET STATISTICS TIME OFF");

//                    while (statement.getMoreResults()) {
                    //SQLWarning warning = statement.getWarnings();
                    //
                    //while (warning != null)
                    //{
                    //   System.out.println(warning.getMessage());
                    //   warning = warning.getNextWarning();
                    //  }
//                        Iterator<Throwable> iterator = statement.getWarnings().iterator();
//                        while (iterator.hasNext()) {
//                            System.out.println(iterator.next().getMessage());
//                        }
//                        System.out.println(statement.getWarnings().getMessage());
//                    }
                    //getting some data from the resultset
                    resultSet.next();
                    System.out.println(resultSet.getString(2));
                    //getting the cpu, ram and duration in ms from the "comments tab in mssql"
                    statement.getMoreResults();
                    String statistics = statement.getWarnings().getMessage();
                    System.out.printf("%s%n", statistics);

                    String durationString = statistics.substring(statistics.indexOf("elapsed"));
                    String cpuTimeString = statistics.substring(statistics.indexOf("CPU"), statistics.indexOf("elapsed"));
                    totalCpuUser = Integer.parseInt(cpuTimeString.replaceAll("[\\D]", ""));
                    duration = Integer.parseInt(durationString.replaceAll("[\\D]", ""));
                    System.out.printf("Duration in ms: %f%n", duration);
                    System.out.printf("CPU time in ms: %f%n", totalCpuUser);

                    //java system duration                     
                    startTime = System.nanoTime();
                    statement.executeQuery(query.getQueryString());
                    stopTime = System.currentTimeMillis();
                    elapsedTime = stopTime - startTime;

                    //java cpu duration
                    startTime = threadBean.getCurrentThreadCpuTime();
                    statement.executeQuery(query.getQueryString());
                    stopTime = threadBean.getCurrentThreadCpuTime();
                    elapsedCpuTime = stopTime - startTime;

                    format.format("%s;%s;%s;%s%n", duration, elapsedTime,
                            Float.toString(totalCpuUser), elapsedCpuTime);
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DomainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Using mysql show profile for the duration, cpu user & cpu system
     * Documentation: https://dev.mysql.com/doc/refman/5.5/en/show-profile.html
     *
     * @param query
     * @param amount
     */
    public void executeMySQLQuery(Query query, int amount) {
        File file = makeCsvFileWithResults(query, amount);
        try (Formatter format = new Formatter(file)) {
            format.format(csvHeader);
            for (int i = 0; i < amount; i++) {
                try {
                    //createstatement is niet query uitvoeren, enkel parsen etc. 
                    Statement statement = connection.createStatement();

                    //Profiling aanzetten, om de performance results van de query te kunnen bekijken.
                    statement.execute("SET PROFILING=1;");

                    //uitvoeren van query
                    startTime = System.nanoTime();
                    statement.executeQuery(query.getQueryString());
                    stopTime = System.currentTimeMillis();

                    elapsedTime = stopTime - startTime;

                    //ophalen van de duration van de query
                    ResultSet resultSet = statement.executeQuery("SHOW PROFILES;");
                    resultSet.next();

                    String queryNaam = resultSet.getString("Query");//temporary
                    duration = resultSet.getFloat("Duration");
                    String durationString = Float.toString(duration);
                    String Query_ID = Integer.toString(resultSet.getInt("Query_ID"));

                    LOGGER.log(Level.INFO, "QueryNaam: " + queryNaam);
                    LOGGER.log(Level.INFO, "Duration: " + duration);
                    LOGGER.log(Level.INFO, "Query_ID: " + Query_ID);

                    //ophalen van de CPU duration van de query
                    float totalCpuDuration = 0;//dit zou mogelijks == duration attribute uit "show profiles"

                    String cpuQuery = "SHOW PROFILE CPU FOR QUERY " + Query_ID + ";";
                    LOGGER.log(Level.INFO, cpuQuery);
                    ResultSet resultSetCPU = statement.executeQuery(cpuQuery);
                    while (/*!resultSetCPU.isClosed() &&*/resultSetCPU.next()) {
                        String CPU_status = resultSetCPU.getString("Status");
                        float cpuUser = resultSetCPU.getFloat("CPU_user");
                        String CPU_user = Float.toString(cpuUser);

                        float cpuSystem = resultSetCPU.getFloat("CPU_system");
                        String CPU_system = Float.toString(cpuSystem);

                        float cpuDuration = resultSetCPU.getFloat("Duration");
                        String CPUduration = Float.toString(cpuDuration);
                        System.out.printf("Duration: %f CPU User: %f CPU System: %f Status: %s%n",
                                cpuDuration, cpuUser, cpuSystem, CPU_status);

                        totalCpuDuration += cpuDuration;
                        totalCpuUser += cpuUser;
                        totalCpuSystem += cpuSystem;
                        System.out.printf("TOTAL Duration: %f CPU User: %f CPU System: %f%n", totalCpuDuration, totalCpuUser, totalCpuSystem);
                    }
                    statement.execute("SET PROFILING=0;");

                    System.out.printf("Duration via \" show profiles\": %s%n", duration);

                    //cpu duration 
                    startTime = threadBean.getCurrentThreadCpuTime();
                    statement.executeQuery(query.getQueryString());
                    stopTime = threadBean.getCurrentThreadCpuTime();
                    elapsedCpuTime = stopTime - startTime;

                    System.out.println(elapsedTime);
                    //formatting data naar .csv row
                    format.format("%s;%s;%s;%s%n", Float.toString(duration), elapsedTime, Float.toString(totalCpuUser), elapsedCpuTime);

                    //Profiling in MySQL uitzetten.
                } catch (SQLException ex) {
                    Logger.getLogger(DomainController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DomainController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
