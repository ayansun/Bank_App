import java.util.Scanner;
import java.util.ArrayList;
class BankApp {
    // Array of accounts (requirement: use arrays)
    static BankAccount[] accounts = new BankAccount[100];
    static int accountCount = 0; // number of created accounts

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("welcome to CS bank! Put Enter if you ready");
        String n = sc.nextLine();
        System.out.println("firstly let's create account");

        // MAIN AUTHORIZATION LOOP
        while (true) {
            System.out.println("==== Authorization Menu ====");
            System.out.println("1) Create new account");
            System.out.println("2) Login to existing account");
            System.out.println("3) Exit");
            System.out.print("Choose option (1-3): ");

            int authChoice = sc.nextInt();
            sc.nextLine();  // clear buffer

            if (authChoice == 1) {
                createAccount(sc); // create new BankAccount (OOP)
            } else if (authChoice == 2) {
                login(sc); // login to existing account
            } else if (authChoice == 3) {
                System.out.println("Goodbye!");
                break; // exit program
            } else {
                System.out.println("Wrong choice!");
            }
        }
    }
    // Creates a new bank account and saves it into array
    static void createAccount(Scanner sc) {
        if (accountCount >= accounts.length) {
            System.out.println("Bank is full, cannot create more accounts.");
            return;
        }
        System.out.print("Enter your name: ");
        String name = sc.nextLine();
        System.out.print("Enter your ID: ");
        String id = sc.nextLine();
        System.out.print("Create PIN (4 digits): ");
        String pin = sc.nextLine();

        // Choose type → demonstrates inheritance
        System.out.println("Choose account type:");
        System.out.println("1) Regular account");
        System.out.println("2) Student account");
        int type = sc.nextInt();
        sc.nextLine();

        BankAccount acc; // polymorphic reference
        if (type == 2) {
            acc = new StudentAccount(name, id, pin);  // child class
        } else {
            acc = new RegularAccount(name, id, pin);  // child class
        }

        accounts[accountCount] = acc; // store in array
        accountCount++;

        System.out.println("Account created! Your PIN is: " + pin);
        System.out.println("Remember it!\n");
    }

    // Find account by ID
    static BankAccount findAccountById(String id) {
        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getCustomerID().equals(id)) {
                return accounts[i];
            }
        }
        return null;
    }

    // Login and PIN check
    static void login(Scanner sc) {
        if (accountCount == 0) {
            System.out.println("There are no accounts yet. Please create it first.\n");
            return;
        }

        System.out.print("Enter your ID: ");
        String loginId = sc.nextLine();
        System.out.print("Enter your PIN: ");
        String loginPin = sc.nextLine();

        BankAccount acc = findAccountById(loginId); // search in array
        if (acc == null) {
            System.out.println("No account with this ID.\n");
            return;
        }

        if (acc.checkPin(loginPin)) {  // encapsulation: private pinCode
            System.out.println("Authorization successful!");
            acc.menu(); // polymorphism → menu depends on account type
        } else {
            System.out.println("Wrong PIN!\n");
        }
    }
}

class BankAccount {
    // Encapsulated fields (private → OOP requirement: encapsulation)
    private int balance;
    private int prevTrans;
    private int loanBalance = 0;
    private String pinCode;

    private String customerName;
    private String customerID;
    // Saves every transaction → data stored in ArrayList
    private ArrayList<String> history = new ArrayList<>();

    public BankAccount(String name, String id, String pin) {
        this.customerName = name;
        this.customerID = id;
        this.pinCode = pin;
    }

    // Getter for ID (cannot access private field directly)
    public String getCustomerID() {
        return customerID;
    }

    // PIN validation (encapsulation: we do not show pinCode)
    public boolean checkPin(String inputPin) {
        return pinCode.equals(inputPin);
    }

    // Method overridden in subclasses → example of polymorphism
    public String getAccountType() {
        return "Base";
    }

    // Deposit money
    void deposit(int amount) {
        if (amount > 0) {
            balance += amount;
            prevTrans = amount;
            history.add("DEPOSIT " + amount + " | balance=" + balance);
        } else {
            System.out.println("Amount must be positive.");
        }
    }

    // Withdraw money
    void withdraw(int amount) {
        if (amount > 0) {
            if (balance >= amount) {
                balance -= amount;
                prevTrans = -amount;
                history.add("WITHDRAW " + amount + " | balance=" + balance);
            } else {
                System.out.println("Not enough balance!");
            }
        } else {
            System.out.println("Amount must be positive.");
        }
    }

    // Buy cinema ticket (regular version)
    void buyTicket(int price) {
        if (balance >= price) {
            balance -= price;
            prevTrans = -price;
            history.add("TICKET " + price + " | balance=" + balance);
            System.out.println("Ticket purchased! Price: " + price);
        } else {
            System.out.println("Not enough balance!");
        }
    }

    // Basic loan logic
    void takeLoan(int amount) {
        if (amount > 0) {
            loanBalance += amount;
            balance += amount;
            history.add("LOAN +" + amount + " | loanBalance=" + loanBalance + " | balance=" + balance);
            System.out.println("Loan taken: " + amount);
        } else {
            System.out.println("Amount must be positive.");
        }
    }

