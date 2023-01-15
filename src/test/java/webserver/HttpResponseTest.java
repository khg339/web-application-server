package webserver;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class HttpResponseTest {
    private String testDirectory = "./src/test/resources/";

    @Test
    public void responseForward() throws Exception {
        //Http_Forward.txt 결과는 응답 body 의 index.html 이 포함되어 있어야 한다.
        HttpResponse response = new HttpResponse(createOutputStream("Http_Forward.txt"));
        response.forward("/index.html");
    }
    private OutputStream createOutputStream(String filename) throws FileNotFoundException {
        return new FileOutputStream(testDirectory + filename);
    }
}
