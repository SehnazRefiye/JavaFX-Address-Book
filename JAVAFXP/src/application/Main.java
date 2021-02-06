package application;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class Main extends Application 
{
	int numberOfPeopleInArray=0; //number of elements in array, only changes when person added into array.
	int index=0;
	Person[] arrayOfPerson;
	// Specify the size of five string fields in the record
	final static int ID_SIZE = 4;
	final static int NAME_SIZE = 32;
	final static int STREET_SIZE = 32;
	final static int CITY_SIZE = 20;
	final static int GENDER_SIZE = 1;
	final static int ZIP_SIZE = 5;
	final static int RECORD_SIZE =(ID_SIZE+NAME_SIZE + STREET_SIZE + CITY_SIZE + GENDER_SIZE + ZIP_SIZE);
	public RandomAccessFile raf;

	// Text fields
	TextField tfID = new TextField();
	TextField tfName = new TextField();
	TextField tfStreet = new TextField();
	TextField tfCity = new TextField();
	TextField tfUpdate_SearchID = new TextField();
	TextField tfGender = new TextField();
	TextField tfZip = new TextField();

	// Buttons
	Button btAdd = new Button("Add");
	Button btFirst = new Button("First");
	Button btNext = new Button("Next");
	Button btPrevious = new Button("Previous");
	Button btLast = new Button("Last");
	Button btUpdateByID = new Button("UpdateByID");
	Button btSearchByID = new Button("SearchByID");
	Button btClear = new Button("Clean textFields");

	// Labels
	Label lbID= new Label("ID");
	Label lbName= new Label("Name");
	Label lbStreet= new Label("Street");
	Label lbCity= new Label("City");
	Label lbUpdate_SearchID = new Label("Update/Search ID");
	Label lbGender= new Label("Gender");
	Label lbZip= new Label ("Zip");

	public Main()
	{
		// Open or create a random access file
		try
		{
			raf = new RandomAccessFile("address.dat", "rw");
			arrayOfPerson=new Person[100];
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	public void start(Stage primaryStage) 
	{
		try 
		{
			tfID.setPrefColumnCount(4);
			tfID.setDisable(true);
			tfGender.setPrefColumnCount(1);
			tfZip.setPrefColumnCount(4);
			tfCity.setPrefColumnCount(12);

			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText("Look, an Information Dialog");

			// Pane p1 for holding labels Name, Street, and City
			GridPane p1 = new GridPane();
			p1.setAlignment(Pos.CENTER);
			p1.setHgap(5);
			p1.setVgap(5);
			p1.add(lbID, 0, 0);
			p1.add(tfID, 1, 0);
			p1.add(lbUpdate_SearchID, 2, 0);
			p1.add(tfUpdate_SearchID, 3, 0);
			p1.add(lbName, 0, 1);
			p1.add(tfName, 1, 1);
			p1.add(lbStreet, 0, 2);
			p1.add(tfStreet, 1, 2);
			p1.add(lbCity, 0, 3);

			//Hbox Pane p2 for grid[3][1]
			HBox p2 = new HBox(5);
			p2.getChildren().addAll(tfCity,lbGender,tfGender,lbZip,tfZip);
			p1.add(p2, 1,3);

			//For buttons
			HBox p3 = new HBox(7);
			p3.getChildren().addAll(btAdd,btFirst,btNext,btPrevious,btLast,btUpdateByID,btSearchByID,btClear);
			p3.setAlignment(Pos.CENTER);

			//Border Pane
			BorderPane borderPane= new BorderPane();
			borderPane.setCenter(p1);
			borderPane.setBottom(p3);

			Scene scene = new Scene(borderPane,550,180);
			primaryStage.setTitle("Address Book Project");
			primaryStage.setScene(scene);
			primaryStage.show();

			// Display the first record if exists
			try 
			{

				if (raf.length() > 0)
				{
					long currentPos= raf.getFilePointer();
					while(currentPos < raf.length())
					{
						readFileFillArray(arrayOfPerson, currentPos);
						currentPos=raf.getFilePointer();
					}
					readFileByPos(0);
				}
			}
			catch (IOException ex) 
			{
				ex.printStackTrace();
			}
			
			btAdd.setOnAction(e-> {
				try 
				{
					boolean areTheBoxesEmpty=false;
					if(Objects.equals(tfName.getText(), "") || Objects.equals(tfStreet.getText(), "") || Objects.equals(tfCity.getText(), "")
							|| Objects.equals(tfGender.getText(), "") || Objects.equals(tfZip.getText(), "")) 
					{
						areTheBoxesEmpty = true;
					}					
					if(areTheBoxesEmpty==false)
					{
						writeAddressToFile(raf.length());
						readFileFillArray(arrayOfPerson, RECORD_SIZE*2*(numberOfPeopleInArray)); //ADD LAST RECORD(IN THE FILE) TO ARRAY
						alert.setContentText("Record is added successfully");
						alert.showAndWait();
						cleanTextFields();
					}
					else 
					{
						System.out.println("Please do not leave boxes blank.");
					}
					
				} catch (Exception ex) 
				{
					System.out.println("Failed to add record!");
				}
			});

			btFirst.setOnAction(e -> {
				index = 0;
				ekranaBastir(arrayOfPerson, index);
				System.out.println("numberOfPeopleInArray="+numberOfPeopleInArray);
				System.out.println("index="+index);
			});

			btLast.setOnAction(e -> {
				index = numberOfPeopleInArray-1;
				ekranaBastir(arrayOfPerson, index);
				System.out.println("numberOfPeopleInArray="+numberOfPeopleInArray);
				System.out.println("index="+index);

			});
			btNext.setOnAction(e->{
				if(index==numberOfPeopleInArray-1) 
				{
					System.out.println("You are viewing the last person.");
				}
				else 
				{
					index++;
					ekranaBastir(arrayOfPerson, index);
				}
				System.out.println("numberOfPeopleInArray="+numberOfPeopleInArray);
				System.out.println("index="+index);
			});
			btPrevious.setOnAction(e->{
				if(index==0) {
					System.out.println("You are viewing the first person.");
				}
				else {
					index--;
					ekranaBastir(arrayOfPerson, index);
				}
				System.out.println("numberOfPeopleInArray="+numberOfPeopleInArray);
				System.out.println("index="+index);	
			});
			
			btSearchByID.setOnAction(e->{
				boolean personFound = false;
				for(int i = 0; i<numberOfPeopleInArray; i++)
				{					
					if(arrayOfPerson[i].getId() == Integer.parseInt(tfUpdate_SearchID.getText())) {
						
						personFound=true;
						index = i;
						ekranaBastir(arrayOfPerson, index);
					}
				}
				if(personFound==false)
				{
					System.out.println("Person not found!");
				}
			});
			
			btUpdateByID.setOnAction(e->{
				try {
					for(int x = 0; x<numberOfPeopleInArray; x++) 
					{			
						if(arrayOfPerson[x].getId() == Integer.parseInt(tfUpdate_SearchID.getText())) 
						{						 
							arrayOfPerson[x].setName(tfName.getText());
							arrayOfPerson[x].setCity(tfCity.getText());
							arrayOfPerson[x].setStreet(tfStreet.getText());
							arrayOfPerson[x].setGender(tfGender.getText());
							arrayOfPerson[x].setZip(tfZip.getText());
							
							raf.seek(RECORD_SIZE*2*(x));
							FileOperations.writeFixedLengthString(tfUpdate_SearchID.getText(),ID_SIZE, raf);
							FileOperations.writeFixedLengthString(tfName.getText(), NAME_SIZE, raf);
							FileOperations.writeFixedLengthString(tfStreet.getText(), STREET_SIZE, raf);
							FileOperations.writeFixedLengthString(tfCity.getText(), CITY_SIZE, raf);
							FileOperations.writeFixedLengthString(tfGender.getText(), GENDER_SIZE, raf);
							FileOperations.writeFixedLengthString(tfZip.getText(), ZIP_SIZE, raf);
						}
					}
				}
				catch(Exception ex) {
					System.out.println("You have entered invalid information.");
				}
			});
			btClear.setOnAction(e -> {
				cleanTextFields();
			}); 
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/** Write a record at the end of the file */
	public void writeAddressToFile(long position) {
		try {
			int numberOfPeopleInArraypp = numberOfPeopleInArray + 1;
			raf.seek(position);
			FileOperations.writeFixedLengthString(Integer.toString(numberOfPeopleInArraypp),ID_SIZE, raf);
			FileOperations.writeFixedLengthString(tfName.getText(), NAME_SIZE, raf);
			FileOperations.writeFixedLengthString(tfStreet.getText(), STREET_SIZE, raf);
			FileOperations.writeFixedLengthString(tfCity.getText(), CITY_SIZE, raf);
			FileOperations.writeFixedLengthString(tfGender.getText(), GENDER_SIZE, raf);
			FileOperations.writeFixedLengthString(tfZip.getText(), ZIP_SIZE, raf);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	public void readFileFillArray(Person[]people,long position) throws IOException {
		raf.seek(position);
		String id = FileOperations.readFixedLengthString(ID_SIZE, raf);
		int intID= Integer.parseInt(id.trim().toString());
		String name = FileOperations.readFixedLengthString(NAME_SIZE, raf).trim();
		String street = FileOperations.readFixedLengthString(STREET_SIZE, raf).trim();
		String city = FileOperations.readFixedLengthString(CITY_SIZE, raf).trim();
		String gender= FileOperations.readFixedLengthString(GENDER_SIZE, raf).trim();
		String zip = FileOperations.readFixedLengthString(ZIP_SIZE, raf).trim();

		Person p = new Person(intID, name, gender, street, city, zip);
		people[numberOfPeopleInArray]=p;
		numberOfPeopleInArray++;
	}
	/** Read a record at the specified position */
	public void readFileByPos(long position) throws IOException {
		raf.seek(position);
		String id = FileOperations.readFixedLengthString(ID_SIZE, raf);
		String name = FileOperations.readFixedLengthString(NAME_SIZE, raf);
		String street = FileOperations.readFixedLengthString(STREET_SIZE, raf);
		String city = FileOperations.readFixedLengthString(CITY_SIZE, raf);
		String gender = FileOperations.readFixedLengthString(GENDER_SIZE, raf);
		String zip = FileOperations.readFixedLengthString(ZIP_SIZE, raf);

		tfID.setText(id);
		tfName.setText(name);
		tfStreet.setText(street);
		tfCity.setText(city);
		tfGender.setText(gender);
		tfZip.setText(zip);
	}
	public void cleanTextFields()
	{
		tfID.clear();
		tfName.clear();
		tfStreet.clear();
		tfCity.clear();
		tfGender.clear();
		tfZip.clear();
	}
	public void ekranaBastir(Person[]people,int numberOfPeopleInArray)
	{
		tfID.setText(String.valueOf(arrayOfPerson[numberOfPeopleInArray].getId()));
		tfName.setText(arrayOfPerson[numberOfPeopleInArray].getName());
		tfStreet.setText(arrayOfPerson[numberOfPeopleInArray].getStreet());
		tfCity.setText(arrayOfPerson[numberOfPeopleInArray].getCity());
		tfGender.setText(arrayOfPerson[numberOfPeopleInArray].getGender());
		tfZip.setText(arrayOfPerson[numberOfPeopleInArray].getZip());
	}

	public static void main(String[] args) 
	{
		launch(args);
	}
}