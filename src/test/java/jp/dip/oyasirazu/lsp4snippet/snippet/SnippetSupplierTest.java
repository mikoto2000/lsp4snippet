package jp.dip.oyasirazu.lsp4snippet.snippet;

import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * SnippetSupplierTest
 */
public class SnippetSupplierTest {

    private static final String FILE_TYPE_BASIC = "test";

    @Test
    public void testGetSnippets_String() throws IOException {
        var yaml = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("snippet/config/GetSnippets_String.yaml"),
                        "UTF-8");
        var ss = SnippetSupplier.createFromYaml(yaml);
        var snippets = ss.getSnippets(FILE_TYPE_BASIC);

        assertSame("check snippets size.", snippets.size(), 2);

        // TODO: snippets に想定通りの Snippet が含まれているかのチェック
    }

    @Test
    public void testGetSnippets_StringString() throws IOException {
        var yaml = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("snippet/config/GetSnippets_StringString.yaml"),
                        "UTF-8");
        var ss = SnippetSupplier.createFromYaml(yaml);

        var snippets = ss.getSnippets(FILE_TYPE_BASIC, "");
        assertSame("check snippets size.", snippets.size(), 5);

        var snippets_a = ss.getSnippets(FILE_TYPE_BASIC, "a");
        assertSame("check snippets size.", snippets_a.size(), 4);

        var snippets_aa = ss.getSnippets(FILE_TYPE_BASIC, "aa");
        assertSame("check snippets size.", snippets_aa.size(), 3);

        var snippets_aaa = ss.getSnippets(FILE_TYPE_BASIC, "aaa");
        assertSame("check snippets size.", snippets_aaa.size(), 2);

        var snippets_aaaa = ss.getSnippets(FILE_TYPE_BASIC, "aaaa");
        assertSame("check snippets size.", snippets_aaaa.size(), 1);

        // TODO: snippets に想定通りの Snippet が含まれているかのチェック
    }
}
