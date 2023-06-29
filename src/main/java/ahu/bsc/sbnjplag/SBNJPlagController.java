package ahu.bsc.sbnjplag;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SBNJPlagController {

    @FXML
    private Label directoryLabel;
    @FXML
    private ChoiceBox<String> languageChoiceBox;
    @FXML
    private Button runButton;
    @FXML
    private TextArea excludedFilesInputTextField;

   @FXML
   private TextField sensitivityNumberInputTextField;

    private Stage stage;
    private String selectedDirectory;
    private String selectedLanguage;
    private String generatedCommand;
    private String excludedFiles;

    private String sensitivityNumber;
    private String excludedFilesDocumentPath;
    private String programDirectoryPath = System.getProperty("user.dir");

    @FXML
    private void initialize() {
        // Populate the choice box with available languages
        ObservableList<String> languages = FXCollections.observableArrayList("Java", "C", "Python");
        languageChoiceBox.setItems(languages);

        // Set the default selected language
        languageChoiceBox.setValue("Java");
        selectedLanguage = "java";

        runButton.setDisable(true);
    }



    @FXML
    private void selectDirectory(ActionEvent event) {
        // Create a directory chooser dialog
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory");

        // Show the directory chooser dialog and get the selected directory path
        String selectedDirectory = directoryChooser.showDialog(stage).getAbsolutePath();

        // Update the directory label
        directoryLabel.setText("Selected Directory: " + selectedDirectory);

        // Construct the CLI command
        generatedCommand = "java -jar ../jplag-4.3.0-jar-with-dependencies.jar " + selectedDirectory;

        runButton.setDisable(false);
    }


    private String createResultsDirectory() {
        // Get the program directory


        // Create a new directory with a timestamp one level above the program directory
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String resultsDirectoryPathWithTimestamp = programDirectoryPath + File.separator + ".." + File.separator + timestamp;
        File resultsDirectory = new File(resultsDirectoryPathWithTimestamp);

        if (resultsDirectory.mkdir()) {
            System.out.println("Results directory created: " + resultsDirectoryPathWithTimestamp);
            return resultsDirectoryPathWithTimestamp;
        } else {
            System.out.println("Failed to create results directory.");
        }

        return null;
    }

   @FXML
    private void saveFilesToExclude(ActionEvent event) {
        // Get the custom input from the text field
        excludedFiles = excludedFilesInputTextField.getText();

        // Create a new text file with the custom input
        excludedFilesDocumentPath = createTextFile(excludedFiles);
    }

    @FXML
    private void saveSensitivityNumber(ActionEvent event) {
        sensitivityNumber = sensitivityNumberInputTextField.getText();
    }


    private String createTextFile(String input) {
        // Create a new text file with a timestamp
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String textFilePath = programDirectoryPath + File.separator + ".." + File.separator + timestamp + ".txt";
        File textFile = new File(textFilePath);

        try (FileWriter writer = new FileWriter(textFile)) {
            // Write the custom input to the text file
            writer.write(input);
            System.out.println("Custom input saved to: " + textFilePath);
            return textFilePath;
        } catch (IOException e) {
            System.out.println("Failed to create text file.");
            e.printStackTrace();
        }

        return null;
    }

    @FXML
    private void selectLanguage(ActionEvent event) {

        if(languageChoiceBox.getValue().equals("Java")){
            selectedLanguage = "java";
        }
        if(languageChoiceBox.getValue().equals("C")){
            selectedLanguage = "cpp";
        }
        if(languageChoiceBox.getValue().equals("Python")){
            selectedLanguage = "python3";
        }

        // Construct the CLI command
        generateCommand();
    }

    private void generateCommand() {
        if (selectedDirectory != null && selectedLanguage != null) {
            generatedCommand = "java -jar ./jplag-4.3.0-jar-with-dependencies.jar " + selectedDirectory;
            runButton.setDisable(false);
        }

    }

    @FXML
    private void runCommand(ActionEvent event) {
        if (generatedCommand != null) {
            // Create a directory to save the results
            String resultsDirectoryPath = createResultsDirectory();


            if(selectedLanguage != null){
                generatedCommand += " -l " + selectedLanguage;
            }

            // Check if the results directory was successfully created
            if (resultsDirectoryPath != null) {
                // Append the results directory path to the generated command
                generatedCommand += " -r " + resultsDirectoryPath;


            }

            if (excludedFilesDocumentPath != null) {
                // Append the text file path to the generated command
                generatedCommand += " -x " + excludedFilesDocumentPath;


            }

            if (sensitivityNumber != null && sensitivityNumber.matches("\\d+")) {
                generatedCommand += " -t " + sensitivityNumber;
            }



            // Execute the CLI command
            runCLICommand(generatedCommand);

        }

    }

    private void runCLICommand(String cliCommand) {
        System.out.println(generatedCommand);
        System.out.println(sensitivityNumber);
        System.out.println(excludedFiles);
        try {
            // Create the process builder with the CLI command
            ProcessBuilder processBuilder = new ProcessBuilder(cliCommand.split(" "));
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Read the output of the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // Do something with the output, such as printing it or processing it further
                System.out.println(line);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("Process exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openReportViewer(ActionEvent event) {
        // Open the report viewer in the default web browser
        String reportViewerURL = "https://jplag.github.io/JPlag/";
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(reportViewerURL));
        } catch (java.io.IOException e) {
            System.out.println("Failed to open the report viewer.");
            e.printStackTrace();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}

