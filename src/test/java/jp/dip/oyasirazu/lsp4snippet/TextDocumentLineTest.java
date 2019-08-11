package jp.dip.oyasirazu.lsp4snippet;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * TextDocumentLineTest
 */
public class TextDocumentLineTest {
    @Test
    public void testConstructor() {
        StringBuilder textDocument = new StringBuilder("abc\ndef\nghi");
        TextDocumentLine line0 = new TextDocumentLine(textDocument, 0);
        assertEquals("line0 is `abc\n`", "abc\n", line0.getTextContent());
        assertEquals("line0 line numbr is `0`", 0, line0.getLineNumber());
        assertEquals("line0 start index is `0`", 0, line0.getStartIndex());
        assertEquals("line0 end index is `3`", 3, line0.getEndIndex());

        TextDocumentLine line1 = new TextDocumentLine(textDocument, 1);
        assertEquals("line1 is `def\n`", "def\n", line1.getTextContent());
        assertEquals("line1 line numbr is `1`", 1, line1.getLineNumber());
        assertEquals("line1 start index is `4`", 4, line1.getStartIndex());
        assertEquals("line1 end index is `7`", 7, line1.getEndIndex());

        TextDocumentLine line2 = new TextDocumentLine(textDocument, 2);
        assertEquals("line2 is `ghi`", "ghi", line2.getTextContent());
        assertEquals("line2 line numbr is `2`", 2, line2.getLineNumber());
        assertEquals("line2 start index is `8`", 8, line2.getStartIndex());
        assertEquals("line2 end index is `10`", 10, line2.getEndIndex());
    }
}
