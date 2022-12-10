# CS 180 | Project 4 & 5: Messaging 
##### By Joshipura, Akshara | Wang, Raymond | Tang, Kevin | Oh, Yejin

### 1. Running the Messenger
To run the messenger, follow these steps:

    0. Clone this GitHub Repository into a folder of your choice on your local device
    1. Open the folder as a Project in an IDE that supports Java (suggested, IntelliJ)
    2. Open and Run the src/MessengerServer.java file
    3. Open and Run as many instances of the src/MessengerClient.java file as you choose
    4. Enjoy!

### 2. Submissions on Brightspace/Vocareum:
> Akshara Joshipura - Submitted Vocareum Workspace
> Akshara Joshipura - Submitted Report on Brightspace
> Test cases (Tests.md) and README.md submitted in Vocareum Workspace

### 3.  The Classes
#### 3.0 `MessengerClient`
The `MessengerClient` class possesses the `public static void main(String[] args)` method that runs the messenger GUI.
Each action performed in the messenger GUI sends a corresponding request to the server `MessengerServer`, and the GUI is 
built using the response received from the server.

#### 3.1 `MessengerServer`
The `MessengerServer` class implements `Runnable` and runs in an infinite loop. This enables the server to run several 
threads, such that each thread of the server can connect to an instance of `MessengerClient` whenever a user connects to
the program. Each thread runs a while loop (that ends when the user logs out) that reads and parses a request from the 
client, identifies and executes the corresponding `AccountsMaster` method, and sends the appropriate response to the 
client.

#### 3.2 `AccountsMaster`
The `AccountsMaster` class acts as the helper class to `Main`. It handles the creation and deletion of accounts. It 
lists conversations, sellers, stores, and customers as per `this User`.

##### `public static void replaceStringInFile(String filePath, String oldString, String newString)`
Method that replaces an old Conversation or User string with its updated version in the file at `filePath`. This is 
used to keep all objects updated during `Main`'s execution.

#### 3.3 `Conversation`
The `Conversation` class acts as the template for any conversation created between a `Seller` and a `Customer`. It holds
elementary details about a conversation: conversationID, seller, and customer. It also holds a fileName for the 
destination file where all future `Message`s will be stored.

##### `public Conversation(String conversationString)`
Constructor that converts a `Conversation` object String into a `Conversation` object. This is used while reading 
`Conversations` from memory.

##### `public Conversation(String conversationID, String fileName, Seller seller, Customer customer)`
Constructor that creates a new `Conversation` object based on indirect input. This is used when creating a new 
`Conversation` before appending or writing to `conversations.txt`.

##### `public ArrayList<Message> readFileAsPerUser(User user)`
Method that reads the file according to the `User user`'s visibility of each `Message` (i.e. they cannot see the message
if they have deleted it).

##### `public ArrayList<Message> readFile()`
Method that reads the file and returns all the messages.

##### `public public boolean writeFile(ArrayList<Message> messages)`
Method that rewrites the `Conversation` file with the argument messages. Used during DELETING and EDITING messages.

##### `public void appendToFile(String stringMessage, User sender, User recipient)`
Method that creates a new `Message` object and appends that `Message` object String to the `Conversation` file with the 
messages.

##### `public boolean importTXT(String filePath, User sender, User recipient)`
Method that reads a .txt file to compile a singular message and sends it as a message to the `recipient`.

#### 3.4 `User`
The `User` class acts as the basic template for both `Seller`s and `Customer`s. It holds the elementary details about a 
user: username, email, and password. It also holds lists for blocked users, invisible to users, and censored words to 
implement BLOCKING and CENSORING.

##### `public User(String userString, boolean hasDetails)`
Constructor that instantiates all `User` fields to their values by parsing a `deepToString()` or `toString()` according 
to `hasDetails`.

##### `public User(String username, String email, String password)`
Constructor that creates a new `User`. Inherently called by `Seller` and `Customer` only.

##### `public boolean sendMessageToUser(String message, User user, AccountsMaster accountsMaster)`
Method to send a message to `User` ONLY IF `User` has not blocked `this User`. Appends a new `Message` object String to 
the `Conversation` file. Sets the recipient's `Conversation` status to UNREAD.

