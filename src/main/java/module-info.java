module com.memestream.httpcleint.multipartrequest {
    requires transitive java.net.http;
    requires static lombok;

    exports com.memestream.httpclient.multipartrequest;
}