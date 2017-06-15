package be.uantwerpen.idlab.cobra.WCET;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class WCETSettingBox extends JFrame {

	private FlowLayout layout;
	private Container container;
	
	private JLabel AVRListLabel;
	private JTextField item1;
	private JTextField item2;
	private JTextField item3;
	private JPasswordField item4;
	private JButton reg;
	private JButton custom;
	private JTextField tf;
	private JCheckBox boldbox;
	private JCheckBox italicbox;

	private JTextField tfJRadio;
	private Font pf;
	private Font bf;
	private Font itf;
	private Font bitf;
	private JRadioButton pb;
	private JRadioButton bb;
	private JRadioButton itb;
	private JRadioButton bitb;
	private ButtonGroup group;

	private JComboBox processorlist;
	private JLabel processorlabel;
	private static String[] processorname = { "AVR90CAN32", "AVR90CAN64", "AVR90CAN128" };

	private JList list;
	private static String[] colornames = { "black", "blue", "red", "white" };
	private static Color[] colors = { Color.BLACK, Color.BLUE, Color.RED, Color.WHITE };

	private JList leftlist;
	private JList rightlist;
	private JButton movebutton;
	private static String[] foods = { "bacon", "wings", "ham", "beef", "morebacon" };

	private String details;
	private JLabel statusbar;
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public WCETSettingBox() {
		super("The title bar"); // Add title
		layout = new FlowLayout();
		container = getContentPane();
		setLayout(layout); // Add default layout

		AVRListLabel = new JLabel("This is a sentence");
		AVRListLabel.setToolTipText("setToolTipText");

		add(AVRListLabel);

		item1 = new JTextField(10);
		add(item1);

		item2 = new JTextField("enter text here");
		add(item2);

		item3 = new JTextField("uneditable", 20);
		item3.setEditable(false);
		add(item3);

		item4 = new JPasswordField("mypass");
		add(item4);

		reg = new JButton("xxx");
		add(reg);

		tf = new JTextField("This is a sentence", 20);
		tf.setFont(new Font("Serif", Font.PLAIN, 14));
		add(tf);

		boldbox = new JCheckBox("Bold");
		italicbox = new JCheckBox("Italicbox");
		add(boldbox);
		add(italicbox);

		tfJRadio = new JTextField("JTextField", 25);
		add(tfJRadio);

		pb = new JRadioButton("plain", true);
		bb = new JRadioButton("bold", false);
		itb = new JRadioButton("italic", false);
		bitb = new JRadioButton("bold and italic", false);
		add(pb);
		add(bb);
		add(itb);
		add(bitb);

		group = new ButtonGroup();
		group.add(pb);
		group.add(bb);
		group.add(itb);
		group.add(bitb);

		processorlist = new JComboBox(processorname);
		add(processorlist);

		processorlist.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					processorlabel.setText(processorname[processorlist.getSelectedIndex()]);
				}
			}
		});

		processorlabel = new JLabel("");
		add(processorlabel);

		list = new JList(colornames);
		list.setVisibleRowCount(3);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(new JScrollPane(list));

		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				container.setBackground(colors[list.getSelectedIndex()]);
			}

		});

		leftlist = new JList(foods);
		leftlist.setVisibleRowCount(3);
		leftlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		add(new JScrollPane(leftlist));

		movebutton = new JButton("Move --- >");
		movebutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				rightlist.setListData(leftlist.getSelectedValuesList().toArray());
			}
		});
		add(movebutton);

		rightlist = new JList();
		rightlist.setVisibleRowCount(3);
		rightlist.setFixedCellHeight(15);
		rightlist.setFixedCellWidth(100);
		rightlist.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		add(new JScrollPane(rightlist));

		statusbar = new JLabel("This is default");
		add(statusbar, BorderLayout.SOUTH);
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent event) {
				details = String.format("You clicked %d", event.getClickCount());
				
				if(event.isMetaDown())
					details += " with right mouse button";
				else if(event.isAltDown())
					details += " with center mouse button";
				else 
					details += " with left mouse button";
				
				statusbar.setText(details);
			}
		});

		ActionHandler handler = new ActionHandler();

		item1.addActionListener(handler);
		item2.addActionListener(handler);
		item3.addActionListener(handler);
		item4.addActionListener(handler);
		reg.addActionListener(handler);

		ItemHandlerCheckBox handlercheckbox = new ItemHandlerCheckBox();
		boldbox.addItemListener(handlercheckbox);
		italicbox.addItemListener(handlercheckbox);

		pf = new Font("Serif", Font.PLAIN, 14);
		bf = new Font("Serif", Font.BOLD, 14);
		itf = new Font("Serif", Font.ITALIC, 14);
		bitf = new Font("Serif", Font.BOLD + Font.ITALIC, 14);

		tfJRadio.setFont(pf);

		// wait for event to happen, pass in font object to constructor
		pb.addItemListener(new ItemHandlerRadioBox(pf));
		bb.addItemListener(new ItemHandlerRadioBox(bf));
		itb.addItemListener(new ItemHandlerRadioBox(itf));
		bitb.addItemListener(new ItemHandlerRadioBox(bitf));

	}

	private class ActionHandler implements ActionListener {

		public void actionPerformed(ActionEvent event) {

			String string = "";

			if (event.getSource() == item1)
				string = String.format("field 1: %s", event.getActionCommand());
			else if (event.getSource() == item2)
				string = String.format("field 2: %s", event.getActionCommand());
			else if (event.getSource() == item3)
				string = String.format("field 3: %s", event.getActionCommand());
			else if (event.getSource() == item4)
				string = String.format("field 4: %s", event.getActionCommand());
			else if (event.getSource() == reg) {
				System.out.println("xxxx");
				string = String.format("Done");
			}

			JOptionPane.showMessageDialog(null, string);
		}

	}

	private class ItemHandlerCheckBox implements ItemListener {
		private Font font;

		public void itemStateChanged(ItemEvent event) {

			if (boldbox.isSelected() && italicbox.isSelected())
				font = new Font("Serif", Font.BOLD + Font.ITALIC, 14);
			else if (boldbox.isSelected())
				font = new Font("Serif", Font.BOLD, 14);
			else if (italicbox.isSelected())
				font = new Font("Serif", Font.ITALIC, 14);
			else
				font = new Font("Serif", Font.PLAIN, 14);

			tf.setFont(font);

		}

	}

	private class ItemHandlerRadioBox implements ItemListener {
		private Font font;

		// the font object get variable font
		public ItemHandlerRadioBox(Font f) {
			font = f;
		}

		// set the font to the font object that was passed in
		public void itemStateChanged(ItemEvent event) {
			tfJRadio.setFont(font);
		}

	}

}
