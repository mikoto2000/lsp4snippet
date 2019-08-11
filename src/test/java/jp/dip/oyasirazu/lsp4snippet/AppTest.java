/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package jp.dip.oyasirazu.lsp4snippet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import jp.dip.oyasirazu.lsp4snippet.App.Options;

import static org.junit.Assert.*;

public class AppTest {
    @Test
    public void testParseArgs() {
        Options options1 = App.parseArgs(new String[]{"--snippet", "snip1"});
        assertTrue("snippet contains `snip1`", options1.getSnippetFilePaths().contains("snip1"));

        Options options2 = App.parseArgs(new String[]{"--snippet", "snip1", "--snippet", "snip2"});
        assertTrue("snippet contains `snip1`", options2.getSnippetFilePaths().contains("snip1"));
        assertTrue("snippet contains `snip2`", options2.getSnippetFilePaths().contains("snip2"));
    }

    @Test
    public void testParseArgs_Empty() {
        Options options1 = App.parseArgs(new String[]{"--snippet", "snip1"});
        assertEquals("snippet size is `0`", 1, options1.getSnippetFilePaths().size());
    }

    @Test
    public void testFindSnippets() throws IOException {
        List<String> snippets = Arrays.asList("./src/test/resources/snippet/**/*.yaml");

        List<String> founds = App.findSnippets(snippets);
        assertEquals("snippet count is `5`", 5, founds.size());
    }

    @Test
    public void testFindSnippets_noGlob() throws IOException {
        List<String> snippets = Arrays.asList(
                "./src/test/resources/snippet/config/Merge01.yaml",
                "./src/test/resources/snippet/config/Merge02.yaml");

        List<String> founds = App.findSnippets(snippets);
        assertEquals("snippet count is `2`", 2, founds.size());
    }
}
