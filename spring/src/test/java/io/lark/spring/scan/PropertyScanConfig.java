// Copyright (c) 2024 LARK Contributors
// SPDX-License-Identifier: MIT

package io.lark.spring.scan;

import io.lark.spring.LaryColumnAutoConfiguration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Config used to verify that auto-configuration alone honors the
 * {@code lary.column.scan-packages} property.
 */
@Configuration(proxyBeanMethods = false)
@Import(LaryColumnAutoConfiguration.class)
public class PropertyScanConfig {
}