##### `public void editMessage(Message message, Conversation conversation, String newMessage)`
Method to edit a message in a `Conversation` ONLY IF `this User` is the sender of the `Message`.

##### `public void deleteMessage(Message message, Conversation conversation)`
Method to delete a message in a `Conversation`. Sets the `Message`'s corresponding `senderVisibility` or 
`recipientVisibility` to `false`.

#### 3.5 `Customer` extends `User`
The `Customer` class extends `User` to behave as a specific template for customers. It holds the same set of information
as `User`. `instanceOf Customer` is used to provide a `Customer` their customized permissions where required.

##### `public Customer(String customerString, boolean hasDetails)` 
Constructor that inherently calls `super(String userString, boolean hasDetails)` to instantiate `Customer` fields to 
their values. It parses the `deepToString()` or `super.toString()` version of the class written in memory according to 
`hasDetails`.

##### `public Customer(String name, String email, String password)`
Constructor that inherently calls `super(String name, String email, String password)` to create a new `Customer`.

##### `public String detailedToString()`
Method that returns a String containing: `username`, `email`, `password`, `requestsCensorship`, `blockedUsers`, 
`InvisibleUsers`, and `censoredWords`. This is used in writing into `passwords.txt` to store censoring toggle, blocked 
users, invisible to users, and censored word pairs for the next log-in.

#### 3.6 `Seller` extends `User`
The `Seller` class extends `User` to behave as a specific template for customers. It holds a list of `Store`s owned by 
the `Seller` in addition to the set of information in `User`. `instanceOf Seller` is used to provide a `Seller` their 
customized permissions where required.

##### `public Seller(String sellerString, boolean hasDetails, boolean hasStores)` 
Constructor that inherently calls `super(String userString, boolean hasDetails)` to instantiate `Seller` fields to their
values. It parses the `deepToString()` or `super.toString()` version of the class written in memory according to 
`hasDetails`. Additionally, if `hasStores`, it updates `this.stores` by calling the Store constructor 
`public Store(String storeString)`.

##### `public Seller(String name, String email, String password)`
Constructor that inherently calls `super(String name, String email, String password)` to create a new `Seller`.

##### `public String detailedToString()`
Method that returns a String containing: `username`, `email`, `password`, `requestsCensorship`, `blockedUsers`, 
`InvisibleUsers`, `censoredWords`, and `stores`. This is used in writing into a `Seller` object String in 
`passwords.txt` to store censoring toggle, blocked users, invisible to users, censored word pairs, and owned stores for 
the next log-in.

##### `public String detailedToStringWithoutStores()`
Method that returns a String containing ONLY: `username`, `email`, `password`, `requestsCensorship`, `blockedUsers`, 
`InvisibleUsers`, and `censoredWords`. This is used in writing into a `Store` object String in `passwords.txt` to 
prevent an infinite nested loop of `detailedToString()` calls.

#### 3.7 `Store`
The `Store` class behaves as a template for any stores that can be created, edited, or deleted by a `Seller`. It holds 
the elementary details about a store: store name, the owner (a `Seller` object).

##### `public Store(String storeString)`
Constructor that converts a `Store` object String into a `Store` object. This is used while reading `User`s and 
`Conversation`s from memory.

##### `public Store(String storeName, Seller seller)`
Constructor that creates a new `Store` object based on input. This is used while creating new `Store`s before adding to 
`Seller` strings in `passwords.txt` and `conversations.txt`.

#### 3.8 `Message`
The `Message` class behaves as a template for any message sent between `User`s. It holds elementary details about a 
message: time stamp, a `UUID` identifier, the message, the sender (a `User` object) and the recipient (a `User` object).
It  also possesses two attributes: `senderVisibility` and `recipientVisibility` to implement DELETING.  

##### `public Message(String messageString)`
Constructor that converts a `Message` object String into a `Message` object. This is used while reading `Message`s from 
memory.

##### `public Message(String message, User sender, User recipient)`
Constructor that creates a new `Message` object based on input. This is used when creating a new `Message`s before 
appending or writing to a conversation file.

##### `public String getCensoredMessage(User user)`
Method that returns the message censored according to the `user`'s settings. 
