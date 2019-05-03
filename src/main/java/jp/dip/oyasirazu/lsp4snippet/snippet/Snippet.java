package jp.dip.oyasirazu.lsp4snippet.snippet;

public class Snippet {
    private String label;
    private String description;
    private String newText;

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setNewText(String newText) {
        this.newText = newText;
    }

    public String getNewText() {
        return newText;
    }

    public String toString() {
        return String.format("{label: %s, description: %s, newText: %s}", label, description, newText);
    }
}

