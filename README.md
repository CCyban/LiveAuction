# Live Auction

## Contents
1. [Description](#Description)
2. [Starting](#Starting)
3. [Usage](#Usage)
7. [Tests](#Tests)

## Description 
Live Auction utilises a server socket for viewing live auction data and live bidding. All important socket communication is done under AES encryption.

At the start of the client-server socket connection, the keys are meant to be exchanged. The asymmetric public key from the server naturally uses no encryption, whereas the secret key sent from the client to the server uses RES encryption from that public key. Any afterwards communication is done under symmetric RES encryption.

- - - -

## Starting
- Note: This tutorial uses an IDE called IntelliJ IDEA.
1. Download the ZIP of this repository's master branch.
- ![image](https://user-images.githubusercontent.com/47162481/160638070-d1962d45-618e-43e9-a2f4-43dad5cecd56.png)
2. Download a JavaFX SDK. They can be found here: https://gluonhq.com/products/javafx/. I recommend getting version 18 because that is what I used within development.
- ![image](https://user-images.githubusercontent.com/47162481/160640950-5bb97026-b973-45b4-b331-d6a6b5e3f0ee.png)
3. Extract both downloaded ZIP files.
4. Open the extracted **LiveAuction-main** folder > Open the **server** folder > Go to the **src\java\com\github\ccyban\liveauction\server** folder > Open **Main.java** using IntelliJ IDEA.
- You should have opened LiveAuction-main\server\src\java\com\github\ccyban\liveauction\server\Main.java in IntelliJ IDEA at this point.
5. In IntelliJ, go to **File** > **Project Structure**.
6. Go to the Libraries tab, press the add button and select Java.
- ![image](https://user-images.githubusercontent.com/47162481/160639987-4b003de7-b8be-4f3b-831e-9294ae2244a4.png)
7. Go inside the extracted JavaFX SDK folder, open the **javafx-sdk-18** folder & select the **lib** folder inside it.
- ![image](https://user-images.githubusercontent.com/47162481/160640592-eee3c97d-8f17-48b7-b66a-02fe30db9c91.png)
8. A dialog window as shown will appear, press OK.
- ![image](https://user-images.githubusercontent.com/47162481/160639807-34d38ca8-9894-4e04-aded-7fd7e0091b7a.png)
9. Now that you have added the JavaFX lib folder to the project structure, you can finalise the changes and exit the **Project Structure** window by pressing the **OK** button.
10. On the top-right, press the **Add Configuration...** button.
- ![image](https://user-images.githubusercontent.com/47162481/120002753-094a5780-bfcd-11eb-8402-0a63b592fb2c.png)
11. Then press **Add new run configuration...** and select **Application**
- ![image](https://user-images.githubusercontent.com/47162481/120003031-43b3f480-bfcd-11eb-9a1d-52048fa3263d.png)
12. You can give the name field any name you want it to use, I recommend Server because it is a fitting name.
13. In the **Main class** field, type **Main**.
14. Press **Modify options**, then press **Add VM options**
- ![image](https://user-images.githubusercontent.com/47162481/120003654-dce30b00-bfcd-11eb-80e6-4cffc5eb79f3.png)
15. In the VM options field, paste this in `--module-path "\path\to\javafx-sdk-16\lib" --add-modules javafx.controls,javafx.fxml`. Change the path inside it to the one used for your extracted JavaFX lib folder.
- For example you may want to use `--module-path "C:\Users\Administrator\Downloads\openjfx-16_windows-x64_bin-sdk\javafx-sdk-16\lib" --add-modules javafx.controls,javafx.fxml` if you extracted it in your downloads folder.
16. The final state of the window should look something like this:
- ![image](https://user-images.githubusercontent.com/47162481/160641927-c3f34e95-2fe9-4a0a-bb07-4e9e5efadc9f.png)
17. Press **OK** to finalise the configurations & exit the window.
18. Press the green hammer button on the top-right to build it.
- ![image](https://user-images.githubusercontent.com/47162481/160643525-8376d799-8766-45ac-b5c3-8b8dd9207f85.png)
19. Press the green play button on the top-right to run it.
- ![image](https://user-images.githubusercontent.com/47162481/160643877-812342e5-7ee3-427c-87dc-3a25c42dd53a.png)
20. It should now be running & showing this screen on top of the IDE.
- ![image](https://user-images.githubusercontent.com/47162481/160642158-4987faec-c2da-41d8-8831-e2425b64171b.png)
21. Repeat steps 5 to 19 for the client, where the client's Main.java is at `LiveAuction-main\client\src\java\com\github\ccyban\liveauction\client\Main.java` instead.
22. It should be running & showing this screen on top of the IDE.
- ![image](https://user-images.githubusercontent.com/47162481/160647226-06dc8cca-6ecf-4df6-9d96-f2c588355c22.png)
23. If you want to run more than one client at a time, turn this on in the Client project. Once done, you can repeat step 19 to run another instance if already running one.
- ![image](https://user-images.githubusercontent.com/47162481/160646912-3c4f12aa-fbe4-496d-a1c0-33fcf5a9253e.png)

- - - -

## Usage
When you have run the client, it will start with an server connecting screen. Once the server is running, you can exchange initiate a connection on the client, exchanging keys in the process. You can then proceed onto the login screen. A couple of accounts are mocked up by default as shown below. The 1 to 8 mocked accounts are added to assist with any concurrent server testing.

List of mocked accounts:
Username  | Password
--------- | --------
Harry | P0tt3r
Darth | Vad3r
1 | 1
2 | 2
3 | 3
4 | 4
5 | 5
6 | 6
7 | 7
8 | 8


### Client UI Pages

#### Exchange Keys
This page is used initiating a connection with the server. Once initiated through the button, keys will be exchanged so we move from socket asymmetric RSA encryption to symmetric AES encryption. Any communication after this is purely through AES encryption, making the socket's communications secure. You can then proceed onto the login screen.

#### Login Screen
This page is used for logging into an account, by authenticating with the server. Currently, there are only mocked accounts (as shown above) that you can use to login with. Invalid logins return an error alert, telling the user that their inputted details are invalid. Valid logins return a success alert, sending you to the Auction List page.

#### Auction List
This page is used for viewing the table list of auctions, showing live auction data. It allows filtering through them as 'All', 'Ongoing', 'Finished', 'Placed a bid' & 'Followed'. You can select an auction to open it to view more details

#### Auction Details
This page is used for viewing the live data of an auction. You can also bid on it with your signed in account. The timer counts down until it has finished, then says how long it has been since it finished. Once an auction is finished, it will no longer allow any bids (this no-allow check is done server-side as well to avoid any bypassing).

### Server UI Pages
#### Server Management
This page is used for opening and closing the server. This page contains a timestamped server log regarding all key server activities.

- - - -

## Tests
There are 3 Server backend-based JUnit test classes which contains 10 unit tests in total. 
### How to run the JUnit tests?
Firstly they are all in the Server project, so make sure you have that open in the IDE.
- To run all at once: Right click the green java test source and press **Run 'All Tests'**.
- ![image](https://user-images.githubusercontent.com/47162481/160652498-1a69ba61-9ae4-4242-84e2-5987e2f404ee.png)
- To run unit tests against a specific class: Right click the class you want unit tests ran against and press the **Run 'nameOfClass'**.
- ![image](https://user-images.githubusercontent.com/47162481/160653073-a4fd320a-a244-428e-9502-b843445a3298.png)
- All results from running unit tests should show up in the **Run** tab, letting you know if they passed or not.
- ![image](https://user-images.githubusercontent.com/47162481/160653238-e449a5f1-ab68-414b-95d4-971558f88aa4.png)
