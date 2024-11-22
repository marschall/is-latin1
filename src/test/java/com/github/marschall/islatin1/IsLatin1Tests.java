package com.github.marschall.islatin1;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class IsLatin1Tests {

  @Test
  void isLatin1Byte() {
    for (int i = 0; i < 256; i++) {
      byte b = (byte) i;
      if (i == 0x0A) {
        assertTrue(IsLatin1.isLatin1(b));
      } else if (i == 0x0D) {
        assertTrue(IsLatin1.isLatin1(b));
      } else if (i >= 0x20 && i <= 0x7E) {
        assertTrue(IsLatin1.isLatin1(b));
      } else if (i >= 0xA0) {
        int j = i;
        assertTrue(IsLatin1.isLatin1(b), () -> "Byte: 0x" + Integer.toHexString(j) + " should be ISO-8859-1");
      } else {
        assertFalse(IsLatin1.isLatin1(b));
      }
    }
  }

  @Test
  void isLatin1Segment() {
    // doesn't fit into 512 bit vector
    int arraySize = (512 / 8) + 8;
    byte[] array = new byte[arraySize];
    MemorySegment memorySegment = MemorySegment.ofArray(array);
    for (int i = 0; i < 256; i++) {
      byte b = (byte) i;
      if (IsLatin1.isLatin1(b)) {
        assertLatin1Segment(memorySegment, array, b);
      } else {
        assertNotLatin1Segment(memorySegment, b);
      }
    }
  }

  private static void assertLatin1Segment(MemorySegment memorySegment, byte[] array, byte b) {
    memorySegment.fill(b);
    assertEquals(-1L, IsLatin1.isLatin1(memorySegment));
  }

  private static void assertNotLatin1Segment(MemorySegment memorySegment, byte b) {
    memorySegment.fill((byte) 'A');

    // flip last element
    memorySegment.set(ValueLayout.JAVA_BYTE, memorySegment.byteSize() - 1, b);
    assertEquals(memorySegment.byteSize() - 1, IsLatin1.isLatin1(memorySegment));
    
    // flip second to last element
    memorySegment.set(ValueLayout.JAVA_BYTE, memorySegment.byteSize() - 2, b);
    assertEquals(memorySegment.byteSize() - 2, IsLatin1.isLatin1(memorySegment));
    
    // flip second element
    memorySegment.set(ValueLayout.JAVA_BYTE, 1L, b);
    assertEquals(1L, IsLatin1.isLatin1(memorySegment));
    
    // flip first element
    memorySegment.set(ValueLayout.JAVA_BYTE, 0L, b);
    assertEquals(0L, IsLatin1.isLatin1(memorySegment));
  }

}
