package jp.dip.oyasirazu.lsp4snippet.util;

import static org.junit.Assert.*;

import org.junit.Test;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentIdentifier;

/**
 * TextDocumentUtilTest
 */
public class TextDocumentUtilTest {

    @Test
    public void testGetFileExtension() {
        var textDocument = new TextDocumentIdentifier("test.md");
        var fileExtension = TextDocumentUtil.getFileExtension(textDocument);
        assertEquals("If `test.md` then `md`.", "md", fileExtension);
    }

    @Test
    public void testGetFileExtension_noExtension() {
        var textDocument = new TextDocumentIdentifier("test");
        var fileExtension = TextDocumentUtil.getFileExtension(textDocument);
        assertEquals("If no extension then empty string.", "", fileExtension);
    }

    @Test
    public void testGetIndex_firstLine() {
        var textDocument = new StringBuilder("123\n456\n789\n");

        // 先頭
        var position_0_0 = new Position(0, 0);
        int position_0_0_index = TextDocumentUtil.getIndex(textDocument, position_0_0);
        assertEquals("new Position(0, 0) then index is 0.", 0, position_0_0_index);

        // 行末文字
        var position_0_2 = new Position(0, 2);
        int position_0_2_index = TextDocumentUtil.getIndex(textDocument, position_0_2);
        assertEquals("new Position(0, 2) then index is 2.", 2, position_0_2_index);

        // 改行文字
        var position_0_3 = new Position(0, 3);
        int position_0_3_index = TextDocumentUtil.getIndex(textDocument, position_0_3);
        assertEquals("new Position(0, 3) then index is 3.", 3, position_0_3_index);
    }

    @Test
    public void testGetIndex_notFirstLine() {
        var textDocument = new StringBuilder("123\n456\n789\n");

        // 先頭
        var position_1_0 = new Position(1, 0);
        int position_1_0_index = TextDocumentUtil.getIndex(textDocument, position_1_0);
        assertEquals("new Position(1, 0) then index is 4.", 4, position_1_0_index);

        // 行末文字
        var position_1_2 = new Position(1, 2);
        int position_1_2_index = TextDocumentUtil.getIndex(textDocument, position_1_2);
        assertEquals("new Position(1, 2) then index is 6.", 6, position_1_2_index);

        // 改行文字
        var position_1_3 = new Position(1, 3);
        int position_1_3_index = TextDocumentUtil.getIndex(textDocument, position_1_3);
        assertEquals("new Position(1, 3) then index is 7.", 7, position_1_3_index);

        // 3 行目行頭
        var position_2_0 = new Position(2, 0);
        int position_2_0_index = TextDocumentUtil.getIndex(textDocument, position_2_0);
        assertEquals("new Position(2, 0) then index is 8.", 8, position_2_0_index);
    }

    @Test
    public void testGetInputedChars() {
        {
            var textDocument = new StringBuilder("123 456\nabc defg\n");
            var cursorPosition = new Position(0, 2);

            var inputedChars = TextDocumentUtil.getInputedChars(textDocument, cursorPosition);
            assertEquals("`12|3 456`, then return `12`.", "12", inputedChars);
        }

        {
            var textDocument = new StringBuilder("123 456\nabc defg\n");
            var cursorPosition = new Position(0, 7);

            var inputedChars = TextDocumentUtil.getInputedChars(textDocument, cursorPosition);
            assertEquals("`123 456|`, then return `456`.", "456", inputedChars);
        }

        {
            var textDocument = new StringBuilder("123 456\nabc defg\n");
            var cursorPosition = new Position(0, 10);

            var inputedChars = TextDocumentUtil.getInputedChars(textDocument, cursorPosition);
            assertEquals("`123 456\nab|c def`, then return `ab`.", "ab", inputedChars);
        }
    }

    @Test
    public void testGetInputedChars_Empty() {
        var textDocument = new StringBuilder("");
        var cursorPosition = new Position(0, 0);

        var inputedChars = TextDocumentUtil.getInputedChars(textDocument, cursorPosition);
        assertEquals("textDocument is empty, then return empty string.", "", inputedChars);

    }
}

