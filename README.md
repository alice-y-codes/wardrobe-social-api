# Wardrobe Social App

A Spring Boot application for managing your wardrobe and creating outfits.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies](#technologies)
- [API Documentation](#api-documentation)
  - [Authentication](#authentication)
  - [Users](#users)
  - [Items](#items)
  - [Outfits](#outfits)
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
    "password": "string",
    "provider": "string"
  }
  ```
- **Response**: The registered user
- **Description**: Registers a new user

#### Find User by Username

- **URL**: `/api/users/findByUsername`
- **Method**: `GET`
- **Query Parameters**: `username=string`
- **Response**: User details
- **Description**: Finds a user by their username

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
