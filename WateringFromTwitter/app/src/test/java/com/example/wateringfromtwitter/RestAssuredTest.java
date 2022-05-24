package com.example.wateringfromtwitter;

import org.junit.Test;

import io.restassured.RestAssured;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class RestAssuredTest {
    @Test
    public void testRestAssuredSmokeTest() {
        RestAssured.get("https://google.com").then().statusCode(200);
    }
}