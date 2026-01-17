package com.example.assignment_3_fawzia;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SoftwareStoreApp extends Application {

    private BinarySearchTree bst;
    private static final String DATA_FILE = "software.txt";
    private TableView<SoftwareDisplay> tableView;
    private ObservableList<SoftwareDisplay> softwareList;
    private TextArea outputArea;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        bst = new BinarySearchTree();
        softwareList = FXCollections.observableArrayList();

        // Load data from file
        loadDataFromFile();

        // Create main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom right, #667eea 0%, #764ba2 100%);");

        // Header
        VBox header = createHeader();
        mainLayout.setTop(header);

        // Center - Split between table and tree visualization
        SplitPane centerPane = createCenterPane();
        mainLayout.setCenter(centerPane);

        // Bottom - Control buttons
        HBox controls = createControlButtons();
        mainLayout.setBottom(controls);

        // Create scene
        Scene scene = new Scene(mainLayout, 1000, 6000);
        scene.getStylesheets().add(getStylesheet());

        primaryStage.setTitle("💎 Software Store Management System 💎");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Fade in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), mainLayout);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: rgba(255, 255, 255, 0.15); " +
                "-fx-background-radius: 15; " +
                "-fx-border-radius: 15;");

        Label title = new Label("✨ SOFTWARE STORE MANAGEMENT ✨");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        title.setTextFill(Color.WHITE);

        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5));
        shadow.setRadius(10);
        title.setEffect(shadow);

        Label subtitle = new Label("Binary Search Tree Implementation");
        subtitle.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        subtitle.setTextFill(Color.rgb(255, 255, 255, 0.9));

        header.getChildren().addAll(title, subtitle);
        VBox.setMargin(header, new Insets(10));

        return header;
    }

    private SplitPane createCenterPane() {
        SplitPane splitPane = new SplitPane();
        splitPane.setStyle("-fx-background-color: transparent;");

        // Left side - Table view
        VBox tableBox = createTableView();

        // Right side - Output area
        VBox outputBox = createOutputArea();

        splitPane.getItems().addAll(tableBox, outputBox);
        splitPane.setDividerPositions(0.6);

        return splitPane;
    }

    private VBox createTableView() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); " +
                "-fx-background-radius: 15; " +
                "-fx-border-radius: 15;");

        Label label = new Label("📦 Current Inventory");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        label.setTextFill(Color.rgb(102, 126, 234));

        tableView = new TableView<>();
        tableView.setItems(softwareList);
        tableView.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10;");

        // Create columns
        TableColumn<SoftwareDisplay, String> nameCol = new TableColumn<>("Software Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(250);
        nameCol.setStyle("-fx-alignment: CENTER-LEFT;");

        TableColumn<SoftwareDisplay, String> versionCol = new TableColumn<>("Version");
        versionCol.setCellValueFactory(new PropertyValueFactory<>("version"));
        versionCol.setPrefWidth(100);
        versionCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<SoftwareDisplay, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setPrefWidth(100);
        quantityCol.setStyle("-fx-alignment: CENTER;");

// Add cell factory to color low quantities RED
        quantityCol.setCellFactory(column -> new TableCell<SoftwareDisplay, Integer>() {
            @Override
            protected void updateItem(Integer quantity, boolean empty) {
                super.updateItem(quantity, empty);

                if (empty || quantity == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(quantity.toString());
                    setAlignment(Pos.CENTER);

                    // Make text RED if quantity < 5
                    if (quantity < 5) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        TableColumn<SoftwareDisplay, Double> priceCol = new TableColumn<>("Price ($)");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(120);
        priceCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<SoftwareDisplay, Integer> positionCol = new TableColumn<>("File Pos");
        positionCol.setCellValueFactory(new PropertyValueFactory<>("position"));
        positionCol.setPrefWidth(100);
        positionCol.setStyle("-fx-alignment: CENTER;");

        tableView.getColumns().addAll(nameCol, versionCol, quantityCol, priceCol, positionCol);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        box.getChildren().addAll(label, tableView);

        return box;
    }

    private Canvas treeCanvas;

    private VBox createOutputArea() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: rgba(255, 255, 255, 0.95); " +
                "-fx-background-radius: 15; " +
                "-fx-border-radius: 15;");

        Label label = new Label("🌳 Binary Search Tree View");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        label.setTextFill(Color.rgb(118, 75, 162));

        // Create canvas for tree visualization
        treeCanvas = new Canvas(500, 500);
        treeCanvas.setStyle("-fx-background-color: white;");

        VBox.setVgrow(treeCanvas, Priority.ALWAYS);
        box.getChildren().addAll(label, treeCanvas);

        drawTree();

        return box;
    }

    private HBox createControlButtons() {
        HBox controls = new HBox(15);
        controls.setPadding(new Insets(20));
        controls.setAlignment(Pos.CENTER);
        controls.setStyle("-fx-background-color: rgba(255, 255, 255, 0.15); " +
                "-fx-background-radius: 15;");

        Button addBtn = createStyledButton("➕ Add Software", "#4CAF50");
        Button updateBtn = createStyledButton("📝 Update Quantity", "#2196F3");
        Button sellBtn = createStyledButton("💰 Sell Software", "#FF9800");
        Button searchBtn = createStyledButton("🔍 Search", "#9C27B0");
        Button refreshBtn = createStyledButton("🔄 Refresh & Clean", "#00BCD4");
        Button exitBtn = createStyledButton("🚪 Exit & Cleanup", "#F44336");

        addBtn.setOnAction(e -> showAddSoftwareDialog());
        updateBtn.setOnAction(e -> showUpdateQuantityDialog());
        sellBtn.setOnAction(e -> showSellSoftwareDialog());
        searchBtn.setOnAction(e -> showSearchDialog());
        refreshBtn.setOnAction(e -> refreshAndRemoveZeroQuantity());
        exitBtn.setOnAction(e -> exitAndCleanup());

        controls.getChildren().addAll(addBtn, updateBtn, sellBtn, searchBtn, refreshBtn, exitBtn);

        return controls;
    }



    private void refreshAndRemoveZeroQuantity() {
        // Remove items with 0 quantity from tree and table
        List<Software> toRemove = new ArrayList<>();

        // Collect items with 0 quantity
        for (SoftwareDisplay display : new ArrayList<>(softwareList)) {
            if (display.getQuantity() == 0) {
                bst.delete(display.getName() + display.getVersion());
                toRemove.add(new Software(display.getName(), display.getVersion(),
                        0, display.getPrice(), display.getPosition()));
            }
        }

        // Remove from table
        softwareList.removeIf(s -> s.getQuantity() == 0);

        // Redraw tree
        drawTree();

        showAlert("Success", "Removed " + toRemove.size() + " items with 0 quantity!",
                Alert.AlertType.INFORMATION);
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 12 25; " +
                "-fx-background-radius: 25; " +
                "-fx-cursor: hand;");

        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: derive(" + color + ", -20%); " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 14px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 12 25; " +
                    "-fx-background-radius: 25; " +
                    "-fx-cursor: hand; " +
                    "-fx-scale-x: 1.05; " +
                    "-fx-scale-y: 1.05;");
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: " + color + "; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-size: 14px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 12 25; " +
                    "-fx-background-radius: 25; " +
                    "-fx-cursor: hand;");
        });

        return button;
    }

    private void loadDataFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            createSampleData();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            int position = 0;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 4) {
                    String name = parts[0].trim();
                    String version = parts[1].trim();
                    int quantity = Integer.parseInt(parts[2].trim());
                    double price = Double.parseDouble(parts[3].trim());

                    Software software = new Software(name, version, quantity, price, position);
                    bst.insert(software);
                    softwareList.add(new SoftwareDisplay(software));
                }
                position++;
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to load data: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void createSampleData() {
        List<Software> samples = new ArrayList<>();
        samples.add(new Software("Adobe Photoshop", "7.0", 21, 580.0, 0));
        samples.add(new Software("Norton Utilities", "", 10, 30.0, 1));
        samples.add(new Software("Norton SystemWorks", "", 6, 50.0, 2));
        samples.add(new Software("Visual J++ Professional", "6.0", 19, 100.0, 3));
        samples.add(new Software("Visual J++ Standard", "6.0", 27, 40.0, 4));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (int i = 0; i < samples.size(); i++) {
                Software s = samples.get(i);
                writer.write(s.getName() + "\t" + s.getVersion() + "\t" +
                        s.getQuantity() + "\t" + s.getPrice());
                if (i < samples.size() - 1) writer.newLine();

                bst.insert(s);
                softwareList.add(new SoftwareDisplay(s));
            }
        } catch (IOException e) {
            showAlert("Error", "Failed to create sample data", Alert.AlertType.ERROR);
        }
    }

    private void showAddSoftwareDialog() {
        Dialog<Software> dialog = new Dialog<>();
        dialog.setTitle("➕ Add New Software");
        dialog.setHeaderText("Enter software details");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: linear-gradient(to bottom, #f8f9fa, #e9ecef);");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Software Name");
        TextField versionField = new TextField();
        versionField.setPromptText("Version (optional)");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity");
        TextField priceField = new TextField();
        priceField.setPromptText("Price");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Version:"), 0, 1);
        grid.add(versionField, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(quantityField, 1, 2);
        grid.add(new Label("Price:"), 0, 3);
        grid.add(priceField, 1, 3);

        dialogPane.setContent(grid);

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    String name = nameField.getText().trim();
                    String version = versionField.getText().trim();
                    int quantity = Integer.parseInt(quantityField.getText().trim());
                    double price = Double.parseDouble(priceField.getText().trim());

                    if (name.isEmpty()) {
                        showAlert("Error", "Name cannot be empty", Alert.AlertType.ERROR);
                        return null;
                    }

                    // Search if software already exists
                    TreeNode existing = bst.search(name + version);
                    if (existing != null) {
                        // Update quantity
                        existing.software.setQuantity(existing.software.getQuantity() + quantity);
                        updateFileAtPosition(existing.software);
                        refreshTable();
                        showAlert("Success", "Quantity updated for existing software!", Alert.AlertType.INFORMATION);
                        return null;
                    } else {
                        // Add new software at end of file
                        int position = getFileLineCount();
                        Software newSoftware = new Software(name, version, quantity, price, position);
                        return newSoftware;
                    }
                } catch (NumberFormatException e) {
                    showAlert("Error", "Invalid quantity or price", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(software -> {
            bst.insert(software);
            appendToFile(software);
            softwareList.add(new SoftwareDisplay(software));
            displayTreeStructure();
            showAlert("Success", "Software added successfully!", Alert.AlertType.INFORMATION);
        });
    }

    private void showUpdateQuantityDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("📝 Update Quantity");
        dialog.setHeaderText("Update software quantity");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: linear-gradient(to bottom, #f8f9fa, #e9ecef);");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Software Name");
        TextField versionField = new TextField();
        versionField.setPromptText("Version (optional)");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Additional Quantity");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Version:"), 0, 1);
        grid.add(versionField, 1, 1);
        grid.add(new Label("Add Quantity:"), 0, 2);
        grid.add(quantityField, 1, 2);

        dialogPane.setContent(grid);

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        Button updateButton = (Button) dialogPane.lookupButton(updateButtonType);
        updateButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            try {
                String name = nameField.getText().trim();
                String version = versionField.getText().trim();
                int additionalQty = Integer.parseInt(quantityField.getText().trim());

                TreeNode node = bst.search(name + version);
                if (node != null) {
                    node.software.setQuantity(node.software.getQuantity() + additionalQty);
                    updateFileAtPosition(node.software);
                    refreshTable();
                    displayTreeStructure();
                    showAlert("Success", "Quantity updated successfully!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Software not found!", Alert.AlertType.ERROR);
                    event.consume();
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid quantity", Alert.AlertType.ERROR);
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private void showSellSoftwareDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("💰 Sell Software");
        dialog.setHeaderText("Process software sale");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: linear-gradient(to bottom, #f8f9fa, #e9ecef);");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Software Name");
        TextField versionField = new TextField();
        versionField.setPromptText("Version (optional)");
        TextField quantityField = new TextField();
        quantityField.setPromptText("Quantity to Sell");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Version:"), 0, 1);
        grid.add(versionField, 1, 1);
        grid.add(new Label("Quantity:"), 0, 2);
        grid.add(quantityField, 1, 2);

        dialogPane.setContent(grid);

        ButtonType sellButtonType = new ButtonType("Sell", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(sellButtonType, ButtonType.CANCEL);

        Button sellButton = (Button) dialogPane.lookupButton(sellButtonType);
        sellButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            try {
                String name = nameField.getText().trim();
                String version = versionField.getText().trim();
                int sellQty = Integer.parseInt(quantityField.getText().trim());

                TreeNode node = bst.search(name + version);
                if (node != null) {
                    if (node.software.getQuantity() >= sellQty) {
                        int newQty = node.software.getQuantity() - sellQty;
                        node.software.setQuantity(newQty);

                        // Always just update quantity, never delete
                        updateFileAtPosition(node.software);
                        refreshTable();

                        if (newQty == 0) {
                            showAlert("Success", "All copies sold! Quantity set to 0.",
                                    Alert.AlertType.INFORMATION);
                        } else {
                            showAlert("Success", "Sale processed! Remaining: " + newQty,
                                    Alert.AlertType.INFORMATION);
                        }
                        displayTreeStructure();
                    } else {
                        showAlert("Error", "Insufficient quantity! Available: " +
                                node.software.getQuantity(), Alert.AlertType.ERROR);
                        event.consume();
                    }
                } else {
                    showAlert("Error", "Software not found!", Alert.AlertType.ERROR);
                    event.consume();
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid quantity", Alert.AlertType.ERROR);
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private void showSearchDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("🔍 Search Software");
        dialog.setHeaderText("Find software in inventory");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: linear-gradient(to bottom, #f8f9fa, #e9ecef);");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField();
        nameField.setPromptText("Software Name");
        TextField versionField = new TextField();
        versionField.setPromptText("Version (optional)");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Version:"), 0, 1);
        grid.add(versionField, 1, 1);

        dialogPane.setContent(grid);

        ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);

        Button searchButton = (Button) dialogPane.lookupButton(searchButtonType);
        searchButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String name = nameField.getText().trim();
            String version = versionField.getText().trim();

            TreeNode node = bst.search(name + version);
            if (node != null) {
                Software s = node.software;
                String info = String.format(
                        "✅ FOUND!\n\n" +
                                "Name: %s\n" +
                                "Version: %s\n" +
                                "Quantity: %d\n" +
                                "Price: $%.2f\n" +
                                "File Position: %d",
                        s.getName(), s.getVersion(), s.getQuantity(), s.getPrice(), s.getFilePosition()
                );
                showAlert("Search Result", info, Alert.AlertType.INFORMATION);
            } else {
                showAlert("Search Result", "❌ Software not found in inventory!",
                        Alert.AlertType.WARNING);
                event.consume();
            }
        });

        dialog.showAndWait();
    }

                    private void displayTreeStructure() {
                        drawTree();
                    }

                    private void drawTree() {
                        if (treeCanvas == null) return;

                        GraphicsContext gc = treeCanvas.getGraphicsContext2D();

                        // Clear canvas
                        gc.setFill(Color.WHITE);
                        gc.fillRect(0, 0, treeCanvas.getWidth(), treeCanvas.getHeight());

                        if (bst.root == null) {
                            gc.setFill(Color.BLACK);
                            gc.setFont(Font.font("Arial", 16));
                            gc.fillText("Tree is empty", treeCanvas.getWidth() / 2 - 50, treeCanvas.getHeight() / 2);
                            return;
                        }

                        // Draw the tree
                        drawNode(gc, bst.root, treeCanvas.getWidth() / 2, 50, treeCanvas.getWidth() / 4);
                    }

                    private void drawNode(GraphicsContext gc, TreeNode node, double x, double y, double xOffset) {
                        if (node == null) return;

                        double radius = 30;

                        // Draw lines to children first (so they appear behind circles)
                        if (node.left != null) {
                            gc.setStroke(Color.BLACK);
                            gc.setLineWidth(2);
                            gc.strokeLine(x, y, x - xOffset, y + 80);
                            drawNode(gc, node.left, x - xOffset, y + 80, xOffset / 2);
                        }

                        if (node.right != null) {
                            gc.setStroke(Color.BLACK);
                            gc.setLineWidth(2);
                            gc.strokeLine(x, y, x + xOffset, y + 80);
                            drawNode(gc, node.right, x + xOffset, y + 80, xOffset / 2);
                        }

                        // Draw circle with gradient (blue like the image)
                        // Draw circle - RED if quantity < 5, otherwise blue
                        int quantity = node.software.getQuantity();
                        if (quantity < 5) {
                            gc.setFill(Color.rgb(220, 53, 69)); // RED for low quantity
                        } else {
                            gc.setFill(Color.rgb(66, 103, 178)); // Blue color
                        }
                        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);

                        // Draw white border
                        gc.setStroke(Color.WHITE);
                        gc.setLineWidth(3);
                        gc.strokeOval(x - radius, y - radius, radius * 2, radius * 2);

                        // Draw quantity number in white
                        gc.setFill(Color.WHITE);
                        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                        String qtyText = String.valueOf(node.software.getQuantity());

                        // Center the text
                        javafx.scene.text.Text text = new javafx.scene.text.Text(qtyText);
                        text.setFont(gc.getFont());
                        double textWidth = text.getLayoutBounds().getWidth();
                        double textHeight = text.getLayoutBounds().getHeight();

                        gc.fillText(qtyText, x - textWidth / 2, y + textHeight / 4);
                    }

    private void exitAndCleanup() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText("Exit and cleanup file?");
        alert.setContentText("This will remove all entries with 0 quantity from the file.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                cleanupFile();
                showAlert("Success", "File cleaned up successfully! Goodbye! 👋",
                        Alert.AlertType.INFORMATION);
                primaryStage.close();
            }
        });
    }

    private void cleanupFile() {
        try {
            List<Software> activeItems = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 4) {
                    int quantity = Integer.parseInt(parts[2].trim());
                    if (quantity > 0) {
                        Software s = new Software(
                                parts[0].trim(),
                                parts[1].trim(),
                                quantity,
                                Double.parseDouble(parts[3].trim()),
                                activeItems.size()
                        );
                        activeItems.add(s);
                    }
                }
            }
            reader.close();

            // Rewrite file with only active items
            BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE));
            for (int i = 0; i < activeItems.size(); i++) {
                Software s = activeItems.get(i);
                writer.write(s.getName() + "\t" + s.getVersion() + "\t" +
                        s.getQuantity() + "\t" + s.getPrice());
                if (i < activeItems.size() - 1) writer.newLine();
            }
            writer.close();

        } catch (IOException e) {
            showAlert("Error", "Failed to cleanup file: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateFileAtPosition(Software software) {
        try {
            List<String> lines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE));
            String line;

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            reader.close();

            if (software.getFilePosition() < lines.size()) {
                String updated = software.getName() + "\t" + software.getVersion() + "\t" +
                        software.getQuantity() + "\t" + software.getPrice();
                lines.set(software.getFilePosition(), updated);
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE));
            for (int i = 0; i < lines.size(); i++) {
                writer.write(lines.get(i));
                if (i < lines.size() - 1) writer.newLine();
            }
            writer.close();

        } catch (IOException e) {
            showAlert("Error", "Failed to update file", Alert.AlertType.ERROR);
        }
    }

    private void appendToFile(Software software) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE, true))) {
            writer.newLine();
            writer.write(software.getName() + "\t" + software.getVersion() + "\t" +
                    software.getQuantity() + "\t" + software.getPrice());
        } catch (IOException e) {
            showAlert("Error", "Failed to append to file", Alert.AlertType.ERROR);
        }
    }

    private int getFileLineCount() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
            int count = 0;
            while (reader.readLine() != null) count++;
            return count;
        } catch (IOException e) {
            return 0;
        }
    }

    private void refreshTable() {
        softwareList.clear();
        List<Software> allSoftware = new ArrayList<>();
        bst.collectAllSoftware(bst.root, allSoftware);
        for (Software s : allSoftware) {
            softwareList.add(new SoftwareDisplay(s));
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private String getStylesheet() {
        return "data:text/css," +
                ".table-view { -fx-background-color: transparent; }" +
                ".table-view .column-header { -fx-background-color: linear-gradient(#667eea, #764ba2); " +
                "-fx-text-fill: white; -fx-font-weight: bold; }" +
                ".table-row-cell:hover { -fx-background-color: #e3f2fd; }";
    }

    public static void main(String[] args) {
        launch(args);
    }
}