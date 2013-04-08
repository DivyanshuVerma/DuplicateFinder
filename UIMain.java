/*

ADVISABLE: before viewing the comments in this code, view the program once,
and get familiar with the various buttons and screens that 
come one after the another. The code is heavily commented and some things necessary are explained here.

ACTION LISTENERS: Let us take a button. the action related to a button is clicking it.
					Now, whenever a user presses a button, we want a specific action to happen.
For us to know when the action has been performed, we use action listeners. Action listeners are like
registers, that have a list of elements that want to know when a specific action has happened.
the action listeners record every action that happen on the screen, and when an action happens, they just
send the signal to all those elements who want to recieve that action, that are present in their list. They
dont care about what action needs to be performed. They just send the signal. For registering an element to the
action listener, we use 'addActionListener( <an action listener interface implemented class> );' type of 
method. This adds the element to the list of all those who want to record the specific action.
Within the brackets, we specify a class that has a statement of the type 'class ABC implements ActionListener'
now, in abc class we should specify a 'actionPerformed()' method otherwise it will generate a compile-time error.
in the actionPerformed() method, we specify the code which is to be performed when a specific action has happened.

This makes it easy for the compiler. Let us take an example of a button. When the user clicks this button, we
want to add two numbers. So, first, we create a button like this:

JButton jb = new JButton("Add");

then, we specify the actionListener that we want to add to it. This actionListener will listen whenever the
button is clicked, and inform the actionPerformed method that the button has been clicked. we do it in this way:

jb.addActionListener(new ABC());

It can also be written as:

ABC a = new ABC();
jb.addActionListener(a);

We just have to pass an instance of the class which implements an actionListener. The class should also contain an actionPerformed() method.
The class is shown below:

class ABC implements ActionListener
{
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == jb)
		{
			//Code what you want to be performd after the button has been clicked		
		}
	}
}

ActionEvent gives us the event that has been performed. We can whatever we want to do with it.
e.getSource() returns the source which generated the event. In this case, it is a button. so we compare it with it.

So when the button is pressed, the code in the actionPerformed method is executed.

*/
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;

/*to do:
setting limit for restoraton, after which the files will be deleted
*/

class UIMain extends JFrame implements ActionListener //We want this class itself to act as an action listener
{
	JTextField tf, tftemp;
	JButton btn,ebtn, prev_btn, next_btn, end_btn, skip_all_btn, new_search_btn, deselect, go, sel_all, sel_none, about, restore, resto, rest, sell_all,browse,browse2,emptyf;
	JLabel lb,elb,plb,ttl1,ttl2,ttl3,ttl4,remaining, copyright;
	Container c;
	String path ="";
	int w,h,mw,mh;
	DuplicateFinder df;
	Font titleFont = new Font("arial", Font.PLAIN, 30);
	String search_text = "";
	String freeable;
	String fileMatches[][] = new String[1000000][];
	String delList[] = new String[1000000];
	int dtop=-1;
	int top=-1;
	int screen_count=0;
	DefaultListModel dlm;
	JList jl;
	JScrollPane js;
	Boolean first = true;
	int prev_sel = 0;
	String restoreList[];
	UIMain() //the UI constructor. As the constructor is always executed first, whenevr the object is created, we do all the initialization of the UI in the constructor.
	{
		analyzeDelFiles();
		
		setTitle("Duplicate Finder Pro v2.0"); //This sets the main title of the frame. Important to include
		
		w = 410; //width of the frame
		h = 410; //height of the frame
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize(); //Dimension is a class, which stores the dimensions of any object, in width and height. With this statement, we get the dimensions of the resolution of the screen.
		mw = dim.width; //Access the width of the dimension object
		mh = dim.height; //Access the height of the dimension object
		setSize(w,h); //sets the dimension of the frame. Important to include
		
		c = this.getContentPane(); //c is an object of the Container class, which is a swing component. With this.getContentPane(), we get a reference to the current frame we are trying to modify. Important to include
		c.setLayout(null); //this sets the layout to null, as we want to position the elements absolutely, and don't want the compiler to decide.
		
		//Importing a new Font. not important, just for decoration {
		try{
		titleFont = Font.createFont(Font.TRUETYPE_FONT, new java.io.File("lib\\myFont.ttf")); //Creates a new font from the file given
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); //GraphicsEnvironment is an abstract class, so it can't be instantiated directly. But, it has a method getLocalGraphicsEnvironment() , which returns the reference to the local graphics environment
		ge.registerFont(titleFont); //we register our created font with the graphics environment
		titleFont = titleFont.deriveFont(Font.PLAIN,40);}//the font is registered, but it won't be rendered correctly as it does not know the style and size yet, so we call the deriveFont() method to change the properties of the existing font.
		catch(FontFormatException ffe)
		{} //This is important, as if the font file is not found, then it will render the default font, and continue with its work with the default font.
		catch(java.io.IOException ie)
		{}
		//}
		
