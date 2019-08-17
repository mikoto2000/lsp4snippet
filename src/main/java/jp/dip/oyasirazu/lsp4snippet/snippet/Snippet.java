package jp.dip.oyasirazu.lsp4snippet.snippet;

public class Snippet {

    // TODO: enum 化
    public static final String TYPE_SNIPPET = "snippet";
    public static final String TYPE_TEMPLATE = "template";

    private String label;
    private String description;
    private String type;
    private String newText;

    public Snippet() {}

    public Snippet(String label, String type, String description, String newText) {
        this.label = label;
        this.type = type;
        this.description = description;
        this.newText = newText;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
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

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (!(other instanceof Snippet)) {
            return false;
        }

        Snippet o = (Snippet)other;

        if (o.getLabel().equals(this.getLabel())
                && o.getType().equals(this.getType())
                && o.getDescription().equals(this.getDescription())
                && o.getNewText().equals(this.getNewText())) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = 31;
        result = 31 * result + label != null ? label.hashCode() : 0;
        result = 31 * result + type != null ? type.hashCode() : 0;
        result = 31 * result + description != null ? description.hashCode() : 0;
        result = 31 * result + newText != null ? newText.hashCode() : 0;

        return result;
    }
}

