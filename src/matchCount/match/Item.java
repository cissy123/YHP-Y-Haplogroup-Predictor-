package matchCount.match;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Item {
    private BooleanProperty selected = new SimpleBooleanProperty(false);
    final private String name;

    public Item(String name) {
        this.name = name;
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public boolean isSelected() {
        return selected.get();
    }

    public String getName() {
        return name;
    }
}