		tf = new JTextField(); //Creates a JTextField, light-weight swing component.
		tf.setBounds((w-334)/2,(h-30)/2,290,30); //the setBounds() method can be called on any component of the swing framework, and is used to absolutely position the element. it has four arguments, (x-pos, y-pos, width, height). (w-350)/2 adjusts the element at exactly the center of the frame. it can be more clearly represented as: [(width of frame - width of the element to position)/2]
		
		if(new File("lib\\JSearch.png").exists())
		{
			browse = new JButton(new ImageIcon("lib\\JSearch.png","Browse"));
			browse.setBounds(330,(h-34)/2,34,34);
		}
		else //fallback, in case the image file is deleted, we dont want the user to press an unlabeled button
		{
			browse = new JButton("Browse");
			tf.setBounds((w-380)/2,(h-30)/2,290,30);
			browse.setBounds(310,(h-30)/2,78,30);
		}
		browse.setToolTipText("Browse for folder");
		browse.addActionListener(this);
		
		btn = new JButton("Search"); //This creates a JButton element
		btn.addActionListener(this); //This adds the action listener to the button. in the brackets, we give a reference to the object, whose class contains an 'actionPerformed()' method. In this case, i have created this class itself as the action listener, so i passed 'this' to it.
		btn.setBounds((w-100)/2,(h-50)/2 + 50,100,50); //Same as setBounds of Text field
		
		ebtn = new JButton("Proceed >>"); //Another button
		ebtn.addActionListener(this);
		ebtn.setBounds( (w-100)/2, (h-50)/2 + 100,100,50);
		
		lb = new JLabel("Enter path, or press the browse button to start search:"); //This is a label. A label is used to represent text on the UI frame. The text is passed as an argument to the constructor
		lb.setBounds((w-330)/2, (h-30)/2 - 30,330,30);
		
		elb = new JLabel("Oops! This path does not exist! Enter again!");
		elb.setBounds( (w-250)/2, (h-50)/2 + 100, 250,50);
		
		plb = new JLabel("Processing...");
		plb.setBounds( (w-300)/2, (h-50)/2 - 150, 300,50);
		
		ttl1 = new JLabel("Duplicate");
		ttl1.setBounds( (w-165)/2 - 80, (h-30)/2 - 130, 165,55);
		ttl1.setFont(titleFont);
		
		ttl2 = new JLabel("Finder");
		ttl2.setBounds( (w-100)/2 + 35, (h-30)/2 - 130, 100,55);
		ttl2.setFont(titleFont);
		
		ttl3 = new JLabel("Pro");
		ttl3.setBounds( (w-65)/2 + 125, (h-30)/2 - 130, 65,55);
		ttl3.setFont(titleFont);
		
		ttl4 = new JLabel("v2.0");
		ttl4.setBounds( (w-75)/2, (h-30)/2 - 90, 75,55);
		ttl4.setFont(titleFont);
		
		about = new JButton("About"); //Another button
		about.addActionListener(this);
		about.setBounds(290,335,100,30);
		
		copyright = new JLabel("<html>Developed By: Divyanshu Verma<br/>(c)Copyright CreativeCommons BY-NC-ND 3.0</html>"); //in this label, i have provided html tags. these tags are supported  to an extent, but some do not render correctly like <img>. But these can be used for giving an extra line using <br/>
		copyright.setBounds(5,335,270,30);
		
		//setupViewMatchesScreen Components
		
		prev_btn = new JButton("<<Prev");
		prev_btn.addActionListener(this);
		prev_btn.setBounds(11,330,125,40);

		next_btn = new JButton("Next>>");
		next_btn.addActionListener(this);
		next_btn.setBounds(265,330,125,40);
		
		end_btn = new JButton("End");
		end_btn.addActionListener(this);
		end_btn.setBounds(265,330,125,40);		
		
		skip_all_btn = new JButton("Skip All");
		skip_all_btn.addActionListener(this);
		skip_all_btn.setBounds(290,10,100,40);
		
		new_search_btn = new JButton("New Search");
		new_search_btn.addActionListener(this);
		new_search_btn.setBounds(11,330,125,40);
		
		deselect = new JButton("Deselect");
		deselect.addActionListener(this);
		deselect.setBounds(150,330,100,40);
		
		remaining = new JLabel(screen_count + "/" + top);
		remaining.setBounds(350,50,50,30);
		
		//setupRestoreScreen Components
		
