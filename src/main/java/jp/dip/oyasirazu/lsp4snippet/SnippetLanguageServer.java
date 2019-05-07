package jp.dip.oyasirazu.lsp4snippet;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * SnippetLanguageServer
 */
public class SnippetLanguageServer implements LanguageServer, LanguageClientAware {

    private LanguageClient client;

    private List<String> snippetFilePaths;

    /**
     * Constructor
     */
    public SnippetLanguageServer(List<String> snippetFilePaths) {
        this.snippetFilePaths = snippetFilePaths;
    }

    /**
     * 初期化パラメーターを受け取って結果を返す
     *
     * 本 LSP 実装では、以下の条件で初期化を行う。
     * <ul>
     *   <li>完全同期(同期ごとにフルテキストをやり取り)</li>
     *   <li>コード補完機能有効</li>
     * </ul>
     */
    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {

        if (client != null) {
            client.logMessage( new MessageParams(MessageType.Info, "Start initialize."));
        }

        // Access `https://microsoft.github.io/language-server-protocol/specification`
        // and search `ServerCapabilities`(contained 'completionProvider' property),
        // and search `CompletionOptions`.

        // 補完オプションの作成
        var completionOptions = new CompletionOptions();
        // コード補完を ON にする
        completionOptions.setResolveProvider(true);

        // Access `https://microsoft.github.io/language-server-protocol/specification`
        // and search `ServerCapabilities`(contained 'textDocumentSync' property),
        // and search `TextDocumentSyncKind`.

        // サーバーのサポート機能を表すオブジェクトを作成
        var capabilities = new ServerCapabilities();
        // クライアントからサーバーに対して、毎回ドキュメント全体の送信を行う。
        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);
        // 先ほど作った補完オプションを capabilities に設定
        capabilities.setCompletionProvider(completionOptions);

        // Access `https://microsoft.github.io/language-server-protocol/specification`
        // and search `InitializeResult`(contained 'capabilities' property),

        // InitializeResult を CompletableFuture にラップして返却
        var result = new InitializeResult(capabilities);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public void initialized(InitializedParams params) {
        if (client != null) {
            client.logMessage( new MessageParams(MessageType.Info, "Start initialized."));
        }
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        if (client != null) {
            client.logMessage( new MessageParams(MessageType.Info, "shutdown received"));
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void exit() {
        if (client != null) {
            client.logMessage( new MessageParams(MessageType.Info, "Start exit."));
        }
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        if (client != null) {
            client.logMessage( new MessageParams(MessageType.Info, "Start getTextDocumentService."));
        }

        return new SnippetTextDocumentService(snippetFilePaths);
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        if (client != null) {
            client.logMessage( new MessageParams(MessageType.Info, "Start getWorkspaceService."));
        }

        return null;
    }

    @Override
    public void connect(LanguageClient client) {
        this.client = client;

        if (client != null) {
            client.logMessage( new MessageParams(MessageType.Info, "Start connect."));
        }
    }
}

