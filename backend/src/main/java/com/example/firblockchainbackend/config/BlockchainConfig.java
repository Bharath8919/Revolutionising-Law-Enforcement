package com.example.firblockchainbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
public class BlockchainConfig {

    @Bean
    public Web3j web3j() {
        // Default to a local Ganache instance at http://localhost:8545
        return Web3j.build(new HttpService("http://localhost:8545"));
    }
}
