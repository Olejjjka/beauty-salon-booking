package com.example.beauty_salon_booking.services;

import com.example.beauty_salon_booking.entities.Client;
import com.example.beauty_salon_booking.entities.Master;
import com.example.beauty_salon_booking.entities.Token;
import com.example.beauty_salon_booking.enums.TokenType;
import com.example.beauty_salon_booking.repositories.ClientRepository;
import com.example.beauty_salon_booking.repositories.MasterRepository;
import com.example.beauty_salon_booking.repositories.TokenRepository;
import com.example.beauty_salon_booking.security.JwtTokenProvider;
import com.example.beauty_salon_booking.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private TokenRepository tokenRepository;
    @Autowired private ClientRepository clientRepository;
    @Autowired private MasterRepository masterRepository;

    public String authenticate(String login, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login, password)
        );

        String jwt = jwtTokenProvider.generateToken(authentication);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        switch (principal.getRole()) {
            case CLIENT -> {
                Client client = clientRepository.findByLogin(login)
                        .orElseThrow(() -> new RuntimeException("Client not found"));
                revokeAllClientTokens(client);
                tokenRepository.save(new Token(jwt, TokenType.BEARER, false, false, client));
            }
            case MASTER -> {
                Master master = masterRepository.findByLogin(login)
                        .orElseThrow(() -> new RuntimeException("Master not found"));
                revokeAllMasterTokens(master);
                tokenRepository.save(new Token(jwt, TokenType.BEARER, false, false, master));
            }
        }

        return jwt;
    }

    private void revokeAllClientTokens(Client client) {
        tokenRepository.findAllByClient(client).forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
    }

    private void revokeAllMasterTokens(Master master) {
        tokenRepository.findAllByMaster(master).forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
    }


}
