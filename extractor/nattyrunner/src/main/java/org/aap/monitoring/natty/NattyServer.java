package org.aap.monitoring.natty;

import com.joestelmach.natty.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

public class NattyServer {

    public static void main(String[] args) throws IOException {
        Scanner stdin = new Scanner(System.in);
        while (stdin.hasNext()) {
            String txt = stdin.nextLine();

            Parser parser = new Parser();
            List<DateGroup> groups = parser.parse(txt);

            if (groups.size() == 1) {
                DateGroup group = groups.get(0);
                List<Date> dates = group.getDates();
                if (groups.size() == 1) {
                    System.out.println(dates.get(0).toString());
                } else {
//                     System.err.println("---> date count : " + dates.size());
                }
            } else {
//                 System.err.println("---> groups count : " + groups.size());
            }
        }
    }
}
