package zdl.util.system;

import com.sun.management.OperatingSystemMXBean;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.StringTokenizer;

/**
 * Created by ZDLegend on 2019/8/3 11:09
 */
public class MonitorService {

    private static final int CPUTIME = 30;

    private static final int PERCENT = 100;

    private static final int FAULTLENGTH = 10;

    private static final int KB = 1024;

    private static String linuxVersion = null;

    /**
     * 获得当前的监控对象.
     *
     * @return 返回构造好的监控对象
     */
    public static MonitorInfoBean getMonitorInfoBean() {

        // 可使用内存
        long totalMemory = Runtime.getRuntime().totalMemory() / KB;
        // 剩余内存
        long freeMemory = Runtime.getRuntime().freeMemory() / KB;
        // 最大可使用内存
        long maxMemory = Runtime.getRuntime().maxMemory() / KB;

        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();

        // 操作系统
        var osName = System.getProperty("os.name");
        // 总的物理内存
        long totalMemorySize = osmxb.getTotalPhysicalMemorySize() / KB;
        // 剩余的物理内存
        long freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize() / KB;
        // 已使用的物理内存
        long usedMemory = (osmxb.getTotalPhysicalMemorySize() - osmxb.getFreePhysicalMemorySize()) / KB;

        // 获得线程总数
        ThreadGroup parentThread;
        for (parentThread = Thread.currentThread().getThreadGroup(); parentThread.getParent() != null; parentThread = parentThread.getParent()) {
            //do nothing
        }
        int totalThread = parentThread.activeCount();

        double cpuRatio;
        if (osName.toLowerCase().startsWith("windows")) {
            cpuRatio = getCpuRatioForWindows();
        } else {
            cpuRatio = getCpuRateForLinux();
        }

        // 构造返回对象
        MonitorInfoBean infoBean = new MonitorInfoBean();
        infoBean.setFreeMemory(freeMemory);
        infoBean.setFreePhysicalMemorySize(freePhysicalMemorySize);
        infoBean.setMaxJvmMemory(maxMemory);
        infoBean.setOsName(osName);
        infoBean.setJvmMemory(totalMemory);
        infoBean.setTotalMemorySize(totalMemorySize);
        infoBean.setTotalThread(totalThread);
        infoBean.setUsedMemory(usedMemory);
        infoBean.setCpuRatio(cpuRatio);
        return infoBean;
    }

