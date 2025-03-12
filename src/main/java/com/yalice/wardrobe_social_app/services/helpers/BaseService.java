package com.yalice.wardrobe_social_app.services.helpers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base service class that provides common functionality for all services.
 * This includes converting entities to response DTOs.
 */
public abstract class BaseService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

}
