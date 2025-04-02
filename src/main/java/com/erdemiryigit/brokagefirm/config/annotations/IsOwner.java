package com.erdemiryigit.brokagefirm.config.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@userAuthenticationService.isOrderOwner(#orderId) and hasRole('CUSTOMER')")
public @interface IsOwner {
}