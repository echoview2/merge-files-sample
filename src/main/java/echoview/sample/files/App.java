/*
 * App
 *
 * All Rights Reserved, Copyright (C) 2020 echoview2
 *
 */

package echoview.sample.files;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author       Takahiro Sato
 * @version      1.0
 */
public class App {
  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      System.err.println("usage : target-dir sorted-dir");
      System.exit(1);
    }

    App app = new App();
    
    Path dir = FileSystems.getDefault().getPath(args[0]);
    if (!Files.isDirectory(dir)) {
      System.err.println("Directory not found or not directory:" + args[0]);
      System.exit(1);
    }

    System.out.println("target dir=" + args[0]);
    List<Path> files = Files.list(dir).
                       filter(p -> !Files.isDirectory(p)).
                       collect(Collectors.toList());
    files.stream().forEach(p -> System.out.println("  " + p.getFileName()));

    Path sortedDir = FileSystems.getDefault().getPath(args[1]);
    if (!Files.exists(sortedDir)) {
      System.out.println("create dir:" + sortedDir);
      Files.createDirectory(sortedDir);
    }

    Path mergedFile = FileSystems.getDefault().getPath("merged.txt");
    app.mergeFiles(dir, sortedDir, mergedFile, files);
  }

  public void mergeFiles(Path dir, Path sortedDir, Path mergedFilePath, List<Path> files) throws IOException {
    List<Path> sortedFiles = new ArrayList<Path>();

    for (Path file : files) {
        // ファイルをオープンして読み込み, 条件に合うものだけを選んでソートして
        // ソート済みファイルディレクトリに格納する。
      Path sortedFilePath = sortedDir.resolve(file.getFileName());
      sortedFiles.add(sortedFilePath);

      try (BufferedWriter bw = Files.newBufferedWriter(sortedFilePath, StandardCharsets.UTF_8);
           PrintWriter pw = new PrintWriter(bw)) {
        Files.lines(file, StandardCharsets.UTF_8).
            // filter(s -> !s.startsWith("#")).
            // map(s -> "[" + s + "]").
            sorted().
            forEach(pw::println);
      }
    }

    try (MergedFile mergedFile = new MergedFile(sortedFiles)) {
      mergedFile.merge(mergedFilePath);
    }
  }
}
