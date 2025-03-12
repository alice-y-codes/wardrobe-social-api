# Wardrobe Social App

A Spring Boot application for managing your wardrobe and creating outfits.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies](#technologies)
- [API Documentation](#api-documentation)
  - [Authentication](#authentication)
  - [Users](#users)
  - [Posts](#posts)
  - [Feed](#feed)
  - [Items](#items)
  - [Outfits](#outfits)
  - [Comments](#comments)
  - [Friendship](#friendship)
  - [Profile](#profile)
- [Setup and Installation](#setup-and-installation)
- [Running Tests](#running-tests)
- [Code Quality](#code-quality)

## Overview

Wardrobe Social App is a platform that allows users to digitize their wardrobe, create outfits, and share them with others. The application provides a RESTful API for managing clothing items, creating outfits, and user authentication.

## Features

- User registration and authentication
- JWT-based authentication
- Wardrobe management (add, update, delete clothing items)
- Outfit creation and management
- Social features (posts, comments, likes)
- Feed customization and filtering
- Search functionality for items and outfits

## Technologies

- Java 21
- Spring Boot 3.4.3
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT for authentication
- Maven
- JUnit and Mockito for testing
- Checkstyle, SpotBugs, and SonarQube for code quality

## API Documentation

### Authentication

#### Login

- **URL**: `/api/auth/login`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```
- **Response**: JWT token and username
  ```json
  {
    "token": "string",
    "username": "string"
  }
  ```
- **Description**: Authenticates a user and returns a JWT token

#### Logout

- **URL**: `/api/auth/logout`
- **Method**: `POST`
- **Description**: Logs out the current user by clearing the JWT cookie

### Users

#### Register User

- **URL**: `/api/users/register`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "username": "string",
    "email": "string",
    "password": "string"
  }
  ```
- **Response**: User details
- **Description**: Registers a new user

#### Change Password

- **URL**: `/api/users/{userId}/password`
- **Method**: `PUT`
- **Request Body**:
  ```json
  {
    "currentPassword": "string",
    "newPassword": "string"
  }
  ```
- **Description**: Changes the password for the authenticated user

#### Delete User Account

- **URL**: `/api/users/{userId}`
- **Method**: `DELETE`
- **Description**: Deletes the user account (must be authenticated as the user)

### Posts

#### Create Post

- **URL**: `/api/feed/post`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "content": "string",
    "imageUrls": ["string"],
    "outfitId": "number"
  }
  ```
- **Response**: Created post details
- **Description**: Creates a new post

#### Get Post

- **URL**: `/api/feed/{postId}`
- **Method**: `GET`
- **Response**: Post details
- **Description**: Retrieves a specific post

#### Update Post

- **URL**: `/api/feed/{postId}`
- **Method**: `PATCH`
- **Request Body**:
  ```json
  {
    "content": "string",
    "imageUrls": ["string"]
  }
  ```
- **Response**: Updated post details
- **Description**: Updates an existing post

#### Delete Post

- **URL**: `/api/feed/{postId}`
- **Method**: `DELETE`
- **Description**: Deletes a post

#### Toggle Like Post

- **URL**: `/api/feed/{postId}/like`
- **Method**: `POST`
- **Response**: Like status message
- **Description**: Toggles like status on a post

### Feed

#### Get Feed

- **URL**: `/api/feed`
- **Method**: `GET`
- **Query Parameters**:
  - `page` (default: 0)
  - `size` (default: 20)
- **Response**: List of feed items
- **Description**: Retrieves the user's feed with pagination

#### Get Feed By Season

- **URL**: `/api/feed/season/{season}`
- **Method**: `GET`
- **Query Parameters**:
  - `page` (default: 0)
  - `size` (default: 20)
- **Response**: List of feed items filtered by season
- **Description**: Retrieves feed items filtered by season

#### Get Feed By Category

- **URL**: `/api/feed/category/{category}`
- **Method**: `GET`
- **Query Parameters**:
  - `page` (default: 0)
  - `size` (default: 20)
- **Response**: List of feed items filtered by category
- **Description**: Retrieves feed items filtered by category

#### Get User Posts

- **URL**: `/api/feed/users/{userId}/posts`
- **Method**: `GET`
- **Query Parameters**:
  - `page` (default: 0)
  - `size` (default: 20)
- **Response**: Page of user's posts
- **Description**: Retrieves posts from a specific user

### Items

#### Create Item

- **URL**: `/api/items`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "name": "string",
    "brand": "string",
    "category": "string",
    "size": "string",
    "color": "string",
    "imageUrl": "string"
  }
  ```
- **Response**: The created item
- **Description**: Creates a new clothing item for the authenticated user

#### Get All Items for a User

- **URL**: `/api/items/users/{userId}`
- **Method**: `GET`
- **Path Parameters**: `userId=number`
- **Response**: List of items
- **Description**: Gets all items for a specific user

#### Get Current User's Items

- **URL**: `/api/items/my-items`
- **Method**: `GET`
- **Response**: List of items
- **Description**: Gets all items for the authenticated user

#### Get Item by ID

- **URL**: `/api/items/{id}`
- **Method**: `GET`
- **Path Parameters**: `id=number`
- **Response**: Item details
- **Description**: Gets an item by its ID

#### Get Item by Name

- **URL**: `/api/items/names/{name}`
- **Method**: `GET`
- **Path Parameters**: `name=string`
- **Response**: Item details
- **Description**: Gets an item by its name

#### Update Item

- **URL**: `/api/items/{id}`
- **Method**: `PUT`
- **Path Parameters**: `id=number`
- **Request Body**:
  ```json
  {
    "name": "string",
    "brand": "string",
    "category": "string",
    "size": "string",
    "color": "string",
    "imageUrl": "string"
  }
  ```
- **Response**: The updated item
- **Description**: Updates an existing item

#### Delete Item

- **URL**: `/api/items/{id}`
- **Method**: `DELETE`
- **Path Parameters**: `id=number`
- **Response**: No content
- **Description**: Deletes an item

### Outfits

#### Create Outfit

- **URL**: `/api/outfits`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "name": "string",
    "description": "string",
    "occasion": "string",
    "season": "string"
  }
  ```
- **Response**: The created outfit
- **Description**: Creates a new outfit for the authenticated user

#### Get Current User's Outfits

- **URL**: `/api/outfits/my-outfits`
- **Method**: `GET`
- **Response**: List of outfits
- **Description**: Gets all outfits for the authenticated user

#### Get User's Outfits

- **URL**: `/api/outfits/users/{userId}`
- **Method**: `GET`
- **Path Parameters**: `userId=number`
- **Response**: List of outfits
- **Description**: Gets all outfits for a specific user

#### Get Outfit by ID

- **URL**: `/api/outfits/{id}`
- **Method**: `GET`
- **Path Parameters**: `id=number`
- **Response**: Outfit details
- **Description**: Gets an outfit by its ID

#### Update Outfit

- **URL**: `/api/outfits/{id}`
- **Method**: `PUT`
- **Path Parameters**: `id=number`
- **Request Body**:
  ```json
  {
    "name": "string",
    "description": "string",
    "occasion": "string",
    "season": "string"
  }
  ```
- **Response**: The updated outfit
- **Description**: Updates an existing outfit

#### Delete Outfit

- **URL**: `/api/outfits/{id}`
- **Method**: `DELETE`
- **Path Parameters**: `id=number`
- **Response**: No content
- **Description**: Deletes an outfit

#### Add Item to Outfit

- **URL**: `/api/outfits/{outfitId}/items/{itemId}`
- **Method**: `POST`
- **Path Parameters**:
  - `outfitId=number`
  - `itemId=number`
- **Response**: The updated outfit
- **Description**: Adds an item to an outfit

#### Remove Item from Outfit

- **URL**: `/api/outfits/{outfitId}/items/{itemId}`
- **Method**: `DELETE`
- **Path Parameters**:
  - `outfitId=number`
  - `itemId=number`
- **Response**: The updated outfit
- **Description**: Removes an item from an outfit

#### Get Outfits by Occasion

- **URL**: `/api/outfits/occasion/{occasion}`
- **Method**: `GET`
- **Path Parameters**: `occasion=string`
- **Response**: List of outfits
- **Description**: Gets outfits filtered by occasion

### Comments

#### Add Comment

- **URL**: `/api/comments/post/{postId}`
- **Method**: `POST`
- **Request Body**:
  ```json
  {
    "content": "string"
  }
  ```
- **Response**: Created comment details
- **Description**: Adds a comment to a post

#### Get Comments

- **URL**: `/api/comments/post/{postId}`
- **Method**: `GET`
- **Query Parameters**:
  - `page` (default: 0)
  - `size` (default: 20)
- **Response**: List of comments
- **Description**: Retrieves comments for a post

#### Update Comment

- **URL**: `/api/comments/{commentId}`
- **Method**: `PUT`
- **Request Body**:
  ```json
  {
    "content": "string"
  }
  ```
- **Response**: Updated comment details
- **Description**: Updates an existing comment

#### Delete Comment

- **URL**: `/api/comments/{commentId}`
- **Method**: `DELETE`
- **Description**: Deletes a comment

### Friendship

#### Send Friend Request

- **URL**: `/api/friendship/request/{userId}`
- **Method**: `POST`
- **Description**: Sends a friend request to another user

#### Accept Friend Request

- **URL**: `/api/friendship/accept/{requestId}`
- **Method**: `POST`
- **Description**: Accepts a friend request

#### Reject Friend Request

- **URL**: `/api/friendship/reject/{requestId}`
- **Method**: `POST`
- **Description**: Rejects a friend request

#### Get Friend Requests

- **URL**: `/api/friendship/requests`
- **Method**: `GET`
- **Response**: List of friend requests
- **Description**: Gets all pending friend requests

#### Get Friends

- **URL**: `/api/friendship/friends`
- **Method**: `GET`
- **Response**: List of friends
- **Description**: Gets all friends of the current user

#### Remove Friend

- **URL**: `/api/friendship/{friendId}`
- **Method**: `DELETE`
- **Description**: Removes a friend

### Profile

#### Get Profile

- **URL**: `/api/profile/{userId}`
- **Method**: `GET`
- **Response**: User profile details
- **Description**: Gets a user's profile

#### Update Profile

- **URL**: `/api/profile`
- **Method**: `PUT`
- **Request Body**:
  ```json
  {
    "displayName": "string",
    "bio": "string",
    "location": "string",
    "avatarUrl": "string"
  }
  ```
- **Response**: Updated profile details
- **Description**: Updates the current user's profile

## Setup and Installation

1. Clone the repository
2. Configure PostgreSQL database
3. Update `application.properties` with your database credentials
4. Run `./mvnw clean install` to build the project
5. Run `./mvnw spring-boot:run` to start the application

## Running Tests

```bash
./mvnw test
```

## Code Quality

This project uses several tools to ensure code quality:

### Checkstyle

```bash
./mvnw checkstyle:check
```

### SpotBugs

```bash
./mvnw spotbugs:check
```

### SonarQube

```bash
./mvnw sonar:sonar
```

### JaCoCo (Code Coverage)

```bash
./mvnw test jacoco:report
```
