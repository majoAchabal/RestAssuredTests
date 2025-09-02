import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasKey;

public class GetTesting {
    @Test
    public void getBooking_HappyPath() throws JsonProcessingException {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";

        Response response = RestAssured.given()
                .pathParam("id", 2)
                .when().get("/booking/{id}");

        response.then().assertThat().statusCode(200);
        response.then().log().body();

        // Propiedades esperadas
        response.then().assertThat().body("$", hasKey("firstname"));
        response.then().assertThat().body("$", hasKey("lastname"));
        response.then().assertThat().body("$", hasKey("totalprice"));
        response.then().assertThat().body("$", hasKey("depositpaid"));
        response.then().assertThat().body("$", hasKey("bookingdates"));
        response.then().assertThat().body("bookingdates", hasKey("checkin"));
        response.then().assertThat().body("bookingdates", hasKey("checkout"));
    }

    @Test
    public void getBooking_ByInvalidId_Returns404() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";

        Response response = RestAssured.given()
                .pathParam("id", 999_999_999)
                .when().get("/booking/{id}");

        response.then().assertThat().statusCode(404);
        response.then().assertThat().body(Matchers.equalTo("Not Found"));
        response.then().log().all();
    }
}
