package jp.dip.oyasirazu.lsp4snippet.util;

import static org.junit.Assert.*;

import org.junit.Test;

import org.eclipse.lsp4j.Position;

/**
 * CompletionItemUtilTest
 */
public class CompletionItemUtilTest {
    @Test
    public void testGetCompletingStringPosition() {
        StringBuilder textDocunemt = new StringBuilder("abcdefghij");
        Position cursorPosition = new Position(0, 7);
        String label = "efgh";

        Position result = CompletionItemUtil.getCompletingStringPosition(
                textDocunemt, cursorPosition, label);

        assertEquals("Result position is 'e' index.", new Position(0, 4), result);
    }

    @Test
    public void testGetCompletingStringPosition_EmptyLabel() {
        StringBuilder textDocunemt = new StringBuilder("abcdefghij");
        Position cursorPosition = new Position(0, 7);
        String label = "";

        Position result = CompletionItemUtil.getCompletingStringPosition(
                textDocunemt, cursorPosition, label);

        assertEquals("If label is empty, then result equals cursorPosition.", new Position(0, 7), result);
    }
}
