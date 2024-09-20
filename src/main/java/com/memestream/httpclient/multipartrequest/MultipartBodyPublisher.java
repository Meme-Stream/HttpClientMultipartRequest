package com.memestream.httpclient.multipartrequest;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class MultipartBodyPublisher {

    private String boundary;

    @Getter(AccessLevel.NONE)
    private byte[] bytes;

    private String contentType;

    public HttpRequest.BodyPublisher bodyPublisher() {
        return HttpRequest.BodyPublishers.ofByteArray(bytes);
    }

    public static class Builder {

        private final List<MultipartRecord> records = new ArrayList<>();

        public Builder addPart(MultipartRecord record) {
            records.add(record);
            return this;
        }

        public Builder addString(String name, String value) {
            records.add(
                    MultipartRecord.builder()
                            .name(name)
                            .content(value)
                            .build()
            );
            return this;
        }

        public Builder addString(String name, String type, String value) {
            records.add(
                    MultipartRecord.builder()
                            .name(name)
                            .content(value)
                            .contentType(type)
                            .build()
            );
            return this;
        }

        public Builder addFile(String name, String type, Path path) {
            records.add(
                    MultipartRecord
                            .builder()
                            .name(name)
                            .content(path)
                            .filename(path.getFileName().toString())
                            .contentType(type)
                            .build()
            );
            return this;
        }

        public MultipartBodyPublisher build() throws IOException {
            final var out = new ByteArrayOutputStream();
            final var boundary = UUID.randomUUID().toString();
            final var lineSeparator = System.lineSeparator();

            for (MultipartRecord record : records) {
                final var stringBuilder = new StringBuilder();
                stringBuilder.append("--").
                        append(boundary).
                        append(lineSeparator);

                stringBuilder.append("Content-Disposition: form-data; ")
                        .append("name=\"").append(record.getName()).append("\"; ");

                if (record.getFilename() != null)
                    stringBuilder.append("filename=\"").append(record.getFilename()).append("\"");

                stringBuilder.append(lineSeparator);
                if (record.getContentType() != null)
                    stringBuilder.append("Content-Type: ").append(record.getContentType()).append(lineSeparator);

                stringBuilder.append(lineSeparator);

                out.write(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));

                final var content = record.getContent();
                if (content != null) {
                    switch (content) {
                        case String s -> out.write(s.getBytes(StandardCharsets.UTF_8));
                        case Path p -> out.write(Files.readAllBytes(p));
                        case byte[] b -> out.write(b);
                        default -> {
                            final var objectOut = new ObjectOutputStream(out);
                            objectOut.writeObject(objectOut);
                            objectOut.flush();
                        }
                    }
                }
                out.write(lineSeparator.getBytes(StandardCharsets.UTF_8));
            }
            if (!records.isEmpty())
                out.write(("--" + boundary).getBytes(StandardCharsets.UTF_8));

            return new MultipartBodyPublisher(boundary, out.toByteArray(), "multipart/form-data; boundary=\"" + boundary + "\"");
        }

    }
}
