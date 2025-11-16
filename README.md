# Payment Transfer System

A Spring Boot application that facilitates financial transactions between accounts with an HTTP interface. The system maintains transaction logs and account states in a PostgreSQL database.

## Features

- Create accounts with initial balances
- Query account balances
- Process transactions between accounts
- Retrieve all transaction history
- Maintain transaction logs for audit purposes
- Ensure data integrity with ACID transactions
- Handle concurrent transactions with pessimistic locking

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher
- Git (for cloning the repository)

## Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd payment-transfer-system
```

2. Build the project:
```bash
mvn clean install
```

## Database Setup

### PostgreSQL

1. Ensure PostgreSQL is installed and running on your system.

2. Create the database:
```bash
psql -U postgres
CREATE DATABASE payment_transfer_db;
\q
```

3. Update database configuration in `src/main/resources/application.properties` if needed:
   - Default database name: `payment_transfer_db`
   - Default username: `postgres`
   - Default password: `postgres`
   - Default host: `localhost`
   - Default port: `5432`

## Running the Application

### With PostgreSQL:
```bash
# Make sure PostgreSQL is running first
mvn spring-boot:run
```

Alternatively, you can run the JAR file:
```bash
java -jar target/payment-transfer-system-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080` by default.

### Troubleshooting PostgreSQL Connection

If you get a connection error like "Connection to localhost:5432 refused":

1. **Check if PostgreSQL is running:**
   ```bash
   # On macOS
   brew services list | grep postgresql
   # Or check with:
   pg_isready
   ```

2. **Start PostgreSQL:**
   ```bash
   # On macOS with Homebrew
   brew services start postgresql@14
   # Or
   brew services start postgresql
   
   # On Linux
   sudo systemctl start postgresql
   
   # On Windows
   # Start PostgreSQL service from Services panel
   ```

3. **Verify connection settings** in `application.properties` match your PostgreSQL setup.

## API Endpoints

### 1. Create Account

**POST** `/accounts`

Creates a new account with the specified account ID and initial balance.

**Request Body:**
```json
{
  "account_id": 123,
  "initial_balance": "100.23344"
}
```

**Success Response:**
- Status: `201 Created`
- Body: Empty (no response body)

**Error Responses:**
- `400 Bad Request` - Validation error
- `409 Conflict` - Account already exists
- `500 Internal Server Error` - Server error

**Example:**
```bash
curl -X POST http://localhost:8080/accounts \
  -H "Content-Type: application/json" \
  -d '{"account_id": 123, "initial_balance": "100.23344"}'
