import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import model.specifications.Specifications;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

public class RequestWithoutPojoClassTest {
    private final static String URL = "https://reqres.in/";

    @Test
    public void getCheckAvatarAndIdTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec(200));
        Response response = given()
                .when()
                .get("api/users?page=2")
                .then()
                .body("page", equalTo(2))
                .body("data.id", notNullValue())
                .body("data.email", notNullValue())
                .body("data.first_name", notNullValue())
                .body("data.last_name", notNullValue())
                .body("data.avatar", notNullValue())
                .extract().response();

        JsonPath jsonPath = response.jsonPath();

        List<String> emails = jsonPath.get("data.email");
        List<Integer> idS = jsonPath.get("data.id");
        List<String> avatars = jsonPath.get("data.avatar");

        for (int i = 0; i < avatars.size(); i++) {
            Assertions.assertTrue(avatars.get(i).contains(idS.get(i).toString()));
        }
        Assertions.assertTrue(emails.stream().allMatch(x->x.endsWith("@reqres.in")));
    }


    @Test
    public void postSuccessUserRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec(200));
        Map<String, String> user = new HashMap<>();
        user.put("email", "eve.holt@reqres.in");
        user.put("password", "pistol");
        Response response = given()
                .body(user)
                .when()
                .post("api/register")
                .then()
                .extract().response();

        JsonPath jsonPath = response.jsonPath();

        int id = jsonPath.get("id");
        String token = jsonPath.get("token");

        Assertions.assertEquals(4, id);
        Assertions.assertEquals("QpwL5tke4Pnpja7X4", token);
    }


    @Test
    public void postFailedRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec(400));
        Map<String, String> user = new HashMap<>();
        user.put("email", "sydney@fife");
        Response response = given()
                .body(user)
                .when()
                .post("api/register")
                .then()
                .extract().response();

        JsonPath jsonPath = response.jsonPath();

        String errorMessage = jsonPath.get("error");

        Assertions.assertEquals("Missing password", errorMessage);
    }
}