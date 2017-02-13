
import java.sql.*;

public class TestConnection {

    public static void main(String[] args){
        try{
            //load the Microsoft Access Database driver
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver"); //returns the DB class
            System.out.println("Driver loaded.");

            //establish a connection
            Connection connection = DriverManager.getConnection("jdbc:odbc:SampleStudents");
            System.out.println("Connection established.");

            //create a statement using the connection
            Statement statement = connection.createStatement();

            //create a result set to store the query executed by statement
            ResultSet resultSet = statement.executeQuery("select * from Students");

            //retrieve data from the result set
            while(resultSet.next()){
                for(int i = 1; i < 5; i++){
                    System.out.print(resultSet.getString(i)+"\t");
                }
                System.out.println();
            }

            //close the connection
            connection.close();

        } catch(Exception ex){
        	System.err.println("Error connecting to database.");
            System.err.println(ex);
        }
    }

}
