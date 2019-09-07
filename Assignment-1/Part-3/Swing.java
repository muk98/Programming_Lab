// Java program to create a blank text 
// field of definite number of columns. 
import java.awt.event.*; 
import javax.swing.*; 
class Swing extends JFrame implements ActionListener { 
	// JTextField 
	static JTextField t; 

	// JFrame 
	static JFrame f; 

	// JButton 
	static JButton N; 
    static JButton S;
    static JButton E; 
    static JButton W;
    static JButton done;
	// label to diaplay text 
	static JLabel l; 
    static JPanel p;
	// default constructor 
	Swing() 
	{ 
	} 

	// main class 
	public static void main(String[] args) 
	{ 
		// create a new frame to stor text field and button 
		f = new JFrame("textfield"); 

		// create a label to display text 
		l = new JLabel("Choose incoming direction"); 

		// create a new button 
        N = new JButton("N"); 
        S = new JButton("S"); 
        E = new JButton("E"); 
        W = new JButton("W"); 
        done = new JButton("Done"); 

		// create a object of the text class 
		 te = new Swing(); 

		// addActionListener to button 
        N.addActionListener(te);
        S.addActionListener(te);
        E.addActionListener(te);
        W.addActionListener(te); 
        done.addActionListener(te);

		// create a object of JTextField with 16 columns 
		t = new JTextField(16); 

		// create a panel to add buttons and textfield 
		 p = new JPanel(); 
       
        p.add(N);
        p.add(S);
        p.add(E);
        p.add(W);
        p.add(l);
        p.add(t);
        p.add(done);
		// add panel to frame 
		f.add(p); 

		// set the size of frame 
		f.setSize(300, 300); 

		f.show(); 
	} 

	// if the vutton is pressed 
	public void actionPerformed(ActionEvent e) 
	{ 
        String s = e.getActionCommand(); 
        if(l.getText()=="Choose incoming direction")
        {
            if(s.charAt(0)=='N')
                

            l.setText("Choose outgoing direction");
        }
        else if(l.getText()=="Choose outgoing direction")
        {
            l.setText("Enter start time");
        }
		
	} 
} 
