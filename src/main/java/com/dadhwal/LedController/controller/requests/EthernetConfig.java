package com.dadhwal.LedController.controller.requests;

import java.util.List;
import java.util.Objects;

/**
 * Represents Ethernet network configuration parameters.
 */
public final class EthernetConfig {

    /**
     * IPv4 address.
     */
    private final String ip;

    /**
     * Subnet mask in IPv4 format.
     */
    private final String mask;

    /**
     * Default gateway IPv4 address.
     */
    private final String gateway;

    /**
     * List of DNS servers (at least one required).
     */
    private final List<String> dnsServers;

    public EthernetConfig(String ip, String mask, String gateway, List<String> dnsServers) {
        this.ip = ip;
        this.mask = mask;
        this.gateway = gateway;
        this.dnsServers = List.copyOf(dnsServers); // Defensive copy
    }

    public String getIp() {
        return ip;
    }

    public String getMask() {
        return mask;
    }

    public String getGateway() {
        return gateway;
    }

    public List<String> getDnsServers() {
        return dnsServers;
    }

    @Override
    public String toString() {
        return "{" +
                "ip='" + ip + '\'' +
                ", mask='" + mask + '\'' +
                ", gateway='" + gateway + '\'' +
                ", dnsServers=" + dnsServers +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EthernetConfig)) return false;
        EthernetConfig that = (EthernetConfig) o;
        return Objects.equals(ip, that.ip) &&
                Objects.equals(mask, that.mask) &&
                Objects.equals(gateway, that.gateway) &&
                Objects.equals(dnsServers, that.dnsServers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, mask, gateway, dnsServers);
    }
}
