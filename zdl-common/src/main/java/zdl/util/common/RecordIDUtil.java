package zdl.util.common;

import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ZDLegend on 2018/10/11.
 */
public class RecordIDUtil {

    private static final AtomicInteger atomicInteger = new AtomicInteger();
    private static final int MAX_SEQ_NUM = (int) Math.pow(2, 17) - 1;

    private static final int NODE_NUM = 1;

    private static final long CAPACITY = 32;
    private static final long RANDOM = 17;
    private static final long TIME_GET = ((1L << CAPACITY) - 1) << RANDOM;

    /* 上次生产id时间戳 */
    private static long lastTimeSecond = -1L;

    public static long generateRecordId(long time) {
        return generateRecordId(0, NODE_NUM, time);
    }

    /**
     * get record id with 3 bit service num + 12 bit node num +32 bit time +17 bit sequence num
     *
     * @param serviceNum service number
     * @param nodeNum    node number
     * @param time       long time
     * @return record id
     */
    public static synchronized long generateRecordId(int serviceNum, int nodeNum, long time) {
        int seqNum = 0;
        if (lastTimeSecond == -1L) {
            lastTimeSecond = time / 1000;
        }

        long lastTime = time;
        if (lastTimeSecond == lastTime / 1000) {
            // 当前毫秒内，则+1
            seqNum = getSeqNum();
            if (seqNum == MAX_SEQ_NUM - 1) {
                // 当前毫秒内计数满了，则等待下一秒
                time = tilNextSecond(lastTimeSecond) * 1000;
            }
        } else if ((lastTime / 1000) > lastTimeSecond) {
            atomicInteger.set(0);
        }

        long id = setBit(0, serviceNum, 0, 3);
        id = setBit(id, nodeNum, 3, 12);
        id = setBit(id, time / 1000, 15, 32);
        id = setBit(id, seqNum, 47, 17);

        lastTimeSecond = lastTime / 1000;
        return id;
    }

    /**
     * get time from record id
     *
     * @param id record id
     * @return timestamp in seconds
     */
    public static long getTimeFromId(long id) {
        return (id << 15 >> 32);
    }

    /**
     * generate sequence increase num
     *
     * @return sequence num
     */
    private static int getSeqNum() {
        return atomicInteger.updateAndGet(current -> {
            if (current >= MAX_SEQ_NUM) {
                return 0;
            }
            return ++current;
        });
    }

    /**
     * @param raw   raw num
     * @param v     value need to set
     * @param start start bit position in raw num
     * @param len   num length
     * @return new value
     */
    private static long setBit(long raw, long v, int start, int len) {
        v = v & ((((long) 1) << len) - 1);
        return raw | (v << (64 - start - len));
    }


    private static long tilNextSecond(final long lastTimeSecond) {
        long passtime = System.currentTimeMillis() + LocalTime.now().toNanoOfDay() / 1000000;
        long timeSecond = passtime / 1000;
        while (timeSecond <= lastTimeSecond) {
            passtime = System.currentTimeMillis() + LocalTime.now().toNanoOfDay() / 1000000;
            timeSecond = passtime / 1000;
        }
        return timeSecond;
    }

    public static long subPassTime(long recordId) {
        return ((recordId & TIME_GET) >> RANDOM) * 1000L;
    }

    public static void main(String[] args) {
        System.out.println(subPassTime(2468742311500133006L));
    }
}
