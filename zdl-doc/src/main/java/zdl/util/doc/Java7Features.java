package zdl.util.doc;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * java 1.7 特性示例
 *
 * @author ZDLegend
 * @version 1.0
 * @date 2021/03/16/ 20:52
 */
public class Java7Features implements AutoCloseable {

    /**
     * 1.switch中可以使用字串了
     */
    public static void switchView(String s) {
        switch (s) {
            case "test":
                System.out.println("test");
            case "test1":
                System.out.println("test1");
                break;
            default:
                System.out.println("break");
                break;
        }
    }

    /**
     * 2.泛型实例化类型自动推断。
     */
    public static void tView() {
        // Pre-JDK 7
        List<String> lst1 = new ArrayList<String>();
        // JDK 7 supports limited type inference for generic instance creation
        List<String> lst2 = new ArrayList<>();
    }

    /**
     * 3. 自定义自动关闭类<p>
     * 只要实现该接口，在该类对象销毁时自动调用close方法，你可以在close方法关闭你想关闭的资源
     */
    @Override
    public void close() throws Exception {
        System.out.println(" Custom close method … close resources ");
    }

    /**
     * 4.文件系统<p>
     * {@link java.nio.file.Paths} 包含了用于创建Path对象的静态方法<p>
     * {@link java.nio.file.Path} 包含了大量用于操纵文件路径的方法<p>
     * {@link java.nio.file.FileSystems} 用于访问文件系统的类<p>
     * {@link java.nio.file.FileSystem} 代表了一种文件系统，例如Unix下的根目录为 / ，而Windows下则为C盘<p>
     * {@link java.nio.file.FileStore} 代表了真正的存储设备，提供了设备的详尽信息<p>
     * {@link java.nio.file.attribute.FileStoreAttributeView} 提供了访问文件的信息
     */
    public static void fileSystem() {
        Path path = FileSystems.getDefault().getPath("/Home/projects/node.txt");
        System.out.println();
        System.out.println("toString: " + path.toString());
        System.out.printf("getFileName: %s\n", path.getFileName());
        System.out.printf("getRoot: %s\n", path.getRoot());
        System.out.printf("getNameCount: %d\n", path.getNameCount());

        for (int index = 0; index < path.getNameCount(); index++) {
            System.out.printf("getName(%d): %s\n", index, path.getName(index));
        }

        System.out.printf("subpath(0,2): %s\n", path.subpath(0, 2));
        System.out.printf("getParent: %s\n", path.getParent());
        System.out.println(path.isAbsolute());

        try {
            path = Paths.get("Home", "projects", "users.txt");
            System.out.printf("Absolute path: %s", path.toAbsolutePath());
        } catch (InvalidPathException ex) {
            System.out.printf("Bad path: [%s] at position %s", ex.getInput(), ex.getIndex());
        }
    }

    /**
     * 5.try(){ } catch(){}资源自动关闭<p>
     * 6.在try catch异常扑捉中，一个catch可以写多个异常类型，用"|"隔开
     */
    public static void resource() {
        try (BufferedReader in = new BufferedReader(new FileReader("in.txt"));
             BufferedWriter out = new BufferedWriter(new FileWriter("out.txt"))) {
            int charRead;
            while ((charRead = in.read()) != -1) {
                System.out.printf("%c ", (char) charRead);
                out.write(charRead);
            }
        } catch (IOException | NullPointerException ex) {
            ex.printStackTrace();
        }
    }
}
