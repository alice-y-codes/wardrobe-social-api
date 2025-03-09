//package com.yalice.wardrobe_social_app.controllers;
//
//import com.yalice.wardrobe_social_app.controllers.utilities.ApiResponse;
//import com.yalice.wardrobe_social_app.controllers.utilities.AuthUtils;
//import com.yalice.wardrobe_social_app.entities.User;
//import com.yalice.wardrobe_social_app.exceptions.ResourceNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.http.ResponseEntity;
//
//import java.util.Objects;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class ApiBaseControllerTest {
//
//    // Mock dependencies
//    @Mock
//    private AuthUtils authUtils;
//
//    // Create an instance of the subclass to test protected methods
//    private ApiBaseControllerTestImpl apiBaseControllerTestImpl;
//
//    // Mocked data for testing
//    private User testUser;
//
//    @BeforeEach
//    public void setup() {
//        // Initialize mocks
//        MockitoAnnotations.openMocks(this);
//
//        // Set up the test user
//        testUser = new User();
//        testUser.setId(1L);
//        testUser.setUsername("testuser");
//
//        // Create an instance of the subclass
//        apiBaseControllerTestImpl = new ApiBaseControllerTestImpl(authUtils);
//
//        // Mock the AuthUtils to return the test user
//        when(authUtils.getCurrentUserOrElseThrow()).thenReturn(testUser);
//    }
//
//    @Test
//    public void testHandleEntityCreation_Success() {
//        // Define a simple supplier that returns a mock entity
//        ApiBaseController.EntitySupplier<String> supplier = () -> "Created Entity";
//
//        // Call the method from the subclass
//        ResponseEntity<ApiResponse<String>> response = apiBaseControllerTestImpl.handleEntityCreation(supplier, "Entity");
//
//        // Assert the success response
//        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
//        assertEquals("Entity created successfully", response.getBody().getMessage());
//        assertEquals("Created Entity", response.getBody().getData());
//    }
//
//    @Test
//    public void testHandleEntityCreation_Exception() {
//        // Define a supplier that throws an exception
//        ApiBaseController.EntitySupplier<String> supplier = () -> {
//            throw new Exception("Creation failed");
//        };
//
//        // Call the method from the subclass
//        ResponseEntity<ApiResponse<String>> response = apiBaseControllerTestImpl.handleEntityCreation(supplier, "Entity");
//
//        // Assert the error response
//        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
//        assertEquals("Failed to create Entity", response.getBody().getMessage());
//    }
//
//    @Test
//    public void testHandleResourceNotFound() {
//        // Simulate a ResourceNotFoundException
//        ResourceNotFoundException exception = new ResourceNotFoundException("Entity not found");
//
//        // Call the handler method from the subclass
//        ResponseEntity<ApiResponse<Void>> response = apiBaseControllerTestImpl.handleResourceNotFound(exception);
//
//        // Assert the "Resource Not Found" error response
//        assertEquals(404, response.getStatusCode().value());
//        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
//        assertEquals("Resource not found: Entity not found", response.getBody().getMessage());
//    }
//
//    // A concrete subclass to access protected methods for testing
//    private static class ApiBaseControllerTestImpl extends ApiBaseController {
//
//        public ApiBaseControllerTestImpl(AuthUtils authUtils) {
//            super(authUtils);
//        }
//
//        // Expose the protected methods for testing
//        public <T> ResponseEntity<ApiResponse<T>> handleEntityCreation(ApiBaseController.EntitySupplier<T> supplier, String entityName) {
//            return super.handleEntityCreation(supplier, entityName);
//        }
//
//        public <T> ResponseEntity<ApiResponse<T>> handleEntityRetrieval(ApiBaseController.EntitySupplier<T> supplier, String entityName) {
//            return super.handleEntityRetrieval(supplier, entityName);
//        }
//
//        public <T> ResponseEntity<ApiResponse<T>> handleEntityUpdate(ApiBaseController.EntitySupplier<T> supplier, String entityName) {
//            return super.handleEntityUpdate(supplier, entityName);
//        }
//
//        public ResponseEntity<ApiResponse<Void>> handleEntityDeletion(ApiBaseController.VoidSupplier supplier, String entityName) {
//            return super.handleEntityDeletion(supplier, entityName);
//        }
//
//        public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException e) {
//            return super.handleResourceNotFound(e);
//        }
//    }
//}
