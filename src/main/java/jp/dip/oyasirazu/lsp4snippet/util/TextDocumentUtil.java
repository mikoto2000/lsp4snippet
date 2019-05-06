package jp.dip.oyasirazu.lsp4snippet.util;

import java.util.regex.Pattern;

import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentIdentifier;

import jp.dip.oyasirazu.lsp4snippet.TextDocumentLine;

/**
 * TextDocumentUtil
 */
public class TextDocumentUtil {

    private static final boolean IS_DEBUG = false;

    private static final Pattern PATTERN_WORD_DEFAULT = Pattern.compile("\\w+$");

    // インデントとして認識する文字列
    // 行頭の半角スペースかタブで構成された文字列にマッチする
    private static final Pattern PATTERN_INDENT_DEFAULT = Pattern.compile("^[\\t ]+");

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
        var cursorLine = new TextDocumentLine(textDocument, cursorPosition.getLine());

        // カーソル行の先頭からカーソルまでの文字列を取得
        var topToCursorOfLineString = cursorLine.getTextContent().substring(
                0, cursorPosition.getCharacter());
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

    public static String getIndentChars(StringBuilder textDocument, int lineNumber) {
        var cursorLine = new TextDocumentLine(textDocument, lineNumber);

        var indentChars = "";
        var indentMatcher = PATTERN_INDENT_DEFAULT.matcher(cursorLine.getTextContent());
        if (indentMatcher.find()) {
            indentChars = indentMatcher.group();
        }
        if (IS_DEBUG) {
            System.err.printf("indentChars: %s\n", indentChars);
        }

        return indentChars;
    }
}

