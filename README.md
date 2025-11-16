# Box Delivery Service

A REST API service for managing delivery boxes and items. Built with Java Spring Boot and PostgreSQL.

## Features

- Create and manage delivery boxes
- Load items into boxes with weight and battery validations
- Track loaded items for each box
- Check available boxes for loading
- Monitor battery levels
- Automatic state management

## Requirements

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

## Database Setup

1. Install PostgreSQL if not already installed
2. Create a database:
```sql
CREATE DATABASE boxdelivery;
```

3. Update database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/boxdelivery
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## Build Instructions

1. Clone the repository
2. Navigate to project directory
3. Build the project:
```bash
mvn clean install
```

## Run Instructions

### Using Maven:
```bash
mvn spring-boot:run
```

### Using JAR:
```bash
java -jar target/box-delivery-service-1.0.0.jar
```

The application will start on `http://localhost:8080`

## API Endpoints

### Box Endpoints

#### 1. Create a Box
```http
POST /api/boxes
Content-Type: application/json

{
  "txref": "BOX123",
  "weightLimit": 500,
  "batteryCapacity": 100
}
```

#### 2. Load Items into a Box
```http
POST /api/boxes/{txref}/load
Content-Type: application/json

{
  "itemCodes": ["MED_001", "MED_002"]
}
```

**Validations:**
- Battery must be >= 25%
- Total weight must not exceed box weight limit
- Box must be in IDLE or LOADING state

#### 3. Get Loaded Items
```http
GET /api/boxes/{txref}/items
```

#### 4. Get Available Boxes
```http
GET /api/boxes/available
```

Returns boxes that are:
- In IDLE state
- Battery level >= 25%
- Have remaining weight capacity

#### 5. Check Battery Level
```http
GET /api/boxes/{txref}/battery
```

#### 6. Get Box Details
```http
GET /api/boxes/{txref}
```

### Item Endpoints

#### 1. Create an Item
```http
POST /api/items
Content-Type: application/json

{
  "name": "Paracetamol",
  "weight": 50.0,
  "code": "MED_001"
}
```

**Validations:**
- Name: only letters, numbers, hyphen, underscore
- Code: only uppercase letters, numbers, underscore
- Weight: positive number

#### 2. Get All Items
```http
GET /api/items
```

## Sample Data

The application preloads sample data on startup:

### Boxes:
- BOX001 (500gr, 100% battery, IDLE)
- BOX002 (400gr, 85% battery, IDLE)
- BOX003 (300gr, 50% battery, IDLE)
- BOX004 (450gr, 20% battery, IDLE) - Not available for loading
- BOX005 (500gr, 15% battery, IDLE) - Not available for loading

### Items:
- MED_001: Paracetamol (50gr)
- MED_002: Ibuprofen (75gr)
- MED_003: Bandage (30gr)
- MED_004: Thermometer (100gr)
- MED_005: Antiseptic (120gr)
- MED_006: Face-Mask (25gr)
- MED_007: Hand-Sanitizer (150gr)
- MED_008: Vitamin-C (80gr)

## Testing

The project includes comprehensive integration tests using H2 in-memory database with preloaded SQL data.

### Test Structure:
- **BoxServiceIntegrationTest**: Service layer tests with real database
- **ItemServiceIntegrationTest**: Item service tests
- **BoxControllerIntegrationTest**: Full API endpoint tests with MockMvc

### Run Tests:
```bash
mvn test
```

### Test Database:
- Uses H2 in-memory database
- Data preloaded from `src/test/resources/test-data.sql`
- Automatic cleanup after each test with `@Transactional`

### Test Coverage:
- Box creation and validation
- Item loading with weight/battery checks
- Available boxes filtering
- Battery level checks
- Loaded items retrieval
- Error handling (404, 400 responses)
- Edge cases (weight limits, low battery, invalid states)

### Manual Testing with cURL:

**Create a Box:**
```bash
curl -X POST http://localhost:8080/api/boxes \
  -H "Content-Type: application/json" \
  -d '{
    "txref": "BOX999",
    "weightLimit": 500,
    "batteryCapacity": 80
  }'
```

**Load Items:**
```bash
curl -X POST http://localhost:8080/api/boxes/BOX001/load \
  -H "Content-Type: application/json" \
  -d '{
    "itemCodes": ["MED_001", "MED_002", "MED_003"]
  }'
```

**Get Available Boxes:**
```bash
curl http://localhost:8080/api/boxes/available
```

**Check Battery:**
```bash
curl http://localhost:8080/api/boxes/BOX001/battery
```

**Get Loaded Items:**
```bash
curl http://localhost:8080/api/boxes/BOX001/items
```

## Business Rules

1. **Weight Limit:** Maximum 500gr per box
2. **Battery Requirement:** Minimum 25% battery to load items
3. **Box States:** IDLE → LOADING → LOADED → DELIVERING → DELIVERED → RETURNING
4. **Validations:**
    - Txref: max 20 characters, unique
    - Item name: letters, numbers, hyphen, underscore only
    - Item code: uppercase letters, numbers, underscore only, unique
    - Battery: 0-100%

## Error Handling

The API returns appropriate HTTP status codes:
- 200: Success
- 201: Created
- 400: Bad Request (validation errors, business rule violations)
- 404: Not Found
- 500: Internal Server Error

Error responses include timestamp, status, error type, and detailed message.

## Design Decisions

1. **Pure Layered Architecture:**
    - **Entities**: Pure data holders with no business logic
    - **Services**: All business logic (weight calculations, validations)
    - **Controllers**: Request/response handling only
    - **DTOs**: Clean data transfer between layers

2. **State Management:** Automatic state transitions during loading (IDLE → LOADING → LOADED)

3. **Many-to-Many Relationship:** Boxes and Items (allows item reusability)

4. **Transactional Operations:** Loading operations are atomic

5. **Validation Layers:**
    - Entity-level constraints (JPA annotations)
    - DTO validation (Bean Validation)
    - Business logic validation in service layer

6. **Exception Handling:** Global exception handler for consistent error responses

7. **Testing Strategy:**
    - Integration tests with real database (H2)
    - SQL-based test data preloading
    - No mocking - tests actual behavior
    - Full API tests with MockMvc

## Future Enhancements

- Battery level monitoring/audit log
- State transition API endpoints
- Item removal from boxes
- Box location tracking
- Delivery history
- Real-time notifications
- Authentication/Authorization
- API documentation with Swagger/OpenAPI
"# box-delivery-service" 
