package jp.dip.oyasirazu.lsp4snippet.snippet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * SnippetSupplier
 */
public class SnippetSupplier {

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
    public SnippetSupplier() {
        snippets = new HashMap<>();
    }

    /**
     * Constructor
     *
     * @param snippets このインスタンスが提供するスニペット
     */
    public SnippetSupplier(Map<String, List<Snippet>> snippets) {
        this.snippets = snippets;
    }

    /**
     * 指定されたファイルタイプに対応するスニペットのリストを返却する。
     *
     * @param fileType ファイルタイプ
     * @return スニペットリスト
     */
    public List<Snippet> getSnippets(String fileType) {
        return this.snippets.getOrDefault(fileType, Collections.emptyList());
    }

    /**
     * 指定されたファイルタイプ・入力済み文字列に対応するスニペットのリストを返却する。
     *
     * @param fileType ファイルタイプ
     * @param inputedString 入力済み文字列
     * @return スニペットリスト
     */
    public List<Snippet> getSnippets(String fileType, String inputedString) {
        var snippetForFileType = this.snippets.getOrDefault(fileType, Collections.emptyList());

        // ラベルの先頭文字列が、入力済み文字列であるものを抽出して返却
        return snippetForFileType.stream()
                .filter(i -> i.getLabel().startsWith(inputedString))
                .collect(Collectors.toList());
    }

    public SnippetSupplier merge(SnippetSupplier other) {
        var mySnippets = new HashMap<String, List<Snippet>>(this.snippets);
        var otherSnippets = other.snippets;

        for (var key : otherSnippets.keySet()) {
            mySnippets.merge(key,
                    otherSnippets.getOrDefault(key, Collections.emptyList()),
                    (myList, otherList) -> {
                myList.addAll(otherList);
                return myList;
            });
        }

        System.err.println("merged: " + mySnippets);
        return new SnippetSupplier(mySnippets);
    }

    public static SnippetSupplier createFromYaml(InputStreamReader yamlStream) throws IOException {
        var mapper = new ObjectMapper(new YAMLFactory());
        var snippetConfig = mapper.readValue(yamlStream, SnippetConfig.class);
        return new SnippetSupplier(snippetConfig.getSnippets());
    }
} 

