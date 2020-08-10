/*
 * SortedFile
 *
 * All Rights Reserved, Copyright (C) 2019 echoview2
 *
 */

package echoview.sample.files;

import java.io.BufferedReader;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.function.Predicate;

/**
 * 
 * @author       Takahiro Sato
 * @version      1.0
 */
public class SortedFile implements AutoCloseable {
  public SortedFile(Path path) throws IOException {
    this.path = path;
    this.reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
    this.topLine = this.reader.readLine();
  }

  /**
   * ファイルの先頭を取り出す. ファイルの終端までくると, nullを返す.
   */
  public String peek() throws IOException {
    return this.topLine;
  }

  /**
   *  ファイルの終端か, 条件が成立する間ファイルを読み出す
   */
  public void removeFromTop(Predicate<String> predicator) throws IOException {
    while ((peek() != null) &&  predicator.test(this.topLine)) {
      // ファイルから先頭行を取り出す
      this.topLine = this.reader.readLine();
    }
  }

  public void close() throws IOException {
    this.reader.close();
  }

  private BufferedReader reader;
  private Path path;
  private String topLine;
}
