package org.rpgwizard.editor.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.rpgwizard.common.assets.BoardVector;
import org.rpgwizard.editor.editors.BoardEditor;

/**
 *
 * @author Joshua Michael Daly
 */
public class RemoveVectorAction extends AbstractAction {

    private final BoardEditor boardEditor;
    private final int x;
    private final int y;

    public RemoveVectorAction(BoardEditor boardEditor, int x, int y) {
        this.boardEditor = boardEditor;
        this.x = x;
        this.y = y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BoardVector result = boardEditor
                .getBoardView()
                .getCurrentSelectedLayer()
                .getLayer()
                .removeVectorAt(x, y);

        if (result == boardEditor.getSelectedObject()) {
            boardEditor.getSelectedObject().setSelectedState(false);
            boardEditor.setSelectedObject(null);
        }
    }

}
