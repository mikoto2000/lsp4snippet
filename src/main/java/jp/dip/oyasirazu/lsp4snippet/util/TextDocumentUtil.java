package jp.dip.oyasirazu.lsp4snippet.util;

import java.util.regex.Pattern;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentIdentifier;

/**
 * TextDocumentUtil
 */
public class TextDocumentUtil {

    private static final boolean IS_DEBUG = false;

    private static final Pattern PATTERN_WORD_DEFAULT = Pattern.compile("\\w+$");

    private TextDocumentUtil() {}

    /**
     * 指定されたテキストドキュメントの拡張子を取得する。
     *
     * @param textDocumentIdentifier テキストドキュメント
     * @return テキストドキュメントの拡張子
     */
    public static String getFileExtension(TextDocumentIdentifier textDocumentIdentifier) {
        var targetUri = textDocumentIdentifier.getUri();
        var targetUriLastDotIndex = targetUri.lastIndexOf(".");

        // 拡張子が無い場合には空文字を返却する
        if (targetUriLastDotIndex < 0) {
            return "";
        }

        // 拡張子が存在する場合には、拡張子を返却する
        return targetUri.substring(targetUriLastDotIndex + 1);
    }

    /**
     * text 内の position を示すインデックス番号を取得する。
     *
     * @param text インデックスを取得したいテキストドキュメント文字列
     * @param position テキスト内の位置を表す Position
     */
    public static int getIndex(StringBuilder text, Position position) {
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

    public static String getInputedChars(StringBuilder textDocument, Position cursorPosition) {
        var cursorPositionIndex = TextDocumentUtil.getIndex(textDocument, cursorPosition);

        // TODO: `\r\n`, `\r` 改行コードへの対応
        var topToCursorString = textDocument.substring(0, cursorPositionIndex);
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

        // カーソル行の先頭からカーソルまでの文字列を取得
        var topToCursorOfLineString = textDocument.substring(
                cursorLineStartIndex,
                cursorPositionIndex);
        if (IS_DEBUG) {
            System.err.printf("topToCursorOfLine: %s\n", topToCursorOfLineString);
        }

        // 正規表現で、 topToCursorOfLine の末尾に単語マッチがあるか確認
        //     - ある: その単語が inputedChars
        //     - ない: 空文字が inputedChars
        var inputedChars = "";
        var lastWordMatcher = PATTERN_WORD_DEFAULT.matcher(topToCursorOfLineString);
        if (lastWordMatcher.find()) {
            inputedChars = lastWordMatcher.group();
        }
        if (IS_DEBUG) {
            System.err.printf("inputedChars: %s\n", inputedChars);
        }

        return inputedChars;
    }
}

