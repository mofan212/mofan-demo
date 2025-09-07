package indi.mofan.util;


import jakarta.annotation.Nonnull;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * @author mofan
 * @date 2025/8/30 20:30
 */
public class SearchFileLineUtil {

    public static void printTargetLineInMarkdown(String path, Predicate<String> linePredicate) {
        Path rootDir = Paths.get(path);
        if (!Files.exists(rootDir) || !Files.isDirectory(rootDir)) {
            System.out.println("错误: 提供的路径不存在或不是目录");
            return;
        }

        Set<String> codeBlockMarkers = new HashSet<>();

        try {
            // 遍历目录查找所有.md文件
            Files.walkFileTree(rootDir, new SimpleFileVisitor<>() {
                @Override
                @Nonnull
                public FileVisitResult visitFile(Path file, @Nonnull BasicFileAttributes attrs) throws IOException {
                    if (file.toString().endsWith(".md")) {
                        processMarkdownFile(file, codeBlockMarkers, linePredicate);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                @Nonnull
                public FileVisitResult visitFileFailed(Path file, @Nonnull IOException exc) {
                    System.err.println("无法访问文件: " + file + " - " + exc.getMessage());
                    return FileVisitResult.CONTINUE;
                }
            });

            // 输出结果
            System.out.println("匹配的行有(" + codeBlockMarkers.size() + "个):");
            for (String marker : codeBlockMarkers) {
                System.out.println(marker);
            }

        } catch (IOException e) {
            System.err.println("处理过程中发生错误: " + e.getMessage());
        }
    }

    private static void processMarkdownFile(Path file, Set<String> codeBlockMarkers, Predicate<String> linePredicate) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (linePredicate.test(line)) {
                    codeBlockMarkers.add(file.getFileName().toString() + ": " + line);
                }
            }
        }
    }
}
