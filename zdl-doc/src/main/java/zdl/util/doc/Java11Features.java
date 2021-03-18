package zdl.util.doc;

import javax.annotation.Nonnull;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

/**
 * @author ZDLegend
 * @version 1.0
 * @date 2021/03/18/ 10:18
 */
public class Java11Features {

    /**
     * 1.局部类型推断
     */
    public void testVar() {
        var str = "helloworld";
        System.out.println(str);

        String[] arr = {"program", "creek", "is", "a", "java", "site"};
        Stream<String> stream = Stream.of(arr);
        //要标注注解的话，就必定要写出x的类型
        stream.forEach((@Nonnull var x) -> System.out.print(x + "\t"));
    }

    /**
     * 流中的新API
     */
    public void testStream() {
        Stream<Integer> stream = Stream.of(1, 3, 2, 5, 6, 7);

        //takeWhile() 该方法会从流中一直获取判定器为真的元素，一旦遇到元素为假，就终止处理
        Stream stream2 = stream.takeWhile(t -> t % 2 != 0);
        stream2.forEach(System.out::println);

        //dropWhile() 这方法和takeWhile()方法相反，它会从流中一直丢弃判定器为真的元素，一旦遇到元素为假，就终止处理
        Stream stream3 = stream.dropWhile(t -> t % 2 != 0);
        stream3.forEach(System.out::println);
    }

    /**
     * 字符串中的新API
     */
    public void testString() {
        String s = "sadas3432cz)_+&<da!!";

        //判断字符串中的字符是否都为空白
        s.isEmpty();
        s.isBlank();

        //去除字符串首尾的空白,与trim()比，strip()可去除全角的空格，
        //因为trim()方法只能去除Unicode码值小于等于32的空白字符，而32正好指的是空格，那么对于全角的空格，trim()方法就无能为力了。所以在功能上，strip()方法更加强大。
        s.strip();

        //去除字符串尾部的空白
        s.stripTrailing();

        //去除字符串首部的空白
        s.stripLeading();

        // 复制字符串，可以传入一个int类型值来控制复制次数
        s.repeat(10);
    }

    /**
     * 异步HttpAPI
     */
    public void testHttp() throws ExecutionException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://www.baidu.com")).build();
        HttpResponse.BodyHandler<String> responseBodyHandler = HttpResponse.BodyHandlers.ofString();
        CompletableFuture<HttpResponse<String>> sendAsync = client.sendAsync(request, responseBodyHandler);
        HttpResponse<String> response = sendAsync.get();
        String body = response.body();
        System.out.println(body);
    }

    /**
     * 集合中的新API java9
     */
    public void testColl() {
        List<String> list = List.of("hello", "world", "java");
        System.out.println(list);
    }
}
