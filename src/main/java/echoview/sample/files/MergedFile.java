/*
 * MergedFile
 *
 * All Rights Reserved, Copyright (C) 2019 echoview2
 *
 */

package echoview.sample.files;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * 
 * @author       Takahiro Sato
 * @version      1.0
 */
public class MergedFile implements AutoCloseable {
  public MergedFile(List<Path> sortedFilePaths) throws IOException {
    this.sortedFilePaths = sortedFilePaths;
    for (Path path : this.sortedFilePaths) {
      this.sortedFiles.add(new SortedFile(path));
    }
  }

  public void merge(Path mergedFile) throws IOException {
    try (BufferedWriter bw = Files.newBufferedWriter(mergedFile, StandardCharsets.UTF_8);
         PrintWriter pw = new PrintWriter(bw)) {

      String line;
      while ((line = getLine()) != null) {
        pw.println(line);
      }
    }
  }

  protected String getLine() throws IOException {
    // 各ファイルの先頭業を取り出す(終端になったファイルは除外)
    SortedSet<String> sortedLines = new TreeSet<String>();
    for (SortedFile sortedFile : this.sortedFiles) {
      if (sortedFile.peek() != null) {
        sortedLines.add(sortedFile.peek());
      }
    }

    if (sortedLines.isEmpty()) {
      return null;
    }

    String first = sortedLines.first();

    // 重複する行を各ファイルから取り除く
    for (SortedFile sortedFile : this.sortedFiles) {
      sortedFile.removeFromTop(first::equals);
    }

    return first;
  }

  public void close() {
    for (SortedFile sortedFile : sortedFiles) {
      try {
        sortedFile.close();
      } catch (Exception ex) {
        // ignore
      }
    }
  }

  private List<Path> sortedFilePaths;
  private List<SortedFile> sortedFiles = new ArrayList<SortedFile>();
}
