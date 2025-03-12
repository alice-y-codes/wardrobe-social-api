# Wardrobe Social App

A social media platform for fashion enthusiasts to manage their wardrobes, create outfits, and share their style with others.

## API Documentation

### Authentication

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
    "username": "string",
    "password": "string"
}
```
Response:
```json
{
    "success": true,
    "message": "Login successful",
    "data": {
        "token": "string",
        "username": "string"
    }
}
```
Note: JWT token is also set in an HTTP-only cookie named "jwt"

#### Logout
```http
POST /api/auth/logout
```
Response:
```json
{
    "success": true,
    "message": "Logged out successfully"
}
```

### User Management

#### Register New User
```http
POST /api/users/register
Content-Type: application/json

{
    "username": "string",
    "email": "string",
    "password": "string",
    "provider": "LOCAL"
}
```
Response:
```json
{
    "success": true,
    "message": "User registered successfully",
    "data": {
        "id": "number",
        "username": "string",
        "email": "string",
        "provider": "string",
        "createdAt": "datetime",
        "updatedAt": "datetime"
    }
}
```

#### Change Password
```http
PUT /api/users/{userId}/password
Content-Type: application/json

{
    "currentPassword": "string",
    "newPassword": "string"
}
```
Response:
```json
{
    "success": true,
    "message": "Password updated successfully"
}
```

#### Delete Account
```http
DELETE /api/users/{userId}
```
Response:
```json
{
    "success": true,
    "message": "User account deleted successfully"
}
```

### Profile Management

#### Get Current User's Profile
```http
GET /api/profiles/me
```
Response:
```json
{
    "success": true,
    "data": {
        "id": "number",
        "userId": "number",
        "displayName": "string",
        "bio": "string",
        "location": "string",
        "avatarUrl": "string",
        "visibility": "PUBLIC|FRIENDS_ONLY|PRIVATE",
        "createdAt": "datetime",
        "updatedAt": "datetime"
    }
}
```

#### Get User Profile by ID
```http
GET /api/profiles/{userId}
```
Response: Same as above

#### Update Profile
```http
PUT /api/profiles/me
Content-Type: application/json

{
    "displayName": "string",
    "bio": "string",
    "location": "string",
    "avatarUrl": "string"
}
```
Response: Same as profile GET response

#### Update Profile Visibility
```http
PUT /api/profiles/me/visibility
Content-Type: application/json

{
    "visibility": "PUBLIC|FRIENDS_ONLY|PRIVATE"
}
```
Response: Same as profile GET response

### Wardrobe Management

#### Create Wardrobe
```http
POST /api/wardrobes
Content-Type: application/json

{
    "name": "string",
    "description": "string",
    "visibility": "PUBLIC|PRIVATE"
}
```
Response:
```json
{
    "success": true,
    "data": {
        "id": "number",
        "userId": "number",
        "name": "string",
        "description": "string",
        "visibility": "string",
        "createdAt": "datetime",
        "updatedAt": "datetime"
    }
}
```

#### Get All Wardrobes
```http
GET /api/wardrobes
```
Response:
```json
{
    "success": true,
    "data": [
        {
            "id": "number",
            "userId": "number",
            "name": "string",
            "description": "string",
            "visibility": "string",
            "createdAt": "datetime",
            "updatedAt": "datetime"
        }
    ]
}
```

#### Get Specific Wardrobe
```http
GET /api/wardrobes/{wardrobeId}
```
Response: Same as single wardrobe object

#### Update Wardrobe
```http
PUT /api/wardrobes/{wardrobeId}
Content-Type: application/json

{
    "name": "string",
    "description": "string",
    "visibility": "PUBLIC|PRIVATE"
}
```
Response: Same as single wardrobe object

#### Delete Wardrobe
```http
DELETE /api/wardrobes/{wardrobeId}
```
Response:
```json
{
    "success": true,
    "message": "Wardrobe deleted successfully"
}
```

### Item Management

#### Add Item to Wardrobe
```http
POST /api/items/{wardrobeId}
Content-Type: multipart/form-data

