package com.memestream.httpclient.multipartrequest;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class PostMultipartRequestTest {

    private final static String TEST_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJDaGVmMiIsImV4cCI6MTcyNzAwMjI1MX0.DgXZy8n1lcfGb9ErFhcl63Gsw0JCkfumczuiI-U3qHCWzYci0pVwav__Ywj6Dl4K0sf4Lg8exY9BYWpfLuZYqw";

    @Test
    void shouldPostMemeWithSuccessMessage() throws URISyntaxException, IOException, InterruptedException {
        //given
        final var multiPartBodyPublisher = new MultipartBodyPublisher.Builder()
                .addString("metadata", "application/json",
                        """
                        {"title": "charming", "description": "This is not a text meme", "type":  "IMAGE_JPEG"}
                        """
                )
                .addFile("meme", "image/jpeg", Paths.get("C:\\Users\\Asus\\Pictures\\Screenshot_20240225-132653_Instagram.jpg"))
                .build();

        final var request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/meme/post"))
                .header("Content-Type", multiPartBodyPublisher.getContentType())
                .header("Authorization", "Bearer " + TEST_TOKEN)
                .POST(multiPartBodyPublisher.bodyPublisher())
                .timeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();

        //when
        final var client = HttpClient.newHttpClient();
        final var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        client.close();

        //then

        assertThat(new JSONObject(response.body()).get("response").toString()).contains("Your meme uploaded successfully with id of:");
        assertThat(response.statusCode()).isEqualTo(200);
    }
}
