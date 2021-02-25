package org.sdrc.mongoclientdetails;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.sdrc.mongoclientdetails.repository.MongoClientDetailsRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Component;

@Component
public class MongoClientDetailsService implements ClientDetailsService, ClientRegistrationService {

    private final MongoClientDetailsRepository mongoClientDetailsRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public MongoClientDetailsService(final MongoClientDetailsRepository mongoClientDetailsRepository,
                                     final BCryptPasswordEncoder passwordEncoder) {
        this.mongoClientDetailsRepository = mongoClientDetailsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ClientDetails loadClientByClientId(final String clientId) {
        try {
            return mongoClientDetailsRepository.findByClientId(clientId);
        } catch (IllegalArgumentException e) {
            throw new ClientRegistrationException("No Client Details for client id", e);
        }
    }

    @Override
    public void addClientDetails(final ClientDetails clientDetails) {
        final MongoClientDetails mongoClientDetails = new MongoClientDetails(clientDetails.getClientId(),
                passwordEncoder.encode(clientDetails.getClientSecret()),
                clientDetails.getScope(),
                clientDetails.getResourceIds(),
                clientDetails.getAuthorizedGrantTypes(),
                clientDetails.getRegisteredRedirectUri(),
                new ArrayList<>(clientDetails.getAuthorities()),
                clientDetails.getAccessTokenValiditySeconds(),
                clientDetails.getRefreshTokenValiditySeconds(),
                clientDetails.getAdditionalInformation(),
                getAutoApproveScopes(clientDetails));

        mongoClientDetailsRepository.save(mongoClientDetails);
    }

    @Override
    public void updateClientDetails(final ClientDetails clientDetails) {
        final MongoClientDetails mongoClientDetails = new MongoClientDetails(clientDetails.getClientId(),
                clientDetails.getClientSecret(),
                clientDetails.getScope(),
                clientDetails.getResourceIds(),
                clientDetails.getAuthorizedGrantTypes(),
                clientDetails.getRegisteredRedirectUri(),
                new ArrayList<>(clientDetails.getAuthorities()),
                clientDetails.getAccessTokenValiditySeconds(),
                clientDetails.getRefreshTokenValiditySeconds(),
                clientDetails.getAdditionalInformation(),
                getAutoApproveScopes(clientDetails));
        final boolean result = mongoClientDetailsRepository.update(mongoClientDetails);

        if (!result) {
            throw new NoSuchClientException("No such Client Id");
        }
    }

    @Override
    public void updateClientSecret(final String clientId,
                                   final String secret) {
        final boolean result = mongoClientDetailsRepository.updateClientSecret(clientId, passwordEncoder.encode(secret));
        if (!result) {
            throw new NoSuchClientException("No such client id");
        }
    }

    @Override
    public void removeClientDetails(String clientId) {
        final boolean result = mongoClientDetailsRepository.deleteByClientId(clientId);
        if (!result) {
            throw new NoSuchClientException("No such client id");
        }
    }

    @Override
    public List<ClientDetails> listClientDetails() {
        final List<MongoClientDetails> allClientDetails = mongoClientDetailsRepository.findAll();
        return new ArrayList<>(allClientDetails);
    }

    private Set<String> getAutoApproveScopes(final ClientDetails clientDetails) {
        if (clientDetails.isAutoApprove("true")) {
        	HashSet<String> hashSet = new HashSet<String>();
        	hashSet.add("true");
            return hashSet; // all scopes autoapproved
        }

        return clientDetails.getScope().stream()
                .filter(clientDetails::isAutoApprove)
                .collect(Collectors.toSet());
    }
}