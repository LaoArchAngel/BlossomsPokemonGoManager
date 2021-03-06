package me.corriekay.pokegoutil.utils.windows.renderer;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.swing.JTable;

import me.corriekay.pokegoutil.utils.StringLiterals;
import me.corriekay.pokegoutil.utils.windows.WindowStuffHelper;

/**
 * A cell renderer that displays percentages in the format "xx.xx%".
 */
public class FutureCellRenderer extends DefaultCellRenderer {

    private Map<String, String> cellCache = new HashMap<>();

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
                                                   final boolean hasFocus, final int rowIndex, final int columnIndex) {
        setNativeLookAndFeel(table, isSelected);

        final String resolvedValue = cellCache.get(rowIndex + StringLiterals.CONCAT_SEPARATOR + columnIndex);
        if (resolvedValue != null) {
            setText(resolvedValue);
            setToolTipText(resolvedValue);
        } else {
            // We set a default text. Must be this long so that the column width is set correctly
            setText("... Loading ...                              ");

            @SuppressWarnings("unchecked")
            final CompletableFuture<String> future = (CompletableFuture<String>) value;
            future.thenAcceptAsync((String text) -> asyncSetValue(text, table, rowIndex, columnIndex));
        }
        return this;
    }

    /**
     * Sets the text and tooltip when the value is resolved.
     *
     * @param textValue   The text to set.
     * @param table       The table.
     * @param rowIndex    The row index.
     * @param columnIndex The column index.
     */
    public void asyncSetValue(final String textValue, final JTable table, final int rowIndex, final int columnIndex) {
        setText(textValue);
        setToolTipText(textValue);
        cellCache.put(rowIndex + StringLiterals.CONCAT_SEPARATOR + columnIndex, textValue);

        // We need the cell repainted
        WindowStuffHelper.fireCellChanged(table, rowIndex, columnIndex);
    }
}
