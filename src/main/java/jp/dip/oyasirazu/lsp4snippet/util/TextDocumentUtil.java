package jp.dip.oyasirazu.lsp4snippet.util;

import org.eclipse.lsp4j.Position;

/**
 * TextDocumentUtil
 */
public class TextDocumentUtil {
    private TextDocumentUtil() {}

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
}

