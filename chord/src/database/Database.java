package database;

import java.io.IOException;
import java.sql.*;

import utils.Utils;

public class Database {

  // define the driver to use 
     private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
  // the database name  
     private String dbName="localDB";
  // define the Derby connection URL to use 
     private String connectionURL = "jdbc:derby:" + dbName + ";create=true";
     
     private String initScript = "initDB.sql";
     
     private Connection conn = null;
     
     
     public Database(){
    	 connect();
    	 loadDB();
     }

     
     public void connect() {
    		 try {
				conn = DriverManager.getConnection(connectionURL);
				System.out.println("Connected to database " + dbName);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  	
	}
     
    public void closeConnection() {
    	if (driver.equals("org.apache.derby.jdbc.EmbeddedDriver")) {
            boolean gotSQLExc = false;
            try {
               DriverManager.getConnection("jdbc:derby:;shutdown=true");
            } catch (SQLException se)  {	
               if ( se.getSQLState().equals("XJ015") ) {		
                  gotSQLExc = true;
               }
            }
            if (!gotSQLExc) {
            	  System.out.println("Database did not shut down normally");
            }  else  {
               System.out.println("Database shut down normally");	
            }  
         }
    }
     
    public void loadDB() {
    	try {
			String sqlScript = Utils.readFile(initScript);
			runScript(sqlScript);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
     
    private void runScript(String sql) {
    	try {
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
    }
}
