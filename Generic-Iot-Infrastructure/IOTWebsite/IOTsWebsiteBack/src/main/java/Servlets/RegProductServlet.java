package Servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import gdbm.AdminDBSingleton;
import gdbm.GDBM;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/product")
public class RegProductServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final GDBM adminDB = AdminDBSingleton.getInstance();
    private final URL url = new URL("http://localhost:8090/iots");

    public RegProductServlet() throws MalformedURLException {
        //this.url = new URL("http://localhost:8090/iots");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();

        Type mapType = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> jsonMap = gson.fromJson(reader, mapType);

        System.out.println("In Post");

        try {
            sendProductInfo(jsonMap);
        } catch (SQLException e) {
            System.out.println("SQL exeption thrown");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        sendRegProduct(jsonMap);
    }

    private void sendProductInfo(Map<String, String> jsonMap) throws SQLException {
        //Insert record to Products table
        adminDB.insertRecord("Products", getProductTableArgs(jsonMap));
        System.out.println("After insert to Products table");
    }

    private void sendRegProduct(Map<String, String> jsonMap) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String jsonInput = getJsonOfRegProduct(jsonMap);
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInput.getBytes("utf-8");
            os.write(input, 0, input.length);

            System.out.println("Req sent to gateway server");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            System.out.println(response.toString());
        }
    }

    private List<String> getProductTableArgs(Map<String, String> jsonMap) {
        List<String> queryArgs = new ArrayList<>();

        queryArgs.add("\"" + jsonMap.get("company_id") + "\"");
        queryArgs.add("\"" + jsonMap.get("product_name") + "\"");
        queryArgs.add("\"" + jsonMap.get("product_id") + "\"");

        return queryArgs;
    }

    private String getJsonOfRegProduct(Map<String, String> jsonMap) {
        String jsonInput = "{ \"command\": \"RegProduct\", \n" +
                "    \"args\": {\n" +
                "    \"ProductName\": \"Iphone\"\n" +
                "    }\n" +
                "}";

        return jsonInput;
    }
}
