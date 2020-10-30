package com.rbkmoney.provider.applepay.service;

import lombok.RequiredArgsConstructor;
import okhttp3.Dns;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class DnsSelector implements Dns {

    public enum Mode {
        IPV6_FIRST,
        IPV4_FIRST,
        IPV6_ONLY,
        IPV4_ONLY
    }

    private final Mode mode;

    @Override
    public List<InetAddress> lookup(String hostname) throws UnknownHostException {
        List<InetAddress> addresses = Dns.SYSTEM.lookup(hostname);

        switch (mode) {
            case IPV6_FIRST:
                addresses.sort(Comparator.comparing(Inet4Address.class::isInstance));
                break;
            case IPV4_FIRST:
                addresses.sort(Comparator.comparing(Inet4Address.class::isInstance).reversed());
                break;
            case IPV6_ONLY:
                addresses = addresses.stream().filter(Inet6Address.class::isInstance).collect(toList());
                break;
            case IPV4_ONLY:
                addresses = addresses.stream().filter(Inet4Address.class::isInstance).collect(toList());
                break;
        }

        return addresses;
    }
}
