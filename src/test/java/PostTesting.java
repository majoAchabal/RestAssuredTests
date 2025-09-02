import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;

public class PostTesting {

    @Test
    public void postBooking_HappyPath() throws JsonProcessingException {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";

        LocalDate checkin = LocalDate.now().plusDays(3);
        LocalDate checkout = checkin.plusDays(2);

        BookingDates dates = new BookingDates();
        dates.setCheckin(checkin.toString());
        dates.setCheckout(checkout.toString());


        Booked booked = new Booked();
        booked.setFirstname("Jorge");
        booked.setLastname("Perez");
        booked.setTotalprice(600);
        booked.setDepositpaid(true);
        booked.setBookingdates(dates);
        booked.setAdditionalneeds("Breakfast");

        ObjectMapper mapper = new ObjectMapper();
        String payload = mapper.writeValueAsString(booked);
        System.out.println(payload);

        Response response = RestAssured.given().contentType(ContentType.JSON).accept(ContentType.JSON).body(payload)
                .when().post("/booking");


        response.then().statusCode(200)
                .body("$", hasKey("bookingid"))
                .body("booking.firstname", equalTo("Jorge"))
                .body("booking.lastname", equalTo("Perez"))
                .body("booking.totalprice", equalTo(600))
                .body("booking.depositpaid", equalTo(true))
                .body("booking.bookingdates.checkin", equalTo(checkin.toString()))
                .body("booking.bookingdates.checkout", equalTo(checkout.toString()))
                .body("booking.additionalneeds", equalTo("Breakfast"))
                .log().body();
    }

    @Test
    public void postBooking_WithNulls() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";

        Booked booked = new Booked();
        booked.setFirstname(null);
        booked.setLastname(null);
        booked.setTotalprice(0);
        booked.setDepositpaid(true);
        booked.setBookingdates(null);
        booked.setAdditionalneeds(null);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(booked)
                .post("/booking");

        response.then().statusCode(anyOf(equalTo(400), equalTo(500)))
                .log().body();
    }

    @Test
    public void postBooking_WithIncompleteData() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";

        Booked booked = new Booked();
        booked.setFirstname("Maria");
        // booked.setLastname("Lopez");
        booked.setTotalprice(450);
        booked.setDepositpaid(true);
        // booked.setBookingdates(new BookingDates());
        // booked.setAdditionalneeds("Breakfast");

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(booked)
                .post("/booking");

        response.then().statusCode(anyOf(equalTo(400), equalTo(500)))
                .log().body();
    }

    @Test
    public void postBooking_WithInvalidDataTypes_ShouldReturnError() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";

        Map<String, Object> booking = new HashMap<>();
        booking.put("firstname", 123); //String
        booking.put("lastname", true); //String
        booking.put("totalprice", "quinientos"); // int
        booking.put("depositpaid", "sí"); // boolean
        booking.put("additionalneeds", 999); // String

        Map<String, Object> dates = new HashMap<>();
        dates.put("checkin", 20250910); //String con formato fecha
        dates.put("checkout", false);   //String con formato fecha
        booking.put("bookingdates", dates);

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(booking)
                .post("/booking");

        response.then()
                .statusCode(400) // Esperamos que la API rechace la solicitud
                .log().body();
    }



}