    private static double getCpuRateForLinux() {

        StringTokenizer tokenStat;

        Process process;
        try {
            process = Runtime.getRuntime().exec("top -b -n 1");
            try (InputStream is = process.getInputStream();
                 InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader brStat = new BufferedReader(isr)) {

                if (linuxVersion.equals("2.4")) {
                    brStat.readLine();
                    brStat.readLine();
                    brStat.readLine();
                    brStat.readLine();

                    tokenStat = new StringTokenizer(brStat.readLine());
                    tokenStat.nextToken();
                    tokenStat.nextToken();
                    String user = tokenStat.nextToken();
                    tokenStat.nextToken();
                    String system = tokenStat.nextToken();
                    tokenStat.nextToken();
                    String nice = tokenStat.nextToken();

                    user = user.substring(0, user.indexOf('%'));
                    system = system.substring(0, system.indexOf('%'));
                    nice = nice.substring(0, nice.indexOf('%'));

                    float userUsage = Float.parseFloat(user);
                    float systemUsage = Float.parseFloat(system);
                    float niceUsage = Float.parseFloat(nice);

                    return (userUsage + systemUsage + niceUsage) / 100;
                } else {
                    brStat.readLine();
                    brStat.readLine();

                    tokenStat = new StringTokenizer(brStat.readLine());
                    tokenStat.nextToken();
                    tokenStat.nextToken();
                    tokenStat.nextToken();
                    tokenStat.nextToken();
                    tokenStat.nextToken();
                    tokenStat.nextToken();
                    tokenStat.nextToken();
                    String cpuUsage = tokenStat.nextToken();

                    double usage = Double.parseDouble(cpuUsage.replace("%", ""));
                    return (1 - usage / 100.00);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 1.0;
        }
    }

    /**
     * 获得CPU使用率.
     *
     * @return 返回cpu使用率
     * @author GuoHuang
     */
    private static double getCpuRatioForWindows() {
        try {
            String procCmd = System.getenv("windir")
                    + "\\system32\\wbem\\wmic.exe process get Caption,CommandLine,"
                    + "KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";

            // 取进程信息
            long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));
            Thread.sleep(CPUTIME);
            long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));
            if (c0.length > 0 && c1.length > 0) {
                long idleTime = c1[0] - c0[0];
                long busyTime = c1[1] - c0[1];
                return (double) (PERCENT * busyTime) / (double) (busyTime + idleTime);
            } else {
                return 0.0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.0;
        }
    }

    /**
     * 读取CPU信息.
     *
     * @param proc
     * @return
     * @author GuoHuang
     */
    private static long[] readCpu(final Process proc) {
        long[] retn = new long[2];
        try (InputStreamReader ir = new InputStreamReader(proc.getInputStream());
             LineNumberReader input = new LineNumberReader(ir)) {
            proc.getOutputStream().close();
            String line = input.readLine();
            if (line == null || line.length() < FAULTLENGTH) {
                return new long[0];
            }

            int capidx = line.indexOf("Caption");
            int cmdidx = line.indexOf("CommandLine");
            int rocidx = line.indexOf("ReadOperationCount");
            int umtidx = line.indexOf("UserModeTime");
            int kmtidx = line.indexOf("KernelModeTime");
            int wocidx = line.indexOf("WriteOperationCount");

            long idletime = 0;
            long kneltime = 0;
            long usertime = 0;

            while ((line = input.readLine()) != null) {

                // 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,
                String caption = substring(line, capidx, cmdidx - 1).trim();
                String cmd = substring(line, cmdidx, kmtidx - 1).trim();

                if (line.length() < wocidx || cmd.contains("wmic.exe")) {
                    continue;
                }

                if (caption.equals("System Idle Process") || caption.equals("System")) {
                    idletime = idletime
                            + Long.parseLong(substring(line, kmtidx, rocidx - 1).trim())
                            + Long.parseLong(substring(line, umtidx, wocidx - 1).trim());
                } else {
                    kneltime += Long.parseLong(substring(line, kmtidx, rocidx - 1).trim());
                    usertime += Long.parseLong(substring(line, umtidx, wocidx - 1).trim());
                }
            }

            retn[0] = idletime;
            retn[1] = kneltime + usertime;
            return retn;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                proc.getInputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new long[0];
    }

    static String substring(String src, int startIdx, int endIdx) {
        byte[] b = src.getBytes();
        StringBuilder tgt = new StringBuilder();
        for (int i = startIdx; i <= endIdx; i++) {
            tgt.append((char) b[i]);
        }
        return tgt.toString();
    }

    /**
     * 测试方法.
     */
    public static void main(String[] args) {
        MonitorService service = new MonitorService();
        MonitorInfoBean monitorInfo = service.getMonitorInfoBean();
        System.out.println("cpu占有率=" + monitorInfo.getCpuRatio());

        System.out.println("可使用内存=" + monitorInfo.getJvmMemory() / KB);
        System.out.println("剩余内存=" + monitorInfo.getFreeMemory() / KB);
        System.out.println("最大可使用内存=" + monitorInfo.getMaxJvmMemory() / KB);

        System.out.println("操作系统=" + monitorInfo.getOsName());
        System.out.println("总的物理内存=" + monitorInfo.getTotalMemorySize() / KB);
        System.out.println("剩余的物理内存=" + monitorInfo.getFreeMemory() / KB);
        System.out.println("已使用的物理内存=" + monitorInfo.getUsedMemory() / KB);
        System.out.println("线程总数=" + monitorInfo.getTotalThread() + "kb");

        System.out.println("已用内存占用=" + (double) monitorInfo.getUsedMemory() / (double) monitorInfo.getTotalMemorySize());
    }
}
