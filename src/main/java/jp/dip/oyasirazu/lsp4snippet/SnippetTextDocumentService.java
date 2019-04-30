package jp.dip.oyasirazu.lsp4snippet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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

    /**
     * Constructor
     */
    public SnippetTextDocumentService() {
        this.textDocuments = new HashMap<String, StringBuilder>();
    }

    /**
     * ファイル・カーソル情報を受け取り、補完候補を返却する。
     *
     * 本実装では、無条件で `LabelOnly` という補完候補を表示する。
     */
    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>>  completion(CompletionParams params) {
        // `LabelOnly` を補完
        var labelOnlyItem = new CompletionItem("LabelOnly");

        // カーソル位置に `newText!!!` という文字列を挿入する
        var label = "textEdit";
        var targetText = this.textDocuments.get(params.getTextDocument().getUri());
        var cursorPosition = params.getPosition();
        var startPosition = this.calculateStartPosition(targetText, cursorPosition, label);
        var textEdit = new TextEdit(
                new Range(
                    startPosition,
                    params.getPosition()),
                "newText!!!");
        var textEditItem = new CompletionItem(label);
        textEditItem.setTextEdit(textEdit);

        // 以下のパラメーターを設定した補完アイテム
        // Label : 補完リクエストを出したときのカーソルインデックス
        // Detail: 補完リクエストを出したときのカーソル位置の文字
        var cursorIndex = this.getIndex(targetText, cursorPosition);
        var cursorIndexItem = new CompletionItem(String.valueOf(cursorIndex));
        cursorIndexItem.setDetail(String.valueOf(targetText.charAt(cursorIndex)));

        List<CompletionItem> completionItemList = new ArrayList<>();
        completionItemList.add(labelOnlyItem);
        completionItemList.add(textEditItem);
        completionItemList.add(cursorIndexItem);


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
