package jmri.jmrix.secsi.serialmon;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Swing action to create and register a SerialMonFrame object
 *
 * @author	Bob Jacobsen Copyright (C) 2001, 2006, 2007, 2008
  */
public class SerialMonAction extends AbstractAction {

    /**
     *
     */
    private static final long serialVersionUID = 4020943026749845219L;

    public SerialMonAction(String s) {
        super(s);
    }

    public SerialMonAction() {
        this("SECSI Monitor");
    }

    public void actionPerformed(ActionEvent e) {
        // create a SerialMonFrame
        SerialMonFrame f = new SerialMonFrame();
        try {
            f.initComponents();
        } catch (Exception ex) {
            log.warn("SerialMonAction starting SerialMonFrame: Exception: " + ex.toString());
        }
        f.setVisible(true);
    }

    private final static Logger log = LoggerFactory.getLogger(SerialMonAction.class.getName());

}
