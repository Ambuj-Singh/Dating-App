# Dating-App

## End-to-end encryption chat simulation final year project.

## System Design

In this project I used ***Firebase*** as the server and for frontend, I used android xml. 

* The app is designed to accomodate almost all screen sizes in mobile and tablet devices.</br><img src="https://user-images.githubusercontent.com/39789077/229717013-c8b6befe-07ed-4ced-9ca8-efb1417b4544.jpg" width="150"> <img src="https://user-images.githubusercontent.com/39789077/232704562-55806b06-93cb-4cb3-b3ac-fbd65dfa6ab7.jpg" width="200"></br>
* It has a persistent server which means even if you lose network, whenever the internet connection is live again it will receive and send message automatically.
* Chats are stored on the local database using ***Android*** and ***SQLite***.
```mermaid
sequenceDiagram
    Alice->>+John: Hello John, how are you?
    Alice->>+John: John, can you hear me?
    John-->>-Alice: Hi Alice, I can hear you!
    John-->>-Alice: I feel great!
```
* Server only holds the message as long as the receiver is offline.</br>
<img src="https://user-images.githubusercontent.com/39789077/229835474-52d17e37-864d-4ae1-8852-3c2fd9028631.png" ></br>
* Messages sent to server are encrypted on the client device first and then decoded on the receiver device. 
```mermaid
flowchart TD;
    A[Sender App] -->|Message| B(Encoder);
    B --> |Encoded Message| C{Server};
    C -->|Encoded Message| D[Receiver App];
    D --> |Encoded Message| E(Decoder);
    E --> |Decoded Message| F(Receiver);
```

## System GUI

The app is easy-to-use and user-friendly. Everything is labeled and easy to access.

>The app is under development.
* **Log in** and **Sign up**.</br>
<img src="https://user-images.githubusercontent.com/39789077/229834227-4926ab93-d8cb-482b-a354-b4c96f68f7f3.jpg" width="150"> <img src="https://user-images.githubusercontent.com/39789077/229834241-dce44c14-4cd9-4043-a100-1a19452489a3.jpg" width="150"></br>
* **Profiles** Screen comes first after loading screen once you have logged in.</br><img src="https://user-images.githubusercontent.com/39789077/229717013-c8b6befe-07ed-4ced-9ca8-efb1417b4544.jpg" width="150"> <img src="https://user-images.githubusercontent.com/39789077/229485204-f265ef19-c93a-43bd-8cb0-25b8a0e85003.jpg" width="150"></br>
* You can click on the user profile card to interact with the user.</br>
* Heart icon represents **Favourite selection**, you can mark any user as Favourite and it will show in your Favourite tab which you can access from your dashboard.</br>
<img src="https://user-images.githubusercontent.com/39789077/229490481-fe10cc56-1c29-4c87-93e7-b0b688e48cfb.jpg" width="150"> <img src="https://user-images.githubusercontent.com/39789077/229490914-20ec0819-5b3a-4f16-af0b-f40303b4e8c4.jpg" width="150"> 
<img src="https://user-images.githubusercontent.com/39789077/229491493-93e00766-303f-4855-ac93-c775ed1109ff.jpg" width="150"></br>
* Chat Dialog icon will let you chat to the other user and also that user will be shown in your chat tab which can be accessed from the dasboard.</br>
<img src="https://user-images.githubusercontent.com/39789077/229490481-fe10cc56-1c29-4c87-93e7-b0b688e48cfb.jpg" width="150"></br>
* These are **Account Options** here you can edit your profile, logout and upload photos to the server.</br>
<img src="https://user-images.githubusercontent.com/39789077/229500353-6528028b-ba95-4c75-9d7c-61260d03c61b.jpg" width="150"> <img src="https://user-images.githubusercontent.com/39789077/229499189-1ca0d49b-af3e-42ef-8505-02652e969c2a.jpg" width="150"></br>
* By clicking on **Edit Profile** option you can edit your profile.</br><img src="https://user-images.githubusercontent.com/39789077/229501549-bddfd672-c1ca-4619-86b1-6750a3f77f77.jpg" width="150"></br>
* For Example we will first turn the swith on and then click on the field you want to edit.</br><img src="https://user-images.githubusercontent.com/39789077/229698961-80b287e3-de98-432d-89e7-590df82d1e2f.jpg" width="150"> <img src="https://user-images.githubusercontent.com/39789077/229698602-42781ada-3462-4242-86a7-06775dcaa2d2.jpg" width="150"> <img src="https://user-images.githubusercontent.com/39789077/229699136-717121a8-4b73-4df6-8be8-d2decafeb4d2.jpg" width="150"></br>
* Another example where we will edit the **Profession field**.</br><img src="https://user-images.githubusercontent.com/39789077/229699676-2ec79726-feab-4a22-ab23-a2cea15061ab.jpg" width="150"> <img src="https://user-images.githubusercontent.com/39789077/229699803-49ebd212-983f-4180-9890-0812691080cb.jpg" width="150"> <img src="https://user-images.githubusercontent.com/39789077/229699818-37a6faa4-7f3e-491b-ac36-d81d8cad1279.jpg" width="150"></br>
* **Chat List** and **Conversation UI**.</br><img src="https://user-images.githubusercontent.com/39789077/232705771-44e9d0a7-7979-4f08-9151-7ed198ac2b43.jpg" width="150"> <img src="https://user-images.githubusercontent.com/39789077/232705795-614a38de-aeb5-403a-bbac-b2639ca7eba1.jpg" width="150"> <img src="https://user-images.githubusercontent.com/39789077/232705806-18d83a8a-cca3-48de-b982-04394f6de188.jpg" width="150"> 
* Tablet user.</br><img src="https://user-images.githubusercontent.com/39789077/232715588-5a19dc46-2b0e-4074-9a09-49db49fce86e.jpg" width="200"></br>

* **Forgot Password?** and exit screen UI Flow.</br><img src="https://user-images.githubusercontent.com/39789077/232711872-aedc6e4f-e49c-407c-b7f9-36b304e043f7.jpg" width="150"> <img src="https://user-images.githubusercontent.com/39789077/232711885-47486291-3c31-484c-ae68-afa2657b0bc1.jpg" width="150"> <img src="https://user-images.githubusercontent.com/39789077/232711828-d29c0c08-07fc-4567-960b-84ef2299a9b0.jpg" width="150"> <img src="https://user-images.githubusercontent.com/39789077/232711812-57a9d6d6-44db-43b2-bf67-93ba5d365289.jpg" width="150"> <img src="https://user-images.githubusercontent.com/39789077/232711976-e332ce90-19df-4034-9b10-03fe58dcc31a.jpg" width="150"> <img src="https://user-images.githubusercontent.com/39789077/232711989-75971538-6c45-421f-a285-1b44104d62af.jpg" width="150"> <img src="https://user-images.githubusercontent.com/39789077/232714182-748533f4-36a9-4300-8240-5c9ba506729f.jpg" width="150"> 


>Some features are under development.
> 📢 **PSA for those who want to fork or copy this repo and use it for their own site:**
>
> Please be a decent person and give me proper credit by linking back to my website! Refer to this handy [quora post](https://www.quora.com/Is-it-bad-to-copy-other-peoples-code) if you're not sure.

### Contact me for any details regarding the app and code features.
### Email - sambuj.github@gmail.com
### Overall the app was designed to only simulate a part of end-to-end encryption using AES-128 encryption with ECB Packaging.
