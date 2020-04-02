package zdl.util.kettle;

import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;

/**
 * Created by ZDLegend on 2020/4/2 17:15
 */
public class Application {
    public static void main(String[] args) throws Exception {
        KettleEnvironment.init();
        JobMeta jobMeta = new JobMeta("httpPostTest.kjb", null);
        Job job = new Job(null, jobMeta);
        job.setLogLevel(LogLevel.ROWLEVEL);
        job.start();
        job.waitUntilFinished();
    }
}
