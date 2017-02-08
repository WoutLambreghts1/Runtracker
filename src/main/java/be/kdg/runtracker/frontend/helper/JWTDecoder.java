package be.kdg.runtracker.frontend.helper;

import be.kdg.runtracker.frontend.exceptions.DecodeTokenException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;

final class JWTDecoder {

    static final JWT decode(String token){
        try {
            JWT jwt = JWT.decode(token);
            return jwt;
        } catch (JWTDecodeException exception){
            throw new DecodeTokenException(token);
        }
    }
}