package jp.dip.oyasirazu.lsp4snippet.util;

import org.eclipse.lsp4j.Position;

/**
 * CompletionItemUtil
 */
public class CompletionItemUtil {

    private static final boolean IS_DEBUG = false;

    private CompletionItemUtil() {}

    /**
     * 補完中文字列の位置を取得する。
     *
     * 補完機能呼び出し時には、ラベル文字列の途中までが入力済みの場合がある。
     * その場合には、「入力済み文字列の先頭からカーソル位置まで」を newText で置換する必要がある。
     * そのため、入力済み文字列から、ラベル文字列の先頭位置を計算する必要がある。
     * このメソッドは、それを計算するためのメソッドである。
     *
     * <code>
     * test labe(ここで補完機能を呼び出す)
     *      ^^^^ ... この範囲を置換する必要がある
     * </code>
     *
     * @param textDocument 補完文字列位置を取得したいテキストドキュメント
     * @param cursorPosition カーソル位置を表す Position インスタンス
     * @param label 補完候補のラベル文字列
     * @return 補完中文字列の位置を表す Position インスタンス
     */
    public static Position getCompletingStringPosition(
            StringBuilder textDocument,
            Position cursorPosition,
            String label) {
        int labelLength = label.length();
        if (IS_DEBUG) {
            System.err.printf("labelLength: %d.\n", labelLength);
        }

        // 入力済み文字列とラベル文字列を比較するため、
        // 対象のテキストドキュメントからラベル長分だけ文字列を取得
        int cursorIndex = TextDocumentUtil.getIndex(textDocument, cursorPosition);
        int targetStringStartIndex = cursorIndex - labelLength;
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
            String inputedChars_tmp = label.substring(0, i);
            if (IS_DEBUG) {
                System.err.printf("inputedChars_tmp: %s.\n", inputedChars_tmp);
            }
            if (targetString.lastIndexOf(inputedChars_tmp) >= 0) {
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
