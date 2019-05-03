package jp.dip.oyasirazu.lsp4snippet.snippet;

import java.util.List;
import java.util.Map;

/**
 * SnippetConfig
 */
public class SnippetConfig {

    /**
     * スニペット管理用 Map。
     *
     * <ul>
     * <li>key: file type</li>
     * <li>value: snippet list</li>
     * </ul>
     */
    private Map<String, List<Snippet>> snippets;

    /**
     * Constructor
     */
    public SnippetConfig() {}

    public void setSnippets(Map<String, List<Snippet>> snippets) {
        this.snippets = snippets;
    }

    public Map<String, List<Snippet>> getSnippets() {
        return snippets;
    }
}


