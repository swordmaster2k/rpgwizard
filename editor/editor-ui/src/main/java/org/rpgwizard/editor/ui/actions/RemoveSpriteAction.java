package org.rpgwizard.editor.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.rpgwizard.common.assets.BoardSprite;
import org.rpgwizard.editor.editors.BoardEditor;

/**
 *
 * @author Joshua Michael Daly
 */
public class RemoveSpriteAction extends AbstractAction {

    private final BoardEditor boardEditor;
    private final BoardSprite boardSprite;

    public RemoveSpriteAction(BoardEditor boardEditor, BoardSprite boardSprite) {
        this.boardEditor = boardEditor;
        this.boardSprite = boardSprite;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boardEditor.getBoard().removeSprite(boardSprite);
        if (boardSprite == boardEditor.getSelectedObject()) {
            boardEditor.getSelectedObject().setSelectedState(false);
            boardEditor.setSelectedObject(null);
        }
    }

}
