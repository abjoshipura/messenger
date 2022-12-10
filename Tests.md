# Test Cases
##### By Oh, Yejin

### Test 1: User Signs Up for New Account
**Steps**:
1. User launches application.
2. User selects the "Sign Up" button.
3. User is directed to page with form for account details.
4. User selects the username text field.
5. User enters preferred username via the keyboard.
6. User repeats Steps 4-5 for email and password.
7. User chooses role by selecting the "Seller"/"Customer" button.
8. User selects the "Create" button.

**Expected Result**: Application verifies that all text fields are filled out and that username and/or email are not already in use. Upon account creation, main menu loads automatically for user.

**Test Status**: Passed.

### Test 2: User Logs In
**Steps**:
1. User launches application.
2. User selects the "Log In" button.
3. User is directed to page with form for login details.
4. User selects the username/email text field.
5. User enters username/email via the keyboard.
6. User repeats Steps 3-4 for password.
7. User selects the "Log In" button.

**Expected Result**: Application verifies that all text fields are filled out and that username/email and password combination matches existing user credentials. (1) If password is incorrect, "Incorrect Password" message pops up and user may try again. (2) If password is correct, application welcomes user back and alerts them of any unread conversations, loading main menu automatically. (3) If username/email is not yet registered, "Unregistered Account" message pops up and user may go back to sign up.

**Test Status**: Passed.

### Test 3: User Logs Out
**Steps**:
1. After Test 1 or 2, user selects the "Log Out" button.

**Expected Result**: Application displays a goodbye message.

**Test Status**: Passed.

### Test 4: User Searches for Another User
**Steps**:
1. After Test 1 or 2, user selects the "Search for a Customer" (if seller) or "Search for a Seller" (if customer) button.
2. In the resulting pop-up window, user selects the search keyword text field.
3. User enters search keyword via the keyboard.
4. User selects the "Search" button.

**Expected Result**: Application verifies that search keyword is not blank. (1) If search yields no results, a "No Customers" (if seller) or "No Sellers" (if customer) message is displayed. (2) If search yields results, a list of visible customers (if seller) or sellers (if customer) with matching usernames and/or emails is displayed.

**Test Status**: Passed.

### Test 5-1: User (Seller) Views List of Customers
**Steps**:
1. After Test 1 or 2, user (if seller) selects the "View All Customers" button.

**Expected Result**: List of all visible customers is displayed. If there are none, "No Customers" message is displayed.

**Test Status**: Passed.

### Test 5-2: User (Customer) Views List of Stores
**Steps**:
1. After Test 1 or 2, user (if customer) selects the "View All Stores" button.

**Expected Result**: List of all stores is displayed. If there are none, "No Stores" message is displayed.

**Test Status**: Passed.

### Test 6-1: User (Seller) Blocks Another User (Customer)
**Steps**:
1. After Test 4 or 5-1, user selects the name of the customer to block.
2. User selects the "Block" button.

**Expected Result**: Upon selection of the same customer, the button is labeled "Unblock." Neither side can message the other.

**Test Status**: Passed.

### Test 6-2: User (Customer) Blocks Another User (Seller)
**Steps**:
1. After Test 4 or 5-2, user selects the name of the seller or store who(se owner) will be blocked.
2. User selects the "Block" button.

**Expected Result**: Upon selection of the same seller, the button is labeled "Unblock." Neither side can message the other.

**Test Status**: Passed.

### Test 7: User Unblocks Previously Blocked User
**Steps (Option A)**:
1. After Test 6-1 or 6-2, user selects the name of the previously blocked other user.
2. User selects the "Unblock" button.

**Steps (Option B)**:
1. After Test 6-1 or 6-2, user selects the "Back" button.
2. User selects the "Edit Account" button.
3. User selects the "Manage Blocked Users" button.
4. User selects the name of the user to unblock.
5. User selects the "Unblock" button.

**Expected Result**: Upon selection of the same user, the button is labeled "Unblock." As long as the other user does not have the current user blocked, both sides are able to message each other again.

**Test Status**: Passed.

### Test 8: User Becomes Invisible to Another User
**Steps**:
1. After Test 4, 5-1, or 5-2, user selects the name of the user to become invisible to.
2. User selects the "Become Invisible" button.

