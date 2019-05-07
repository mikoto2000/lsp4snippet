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

    @Test
    public void testMerge() throws IOException {
        var yaml1 = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("snippet/config/Merge01.yaml"),
                        "UTF-8");
        var ss1 = SnippetSupplier.createFromYaml(yaml1);

        var yaml2 = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("snippet/config/Merge02.yaml"),
                        "UTF-8");
        var ss2 = SnippetSupplier.createFromYaml(yaml2);

        var mergedSs = ss1.merge(ss2);

        var fileType1Snippets = mergedSs.getSnippets("fileType1");
        assertEquals("fileType1 size is `4`", 4, fileType1Snippets.size());

        assertTrue("fileType1Snippets contains `{item1, item1 description, item1 newText}`",
                fileType1Snippets.contains(new Snippet("item1", "item1 description", "item1 newText")));
        assertTrue("fileType1Snippets contains `{item2, item2 description, item2 newText}`",
                fileType1Snippets.contains(new Snippet("item2", "item2 description", "item2 newText")));
        assertTrue("fileType1Snippets contains `{item3, item3 description, item3 newText}`",
                fileType1Snippets.contains(new Snippet("item3", "item3 description", "item3 newText")));
        assertTrue("fileType1Snippets contains `{item4, item4 description, item4 newText}`",
                fileType1Snippets.contains(new Snippet("item4", "item4 description", "item4 newText")));

        var fileType2Snippets = mergedSs.getSnippets("fileType2");
        assertEquals("fileType2 size is `2`", 2, fileType2Snippets.size());
        assertTrue("fileType2Snippets contains `{item1, item1 description, item1 newText}`",
                fileType1Snippets.contains(new Snippet("item1", "item1 description", "item1 newText")));
        assertTrue("fileType2Snippets contains `{item2, item2 description, item2 newText}`",
                fileType1Snippets.contains(new Snippet("item2", "item2 description", "item2 newText")));
    }
}
