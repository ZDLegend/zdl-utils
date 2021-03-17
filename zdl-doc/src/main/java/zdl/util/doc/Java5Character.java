package zdl.util.doc;

/**
 * java 1.5 特性示例
 *
 * @author ZDLegend
 * @version 1.0
 * @date 2021/03/16/ 20:44
 */
public class Java5Character {

    /**
     * JDK1.5新特性：<p>
     * 1.自动装箱与拆箱<p>
     * 自动装箱，只需将该值赋给一个类型包装器引用，java会自动创建一个对象。
     * 自动拆箱，只需将该对象值赋给一个基本类型即可。
     * java——类的包装器
     * 类型包装器有：Double,Float,Long,Integer,Short,Character和Boolean<p>
     * 2.枚举<p>
     * 把集合里的对象元素一个一个提取出来。枚举类型使代码更具可读性，理解清晰，易于维护。枚举类型是强类型的，从而保证了系统安全性。
     * 而以类的静态字段实现的类似替代模型，不具有枚举的简单性和类型安全性。
     * 简单的用法：JavaEnum简单的用法一般用于代表一组常用常量，可用来代表一类相同类型的常量值。
     * 复杂用法：Java为枚举类型提供了一些内置的方法，同事枚举常量还可以有自己的方法。可以很方便的遍历枚举对象。<p>
     * 3.静态导入<p>
     * 通过使用 import static，就可以不用指定 Constants 类名而直接使用静态成员，包括静态方法。
     * import xxxx 和 import static xxxx的区别是前者一般导入的是类文件如import java.util.Scanner;
     * 后者一般是导入静态的方法，import static java.lang.System.out。<p>
     * 4.可变参数<p>
     * 可变参数本质就是一个数组，arr就是一个数组的引用地址（反编译工具查看源代码）
     * 一个方法 可以有可变参数和普通参数，但是可变参数必须放到参数列表末尾；
     * 一个方法 有且只能有一个可变参数;
     * 5.内省（Introspector）<p>
     * 是 Java语言对Bean类属性、事件的一种缺省处理方法。例如类A中有属性name,那我们可以通过getName,setName来得到其值或者设置新 的值。
     * 通过getName/setName来访问name属性，这就是默认的规则。Java中提供了一套API用来访问某个属性的getter /setter方法，通过这些API可以使你不需要了解这个规则（但你最好还是要搞清楚），
     * 这些API存放于包java.beans中。一般的做法是通过类Introspector来获取某个对象的BeanInfo信息，然后通过BeanInfo来获取属性的描述器 （PropertyDescriptor），通过这个属性描述器就
     * 可以获取某个属性对应的getter/setter方法，然后我们就可以通过反射机制来 调用这些方法。<p>
     * 6.泛型(Generic) <p>
     * C++ 通过模板技术可以指定集合的元素类型，而Java在1.5之前一直没有相对应的功能。一个集合可以放任何类型的对象，相应地从集合里面拿对象的时候我们也 不得不对他们进行强制得类型转换。
     * 引入了泛型，它允许指定集合里元素的类型，这样你可以得到强类型在编译时刻进行类型检查的好处。<p>
     * 7.For-Each循环 <p>
     * For-Each循环得加入简化了集合的遍历。假设我们要遍历一个集合对其中的元素进行一些处理。
     */
    public void getSum(int value, int... arr) {

    }
}
