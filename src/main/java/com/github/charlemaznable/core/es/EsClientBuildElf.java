package com.github.charlemaznable.core.es;

import lombok.NoArgsConstructor;
import lombok.val;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;

import java.net.URI;
import java.net.URISyntaxException;

import static java.util.Objects.nonNull;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@NoArgsConstructor(access = PRIVATE)
final class EsClientBuildElf {

    static HttpHost createHttpHost(String uri) {
        try {
            return createHttpHost(URI.create(uri));
        } catch (IllegalArgumentException ex) {
            return HttpHost.create(uri);
        }
    }

    static HttpHost createHttpHost(URI uri) {
        if (isEmpty(uri.getUserInfo())) {
            return HttpHost.create(uri.toString());
        }
        try {
            return HttpHost.create(new URI(uri.getScheme(), null,
                    uri.getHost(), uri.getPort(), uri.getPath(),
                    uri.getQuery(), uri.getFragment()).toString());
        } catch (URISyntaxException ex) {
            throw new IllegalStateException(ex);
        }
    }

    static class ConfigCredentialsProvider extends BasicCredentialsProvider {

        ConfigCredentialsProvider(EsConfig esConfig) {
            if (isNotBlank(esConfig.getUsername())) {
                val credentials = new UsernamePasswordCredentials(
                        esConfig.getUsername(), esConfig.getPassword());
                setCredentials(AuthScope.ANY, credentials);
            }
            esConfig.getUris().parallelStream().map(this::toUri)
                    .forEach(this::addUserInfoCredentials);
        }

        private URI toUri(String uri) {
            try {
                return URI.create(uri);
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }

        private void addUserInfoCredentials(URI uri) {
            if (!hasUserInfo(uri)) return;
            val authScope = new AuthScope(uri.getHost(), uri.getPort());
            val credentials = createUserInfoCredentials(uri.getUserInfo());
            setCredentials(authScope, credentials);
        }

        private boolean hasUserInfo(URI uri) {
            return nonNull(uri) && isNotEmpty(uri.getUserInfo());
        }

        private Credentials createUserInfoCredentials(String userInfo) {
            val delimiter = userInfo.indexOf(':');
            if (delimiter == -1)
                return new UsernamePasswordCredentials(userInfo, null);

            val username = userInfo.substring(0, delimiter);
            val password = userInfo.substring(delimiter + 1);
            return new UsernamePasswordCredentials(username, password);
        }
    }
}
