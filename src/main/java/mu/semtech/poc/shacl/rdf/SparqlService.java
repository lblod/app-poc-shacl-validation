package mu.semtech.poc.shacl.rdf;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class SparqlService {
    @Value("${sparql.username}")
    private String username;
    @Value("${sparql.endpoint}")
    private String endpoint;
    @Value("${sparql.password}")
    private String password;
    @Value("${sparql.defaultGraphUri}")
    private String defaultGraphUri;
    public void persist(Model model) {
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray());
            }
        });

        try {
            StringWriter writer = new StringWriter();
            model.write(writer, "TURTLE");

            String sparqlUrl = endpoint + "/sparql-graph-crud-auth?graph-uri=" + defaultGraphUri;
            loadIntoGraph_exception(writer.toString().getBytes(StandardCharsets.UTF_8), sparqlUrl);
        }
        catch (Exception e) {
            log.error("error during upload",e);
        }
    }

    private  void loadIntoGraph_exception(byte[] data, String updateUrl) throws Exception {
        URL url = new URL(updateUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-turtle");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(data.length));
        conn.setUseCaches(false);
        conn.getOutputStream().write(data);
        log.trace("code: {}, message: {}", conn.getResponseCode(),conn.getResponseMessage());
    }
}
