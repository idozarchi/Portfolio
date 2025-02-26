package gdbm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminDB {
    public static GDBM initAdminDB() throws SQLException {
        GDBM gdbm = new GDBM("AdminDB", "jdbc:mysql://localhost:3306", "root", "12345");
        createCompaniesTable(gdbm);
        createContactsTable(gdbm);
        createProductsTable(gdbm);

        return gdbm;
    }

    public static void createCompaniesTable(GDBM gdbm) throws SQLException {
        List<Map.Entry<GDBM.DataTypes, String>> values = new ArrayList<>();
        List<String> varcharSizes = new ArrayList<>();

        values.add(new DBEntry(GDBM.DataTypes.VARCHAR, "CompanyID"));
        values.add(new DBEntry(GDBM.DataTypes.VARCHAR, "CompanyName"));
        values.add(new DBEntry(GDBM.DataTypes.VARCHAR, "CompanyAddress"));
        values.add(new DBEntry(GDBM.DataTypes.VARCHAR, "CompanySubscription"));

        varcharSizes.add("16");
        varcharSizes.add("32");
        varcharSizes.add("64");
        varcharSizes.add("16");

        gdbm.createTable("Companies", values, "CompanyID", varcharSizes);
    }

    public static void createContactsTable(GDBM gdbm) throws SQLException {
        List<Map.Entry<GDBM.DataTypes, String>> values = new ArrayList<>();
        List<String> varcharSizes = new ArrayList<>();

        values.add(new DBEntry(GDBM.DataTypes.VARCHAR, "CompanyID"));
        values.add(new DBEntry(GDBM.DataTypes.VARCHAR, "ContactID"));
        values.add(new DBEntry(GDBM.DataTypes.VARCHAR, "ContactName"));
        values.add(new DBEntry(GDBM.DataTypes.VARCHAR, "ContactEmail"));
        values.add(new DBEntry(GDBM.DataTypes.VARCHAR, "ContactPhoneNumber"));

        varcharSizes.add("16");
        varcharSizes.add("16");
        varcharSizes.add("32");
        varcharSizes.add("64");
        varcharSizes.add("16");

        gdbm.createTable("Contacts", values, "ContactID", varcharSizes, "Companies", "CompanyID");
    }

    public static void createProductsTable(GDBM gdbm) throws SQLException {
        List<Map.Entry<GDBM.DataTypes, String>> values = new ArrayList<>();
        List<String> varcharSizes = new ArrayList<>();

        values.add(new DBEntry(GDBM.DataTypes.VARCHAR, "CompanyID"));
        values.add(new DBEntry(GDBM.DataTypes.VARCHAR, "ProductName"));
        values.add(new DBEntry(GDBM.DataTypes.VARCHAR, "ProductID"));

        varcharSizes.add("16");
        varcharSizes.add("32");
        varcharSizes.add("16");

        gdbm.createTable("Products", values, "ProductID", varcharSizes, "Companies", "CompanyID");
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
