package jp.dip.oyasirazu.lsp4snippet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.services.TextDocumentService;

import org.eclipse.lsp4j.jsonrpc.messages.Either;

/**
 * SnippetTextDocumentService
 */
public class SnippetTextDocumentService implements TextDocumentService {
    /**
     * Constructor
     */
    public SnippetTextDocumentService() {}

    /**
     * ファイル・カーソル情報を受け取り、補完候補を返却する。
     *
     * 本実装では、無条件で `LabelOnly` という補完候補を表示する。
     */
    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>>  completion(CompletionParams params) {
        var completionItem = new CompletionItem("LabelOnly");
        List<CompletionItem> completionItemList = new ArrayList<>();
        completionItemList.add(completionItem);
        return CompletableFuture.completedFuture(Either.forLeft(completionItemList));
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
    }
}
