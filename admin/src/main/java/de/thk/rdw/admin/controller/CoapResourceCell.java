package de.thk.rdw.admin.controller;

import de.thk.rdw.admin.model.GuiCoapResource;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;

public class CoapResourceCell extends TreeCell<GuiCoapResource> {

	private ContextMenu menu = new ContextMenu();

	public CoapResourceCell() {
		// TODO Read text from bundle.
		MenuItem connect = new MenuItem("Connect");
		connect.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				System.out.println("Pressed \"Connect\" on resource \"" + getItem().getName() + "\".");
			}
		});
		menu.getItems().add(connect);
	}

	@Override
	protected void updateItem(GuiCoapResource item, boolean empty) {
		super.updateItem(item, empty);
		if (empty || item == null) {
			setText(null);
			setGraphic(null);
		} else {
			setText(item.getName());
			setContextMenu(menu);
		}
	}
}