**Expected Result**: Upon selection of the same user, the button is labeled "Become Visible." The other user cannot search for the current user, see them in a list, see their stores (if applicable), or see an existing conversation with them. The current user is able to message the other user, given that neither side is blocked.

**Test Status**: Passed.

### Test 9: User Becomes Visible to Another User
**Steps (Option A)**:
1. After Test 8, user selects the name of the other user they are invisible to.
2. User selects the "Become Visible" button.

**Steps (Option B)**:
1. After Test 8, user selects the "Back" button.
2. User selects the "Edit Account" button.
3. User selects the "Manage Invisible to Users" button. 
4. User selects the name of the user to become visible to.
5. User selects the "Become Visible" button.

**Expected Result**: Upon selection of the same user, the button is labeled "Become Invisible." The other user can now search for the current user, see them in a list, see their stores (if applicable), or see an existing conversation with them. The other user is also able to view messages sent by the current user while invisible.

**Test Status**: Passed.

### Test 10: User Attempts Username Change
**Steps**:
1. After Test 1 or 2, user selects the "Edit Account" button.
2. User selects the "Change Username" button.
3. User selects the new username text field.
4. User enters preferred username via the keyboard.
5. User selects the "Change" button.

**Expected Result**: Application verifies that new username is not blank. If new username is already taken, "Username already Taken" message pops up. If new username is not already taken, username is changed successfully.

**Test Status**: Passed.

### Test 11: User Attempts Password Change
**Steps**:
1. After Test 1 or 2, user selects the "Edit Account" button.
2. User selects the "Change Password" button.
3. User selects the current password text field.
4. User enters current password via the keyboard.
5. User repeats Steps 3-4 for new password and confirm new password.
6. User selects the "Change" button.

**Expected Result**: Application verifies that none of the text fields are blank. If new password does not match confirmation, "New Passwords Did Not Match" message pops up. If new password and confirmation match, password is changed successfully.

**Test Status**: Passed.

### Test 12: User (Seller) Creates New Store
**Steps**:
1. After Test 1 or 2, user (if seller) selects the "Manage Stores" button.
2. User selects the "Add" button.
3. User selects the new store name text field.
4. User enters name of new store via the keyboard.
5. User selects the "Add" button.

**Expected Result**: Application verifies that new store name text field is not blank. Store is successfully created and can be seen by the user and all customers they are visible to.

**Test Status**: Passed.

### Test 13: User (Seller) Deletes Existing Store
**Steps**:
1. After Test 12, user selects the "Manage Stores" button.
2. User selects the name of their store to delete.
3. User selects the "Delete" button.

**Expected Result**: Store is successfully deleted and can no longer be seen by the user or any customers. Any records of customers visiting the now deleted store remain in the conversation.

**Test Status**: Passed.

### Test 14: User Deletes Account
**Steps**:
1. After Test 1 or 2, user selects the "Edit Account" button.
2. User selects the "Delete Account" button.
3. When the confirmation window pops up, user selects the "Delete" button.

**Expected Result**: Application displays a goodbye message. The deleted user remains in any blocked or invisible to states that were present prior to account deletion. Other participants are able to continue sending messages to the deleted user, but the deleted user can no longer be discovered and no new conversations can be created with them. If the deleted user creates an account with the same email and role they had previously, they are able to access and participate in conversations from pre-deletion, and are blocked by and cannot view the same users (if not deleted from the list of blocked and invisible to users).

**Test Status**: Passed.

### Test 15: User Toggles Censorship On/Off
**Steps**:
1. After Test 1 or 2, user selects the "Edit Account" button.
2. User selects the "Edit Censoring" button.
3. User selects the "On / Off" button.

**Expected Result**: (1) The "Censoring Switched ON!" message pops up if censoring was previously toggled off. If there are any censor pairs set, selected words are censored in the user's conversation view by their chosen replacements. (2) The "Censoring Switched OFF!" message pops up if censoring was previously toggled on. If there were any censor pairs set, the user's conversation view now shows the uncensored messages.

**Test Status**: Passed.

### Test 16: User Creates New Censor Pair
**Steps**:
1. After Test 1 or 2, user selects "Edit Account" button.
2. User selects "Edit Censoring" button.
3. User selects "Add" button.
4. User selects the censored word text field.
5. User enters word to censor via the keyboard.
6. User selects the replacement text field.
7. User may alter replacement for the censored word from default "****" via the keyboard.
8. User selects the "Add" button.

