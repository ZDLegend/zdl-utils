package zdl.util.doc;

import java.time.DayOfWeek;

/**
 * @author ZDLegend
 * @version 1.0
 * @date 2021/03/17/ 20:42
 */
public class Java14Features {

    /**
     * NullPointerException 错误推断 <p>
     * 虚拟机参数里面加上-XX:+ShowCodeDetailsInExceptionMessages，可以显示空指针发生时显示明确的诊断信息，有助于查找问题
     */
    public static void main(String[] args) {
        var obj = new Person(null, 18);
        try {
            obj.getName().trim();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 1. instanceof的模式匹配<p>
     * 使用instanceof关键字来判断对象的真实类型，可以不用强制转换了，从而减少冗余的代码
     */
    public boolean testInstanceof(Object person) {
        Person obj = new Person("Tom", 18);
        if (person instanceof Person p) {
            System.out.println(p.getName());
        }
        return person.equals(obj);
    }

    /**
     * 2. switch表达式<p>
     * Java8中switch支持的类型有：byte char short int Byte Short Character Integer String enum<p>
     * Java12中支持返回值和 ->语法，Java13支持yield返回值，这两个版本只是预览版，Java14中正式发布switch表达式特性
     */
    public void testSwitch(DayOfWeek day) {
        switch (day) {
            case MONDAY, FRIDAY, SUNDAY -> System.out.println(1);
            case TUESDAY -> System.out.println(2);
            case THURSDAY, SATURDAY -> System.out.println(3);
            case WEDNESDAY -> System.out.println(4);
        }

        //返回值
        int i = switch (day) {
            case MONDAY, FRIDAY, SUNDAY -> 1;
            case TUESDAY -> 2;
            case THURSDAY, SATURDAY -> 3;
            case WEDNESDAY -> 4;
        };

        //使用yield返回值
        int j = switch (day) {
            case MONDAY -> 0;
            case TUESDAY -> 1;
            default -> {
                System.out.println("default");
                yield 2;
            }
        };

        System.out.println(i);
        System.out.println(j);
    }


    /**
     * 3. 文本块<p>
     * 类似于Python和Groovy，方便编写多行的字符串字面量。<p>
     * Java13中已引入作为预览特性，Java 14中做了改进，引入了两个新的转义序列。可以使用 \s 转义序列来表示一个空格，可以使用反斜杠 \ 来避免在行尾插入换行字符。
     */
    String TEXT = """
            惜罇空（『敦煌残卷—唐人抄本』）
            君不见，黄河之水天上來，
            奔流到海不复回。
            君不见，床头明镜悲白发，
            朝如青云暮成雪。
            人生得意须尽欢，莫使金罇空对月。
            天生吾徒有俊才，千金散尽还复来。
            烹羊宰牛且为乐，会须一饮三百杯。
            岑夫子，丹丘生，
            与君歌一曲，请君为我倾。
            钟鼓玉帛岂足贵，但愿长醉不用醒。
            古来圣贤皆死尽，唯有饮者留其名。
            陈王昔时宴平乐，斗酒十千恣欢谑 。
            主人何为言少钱，径须沽取对君酌。
            五花马，千金裘，呼儿将出换美酒，
            与尔同销万古愁。
            """;


    /**
     * 4. record<p>
     * 类似于Lombok的@Data注解，用于减少固定的模板代码。和枚举类型一样，记录也是类的一种受限形式，只用于存储数据，没有其它自定义的行为。
     * <li>record只能作用于类，且该类为final修饰，不能被继承。属性都是final修饰的，不能再被赋值，不能完全代替JavaBeans使用。
     * <li>继承了java.lang.Record，不可以在继承其它类。
     * <li>可以定义静态属性、静态或实例方法、构造函数。
     */
    public record Person(String name, int age) {
        public static String address;

        public String getName() {
            return name;
        }
    }
}
