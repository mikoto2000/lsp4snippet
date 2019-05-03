package jp.dip.oyasirazu.lsp4snippet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.TextEdit;
import org.eclipse.lsp4j.services.TextDocumentService;

import org.eclipse.lsp4j.jsonrpc.messages.Either;

import jp.dip.oyasirazu.lsp4snippet.snippet.SnippetSupplier;

/**
 * SnippetTextDocumentService
 */
public class SnippetTextDocumentService implements TextDocumentService {

    private final boolean IS_DEBUG = false;

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
                        ClassLoader.getSystemResourceAsStream("snippets/java.yaml"),
                        "UTF-8");

            this.snippetSupplier = SnippetSupplier.createFromYaml(yaml);
        } catch (IOException e) {
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

        // java のスニペットを全部追加
        var snippets = this.snippetSupplier.getSnippets("java");
        if (IS_DEBUG) {
            System.err.printf("snippets: %s\n", snippets);
        }

        var targetText = this.textDocuments.get(params.getTextDocument().getUri());
        var cursorPosition = params.getPosition();

        List<CompletionItem> completionItemList = snippets.stream().map(i ->
                {
                    var label = i.getLabel();
                    var startPosition = this.calculateStartPosition(targetText, cursorPosition, label);
                    var textEdit = new TextEdit(
                            new Range(
                                startPosition,
                                params.getPosition()),
                            i.getNewText());
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

    private int getIndex(StringBuilder text, Position position) {
        var lineIndex = position.getLine();
        var characterIndex = position.getCharacter();

        var firstOfLineIndex = 0;
        for (int i = 0; i < lineIndex; i++) {
            // TODO: `\r\n`, `\r` 改行コードへの対応
            firstOfLineIndex = text.indexOf("\n", firstOfLineIndex + 1);
        }

        var positionIndex = firstOfLineIndex + characterIndex;

        // indexOf で取得するのは `\n` のインデックスなので、 2 行目以降は +1 する。
        if (firstOfLineIndex > 0) {
            positionIndex++;
        }

        return positionIndex;
    }

    private Position calculateStartPosition(StringBuilder textDocument, Position cursorPosition, String label) {
        var labelLength = label.length();
        if (IS_DEBUG) {
            System.err.printf("labelLength: %d.\n", labelLength);
        }

        // 入力済み文字列とラベル文字列を比較するため、
        // 対象のテキストドキュメントからラベル長分だけ文字列を取得
        var cursorIndex = this.getIndex(textDocument, cursorPosition);
        var targetStringStartIndex = cursorIndex - labelLength;
        if (targetStringStartIndex < 0) {
            targetStringStartIndex = 0;
        }
        String targetString = textDocument.substring(targetStringStartIndex, cursorIndex);
        if (IS_DEBUG) {
            System.err.printf("targetString: %s.\n", targetString);
        }

        // 入力済み文字列とラベル文字列を比較し、
        // 一番長く一致する場所を探す
        // 一番長く一致した文字列が「入力済み文字列」
        // 「入力済み文字列」は `inputedChars` に格納
        String inputedChars = "";
        for (int i = labelLength; i >= 0; i--) {
            var inputedChars_tmp = label.substring(0, i);
            if (IS_DEBUG) {
                System.err.printf("inputedChars_tmp: %s.\n", inputedChars_tmp);
            }
            if (targetString.lastIndexOf(inputedChars_tmp) > 0) {
                inputedChars = inputedChars_tmp;
                break;
            }
        }
        if (IS_DEBUG) {
            System.err.printf("inputedChars: %s.\n", inputedChars);
        }

        // Range の startPosition として、「入力済み文字列の先頭」を返却
        return new Position(
                cursorPosition.getLine(),
                cursorPosition.getCharacter() - inputedChars.length());
    }
}