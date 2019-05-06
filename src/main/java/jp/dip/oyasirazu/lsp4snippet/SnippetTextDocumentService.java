package jp.dip.oyasirazu.lsp4snippet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.services.TextDocumentService;

import org.eclipse.lsp4j.jsonrpc.messages.Either;

import jp.dip.oyasirazu.lsp4snippet.snippet.SnippetSupplier;
import jp.dip.oyasirazu.lsp4snippet.util.CompletionItemUtil;
import jp.dip.oyasirazu.lsp4snippet.util.TextDocumentUtil;

/**
 * SnippetTextDocumentService
 */
public class SnippetTextDocumentService implements TextDocumentService {

    private final boolean IS_DEBUG = false;

    private final Pattern PATTERN_INDENT_DEFAULT = Pattern.compile("^\\s+");

    /**
     * このサービスが管理するテキストドキュメント
     *
     * Key: uri, Value: textContent
     */
    private Map<String, StringBuilder> textDocuments;

    private SnippetSupplier snippetSupplier;

    /**
     * Constructor
     */
    public SnippetTextDocumentService() {
        this.textDocuments = new HashMap<String, StringBuilder>();

        // ビルトイン設定を読み込んで SnippetSupplier をインスタンス化
        try {
            var yaml = new InputStreamReader(
                        ClassLoader.getSystemResourceAsStream("snippets/md.yaml"),
                        "UTF-8");

            this.snippetSupplier = SnippetSupplier.createFromYaml(yaml);
        } catch (IOException e) {
            // TODO: 例外送出
            System.err.printf("Catch exception: %s\n", e);
        }
    }

    /**
     * ファイル・カーソル情報を受け取り、補完候補を返却する。
     *
     * 本実装では、無条件で `LabelOnly` という補完候補を表示する。
     */
    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>>  completion(CompletionParams params) {

        // ファイル拡張子取得
        // TODO: 拡張子 -> filetype 変換まで面倒見るようにしたい
        var fileExtension = TextDocumentUtil.getFileExtension(params.getTextDocument());
        if (IS_DEBUG) {
            System.err.printf("fileExtension: %s\n", fileExtension);
        }

        // 「入力済み文字列」を取得
        // 入力済み文字列: カーソル位置直前の 「/\w/(単語にマッチする正規表現)」
        var targetUri = params.getTextDocument().getUri();
        var targetText = this.textDocuments.get(targetUri);
        var cursorPosition = params.getPosition();
        var inputedChars = TextDocumentUtil.getInputedChars(targetText, cursorPosition);
        if (IS_DEBUG) {
            System.err.printf("inputedChars: %s\n", inputedChars);
        }

        // 既存インデント文字列取得
        // 既存インデント文字列: カーソル行の「/^\s+/(空白文字列にマッチする正規表現)」
        // TODO: メソッド化
        var cursorPositionIndex = TextDocumentUtil.getIndex(targetText, cursorPosition);
        var topToCursorString = targetText.substring(0, cursorPositionIndex);
        var cursorLineStartIndex = topToCursorString.lastIndexOf("\n");
        if (IS_DEBUG) {
            System.err.printf("cursorLineStartIndex(org): %s\n", cursorLineStartIndex);
        }
        if (cursorLineStartIndex <= 0) {
            cursorLineStartIndex = 0;
        } else {
            cursorLineStartIndex++;
        }
        if (IS_DEBUG) {
            System.err.printf("cursorLineStartIndex: %s\n", cursorLineStartIndex);
            System.err.printf("cursorPositionIndex: %s\n", cursorPositionIndex);
        }
        var topToCursorOfLineString = targetText.substring(
                cursorLineStartIndex,
                cursorPositionIndex);

        var indentChars = "";
        var indentMatcher = PATTERN_INDENT_DEFAULT.matcher(topToCursorOfLineString);
        if (indentMatcher.find()) {
            indentChars = indentMatcher.group();
        }

        // 改行文字の後ろに indentChars を追加することで、 2 行目以降のインデントを保つ
        final var indentReplaceChars = "\n" + indentChars;

        // 「ファイル拡張子」と「入力済み文字列」にマッチするスニペットを取得
        var snippets = this.snippetSupplier.getSnippets(fileExtension, inputedChars);
        if (IS_DEBUG) {
            System.err.printf("snippets: %s\n", snippets);
        }

        List<CompletionItem> completionItemList = snippets.stream().map(i ->
                {
                    var label = i.getLabel();
                    var startPosition = CompletionItemUtil.getCompletingStringPosition(targetText, cursorPosition, label);

                    // インデントを保つために改行文字を置換
                    var newText = i.getNewText().replaceAll("\n", indentReplaceChars);

                    var textEdit = new TextEdit(
                            new Range(
                                startPosition,
                                params.getPosition()),
                            newText);
                    var textEditItem = new CompletionItem(label);
                    textEditItem.setTextEdit(textEdit);
                    textEditItem.setDetail(i.getDescription());

                    return textEditItem;
                }).collect(Collectors.toList());

        return CompletableFuture.completedFuture(Either.forLeft(completionItemList));
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        var uri = params.getTextDocument().getUri();
        var textContent = new StringBuilder(params.getTextDocument().getText());

        this.textDocuments.put(uri, textContent);
    }

    /**
     * ファイル更新通知を受け付ける。
     *
     * 本実装においては、 `TextDocumentSyncKind.Full` が指定されているものとして実装する。
     */
    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        var uri = params.getTextDocument().getUri();

        // `TextDocumentSyncKind.Full` を指定している前提で、
        // ContentChanges の先頭テキストにファイル全文が入っているものとして処理する。
        var textContent = new StringBuilder(params.getContentChanges().get(0).getText());

        this.textDocuments.put(uri, textContent);
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
    }
}