- item: {
    "name": "string",
    "description": "string",
    "category": "string",
    "season": "string",
    "color": "string",
    "brand": "string",
    "size": "string"
}
- image: (file)
```
Response:
```json
{
    "success": true,
    "data": {
        "id": "number",
        "wardrobeId": "number",
        "name": "string",
        "description": "string",
        "category": "string",
        "season": "string",
        "color": "string",
        "brand": "string",
        "size": "string",
        "imageUrl": "string",
        "createdAt": "datetime",
        "updatedAt": "datetime"
    }
}
```

#### Update Item
```http
PATCH /api/items/{itemId}
Content-Type: multipart/form-data

- item: {
    "name": "string",
    "description": "string",
    "category": "string",
    "season": "string",
    "color": "string",
    "brand": "string",
    "size": "string"
}
- image: (file, optional)
```
Response: Same as item creation response

#### Get All User Items
```http
GET /api/items/my-items
```
Response:
```json
{
    "success": true,
    "data": [
        {
            // Item object as above
        }
    ]
}
```

#### Get Specific Item
```http
GET /api/items/{itemId}
```
Response: Same as single item object

#### Delete Item
```http
DELETE /api/items/{itemId}
```
Response:
```json
{
    "success": true,
    "message": "Item deleted successfully"
}
```

### Outfit Management

#### Create Outfit
```http
POST /api/outfits
Content-Type: application/json

{
    "name": "string",
    "description": "string",
    "season": "string",
    "occasion": "string",
    "visibility": "PUBLIC|PRIVATE",
    "itemIds": ["number"]
}
```
Response:
```json
{
    "success": true,
    "data": {
        "id": "number",
        "userId": "number",
        "name": "string",
        "description": "string",
        "season": "string",
        "occasion": "string",
        "visibility": "string",
        "items": [
            // Item objects
        ],
        "createdAt": "datetime",
        "updatedAt": "datetime"
    }
}
```

#### Update Outfit
```http
PATCH /api/outfits/{outfitId}
Content-Type: application/json

{
    "name": "string",
    "description": "string",
    "season": "string",
    "occasion": "string",
    "visibility": "PUBLIC|PRIVATE"
}
```
Response: Same as outfit creation response

#### Get User's Outfits
```http
GET /api/outfits/my-outfits
```
Response:
```json
{
    "success": true,
    "data": [
        {
            // Outfit object as above
        }
    ]
}
```

#### Get Specific Outfit
```http
GET /api/outfits/{outfitId}
```
Response: Same as single outfit object

#### Get User's Public Outfits
```http
GET /api/outfits/users/{userId}
```
Response: Same as outfits list response

#### Add Item to Outfit
```http
POST /api/outfits/{outfitId}/items/{itemId}
```
Response: Same as outfit object

#### Remove Item from Outfit
```http
DELETE /api/outfits/{outfitId}/items/{itemId}
```
Response:
```json
{
    "success": true,
    "message": "Item removed from outfit successfully"
}
```

### Social Features

#### Feed

##### Get User's Feed
```http
GET /api/feed
```
Response:
```json
{
    "success": true,
    "data": [
        {
            "id": "number",
            "userId": "number",
            "type": "OUTFIT|POST",
            "content": "string",
            "imageUrl": "string",
            "likes": "number",
            "comments": "number",
            "createdAt": "datetime",
            "updatedAt": "datetime"
        }
    ]
}
```

##### Get Seasonal Feed
```http
GET /api/feed/season/{season}
```
Response: Same as feed response

##### Get Feed by Category
```http
GET /api/feed/category/{category}
```
Response: Same as feed response

##### Get User's Posts
```http
GET /api/feed/users/{userId}/posts
```
Response: Same as feed response

#### Posts

##### Create Post
```http
POST /api/feed/post
Content-Type: multipart/form-data

