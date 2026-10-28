package com.framework.api;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Tests against the public Restful-Booker practice API. Note the API has a few quirks that the
 * tests deliberately encode: bad credentials return 200 with a "reason", and DELETE returns 201.
 */
@Epic("Booking API")
@Feature("Bookings")
public class BookingApiTests extends BaseApiTest {

    @Test(groups = {"smoke", "api"})
    @Description("Valid credentials return an auth token")
    public void auth_withValidCredentials_returnsToken() {
        given().spec(spec)
                .body(java.util.Map.of("username", "admin", "password", "password123"))
                .when().post("/auth")
                .then().statusCode(200)
                .body("token", not(emptyString()));
    }

    @Test(groups = {"api"})
    @Description("Wrong credentials are reported in the body, with no token")
    public void auth_withInvalidCredentials_returnsBadCredentialsReason() {
        given().spec(spec)
                .body(java.util.Map.of("username", "admin", "password", "wrong-password"))
                .when().post("/auth")
                .then().statusCode(200)
                .body("reason", equalTo("Bad credentials"))
                .body("$", not(hasKey("token")));
    }

    @Test(groups = {"smoke", "api"})
    @Description("Creating a booking returns an id and echoes the submitted details")
    public void createBooking_returnsIdAndEchoesPayload() {
        String lastName = uniqueLastName();

        given().spec(spec).body(bookingPayload("Asha", lastName))
                .when().post("/booking")
                .then().statusCode(200)
                .body("bookingid", notNullValue())
                .body("booking.firstname", equalTo("Asha"))
                .body("booking.lastname", equalTo(lastName))
                .body("booking.totalprice", equalTo(150))
                .body("booking.depositpaid", equalTo(true));
    }

    @Test(groups = {"api"})
    @Description("A fetched booking matches the JSON schema")
    public void getBooking_matchesJsonSchema() {
        int id = createBookingAndGetId(uniqueLastName());

        given().spec(spec)
                .when().get("/booking/" + id)
                .then().statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/booking-schema.json"));
    }

    @Test(groups = {"api"})
    @Description("Fetching a booking that does not exist returns 404")
    public void getBooking_unknownId_returns404() {
        given().spec(spec)
                .when().get("/booking/999999999")
                .then().statusCode(404);
    }

    @Test(groups = {"api"})
    @Description("The booking list is not empty and each entry has an id")
    public void listBookings_returnsIds() {
        given().spec(spec)
                .when().get("/booking")
                .then().statusCode(200)
                .body("size()", greaterThan(0))
                .body("[0]", hasKey("bookingid"));
    }

    @Test(groups = {"api"})
    @Description("An authenticated update changes the booking")
    public void updateBooking_withToken_changesLastName() {
        int id = createBookingAndGetId(uniqueLastName());
        String newLastName = uniqueLastName();

        given().spec(spec)
                .header("Cookie", "token=" + token)
                .body(bookingPayload("Asha", newLastName))
                .when().put("/booking/" + id)
                .then().statusCode(200)
                .body("lastname", equalTo(newLastName));

        given().spec(spec)
                .when().get("/booking/" + id)
                .then().statusCode(200)
                .body("lastname", equalTo(newLastName));
    }

    @Test(groups = {"api"})
    @Description("An authenticated delete removes the booking (API answers 201, then 404 on fetch)")
    public void deleteBooking_withToken_removesIt() {
        int id = createBookingAndGetId(uniqueLastName());

        given().spec(spec)
                .header("Cookie", "token=" + token)
                .when().delete("/booking/" + id)
                .then().statusCode(201);

        given().spec(spec)
                .when().get("/booking/" + id)
                .then().statusCode(404);
    }

    @Test(groups = {"api"})
    @Description("Deleting without a token is forbidden")
    public void deleteBooking_withoutToken_returns403() {
        int id = createBookingAndGetId(uniqueLastName());

        given().spec(spec)
                .when().delete("/booking/" + id)
                .then().statusCode(403);
    }
}
