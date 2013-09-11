package org.aap.monitoring;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SQLManager {

    Connection con = null;
    SolrManager solrManager;
    String url = "jdbc:mysql://66.175.223.5:3306/AAP";
    String user = "root";
    String password = "aapmysql00t";

    public SQLManager(SolrManager solrManager) throws SQLException {
        this.solrManager = solrManager;
    }

    
    //yy-mm-dd
    public void triggerIndexer(String dateString) throws SQLException {
    	if(con == null){
    		getConn();
    	}
        ResultSet rs = null;
        Statement st = null;
        try {
        	String query = "SELECT * from ARTICLE_TBL where publishedDate >" + dateString + ";";
        	System.out.println(query);
            st = con.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {
                this.solrManager.insertDocument(rs);
            }
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(this.getClass().getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(this.getClass().getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }
    
    public void getConn() throws SQLException {
    	con = DriverManager.getConnection(url, user, password);
    }
    public void closeConn() throws SQLException{
    	if (con != null) {
            con.close();
        }
    }
}
