package pl.art.lach.mateusz.javaopenchess.network;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.Document;



























public class Chat
  extends JPanel
  implements ActionListener
{
  protected Client client;
  private GridBagLayout gbl;
  private GridBagConstraints gbc;
  private JScrollPane scrollPane;
  private JTextArea textOutput;
  private JTextField textInput;
  private JButton buttonSend;
  private Font font;
  
  public Chat()
  {
    initComponents();
    initScrollPane();
    initInputField();
    initSendButton();
  }
  
  private void initComponents()
  {
    font = new Font("Arial", 1, 10);
    textOutput = new JTextArea();
    setFont(font);
    textOutput.setFont(font);
    textOutput.setEditable(false);
    scrollPane = new JScrollPane();
    scrollPane.setViewportView(textOutput);
    textInput = new JTextField();
    textInput.addActionListener(this);
    buttonSend = new JButton("^");
    buttonSend.addActionListener(this);
    

    gbl = new GridBagLayout();
    gbc = new GridBagConstraints();
    gbc.fill = 1;
    setLayout(gbl);
  }
  
  private void initSendButton()
  {
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weighty = 0.0D;
    gbc.weightx = 0.0D;
    gbl.setConstraints(buttonSend, gbc);
    add(buttonSend);
  }
  
  private void initInputField()
  {
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    gbc.weighty = 0.0D;
    gbc.weightx = 1.0D;
    gbl.setConstraints(textInput, gbc);
    add(textInput);
  }
  
  private void initScrollPane()
  {
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.gridheight = 1;
    gbc.weighty = 1.0D;
    gbc.weightx = 0.0D;
    gbl.setConstraints(scrollPane, gbc);
    add(scrollPane);
  }
  




  public void addMessage(String str)
  {
    textOutput.append(str + "\n");
    textOutput.setCaretPosition(textOutput.getDocument().getLength());
  }
  





  public void actionPerformed(ActionEvent arg0)
  {
    getClient().sendMassage(textInput.getText());
    textInput.setText("");
  }
  



  public Client getClient()
  {
    return client;
  }
  



  public void setClient(Client client)
  {
    this.client = client;
  }
  

  public void setEnabled(boolean enabled)
  {
    super.setEnabled(enabled);
    buttonSend.setEnabled(enabled);
    textInput.setEnabled(enabled);
    textOutput.setEnabled(enabled);
  }
}