- post: {
    "content": "string",
    "outfitId": "number" (optional)
}
- image: (file, optional)
```
Response:
```json
{
    "success": true,
    "data": {
        "id": "number",
        "userId": "number",
        "content": "string",
        "imageUrl": "string",
        "outfitId": "number",
        "likes": 0,
        "comments": 0,
        "createdAt": "datetime",
        "updatedAt": "datetime"
    }
}
```

##### Update Post
```http
PATCH /api/feed/{postId}
Content-Type: application/json

{
    "content": "string"
}
```
Response: Same as post object

##### Like/Unlike Post
```http
POST /api/feed/{postId}/like
```
Response:
```json
{
    "success": true,
    "message": "Post liked/unliked successfully",
    "data": {
        "liked": "boolean",
        "likeCount": "number"
    }
}
```

#### Comments

##### Add Comment
```http
POST /api/comments/posts/{postId}
Content-Type: application/json

{
    "content": "string"
}
```
Response:
```json
{
    "success": true,
    "data": {
        "id": "number",
        "postId": "number",
        "userId": "number",
        "content": "string",
        "createdAt": "datetime",
        "updatedAt": "datetime"
    }
}
```

##### Update Comment
```http
PUT /api/comments/{commentId}
Content-Type: application/json

{
    "content": "string"
}
```
Response: Same as comment object

##### Get Post Comments
```http
GET /api/comments/posts/{postId}/comments
```
Response:
```json
{
    "success": true,
    "data": [
        {
            // Comment object as above
        }
    ]
}
```

#### Friendships

##### Send Friend Request
```http
POST /api/friendships/requests
Content-Type: application/json

{
    "recipientId": "number"
}
```
Response:
```json
{
    "success": true,
    "message": "Friend request sent successfully",
    "data": {
        "id": "number",
        "senderId": "number",
        "recipientId": "number",
        "status": "PENDING",
        "createdAt": "datetime",
        "updatedAt": "datetime"
    }
}
```

##### Accept Friend Request
```http
POST /api/friendships/requests/{requestId}/accept
```
Response:
```json
{
    "success": true,
    "message": "Friend request accepted",
    "data": {
        // Friendship object with status "ACCEPTED"
    }
}
```

##### Reject Friend Request
```http
POST /api/friendships/requests/{requestId}/reject
```
Response:
```json
{
    "success": true,
    "message": "Friend request rejected",
    "data": {
        // Friendship object with status "REJECTED"
    }
}
```

##### Get Pending Friend Requests
```http
GET /api/friendships/requests/pending
```
Response:
```json
{
    "success": true,
    "data": [
        {
            // Friendship objects with status "PENDING"
        }
    ]
}
```

##### Get User's Friends
```http
GET /api/friendships/friends
```
Response:
```json
{
    "success": true,
    "data": [
        {
            "id": "number",
            "username": "string",
            "displayName": "string",
            "avatarUrl": "string"
        }
    ]
}
```

## Setup and Installation

1. Clone the repository
2. Configure your PostgreSQL database
3. Set up required environment variables
4. Run the application

### Database Configuration

Update `application.properties` with your database settings:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/wardrobe_social_app_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Environment Variables

Required environment variables:
- `JWT_SECRET` - Secret key for JWT token generation (must be Base64-encoded, at least 256 bits)
- `JWT_EXPIRATION` - JWT token expiration time in milliseconds (default: 86400000)

### Running the Application

```bash
./mvnw spring-boot:run
```

## Technologies Used

- Spring Boot 3.x
- Spring Security with JWT Authentication
- PostgreSQL
- JPA/Hibernate
- Maven
- Java 17+

## Error Handling

All endpoints follow a consistent error response format:

```json
{
    "success": false,
    "message": "Error message",
    "error": {
        "code": "ERROR_CODE",
        "details": "Detailed error message"
    }
}
```

Common HTTP status codes:
- 200: Success
- 201: Created
- 400: Bad Request
- 401: Unauthorized
- 403: Forbidden
- 404: Not Found
- 500: Internal Server Error
