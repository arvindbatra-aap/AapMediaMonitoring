package org.aap.monitoring.natty;

import com.joestelmach.natty.*;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

public class NattyRunner {

    private final Parser parser;

    public NattyRunner() {
        parser = new Parser();
    }

    public String extractDate(String text) {

        String out = null;

        List<DateGroup> groups = parser.parse(text);

        if (groups.size() == 1) {
            DateGroup group = groups.get(0);
            List<Date> dates = group.getDates();
            if (groups.size() == 1) {
                out = dates.get(0).toString();
            }
        }
        return out;
    }

    public static void main(String[] args) throws IOException {

        NattyRunner nr = new NattyRunner();

        Scanner stdin = new Scanner(System.in);
        while (stdin.hasNext()) {
            String text = stdin.nextLine();
            String date = nr.extractDate(text);
            if (date != null) {
                System.out.println(date);
            }
            else{
                System.err.println("no date");
            }
        }
    }
}
