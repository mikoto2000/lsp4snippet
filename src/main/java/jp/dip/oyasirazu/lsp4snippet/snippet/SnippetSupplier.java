package jp.dip.oyasirazu.lsp4snippet.snippet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

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
        List<Snippet> snippetForFileType = this.snippets.getOrDefault(fileType, Collections.emptyList());

        // ラベルの先頭文字列が、入力済み文字列であるものを抽出して返却
        return snippetForFileType.stream()
                .filter(i -> i.getLabel().startsWith(inputedString))
                .collect(Collectors.toList());
    }

    public SnippetSupplier merge(SnippetSupplier other) {
        HashMap<String, List<Snippet>> mySnippets = new HashMap<String, List<Snippet>>(this.snippets);
        Map<String, List<Snippet>> otherSnippets = other.snippets;

        for (String key : otherSnippets.keySet()) {
            mySnippets.merge(key,
                    otherSnippets.getOrDefault(key, Collections.emptyList()),
                    (myList, otherList) -> {
                myList.addAll(otherList);
                return myList;
            });
        }

        return new SnippetSupplier(mySnippets);
    }

    public static SnippetSupplier createFromYaml(InputStreamReader yamlStream) throws IOException {
        Yaml yaml = new Yaml(new Constructor() {
                @Override
                protected Object constructObject(Node node) {

                    if (node.getTag() == Tag.MAP) {
                        @SuppressWarnings("unchecked")
                        Map<String, String> map = (Map<String, String>)(super.constructObject(node));

                        // YAML の Hash が、 Snippet の条件を満たしているかを確認し、
                        // 満たしていれば Snippet へ詰め替える。
                        if (map.containsKey("label")
                                    && map.containsKey("description")
                                    && map.containsKey("newText")) {
                            return new Snippet(
                                    map.get("label"),
                                    map.get("description"),
                                    map.get("newText"));
                        }
                    }

                    // Snippet じゃなかった場合は、デフォルトの挙動
                    return super.constructObject(node);
                }
        });
        SnippetConfig snippetConfig = yaml.loadAs(yamlStream, SnippetConfig.class);
        return new SnippetSupplier(snippetConfig.getSnippets());
    }
}

