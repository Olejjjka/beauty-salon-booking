package com.example.beauty_salon_booking.security;

import com.example.beauty_salon_booking.entities.Client;
import com.example.beauty_salon_booking.entities.Master;
import com.example.beauty_salon_booking.enums.Role;
import com.example.beauty_salon_booking.repositories.ClientRepository;
import com.example.beauty_salon_booking.repositories.MasterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final ClientRepository clientRepository;
    private final MasterRepository masterRepository;

    @Autowired
    public MyUserDetailsService(ClientRepository clientRepository, MasterRepository masterRepository) {
        this.clientRepository = clientRepository;
        this.masterRepository = masterRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Client client = clientRepository.findByLogin(login).orElse(null);
        if (client != null) {
            return new UserPrincipal(client.getId(), client.getLogin(), client.getPassword(), client.getName(), Role.CLIENT);
        }

        Master master = masterRepository.findByLogin(login).orElse(null);
        if (master != null) {
            return new UserPrincipal(master.getId(), master.getLogin(), master.getPassword(), master.getName(), Role.MASTER);
        }

        throw new UsernameNotFoundException("User with login '" + login + "' not found");
    }
}
