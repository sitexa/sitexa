
import com.healthmarketscience.jackcess.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by open on 27/05/2017. hehe
 */

public class Jackcess {

    public static void main(String[] args) throws IOException {
        Jackcess.shequ();
    }

    private static void tables() throws IOException {
        Database db = DatabaseBuilder.open(new File("/Users/open/IdeaProjects/sitexa/src/sqk.mdb"));
        Set tables = db.getTableNames();
        Iterator it = tables.iterator();
        while (it.hasNext()) {
            String table1 = (String) it.next();
            System.out.println("table = " + table1);
            cols(table1);
        }
    }


    private static void cols(String tname) throws IOException {
        Database db = DatabaseBuilder.open(new File("/Users/open/IdeaProjects/sitexa/src/sqk.mdb"));
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
        Database db = DatabaseBuilder.open(new File("/Users/open/IdeaProjects/sitexa/src/sqk.mdb"));
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
        Database db = DatabaseBuilder.open(new File("/Users/open/IdeaProjects/sitexa/src/sqk.mdb"));
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
}
