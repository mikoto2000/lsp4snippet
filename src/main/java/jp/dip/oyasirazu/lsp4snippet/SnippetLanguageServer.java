package jp.dip.oyasirazu.lsp4snippet;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;

import java.util.concurrent.CompletableFuture;

/**
 * SnippetLanguageServer
 */
public class SnippetLanguageServer implements LanguageServer, LanguageClientAware {

    private LanguageClient client;

    /**
     * Constructor
     */
    public SnippetLanguageServer() {}

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {

        if (client != null) {
            client.logMessage( new MessageParams(MessageType.Info, "Start initialize."));
        }

        return CompletableFuture.completedFuture(null);
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

        return null;
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