```

### 2. Get Account Balance

**GET** `/accounts/{accountId}`

Retrieves the account information including balance for the specified account ID.

**Path Parameter:**
- `accountId` (Long) - The account ID to query

**Success Response:**
- Status: `200 OK`
- Body:
```json
{
  "account_id": 123,
  "balance": "100.23344"
}
```

**Error Responses:**
- `404 Not Found` - Account doesn't exist
- `500 Internal Server Error` - Server error

**Example:**
```bash
curl -X GET http://localhost:8080/accounts/123
```

### 3. Submit Transaction

**POST** `/transactions`

Processes a transaction between two accounts, transferring the specified amount from the source account to the destination account.

**Request Body:**
```json
{
  "source_account_id": 123,
  "destination_account_id": 456,
  "amount": "100.12345"
}
```

**Success Response:**
- Status: `201 Created`
- Body:
```json
{
  "transactionId": 1,
  "message": "Transaction processed successfully",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**Error Responses:**
- `400 Bad Request` - Validation error, insufficient balance, or invalid amount
- `404 Not Found` - Source or destination account doesn't exist
- `500 Internal Server Error` - Server error

**Example:**
```bash
curl -X POST http://localhost:8080/transactions \
  -H "Content-Type: application/json" \
  -d '{"source_account_id": 123, "destination_account_id": 456, "amount": "100.12345"}'
```

### 4. Get Transaction by ID

**GET** `/transactions/{transactionId}`

Retrieves a specific transaction by its unique identifier.

**Path Parameter:**
- `transactionId` (Long) - The unique transaction identifier

**Success Response:**
- Status: `200 OK`
- Body:
```json
{
  "id": 1,
  "source_account_id": 123,
  "destination_account_id": 456,
  "amount": "100.12345",
  "timestamp": "2024-01-15T10:30:00"
}
```

**Error Responses:**
- `404 Not Found` - Transaction doesn't exist
- `500 Internal Server Error` - Server error

**Note:** The timestamp is in ISO-8601 format (LocalDateTime) without timezone information.

**Example:**
```bash
curl -X GET http://localhost:8080/transactions/1
```

### 5. Get All Transactions

**GET** `/transactions`

Retrieves all transactions from the system.

**Success Response:**
- Status: `200 OK`
- Body:
```json
[
  {
    "id": 1,
    "source_account_id": 123,
    "destination_account_id": 456,
    "amount": "100.12345",
    "timestamp": "2024-01-15T10:30:00"
  },
  {
    "id": 2,
    "source_account_id": 456,
    "destination_account_id": 789,
    "amount": "50.50000",
    "timestamp": "2024-01-15T11:00:00"
  }
]
```

**Note:** The timestamp is in ISO-8601 format (LocalDateTime) without timezone information.

**Example:**
```bash
curl -X GET http://localhost:8080/transactions
```

## Testing

**Prerequisites for Testing:**
- PostgreSQL must be running
- The main database `payment_transfer_db` must exist (same as production)

Run unit tests and integration tests:
```bash
mvn test
```

The test suite includes:
- Unit tests for service layer
- Controller tests with mocked services
- Integration tests for end-to-end scenarios (require PostgreSQL)

**Note:** Integration tests use the `test` profile and connect to the same `payment_transfer_db` database. The tests use `@Transactional` to ensure automatic rollback and data cleanup after each test, so test data won't persist.

## Error Handling

The system handles various error scenarios:

- **Account Not Found (404)**: When querying or processing transactions for non-existent accounts
- **Insufficient Balance (400)**: When source account doesn't have enough balance
- **Invalid Amount (400)**: When transaction amount is zero, negative, or when source and destination accounts are the same
- **Account Already Exists (409)**: When attempting to create a duplicate account
- **Validation Errors (400)**: When request body validation fails

All errors are returned in the following format:
```json
{
  "error": "Error message description",
  "timestamp": "2024-01-15T10:30:00Z",
  "path": "/transactions"
}
```

## Assumptions

1. **Currency**: All accounts use the same currency. No currency conversion is required.

2. **Account IDs**: Account IDs are provided by the client and are not auto-generated. The system expects unique account IDs.

3. **Initial Balance**: Initial balance can be zero or any positive value. Negative initial balances are not allowed.

4. **Transaction Amounts**: Transaction amounts must be positive values greater than zero. Zero or negative amounts are rejected.

5. **Concurrent Transactions**: The system handles concurrent transactions using pessimistic locking to prevent race conditions and ensure data consistency.

6. **Authentication/Authorization**: No authentication or authorization is implemented as per requirements.

7. **Precision**: Account balances and transaction amounts support up to 5 decimal places (precision 19, scale 5) for accurate financial calculations.

8. **Transaction Logging**: All transactions are logged in the database for audit purposes.

9. **Database**: PostgreSQL is used as the database. The schema is automatically created/updated using JPA's `ddl-auto=update`.

10. **Same Account Transfer**: Transactions between the same account (source equals destination) are not allowed and will result in a validation error.

## Architecture

The application follows a layered architecture:

- **Controller Layer**: REST endpoints for HTTP requests
- **Service Layer**: Business logic and transaction management
- **Repository Layer**: Data access using Spring Data JPA
- **Model Layer**: Entities and DTOs

## Data Integrity

The system ensures data integrity through:

- **ACID Transactions**: All transaction operations are wrapped in database transactions
- **Pessimistic Locking**: Accounts are locked during transaction processing to prevent concurrent modifications
- **Validation**: Input validation at both controller and service layers
- **Database Constraints**: Unique constraints on account IDs and proper data types

## Project Structure

```
src/main/java/com/mani/payment_transfer_system/
├── PaymentTransferSystemApplication.java
├── controller/
│   ├── AccountController.java
│   └── TransactionController.java
├── service/
│   ├── AccountService.java
│   └── TransactionService.java
├── repository/
│   ├── AccountRepository.java
│   └── TransactionRepository.java
├── dto/
│   ├── AccountRequest.java
│   ├── AccountResponse.java
│   ├── TransactionRequest.java
│   ├── TransactionResponse.java
│   └── SuccessResponse.java
├── entity/
│   ├── Account.java
│   └── Transaction.java
└── exception/
    ├── GlobalExceptionHandler.java
    ├── AccountNotFoundException.java
    ├── InsufficientBalanceException.java
    ├── InvalidAmountException.java
    └── AccountAlreadyExistsException.java
```

## Technologies Used

- **Spring Boot 3.5.7**: Application framework
- **Spring Data JPA**: Database access layer
- **PostgreSQL**: Relational database
- **Maven**: Build and dependency management
- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework for unit tests
- **Jakarta Validation**: Input validation

## Code Quality

- Comprehensive JavaDoc documentation
- Clean code architecture following SOLID principles
- Proper exception handling with global exception handler
- Transaction management for data integrity
- Pessimistic locking for concurrent operations

## Metrics and Monitoring

The application includes comprehensive metrics collection using Spring Boot Actuator and Micrometer. All metrics are exposed via the `/actuator/metrics` endpoint.

### Available Metrics

The system tracks the following custom metrics:

#### Transaction Metrics
- `payment.transactions.total` - Total number of transactions processed
- `payment.transactions.amount.total` - Total amount of all transactions (in currency units)
- `payment.transactions.processing.time` - Time taken to process transactions (in seconds)
- `payment.transactions.queried.total` - Total number of transaction queries

#### Account Metrics
- `payment.accounts.created.total` - Total number of accounts created
- `payment.accounts.queried.total` - Total number of account queries
- `payment.accounts.creation.time` - Time taken to create accounts (in seconds)

#### Error Metrics
- `payment.errors.total` - Total number of errors
- `payment.errors.insufficient_balance` - Number of insufficient balance errors
- `payment.errors.account_not_found` - Number of account not found errors
- `payment.errors.invalid_amount` - Number of invalid amount errors
- `payment.errors.account_already_exists` - Number of account already exists errors

### Accessing Metrics

#### List All Available Metrics

```bash
curl -X GET http://localhost:8080/actuator/metrics
```

**Expected Response:**
```json
{
  "names": [
    "payment.transactions.total",
    "payment.transactions.amount.total",
    "payment.transactions.processing.time",
    "payment.accounts.created.total",
    "payment.accounts.queried.total",
    "payment.accounts.creation.time",
    "payment.transactions.queried.total",
    "payment.errors.total",
    "payment.errors.insufficient_balance",
    "payment.errors.account_not_found",
    "payment.errors.invalid_amount",
    "payment.errors.account_already_exists",
    "jvm.memory.used",
    "jvm.memory.max",
    "http.server.requests",
    ...
  ]
}
```

#### View a Specific Metric

```bash
# View transaction count
curl -X GET http://localhost:8080/actuator/metrics/payment.transactions.total

# View account creation count
curl -X GET http://localhost:8080/actuator/metrics/payment.accounts.created.total

# View error count
curl -X GET http://localhost:8080/actuator/metrics/payment.errors.total
```

**Example Response:**
```json
{
  "name": "payment.transactions.total",
  "description": "Total number of transactions processed",
  "measurements": [
    {
      "statistic": "COUNT",
      "value": 10.0
    }
  ],
  "availableTags": []
}
```

#### View Health Status

```bash
curl -X GET http://localhost:8080/actuator/health
```

### Metrics Endpoints

The following actuator endpoints are enabled:

- `/actuator/metrics` - List all available metrics
- `/actuator/metrics/{metric-name}` - View specific metric details
- `/actuator/health` - Application health status
- `/actuator/info` - Application information

### Integration with Monitoring Tools

The metrics can be exported to various monitoring tools:

- **Prometheus**: Add `micrometer-registry-prometheus` dependency and configure:
  ```properties
  management.endpoints.web.exposure.include=metrics,prometheus
  management.metrics.export.prometheus.enabled=true
  ```

- **Grafana**: Use Prometheus as a data source to create dashboards

- **CloudWatch**: Add `micrometer-registry-cloudwatch` dependency

### Troubleshooting Metrics

**Issue: 404 Not Found on /actuator endpoints**

- Ensure `spring-boot-starter-actuator` is in your `pom.xml`
- Restart the application after adding the dependency

**Issue: Metrics show 0 values**

- Metrics are only recorded when operations occur. Make some API calls first.

**Issue: Cannot see custom metrics**

- Verify that `MetricsService` is properly injected and operations are being executed.

**Issue: Actuator endpoints return 401 Unauthorized**

- By default, actuator endpoints are not secured. If you see 401, check if security is enabled in your configuration.

## License

This project is provided as-is for evaluation purposes.
