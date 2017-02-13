
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AccessingDatabaseFromApplet extends JApplet{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField jtfFirst, jtfLast, jtfGrade;
    private JButton jbtInsert, jbtSearch, jbtNext;
    private Statement statement;
    private Connection connection;
    private boolean moreResults;
    private ResultSet resultSet;

    @Override
    public void init(){
        //connect to database
        initDB();
        moreResults = false;
        //UI
        setLayout(new GridLayout(5,2, 4, 4));
        add(new JLabel("First Name", SwingConstants.RIGHT));
        add(jtfFirst = new JTextField());
        add(new JLabel("Last Name", SwingConstants.RIGHT));
        add(jtfLast = new JTextField());
        add(new JLabel("Grade", SwingConstants.RIGHT));
        add(jtfGrade = new JTextField());
        add(jbtInsert = new JButton("Insert"));
        add(jbtSearch = new JButton("Search"));
        add(jbtNext = new JButton("Next Matching Result"));
        //add action listeners
        ButtonActionListener listener = new ButtonActionListener();
        jbtInsert.addActionListener(listener);
        jbtSearch.addActionListener(listener);
        jbtNext.addActionListener(listener);

        Timer timer = new Timer(25, new ActionListener(){public void actionPerformed(ActionEvent e){
        enableButtons();}});
        timer.start();

    }

    @Override
    public void destroy(){
        try{
            System.out.println("Closing connection to database.");
            connection.close();
        } catch(Exception ex) {
            System.err.println(ex);
        }
    }

    public void initDB() {
        try {
            //load driver
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver"); //returns the DB class
            System.out.println("Driver loaded.");
            //establish connection
            connection = DriverManager.getConnection("jdbc:odbc:Students");
            System.out.println("Connection established.");
            //create statement
            statement = connection.createStatement();

        } catch (Exception ex) {
            System.err.println("Unable to connect to database. Program will exit.");
            System.err.println(ex);
            System.exit(0);
        }
    }

    public void enableButtons(){
        int filled = 0;
        if(!jtfFirst.getText().equals("")){
            filled++;
        }
        if(!jtfLast.getText().equals("")){
            filled++;
        }
        if(!jtfGrade.getText().equals("")){
            filled++;
        }
        switch(filled){
            case 0: jbtInsert.setEnabled(false); jbtSearch.setEnabled(false); break;
            case 1: jbtInsert.setEnabled(false); jbtSearch.setEnabled(true); break;
            case 2: jbtInsert.setEnabled(false); jbtSearch.setEnabled(true); break;
            case 3: jbtInsert.setEnabled(true); jbtSearch.setEnabled(false); break;
        }
        if(moreResults){
            jbtNext.setEnabled(true); jbtSearch.setEnabled(false); jbtInsert.setEnabled(false);
        } else {
            jbtNext.setEnabled(false);
        }

    }

    class ButtonActionListener implements ActionListener {

        private String command;

        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(jbtInsert)) { //INSERT NEW ENTRY
                insert(); //set up command statement
                try {
                    System.out.println("Attempting to use command: " + command);
                    statement.executeUpdate(command);
                } catch (Exception ex) {
                    System.err.println("Unable to add new entry to database.");
                    System.err.println(ex);
                }

            } else if (e.getSource().equals(jbtSearch)) { //SEARCH FOR ENTRY
                search(); //set up command statement

                try {

                    System.out.println("Attempting to use command: " + command);
                    resultSet = statement.executeQuery(command);
                    //display results in text fields
                    if(resultSet.next()) {
                        moreResults = true;
                    } else {
                        moreResults = false;
                     System.out.println("No matching results were found.");
                    }

                } catch (Exception ex) {
                    System.err.println("Unable to search database for entry.");
                    System.err.println(ex);
                }
            } else if (e.getSource().equals(jbtNext)) {
                try {
                    jtfFirst.setText(resultSet.getString("firstName"));
                    jtfLast.setText(resultSet.getString("lastName"));
                    jtfGrade.setText(resultSet.getString("grade"));

                    if (resultSet.next()) {
                        moreResults = true;
                    } else {
                        moreResults = false;
                    }
                } catch (Exception ex) {
                    System.err.println("Unable to read next result.");
                    System.err.println(ex);
                }
            }
        }

        public void insert(){
            command = "insert into Students(firstName, lastName, grade) values('";
            command += jtfFirst.getText()+"','";
            command += jtfLast.getText()+"','";
            command += jtfGrade.getText()+"');";
        }

        public void search() {
            command = "select firstName, lastName, grade from Students where";
            if (!jtfFirst.getText().equals("")) {
                command += " firstName = '" + jtfFirst.getText() + "' and";
            }
            if (!jtfLast.getText().equals("")) {
                command += " lastName = '" + jtfLast.getText() + "' and";
            }
            if (!jtfGrade.getText().equals("")) {
                command += " grade = '" + jtfGrade.getText() + "'";
            }
            //delete trailing "and"
            if(command.substring(command.length()-4).equals(" and")){
                command = command.substring(0, command.length()-4);
            }
            command += ";";

        }

    }

}
