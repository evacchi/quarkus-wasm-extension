package io.quarkiverse.quarkus.wasm.it;

import java.util.Set;

import io.quarkus.test.junit.QuarkusTestProfile;

public class TestProfiles {

    public static class NoTags implements QuarkusTestProfile {
    }

    public static class AdminTest implements QuarkusTestProfile {
        @Override
        public Set<String> tags() {
            return Set.of("admin-test");
        }
    }

}
