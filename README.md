A simple solution for making multipart requests using java http client

To make a multipart request in http we need a body like this:
```
--delimiter12345
Content-Disposition: form-data; name="field1"

value1
--delimiter12345
Content-Disposition: form-data; name="field2"; filename="example.txt"

value2
--delimiter12345--
```

This project basically creates something like this and writes that into a byte array,
and uses java http client ByteArrayPublisher to create a body publisher.

You can find example codes in test folder.