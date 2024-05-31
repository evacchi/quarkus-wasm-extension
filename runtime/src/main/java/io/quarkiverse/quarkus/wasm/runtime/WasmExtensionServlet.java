package io.quarkiverse.quarkus.wasm.runtime;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.extism.sdk.Plugin;
import org.extism.sdk.manifest.Manifest;
import org.extism.sdk.wasm.UrlWasmSource;
import org.jboss.logging.Logger;

@WebServlet
public class WasmExtensionServlet extends HttpServlet {
    private static final Logger LOG = Logger.getLogger(WasmExtensionServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        var headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String hn = headerNames.nextElement();
            LOG.infof("%s: %s", hn, req.getHeader(hn));
        }
        String payload = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

        var url = "https://github.com/extism/plugins/releases/latest/download/count_vowels.wasm";
        var manifest = new Manifest(List.of(UrlWasmSource.fromUrl(url)));
        try (var plugin = new Plugin(manifest, false, null)) {
            byte[] result = plugin.call("count_vowels", payload.getBytes(StandardCharsets.UTF_8));
            resp.addHeader("Content-Type", "application/json");
            resp.getWriter().write(new String(result, StandardCharsets.UTF_8));
        }
    }

}
