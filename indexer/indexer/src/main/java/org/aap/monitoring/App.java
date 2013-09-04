package org.aap.monitoring;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import sun.misc.Version;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        String url = "jdbc:mysql://in-cia-dev00.in.walmartlabs.com:3306/hjp";
        String user = "hjp";
        String password = "";

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery("SELECT * from affiliatepl;");
            processSqlResultItems(rs);

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Version.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Logger lgr = Logger.getLogger(Version.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

    static void processSqlResultItems(ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            getObjectItem(resultSet);
        }
    }

    static void getObjectItem(ResultSet resultSet) throws SQLException {
        System.out.println(resultSet.getString("date"));
        System.out.println(resultSet.getDouble("revenue"));
    }
}
