package org.apache.log4j.chainsaw;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;




















class ControlPanel
  extends JPanel
{
  private static final Logger LOG = Logger.getLogger(ControlPanel.class);
  





  ControlPanel(final MyTableModel aModel)
  {
    setBorder(BorderFactory.createTitledBorder("Controls: "));
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    setLayout(gridbag);
    

    ipadx = 5;
    ipady = 5;
    

    gridx = 0;
    anchor = 13;
    
    gridy = 0;
    JLabel label = new JLabel("Filter Level:");
    gridbag.setConstraints(label, c);
    add(label);
    
    gridy += 1;
    label = new JLabel("Filter Thread:");
    gridbag.setConstraints(label, c);
    add(label);
    
    gridy += 1;
    label = new JLabel("Filter Logger:");
    gridbag.setConstraints(label, c);
    add(label);
    
    gridy += 1;
    label = new JLabel("Filter NDC:");
    gridbag.setConstraints(label, c);
    add(label);
    
    gridy += 1;
    label = new JLabel("Filter Message:");
    gridbag.setConstraints(label, c);
    add(label);
    

    weightx = 1.0D;
    
    gridx = 1;
    anchor = 17;
    
    gridy = 0;
    Level[] allPriorities = { Level.FATAL, Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG, Level.TRACE };
    





    final JComboBox priorities = new JComboBox(allPriorities);
    Level lowest = allPriorities[(allPriorities.length - 1)];
    priorities.setSelectedItem(lowest);
    aModel.setPriorityFilter(lowest);
    gridbag.setConstraints(priorities, c);
    add(priorities);
    priorities.setEditable(false);
    priorities.addActionListener(new ActionListener() { private final MyTableModel val$aModel;
      
      public void actionPerformed(ActionEvent aEvent) { aModel.setPriorityFilter((Priority)priorities.getSelectedItem());
      }
      


    });
    fill = 2;
    gridy += 1;
    final JTextField threadField = new JTextField("");
    threadField.getDocument().addDocumentListener(new DocumentListener() { private final JComboBox val$priorities;
      
      public void insertUpdate(DocumentEvent aEvent) { aModel.setThreadFilter(threadField.getText()); }
      
      public void removeUpdate(DocumentEvent aEvente) {
        aModel.setThreadFilter(threadField.getText());
      }
      
      public void changedUpdate(DocumentEvent aEvent) { aModel.setThreadFilter(threadField.getText());
      }
    });
    gridbag.setConstraints(threadField, c);
    add(threadField);
    
    gridy += 1;
    final JTextField catField = new JTextField("");
    catField.getDocument().addDocumentListener(new DocumentListener() { private final MyTableModel val$aModel;
      
      public void insertUpdate(DocumentEvent aEvent) { aModel.setCategoryFilter(catField.getText()); }
      
      private final JTextField val$threadField;
      public void removeUpdate(DocumentEvent aEvent) { aModel.setCategoryFilter(catField.getText()); }
      
      public void changedUpdate(DocumentEvent aEvent) {
        aModel.setCategoryFilter(catField.getText());
      }
    });
    gridbag.setConstraints(catField, c);
    add(catField);
    
    gridy += 1;
    final JTextField ndcField = new JTextField("");
    ndcField.getDocument().addDocumentListener(new DocumentListener() { private final MyTableModel val$aModel;
      private final JTextField val$catField;
      public void insertUpdate(DocumentEvent aEvent) { aModel.setNDCFilter(ndcField.getText()); }
      
      private final MyTableModel val$aModel;
      public void removeUpdate(DocumentEvent aEvent) { aModel.setNDCFilter(ndcField.getText()); }
      
      public void changedUpdate(DocumentEvent aEvent) {
        aModel.setNDCFilter(ndcField.getText());
      }
    });
    gridbag.setConstraints(ndcField, c);
    add(ndcField);
    
    gridy += 1;
    final JTextField msgField = new JTextField("");
    msgField.getDocument().addDocumentListener(new DocumentListener() { private final JTextField val$ndcField;
      private final MyTableModel val$aModel;
      public void insertUpdate(DocumentEvent aEvent) { aModel.setMessageFilter(msgField.getText()); }
      
      private final JTextField val$msgField;
      public void removeUpdate(DocumentEvent aEvent) { aModel.setMessageFilter(msgField.getText()); }
      
      public void changedUpdate(DocumentEvent aEvent) {
        aModel.setMessageFilter(msgField.getText());
      }
      

    });
    gridbag.setConstraints(msgField, c);
    add(msgField);
    

    weightx = 0.0D;
    fill = 2;
    anchor = 13;
    gridx = 2;
    
    gridy = 0;
    JButton exitButton = new JButton("Exit");
    exitButton.setMnemonic('x');
    exitButton.addActionListener(ExitAction.INSTANCE);
    gridbag.setConstraints(exitButton, c);
    add(exitButton);
    
    gridy += 1;
    JButton clearButton = new JButton("Clear");
    clearButton.setMnemonic('c');
    clearButton.addActionListener(new ActionListener() { private final MyTableModel val$aModel;
      
      public void actionPerformed(ActionEvent aEvent) { aModel.clear();
      }
    });
    gridbag.setConstraints(clearButton, c);
    add(clearButton);
    
    gridy += 1;
    final JButton toggleButton = new JButton("Pause");
    toggleButton.setMnemonic('p');
    toggleButton.addActionListener(new ActionListener() { private final MyTableModel val$aModel;
      private final JButton val$toggleButton;
      public void actionPerformed(ActionEvent aEvent) { aModel.toggle();
        toggleButton.setText(aModel.isPaused() ? "Resume" : "Pause");
      }
      
    });
    gridbag.setConstraints(toggleButton, c);
    add(toggleButton);
  }
}
