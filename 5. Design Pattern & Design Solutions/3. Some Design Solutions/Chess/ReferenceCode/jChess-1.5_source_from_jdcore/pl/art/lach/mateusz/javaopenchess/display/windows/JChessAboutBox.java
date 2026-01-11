package pl.art.lach.mateusz.javaopenchess.display.windows;

import java.awt.Font;
import javax.swing.ActionMap;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import pl.art.lach.mateusz.javaopenchess.JChessApp;

public class JChessAboutBox extends JDialog
{
  private JButton closeButton;
  
  public JChessAboutBox(java.awt.Frame parent)
  {
    super(parent);
    initComponents();
    getRootPane().setDefaultButton(closeButton);
  }
  
  @org.jdesktop.application.Action
  public void closeAboutBox()
  {
    dispose();
  }
  






  private void initComponents()
  {
    closeButton = new JButton();
    JLabel appTitleLabel = new JLabel();
    JLabel versionLabel = new JLabel();
    JLabel appVersionLabel = new JLabel();
    JLabel homepageLabel = new JLabel();
    JLabel appHomepageLabel = new JLabel();
    JLabel appDescLabel = new JLabel();
    JLabel imageLabel = new JLabel();
    JLabel vendorLabel1 = new JLabel();
    JLabel appVendorLabel1 = new JLabel();
    JLabel appHomepageLabel1 = new JLabel();
    JLabel appHomepageLabel2 = new JLabel();
    JLabel vendorLabel2 = new JLabel();
    JLabel appHomepageLabel3 = new JLabel();
    
    setDefaultCloseOperation(2);
    ResourceMap resourceMap = ((JChessApp)Application.getInstance(JChessApp.class)).getContext().getResourceMap(JChessAboutBox.class);
    setTitle(resourceMap.getString("title", new Object[0]));
    setModal(true);
    setName("aboutBox");
    setResizable(false);
    
    ActionMap actionMap = ((JChessApp)Application.getInstance(JChessApp.class)).getContext().getActionMap(JChessAboutBox.class, this);
    closeButton.setAction(actionMap.get("closeAboutBox"));
    closeButton.setName("closeButton");
    
    appTitleLabel.setFont(appTitleLabel.getFont().deriveFont(appTitleLabel.getFont().getStyle() | 0x1, appTitleLabel.getFont().getSize() + 4));
    appTitleLabel.setText(resourceMap.getString("Application.title", new Object[0]));
    appTitleLabel.setName("appTitleLabel");
    
    versionLabel.setFont(versionLabel.getFont().deriveFont(versionLabel.getFont().getStyle() | 0x1));
    versionLabel.setText(resourceMap.getString("versionLabel.text", new Object[0]));
    versionLabel.setName("versionLabel");
    
    appVersionLabel.setText(resourceMap.getString("Application.version", new Object[0]));
    appVersionLabel.setName("appVersionLabel");
    
    homepageLabel.setFont(homepageLabel.getFont().deriveFont(homepageLabel.getFont().getStyle() | 0x1));
    homepageLabel.setText(resourceMap.getString("homepageLabel.text", new Object[0]));
    homepageLabel.setName("homepageLabel");
    
    appHomepageLabel.setText(resourceMap.getString("Application.homepage", new Object[0]));
    appHomepageLabel.setName("appHomepageLabel");
    
    appDescLabel.setText(resourceMap.getString("appDescLabel.text", new Object[0]));
    appDescLabel.setName("appDescLabel");
    
    imageLabel.setIcon(resourceMap.getIcon("imageLabel.icon"));
    imageLabel.setName("imageLabel");
    
    vendorLabel1.setFont(vendorLabel1.getFont().deriveFont(vendorLabel1.getFont().getStyle() | 0x1));
    vendorLabel1.setText(resourceMap.getString("vendorLabel1.text", new Object[0]));
    vendorLabel1.setName("vendorLabel1");
    
    appVendorLabel1.setName("appVendorLabel1");
    
    appHomepageLabel1.setText(resourceMap.getString("appHomepageLabel1.text", new Object[0]));
    appHomepageLabel1.setName("appHomepageLabel1");
    
    appHomepageLabel2.setText(resourceMap.getString("appHomepageLabel2.text", new Object[0]));
    appHomepageLabel2.setName("appHomepageLabel2");
    
    vendorLabel2.setFont(vendorLabel2.getFont().deriveFont(vendorLabel2.getFont().getStyle() | 0x1));
    vendorLabel2.setText(resourceMap.getString("vendorLabel2.text", new Object[0]));
    vendorLabel2.setName("vendorLabel2");
    
    appHomepageLabel3.setText(resourceMap.getString("appHomepageLabel3.text", new Object[0]));
    appHomepageLabel3.setName("appHomepageLabel3");
    
    GroupLayout layout = new GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(layout
      .createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
      .addComponent(imageLabel)
      .addGap(18, 18, 18)
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
      .addComponent(appTitleLabel, GroupLayout.Alignment.LEADING)
      .addComponent(appDescLabel, GroupLayout.Alignment.LEADING, -1, 691, 32767)
      .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addComponent(versionLabel)
      .addComponent(homepageLabel)
      .addComponent(vendorLabel1)
      .addComponent(vendorLabel2))
      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addComponent(appHomepageLabel1)
      .addComponent(appHomepageLabel3, -1, 466, 32767))
      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
      .addComponent(closeButton))
      .addComponent(appVersionLabel)
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
      .addComponent(appVendorLabel1, GroupLayout.Alignment.LEADING, -1, -1, 32767)
      .addComponent(appHomepageLabel, GroupLayout.Alignment.LEADING, -1, -1, 32767)
      .addComponent(appHomepageLabel2, -1, -1, 32767)))))
      .addContainerGap()));
    
    layout.setVerticalGroup(layout
      .createParallelGroup(GroupLayout.Alignment.LEADING)
      .addComponent(imageLabel, -2, 194, 32767)
      .addGroup(layout.createSequentialGroup()
      .addContainerGap()
      .addComponent(appTitleLabel)
      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
      .addComponent(appDescLabel)
      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
      .addComponent(versionLabel)
      .addComponent(appVersionLabel))
      .addGap(27, 27, 27)
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
      .addComponent(homepageLabel)
      .addComponent(appHomepageLabel))
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 41, 32767)
      .addComponent(closeButton)
      .addContainerGap())
      .addGroup(layout.createSequentialGroup()
      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
      .addComponent(vendorLabel1)
      .addComponent(appVendorLabel1)
      .addComponent(appHomepageLabel2))
      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
      .addComponent(appHomepageLabel1)
      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
      .addComponent(vendorLabel2)
      .addComponent(appHomepageLabel3)))
      .addGap(36, 36, 36)))));
    

    pack();
  }
}
