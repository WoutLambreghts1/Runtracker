package be.kdg.runtracker.frontend.controllers.rest;

import be.kdg.runtracker.backend.dom.profile.Friendship;
import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.tracking.Coordinate;
import be.kdg.runtracker.backend.dom.tracking.Tracking;
import be.kdg.runtracker.backend.persistence.api.CoordinatesRepository;
import be.kdg.runtracker.backend.persistence.api.FriendshipRepository;
import be.kdg.runtracker.backend.persistence.api.TrackingRepository;
import be.kdg.runtracker.backend.persistence.api.UserRepository;
import be.kdg.runtracker.frontend.dto.TrackingDTO;
import com.auth0.jwt.JWT;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@Transactional
public class TrackingRestControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TrackingRepository trackingRepository;
    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private CoordinatesRepository coordinatesRepository;

    private MockMvc mockMvc;
    private Gson gson;

    private User alexander;
    private User wout;
    private User jelle;

    private Tracking tracking1;
    private Tracking tracking2;
    private Tracking tracking3;
    private Tracking tracking4;
    private Tracking testTracking;

    List<Coordinate> coordinates;

    private String tokenAlexander;
    private String tokenWout;
    private String tokenJelle;
    private String wrongToken;

    @Before
    public void setup() {
        tokenAlexander = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ";
        tokenWout = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDIifQ.gbIlCFTC5tyyLg8UqvxcoUUmhgUTdstZnHWNzEO1jVM";
        tokenJelle = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDMifQ._Am34giuNzroHLmI482DV46DFq7UuNTdpzQ01aQdyio";
        wrongToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxkZXIiLCJpYXQiOjE0ODY3MzE5MzgsImV4cCI6MTUxODI2NzkzOCwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoidGVzdDYifQ.X8l82QUd7sXLuqNxiTJaQZDhU9V7_4fIi3MKNxYHOQU";

        this.alexander = new User();
        this.alexander.setAuthId(JWT.decode(tokenAlexander).getSubject());
        this.alexander.setFirstname("Alexander");
        this.alexander.setLastname("van Ravestyn");
        this.alexander.setUsername("alexvr");

        this.wout = new User();
        this.wout.setAuthId(JWT.decode(tokenWout).getSubject());
        this.wout.setFirstname("Wout");
        this.wout.setLastname("Lambreghts");
        this.wout.setUsername("woutl");

        this.jelle = new User();
        this.jelle.setAuthId(JWT.decode(tokenJelle).getSubject());
        this.jelle.setFirstname("Jelle");
        this.jelle.setLastname("Mannaerts");
        this.jelle.setUsername("jellem");

        this.tracking1 = new Tracking(10, 10, 10, 10);
        this.tracking2 = new Tracking(10, 10, 10, 10);
        this.tracking3 = new Tracking(10, 10, 10, 10);
        this.tracking4 = new Tracking(10, 10, 10, 10);
        this.testTracking = new Tracking(10, 10, 10, 10);

        Coordinate coordinate = new Coordinate();
        coordinate.setLat(10);
        coordinate.setLon(10);
        coordinate.setSpeed(10);
        coordinate.setTime(10);
        coordinates = new ArrayList<>();
        coordinates.add(coordinate);
        this.testTracking.setCoordinates(coordinates);


        this.tracking1.setUser(alexander);
        this.tracking2.setUser(alexander);
        this.alexander.addTracking(tracking1);
        this.alexander.addTracking(tracking2);

        this.tracking3.setUser(jelle);
        this.tracking4.setUser(jelle);
        this.jelle.addTracking(tracking3);
        this.jelle.addTracking(tracking4);

        Friendship friendship1 = new Friendship(wout);
        Friendship friendship2 = new Friendship(alexander);
        friendship1.setAccepted(true);
        friendship2.setAccepted(true);
        friendshipRepository.save(friendship1);
        friendshipRepository.save(friendship2);
        this.alexander.addFriendship(friendship1);
        this.wout.addFriendship(friendship2);

        Friendship friendship3 = new Friendship(wout);
        Friendship friendship4 = new Friendship(jelle);
        friendship3.setAccepted(true);
        friendship4.setAccepted(true);
        friendshipRepository.save(friendship3);
        friendshipRepository.save(friendship4);
        this.jelle.addFriendship(friendship3);
        this.wout.addFriendship(friendship4);

        this.userRepository.save(alexander);
        this.userRepository.save(wout);
        this.userRepository.save(jelle);
        this.trackingRepository.save(tracking1);
        this.trackingRepository.save(tracking2);
        this.trackingRepository.save(tracking3);
        this.trackingRepository.save(tracking4);

        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        this.gson = new Gson();
    }

    @Test
    public void testGetAllTrackingsForUser() throws Exception {
        this.mockMvc.perform(get("/trackings/getTrackings").header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testGetAllTrackingsForNonExistingUser() throws Exception {
        this.mockMvc.perform(get("/trackings/getTrackings").header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void testGetNoTrackingsForUser() throws Exception {
        this.mockMvc.perform(get("/trackings/getTrackings").header("token", tokenWout).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testGetTrackingsFromFriend() throws Exception {
        String username = "alexvr";
        this.mockMvc.perform(get("/trackings/getAllTrackings/" + username).header("token", tokenWout).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testGetAllTrackingsFriends() throws Exception {
        String username = "alexvr";
        this.mockMvc.perform(get("/trackings/getAllTrackingsFriends/").header("token", tokenWout).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(username)));
    }

    @Test
    public void testGetNoTrackingsFromFriend() throws Exception {
        String username = "woutl";
        this.mockMvc.perform(get("/trackings/getAllTrackings/" + username).header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testGetTrackingsFromNotFriend() throws Exception {
        String username = "jellem";
        this.mockMvc.perform(get("/trackings/getAllTrackings/" + username).header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void testCreateTracking() throws Exception {
        //String localDateTimeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        //String testTrackingJSON = "{\"time\":\"" + localDateTimeNow + "\",\"totalDuration\":10,\"totalDistance\":10,\"maxSpeed\":10.0,\"avgSpeed\":10.0,\"coordinates\":[{\"time\":10,\"lat\":10.0,\"lon\":10.0,\"trackingId\":0,\"speed\":10.0}]}";
        TrackingDTO testTracking = new TrackingDTO();
        testTracking.setTotalDuration(10);
        testTracking.setTotalDistance(10);
        testTracking.setAvgSpeed(10);
        testTracking.setAvgPace(10);
        testTracking.setMaxSpeed(10);
        List<Coordinate> coordinates = new ArrayList<Coordinate>();
        coordinates.add(new Coordinate(10, 10, 10, 10));

        this.mockMvc.perform(post("/trackings/createTracking").content(gson.toJson(testTracking)).header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    public void testCreateTrackingForNonExistingUser() throws Exception {
        //String localDateTimeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        //String testTrackingJSON = "{\"time\":\"" + localDateTimeNow + "\",\"totalDuration\":10,\"totalDistance\":10,\"maxSpeed\":10.0,\"avgSpeed\":10.0,\"coordinates\":[{\"time\":10,\"lat\":10.0,\"lon\":10.0,\"trackingId\":0,\"speed\":10.0}]}";
        TrackingDTO testTracking = new TrackingDTO();
        testTracking.setTotalDuration(10);
        testTracking.setTotalDistance(10);
        testTracking.setAvgSpeed(10);
        testTracking.setAvgPace(10);
        testTracking.setMaxSpeed(10);
        List<Coordinate> coordinates = new ArrayList<Coordinate>();
        coordinates.add(new Coordinate(10, 10, 10, 10));

        this.mockMvc.perform(post("/trackings/createTracking").content(gson.toJson(testTracking)).header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void addCoordinateToTracking() throws Exception {
        long trackingId = alexander.getTrackings().get(0).getTrackingId();
        Coordinate coordinate = new Coordinate();
        coordinate.setLon(10);
        coordinate.setLat(10);
        coordinate.setTime(10);
        coordinate.setSpeed(10);
        this.mockMvc.perform(post("/trackings/addCoordinateToTracking/" + trackingId).content(gson.toJson(coordinate)).header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    public void addCoordinateToTrackingFromNonExistinguser() throws Exception {
        long trackingId = 1;
        Coordinate coordinate = new Coordinate();
        this.mockMvc.perform(post("/trackings/addCoordinateToTracking/" + trackingId).content(gson.toJson(coordinate)).header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void addCoordinateToNonExistingTracking() throws Exception {
        long trackingId = 137286382;
        Coordinate coordinate = new Coordinate();
        this.mockMvc.perform(post("/trackings/addCoordinateToTracking/" + trackingId).content(gson.toJson(coordinate)).header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testDeleteTrackingFromUser() throws Exception {
        long trackingId = alexander.getTrackings().get(0).getTrackingId();
        this.mockMvc.perform(delete("/trackings/deleteTracking/" + trackingId).header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void testDeleteTrackingFromNonExistingUser() throws Exception {
        long trackingId = 1;
        this.mockMvc.perform(delete("/trackings/deleteTracking/" + trackingId).header("token", wrongToken).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    public void testDeleteNonExistingTracking() throws Exception {
        long trackingId = 137286382;
        this.mockMvc.perform(delete("/trackings/deleteTracking/" + trackingId).header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void testDeleteTrackingFromOtherUser() throws Exception {
        long trackingId = jelle.getTrackings().get(0).getTrackingId();
        this.mockMvc.perform(delete("/trackings/deleteTracking/" + trackingId).header("token", tokenAlexander).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @After
    public void deleteUserAndTrackings() {
        alexander.getTrackings().stream().forEach(tracking -> trackingRepository.delete(tracking.getTrackingId()));
        jelle.getTrackings().stream().forEach(tracking -> trackingRepository.delete(tracking.getTrackingId()));
        alexander.getTrackings().stream().forEach(tracking -> coordinatesRepository.deleteCoordinatesCollection(tracking.getTrackingId()));
        jelle.getTrackings().stream().forEach(tracking -> coordinatesRepository.deleteCoordinatesCollection(tracking.getTrackingId()));

        userRepository.delete(alexander.getUserId());
        userRepository.delete(wout.getUserId());
        userRepository.delete(jelle.getUserId());
    }

}
