package zdl.util.system;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * @author ZDLegend
 * @version 1.0
 * @date 2021/03/16/ 09:15
 */
public class SystemUtil {

    private static String OS = System.getProperty("os.name").toLowerCase();

    public static void main(String[] args) throws UnknownHostException {
        all();
    }

    public static void all() throws UnknownHostException {

        InetAddress addr = InetAddress.getLocalHost();
        String ip = addr.getHostAddress(); //获取本机ip
        String hostName = addr.getHostName(); //获取本机计算机名称
        System.out.println("本机IP：" + ip + "\n本机名称:" + hostName);

        Properties props = System.getProperties();
        System.out.println("Java的执行环境版本号：" + props.getProperty("java.version"));
        System.out.println("Java的执行环境供应商：" + props.getProperty("java.vendor"));
        System.out.println("Java供应商的URL：" + props.getProperty("java.vendor.url"));
        System.out.println("Java的安装路径：" + props.getProperty("java.home"));
        System.out.println("Java的虚拟机规范版本号：" + props.getProperty("java.vm.specification.version"));
        System.out.println("Java的虚拟机规范供应商：" + props.getProperty("java.vm.specification.vendor"));
        System.out.println("Java的虚拟机规范名称：" + props.getProperty("java.vm.specification.name"));
        System.out.println("Java的虚拟机实现版本号：" + props.getProperty("java.vm.version"));
        System.out.println("Java的虚拟机实现供应商：" + props.getProperty("java.vm.vendor"));
        System.out.println("Java的虚拟机实现名称：" + props.getProperty("java.vm.name"));
        System.out.println("Java执行时环境规范版本号：" + props.getProperty("java.specification.version"));
        System.out.println("Java执行时环境规范供应商：" + props.getProperty("java.specification.vender"));
        System.out.println("Java执行时环境规范名称：" + props.getProperty("java.specification.name"));
        System.out.println("Java的类格式版本号号：" + props.getProperty("java.class.version"));
        System.out.println("Java的类路径：" + props.getProperty("java.class.path"));
        System.out.println("载入库时搜索的路径列表：" + props.getProperty("java.library.path"));
        System.out.println("默认的暂时文件路径：" + props.getProperty("java.io.tmpdir"));
        System.out.println("一个或多个扩展文件夹的路径：" + props.getProperty("java.ext.dirs"));
        System.out.println("操作系统的名称：" + props.getProperty("os.name"));
        System.out.println("操作系统的构架：" + props.getProperty("os.arch"));
        System.out.println("操作系统的版本号：" + props.getProperty("os.version"));
        System.out.println("文件分隔符：" + props.getProperty("file.separator"));
        //在 unix 系统中是＂／＂
        System.out.println("路径分隔符：" + props.getProperty("path.separator"));
        //在 unix 系统中是＂:＂
        System.out.println("行分隔符：" + props.getProperty("line.separator"));
        //在 unix 系统中是＂/n＂
        System.out.println("用户的账户名称：" + props.getProperty("user.name"));
        System.out.println("用户的主文件夹：" + props.getProperty("user.home"));
        System.out.println("用户的当前工作文件夹：" + props.getProperty("user.dir"));
    }

    public static boolean isLinux() {
        return OS.contains("linux");
    }

    public static boolean isMacOS() {
        return OS.contains("mac") && OS.contains("os") && !OS.contains("x");
    }

    public static boolean isMacOSX() {
        return OS.contains("mac") && OS.contains("os") && OS.contains("x");
    }

    public static boolean isWindows() {
        return OS.contains("windows");
    }

    public static boolean isOS2() {
        return OS.contains("os/2");
    }

    public static boolean isSolaris() {
        return OS.contains("solaris");
    }

    public static boolean isSunOS() {
        return OS.contains("sunos");
    }

    public static boolean isMPEiX() {
        return OS.contains("mpe/ix");
    }

    public static boolean isHPUX() {
        return OS.contains("hp-ux");
    }

    public static boolean isAix() {
        return OS.contains("aix");
    }

    public static boolean isOS390() {
        return OS.contains("os/390");
    }

    public static boolean isFreeBSD() {
        return OS.contains("freebsd");
    }

    public static boolean isIrix() {
        return OS.contains("irix");
    }

    public static boolean isDigitalUnix() {
        return OS.contains("digital") && OS.contains("unix");
    }

    public static boolean isNetWare() {
        return OS.contains("netware");
    }

    public static boolean isOSF1() {
        return OS.contains("osf1");
    }

    public static boolean isOpenVMS() {
        return OS.contains("openvms");
    }

    /**
     * 获取操作系统名字
     *
     * @return 操作系统名
     */
    public static EPlatform getOSName() {
        if (isAix()) {
            return EPlatform.AIX;
        } else if (isDigitalUnix()) {
            return EPlatform.Digital_Unix;
        } else if (isFreeBSD()) {
            return EPlatform.FreeBSD;
        } else if (isHPUX()) {
            return EPlatform.HP_UX;
        } else if (isIrix()) {
            return EPlatform.Irix;
        } else if (isLinux()) {
            return EPlatform.Linux;
        } else if (isMacOS()) {
            return EPlatform.Mac_OS;
        } else if (isMacOSX()) {
            return EPlatform.Mac_OS_X;
        } else if (isMPEiX()) {
            return EPlatform.MPEiX;
        } else if (isNetWare()) {
            return EPlatform.NetWare_411;
        } else if (isOpenVMS()) {
            return EPlatform.OpenVMS;
        } else if (isOS2()) {
            return EPlatform.OS2;
        } else if (isOS390()) {
            return EPlatform.OS390;
        } else if (isOSF1()) {
            return EPlatform.OSF1;
        } else if (isSolaris()) {
            return EPlatform.Solaris;
        } else if (isSunOS()) {
            return EPlatform.SunOS;
        } else if (isWindows()) {
            return EPlatform.Windows;
        } else {
            return EPlatform.Others;
        }
    }
}
