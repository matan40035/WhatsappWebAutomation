package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WhatsApp extends JFrame {
    private static final String WHATSAPP_WEB_URL = "https://web.whatsapp.com/";
    private static final int TIME_TO_LOGIN = 30000;
    private static final int MAIN_WINDOW_WIDTH = 500;
    private static final int MAIN_WINDOW_HEIGHT = 400;
    private static final int WAIT_TIME = 5000;
    private final int CONFIRMATION_FRAME_WIDTH = 300;
    private final int CONFIRMATION_FRAME_HEIGHT = 200;
    private final int TEXT_FIELD_COLUMNS = 15;


    private static JFrame confirmationFrame;
    private static JLabel confirmationLabel;

    public WhatsApp() {
        JFrame frame = new JFrame("Whatsapp Web");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        final JTextField phoneNumberField = new JTextField(TEXT_FIELD_COLUMNS);
        panel.add(new JLabel("Phone Number: 05"));
        panel.add(phoneNumberField);

        final JTextField messageField = new JTextField(TEXT_FIELD_COLUMNS);
        panel.add(new JLabel("Message Content:"));
        panel.add(messageField);

        JButton button = new JButton("Send Message");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String phoneNumber = phoneNumberField.getText().trim();
                String messageContent = messageField.getText().trim();

                if (phoneNumber.isEmpty() || messageContent.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter both phone number and message content.");
                } else if (!isValidMobileNumber(phoneNumber)) {
                    JOptionPane.showMessageDialog(frame, "Invalid mobile number format.");
                } else {
                    openWhatsappConversation(phoneNumber, messageContent);
                }
            }
        });
        panel.add(button);

        frame.getContentPane().add(panel);
        frame.setVisible(true);

        confirmationFrame = new JFrame("Message Confirmation");
        confirmationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        confirmationFrame.setSize(CONFIRMATION_FRAME_WIDTH, CONFIRMATION_FRAME_HEIGHT);

        JPanel confirmationPanel = new JPanel();
        confirmationPanel.setLayout(new FlowLayout());

        confirmationLabel = new JLabel();
        confirmationPanel.add(confirmationLabel);

        confirmationFrame.getContentPane().add(confirmationPanel);
        confirmationFrame.setVisible(false);
    }

    private static boolean isValidMobileNumber(final String phoneNumber) {
        final int MOBILE_NUMBER_LENGTH = 8;

        if (phoneNumber.length() != MOBILE_NUMBER_LENGTH) {
            return false;
        }

        if (!phoneNumber.matches("\\d+")) {
            return false;
        }
        return true;
    }

    private static void openWhatsappConversation( String number,  String message) {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\מתן ושיראל\\Downloads\\chromedriver_win32\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get(WHATSAPP_WEB_URL);
        wait(TIME_TO_LOGIN);
        JOptionPane.showMessageDialog(null, "Login was successful.");
        driver.get("https://web.whatsapp.com/send?phone=97205" + number);
        wait(WAIT_TIME*3);
        WebElement messageInput = driver.findElement(By.xpath("//div[@contenteditable='true'][@data-tab='10']"));
        messageInput.sendKeys(message);
        messageInput.sendKeys(Keys.ENTER);
        JOptionPane.showMessageDialog(null, "send message was successful.");

        wait(WAIT_TIME);
        new Thread(() -> {
            while (true) {
                WebElement singleVMark = null;
                try {
                    singleVMark = driver.findElement(By.xpath("//span[contains(@aria-label, 'נשלחה')]"));
                    if (singleVMark.isDisplayed()) {
                        showConfirmationMessage("The message has been sent");
                        wait(WAIT_TIME);
                    }
                } catch (NoSuchElementException e) {
                    // Single V mark not found
                }

                WebElement doubleVMark = null;
                try {
                    doubleVMark = driver.findElement(By.xpath("//span[contains(@aria-label, 'נמסרה')]"));
                    if (doubleVMark.isDisplayed()) {
                        showConfirmationMessage("The message has been delivered");
                        //`wait(WAIT_TIME);
                    }
                } catch (NoSuchElementException e) {
                    // Double V mark not found
                }

                try {
                    WebElement blueVMark = driver.findElement(By.xpath("//span[contains(@aria-label, 'נקראה')]"));
                    if (blueVMark.isDisplayed()) {
                        // Blue V mark found
                    }
                } catch (NoSuchElementException e) {
                    // Blue V mark not found
                }

                if (singleVMark == null && doubleVMark == null) {
                    showConfirmationMessage("The message has been read.");
                    driver.quit();
                    break;
                }
            }
        }).start();
    }

    private static void showConfirmationMessage(String message) {
        confirmationLabel.setText(message);
        confirmationFrame.setVisible(true);
    }

    private static void wait(final int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}