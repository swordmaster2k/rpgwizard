package org.rpgwizard.editor.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.rpgwizard.common.assets.BoardLayerImage;
import org.rpgwizard.editor.editors.BoardEditor;

/**
 *
 * @author Joshua Michael Daly
 */
public class RemoveLayerImageAction extends AbstractAction {

    private final BoardEditor boardEditor;
    private final BoardLayerImage boardLayerImage;

    public RemoveLayerImageAction(BoardEditor boardEditor, BoardLayerImage boardLayerImage) {
        this.boardEditor = boardEditor;
        this.boardLayerImage = boardLayerImage;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boardEditor.getBoard().removeLayerImage(boardLayerImage);
        if (boardLayerImage == boardEditor.getSelectedObject()) {
            boardEditor.getSelectedObject().setSelectedState(false);
            boardEditor.setSelectedObject(null);
        }
    }

}
