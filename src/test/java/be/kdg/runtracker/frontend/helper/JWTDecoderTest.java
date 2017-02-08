package be.kdg.runtracker.frontend.helper;

import com.auth0.jwt.JWT;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import static org.junit.Assert.*;
/**
 * Created by Wout on 8/02/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class JWTDecoderTest {

    @Value("eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6Ik5EQkJNRUU1TmpnM09FVTVNVVJDUXpCRFFrSkVNVEEwTlVFek5qZ3lOakUxTVVFM1JFSkROdyJ9.eyJpc3MiOiJodHRwczovL3J1bnRyYWNrbWluZHMuZXUuYXV0aDAuY29tLyIsInN1YiI6ImF1dGgwfDU4OTc0MDU2NDYwNWU1MTdmYjkzNTc4MyIsImF1ZCI6WyJodHRwczovL3J1bnRyYWNrbWluZHMuZXUuYXV0aDAuY29tL3VzZXJpbmZvIl0sImF6cCI6IkZJbWw3YmVQdnlXZmMyeTlVemFSVVBqWURlbkRRU05FIiwiZXhwIjoxNDg2NjIyNTU3LCJpYXQiOjE0ODY1ODY1NTcsInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwgYWRkcmVzcyBwaG9uZSJ9.r84e00mExVjRe-tEm-CgJZGaCgh7Uc-72ZB48C5yjKUykIdrM76VGKMMO5Hcf3NULFwzDPLXx6NnQvc32VKUV-iDCU1p-deMrmoedAHtYpunTvOTCDETZp9a7BoXIEcRo5q-AVvGx8IlRfORJmaMA3i3tlqG67vpn5OymXHKsBKoMUukpk-UpoK8UilEU533xTl3D8qfNuAZYWMjDG1lBVZ-nj6jbMyl0LZjqb_mVkTQoTAngQEhYdVcvRgkO4S1ukdvXDcEBscUT_UcuYRoBP-3ssQFGg7HXHnNn8Gz0gSCCqdjFG2_T0N2i6aL8mr6lQv9FVpS7sMuh2OBxWXsOQ")
    private String token;


    @Test
    public void testDecoder(){
        JWT jwt = JWTDecoder.decode(token);
        System.out.println("User: " + jwt.getSubject());
        System.out.println("Issuer: " + jwt.getIssuer());
        System.out.println("Expiration: " + jwt.getExpiresAt());

        assertEquals(jwt.getSubject(),"auth0|589740564605e517fb935783");

    }
}
