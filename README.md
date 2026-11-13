# NewBank

A small bank client and server written with plain Java sockets. The server listens on port 14002, gives each connection its own thread, asks for a username and password, then reads commands one line at a time.

The protocol in [protocol.txt](protocol.txt) lists four commands. Only `SHOWMYACCOUNTS` is implemented so far.

## Contents

- [Layout](#layout)
- [Running it](#running-it)
- [Protocol](#protocol)
- [Architecture](#architecture)
- [Classes](#classes)
- [A session, step by step](#a-session-step-by-step)
- [States](#states)
- [Request handling](#request-handling)
- [Threads](#threads)

## Layout

```text
bath_new_bank/
├── protocol.txt
└── newbank/
    ├── client/
    │   └── ExampleClient.java
    └── server/
        ├── NewBankServer.java
        ├── NewBankClientHandler.java
        ├── NewBank.java
        ├── Customer.java
        ├── Account.java
        └── CustomerID.java
```

| Class | What it does |
|---|---|
| `NewBankServer` | Opens `ServerSocket(14002)` and loops on `accept()`, starting a `NewBankClientHandler` for each connection. |
| `NewBankClientHandler` | Runs the login prompts, then reads requests in a loop and writes back the replies. |
| `NewBank` | Singleton holding all customers in a `HashMap<String, Customer>`. Handles login and command dispatch; both methods are `synchronized`. |
| `Customer` | A list of `Account`s. |
| `Account` | An account name and a `double` balance (the field is called `openingBalance`). |
| `CustomerID` | Wraps the username. Handed out on login and passed with every request. |
| `ExampleClient` | Console client. The main thread sends what you type; a second thread prints whatever the server sends back. |

### Test data

`NewBank.addTestData()` creates three customers at startup. Nothing is saved, so every restart starts from the same data.

| Username | Account | Balance |
|---|---|---|
| `Bhagy` | Main | 1000.0 |
| `Christina` | Savings | 1500.0 |
| `John` | Checking | 250.0 |

## Running it

You need JDK 8 or later. There are no dependencies and no build tool.

```bash
javac -d out newbank/server/*.java newbank/client/*.java

# terminal 1
java -cp out newbank.server.NewBankServer

# terminal 2
java -cp out newbank.client.ExampleClient
```

A session looks like this (lines starting with `>` are typed by you):

```text
Enter Username
> Bhagy
Enter Password
> whatever
Checking Details...
Log In Successful. What do you want to do?
> SHOWMYACCOUNTS
Main: 1000.0
> NEWACCOUNT Savings
FAIL
```

The password isn't checked yet, so anything works. `nc localhost 14002` works just as well as the example client.

## Protocol

Everything is plain text over TCP, one message per line. After the username and password lines, each command you send gets exactly one line back. Commands must match exactly, including case, so `showmyaccounts` gets `FAIL`.

| Command | Example | Reply | Implemented |
|---|---|---|---|
| `SHOWMYACCOUNTS` | `SHOWMYACCOUNTS` | Accounts and balances, e.g. `Main: 1000.0` | Yes |
| `NEWACCOUNT <Name>` | `NEWACCOUNT Savings` | `SUCCESS` or `FAIL` | No, always `FAIL` |
| `MOVE <Amount> <From> <To>` | `MOVE 100 Main Savings` | `SUCCESS` or `FAIL` | No, always `FAIL` |
| `PAY <Person/Company> <Amount>` | `PAY John 100` | `SUCCESS` or `FAIL` | No, always `FAIL` |

## Architecture

```mermaid
flowchart LR
    subgraph Client["ExampleClient"]
        STDIN[/"System.in"/]
        MAIN["main thread<br/>stdin to socket"]
        RECV["reader thread<br/>socket to stdout"]
        STDOUT[/"System.out"/]
        STDIN --> MAIN
        RECV --> STDOUT
    end

    subgraph Server["NewBankServer on port 14002"]
        ACCEPT["accept() loop"]
        H1["ClientHandler 1"]
        H2["ClientHandler 2"]
        HN["ClientHandler N"]
        subgraph Core["NewBank (singleton)"]
            AUTH["checkLogInDetails()"]
            PROC["processRequest()"]
            DATA[("customers<br/>HashMap in memory")]
            AUTH --> DATA
            PROC --> DATA
        end
        ACCEPT -- "one per connection" --> H1 & H2 & HN
        H1 & H2 & HN --> AUTH
        H1 & H2 & HN --> PROC
    end

    MAIN -- "text lines" --> H1
    H1 -- "text lines" --> RECV
```

Seen as layers, each class only talks to the one below it:

```mermaid
flowchart TB
    L1["NewBankServer<br/>accepts connections"]
    L2["NewBankClientHandler<br/>login and request loop"]
    L3["NewBank<br/>authentication and commands"]
    L4["Customer, Account, CustomerID"]
    L1 --> L2 --> L3 --> L4
```

## Classes

```mermaid
classDiagram
    direction LR

    class NewBankServer {
        -ServerSocket server
        +NewBankServer(int port)
        +run()
        +main(String[] args)$
    }
    class NewBankClientHandler {
        -NewBank bank
        -BufferedReader in
        -PrintWriter out
        +NewBankClientHandler(Socket s)
        +run()
    }
    class NewBank {
        -NewBank bank$
        -HashMap~String,Customer~ customers
        -NewBank()
        -addTestData()
        +getBank()$ NewBank
        +checkLogInDetails(String userName, String password) CustomerID
        +processRequest(CustomerID customer, String request) String
        -showMyAccounts(CustomerID customer) String
    }
    class Customer {
        -ArrayList~Account~ accounts
        +accountsToString() String
        +addAccount(Account account)
    }
    class Account {
        -String accountName
        -double openingBalance
        +toString() String
    }
    class CustomerID {
        -String key
        +getKey() String
    }
    class ExampleClient {
        -Socket server
        -PrintWriter bankServerOut
        -BufferedReader userInput
        -Thread bankServerResponceThread
        +run()
        +main(String[] args)$
    }

    Thread <|-- NewBankServer
    Thread <|-- NewBankClientHandler
    Thread <|-- ExampleClient
    NewBankServer ..> NewBankClientHandler : creates per connection
    NewBankClientHandler --> NewBank : uses singleton
    NewBankClientHandler ..> CustomerID : holds after login
    NewBank "1" o-- "*" Customer : customers (key = userName)
    Customer "1" o-- "*" Account : accounts
    NewBank ..> CustomerID : issues
    ExampleClient ..> NewBankServer : TCP port 14002
```

## A session, step by step

```mermaid
sequenceDiagram
    autonumber
    actor U as User
    participant C as ExampleClient
    participant S as NewBankServer
    participant H as NewBankClientHandler
    participant B as NewBank

    C->>S: connect to localhost:14002
    S->>H: start handler thread
    H-->>C: "Enter Username"
    U->>C: Bhagy
    C->>H: "Bhagy"
    H-->>C: "Enter Password"
    U->>C: ****
    C->>H: "****"
    H-->>C: "Checking Details..."
    H->>B: checkLogInDetails(user, pwd)
    alt username exists
        B-->>H: CustomerID("Bhagy")
        H-->>C: "Log In Successful. What do you want to do?"
        loop every line the user sends
            U->>C: SHOWMYACCOUNTS
            C->>H: "SHOWMYACCOUNTS"
            H->>B: processRequest(id, request)
            B-->>H: "Main: 1000.0"
            H-->>C: "Main: 1000.0"
        end
    else unknown username
        B-->>H: null
        H-->>C: "Log In Failed"
        H->>H: close streams and exit
    end
```

## States

### A client session on the server

```mermaid
stateDiagram-v2
    [*] --> Connected : accept()
    Connected --> AwaitUsername : send "Enter Username"
    AwaitUsername --> AwaitPassword : got username
    AwaitPassword --> Authenticating : got password
    Authenticating --> LoggedIn : CustomerID returned
    Authenticating --> Rejected : null returned
    Rejected --> Closed : send "Log In Failed"

    state LoggedIn {
        [*] --> AwaitCommand
        AwaitCommand --> Processing : readLine()
        Processing --> AwaitCommand : reply sent
    }

    LoggedIn --> Closed : IOException or disconnect
    Closed --> [*]

    note right of LoggedIn
        On disconnect, readLine() returns null.
        switch(null) then throws a
        NullPointerException, so today the
        thread crashes instead of closing cleanly.
    end note
```

### The server process

```mermaid
stateDiagram-v2
    [*] --> Bound : new ServerSocket(14002)
    Bound --> Listening : start()
    Listening --> Dispatching : accept() returns
    Dispatching --> Listening : handler thread started
    Listening --> Stopped : IOException
    Stopped --> [*] : server.close()
```

## Request handling

### Current code

```mermaid
flowchart TD
    A([client connects]) --> B[accept]
    B --> C[start NewBankClientHandler]
    C --> D[ask for and read username]
    D --> E[ask for and read password]
    E --> F{"username known?<br/>(password ignored)"}
    F -- no --> G[send Log In Failed] --> Z([close streams])
    F -- yes --> H[send Log In Successful]
    H --> I[/read a line/]
    I --> J{customer exists?}
    J -- no --> K[FAIL]
    J -- yes --> L{which command?}
    L -- SHOWMYACCOUNTS --> M[list accounts]
    L -- anything else --> K
    M --> N[/send reply/]
    K --> N
    N --> I
```

### Once all four commands exist

One way to handle the rest of the protocol:

```mermaid
flowchart TD
    R[/request line/] --> P[split on whitespace]
    P --> Q{first word}
    Q -- SHOWMYACCOUNTS --> S1[list accounts]
    Q -- NEWACCOUNT --> V1{one name given<br/>and not already used?}
    V1 -- yes --> S2[add account with balance 0] --> OK([SUCCESS])
    V1 -- no --> FAIL([FAIL])
    Q -- MOVE --> V2{"amount > 0, both accounts exist,<br/>enough money in From?"}
    V2 -- yes --> S3[take from From, add to To] --> OK
    V2 -- no --> FAIL
    Q -- PAY --> V3{"payee exists, amount > 0,<br/>enough money?"}
    V3 -- yes --> S4[take from payer, add to payee] --> OK
    V3 -- no --> FAIL
    Q -- anything else --> FAIL
```

## Threads

```mermaid
flowchart LR
    subgraph JVM["server JVM"]
        M["main<br/>returns after start()"] --> SV["NewBankServer<br/>accept loop"]
        SV --> T1["handler 1"]
        SV --> T2["handler 2"]
        SV --> TN["handler N"]
        T1 & T2 & TN -- "synchronized" --> NB["NewBank"]
    end
```

Each connection gets a new thread. There's no pool and no cap on connections.

Both public methods on `NewBank` are `synchronized` on the same instance, so requests from different clients run one at a time. That keeps the data safe without any other locking, at the cost of throughput.

`NewBank` is created when the class loads (`private static final`), so there's no race in creating the singleton.
