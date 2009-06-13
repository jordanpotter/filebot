
package net.sourceforge.filebot.ui.panel.subtitle;


import java.awt.Color;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.CompoundBorder;

import net.miginfocom.swing.MigLayout;
import net.sourceforge.filebot.ResourceManager;
import net.sourceforge.tuned.ui.AbstractFancyListCellRenderer;


public class SubtitleListCellRenderer extends AbstractFancyListCellRenderer {
	
	private final JLabel titleLabel = new JLabel();
	private final JLabel languageLabel = new JLabel();
	

	public SubtitleListCellRenderer() {
		super(new Insets(5, 5, 5, 5));
		setHighlightingEnabled(false);
		
		setLayout(new MigLayout("fill, nogrid, insets 0"));
		
		add(languageLabel, "hidemode 3, w 85px!");
		add(titleLabel);
		
		setBorder(new CompoundBorder(new DashedSeparator(2, 4, Color.lightGray, Color.white), getBorder()));
	}
	

	@Override
	public void configureListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.configureListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		SubtitlePackage subtitle = (SubtitlePackage) value;
		
		titleLabel.setText(subtitle.getName());
		titleLabel.setIcon(getIcon(subtitle));
		
		if (languageLabel.isVisible()) {
			languageLabel.setText(subtitle.getLanguage().getName());
			languageLabel.setIcon(ResourceManager.getFlagIcon(subtitle.getLanguage().getCode()));
		}
		
		titleLabel.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
		languageLabel.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
		
		// don't paint border on last element
		setBorderPainted(index < list.getModel().getSize() - 1);
	}
	

	private Icon getIcon(SubtitlePackage subtitle) {
		switch (subtitle.getDownload().getPhase()) {
			case PENDING:
				return ResourceManager.getIcon(subtitle.getArchiveType() != ArchiveType.UNDEFINED ? "bullet.green" : "bullet.yellow");
			case DOWNLOADING:
				return ResourceManager.getIcon("package.fetch");
			case EXTRACTING:
				return ResourceManager.getIcon("package.extract");
			case DONE:
				return ResourceManager.getIcon("status.ok");
		}
		
		// unreachable
		return null;
	}
	

	public JLabel getLanguageLabel() {
		return languageLabel;
	}
	

	@Override
	public void validate() {
		// validate children, yet avoid flickering of the mouse cursor
		validateTree();
	}
	
}
