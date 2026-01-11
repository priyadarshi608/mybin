package pl.art.lach.mateusz.javaopenchess.display.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import pl.art.lach.mateusz.javaopenchess.core.Game;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;






















public class LocalSettingsView
  extends JPanel
  implements ActionListener
{
  private JCheckBox isUpsideDown;
  private JCheckBox isDisplayLegalMovesEnabled;
  private JCheckBox isRenderLabelsEnabled;
  private GridBagConstraints gbc;
  private GridBagLayout gbl;
  private Game game;
  
  public LocalSettingsView(Game game)
  {
    this.game = game;
    
    gbc = new GridBagConstraints();
    gbl = new GridBagLayout();
    
    setLayout(gbl);
    
    initUpsideDownControl();
    initDisplayLegalMovesControl();
    initRenderLabelsControl();
    refreshCheckBoxesState();
  }
  
  private void initUpsideDownControl()
  {
    isUpsideDown = new JCheckBox();
    isUpsideDown.setText(Settings.lang("upside_down"));
    isUpsideDown.setSize(isUpsideDown.getHeight(), getWidth());
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets(3, 3, 3, 3);
    gbl.setConstraints(isUpsideDown, gbc);
    add(isUpsideDown);
    
    isUpsideDown.addActionListener(this);
  }
  
  private void initDisplayLegalMovesControl()
  {
    isDisplayLegalMovesEnabled = new JCheckBox();
    isDisplayLegalMovesEnabled.setText(Settings.lang("display_legal_moves"));
    
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbl.setConstraints(isDisplayLegalMovesEnabled, gbc);
    add(isDisplayLegalMovesEnabled);
    
    isDisplayLegalMovesEnabled.addActionListener(this);
  }
  
  private void initRenderLabelsControl()
  {
    isRenderLabelsEnabled = new JCheckBox();
    isRenderLabelsEnabled.setText(Settings.lang("display_labels"));
    
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbl.setConstraints(isRenderLabelsEnabled, gbc);
    add(isRenderLabelsEnabled);
    
    isRenderLabelsEnabled.addActionListener(this);
  }
  
  private void refreshCheckBoxesState()
  {
    if (isInitiatedCorrectly())
    {
      isUpsideDown.setSelected(game.getSettings().isUpsideDown());
      isDisplayLegalMovesEnabled.setSelected(game.getSettings().isDisplayLegalMovesEnabled());
      isRenderLabelsEnabled.setSelected(game.getSettings().isRenderLabels());
    }
  }
  

  public void actionPerformed(ActionEvent e)
  {
    JCheckBox clickedComponent = (JCheckBox)e.getSource();
    if (clickedComponent == isUpsideDown)
    {
      game.getSettings().setUpsideDown(isUpsideDown.isSelected());
    }
    else if (clickedComponent == isDisplayLegalMovesEnabled)
    {
      game.getSettings().setDisplayLegalMovesEnabled(isDisplayLegalMovesEnabled.isSelected());
    }
    else if (clickedComponent == isRenderLabelsEnabled)
    {
      game.getSettings().setRenderLabels(isRenderLabelsEnabled.isSelected());
      game.resizeGame();
    }
    game.repaint();
  }
  

  public void repaint()
  {
    refreshCheckBoxesState();
    super.repaint();
  }
  
  private boolean isInitiatedCorrectly()
  {
    return (null != isUpsideDown) && (null != isDisplayLegalMovesEnabled) && (null != isRenderLabelsEnabled);
  }
}
