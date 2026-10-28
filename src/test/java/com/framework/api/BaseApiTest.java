package com.framework.api;

import com.framework.config.ConfigReader;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;

/** Shared request spec, auth token and payload builders for the Restful-Booker API tests. */
public abstract class BaseApiTest {

    protected RequestSpecification spec;
    protected String token;

    @BeforeClass(alwaysRun = true)
    public void setUpApi() {
        spec = new RequestSpecBuilder()
                .setBaseUri(ConfigReader.get("api.base.url"))
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")   // without this the API replies in plain text
                .addFilter(new AllureRestAssured())        // request/response attached to the Allure report
                .build();
        token = fetchToken();
    }

    protected String fetchToken() {
        Map<String, String> credentials = Map.of(
                "username", ConfigReader.get("api.username"),
                "password", ConfigReader.get("api.password"));
        return given().spec(spec).body(credentials)
                .when().post("/auth")
                .then().statusCode(200)
                .extract().path("token");
    }

    /** Creates a booking with a unique last name and returns its id. */
    protected int createBookingAndGetId(String lastName) {
        Number id = given().spec(spec).body(bookingPayload("Asha", lastName))
                .when().post("/booking")
                .then().statusCode(200)
                .extract().path("bookingid");
        return id.intValue();
    }

    protected static Map<String, Object> bookingPayload(String firstName, String lastName) {
        Map<String, Object> dates = new HashMap<>();
        dates.put("checkin", "2030-01-01");
        dates.put("checkout", "2030-01-05");

        Map<String, Object> body = new HashMap<>();
        body.put("firstname", firstName);
        body.put("lastname", lastName);
        body.put("totalprice", 150);
        body.put("depositpaid", true);
        body.put("bookingdates", dates);
        body.put("additionalneeds", "Breakfast");
        return body;
    }

    protected static String uniqueLastName() {
        return "Tester-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
