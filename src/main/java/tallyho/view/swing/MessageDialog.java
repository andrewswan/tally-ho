/*
 * Created on 28/08/2004
 */
package tallyho.view.swing;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Dialog for showing the user a message (for example an illegal move)
 */
public class MessageDialog extends JDialog implements ActionListener {

  // Constants
  // It's OK to use a static label, as we only instantiate this class once
  private static final JLabel LBL_MESSAGE = new JLabel();
  
  /**
   * Constructor
   * 
   * @param parent the parent frame
   * @param title the text for the dialog title
   * @param message can be <code>null</code>
   */
  MessageDialog(Frame parent, String title, String message) {
    super(parent, title, true);

    // Apply the given message
    LBL_MESSAGE.setText(message);
    
    // Set the appearance of this dialog
    initialiseGUI();
  }
  
  /**
   * Sets the graphical properties of this dialog
   */
  private void initialiseGUI() {
    JPanel contentPanel = new JPanel();
    setContentPane(contentPanel);
    contentPanel.setBackground(BoardPanel.COLOR_BOARD);
    contentPanel.setBorder(Utils.BORDER_EMPTY_10);
    BorderLayout borderLayout = new BorderLayout();
    borderLayout.setVgap(10);
    contentPanel.setLayout(borderLayout);  
    contentPanel.add(LBL_MESSAGE, BorderLayout.CENTER);
    JButton btnOK = new JButton("OK");
    btnOK.addActionListener(this);
    btnOK.setBackground(contentPanel.getBackground()); // better than transparent
    contentPanel.add(btnOK, BorderLayout.SOUTH);
    pack();
    setResizable(false);
    Utils.centre(this);
  }
  
  /**
   * Sets the message to be displayed by this dialog
   * 
   * @param message can't be <code>null</code> or just whitespace
   */
  void setMessage(String message) {
    // Check the message is valid
    if (message == null || message.trim().length() == 0)
      throw new IllegalArgumentException("Invalid message: " + message);
    
    // Apply it
    StringBuffer messageBuf = new StringBuffer("<html><center>");
    messageBuf.append(message);
    messageBuf.append("</center></html>");
    LBL_MESSAGE.setText(messageBuf.toString());

    // Resize and re-centre this dialog for the new message
    pack();
    Utils.centre(this);
  }

  /**
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed(ActionEvent arg0) {
    // Any action means hide the dialog
    setVisible(false);
  }
}