		restore = new JButton("Restore");
		restore.addActionListener(this);
		restore.setBounds(310,10,85,25);
		restore.setToolTipText("Restore previously deleted files");
		
		emptyf = new JButton("Empty Files");
		emptyf.addActionListener(this);
		emptyf.setBounds(205,10,100,25);
		emptyf.setToolTipText("Empty the files temporarily stored for restoring");
		
		ToolTipManager.sharedInstance().setInitialDelay(100); //<<USED TO GIVE TOOLTIPS AFTER 100ms DELAY. Tooltips are text that appear after you hover over an element for some time. They can be given to any swing component which contains text using the method setToolTipText(). The statement is used to set the timeout for the display of this tooltip.
		
		setupStartScreen(); //This is a method which sets up the Start up screen. I created a method, because I had to call it again after the user has performed one search operation.
		
		setDefaultCloseOperation(EXIT_ON_CLOSE); //This sets the default operation performed when the user clicks the 'X' on the top-right-most corner of the screen. If you don't set this, your program wont even terminate. This is Important.
		setResizable(false); // Optional.This depends on you to set. I dint want to display extra empty space after the user has resized, so i made it false.
		setVisible(true); // This is important. If you dont include this, the frame won't be displayed.
		
		//Code to center the window{
		//Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();		
		Window window = this; //Gives a reference to the window
		//int w = window.getSize().width;
		//int h = window.getSize().height;
		int x = (mw-w)/2; //mw is the resolution width.
		int y = (mh-h)/2; //mh is the resolution height.
		window.setLocation(x, y); //this sets the x and y position where we want our window to be showed after it has been set visible. Default is in the top-left corner of the screen, which did not look good. Optional.
		//}
	}
	public void setupStartScreen() //Startup screen to be showed at the start, and also whenever the "new search" button is clicked.
	{
		//Code to remove all the existing components from the screen
		//{
		if(c.getComponentCount()>0) //This gets the total number of components that are present. If it is greater than zero, we will remove them as we want to set up our startup screen
		{
			Component carr[] = c.getComponents(); //gets all the components present in an array. the return type is a component array.
			for(int j=0;j<carr.length;j++)
				c.remove(carr[j]); //this removes all the components from the screen
		}
		//}
		c.add(ttl1); //Adding "duplicate" label to the frame.
		c.add(ttl2); //Adding "Finder" label
		c.add(ttl3); //Adding "pro" label. yes, these can also be embedded using only one label via html tags, but i did not know this at that time. Maybe i'll improve this in the other versions.
		c.add(ttl4); //Adding "v2.0"
		c.add(lb); //Adds the line for search
		c.add(tf); //adds the textfield
		c.add(btn); //adds the search button
		c.add(browse); //adds the browse button
		c.add(about); //adds the about button
		c.add(copyright); //adds the copyright label
		File fff = new File("Files\\");
		if(fff.exists())
		{
			File[] farr = fff.listFiles();
			if(farr.length>0)
			{
				c.add(restore);
				if(farr.length>=50)
					c.add(emptyf);
			}
		}
		repaint(); //Important. if you don't write this, the components on the screen will just freeze and wont respond. This paint's the ui all over again.
	}
	public void analyzeDelFiles()
	{		
		File ft = new File("Files\\");
		if(!ft.exists())
			ft.mkdir();
		restoreList = ft.list();
		if(restoreList.length>0)
		{
			for(int i=0;i<restoreList.length;i++)
				restoreList[i] = "Files\\" + restoreList[i];
		}
		else
			restoreList=null;
	}
	public void setupRestoreScreen()
	{
		if(c.getComponentCount()>0)
		{
			Component carr[] = c.getComponents();
			for(int j=0;j<carr.length;j++)
				c.remove(carr[j]);
		}
		if(restoreList!=null)
		{
		dlm = new DefaultListModel();
		jl = new JList(dlm);
		jl.setVisible(true);
		jl.setLayoutOrientation(JList.VERTICAL);
		js = new JScrollPane(jl);
		js.setBounds(5,80,385,240);		
		jl.setBounds(10,80,350,220);
		js.setVisible(true);
		
		int delim = 60;
		boolean changer = true;
		String color;
		for(int i=0;i<restoreList.length;i++)
		{
			String tmp1 = restoreList[i].substring( restoreList[i].indexOf("\\") +1 , restoreList[i].lastIndexOf("__") );
			if(tmp1.length()>delim)
			{
				int tind = -1;
				int mod = tmp1.length();
				if(changer)
					color = "background-color:#dddddd;";
				else
					color = "";
				changer = !changer;				
				for(; tmp1.lastIndexOf("\\")!=-1 && (tind = tmp1.lastIndexOf("\\",mod))>delim; mod = tmp1.lastIndexOf("\\",mod) - 1);
				if(tind!=-1)
					tmp1 = "<html><div style='" + color + "'>" + tmp1.substring(0,tind+1) + "<br/>" + tmp1.substring(tind+1) + "</div></html>";
				else
					tmp1 = "<html><div style='" + color + "'>" + tmp1.substring(0,delim) + "<br/>" + tmp1.substring(delim) + "</div></html>";
			}
			dlm.addElement(tmp1);
		}
		
		JLabel lbl;
		
		resto = new JButton("RESTORE");
		resto.addActionListener(this);
		resto.setBounds(300,330,90,40);
		
		sell_all = new JButton("<html>Select<br/><center>All</center></html>");
		sell_all.addActionListener(this);
		sell_all.setBounds(6,35,125,40);
		
		sel_none = new JButton("<html>Select<br/><center>None</center></html>");
		sel_none.addActionListener(this);
		sel_none.setBounds(265,35,125,40);
		
		lbl = new JLabel("Select the files which you want to be restored: ");
		this.setTitle("Restoration");
		lbl.setBounds(5,5,350,30);
				
		c.add(js);
		c.add(resto);
		c.add(sell_all);
		c.add(sel_none);
		c.add(new_search_btn);
		c.add(lbl);
		}
		else
		{
			JLabel ltemp = new JLabel("<html>Oops! There are no files that can be restored<br/>You can start a new search!</html>");
			ltemp.setBounds(5,270,350,50);
			c.add(ltemp);
			c.add(new_search_btn);
		}
		repaint();
	}
	public void takePath()
	{
		if(c.getComponentCount()>0)
		{
			Component carr[] = c.getComponents();
			for(int j=0;j<carr.length;j++)
				c.remove(carr[j]);
		}

		tftemp = new JTextField();
		tftemp.setBounds((w-334)/2,(h-30)/2,290,30);
		
		if(new File("lib\\JSearch.png").exists())
		{
			browse2 = new JButton(new ImageIcon("lib\\JSearch.png","Browse"));
			browse2.setBounds(330,(h-34)/2,34,34);
		}
		else
		{
			browse2 = new JButton("Browse");
			tftemp.setBounds((w-380)/2,(h-30)/2,290,30);
			browse2.setBounds(310,(h-30)/2,78,30);
		}
		browse2.setToolTipText("Browse for folder");
		browse2.addActionListener(this);		

		JLabel ltemp = new JLabel("Enter the path to restore the files:");
		ltemp.setBounds((w-200)/2, (h-50)/2 - 50,200,50);
		
		rest = new JButton("RESTORE");
		rest.addActionListener(this);
		rest.setBounds(300,330,90,40);
		
		c.add(tftemp);
		c.add(browse2);
		c.add(ltemp);
		c.add(rest);
		
		repaint();
	}
	public void setupSummaryScreen()
	{
		setupSummaryScreen("<html>Whoa! The files were successfully deleted!<br/>What next?<br/>You can start a new search below!</html>");
	}
	public void setupSummaryScreen(String lab)  //The last screen to be viewed after the user has completed the search.
	{
		//Code to remove all the existing components from the screen
		//{
		if(c.getComponentCount()>0)
		{
			Component carr[] = c.getComponents();
			for(int j=0;j<carr.length;j++)
				c.remove(carr[j]);
		}
		//}
		JLabel lb = new JLabel(lab);
		lb.setBounds(10,200,250,60);		
		
		this.setTitle("Summary"); //Changes the title.
		
		c.add(lb); //Adds the label
		c.add(new_search_btn); //Adds the new search button
		repaint(); //repaints.
	}
	public void setupViewMatchesScreen() //this screen is to be showed after all the processing has been done, and now its time to show all the matching files that were found.
	{		
		//Code to remove all the existing components from the screen
		//{	
		if(c.getComponentCount()>0)
		{
			Component carr[] = c.getComponents();
			for(int j=0;j<carr.length;j++)
				c.remove(carr[j]);
		}
		//}
		if(top>0)
		{
		if(screen_count == top) //screen_count is used to keep a count of the matches that are being viewed. top is a pointer that refers to the top(th) match. Now , if the user is viewing the last match, we don't want him to go next, we want to end the selections instead. So, we add here a previous button, and an end button.
		{
			c.add(prev_btn);
			c.add(end_btn);
		}
		else if(screen_count == 0) //if the screen being viewed is the first screen, then we don't want a prev button, instead, we want a next button, and a 'new search' button
		{
			c.add(new_search_btn);
			c.add(next_btn);
		}
		else //otherwise, just add the previous and the next buttons
		{
			c.add(prev_btn);
			c.add(next_btn);
		}
		}
		else
		{
			c.add(end_btn);
			c.add(new_search_btn);
		}
		
		c.add(skip_all_btn); //add skip all button to the screen. Important, if the user wants to skip all that he has done, and do the results instantly.
		c.add(deselect); //used to deselect everything selected from the list.
		this.setTitle("Select Duplicates to delete");
		
		remaining.setText( (screen_count+1) + "/" + (top+1)); //shows the current count of the matches out of the total matches
		c.add(remaining);
		
		dlm = new DefaultListModel(); //This is a DefaultListModel for the implementation of the JList. whatever content we have to display in the list, we add it to the model. Then add it to the JList, so that it contains all the list elements
		jl = new JList(dlm); //creating a list by using the DefaultListModel object
		jl.setVisible(true);
		jl.setLayoutOrientation(JList.VERTICAL); //Vertical orientation specifies the list to contain elements only one after the other and not sideways.
		js = new JScrollPane(jl); //A scroll pane. A JList does not itself has any scroll bar. so, we add one to it, in case the matched files are more than what can be displayed in the JList's visible area
		js.setBounds(10,80,385,240); 
		jl.setBounds(10,80,350,220); //Don't know why, but it is required!! If you remove it, the list won't show. The values don't matter 
		js.setVisible(true);
		
		String tt = fileMatches[screen_count][0];
		String t = tt.substring(0,tt.indexOf(">>"));
		String sz = tt.substring( tt.lastIndexOf(">>", tt.lastIndexOf(">>") -1) +2 ,tt.lastIndexOf(">>"));
		
		
		//Calculates the size in proper Gb,Mb,Kb and bytes
		//{
		String str = "";
		long bt = Long.parseLong(sz);
		long kbt = bt/1024;
		bt%=1024;
		if(bt>0) str = bt + " bytes";
		long mbt = kbt/1024;
		kbt%=1024;
		if(kbt>0) str = kbt + " Kb " + str;
		long gbt = mbt/1024;
		mbt%=1024;
		if(mbt>0) str = mbt + " Mb " + str;
		if(gbt>0) str = gbt + " Gb " + str;
		str += " (" +sz+ " bytes)"; 
		//}
		
		JLabel fileName = new JLabel("<html>Filename: <span style='color:red;'>" + t + "</span><br/>Size: " + str + "<br/><br/>Select file(s) to be deleted, then press next</html>");
		fileName.setBounds(10,10,300,65);
		fileName.setToolTipText(t); //set the tool tip in case the label has a name that exceedes the viewable area.
		c.add(fileName);
		int delim = 60;
		boolean changer = true;
		String color;
		for(int i=0;i<fileMatches[screen_count].length;i++)
		{
			String tmp = fileMatches[screen_count][i];
			int ind1 = tmp.indexOf(">>");
			int ind2 = tmp.indexOf(">>" , ind1 + 1);
			String tmp1;
			if(ind2>0)
				tmp1 = tmp.substring(ind1 + 2, ind2);
			else
				tmp1 = tmp.substring(ind1 + 2);
			if(tmp1.length()>delim)
			{
				int tind = -1;
				int mod = tmp1.length();
				if(changer)
					color = "background-color:#dddddd;";
				else
					color = "";
				changer = !changer;
				for(; tmp1.lastIndexOf("\\")!=-1 && (tind = tmp1.lastIndexOf("\\",mod))>delim; mod = tmp1.lastIndexOf("\\",mod) - 1);
				if(tind!=-1)
					tmp1 = "<html><div style='" + color + "'>" + tmp1.substring(0,tind+1) + "<br/>" + tmp1.substring(tind+1) + "</div></html>";
				else
					tmp1 = "<html><div style='" + color + "'>" + tmp1.substring(0,delim) + "<br/>" + tmp1.substring(delim) + "</div></html>";
			}
			dlm.addElement(tmp1); //add the processed element to the list
		}
		c.add(js);
		repaint();
	}
	public void actionPerformed(ActionEvent e) //our actionPerformed method
	{
		if(e.getSource()==btn) //if it is a new search button, get the text from the text field, and perform search on it
		{
			path = tf.getText(); //get the text from the text field
			try
			{
				if(!first)
					reSearch(path);
				else
					first = false;
			}
			catch(Exception ex)
			{}
		}
		else if(e.getSource()==ebtn) //the proceed button
		{
			setupViewMatchesScreen();
		}
		else if(e.getSource()==next_btn) //if next is clicked, get all the items the user has selected in the list, increase the count of the main counter, and setup new screen
		{
			getDelListSelection();
			screen_count++;
			setupViewMatchesScreen();
		}
		else if(e.getSource()==prev_btn) //if prev is clicked, remove all the items that the user had selected in the prev selection from the del array, decrease the count, and show the new screen
		{
			if(dtop>=0 && screen_count>0)
			{
				String cmp = fileMatches[screen_count -1][0].substring(0, fileMatches[screen_count -1][0].indexOf(">>"));//delList[dtop].substring( delList[dtop].lastIndexOf("\\") );
				for(; dtop>-1 && delList[dtop].substring( delList[dtop].lastIndexOf("\\") + 1 ).equals(cmp) ; dtop--);
			}
			screen_count--;
			setupViewMatchesScreen();
		}
		else if(e.getSource()==end_btn || e.getSource()==skip_all_btn) //if end and skip all has been pressed, get the selection, and setup final screen
		{
			getDelListSelection();
			setupFinalScreen();
		}
		else if(e.getSource()==new_search_btn) // if new search has been clicked, just set the new startup screen
		{
			analyzeDelFiles();
			setupStartScreen();
		}
		else if(e.getSource()==deselect) //deselect all the items that are currently selected in the list
		{
			jl.clearSelection();
		}
		else if(e.getSource()==go) //move all the items that are deleted to the "Files" folder, in case the user has deleted something by chance. and setup the summary screen
		{
			int arr[] = jl.getSelectedIndices();
			File ftemp = new File("Files");			
			if( ftemp.exists() &&  ftemp.isDirectory());
			else
				ftemp.mkdir();
			for(int i=0;i<arr.length;i++)
			{
				File f = new File(delList[arr[i]]);
				String fname = delList[arr[i]].substring( delList[arr[i]].lastIndexOf("\\")+1);
				String new_pos = "Files\\" + fname + "__" + (int)(new Random()).nextInt();
				f.renameTo(new File(new_pos));
			}
			setupSummaryScreen();
		}
		else if(e.getSource()==sel_all) //select all the items in the list
		{
			int selArr[] = new int[dtop+1];
			for(int i=0;i<=dtop;i++)
				selArr[i] = i;
			jl.setSelectedIndices(selArr);
		}
		else if(e.getSource()==sell_all)
		{
			int selArr[] = new int[restoreList.length];
			for(int i=0;i<=dtop;i++)
				selArr[i] = i;
			jl.setSelectedIndices(selArr);
		}
		else if(e.getSource()==sel_none) //deselect all the items
		{
			jl.clearSelection();
		}
		else if(e.getSource()==about) //display the details about me and the copyright info
		{
			JOptionPane.showMessageDialog(this, "<html>Developed By:<br/><div style='color:blue;'>Divyanshu Verma<br/>4th Semester,<br/>BE - Information and Communication Technology,<br/>Manipal Institute Of Technology<br/>Email: divyanshuv_1010@yahoo.com</div><br/><br/>Duplicate Finder Pro v2.0 by Divyanshu Verma is licensed under a<br/>Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.<br/>To view a copy of the license, visit:<br/><a href=\"http://creativecommons.org/licenses/by-nc-nd/3.0/\">http://creativecommons.org/licenses/by-nc-nd/3.0/</a></html>", "About", JOptionPane.PLAIN_MESSAGE);
		}
		else if(e.getSource()==restore)
		{
			setupRestoreScreen();
		}
		else if(e.getSource()==resto)
		{
			int arr[] = jl.getSelectedIndices();
			if(arr.length<1)
				JOptionPane.showMessageDialog(this,"<html>Please select at least one option,<br/>or click on the New Search button to<br/>start a new search</html>","Nothing Selected", JOptionPane.PLAIN_MESSAGE);
			else
				takePath();
		}
		else if(e.getSource()==rest)
		{
			String dname = tftemp.getText();
			File ftt = new File(dname);
			if(ftt.exists() && ftt.isDirectory())
			{
				
				int arr[] = jl.getSelectedIndices();
				for(int i=0;i<arr.length;i++)
				{
					File ftemp = new File(restoreList[arr[i]]);
					String finame = restoreList[arr[i]].substring( restoreList[arr[i]].indexOf("\\") +1 , restoreList[arr[i]].lastIndexOf("__") );
					String fname = finame.substring( 0, finame.lastIndexOf("."));
					String fext = finame.substring( finame.lastIndexOf("."));				
					File ftemp2 = new File( dname + "\\" + fname + fext);
					if(ftemp2.exists())
					{
						for(int j=1;ftemp2.exists();j++)
							ftemp2 = new File(dname + "\\" + fname + "_" + j + fext );
					}
					ftemp.renameTo(ftemp2);
				}
				setupRestoreSummaryScreen();
			}
			else
				JOptionPane.showMessageDialog(this,"The directory does not exist, please choose a valid directory","Error",JOptionPane.PLAIN_MESSAGE);
		}
		else if(e.getSource()==browse)
		{
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Choose the directory to search for duplicates");
			fc.setDialogType(JFileChooser.OPEN_DIALOG);
			fc.setFileHidingEnabled(true);
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setMultiSelectionEnabled(false);
			int rval = fc.showOpenDialog(this);
			if(rval==JFileChooser.APPROVE_OPTION)
				tf.setText(fc.getSelectedFile().getAbsolutePath());
		}
		else if(e.getSource()==browse2)
		{
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Choose the directory to save the files");
			fc.setDialogType(JFileChooser.SAVE_DIALOG);
			fc.setFileHidingEnabled(true);
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setMultiSelectionEnabled(false);
			int rval = fc.showOpenDialog(this);
			if(rval==JFileChooser.APPROVE_OPTION)
				tftemp.setText(fc.getSelectedFile().getAbsolutePath());			
		}
		else if(e.getSource()==emptyf)
		{
			int op = JOptionPane.showConfirmDialog(this,"Are you sure you want to delete all the temporarily stored files?","Confirm Option",JOptionPane.YES_NO_OPTION);
			if(op==JOptionPane.YES_OPTION)
			{
				File ff[] = new File("Files\\").listFiles();
				int i;
				for(i=0;i<ff.length;i++)
					ff[i].delete();
				JOptionPane.showMessageDialog(this,i+" files were deleted!","Message",JOptionPane.PLAIN_MESSAGE);
			}
			else
				JOptionPane.showMessageDialog(this,"The files were not deleted","Message",JOptionPane.PLAIN_MESSAGE);
			setupStartScreen();
		}
		else;
	}
	public void setupRestoreSummaryScreen()
	{
		if(c.getComponentCount()>0)
		{
			Component carr[] = c.getComponents();
			for(int j=0;j<carr.length;j++)
				c.remove(carr[j]);
		}

		JLabel ltemp = new JLabel("Wow! You have just now restored some files!");
		ltemp.setBounds(10,280,300,30);
		
		c.add(ltemp);
		c.add(new_search_btn);
		repaint();
	}
	public void getDelListSelection() //get all the elements that are selected from the list
	{
		int arr[] = jl.getSelectedIndices(); //returns the integer array to all the files that are selected in the Jlist.
		for(int i=0;i<arr.length;i++)
		{
			String tt = fileMatches[screen_count][arr[i]];
			delList[++dtop] = tt.substring( tt.indexOf(">>") + 2, tt.indexOf(">>", tt.indexOf(">>") + 2) ); //save the absolute paths of all the selected files, don't delete them right away.
		}
	}
	public void setupFinalScreen() //show all the items that were selected to be deleted for confirmation in a new list, take the user's choice.
	{
		if(dtop>-1)
		{
			if(c.getComponentCount()>0)
			{
				Component carr[] = c.getComponents();
				for(int j=0;j<carr.length;j++)
					c.remove(carr[j]);
			}
			
			dlm = new DefaultListModel();
			jl = new JList(dlm);
			jl.setVisible(true);
			jl.setLayoutOrientation(JList.VERTICAL);
			js = new JScrollPane(jl);
			js.setBounds(5,80,385,240);		
			jl.setBounds(10,80,350,220);
			js.setVisible(true);
			
			int delim = 60;
			int arr[] = new int[dtop+1];
			for(int i=0;i<=dtop;i++)
				arr[i] = i;
			boolean changer = true;
			String color;
			for(int i=0;i<=dtop;i++)
			{
				String tmp1 = delList[i];
				if(tmp1.length()>delim)
				{
					int tind = -1;
					int mod = tmp1.length();
					if(changer)
						color = "background-color:#dddddd;";
					else
						color = "";
					changer = !changer;
					for(; tmp1.lastIndexOf("\\")!=-1 && (tind = tmp1.lastIndexOf("\\",mod))>delim; mod = tmp1.lastIndexOf("\\",mod) - 1);
					if(tind!=-1)
						tmp1 = "<html><div style='" + color + "'>" + tmp1.substring(0,tind+1) + "<br/>" + tmp1.substring(tind+1) + "</div></html>";
					else
						tmp1 = "<html><div style='" + color + "'>" + tmp1.substring(0,delim) + "<br/>" + tmp1.substring(delim) + "</div></html>";
				}
				dlm.addElement(tmp1);
			}
			
			jl.setSelectedIndices(arr);
			JLabel lbl;
			
			go = new JButton("GO");
			go.addActionListener(this);
			go.setBounds(300,330,90,40);
			
			sel_all = new JButton("<html>Select<br/><center>All</center></html>");
			sel_all.addActionListener(this);
			sel_all.setBounds(6,35,125,40);
			
			sel_none = new JButton("<html>Select<br/><center>None</center></html>");
			sel_none.addActionListener(this);
			sel_none.setBounds(265,35,125,40);
			
			lbl = new JLabel("Select the files which you want to be deleted: ");
			this.setTitle("Confirm Deletion");
			lbl.setBounds(5,5,350,30);
					
			c.add(js);
			c.add(go);
			c.add(sel_all);
			c.add(sel_none);
			c.add(lbl);
		}
		else
		{
			setupSummaryScreen("<html>Looks like you didn't select anything!!<br/>Anyways you can select to start a new search!</html>");
		}
		repaint();
	}
	public boolean analyzeFile() throws Exception
	{
		File fl = new File("NameSizeAndModifiedMatches");
		if(fl.length()!=0){
		Scanner s = new Scanner(fl);
		int num[] = new int[100000];
		for(int i=0;i<100000;i++)
			num[i]=0;
		for(int i=0;s.hasNextLine();)
		{
			String temp = s.nextLine();
			if(!temp.equals(""))
				num[i]++;
			else
				i++;
		}
		s.close();
		s = new Scanner(fl);
		int i=0;
		for(i=0;s.hasNextLine();i++)
		{			
			fileMatches[i] = new String[num[i]];
			for(int j=0;j<num[i];j++)
				fileMatches[i][j] = s.nextLine();
			s.nextLine();
		}
		top = i-1;}
		else
			return false;
		return true;
	}
	public void setErrorLabel() //if the entered path in the textfield is not valid, show thw error dialog
	{
		c.add(elb);
		repaint();
	}
	public void enable_Button() //enable the button after the processing has been done
	{
		c.add(ebtn);
		repaint();
	}
	public void processing() //clear the screen and add the processing label
	{
		if(c.getComponentCount()>0)
		{
			Component carr[] = c.getComponents();
			for(int j=0;j<carr.length;j++)
				c.remove(carr[j]);
		}
		c.add(plb);
		repaint();
	}
	public void reSearch(String dest) throws Exception //if the new search button has been clicked, search again after taking the values
	{
		df = new DuplicateFinder();
		boolean enabler = true;
		if(df.init_dir(dest)!=-1)
		{
			processing();
			df.t.start();
			search_text = "";
			int cdots = 0;
			while(!search_text.equals("end"))
			{
				search_text = df.txt_update;
				if(enabler)
				{
					cdots = (cdots+1)%10;
					String str="";
					for(int tmp=0;tmp<cdots;tmp++)
						str+=".";
					plb.setText("Processing" + str);
				}
				Thread.sleep(100);
				if(!search_text.equals("end"));
				else
				{
					plb.setText("Search Completed!");
					enabler=false;
					freeable = df.match();
					if(analyzeFile())
						enable_Button();
					else
					{
						plb.setText("<html>Congrats! No duplicates were found!<br/>Start a new Search!</html>");
						c.add(new_search_btn);
						repaint();
					}
				}
			}
		}
		else
			setErrorLabel();
	}
	public static void main(String abc[]) throws Exception //main class. here we initialize our UI. this is a must for execution to start.
	{
		UIMain ui = new UIMain(); //create a new object of the UI class. the main frame will now show after this line. Important.
		ui.df = new DuplicateFinder();
		boolean enabler = true;
		while(true)
		{
			if(!ui.path.equals(""))
			{
				if(ui.df.init_dir(ui.path)!=-1)
				{
					ui.processing();
					ui.df.t.start();
					int cdots=0;
					while(!ui.search_text.equals("end"))
					{
						ui.search_text = ui.df.txt_update;
						if(enabler)
						{
							cdots = (cdots+1)%10;
							String str="";
							for(int tmp=0;tmp<cdots;tmp++)
								str+=".";
							ui.plb.setText("Processing" + str);
						}
						Thread.sleep(100);
						if(!ui.search_text.equals("end"));
						else
						{
							ui.plb.setText("Search Completed!");
							enabler=false;
							ui.freeable = ui.df.match();
							if(ui.analyzeFile())
								ui.enable_Button();
							else
							{
								ui.plb.setText("<html>Congrats! No duplicates were found!<br/>Start a new Search!</html>");
								ui.c.add(ui.new_search_btn);
								ui.repaint();
							}
						}
					}
					break;
				}
				else
					ui.setErrorLabel();
				ui.path="";
			}		
		}
	}
}