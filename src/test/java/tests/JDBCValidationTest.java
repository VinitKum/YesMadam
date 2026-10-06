package tests;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;
public class JDBCValidationTest {


    // ---------------------------------------------------------
    // @BeforeClass
    // ---------------------------------------------------------
    // Ye method test class ke tests run hone se pehle ek baar chalega.
    // Yahan hum test database aur test data prepare kar rahe hain.
    // ---------------------------------------------------------

    @BeforeClass
    public void setup() throws Exception {

        DatabaseUtils.setupDatabase();
    }


    // ---------------------------------------------------------
    // Actual test
    // ---------------------------------------------------------

    @Test
    public void validateApiAgainstDatabase() throws Exception {


        // =====================================================
        // STEP 1: API se order data retrieve karna
        // =====================================================

        Response response =
                given()

                        // API ka base URL
                        .baseUri("http://localhost:3001")

                        .when()

                        // Order 1001 ko retrieve kar rahe hain
                        .get("/orders/1001")

                        .then()

                        // API successful honi chahiye
                        .statusCode(200)

                        // Response ko Response object mein store karna
                        .extract()
                        .response();


        // =====================================================
        // STEP 2: API response se required fields extract karna
        // =====================================================

        String apiId =
                response.jsonPath()
                        .getString("id");

        int apiCustomerId =
                response.jsonPath()
                        .getInt("customerId");

        String apiStatus =
                response.jsonPath()
                        .getString("status");


        // API se values console mein print kar rahe hain
        System.out.println(
                "API ID       = " + apiId
        );

        System.out.println(
                "API Customer = " + apiCustomerId
        );

        System.out.println(
                "API Status   = " + apiStatus
        );


        // =====================================================
        // STEP 3: Database connection establish karna
        // =====================================================

        Connection connection =
                DatabaseUtils.getConnection();


        // =====================================================
        // STEP 4: SQL query prepare karna
        // =====================================================

        // '?' ek parameter placeholder hai.
        //
        // Is approach ko PreparedStatement kehte hain.
        String sql = """
                SELECT id, customer_id, status
                FROM orders
                WHERE id = ?
                """;


        // PreparedStatement create kar rahe hain
        PreparedStatement preparedStatement =
                connection.prepareStatement(sql);


        // '?' ki jagah API se aayi hui order ID set kar rahe hain
        //
        // Index 1 = first '?'
        preparedStatement.setString(1, apiId);


        // =====================================================
        // STEP 5: SQL query execute karna
        // =====================================================

        ResultSet resultSet =
                preparedStatement.executeQuery();


        // =====================================================
        // STEP 6: Database result read karna
        // =====================================================

        // ResultSet initially first row se pehle hota hai.
        // next() call karne par first matching row par move karta hai.
        resultSet.next();
        String dbId =
                resultSet.getString("id");

        int dbCustomerId =
                resultSet.getInt("customer_id");

        String dbStatus =
                resultSet.getString("status");


        // DB values print kar rahe hain
        System.out.println(
                "DB ID        = " + dbId
        );

        System.out.println(
                "DB Customer  = " + dbCustomerId
        );

        System.out.println(
                "DB Status    = " + dbStatus
        );


        // =====================================================
        // STEP 7: API data vs DB data compare karna
        // =====================================================

        // API order ID aur DB order ID same hone chahiye
        assertEquals(
                apiId,
                dbId,
                "API ID and DB ID do not match"
        );


        // API customer ID aur DB customer ID same hone chahiye
        assertEquals(
                apiCustomerId,
                dbCustomerId,
                "API Customer ID and DB Customer ID do not match"
        );


        // API status aur DB status same hona chahiye
        assertEquals(
                apiStatus,
                dbStatus,
                "API Status and DB Status do not match"
        );


        // =====================================================
        // STEP 8: Resources close karna
        // =====================================================

        resultSet.close();
        preparedStatement.close();
        connection.close();
    }
}