package zdl.system;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by ZDLegend on 2019/9/19 17:42
 */
public class CmdProcessDemo {

    public static void main(String[] args) {
        try {
            Process process = Runtime.getRuntime().exec("cc.bat");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));) {
                String line;
                int i = 0;
                while ((line = reader.readLine()) != null) {
                    System.out.println(i++ + ":" + line);
                    if (line.endsWith("qq:")) {
                        writer.write("12345");
                        writer.newLine();
                        writer.flush();
                    }

                    if (line.endsWith("phone:")) {
                        writer.write("45678");
                        writer.newLine();
                        writer.flush();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
