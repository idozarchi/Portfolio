package gdbm;

import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestGDBM {

    static GDBM gdbm;

    @Test
    public void testInsertDelete() throws SQLException {
        GDBM gdbm = new GDBM("TestGDBM", "jdbc:mysql://localhost:3306", "root", "12345");
        List<Map.Entry<GDBM.DataTypes, String>> list = new ArrayList<>();
        List<List<String>> rs;
        List<String> list2 = new ArrayList<>();
        list2.add("50");

        list.add(new DBEntry(GDBM.DataTypes.INTEGER, "ID"));
        list.add(new DBEntry(GDBM.DataTypes.VARCHAR, "Name"));
        list.add(new DBEntry(GDBM.DataTypes.BOOLEAN, "IsValid"));

        gdbm.createTable("Products", list, "", list2);

        List<String> addRecordlist = new ArrayList<>();
        addRecordlist.add("7");
        addRecordlist.add("'p2'");
        addRecordlist.add("0");

        gdbm.insertRecord("Products", addRecordlist);
        gdbm.deleteRecords("Products");

        addRecordlist.clear();
        addRecordlist.add("8");
        addRecordlist.add("'p3'");
        addRecordlist.add("0");
        gdbm.insertRecord("Products", addRecordlist);

        gdbm.updateRecord("Products", "ID", "8", "80");

        rs = gdbm.getRecords("Products", "ID", "80");
        for (List<String> record : rs) {
            System.out.println("ID: " + record.get(0));
        }

        gdbm.deleteRecords("Products", "Name", "0");

        for(int i = 0; i < 10; ++i){
            addRecordlist.clear();
            addRecordlist.add("" + i);
            addRecordlist.add("'name: " + i + "'");
            addRecordlist.add("" + 0);
            gdbm.insertRecord("Products", addRecordlist);
        }

        rs = gdbm.getRecords("Products");
        System.out.println("Num of records is: " + rs.size());

        for (List<String> record : rs) {
            System.out.println("ID: " + record.get(0));
            System.out.println("Name: " + record.get(1));
        }
        gdbm.deleteRecords("Products");

        for(int i = 0; i < 10; ++i){
            addRecordlist.clear();
            addRecordlist.add("" + i);
            addRecordlist.add("'name: " + i + "'");
            addRecordlist.add("" + 0);
            gdbm.insertRecord("Products", addRecordlist);
        }

        rs = gdbm.getRecords("Products", "ID");
        for (List<String> record : rs) {
            System.out.println("ID: " + record.get(0));
        }
        gdbm.deleteRecords("Products");

        for(int i = 0; i < 10; ++i){
            addRecordlist.clear();
            addRecordlist.add("" + i);
            addRecordlist.add("'name: " + i + "'");
            addRecordlist.add("" + 0);
            gdbm.insertRecord("Products", addRecordlist);
        }

        rs = gdbm.getRecords("Products", "ID", "5");
        for (List<String> record : rs) {
            System.out.println("ID: " + record.get(0));
        }

        gdbm.deleteRecords("Products");
    }











    public static void main(String[] args) throws SQLException {
        GDBM gdbm = new GDBM("TestGDBM", "jdbc:mysql://localhost:3306", "root", "12345");
        List<Map.Entry<GDBM.DataTypes, String>> list = new ArrayList<>();
        List<List<String>> rs;
        List<String> list2 = new ArrayList<>();
        list2.add("50");

        list.add(new DBEntry(GDBM.DataTypes.INTEGER, "ID"));
        list.add(new DBEntry(GDBM.DataTypes.VARCHAR, "Name"));
        list.add(new DBEntry(GDBM.DataTypes.BOOLEAN, "IsValid"));

        gdbm.createTable("Products", list, "", list2);
        gdbm.createTable("Products2", list, "", list2, "Products", "ID");
    }

    private static class DBEntry implements Map.Entry<GDBM.DataTypes, String> {
        private final GDBM.DataTypes type;
        private String name;

        public DBEntry(GDBM.DataTypes type, String name) {
            this.type = type;
            this.name = name;
        }


        @Override
        public GDBM.DataTypes getKey() {
            return type;
        }

        @Override
        public String getValue() {
            return name;
        }

        @Override
        public String setValue(String s) {
            String res = name;
            name = s;

            return res;
        }
    }
}
