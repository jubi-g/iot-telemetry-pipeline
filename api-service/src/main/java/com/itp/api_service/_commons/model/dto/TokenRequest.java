package com.itp.api_service._commons.model.dto;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record TokenRequest(
   String scope
) {
    public Set<String> getAllScopes() {
        // for prototype / demo purpose, default is read:stats
        if (scope == null || scope.isBlank()) {
            return new HashSet<>(Collections.singleton("all read:stats"));
        }
        return Arrays.stream(scope.split("[,\\s]+"))
            .filter(s -> !s.isBlank())
            .collect(Collectors.toCollection(HashSet::new));
    }
}
