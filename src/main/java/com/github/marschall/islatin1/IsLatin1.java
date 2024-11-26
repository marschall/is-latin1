package com.github.marschall.islatin1;

import static java.nio.channels.FileChannel.MapMode.READ_ONLY;
import static java.nio.file.StandardOpenOption.READ;
import static jdk.incubator.vector.VectorOperators.EQ;
import static jdk.incubator.vector.VectorOperators.UNSIGNED_GE;
import static jdk.incubator.vector.VectorOperators.UNSIGNED_LE;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;

import jdk.incubator.vector.ByteVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorSpecies;

public class IsLatin1 {

  private static final ByteOrder BYTE_ORDER = ByteOrder.nativeOrder();
  private static final VectorSpecies<Byte> SPECIES = ByteVector.SPECIES_PREFERRED;

  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("usage: path");
      System.exit(1);
    }
    Path file = Path.of(args[0]);
    long index = 0;
    try {
      index = isLatin1(file);
    } catch (IOException e) {
      System.err.println(e.getMessage());
      System.exit(1);
    }
    if (index == -1) {
      System.out.println("file: " + file + " is ISO-8859-1");
      System.exit(0);
    } else {
      System.err.println("file: " + file + " not is ISO-8859-1, byte at index: " + index + " is not ISO-8859-1");
      System.exit(1);
    }

  }

  static long isLatin1(Path file) throws IOException {
    try (FileChannel channel = FileChannel.open(file, READ)) {
      long fileSize = channel.size();
      FileLock fileLock = channel.lock(0, fileSize, true);
      try (Arena arena = Arena.ofConfined()) {
        MemorySegment segment = channel.map(READ_ONLY, 0, fileSize, arena);
        return isLatin1(segment);
      } finally {
        fileLock.release();
      }
    }
  }

  static long isLatin1(MemorySegment segment) {
    long i = 0;
    ByteVector lf = ByteVector.broadcast(SPECIES, (byte) 0x0A);
    ByteVector cr = ByteVector.broadcast(SPECIES, (byte) 0x0D);
    ByteVector sp = ByteVector.broadcast(SPECIES, (byte) 0x20);
    ByteVector tilde = ByteVector.broadcast(SPECIES, (byte) 0x7E);
    ByteVector nbsp = ByteVector.broadcast(SPECIES, (byte) 0xA0);
    long upperBound = SPECIES.loopBound(segment.byteSize());
    for (; i < upperBound; i += SPECIES.length()) {
      ByteVector vector = ByteVector.fromMemorySegment(SPECIES, segment, i, BYTE_ORDER);
      VectorMask<Byte> isLf = vector.compare(EQ, lf);
      VectorMask<Byte> isCr = vector.compare(EQ, cr);
      VectorMask<Byte> asciiLowRange = vector.compare(UNSIGNED_GE, sp);
      VectorMask<Byte> isAscii = vector.compare(UNSIGNED_LE, tilde, asciiLowRange);
      VectorMask<Byte> isHighRange = vector.compare(UNSIGNED_GE, nbsp);

      VectorMask<Byte> notLatin1 = isLf.or(isCr).or(isAscii).or(isHighRange).not();
      int firstNonLatin1 = notLatin1.firstTrue();
      if (firstNonLatin1 != SPECIES.length()) {
        return i + firstNonLatin1;
      }
    }
    for (; i < segment.byteSize(); i++) {
      byte b = segment.get(ValueLayout.JAVA_BYTE, i);
      if (!isLatin1(b)) {
        return i;
      }
    }
    return -1;
  }

  static boolean isLatin1(byte b) {
    int i = Byte.toUnsignedInt(b);
    return i == 0x0A | i == 0x0D
        | (i >= 0x20 & i <= 0x7E)
        | i >= 0xA0;
  }

}
