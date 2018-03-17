import com.healthmarketscience.jackcess.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by open on 27/05/2017. hehe
 */

public class Jackcess {

    private static String path = "/Users/open/Data/sqk.mdb";

    public static void main(String[] args) throws Exception {
        Jackcess.insertDb();
    }

    private static void tables() throws IOException {
        Database db = DatabaseBuilder.open(new File(path));
        Set tables = db.getTableNames();
        Iterator it = tables.iterator();
        while (it.hasNext()) {
            String table1 = (String) it.next();
            System.out.println("table = " + table1);
            cols(table1);
        }
    }


    private static void cols(String tname) throws IOException {
        Database db = DatabaseBuilder.open(new File(path));
        Table table = db.getTable(tname);
        List cols = table.getColumns();
        for (int i = 0; i < cols.size(); i++) {
            Column col = (Column) cols.get(i);
            String colname = col.getName();
            DataType coltype = col.getType();
            System.out.println("colname = " + colname + " coltype =" + coltype.name());
        }
    }

    /**
     * table:sys_prov_city_area_street
     * column:id,long;code,long;parentId,long;name,memo;level,byte
     *
     * @throws IOException
     */
    private static void shequ() throws IOException {
        Database db = DatabaseBuilder.open(new File(path));
        Table table = db.getTable("sys_prov_city_area_street");
        int count1 = 0;
        StringBuilder insertString = new StringBuilder();
        for (Row row : table) {
            count1++;
            String sql = "insert into Sites(id,code,parent_id,name,level) values(";
            long id = row.getInt("id");
            long code = row.getInt("code");
            long parentId = row.getInt("parentId");
            String name = row.getString("name");
            int level = row.getByte("level");
            sql += id + "," + code + "," + parentId + ",'" + name + "'," + level + ");";
            insertString.append(sql);
        }
        db.close();
        System.out.println("count1 = " + count1);
        File f = new File("data/insert_Sites.sql");
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(insertString.toString().getBytes());
        fos.close();
    }

    /**
     * Post: id,long;postnumber,text;provice,memo;city,memo;district,memo;address,memo;jd,memo;
     *
     * @throws IOException
     */
    private static void post() throws IOException {
        Database db = DatabaseBuilder.open(new File(path));
        Table table = db.getTable("POST");
        int count2 = 0;
        File f = new File("data/insert_Posts.sql");
        FileOutputStream fos = new FileOutputStream(f);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        for (Row row : table) {
            count2++;
            String sql = "insert into Posts(id,post_number,province,city,district,address,jd) values(";
            long id = row.getInt("id");
            String post_number = row.getString("PostNumber");
            String province = row.getString("Province");
            String city = row.getString("City");
            String address = row.getString("Address");
            String district = row.getString("District");
            String jd = row.getString("jd");
            sql += id + ",'" + post_number + "','" + province + "','" + city + "','" + district + "'" + address + "','" + jd + "');";
            bos.write(sql.getBytes());
        }
        db.close();
        System.out.println("count2 = " + count2);
        bos.close();
    }

    /**
     * 184万条记录，耗时182秒。速度惊人！
     * @throws Exception
     */
    private static void insertDb() throws Exception {
        Database db = DatabaseBuilder.open(new File(path));
        Table table = db.getTable("POST");
        Iterator it = table.iterator();

        long begin = new Date().getTime();
        String prefix = "insert into Posts(id,post_number,province,city,district,address,jd) values ";

        for (int i = 0; i < 200; i++) {
            StringBuilder suffix = new StringBuilder("");
            int count = 0;
            while (it.hasNext() && count++ < 10000) {
                Row row = (Row) it.next();
                long id = row.getInt("id");
                String post_number = row.getString("PostNumber");
                String province = row.getString("Province");
                String city = row.getString("City");
                String address = row.getString("Address");
                String district = row.getString("District");
                String jd = row.getString("jd");
                suffix.append("(").append(id).append(",'").append(post_number).append("','")
                        .append(province).append("','").append(city).append("','")
                        .append(address).append("','").append(district).append("','")
                        .append(jd).append("'),");
            }

            System.out.println("count: i = " + i);

            if (suffix.length() > 0) {
                String sql = prefix + suffix.substring(0, suffix.length() - 1);
                Connection conn = getConn();
                if (conn != null) {
                    conn.setAutoCommit(false);
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.addBatch();
                    pstmt.executeBatch();
                    conn.commit();
                    pstmt.close();
                    conn.close();
                }
            }
        }

        db.close();
        //bos.close();

        long end = new Date().getTime();
        System.out.println("cast : " + (end - begin) / 1000 + "s");
        //cast : 182s; rows : 1844219
    }

    private static Connection getConn() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            String url = "jdbc:mysql://192.168.2.108:3306/sitexa?user=root&password=pop007";
            return DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void testConn() throws SQLException {
        Connection conn = getConn();
        System.out.println("conn.getCatalog() = " + conn.getCatalog());
    }

}
