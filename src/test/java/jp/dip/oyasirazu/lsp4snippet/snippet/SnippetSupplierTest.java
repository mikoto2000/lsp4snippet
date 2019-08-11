package jp.dip.oyasirazu.lsp4snippet.snippet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * SnippetSupplierTest
 */
public class SnippetSupplierTest {

    private static final String FILE_TYPE_BASIC = "test";

    @Test
    public void testGetSnippets_String() throws IOException {
        InputStreamReader yaml = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("snippet/config/GetSnippets_String.yaml"),
                        "UTF-8");
        SnippetSupplier ss = SnippetSupplier.createFromYaml(yaml);
        List<Snippet> snippets = ss.getSnippets(FILE_TYPE_BASIC);

        assertSame("check snippets size.", snippets.size(), 2);

        // TODO: snippets に想定通りの Snippet が含まれているかのチェック
    }

    @Test
    public void testGetSnippets_StringString() throws IOException {
        InputStreamReader yaml = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("snippet/config/GetSnippets_StringString.yaml"),
                        "UTF-8");
        SnippetSupplier ss = SnippetSupplier.createFromYaml(yaml);

        List<Snippet> snippets = ss.getSnippets(FILE_TYPE_BASIC, "");
        assertSame("check snippets size.", snippets.size(), 5);

        List<Snippet> snippets_a = ss.getSnippets(FILE_TYPE_BASIC, "a");
        assertSame("check snippets size.", snippets_a.size(), 4);

        List<Snippet> snippets_aa = ss.getSnippets(FILE_TYPE_BASIC, "aa");
        assertSame("check snippets size.", snippets_aa.size(), 3);

        List<Snippet> snippets_aaa = ss.getSnippets(FILE_TYPE_BASIC, "aaa");
        assertSame("check snippets size.", snippets_aaa.size(), 2);

        List<Snippet> snippets_aaaa = ss.getSnippets(FILE_TYPE_BASIC, "aaaa");
        assertSame("check snippets size.", snippets_aaaa.size(), 1);

        // TODO: snippets に想定通りの Snippet が含まれているかのチェック
    }

    @Test
    public void testMarkAndAlias() throws IOException {
        InputStreamReader yaml = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("snippet/config/AnchorAndAlias.yaml"),
                        "UTF-8");
        SnippetSupplier ss = SnippetSupplier.createFromYaml(yaml);

        List<Snippet> snippetsForMd = ss.getSnippets("md");
        List<Snippet> snippetsForMarkdown = ss.getSnippets("markdown");
        assertEquals("`md` equals `markdown`.", snippetsForMd, snippetsForMarkdown);
    }

    @Test
    public void testMerge() throws IOException {
        InputStreamReader yaml1 = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("snippet/config/Merge01.yaml"),
                        "UTF-8");
        SnippetSupplier ss1 = SnippetSupplier.createFromYaml(yaml1);

        InputStreamReader yaml2 = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("snippet/config/Merge02.yaml"),
                        "UTF-8");
        SnippetSupplier ss2 = SnippetSupplier.createFromYaml(yaml2);

        SnippetSupplier mergedSs = ss1.merge(ss2);

        List<Snippet> fileType1Snippets = mergedSs.getSnippets("fileType1");
        assertEquals("fileType1 size is `4`", 4, fileType1Snippets.size());

        assertTrue("fileType1Snippets contains `{item1, item1 description, item1 newText}`",
                fileType1Snippets.contains(new Snippet("item1", "item1 description", "item1 newText")));
        assertTrue("fileType1Snippets contains `{item2, item2 description, item2 newText}`",
                fileType1Snippets.contains(new Snippet("item2", "item2 description", "item2 newText")));
        assertTrue("fileType1Snippets contains `{item3, item3 description, item3 newText}`",
                fileType1Snippets.contains(new Snippet("item3", "item3 description", "item3 newText")));
        assertTrue("fileType1Snippets contains `{item4, item4 description, item4 newText}`",
                fileType1Snippets.contains(new Snippet("item4", "item4 description", "item4 newText")));

        List<Snippet> fileType2Snippets = mergedSs.getSnippets("fileType2");
        assertEquals("fileType2 size is `2`", 2, fileType2Snippets.size());
        assertTrue("fileType2Snippets contains `{item1, item1 description, item1 newText}`",
                fileType1Snippets.contains(new Snippet("item1", "item1 description", "item1 newText")));
        assertTrue("fileType2Snippets contains `{item2, item2 description, item2 newText}`",
                fileType1Snippets.contains(new Snippet("item2", "item2 description", "item2 newText")));
    }
}
