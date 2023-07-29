import model.get.ColorsData;
import model.get.UserData;
import model.post.FailedReg;
import model.post.Register;
import model.post.SuccessReg;
import model.put.UpdateInfoUsersRq;
import model.put.UpdateInfoUsersRs;
import model.specifications.Specifications;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class RequestTest {
    private final static String URL = "https://reqres.in/";
    private final static Integer id = 4;
    private final static String token = "QpwL5tke4Pnpja7X4";

    @Test
    public void getCheckAvatarAndIdTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec(200));
        List<UserData> users = given()
                .when()
                .get("api/users?page=2")
                .then().log().all()
                .extract().body().jsonPath().getList("data", UserData.class);

        //  users.forEach(x -> Assertions.assertTrue(x.getAvatar().contains(x.getId().toString())));

        //   Assertions.assertTrue(users.stream().allMatch(x->x.getEmail().endsWith("@regress.in")));

        List<String> avatars = users.stream()
                .map(UserData::getAvatar)
                .collect(Collectors.toList());

        List<String> idS = users.stream()
                .map(x -> x.getId().toString())
                .collect(Collectors.toList());

        for (int i = 0; i < avatars.size(); i++) {
            Assertions.assertTrue(avatars.get(i).contains(idS.get(i)));
        }
    }

    @Test
    public void postSuccessRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec(200));
        Register user = new Register(
                "eve.holt@reqres.in",
                "pistol");
        SuccessReg successReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(SuccessReg.class);
        Assertions.assertNotNull(successReg.getId());
        Assertions.assertNotNull(successReg.getToken());
        Assertions.assertEquals(id, successReg.getId());
        Assertions.assertEquals(token, successReg.getToken());
    }


    @Test
    public void postFailedRegTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec(400));
        Register user = new Register(
                "sydney@fife",
                "");
        FailedReg failedReg = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().as(FailedReg.class);
        Assertions.assertEquals("Missing password", failedReg.getError());
    }

    @Test
    public void getSortedYearsTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec(200));
        List<ColorsData> colors = given()
                .when()
                .get("api/unknown")
                .then().log().all()
                .extract().body().jsonPath().getList("data", ColorsData.class);

        List<Integer> years = colors.stream()
                .map(ColorsData::getYear)
                .collect(Collectors.toList());

        List<Integer> sortedYears = years.stream()
                .sorted()
                .collect(Collectors.toList());

        Assertions.assertEquals(years, sortedYears);
    }


    @Test
    public void deleteUserTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec(204));
        given()
                .when()
                .delete("api/users/2")
                .then().log().all();
    }

    @Test
    public void putInfoUserTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec(200));
        UpdateInfoUsersRq updateInfoUsers = new UpdateInfoUsersRq(
                "morpheus",
                "zion resident");
        UpdateInfoUsersRs updateInfoUsersRs = given()
                .body(updateInfoUsers)
                .when()
                .put("api/users/2")
                .then().log().all()
                .extract().as(UpdateInfoUsersRs.class);
        String regexOne = "(.{8})$";
        String regexTwo = "(.{5})$";
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regexOne, "");
        Assertions.assertEquals(currentTime, updateInfoUsersRs.getUpdatedAt().replaceAll(regexTwo, ""));
    }
}