**Expected Result**: Censored word and replacement appears in list of censored pairs for user. If censoring is toggled on, the censored word is replaced for all messages visible to the current user, regardless of their sender.

**Test Status**: Passed.

### Test 17: User Deletes Existing Censor Pair
**Steps**:
1. After Test 16, user selects censor pair to delete.
2. User selects the "Delete Censor Pair" button.

**Expected Result**: The censor pair is successfully removed from the list of censor pairs. If censoring is toggled on, the word replaced by the deleted censor pair is no longer censored in the user's conversations.

**Test Status**: Passed.

### Test 18: User Views List of Existing Conversations
**Steps**:
1. After Test 1 or 2, the user selects the "View Conversations" button.

**Expected Result**: A list of all conversations the user is involved in is displayed, unless the other participant is invisible to the current user.

**Test Status**: Passed.

### Test 19: User Attempts to Send Message to Another User
**Steps (Option A)**:
1. After Test 4, 5-1, or 5-2, user selects the other user to send a message to.
2. User selects the "Message" button.
3. User selects the "Your Message:" text field.
4. User enters message via the keyboard.
5. User selects the "Send" button.

**Steps (Option B)**:
1. After Test 18, user selects existing conversation with the other user to send a message to.
2. User selects text field between "Import a TXT" and "Send" buttons.
3. User enters message via the keyboard.
4. User selects the "Send" button.

**Expected Result**: Application verifies that message is not blank. (1) For Option A, a "Sent!" message pops up to indicate that message was successfully sent. If one of the users is blocked, "You cannot message this user!" message pops up after Step 2. (2) For Option B, a successful message immediately appears under previous messages in the selected conversation. If one of the users is blocked, "You cannot message this user!" message pops up after Step 4.

**Test Status**: Passed.

### Test 20: User Attempts to Edit Message in Selected Conversation
**Steps**:
1. After Test 18, user selects existing conversation to edit message in.
2. User selects message to edit.
3. User selects "Edit Message" button.
4. User selects the "Edited Message:" text field.
5. User enters edited version of message via the keyboard.
6. User selects the "Enter & Send" button.

**Expected Result**: If the message was not sent by the user, a "You cannot edit received messages!" message pops up after Step 3. If the message was sent by the user and neither participant is blocked, the message appears with updated contents to both participants. Otherwise, a "You cannot message this user!" message pops up.

**Test Status**: Passed.

### Test 21: User Attempts to Delete Message in Selected Conversation
**Steps**:
1. After Test 18, user selects existing conversation to delete message in.
2. User selects message to delete.
3. User selects the "Delete Message" button.

**Expected Result**: Regardless of which participant originally sent the message and whether at least one of the participants is blocked, the deleted message disappears only from the current user's view. The other participant is still able to view the message.

**Test Status**: Passed.

### Test 22: User Imports .txt File as Message to Selected Conversation
**Steps**:
1. After Test 18, user selects existing conversation to send message from a .txt file to.
2. User selects the "Export a TXT" button.
3. User selects the ".txt File Source" text field.
4. User enters source of .txt file via the keyboard.
5. User selects the "Send" button.

**Expected Result**: After Step 2, if one or both of the participants have the other user blocked, a "You cannot message this user!" message pops up. If the path for the .txt file source is incorrect, an "Import failed. Check the entered path." message pops up. Otherwise, the contents of the .txt file will be sent as a single message.

**Test Status**: Passed.

### Test 23: User Exports Selected Conversation as .csv File
**Steps**:
1. After Test 18, user selects existing conversation to export to a CSV file.
2. User selects the "Export to CSV" button.

**Expected Result**: The "Exported to src/exports!" message pops up. The .csv file for the selected conversation is created in the "exports" folder, if it doesn't already exist. If a .csv file for the conversation file was previously exported, Step 2 alters the file contents to reflect any new messages, as well as messages edited or deleted by the current exporting user. Censorship options for the user are not implemented in the .csv file as they are in regular conversation view.

**Test Status**: Passed.

### Test 24: Multithreading and Synchronization?
**Steps**:

**Expected Result**:

**Test Status**: