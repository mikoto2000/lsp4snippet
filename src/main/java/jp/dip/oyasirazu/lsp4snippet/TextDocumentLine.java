package jp.dip.oyasirazu.lsp4snippet;

/**
 * TextDocumentLine
 *
 * テキストドキュメント内の 1 行を表現する。
 *
 * NOTE: textDocument が空の場合に、 endIndex が -1 になるが、 textDocument が空なんてめったにないことだから無視する。
 */
public class TextDocumentLine {

    private StringBuilder textDocument;
    private int lineNumber;
    private int startIndex;
    private int endIndex;
    private String textContent;

    /**
     * Constructor
     */
    public TextDocumentLine(StringBuilder textDocument, int lineNumber) {
        this.textDocument = textDocument;
        this.lineNumber = lineNumber;

        // 開始インデックス取得
        // TODO: 存在しない行を指定した場合はどうするか検討
        startIndex = 0;
        for (int i = 0; i < lineNumber; i++) {
            // TODO: `\r\n`, `\r` 改行コードへの対応
            startIndex = textDocument.indexOf("\n", startIndex + 1);
        }

        // indexOf で取得するのは `\n` のインデックスなので、 2 行目以降は +1 する。
        if (startIndex > 0) {
            startIndex++;
        }

        // 終了インデックスの取得
        endIndex = textDocument.indexOf("\n", startIndex);
        if (endIndex < 0) {
            endIndex = textDocument.length() - 1;
        }

        textContent = textDocument.substring(startIndex, endIndex + 1);
    }

    /**
     * この行が記述されているテキストドキュメントを取得。
     *
     * @return この行が記述されているテキストドキュメント
     */
    public StringBuilder getTextDocument() {
        return textDocument;
    }

    /**
     * 行番号取得。
     *
     * @return 行番号
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * 行の開始インデックス番号取得。
     *
     * @return 行の開始インデックス番号
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * 行の終了インデックス番号取得。
     *
     * @return 行の終了インデックス番号
     */
    public int getEndIndex() {
        return endIndex;
    }

    /**
     * 行の文字列を取得。
     *
     * @return 行の文字列
     */
    public String getTextContent() {
        return textContent;
    }

    @Override
    public String toString() {
        return String.format("{ lineNumber: %s, startIndex: %s, endIndex: %s, textContent: %s }",
                lineNumber, startIndex, endIndex, textContent);
    }
}