    // Loan repayment
    void repayLoan(int amount) {
        if (loanBalance == 0) {
            System.out.println("You have no loan!");
            return;
        }
        if (amount > 0 && amount <= balance) {
            if (amount > loanBalance) {
                amount = loanBalance; // repay only remaining amount
            }
            loanBalance -= amount;
            balance -= amount;
            history.add("REPAY " + amount + " | loanBalance=" + loanBalance + " | balance=" + balance);
            System.out.println("Loan repaid: " + amount);
        } else {
            System.out.println("Not enough balance to repay or wrong amount!");
        }
    }

    // Shows only last transaction
    void getPrevTrans() {
        if (prevTrans > 0) {
            System.out.println("Last transaction: Deposited " + prevTrans);
        } else if (prevTrans < 0) {
            System.out.println("Last transaction: Withdrawn " + Math.abs(prevTrans));
        } else {
            System.out.println("No transactions yet.");
        }
    }

    // Shows full history stored in ArrayList
    void showHistory() {
        if (history.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        System.out.println("=== FULL TRANSACTION HISTORY ===");
        for (String h : history) {
            System.out.println(h);
        }
    }

    // Menu for one account (repeats until user exits)
    void menu() {
        Scanner sc = new Scanner(System.in);
        char option = '\0';

        System.out.println("Welcome to CS bank, " + customerName + "!");
        System.out.println("Your personal ID is " + customerID + " DO NOT TELL ANYONE!");
        System.out.println("Account type: " + getAccountType()); // polymorphic call
        System.out.println();
        // Menu options
        System.out.println("A) Check Balance");
        System.out.println("B) Deposit");
        System.out.println("C) Withdraw");
        System.out.println("D) Last transaction");
        System.out.println("K) Full history");
        System.out.println("E) Exit");
        System.out.println("F) Buy Ticket");
        System.out.println("G) Take Loan");
        System.out.println("H) Repay Loan");
        System.out.println("I) Show Loan Balance");
        System.out.println("J) Show PIN");

        do {
            System.out.println("============================");
            System.out.println("Enter your option :)");
            System.out.println("============================");

            option = sc.next().toUpperCase().charAt(0);

            switch (option) {

                case 'A':
                    System.out.println("Your balance -> " + balance);
                    break;

                case 'B':
                    System.out.println("Enter deposit amount:");
                    int dep = sc.nextInt();
                    deposit(dep);
                    break;

                case 'C':
                    System.out.println("Enter withdraw amount:");
                    int wd = sc.nextInt();
                    withdraw(wd);
                    break;

                case 'D':
                    getPrevTrans();
                    break;

                case 'K':
                    showHistory(); // print all history
                    break;

                case 'F':
                    // Ticket purchasing (demonstrates polymorphism when StudentAccount overrides method)
                    System.out.println("Movies for today ->");
                    System.out.println("1) Interstellar -> price: 1200₸");
                    System.out.println("2) The Matrix   -> price: 1700₸");
                    System.out.println("3) Inception    -> price: 2000₸");

                    int movieChoice = sc.nextInt();
                    int ticketPrice = 0;

                    if (movieChoice == 1) ticketPrice = 1200;
                    else if (movieChoice == 2) ticketPrice = 1700;
                    else if (movieChoice == 3) ticketPrice = 2000;
                    else {
                        System.out.println("Wrong choice! No such movie.");
                        break;
                    }

                    buyTicket(ticketPrice);
                    break;

                case 'G':
                    System.out.println("Enter loan amount:");
                    int loan = sc.nextInt();
                    takeLoan(loan);
                    break;

                case 'H':
                    System.out.println("Enter amount to repay:");
                    int repay = sc.nextInt();
                    repayLoan(repay);
                    break;

                case 'I':
                    System.out.println("Your loan balance: " + loanBalance);
                    break;

                case 'J':
                    System.out.println("Your PIN: " + pinCode);
                    break;

                case 'E':
                    System.out.println("Exiting account menu...");
                    break;

                default:
                    System.out.println("Invalid option!");
            }

        } while (option != 'E');
    }
}

// ===== Regular Account (child class) =====
// Inherits everything from BankAccount
class RegularAccount extends BankAccount {

    public RegularAccount(String name, String id, String pin) {
        super(name, id, pin);
    }

    // Polymorphism: overrides base method
    public String getAccountType() {
        return "Regular";
    }
}

// ===== Student Account (child class) =====
class StudentAccount extends BankAccount {

    public StudentAccount(String name, String id, String pin) {
        super(name, id, pin);
    }
    public String getAccountType() {
        return "Student";
    }
    // Polymorphism: different ticket logic (20% discount)
    void buyTicket(int price) {
        int discounted = (int)(price * 0.8);
        System.out.println("Student discount 20%! Original price: " + price + ", you pay: " + discounted);
        super.buyTicket(discounted);
    }

    // Students cannot take loans above 50000
    void takeLoan(int amount) {
        if (amount > 50000) {
            System.out.println("Student account: max loan is 50000₸ at once.");
            return;
        }
        super.takeLoan(amount);
    }
}