package com.system.batch.sybatchsystem.chap03;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LogGenerator {
    private static final String ROOT_PATH = "./test-logs";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) throws IOException {
        File dir = new File(ROOT_PATH);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 파일 이름에 날짜를 포함하여 생성 (예 : access_2026-01-01.log)
        createLogFile(dir, "accesss", 2);
        createLogFile(dir, "accesss", 0);
        createLogFile(dir, "accesss", 50);
        createLogFile(dir, "accesss", 100);

        // 날짜 형식이 없는 예외 파일도 하나 추가 (삭제되면 안 됨)
        createLogFile(dir, "accesss", 100);
    }

    private static void createLogFile(File dir, String prefix, int dayAgo) throws IOException {
        String filename;

        if (dayAgo == -1) {
            filename = prefix;
        } else {
            // 날짜 패턴 적용: prefix_yyyy-MM-dd.log
            LocalDate targetDate = LocalDate.now().minusDays(dayAgo);
            String dataStr = targetDate.format(DATE_FORMATTER);
            filename = prefix + "_" + dataStr + ".log";

            File file = new File(dir, filename);

            if (file.createNewFile()) {
                System.out.println("파일 생성됨: " + filename);
            } else {
                System.out.println("이미 존재함 : " + filename);
            }
        }
    }
}
