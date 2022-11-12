import java.io.IOException;
import java.util.Scanner;

public class Dashboard {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        String password = "";
        String email = "";
        String name = "";
        boolean typeOfUser = false;
        boolean validLogin = false;
        int logInChoice = -1;

        User currentUser;

        while(logInChoice != 1 && logInChoice != 2) {
            System.out.println("1. Create Account\n2. Log in");
            logInChoice = scan.nextInt();
        }
        scan.nextLine();
        if(logInChoice == 1) {
            boolean newAccount = false;
            while(!newAccount) {
                System.out.println("Please enter your name");
                name = scan.nextLine();
                System.out.println("Please enter your email");
                email = scan.nextLine();
                System.out.println("Create a password");
                password = scan.nextLine();
                System.out.println("Are you a customer or seller (c or s)?");
                String buyerOrSeller;
                boolean isSeller;

                while (true) {
                    buyerOrSeller = scan.nextLine();
                    if (buyerOrSeller.equalsIgnoreCase("s")) {
                        isSeller = true;
                        break;
                    } else if (buyerOrSeller.equalsIgnoreCase("c")) {
                        isSeller = false;
                        break;
                    } else {
                        System.out.println("Please enter either c or s");
                    }
                }

                newAccount = AccountHandler.makeNewAcc(name, email, password, isSeller);

                try {
                    typeOfUser = AccountHandler.userType(email);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (logInChoice == 2) {
            do {
                try {
                    System.out.println("Please enter your email");
                    email = scan.nextLine();
                    System.out.println("Please enter your password");
                    password = scan.nextLine();
                    validLogin = AccountHandler.login(email, password);
                    if (!validLogin) {
                        System.out.println("Login failed. Incorrect email or password");
                    } else {
                        typeOfUser = AccountHandler.userType(email);
                        currentUser = AccountHandler.findUser(email);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (!validLogin);
        }

        while(true) {
            // Put everything else here
            if (typeOfUser) {
                //When user is Seller

            } else {
                //When user is Customer
            }
        }

    }


}
